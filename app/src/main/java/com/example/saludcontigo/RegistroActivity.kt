package com.example.saludcontigo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.saludcontigo.databinding.ActivityRegistroBinding

class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Registrarme -> Login (para que la persona inicie sesión)
        binding.btnRegistrarme.setOnClickListener {
            val nombre = binding.txtNombre.text?.toString().orEmpty().trim()
            val cedula = binding.txtCedula.text?.toString().orEmpty().trim()

            if (nombre.isBlank()) {
                Toast.makeText(this, "Escribe tu nombre", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Guarda los datos para usarlos al iniciar sesión y en las demás pantallas
            Sesion.guardar(this, nombre, cedula)
            Toast.makeText(this, "Cuenta creada. Ahora inicia sesión", Toast.LENGTH_SHORT).show()

            // Regresa al Login (que está debajo, pues desde ahí se abrió el registro)
            finish()
        }

        // Ya tengo cuenta -> regresar al Login
        binding.btnYaTengoCuenta.setOnClickListener {
            finish()
        }
    }
}
