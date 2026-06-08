package com.example.android_module1.data

/**
 * Représente un colis à récupérer.
 *
 * Données de test pour l'instant ; seront fournies par une API par la suite
 * (cf. [Parcel.sampleData]).
 */
data class Parcel(
    val id: String,
    val title: String,
    val location: String,
    val pickupCode: String
) {
    companion object {
        /** Jeu de données de test, à remplacer par un appel API. */
        fun sampleData(): List<Parcel> = listOf(
            Parcel(
                id = "1",
                title = "Veste en jean upcyclée",
                location = "Point relais — 12 rue des Lilas, Paris 11e",
                pickupCode = "UPC-7F3A-2025"
            ),
            Parcel(
                id = "2",
                title = "Lampe de bureau restaurée",
                location = "Casier connecté — Gare de Lyon, Hall 2",
                pickupCode = "UPC-9K1D-8842"
            ),
            Parcel(
                id = "3",
                title = "Lot de livres de poche",
                location = "Point relais — 5 av. Jean Jaurès, Lyon 7e",
                pickupCode = "UPC-3B6C-5571"
            )
        )
    }
}
