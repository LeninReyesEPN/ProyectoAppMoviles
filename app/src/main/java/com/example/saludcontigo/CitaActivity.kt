package com.example.saludcontigo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.saludcontigo.databinding.ActivityCitaBinding

class CitaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCitaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Regresar al Home
        binding.btnRegresar.setOnClickListener { finish() }
        binding.btnVolver.setOnClickListener { finish() }

        // Selección de especialidad (pendiente de implementar)
        val elegir: (String) -> Unit = { nombre ->
            Toast.makeText(this, "Elegiste $nombre", Toast.LENGTH_SHORT).show()
        }
        binding.btnMedicinaGeneral.setOnClickListener { elegir("Medicina General") }
        binding.btnCardiologia.setOnClickListener { elegir("Cardiología") }
        binding.btnNeumologia.setOnClickListener { elegir("Neumología") }
        binding.btnNeurologia.setOnClickListener { elegir("Neurología") }
    }
}
