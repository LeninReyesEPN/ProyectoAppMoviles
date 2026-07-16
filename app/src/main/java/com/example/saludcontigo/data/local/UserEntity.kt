package com.example.saludcontigo.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UserEntity(
    @PrimaryKey val cedula: String,
    val nombre: String,
    val edad: Int?,
    val passwordHash: String,
    val passwordSalt: String,
    val huellaActiva: Boolean = false
)
