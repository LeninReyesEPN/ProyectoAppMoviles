package com.example.saludcontigo.ui.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.saludcontigo.R
import com.example.saludcontigo.databinding.FragmentExitoBinding

class ExitoFragment : Fragment() {

    private var _binding: FragmentExitoBinding? = null
    private val binding get() = _binding!!

    private val bookingViewModel: BookingViewModel by navGraphViewModels(R.id.nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExitoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvMensaje.text = getString(R.string.exito_mensaje, bookingViewModel.doctor)
        binding.tvReciboEspecialidad.text = bookingViewModel.especialidad
        binding.tvReciboFecha.text = bookingViewModel.fechaTexto
        binding.tvReciboHora.text = bookingViewModel.hora

        binding.ivCheck.scaleX = 0f
        binding.ivCheck.scaleY = 0f
        binding.ivCheck.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(400)
            .setInterpolator(OvershootInterpolator())
            .start()

        binding.btnVolver.setOnClickListener {
            val opciones = NavOptions.Builder()
                .setPopUpTo(R.id.home, false)
                .build()
            findNavController().navigate(R.id.home, null, opciones)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
