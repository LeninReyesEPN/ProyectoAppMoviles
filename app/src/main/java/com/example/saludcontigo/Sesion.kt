package com.example.saludcontigo

import android.content.Context

/**
 * Guarda de forma sencilla los datos de la persona que usa la app
 * (nombre y cédula) para poder mostrarlos en las demás pantallas,
 * como el saludo del Home. Usa SharedPreferences, así se conservan
 * aunque se cierre la app.
 */
object Sesion {

    private const val PREFS = "sesion_usuario"
    private const val KEY_NOMBRE = "nombre"
    private const val KEY_CEDULA = "cedula"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    /** Guarda el nombre completo y la cédula de la persona. */
    fun guardar(context: Context, nombre: String, cedula: String) {
        prefs(context).edit()
            .putString(KEY_NOMBRE, nombre.trim())
            .putString(KEY_CEDULA, cedula.trim())
            .apply()
    }

    /** Guarda solo la cédula (por ejemplo, al iniciar sesión). */
    fun guardarCedula(context: Context, cedula: String) {
        prefs(context).edit()
            .putString(KEY_CEDULA, cedula.trim())
            .apply()
    }

    /** Nombre completo guardado (vacío si no hay). */
    fun obtenerNombre(context: Context): String =
        prefs(context).getString(KEY_NOMBRE, "").orEmpty()

    /** Solo el primer nombre, ideal para un saludo corto. */
    fun obtenerPrimerNombre(context: Context): String =
        obtenerNombre(context).trim().split(" ").firstOrNull().orEmpty()

    /** Cédula guardada (vacía si no hay). */
    fun obtenerCedula(context: Context): String =
        prefs(context).getString(KEY_CEDULA, "").orEmpty()
}
