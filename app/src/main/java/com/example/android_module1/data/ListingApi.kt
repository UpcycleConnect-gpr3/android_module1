package com.example.android_module1.data

import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Client minimal pour le backend Go des annonces ("objects").
 * Même approche que [AuthApi] : HttpURLConnection + org.json, sans dépendance.
 *
 * Les routes GET /objects et GET /objects/{id} sont publiques (pas de token).
 */
object ListingApi {

    // 10.0.2.2 = localhost du PC vu depuis l'émulateur.
    // Backend annonces (go_upcycle_connect_backend) : port 4343.
    // Sur un téléphone physique, remplacer par l'IP locale du PC.
    private const val BASE_URL = "http://10.0.2.2:4343"

    sealed class Result<out T> {
        data class Success<T>(val data: T) : Result<T>()
        data class Error(val message: String) : Result<Nothing>()
    }

    /** Liste toutes les annonces. Appel bloquant : à exécuter hors du thread principal. */
    fun fetchAll(): Result<List<Listing>> {
        return try {
            val (code, json) = getJson("/objects")
            if (code == 200 && json.optBoolean("success")) {
                val array = json.optJSONArray("data") ?: JSONArray()
                val listings = (0 until array.length())
                    .map { parseListing(array.getJSONObject(it)) }
                Result.Success(listings)
            } else {
                Result.Error("Impossible de charger les annonces")
            }
        } catch (e: Exception) {
            Result.Error("Serveur injoignable (${e.javaClass.simpleName})")
        }
    }

    /** Récupère une annonce par son identifiant. Appel bloquant. */
    fun fetchById(id: String): Result<Listing> {
        return try {
            val (code, json) = getJson("/objects/$id")
            if (code == 200 && json.optBoolean("success")) {
                Result.Success(parseListing(json.getJSONObject("data")))
            } else {
                Result.Error("Annonce introuvable")
            }
        } catch (e: Exception) {
            Result.Error("Serveur injoignable (${e.javaClass.simpleName})")
        }
    }

    private fun getJson(path: String): Pair<Int, JSONObject> {
        val connection = URL(BASE_URL + path).openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 5000
        connection.readTimeout = 5000

        val stream =
            if (connection.responseCode in 200..299) connection.inputStream
            else connection.errorStream
        return connection.responseCode to JSONObject(stream.bufferedReader().use { it.readText() })
    }

    /** Convertit un "object" du backend en [Listing] affichable. */
    private fun parseListing(obj: JSONObject): Listing = Listing(
        id = obj.optString("id"),
        title = obj.optString("name"),
        description = obj.optString("description"),
        price = formatPrice(obj.optDouble("price", 0.0)),
        score = formatScore(obj.optDouble("score", 0.0)),
        quantity = obj.optInt("quantity", 0).toString(),
        publishedDate = formatDate(obj.optString("created_at"))
    )

    /** 85.0 -> "85€", 39.5 -> "39,5€". */
    private fun formatPrice(price: Double): String {
        val text = if (price % 1.0 == 0.0) price.toInt().toString()
        else price.toString().replace('.', ',')
        return "$text€"
    }

    private fun formatScore(score: Double): String =
        String.format("%.1f", score).replace('.', ',')

    /** "2026-06-02T10:00:00Z" -> "02-06-2026" ; renvoie la valeur brute en cas d'imprévu. */
    private fun formatDate(raw: String): String {
        val date = raw.take(10) // "2026-06-02"
        val parts = date.split("-")
        return if (parts.size == 3) "${parts[2]}-${parts[1]}-${parts[0]}" else raw
    }
}
