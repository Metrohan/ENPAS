package com.example.loginregisterdemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    lateinit var usernameInput: EditText
    lateinit var passwordInput: EditText
    lateinit var loginButton: Button
    lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI elements
        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.passwd_input)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)

        // Register button click listener
        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Login button click listener
        loginButton.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val client = OkHttpClient()

        // Prepare the JSON request body
        val requestBody = JSONObject().apply {
            put("phone", usernameInput.text.toString()) // Username should be phone in this case
            put("passwd", passwordInput.text.toString())
        }.toString().toRequestBody("application/json".toMediaTypeOrNull())

        // Create the request
        val request = Request.Builder()
            .url("http://192.168.1.x:3000/login")
            .post(requestBody)
            .build()

        // Execute the request asynchronously
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Login", "Error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                // Handle the response
                if (response.isSuccessful) {
                    Log.d("Login", "Success: ${response.body?.string()}")
                } else {
                    Log.e("Login", "Error: ${response.code}")
                }
            }
        })
    }
}
