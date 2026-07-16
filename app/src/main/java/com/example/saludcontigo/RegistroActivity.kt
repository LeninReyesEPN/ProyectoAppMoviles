package com.example.saludcontigo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.saludcontigo.data.repository.UserRepository
import com.example.saludcontigo.databinding.ActivityRegistroBinding
import kotlinx.coroutines.launch

class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRepository = UserRepository(this)

        binding.btnRegistrarme.setOnClickListener {
            val nombre = binding.txtNombre.text?.toString().orEmpty().trim()
            val cedula = binding.txtCedula.text?.toString().orEmpty().trim()
            val edad = binding.txtEdad.text?.toString()?.trim()?.toIntOrNull()
            val eps = binding.txtEps.text?.toString().orEmpty().trim().ifBlank { null }

            if (nombre.isBlank()) {
                Toast.makeText(this, getString(R.string.registro_nombre_vacio), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (cedula.isBlank()) {
                Toast.makeText(this, getString(R.string.registro_cedula_vacia), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                userRepository.registrar(cedula, nombre, edad, eps)
                Sesion.iniciarSesion(this@RegistroActivity, cedula)
                Toast.makeText(this@RegistroActivity, getString(R.string.registro_exito), Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        binding.btnYaTengoCuenta.setOnClickListener {
            finish()
        }
    }
}
