package com.example.android_module1.data

import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Client minimal pour les colis du backend Go ("packages" + "lockers").
 * Même approche que [ListingApi] : HttpURLConnection + org.json, sans dépendance.
 *
 * Un "package" porte le code de retrait et un locker_id ; le lieu de retrait
 * (nom + adresse) vient de la table LOCKERS, qu'on récupère en une fois pour
 * éviter un appel par colis.
 */
object ParcelApi {

    private const val BASE_URL = "http://10.0.2.2:4343"

    sealed class Result<out T> {
        data class Success<T>(val data: T) : Result<T>()
        data class Error(val message: String) : Result<Nothing>()
    }

    /** Lieu de retrait résolu depuis un locker. */
    private data class Locker(val name: String, val street: String, val city: String, val zip: String)

    /** Récupère les colis. Appel bloquant : à exécuter hors du thread principal. */
    fun fetchAll(): Result<List<Parcel>> {
        return try {
            val lockers = fetchLockers()
            val (code, json) = getJson("/packages")
            if (code == 200 && json.optBoolean("success")) {
                val array = json.optJSONArray("data") ?: JSONArray()
                val parcels = (0 until array.length()).map {
                    parseParcel(array.getJSONObject(it), lockers)
                }
                Result.Success(parcels)
            } else {
                Result.Error("Impossible de charger les colis")
            }
        } catch (e: Exception) {
            Result.Error("Serveur injoignable (${e.javaClass.simpleName})")
        }
    }

    /** Map locker_id -> locker, pour résoudre le lieu de retrait de chaque colis. */
    private fun fetchLockers(): Map<String, Locker> {
        val (code, json) = getJson("/lockers")
        if (code != 200 || !json.optBoolean("success")) return emptyMap()
        val array = json.optJSONArray("data") ?: return emptyMap()
        val map = HashMap<String, Locker>()
        for (i in 0 until array.length()) {
            val o = array.getJSONObject(i)
            map[o.optString("id")] = Locker(
                name = o.optString("name"),
                street = o.optString("street"),
                city = o.optString("city"),
                zip = o.optString("zip_code")
            )
        }
        return map
    }

    private fun parseParcel(pkg: JSONObject, lockers: Map<String, Locker>): Parcel {
        val locker = lockers[pkg.optString("locker_id")]
        return Parcel(
            id = pkg.optString("id"),
            title = locker?.name?.takeIf { it.isNotBlank() } ?: "Colis à récupérer",
            location = formatLocation(locker),
            pickupCode = pkg.optString("code")
        )
    }

    /** "12 rue des Lilas, 75011 Paris" à partir des champs du locker. */
    private fun formatLocation(locker: Locker?): String {
        if (locker == null) return "Lieu de retrait à préciser"
        val address = listOf(locker.zip, locker.city).filter { it.isNotBlank() }.joinToString(" ")
        return listOf(locker.street, address).filter { it.isNotBlank() }.joinToString(", ")
            .ifBlank { "Lieu de retrait à préciser" }
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
}
