package com.example.saludcontigo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.saludcontigo.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ingresar -> Home
        binding.btnIngresar.setOnClickListener {
            val cedula = binding.txtCedula.text?.toString().orEmpty().trim()
            val password = binding.txtPassword.text?.toString().orEmpty()

            // Ambos campos son obligatorios
            if (cedula.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Ingresa tu cédula y tu contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Guarda la cédula ingresada; el nombre se toma del registro previo
            Sesion.guardarCedula(this, cedula)
            startActivity(Intent(this, HomeActivity::class.java))
        }

        // Ir a la pantalla de registro
        binding.btnIrRegistro.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }

        // Ingreso biométrico (pendiente de implementar)
        binding.btnHuella.setOnClickListener {
            Toast.makeText(this, "Pronto podrás ingresar con tu huella", Toast.LENGTH_SHORT).show()
        }
    }
}
