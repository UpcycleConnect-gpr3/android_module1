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

        // Données de test ; l'annonce sera récupérée via l'API par la suite.
        val listing = Listing.findById(intent.getStringExtra(EXTRA_LISTING_ID))
        if (listing == null) {
            finish()
            return
        }
        renderListing(listing)

        // Mise en relation pas encore branchée (API messagerie à venir).
        findViewById<View>(R.id.contact_button).setOnClickListener {
            Toast.makeText(this, R.string.listing_contact_soon, Toast.LENGTH_SHORT).show()
        }
    }

    private fun renderListing(listing: Listing) {
        findViewById<TextView>(R.id.detail_title).text = listing.title
        findViewById<TextView>(R.id.detail_price).text = listing.price
        findViewById<TextView>(R.id.detail_description).text = listing.description
        findViewById<TextView>(R.id.detail_condition).text = listing.condition
        findViewById<TextView>(R.id.detail_location).text = listing.location
        findViewById<TextView>(R.id.detail_seller).text = listing.seller
        findViewById<TextView>(R.id.detail_date).text = listing.publishedDate
    }
}
