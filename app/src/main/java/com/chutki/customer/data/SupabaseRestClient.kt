package com.chutki.customer.data

import com.chutki.customer.BuildConfig
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Supabase PostgREST transport for Chutki.
 * Uses the Supabase publishable key and signed-in Firebase user's ID token.
 * Supabase Third-Party Auth and matching JWT/RLS claims must be configured.
 * Never embed a Supabase secret/service_role key in an Android app.
 */
class SupabaseRestClient(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val http: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
) {
    private val baseUrl = BuildConfig.SUPABASE_URL.trimEnd('/')
    private val publishableKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY

    private suspend fun firebaseIdToken(): String {
        val user = firebaseAuth.currentUser ?: throw IllegalStateException("Please sign in first.")
        return user.getIdToken(true).await().token
            ?: throw IllegalStateException("Could not obtain Firebase ID token.")
    }

    private fun validateTable(table: String) {
        require(table.matches(Regex("[A-Za-z_][A-Za-z0-9_]*"))) { "Invalid table name." }
    }

    suspend fun request(
        method: String,
        pathAndQuery: String,
        jsonBody: String? = null,
        prefer: String? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            require(method in setOf("GET", "POST", "PATCH", "DELETE", "PUT"))
            require(pathAndQuery.startsWith("/") && !pathAndQuery.startsWith("//"))
            val token = firebaseIdToken()
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = jsonBody?.toRequestBody(mediaType)
            val builder = Request.Builder()
                .url("$baseUrl/rest/v1$pathAndQuery")
                .header("apikey", publishableKey)
                .header("Authorization", "Bearer $token")
                .header("Accept", "application/json")
            if (body != null) builder.header("Content-Type", "application/json")
            if (!prefer.isNullOrBlank()) builder.header("Prefer", prefer)
            when (method) {
                "GET" -> builder.get()
                "POST" -> builder.post(body ?: ByteArray(0).toRequestBody(null))
                "PATCH" -> builder.patch(body ?: ByteArray(0).toRequestBody(null))
                "PUT" -> builder.put(body ?: ByteArray(0).toRequestBody(null))
                "DELETE" -> builder.delete(body)
            }
            http.newCall(builder.build()).execute().use { response ->
                val responseText = response.body?.string().orEmpty()
                if (!response.isSuccessful) throw IOException("Supabase HTTP ${response.code}: $responseText")
                responseText
            }
        }
    }


    /** Calls the shared chutki-api /me route using the signed-in Firebase ID token.
     * The token is sent only in the HTTPS Authorization header and is never logged or displayed.
     */
    suspend fun testChutkiApiMe(): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val token = firebaseIdToken()
            val url = "${BuildConfig.SUPABASE_URL.trimEnd('/')}/functions/v1/chutki-api/me"
            val request = Request.Builder()
                .url(url)
                .header("apikey", publishableKey)
                .header("Authorization", "Bearer $token")
                .header("Accept", "application/json")
                .get()
                .build()
            http.newCall(request).execute().use { response ->
                val responseText = response.body?.string().orEmpty()
                "HTTP ${response.code}\n$responseText"
            }
        }
    }

    suspend fun select(table: String, query: String = "select=*"): Result<String> {
        validateTable(table)
        return request("GET", "/$table?$query")
    }

    suspend fun insert(table: String, jsonBody: String, returnRepresentation: Boolean = true): Result<String> {
        validateTable(table)
        val prefer = if (returnRepresentation) "return=representation" else "return=minimal"
        return request("POST", "/$table", jsonBody, prefer)
    }

    suspend fun update(table: String, filterQuery: String, jsonBody: String): Result<String> {
        validateTable(table)
        require(filterQuery.startsWith("?") && filterQuery.contains("="))
        return request("PATCH", "/$table$filterQuery", jsonBody, "return=representation")
    }
}
