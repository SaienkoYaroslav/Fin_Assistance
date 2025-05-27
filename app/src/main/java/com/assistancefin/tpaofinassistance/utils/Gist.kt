package com.assistancefin.tpaofinassistance.utils

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

object Gist {

    private const val URL_STRING = "https://gist.githubusercontent.com/YaroslavGGG/92c790b8a2bc36b78e5f08da12f7b01f/raw/com.assistancefin.tpaofinassistance"

    suspend fun getDataJson(): DataJSON? = withContext(Dispatchers.IO) {
        try {
            val url = URL(URL_STRING)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000  // 5 секунд таймаут на з'єднання
            connection.readTimeout = 5000  // 5 секунд таймаут на читання
            connection.doInput = true

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream: InputStream = connection.inputStream
                val response =
                    inputStream.readBytes().toString(Charsets.UTF_8) // Читаємо відповідь у String
                inputStream.close()

                val json = JSONObject(response)
                Log.d("TAG_WEB", "stringJson = $json")

                DataJSON(
                    link = json.optString("domain", ""),
                    afDevKey = json.optString("af_dev_key", "")
                )
            } else {
                Log.d("TAG_WEB", "Gist = HTTP Error: ${connection.responseCode}")
                null
            }
        } catch (e: Exception) {
            Log.d("TAG_WEB", "Gist = Exception: ${e.message}")
            null
        }
    }

    data class DataJSON(
        val link: String,
        val afDevKey: String
    )

}