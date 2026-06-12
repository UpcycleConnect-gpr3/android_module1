package com.example.android_module1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.android_module1.data.Listing

class ListingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_listings)
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

        // Données de test ; seront fournies par une API par la suite.
        renderListings(Listing.sampleData())
    }

    private fun renderListings(listings: List<Listing>) {
        val container = findViewById<LinearLayout>(R.id.listings_container)
        container.removeAllViews()

        if (listings.isEmpty()) {
            val empty = TextView(this).apply {
                text = getString(R.string.listings_empty)
                setTextColor(getColor(R.color.primary))
                textSize = 14f
            }
            container.addView(empty)
            return
        }

        listings.forEach { listing ->
            val card = layoutInflater.inflate(R.layout.item_listing, container, false)
            card.findViewById<TextView>(R.id.listing_title).text = listing.title
            card.findViewById<TextView>(R.id.listing_price).text = listing.price
            card.findViewById<TextView>(R.id.listing_description).text = listing.description
            card.findViewById<TextView>(R.id.listing_condition).text = listing.condition
            card.findViewById<TextView>(R.id.listing_location).text = listing.location
            card.setOnClickListener {
                val intent = Intent(this, ListingDetailActivity::class.java)
                    .putExtra(ListingDetailActivity.EXTRA_LISTING_ID, listing.id)
                startActivity(intent)
            }
            container.addView(card)
        }
    }
}
