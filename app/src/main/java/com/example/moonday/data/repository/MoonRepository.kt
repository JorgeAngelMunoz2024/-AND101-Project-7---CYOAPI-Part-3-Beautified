package com.example.moonday.data.repository

import com.example.moonday.BuildConfig
import com.example.moonday.data.api.ApiClient
import com.example.moonday.data.api.FreeAstroApiService
import com.example.moonday.data.model.MoonMonthResponse
import com.example.moonday.data.model.MoonPhaseResponse
import com.google.gson.Gson
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONObject
import kotlin.coroutines.resume

interface MoonRepository {
    suspend fun getMoonPhase(date: String? = null): Result<MoonPhaseResponse>
    suspend fun getMoonMonth(year: Int, month: Int): Result<MoonMonthResponse>
}

class MoonRepositoryImpl(
    private val apiService: FreeAstroApiService = ApiClient.service,
    private val useAsyncHttpClient: Boolean = true
) : MoonRepository {

    private val gson = Gson()

    override suspend fun getMoonPhase(date: String?): Result<MoonPhaseResponse> {
        if (!useAsyncHttpClient) {
            return try {
                val response = apiService.getMoonPhase(date = date)
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        val asyncResult = suspendCancellableCoroutine<Result<MoonPhaseResponse>> { continuation ->
            val client = AsyncHttpClient()
            client.addHeader("x-api-key", BuildConfig.FREE_ASTRO_API_KEY)
            client.addHeader("Content-Type", "application/json")

            val url = "${BuildConfig.FREE_ASTRO_BASE_URL}api/v1/moon/phase"
            val params = RequestParams().apply {
                date?.let { put("date", it) }
                put("include_visuals", "true")
                put("include_zodiac", "true")
                put("include_special", "true")
                put("include_traditional_moon", "true")
                put("include_interpretation", "true")
            }

            client.get(url, params, object : JsonHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Array<out Header>?, response: JSONObject?) {
                    try {
                        if (response != null) {
                            val moonPhase = gson.fromJson(response.toString(), MoonPhaseResponse::class.java)
                            if (continuation.isActive) continuation.resume(Result.success(moonPhase))
                        } else {
                            if (continuation.isActive) continuation.resume(Result.failure(Exception("Empty response")))
                        }
                    } catch (e: Exception) {
                        if (continuation.isActive) continuation.resume(Result.failure(e))
                    }
                }

                override fun onFailure(statusCode: Int, headers: Array<out Header>?, throwable: Throwable?, errorResponse: JSONObject?) {
                    if (continuation.isActive) {
                        continuation.resume(Result.failure(throwable ?: Exception("AsyncHTTPClient request failed with status $statusCode")))
                    }
                }
            })
        }

        if (asyncResult.isSuccess) {
            return asyncResult
        }

        // Fallback to Retrofit Coroutine on IO thread if AsyncHttpClient fails
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getMoonPhase(date = date)
                Result.success(response)
            } catch (e: Exception) {
                asyncResult
            }
        }
    }

    override suspend fun getMoonMonth(year: Int, month: Int): Result<MoonMonthResponse> {
        if (!useAsyncHttpClient) {
            return try {
                val response = apiService.getMoonMonth(year = year, month = month)
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        val asyncResult = suspendCancellableCoroutine<Result<MoonMonthResponse>> { continuation ->
            val client = AsyncHttpClient()
            client.addHeader("x-api-key", BuildConfig.FREE_ASTRO_API_KEY)
            client.addHeader("Content-Type", "application/json")

            val url = "${BuildConfig.FREE_ASTRO_BASE_URL}api/v1/moon/month"
            val params = RequestParams().apply {
                put("year", year.toString())
                put("month", month.toString())
                put("include_visuals", "true")
                put("include_zodiac", "true")
                put("include_traditional_moon", "true")
            }

            client.get(url, params, object : JsonHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Array<out Header>?, response: JSONObject?) {
                    try {
                        if (response != null) {
                            val moonMonth = gson.fromJson(response.toString(), MoonMonthResponse::class.java)
                            if (continuation.isActive) continuation.resume(Result.success(moonMonth))
                        } else {
                            if (continuation.isActive) continuation.resume(Result.failure(Exception("Empty response")))
                        }
                    } catch (e: Exception) {
                        if (continuation.isActive) continuation.resume(Result.failure(e))
                    }
                }

                override fun onFailure(statusCode: Int, headers: Array<out Header>?, throwable: Throwable?, errorResponse: JSONObject?) {
                    if (continuation.isActive) {
                        continuation.resume(Result.failure(throwable ?: Exception("AsyncHTTPClient request failed with status $statusCode")))
                    }
                }
            })
        }

        if (asyncResult.isSuccess) {
            return asyncResult
        }

        // Fallback to Retrofit Coroutine on IO thread if AsyncHttpClient fails
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getMoonMonth(year = year, month = month)
                Result.success(response)
            } catch (e: Exception) {
                asyncResult
            }
        }
    }
}
