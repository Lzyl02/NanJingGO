package com.COMP3040.NanjingGo.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// ======================= 数据类 =======================
data class GeminiRequest(
    val contents: List<GeminiContent>
)

data class GeminiContent(
    val parts: List<GeminiTextPart>
)

data class GeminiTextPart(
    val text: String
)

data class GeminiResponse(
    val candidates: List<GeminiCandidate>?
)

data class GeminiCandidate(
    val content: GeminiContent?
)

// ======================= Retrofit 接口 =======================
interface GeminiService {
    @POST("v1beta/models/gemini-2.0-flash:generateContent")
    @Headers("Content-Type: application/json")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

// ======================= Retrofit 单例 =======================
object GeminiApiClient {
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: GeminiService by lazy {
        retrofit.create(GeminiService::class.java)
    }
}

/**
 * 真正调用 Gemini API 的函数
 * @param prompt 用户输入的文本
 * @return AI 回复文本
 */
suspend fun callGeminiApi(prompt: String): String {
    // 内嵌你的 Gemini API Key（仅用于测试）
    val apiKey = "AIzaSyBL7ysyas8n7HLxmsUsnx49WK0bnoCUAiw"

    // 1. 构造请求体
    val request = GeminiRequest(
        contents = listOf(
            GeminiContent(
                parts = listOf(GeminiTextPart(prompt))
            )
        )
    )

    // 2. 发起网络请求
    val response = GeminiApiClient.service.generateContent(apiKey, request)

    // 3. 解析 AI 回复并返回
    return response.candidates
        ?.firstOrNull()
        ?.content
        ?.parts
        ?.firstOrNull()
        ?.text
        ?: "No response"
}
