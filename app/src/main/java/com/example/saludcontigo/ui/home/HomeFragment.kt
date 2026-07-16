package com.example.saludcontigo.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.saludcontigo.R
import com.example.saludcontigo.Sesion
import com.example.saludcontigo.data.local.EstadoCita
import com.example.saludcontigo.data.repository.AppointmentRepository
import com.example.saludcontigo.data.repository.UserRepository
import com.example.saludcontigo.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var userRepository: UserRepository
    private lateinit var appointmentRepository: AppointmentRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userRepository = UserRepository(requireContext())
        appointmentRepository = AppointmentRepository(requireContext())
        val cedula = Sesion.obtenerCedula(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            val usuario = userRepository.buscarPorCedula(cedula)
            val primerNombre = usuario?.nombre?.trim()?.split(" ")?.firstOrNull()
            binding.tvSaludo.text = if (primerNombre.isNullOrBlank()) {
                getString(R.string.home_saludo_generico)
            } else {
                getString(R.string.home_saludo, primerNombre)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                appointmentRepository.obtenerCitasDe(cedula).collect { citas ->
                    val proxima = citas.firstOrNull { it.estado == EstadoCita.PROXIMA.name }
                    binding.tvProximaCita.text = if (proxima != null) {
                        "${proxima.especialidad} · ${proxima.hora}"
                    } else {
                        getString(R.string.home_sin_proxima_cita)
                    }

                    val anioActual = Calendar.getInstance().get(Calendar.YEAR)
                    val esteAnio = citas.count {
                        Calendar.getInstance().apply { timeInMillis = it.fechaMillis }
                            .get(Calendar.YEAR) == anioActual
                    }
                    binding.tvStatTotal.text = citas.size.toString()
                    binding.tvStatAnio.text = esteAnio.toString()
                }
            }
        }

        binding.cardAgendar.setOnClickListener {
            findNavController().navigate(R.id.especialidad)
        }
        binding.cardMisCitas.setOnClickListener {
            findNavController().navigate(R.id.misCitas)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
