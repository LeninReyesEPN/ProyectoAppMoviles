package com.example.saludcontigo.data.repository

import android.content.Context
import com.example.saludcontigo.data.local.AppDatabase
import com.example.saludcontigo.data.local.UserEntity
import com.example.saludcontigo.util.PasswordUtil

class UserRepository(context: Context) {

    private val userDao = AppDatabase.obtener(context).userDao()

    suspend fun registrar(
        cedula: String,
        nombre: String,
        edad: Int?,
        passwordHash: String,
        passwordSalt: String
    ) {
        userDao.insert(
            UserEntity(
                cedula = cedula,
                nombre = nombre,
                edad = edad,
                passwordHash = passwordHash,
                passwordSalt = passwordSalt
            )
        )
    }

    suspend fun buscarPorCedula(cedula: String): UserEntity? = userDao.getByCedula(cedula)

    suspend fun validarCredenciales(cedula: String, password: String): UserEntity? {
        val usuario = userDao.getByCedula(cedula) ?: return null
        val esValida = PasswordUtil.verificar(password, usuario.passwordSalt, usuario.passwordHash)
        return if (esValida) usuario else null
    }

    suspend fun activarHuella(cedula: String) {
        userDao.setHuellaActiva(cedula, true)
    }

    suspend fun desactivarHuella(cedula: String) {
        userDao.setHuellaActiva(cedula, false)
    }
}
