package com.example.saludcontigo.data.repository

import android.content.Context
import com.example.saludcontigo.data.local.AppDatabase
import com.example.saludcontigo.data.local.AppointmentEntity
import com.example.saludcontigo.data.local.EstadoCita
import kotlinx.coroutines.flow.Flow

class AppointmentRepository(context: Context) {

    private val appointmentDao = AppDatabase.obtener(context).appointmentDao()

    fun obtenerCitasDe(cedula: String): Flow<List<AppointmentEntity>> =
        appointmentDao.getAllByUser(cedula)

    suspend fun agendar(
        cedula: String,
        doctorNombre: String,
        especialidad: String,
        fechaMillis: Long,
        fechaTexto: String,
        hora: String,
        modalidad: String,
        duracionMin: Int
    ): Long = appointmentDao.insert(
        AppointmentEntity(
            userCedula = cedula,
            doctorNombre = doctorNombre,
            especialidad = especialidad,
            fechaMillis = fechaMillis,
            fechaTexto = fechaTexto,
            hora = hora,
            modalidad = modalidad,
            duracionMin = duracionMin,
            estado = EstadoCita.PROXIMA.name
        )
    )
}
