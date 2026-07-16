package com.example.saludcontigo

import android.content.Context

/**
 * Guarda solo un puntero a la persona con sesion activa (su cedula).
 * Los datos reales (nombre, edad, citas) viven en Room.
 */
object Sesion {

    private const val PREFS = "sesion_usuario"
    private const val KEY_CEDULA = "cedula"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    /** Marca esta cedula como la sesion activa. */
    fun iniciarSesion(context: Context, cedula: String) {
        prefs(context).edit()
            .putString(KEY_CEDULA, cedula.trim())
            .apply()
    }

    /** Cedula de la sesion activa (vacia si no hay). */
    fun obtenerCedula(context: Context): String =
        prefs(context).getString(KEY_CEDULA, "").orEmpty()

    fun haySesionActiva(context: Context): Boolean =
        obtenerCedula(context).isNotBlank()

    /**
     * Vuelve a la pantalla de Login, pero NO borra la cedula recordada: si se borrara,
     * el acceso rapido con huella (activado en Mi Perfil) dejaria de saber a que cuenta
     * entrar despues de cerrar sesion, justo el caso de uso que debe seguir funcionando
     * (entrar de nuevo con huella, sin volver a escribir la contrasena).
     */
    fun cerrarSesion(context: Context) {
        // Intencionalmente vacio: ver comentario de la funcion.
    }
}
