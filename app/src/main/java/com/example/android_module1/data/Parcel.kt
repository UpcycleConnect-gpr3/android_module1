package com.example.android_module1.data

/**
 * Représente un colis à récupérer.
 *
 * Les données viennent du backend Go (cf. [ParcelApi]) : le titre et le lieu
 * sont ceux du casier de retrait, le code sert à générer le QR code de retrait.
 */
data class Parcel(
    val id: String,
    val title: String,
    val location: String,
    val pickupCode: String
)
