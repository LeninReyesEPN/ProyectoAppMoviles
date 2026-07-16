package com.example.saludcontigo.util

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Hash de contrasenas con sal usando PBKDF2 (API estandar de Java, sin dependencias extra).
 * Nunca se guarda la contrasena en texto plano en Room.
 */
object PasswordUtil {

    private const val ITERACIONES = 10_000
    private const val LONGITUD_CLAVE_BITS = 256
    private const val ALGORITMO = "PBKDF2WithHmacSHA1"

    fun generarSalt(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    fun hash(password: String, salt: String): String {
        val saltBytes = Base64.decode(salt, Base64.NO_WRAP)
        val spec = PBEKeySpec(password.toCharArray(), saltBytes, ITERACIONES, LONGITUD_CLAVE_BITS)
        val claveDerivada = SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).encoded
        return Base64.encodeToString(claveDerivada, Base64.NO_WRAP)
    }

    fun verificar(password: String, salt: String, hashGuardado: String): Boolean =
        hash(password, salt) == hashGuardado
}
