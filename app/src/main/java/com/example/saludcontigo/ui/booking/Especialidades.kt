package com.example.saludcontigo.ui.booking

import androidx.annotation.ColorRes
import com.example.saludcontigo.R

data class Especialidad(
    val nombre: String,
    val doctor: String,
    val emoji: String,
    @ColorRes val colorAcento: Int
)

object Especialidades {

    val lista = listOf(
        Especialidad(
            nombre = "Cardiología",
            doctor = "Dr. Roberto Vargas",
            emoji = "❤️",
            colorAcento = R.color.esp_cardiologia
        ),
        Especialidad(
            nombre = "Medicina General",
            doctor = "Dra. Lucía Morales",
            emoji = "🩺",
            colorAcento = R.color.esp_medicina_general
        ),
        Especialidad(
            nombre = "Neumología",
            doctor = "Dr. Andrés Castro",
            emoji = "🫁",
            colorAcento = R.color.esp_neumologia
        ),
        Especialidad(
            nombre = "Neurología",
            doctor = "Dra. Patricia León",
            emoji = "🧠",
            colorAcento = R.color.esp_neurologia
        )
    )

    fun porNombre(nombre: String): Especialidad =
        lista.firstOrNull { it.nombre == nombre } ?: Especialidad(nombre, "", "🩺", R.color.esp_default)
}
