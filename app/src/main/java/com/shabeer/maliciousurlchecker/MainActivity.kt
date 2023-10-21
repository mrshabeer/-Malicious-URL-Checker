package com.shabeer.maliciousurlchecker

import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.shabeer.maliciousurlchecker.databinding.ActivityMainBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import org.json.JSONObject
import okhttp3.Request

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = getColor(R.color.main_theme)

        binding.animationView.visibility = View.VISIBLE

        binding.checkButton.setOnClickListener {
            binding.checkView.visibility = View.VISIBLE
            val url = binding.urlLink.text.toString()
            if (url.isNotEmpty()) {
                CheckUrlTask().execute(url)
        }
    }
}

    inner class CheckUrlTask : AsyncTask<String, Void, Boolean>() {

        override fun doInBackground(vararg params: String?): Boolean {
            val url = params[0]
            val apiKey = "AIzaSyCbPDA6f-3E0h44Q9drV3hYXAZ5jOCW3RQ"
            val apiUrl = "https://safebrowsing.googleapis.com/v4/threatMatches:find"

            val client = OkHttpClient()
            val json = JSONObject()
            json.put("client", JSONObject().put("clientId", "your-client-id").put("clientVersion", "1.5.2"))
            json.put("threatInfo", JSONObject().put("threatTypes", listOf("MALWARE", "SOCIAL_ENGINEERING", "THREAT_TYPE_UNSPECIFIED", "UNWANTED_SOFTWARE")).put("platformTypes", listOf("ANY_PLATFORM")).put("threatEntryTypes", listOf("URL")).put("threatEntries", listOf(JSONObject().put("url", url))))

            val body = json.toString()
            val request = Request.Builder()
                .url("$apiUrl?key=$apiKey")
                .post(okhttp3.RequestBody.create("application/json".toMediaTypeOrNull(), body))
                .build()

            val response = client.newCall(request).execute()
            val responseData = response.body?.string()

            val jsonResponse = JSONObject(responseData)
            val matches = jsonResponse.optJSONArray("matches")

            return matches != null && matches.length() > 0
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            if (result == true) {
                binding.wrong.visibility = View.VISIBLE
                binding.resultTextView.text = "The URL is MALICIOUS."
            } else {
                binding.checkView.visibility = View.GONE
                binding.right.visibility = View.VISIBLE
                binding.resultTextView.text = "The URL is SAFE."
            }
        }
    }
}