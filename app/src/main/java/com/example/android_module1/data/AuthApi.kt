package com.example.android_module1.data

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Client minimal pour le backend Go d'authentification.
 * Aucune dépendance externe : HttpURLConnection + org.json, inclus dans Android.
 */
object AuthApi {

    // 10.0.2.2 = adresse spéciale de l'émulateur qui pointe vers le localhost du PC.
    // Sur un téléphone physique, remplacer par l'IP locale du PC (ex: 192.168.1.X).
    private const val BASE_URL = "http://10.0.2.2:4242"

    sealed class LoginResult {
        data class Success(val bearerToken: String) : LoginResult()
        data class Error(val message: String) : LoginResult()
    }

    sealed class RegisterResult {
        data object Success : RegisterResult()
        data class Error(val message: String) : RegisterResult()
    }

    /** Appel bloquant : à exécuter hors du thread principal. */
    fun login(email: String, password: String): LoginResult {
        return try {
            val (code, json) = postJson("/auth/login/", email, password)

            if (code == 200 && json.optBoolean("success")) {
                val data = json.getJSONObject("data")
                if (data.optBoolean("totp_required")) {
                    LoginResult.Error("Compte avec TOTP : non géré par l'app pour l'instant")
                } else {
                    LoginResult.Success(data.getString("bearer_token"))
                }
            } else {
                LoginResult.Error(errorMessage(json, "Identifiants incorrects"))
            }
        } catch (e: Exception) {
            LoginResult.Error("Serveur injoignable (${e.javaClass.simpleName})")
        }
    }

    /** Appel bloquant : à exécuter hors du thread principal. */
    fun register(email: String, password: String): RegisterResult {
        return try {
            val (code, json) = postJson("/auth/register/", email, password)

            if (code in 200..299 && json.optBoolean("success")) {
                RegisterResult.Success
            } else {
                RegisterResult.Error(errorMessage(json, "Inscription refusée"))
            }
        } catch (e: Exception) {
            RegisterResult.Error("Serveur injoignable (${e.javaClass.simpleName})")
        }
    }

    private fun postJson(path: String, email: String, password: String): Pair<Int, JSONObject> {
        // Le "/" final des routes est obligatoire : côté Go elles sont déclarées avec "{$}"
        val connection = URL(BASE_URL + path).openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.doOutput = true
        connection.connectTimeout = 5000
        connection.readTimeout = 5000

        val body = JSONObject()
            .put("email", email)
            .put("password", password)
        connection.outputStream.use { it.write(body.toString().toByteArray()) }

        val stream =
            if (connection.responseCode in 200..299) connection.inputStream
            else connection.errorStream
        return connection.responseCode to JSONObject(stream.bufferedReader().use { it.readText() })
    }

    /** Extrait le message le plus utile de la réponse d'erreur du backend. */
    private fun errorMessage(json: JSONObject, fallback: String): String {
        val errors = json.optJSONArray("errors")
        if (errors != null && errors.length() > 0) {
            val first = errors.getJSONObject(0)
            return "${first.optString("field")} : ${first.optString("message")}"
        }
        return json.optString("message", fallback)
    }
}
