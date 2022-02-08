package com.axoul.soundicode.history

import android.app.Activity
import android.content.Context
import com.axoul.soundicode.communication.JsonResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.IOException

object History {
    private const val name = "historySetting"
    private const val key_name = "json"

    fun getHistory(app: Activity?): List<JsonResponse>? {
        val sharedPreferences = app!!.getSharedPreferences(name, Context.MODE_PRIVATE)
        if (sharedPreferences != null) {
            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()
            val type = Types.newParameterizedType(List::class.java, JsonResponse::class.java)
            val adapter = moshi.adapter<List<JsonResponse>>(type)
            try {
                return (adapter.fromJson(sharedPreferences.getString(key_name, "")))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }

    fun setHistory(app: Activity?, newResponse: JsonResponse?) {
        var listHistory = getHistory(app) as ArrayList<JsonResponse>?
        if (listHistory == null) {
            listHistory = ArrayList()
        }
        if (newResponse != null) {
            listHistory.add(newResponse)
        }
        val sharedPreferences = app!!.getSharedPreferences(name, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        val type = Types.newParameterizedType(List::class.java, JsonResponse::class.java)
        val adapter = moshi.adapter<List<JsonResponse?>>(type)
        editor.putString(key_name, adapter.toJson(listHistory))
        editor.apply()
    }

    fun clearHistory(app: Activity?) {
        val sharedPreferences = app!!.getSharedPreferences(name, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear().apply()
    }
}