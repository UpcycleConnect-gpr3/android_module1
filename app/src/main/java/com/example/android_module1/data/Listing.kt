package com.example.android_module1.data

/**
 * Représente une annonce publiée sur Upcycle Connect.
 *
 * Données de test pour l'instant ; seront fournies par une API par la suite
 * (cf. [Listing.sampleData]). L'API fournira aussi l'URL de la photo de
 * l'annonce ; en attendant, l'écran de détail affiche une image générique.
 */
data class Listing(
    val id: String,
    val title: String,
    val description: String,
    val price: String,
    val condition: String,
    val location: String,
    val seller: String,
    val publishedDate: String
) {
    companion object {
        /** Jeu de données de test, à remplacer par un appel API. */
        fun sampleData(): List<Listing> = listOf(
            Listing(
                id = "1",
                title = "Commode vintage relookée",
                description = "Commode en pin des années 70, poncée et repeinte à la main.",
                price = "85€",
                condition = "Très bon état",
                location = "Paris 11e",
                seller = "Atelier Renouveau",
                publishedDate = "02-06-2026"
            ),
            Listing(
                id = "2",
                title = "Sac à main en chambre à air",
                description = "Sac artisanal fabriqué à partir de chambres à air recyclées.",
                price = "39€",
                condition = "Neuf",
                location = "Lyon 7e",
                seller = "RecycCuir",
                publishedDate = "28-05-2026"
            ),
            Listing(
                id = "3",
                title = "Étagère en palettes",
                description = "Étagère murale réalisée en bois de palette, traitée et vernie.",
                price = "45€",
                condition = "Bon état",
                location = "Bordeaux",
                seller = "Bois & Co",
                publishedDate = "21-05-2026"
            ),
            Listing(
                id = "4",
                title = "Veste en jean patchwork",
                description = "Veste unique assemblée à partir de jeans de seconde main.",
                price = "60€",
                condition = "Très bon état",
                location = "Nantes",
                seller = "Gravityfall",
                publishedDate = "15-05-2026"
            )
        )

        /** Recherche une annonce par son identifiant (sera un appel API). */
        fun findById(id: String?): Listing? = sampleData().find { it.id == id }
    }
}
