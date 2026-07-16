package com.example.saludcontigo.ui.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.saludcontigo.R
import com.example.saludcontigo.Sesion
import com.example.saludcontigo.data.repository.AppointmentRepository
import com.example.saludcontigo.databinding.FragmentConfirmacionBinding
import kotlinx.coroutines.launch

class ConfirmacionFragment : Fragment() {

    private var _binding: FragmentConfirmacionBinding? = null
    private val binding get() = _binding!!

    private val bookingViewModel: BookingViewModel by navGraphViewModels(R.id.nav_graph)
    private lateinit var appointmentRepository: AppointmentRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfirmacionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appointmentRepository = AppointmentRepository(requireContext())

        val especialidad = Especialidades.porNombre(bookingViewModel.especialidad)

        binding.tvIconoDoctor.text = especialidad.emoji
        binding.tvDoctorNombre.text = bookingViewModel.doctor
        binding.tvEspecialidadTag.text = especialidad.nombre

        binding.filaFecha.tvIcono.text = "📅"
        binding.filaFecha.tvLabel.text = getString(R.string.label_fecha)
        binding.filaFecha.tvValor.text = bookingViewModel.fechaTexto

        binding.filaHora.tvIcono.text = "🕐"
        binding.filaHora.tvLabel.text = getString(R.string.label_hora)
        binding.filaHora.tvValor.text = bookingViewModel.hora

        binding.filaModalidad.tvIcono.text = "📍"
        binding.filaModalidad.tvLabel.text = getString(R.string.label_modalidad)
        binding.filaModalidad.tvValor.text = bookingViewModel.modalidad

        binding.filaDuracion.tvIcono.text = "⏱"
        binding.filaDuracion.tvLabel.text = getString(R.string.label_duracion)
        binding.filaDuracion.tvValor.text = getString(R.string.duracion_default)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnConfirmar.setOnClickListener { confirmarCita() }
    }

    private fun confirmarCita() {
        val cedula = Sesion.obtenerCedula(requireContext())
        lifecycleScope.launch {
            appointmentRepository.agendar(
                cedula = cedula,
                doctorNombre = bookingViewModel.doctor,
                especialidad = bookingViewModel.especialidad,
                fechaMillis = bookingViewModel.fechaMillis,
                fechaTexto = bookingViewModel.fechaTexto,
                hora = bookingViewModel.hora,
                modalidad = bookingViewModel.modalidad,
                duracionMin = bookingViewModel.duracionMin
            )
            findNavController().navigate(R.id.exito)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
