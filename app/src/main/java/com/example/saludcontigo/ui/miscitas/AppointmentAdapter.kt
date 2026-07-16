package com.example.saludcontigo.ui.miscitas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.saludcontigo.R
import com.example.saludcontigo.data.local.AppointmentEntity
import com.example.saludcontigo.data.local.EstadoCita
import com.example.saludcontigo.databinding.ItemCitaBinding
import com.example.saludcontigo.ui.booking.Especialidades

class AppointmentAdapter : RecyclerView.Adapter<AppointmentAdapter.ViewHolder>() {

    private var citas: List<AppointmentEntity> = emptyList()

    fun actualizar(nuevas: List<AppointmentEntity>) {
        citas = nuevas
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val binding = ItemCitaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(citas[position])
    }

    override fun getItemCount(): Int = citas.size

    class ViewHolder(private val binding: ItemCitaBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cita: AppointmentEntity) {
            val context = binding.root.context
            val especialidad = Especialidades.porNombre(cita.especialidad)

            binding.tvIconoDoctor.text = especialidad.emoji
            binding.tvDoctorNombre.text = cita.doctorNombre
            binding.tvEspecialidad.text = cita.especialidad
            binding.tvFecha.text = cita.fechaTexto
            binding.tvHora.text = cita.hora

            val estado = runCatching { EstadoCita.valueOf(cita.estado) }.getOrDefault(EstadoCita.AGENDADA)
            val (textoResId, colorResId) = when (estado) {
                EstadoCita.PROXIMA -> R.string.estado_proxima to R.color.estado_proxima
                EstadoCita.AGENDADA -> R.string.estado_agendada to R.color.estado_agendada
                EstadoCita.PASADA -> R.string.estado_pasada to R.color.estado_pasada
            }
            binding.tvEstado.text = context.getString(textoResId)
            binding.tvEstado.background?.let { fondo ->
                DrawableCompat.setTint(fondo.mutate(), ContextCompat.getColor(context, colorResId))
            }
        }
    }
}
