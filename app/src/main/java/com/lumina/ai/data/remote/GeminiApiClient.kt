package com.lumina.ai.data.remote

import com.lumina.ai.BuildConfig
import com.lumina.ai.data.model.AgentConfig
import com.lumina.ai.data.model.ChatMessage
import com.lumina.ai.data.model.UserProfile
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

class GeminiApiClient {

    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val client = HttpClient(OkHttp) {
        install(HttpTimeout) {
            requestTimeoutMillis = 60_000
            connectTimeoutMillis = 30_000
            socketTimeoutMillis = 60_000
        }
        install(ContentNegotiation) {
            json(jsonParser)
        }
    }

    private fun resolveApiKey(userProfile: UserProfile?): String {
        val userKey = userProfile?.geminiApiKey?.trim()
        if (!userKey.isNullOrEmpty()) return userKey
        return try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }
    }

    /**
     * Streams Gemini response using Server-Sent Events (alt=sse)
     */
    fun streamChatResponse(
        agent: AgentConfig,
        userProfile: UserProfile,
        history: List<ChatMessage>,
        userMessage: String
    ): Flow<String> = flow {
        val apiKey = resolveApiKey(userProfile)
        val model = userProfile.textModel.ifBlank { "gemini-2.5-flash" }

        val systemInstructionText = buildString {
            append("Kamu adalah ${agent.name}. ")
            append("Kepribadian dan karaktermu: ${agent.personality}. ")
            if (!agent.currentOutfit.isNullOrBlank()) {
                append("Pakaianmu saat ini: ${agent.currentOutfit}. ")
            }
            if (!userProfile.name.isBlank()) {
                append("Lawan bicaramu bernama ${userProfile.name}. ")
            }
            if (!userProfile.personalityInfo.isBlank()) {
                append("Informasi tentang lawan bicara: ${userProfile.personalityInfo}. ")
            }
            append("Bicaralah dalam bahasa Indonesia yang ekspresif, hangat, dan alami. ")
            append("Gunakan tindakan fisik/ekspresi di antara tanda bintang seperti *tersenyum hangat* bila sesuai situasi.")
        }

        val contentsArray = buildJsonArray {
            // Recent history (last 10 messages)
            val recent = history.takeLast(10)
            for (msg in recent) {
                add(buildJsonObject {
                    put("role", if (msg.role == "user") "user" else "model")
                    put("parts", buildJsonArray {
                        add(buildJsonObject {
                            put("text", msg.text)
                        })
                    })
                })
            }
            // Add current user prompt
            add(buildJsonObject {
                put("role", "user")
                put("parts", buildJsonArray {
                    add(buildJsonObject {
                        put("text", userMessage)
                    })
                })
            })
        }

        val requestBody = buildJsonObject {
            put("systemInstruction", buildJsonObject {
                put("parts", buildJsonArray {
                    add(buildJsonObject {
                        put("text", systemInstructionText)
                    })
                })
            })
            put("contents", contentsArray)
            put("generationConfig", buildJsonObject {
                put("temperature", 0.9)
                put("topP", 0.95)
            })
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:streamGenerateContent?alt=sse&key=$apiKey"

        try {
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(requestBody.toString())
            }

            val channel = response.bodyAsChannel()
            while (!channel.isClosedForRead) {
                val line = channel.readUTF8Line() ?: break
                if (line.startsWith("data: ")) {
                    val jsonStr = line.removePrefix("data: ").trim()
                    if (jsonStr.isNotBlank() && jsonStr != "[DONE]") {
                        try {
                            val parsed = jsonParser.parseToJsonElement(jsonStr).jsonObject
                            val candidates = parsed["candidates"]?.jsonArray
                            val candidate = candidates?.firstOrNull()?.jsonObject
                            val content = candidate?.get("content")?.jsonObject
                            val parts = content?.get("parts")?.jsonArray
                            val textPart = parts?.firstOrNull()?.jsonObject?.get("text")?.jsonPrimitive?.content
                            if (!textPart.isNullOrEmpty()) {
                                emit(textPart)
                            }
                        } catch (_: Exception) {
                            // Parse individual chunk gracefully
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // Fallback or emit friendly error
            emit("\n*[Maaf, ada sedikit gangguan koneksi ke AI: ${e.localizedMessage ?: "timeout"}...]*")
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Generates character data using Gemini
     */
    suspend fun generateCharacterIdea(description: String, apiKey: String): Pair<String, String>? {
        val model = "gemini-2.5-flash"
        val prompt = "Berdasarkan ide berikut: '$description', buatlah karakter AI fiktif. " +
                "Kembalikan respon DALAM FORMAT JSON HANYA dengan format: {\"name\": \"...\", \"personality\": \"...\"}"

        val requestBody = buildJsonObject {
            put("contents", buildJsonArray {
                add(buildJsonObject {
                    put("parts", buildJsonArray {
                        add(buildJsonObject { put("text", prompt) })
                    })
                })
            })
        }

        return try {
            val res = client.post("https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey") {
                contentType(ContentType.Application.Json)
                setBody(requestBody.toString())
            }
            val text = res.bodyAsText()
            val parsed = jsonParser.parseToJsonElement(text).jsonObject
            val candidateText = parsed["candidates"]?.jsonArray?.firstOrNull()
                ?.jsonObject?.get("content")?.jsonObject
                ?.get("parts")?.jsonArray?.firstOrNull()
                ?.jsonObject?.get("text")?.jsonPrimitive?.content ?: ""

            val cleaned = candidateText.substringAfter("{").substringBeforeLast("}")
            val obj = jsonParser.parseToJsonElement("{$cleaned}").jsonObject
            val name = obj["name"]?.jsonPrimitive?.content ?: "Karakter Baru"
            val personality = obj["personality"]?.jsonPrimitive?.content ?: description
            Pair(name, personality)
        } catch (_: Exception) {
            Pair("Lumina Companion", description)
        }
    }
}
