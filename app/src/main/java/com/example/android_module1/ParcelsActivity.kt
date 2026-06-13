package com.example.android_module1

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.android_module1.data.Parcel
import com.example.android_module1.data.ParcelApi
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter

class ParcelsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_parcels)
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

        loadParcels()
    }

    /** Récupère les colis depuis le backend Go (hors thread principal). */
    private fun loadParcels() {
        showMessage(getString(R.string.loading))
        Thread {
            val result = ParcelApi.fetchAll()
            runOnUiThread {
                when (result) {
                    is ParcelApi.Result.Success -> renderParcels(result.data)
                    is ParcelApi.Result.Error -> showMessage(getString(R.string.parcels_error))
                }
            }
        }.start()
    }

    private fun renderParcels(parcels: List<Parcel>) {
        val container = findViewById<LinearLayout>(R.id.parcels_container)
        container.removeAllViews()

        if (parcels.isEmpty()) {
            showMessage(getString(R.string.parcels_empty))
            return
        }

        val qrSizePx = (140 * resources.displayMetrics.density).toInt()
        parcels.forEach { parcel ->
            val card = layoutInflater.inflate(R.layout.item_parcel, container, false)
            card.findViewById<TextView>(R.id.parcel_title).text = parcel.title
            card.findViewById<TextView>(R.id.parcel_location).text = parcel.location
            card.findViewById<TextView>(R.id.parcel_code).text = parcel.pickupCode
            val qr = card.findViewById<ImageView>(R.id.parcel_qr)
            if (parcel.pickupCode.isNotBlank()) {
                qr.setImageBitmap(generateQrCode(parcel.pickupCode, qrSizePx))
            } else {
                qr.setImageDrawable(null)
            }
            container.addView(card)
        }
    }

    /** Affiche un message simple (chargement, vide ou erreur) à la place de la liste. */
    private fun showMessage(message: String) {
        val container = findViewById<LinearLayout>(R.id.parcels_container)
        container.removeAllViews()
        val view = TextView(this).apply {
            text = message
            setTextColor(getColor(R.color.primary))
            textSize = 14f
        }
        container.addView(view)
    }

    /** Génère un QR code (noir sur blanc) à partir d'un contenu texte. */
    private fun generateQrCode(content: String, size: Int): Bitmap {
        val hints = mapOf(EncodeHintType.MARGIN to 1)
        val matrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
        val pixels = IntArray(size * size)
        for (y in 0 until size) {
            val offset = y * size
            for (x in 0 until size) {
                pixels[offset + x] = if (matrix.get(x, y)) Color.BLACK else Color.WHITE
            }
        }
        return Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888).apply {
            setPixels(pixels, 0, size, 0, 0, size, size)
        }
    }
}
