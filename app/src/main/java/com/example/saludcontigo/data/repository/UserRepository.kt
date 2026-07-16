package com.example.saludcontigo.data.repository

import android.content.Context
import com.example.saludcontigo.data.local.AppDatabase
import com.example.saludcontigo.data.local.UserEntity

class UserRepository(context: Context) {

    private val userDao = AppDatabase.obtener(context).userDao()

    suspend fun registrar(cedula: String, nombre: String, edad: Int?, eps: String?) {
        userDao.insert(UserEntity(cedula = cedula, nombre = nombre, edad = edad, eps = eps))
    }

    suspend fun buscarPorCedula(cedula: String): UserEntity? = userDao.getByCedula(cedula)
}
