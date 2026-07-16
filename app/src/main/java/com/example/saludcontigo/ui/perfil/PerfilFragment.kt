package com.example.saludcontigo.ui.perfil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.saludcontigo.R
import com.example.saludcontigo.Sesion
import com.example.saludcontigo.data.local.EstadoCita
import com.example.saludcontigo.data.repository.AppointmentRepository
import com.example.saludcontigo.data.repository.UserRepository
import com.example.saludcontigo.databinding.FragmentPerfilBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    private lateinit var userRepository: UserRepository
    private lateinit var appointmentRepository: AppointmentRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userRepository = UserRepository(requireContext())
        appointmentRepository = AppointmentRepository(requireContext())
        val cedula = Sesion.obtenerCedula(requireContext())

        configurarOpciones()

        viewLifecycleOwner.lifecycleScope.launch {
            val usuario = userRepository.buscarPorCedula(cedula)
            if (usuario != null) {
                binding.tvNombre.text = usuario.nombre
                binding.tvCedulaEdad.text = buildString {
                    append(getString(R.string.perfil_cedula, cedula))
                    usuario.edad?.let { append(" · ").append(getString(R.string.perfil_anios, it)) }
                }
                if (usuario.eps.isNullOrBlank()) {
                    binding.tvEpsBadge.visibility = View.GONE
                } else {
                    binding.tvEpsBadge.visibility = View.VISIBLE
                    binding.tvEpsBadge.text = usuario.eps
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                appointmentRepository.obtenerCitasDe(cedula).collect { citas ->
                    binding.tvStatTotal.text = citas.size.toString()

                    val anioActual = Calendar.getInstance().get(Calendar.YEAR)
                    val esteAnio = citas.count {
                        Calendar.getInstance().apply { timeInMillis = it.fechaMillis }
                            .get(Calendar.YEAR) == anioActual
                    }
                    binding.tvStatAnio.text = esteAnio.toString()

                    val proxima = citas.filter { it.estado == EstadoCita.PROXIMA.name }
                        .minByOrNull { it.fechaMillis }
                    binding.tvStatProxima.text = if (proxima != null) {
                        SimpleDateFormat("MMM d", Locale("es", "ES"))
                            .format(proxima.fechaMillis)
                            .replaceFirstChar { it.uppercase() }
                    } else {
                        getString(R.string.perfil_sin_proxima)
                    }
                }
            }
        }
    }

    private fun configurarOpciones() {
        binding.rowNotificaciones.tvIcono.text = "🔔"
        binding.rowNotificaciones.tvTitulo.text = getString(R.string.opt_notificaciones_titulo)
        binding.rowNotificaciones.tvSubtitulo.text = getString(R.string.opt_notificaciones_subtitulo)

        binding.rowHistorial.tvIcono.text = "📄"
        binding.rowHistorial.tvTitulo.text = getString(R.string.opt_historial_titulo)
        binding.rowHistorial.tvSubtitulo.text = getString(R.string.opt_historial_subtitulo)

        binding.rowCuidador.tvIcono.text = "👥"
        binding.rowCuidador.tvTitulo.text = getString(R.string.opt_cuidador_titulo)
        binding.rowCuidador.tvSubtitulo.text = getString(R.string.opt_cuidador_subtitulo)

        val avisarProximamente = View.OnClickListener {
            Toast.makeText(requireContext(), getString(R.string.opt_proximamente), Toast.LENGTH_SHORT).show()
        }
        binding.rowNotificaciones.root.setOnClickListener(avisarProximamente)
        binding.rowHistorial.root.setOnClickListener(avisarProximamente)
        binding.rowCuidador.root.setOnClickListener(avisarProximamente)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
