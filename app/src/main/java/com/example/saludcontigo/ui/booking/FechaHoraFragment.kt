package com.example.saludcontigo.ui.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.saludcontigo.R
import com.example.saludcontigo.databinding.FragmentFechaHoraBinding
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FechaHoraFragment : Fragment() {

    private var _binding: FragmentFechaHoraBinding? = null
    private val binding get() = _binding!!

    private val bookingViewModel: BookingViewModel by navGraphViewModels(R.id.nav_graph)

    private val mesMostrado: Calendar = Calendar.getInstance()
    private val diaSeleccionado: Calendar = Calendar.getInstance()

    private val horarios = listOf(
        "7:00 AM", "8:00 AM", "9:00 AM", "10:00 AM",
        "11:00 AM", "2:00 PM", "3:00 PM", "4:00 PM"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFechaHoraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        renderCalendario()
        renderHorarios()

        binding.btnMesAnterior.setOnClickListener {
            mesMostrado.add(Calendar.MONTH, -1)
            renderCalendario()
        }
        binding.btnMesSiguiente.setOnClickListener {
            mesMostrado.add(Calendar.MONTH, 1)
            renderCalendario()
        }

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnContinuar.setOnClickListener {
            guardarSeleccionYContinuar()
        }
    }

    private fun renderCalendario() {
        val formatoMes = SimpleDateFormat("MMMM yyyy", Locale("es", "ES"))
        binding.tvMesAnio.text = formatoMes.format(mesMostrado.time)
            .replaceFirstChar { it.uppercase() }

        binding.gridDias.removeAllViews()

        val primerDiaMes = mesMostrado.clone() as Calendar
        primerDiaMes.set(Calendar.DAY_OF_MONTH, 1)
        val offset = (primerDiaMes.get(Calendar.DAY_OF_WEEK) + 5) % 7
        val diasEnMes = primerDiaMes.getActualMaximum(Calendar.DAY_OF_MONTH)

        val celdas = mutableListOf<Int?>()
        repeat(offset) { celdas.add(null) }
        for (dia in 1..diasEnMes) celdas.add(dia)

        celdas.chunked(7).forEach { fila ->
            val filaLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            fila.forEach { dia ->
                val celda = TextView(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(0, dpAPx(40)).apply { weight = 1f }
                    gravity = android.view.Gravity.CENTER
                    textSize = 16f
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
                    if (dia != null) {
                        text = dia.toString()
                        val esSeleccionado = dia == diaSeleccionado.get(Calendar.DAY_OF_MONTH) &&
                            mesMostrado.get(Calendar.MONTH) == diaSeleccionado.get(Calendar.MONTH) &&
                            mesMostrado.get(Calendar.YEAR) == diaSeleccionado.get(Calendar.YEAR)
                        if (esSeleccionado) {
                            setBackgroundResource(R.drawable.bg_circle_primary)
                        }
                        setOnClickListener {
                            diaSeleccionado.set(
                                mesMostrado.get(Calendar.YEAR),
                                mesMostrado.get(Calendar.MONTH),
                                dia
                            )
                            renderCalendario()
                        }
                    }
                }
                filaLayout.addView(celda)
            }
            binding.gridDias.addView(filaLayout)
        }
    }

    private fun renderHorarios() {
        binding.chipGroupHorarios.removeAllViews()
        horarios.forEach { horario ->
            val chip = layoutInflater.inflate(
                R.layout.item_chip_horario,
                binding.chipGroupHorarios,
                false
            ) as Chip
            chip.text = horario
            chip.isChecked = horario == bookingViewModel.hora
            binding.chipGroupHorarios.addView(chip)
        }
        if (binding.chipGroupHorarios.checkedChipId == View.NO_ID && binding.chipGroupHorarios.childCount > 0) {
            (binding.chipGroupHorarios.getChildAt(0) as Chip).isChecked = true
        }
    }

    private fun guardarSeleccionYContinuar() {
        val formatoFecha = SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", Locale("es", "ES"))
        bookingViewModel.fechaMillis = diaSeleccionado.timeInMillis
        bookingViewModel.fechaTexto = formatoFecha.format(diaSeleccionado.time)
            .replaceFirstChar { it.uppercase() }

        val chipSeleccionado = binding.chipGroupHorarios.checkedChipId
        val chip = binding.chipGroupHorarios.findViewById<Chip>(chipSeleccionado)
        if (chip != null) {
            bookingViewModel.hora = chip.text.toString()
        }

        findNavController().navigate(R.id.confirmacion)
    }

    private fun dpAPx(dp: Int): Int =
        (dp * resources.displayMetrics.density).toInt()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
