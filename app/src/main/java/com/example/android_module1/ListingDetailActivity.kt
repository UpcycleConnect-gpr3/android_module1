package com.example.android_module1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.android_module1.data.Listing
import com.example.android_module1.data.ListingApi

class ListingDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_LISTING_ID = "listing_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_listing_detail)
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
        findViewById<View>(R.id.nav_settings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        // Mise en relation pas encore branchée (API messagerie à venir).
        findViewById<View>(R.id.contact_button).setOnClickListener {
            Toast.makeText(this, R.string.listing_contact_soon, Toast.LENGTH_SHORT).show()
        }

        val id = intent.getStringExtra(EXTRA_LISTING_ID)
        if (id.isNullOrEmpty()) {
            finish()
            return
        }
        loadListing(id)
    }

    /** Récupère l'annonce depuis le backend Go (hors thread principal). */
    private fun loadListing(id: String) {
        Thread {
            val result = ListingApi.fetchById(id)
            runOnUiThread {
                when (result) {
                    is ListingApi.Result.Success -> renderListing(result.data)
                    is ListingApi.Result.Error -> {
                        Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }.start()
    }

    private fun renderListing(listing: Listing) {
        findViewById<TextView>(R.id.detail_title).text = listing.title
        findViewById<TextView>(R.id.detail_price).text = listing.price
        findViewById<TextView>(R.id.detail_description).text = listing.description
        findViewById<TextView>(R.id.detail_score).text = listing.score
        findViewById<TextView>(R.id.detail_quantity).text = listing.quantity
        findViewById<TextView>(R.id.detail_date).text = listing.publishedDate
    }
}
