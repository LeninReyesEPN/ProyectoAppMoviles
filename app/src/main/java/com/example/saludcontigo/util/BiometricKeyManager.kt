package com.example.saludcontigo.util

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

/**
 * Una clave de Android Keystore por cedula (alias = "huella_<cedula>"), marcada con
 * setUserAuthenticationRequired + setInvalidatedByBiometricEnrollment. Esto liga el
 * ingreso con huella a la cuenta que la activo: si otra cedula nunca activo su huella
 * no existe clave para ella y el login biometrico falla; y si cambian las huellas
 * registradas en el equipo, la clave se invalida sola (hay que reactivarla).
 */
object BiometricKeyManager {

    private const val PROVEEDOR = "AndroidKeyStore"
    private const val TRANSFORMACION =
        KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_GCM + "/" + KeyProperties.ENCRYPTION_PADDING_NONE

    private fun alias(cedula: String) = "huella_$cedula"

    private fun keyStore(): KeyStore = KeyStore.getInstance(PROVEEDOR).apply { load(null) }

    fun crearClave(cedula: String) {
        val generador = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, PROVEEDOR)
        val spec = KeyGenParameterSpec.Builder(
            alias(cedula),
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setUserAuthenticationRequired(true)
            .setInvalidatedByBiometricEnrollment(true)
            .build()
        generador.init(spec)
        generador.generateKey()
    }

    fun existeClave(cedula: String): Boolean = keyStore().containsAlias(alias(cedula))

    /**
     * Puede lanzar android.security.keystore.KeyPermanentlyInvalidatedException si las
     * huellas del equipo cambiaron desde que se activo el acceso para esta cedula.
     */
    fun obtenerCipher(cedula: String): Cipher? {
        val clave = obtenerClave(cedula) ?: return null
        val cipher = Cipher.getInstance(TRANSFORMACION)
        cipher.init(Cipher.ENCRYPT_MODE, clave)
        return cipher
    }

    fun eliminarClave(cedula: String) {
        val ks = keyStore()
        if (ks.containsAlias(alias(cedula))) {
            ks.deleteEntry(alias(cedula))
        }
    }

    private fun obtenerClave(cedula: String): SecretKey? =
        keyStore().getKey(alias(cedula), null) as? SecretKey
}
