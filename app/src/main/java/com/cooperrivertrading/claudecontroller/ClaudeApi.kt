package com.cooperrivertrading.claudecontroller

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object ClaudeApi {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private const val ENDPOINT = "https://api.anthropic.com/v1/messages"
    private const val MODEL = "claude-sonnet-4-6"

    fun stream(apiKey: String, systemPrompt: String, userPrompt: String): Flow<String> = flow {
        val body = JSONObject().apply {
            put("model", MODEL)
            put("max_tokens", 4096)
            put("stream", true)
            put("system", systemPrompt)
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", userPrompt)
                })
            })
        }.toString()

        val request = Request.Builder()
            .url(ENDPOINT)
            .post(body.toRequestBody("application/json".toMediaType()))
            .header("x-api-key", apiKey)
            .header("anthropic-version", "2023-06-01")
            .header("content-type", "application/json")
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            val errBody = response.body?.string() ?: "HTTP ${response.code}"
            emit("\n[Error ${response.code}] $errBody")
            return@flow
        }

        val source = response.body?.source() ?: return@flow
        val buffer = okio.Buffer()

        while (!source.exhausted()) {
            source.read(buffer, 8192)
            val chunk = buffer.readUtf8()
            for (line in chunk.lines()) {
                if (!line.startsWith("data: ")) continue
                val data = line.removePrefix("data: ").trim()
                if (data == "[DONE]") return@flow
                runCatching {
                    val json = JSONObject(data)
                    val type = json.optString("type")
                    if (type == "content_block_delta") {
                        val delta = json.optJSONObject("delta")
                        val text = delta?.optString("text") ?: ""
                        if (text.isNotEmpty()) emit(text)
                    }
                }
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun complete(apiKey: String, systemPrompt: String, userPrompt: String): String =
        withContext(Dispatchers.IO) {
            val body = JSONObject().apply {
                put("model", MODEL)
                put("max_tokens", 4096)
                put("system", systemPrompt)
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", userPrompt)
                    })
                })
            }.toString()

            val request = Request.Builder()
                .url(ENDPOINT)
                .post(body.toRequestBody("application/json".toMediaType()))
                .header("x-api-key", apiKey)
                .header("anthropic-version", "2023-06-01")
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext "[Error ${response.code}]"

            val json = JSONObject(response.body?.string() ?: return@withContext "[Empty]")
            json.getJSONArray("content")
                .getJSONObject(0)
                .getString("text")
        }
}
