package com.example.saludcontigo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.saludcontigo.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Saludo personalizado con el nombre de la persona
        val nombre = Sesion.obtenerPrimerNombre(this)
        binding.msgSaludo.text = if (nombre.isBlank()) {
            getString(R.string.home_saludo_generico)
        } else {
            getString(R.string.home_saludo, nombre)
        }

        // Agendar nueva cita -> pantalla de especialidades
        binding.btnAgendar.setOnClickListener {
            startActivity(Intent(this, CitaActivity::class.java))
        }

        // Ver mis citas (pendiente de implementar)
        binding.btnVerCitas.setOnClickListener {
            Toast.makeText(this, "Pronto podrás ver tus citas", Toast.LENGTH_SHORT).show()
        }

        // Cerrar sesión -> volver al Login
        binding.btnCerrarSesion.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            // Limpia las pantallas anteriores para que "atrás" no regrese al Home
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
