package com.example.android_module1

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.android_module1.data.AuthApi
import com.google.android.material.button.MaterialButton

class
MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val submitButton = findViewById<MaterialButton>(R.id.login_submit)
        submitButton.setOnClickListener {
            val email = findViewById<EditText>(R.id.email_input).text.toString().trim()
            val password = findViewById<EditText>(R.id.password_input).text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, R.string.login_error_empty, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Évite les doubles clics pendant que la requête est en cours
            submitButton.isEnabled = false

            // Le réseau est interdit sur le thread principal : on passe par un thread séparé
            Thread {
                val result = AuthApi.login(email, password)
                runOnUiThread {
                    submitButton.isEnabled = true
                    when (result) {
                        is AuthApi.LoginResult.Success -> {
                            // Token gardé pour les futurs appels authentifiés (ex: /auth/me/)
                            getSharedPreferences("auth", MODE_PRIVATE).edit()
                                .putString("bearer_token", result.bearerToken)
                                .apply()
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        }
                        is AuthApi.LoginResult.Error ->
                            Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
                    }
                }
            }.start()
        }

        findViewById<TextView>(R.id.login_signup_link).setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}