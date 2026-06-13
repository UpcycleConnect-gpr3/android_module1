package com.example.android_module1.data

/**
 * Représente une annonce ("object" côté backend) publiée sur Upcycle Connect.
 *
 * Les données sont fournies par le backend Go (cf. [ListingApi]). Le backend
 * expose le nom, la description, le prix, l'indice éco (score), la quantité et
 * la date de publication ; la photo arrivera plus tard (image_path), en
 * attendant l'écran de détail affiche une image générique.
 */
data class Listing(
    val id: String,
    val title: String,
    val description: String,
    val price: String,
    val score: String,
    val quantity: String,
    val publishedDate: String
)
