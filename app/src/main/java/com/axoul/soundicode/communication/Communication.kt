package com.axoul.soundicode.communication

import android.os.Build
import androidx.annotation.RequiresApi
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*

class Communication {
    @RequiresApi(Build.VERSION_CODES.O)
    fun recognizeSongFromSignature(decod: DecodedSignature): String? {
        var jsonBody: JsonBody? = null
        try {
            jsonBody = JsonBody(((decod.numberSamples.toFloat() / decod.sampleRateHz.toFloat()) * 1000.0).toInt(), decod.encodeToUri())
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        val adapter: JsonAdapter<JsonBody> = moshi.adapter(JsonBody::class.java)
        val postData = adapter.toJson(jsonBody)
        val uuid1 = UUID.randomUUID().toString().uppercase(Locale.getDefault())
        val uuid2 = UUID.randomUUID().toString()
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("amp.shazam.com")
            .addPathSegments("discovery/v5/en/US/android/-/tag")
            .addPathSegment(uuid1)
            .addPathSegment(uuid2)
            .addQueryParameter("sync", "true")
            .addQueryParameter("webv3", "true")
            .addQueryParameter("sampling", "true")
            .addQueryParameter("connected", "")
            .addQueryParameter("shazamapiversion", "v3")
            .addQueryParameter("sharehub", "true")
            .addQueryParameter("video", "v3")
            .build()

        val header = Headers.Builder()
            .add("Content-Type", "application/json")
            .add("User-Agent", UserAgent.randUser)
            .add("Content-Language", "en_US")
            .build()

        val client = OkHttpClient()
        val body: RequestBody = postData.toByteArray(StandardCharsets.UTF_8).toRequestBody()
        val request = Request.Builder()
            .url(url)
            .headers(header)
            .post(body)
            .build()

        /*try {
            client.newCall(request).execute().use { response -> return response.body!!.string() }
        } catch (e: IOException) {
            e.printStackTrace()
        }*/
        try {
            val response: Response = client.newCall(request).execute()
            return response.body!!.string()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}