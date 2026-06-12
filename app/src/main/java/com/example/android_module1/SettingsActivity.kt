package com.example.android_module1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<View>(R.id.back_button).setOnClickListener { finish() }
        findViewById<View>(R.id.nav_home).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        // Données de test ; seront fournies par une API par la suite.
        findViewById<EditText>(R.id.username_input).setText(getString(R.string.settings_sample_username))
        findViewById<EditText>(R.id.email_input).setText(getString(R.string.settings_sample_email))

        // Enregistrement local factice ; sera envoyé à l'API par la suite.
        findViewById<View>(R.id.save_button).setOnClickListener {
            Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show()
        }

        // Déconnexion : retour au login en vidant la pile d'écrans.
        findViewById<View>(R.id.logout_button).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
    }
}
