package com.example.saludcontigo.ui.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.saludcontigo.R
import com.example.saludcontigo.databinding.FragmentEspecialidadBinding

class EspecialidadFragment : Fragment() {

    private var _binding: FragmentEspecialidadBinding? = null
    private val binding get() = _binding!!

    private val bookingViewModel: BookingViewModel by navGraphViewModels(R.id.nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEspecialidadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tarjetas = listOf(
            binding.cardCardiologia to Especialidades.lista[0],
            binding.cardMedicinaGeneral to Especialidades.lista[1],
            binding.cardNeumologia to Especialidades.lista[2],
            binding.cardNeurologia to Especialidades.lista[3]
        )

        tarjetas.forEach { (tarjeta, especialidad) ->
            tarjeta.setOnClickListener {
                seleccionar(especialidad, tarjetas)
            }
        }

        binding.tvDisponibilidad.text = getString(
            R.string.disponibilidad_detalle,
            Especialidades.lista[1].nombre
        )

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnContinuar.setOnClickListener { findNavController().navigate(R.id.fechaHora) }
    }

    private fun seleccionar(
        especialidad: Especialidad,
        tarjetas: List<Pair<View, Especialidad>>
    ) {
        bookingViewModel.especialidad = especialidad.nombre
        bookingViewModel.doctor = especialidad.doctor

        tarjetas.forEach { (tarjeta, esp) ->
            tarjeta.setBackgroundResource(
                if (esp.nombre == especialidad.nombre) R.drawable.bg_card_selected else R.drawable.bg_card
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
