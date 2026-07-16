package com.example.saludcontigo.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "citas")
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userCedula: String,
    val doctorNombre: String,
    val especialidad: String,
    val fechaMillis: Long,
    val fechaTexto: String,
    val hora: String,
    val modalidad: String,
    val duracionMin: Int,
    val estado: String
)
