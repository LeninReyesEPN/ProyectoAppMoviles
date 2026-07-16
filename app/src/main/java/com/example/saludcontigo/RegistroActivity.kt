package com.example.saludcontigo

import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.saludcontigo.data.repository.UserRepository
import com.example.saludcontigo.databinding.ActivityRegistroBinding
import com.example.saludcontigo.util.PasswordUtil
import kotlinx.coroutines.launch

class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding
    private lateinit var userRepository: UserRepository

    private var passwordVisible = false
    private var passwordConfirmarVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRepository = UserRepository(this)

        binding.btnTogglePassword.setOnClickListener {
            passwordVisible = alternarVisibilidadPassword(binding.txtPassword, binding.btnTogglePassword, passwordVisible)
        }
        binding.btnTogglePasswordConfirmar.setOnClickListener {
            passwordConfirmarVisible = alternarVisibilidadPassword(
                binding.txtPasswordConfirmar,
                binding.btnTogglePasswordConfirmar,
                passwordConfirmarVisible
            )
        }

        binding.btnRegistrarme.setOnClickListener {
            val nombre = binding.txtNombre.text?.toString().orEmpty().trim()
            val cedula = binding.txtCedula.text?.toString().orEmpty().trim()
            val edad = binding.txtEdad.text?.toString()?.trim()?.toIntOrNull()
            val password = binding.txtPassword.text?.toString().orEmpty()
            val passwordConfirmar = binding.txtPasswordConfirmar.text?.toString().orEmpty()

            if (nombre.isBlank()) {
                Toast.makeText(this, getString(R.string.registro_nombre_vacio), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (cedula.isBlank()) {
                Toast.makeText(this, getString(R.string.registro_cedula_vacia), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.isBlank()) {
                Toast.makeText(this, getString(R.string.registro_password_vacia), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.length < 4) {
                Toast.makeText(this, getString(R.string.registro_password_corta), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password != passwordConfirmar) {
                Toast.makeText(this, getString(R.string.registro_password_no_coincide), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val salt = PasswordUtil.generarSalt()
            val hash = PasswordUtil.hash(password, salt)

            lifecycleScope.launch {
                userRepository.registrar(cedula, nombre, edad, hash, salt)
                Sesion.iniciarSesion(this@RegistroActivity, cedula)
                Toast.makeText(this@RegistroActivity, getString(R.string.registro_exito), Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        binding.btnYaTengoCuenta.setOnClickListener {
            finish()
        }
    }

    private fun alternarVisibilidadPassword(campo: EditText, boton: ImageButton, visibleActual: Boolean): Boolean {
        val nuevoVisible = !visibleActual
        campo.inputType = if (nuevoVisible) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        campo.setSelection(campo.text?.length ?: 0)
        boton.setImageResource(if (nuevoVisible) R.drawable.ic_eye else R.drawable.ic_eye_off)
        boton.contentDescription = getString(
            if (nuevoVisible) R.string.desc_ocultar_password else R.string.desc_mostrar_password
        )
        return nuevoVisible
    }
}
