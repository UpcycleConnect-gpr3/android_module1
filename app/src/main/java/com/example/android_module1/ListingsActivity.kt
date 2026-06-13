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
import com.example.android_module1.data.ListingApi

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

        loadListings()
    }

    /** Récupère les annonces depuis le backend Go (hors thread principal). */
    private fun loadListings() {
        showMessage(getString(R.string.loading))
        Thread {
            val result = ListingApi.fetchAll()
            runOnUiThread {
                when (result) {
                    is ListingApi.Result.Success -> renderListings(result.data)
                    is ListingApi.Result.Error -> showMessage(getString(R.string.listings_error))
                }
            }
        }.start()
    }

    private fun renderListings(listings: List<Listing>) {
        val container = findViewById<LinearLayout>(R.id.listings_container)
        container.removeAllViews()

        if (listings.isEmpty()) {
            showMessage(getString(R.string.listings_empty))
            return
        }

        listings.forEach { listing ->
            val card = layoutInflater.inflate(R.layout.item_listing, container, false)
            card.findViewById<TextView>(R.id.listing_title).text = listing.title
            card.findViewById<TextView>(R.id.listing_price).text = listing.price
            card.findViewById<TextView>(R.id.listing_description).text = listing.description
            card.findViewById<TextView>(R.id.listing_score).text = listing.score
            card.findViewById<TextView>(R.id.listing_quantity).text = listing.quantity
            card.setOnClickListener {
                val intent = Intent(this, ListingDetailActivity::class.java)
                    .putExtra(ListingDetailActivity.EXTRA_LISTING_ID, listing.id)
                startActivity(intent)
            }
            container.addView(card)
        }
    }

    /** Affiche un message simple (chargement, vide ou erreur) à la place de la liste. */
    private fun showMessage(message: String) {
        val container = findViewById<LinearLayout>(R.id.listings_container)
        container.removeAllViews()
        val view = TextView(this).apply {
            text = message
            setTextColor(getColor(R.color.primary))
            textSize = 14f
        }
        container.addView(view)
    }
}
