package com.example.saludcontigo.ui.miscitas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saludcontigo.R
import com.example.saludcontigo.Sesion
import com.example.saludcontigo.data.local.AppointmentEntity
import com.example.saludcontigo.data.local.EstadoCita
import com.example.saludcontigo.data.repository.AppointmentRepository
import com.example.saludcontigo.databinding.FragmentMisCitasBinding
import kotlinx.coroutines.launch

class MisCitasFragment : Fragment() {

    private var _binding: FragmentMisCitasBinding? = null
    private val binding get() = _binding!!

    private lateinit var appointmentRepository: AppointmentRepository
    private val adapter = AppointmentAdapter()

    private var todasLasCitas: List<AppointmentEntity> = emptyList()
    private var pestanaProximas = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMisCitasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appointmentRepository = AppointmentRepository(requireContext())
        binding.rvCitas.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCitas.adapter = adapter

        binding.tabProximas.setOnClickListener {
            pestanaProximas = true
            actualizarEstiloTabs()
            aplicarFiltro()
        }
        binding.tabPasadas.setOnClickListener {
            pestanaProximas = false
            actualizarEstiloTabs()
            aplicarFiltro()
        }
        actualizarEstiloTabs()

        val cedula = Sesion.obtenerCedula(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                appointmentRepository.obtenerCitasDe(cedula).collect { citas ->
                    todasLasCitas = citas
                    aplicarFiltro()
                }
            }
        }
    }

    private fun aplicarFiltro() {
        val filtradas = if (pestanaProximas) {
            todasLasCitas.filter { it.estado != EstadoCita.PASADA.name }
        } else {
            todasLasCitas.filter { it.estado == EstadoCita.PASADA.name }
        }

        binding.tvContador.text = getString(R.string.mis_citas_contador, filtradas.size)
        adapter.actualizar(filtradas)

        if (filtradas.isEmpty()) {
            binding.rvCitas.visibility = View.GONE
            binding.tvVacio.visibility = View.VISIBLE
            binding.tvVacio.text = getString(
                if (pestanaProximas) R.string.mis_citas_vacio_proximas else R.string.mis_citas_vacio_pasadas
            )
        } else {
            binding.rvCitas.visibility = View.VISIBLE
            binding.tvVacio.visibility = View.GONE
        }
    }

    private fun actualizarEstiloTabs() {
        val blanco = ContextCompat.getColor(requireContext(), R.color.white)
        val secundario = ContextCompat.getColor(requireContext(), R.color.text_secondary)

        binding.tabProximas.setBackgroundResource(if (pestanaProximas) R.drawable.bg_pill else 0)
        binding.tabProximas.setTextColor(if (pestanaProximas) blanco else secundario)

        binding.tabPasadas.setBackgroundResource(if (!pestanaProximas) R.drawable.bg_pill else 0)
        binding.tabPasadas.setTextColor(if (!pestanaProximas) blanco else secundario)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
