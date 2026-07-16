package com.example.saludcontigo.ui.booking

import androidx.lifecycle.ViewModel
import java.util.Calendar

/**
 * Estado compartido del flujo Especialidad -> Fecha/Hora -> Confirmacion -> Exito.
 * Se mantiene sencillo (vars simples) porque cada pantalla lo lee/escribe
 * de forma sincrona al interactuar, no necesita observadores reactivos.
 */
class BookingViewModel : ViewModel() {

    var especialidad: String = Especialidades.lista.first().nombre
    var doctor: String = Especialidades.lista.first().doctor

    var fechaMillis: Long = Calendar.getInstance().timeInMillis
    var fechaTexto: String = ""
    var hora: String = "9:00 AM"

    val modalidad: String = "Presencial · Consultorio 204"
    val duracionMin: Int = 30
}
