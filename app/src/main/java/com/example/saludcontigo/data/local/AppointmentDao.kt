package com.example.saludcontigo.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {

    @Insert
    suspend fun insert(appointment: AppointmentEntity): Long

    @Query("SELECT * FROM citas WHERE userCedula = :cedula ORDER BY fechaMillis ASC")
    fun getAllByUser(cedula: String): Flow<List<AppointmentEntity>>
}
