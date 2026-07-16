package com.example.saludcontigo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.saludcontigo.data.repository.UserRepository
import com.example.saludcontigo.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRepository = UserRepository(this)

        // Ingresar con cedula o telefono -> busca al usuario en Room
        binding.btnIngresar.setOnClickListener {
            val cedula = binding.txtCedula.text?.toString().orEmpty().trim()

            if (cedula.isBlank()) {
                Toast.makeText(this, getString(R.string.login_cedula_vacia), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val usuario = userRepository.buscarPorCedula(cedula)
                if (usuario == null) {
                    Toast.makeText(this@LoginActivity, getString(R.string.login_usuario_no_encontrado), Toast.LENGTH_LONG).show()
                } else {
                    Sesion.iniciarSesion(this@LoginActivity, cedula)
                    irAHome()
                }
            }
        }

        binding.btnHuella.setOnClickListener { intentarIngresoBiometrico() }
        binding.btnFaceId.setOnClickListener { intentarIngresoBiometrico() }

        binding.btnIrRegistro.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }
    }

    private fun intentarIngresoBiometrico() {
        val cedulaActiva = Sesion.obtenerCedula(this)
        if (cedulaActiva.isBlank()) {
            Toast.makeText(this, getString(R.string.biometria_sin_sesion), Toast.LENGTH_LONG).show()
            return
        }

        val gestor = BiometricManager.from(this)
        val puedeAutenticar = gestor.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_WEAK or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )
        if (puedeAutenticar != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(this, getString(R.string.biometria_no_disponible), Toast.LENGTH_LONG).show()
            return
        }

        val prompt = BiometricPrompt(
            this,
            ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    lifecycleScope.launch {
                        val usuario = userRepository.buscarPorCedula(cedulaActiva)
                        if (usuario != null) {
                            irAHome()
                        } else {
                            Toast.makeText(this@LoginActivity, getString(R.string.login_usuario_no_encontrado), Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    Toast.makeText(this@LoginActivity, errString, Toast.LENGTH_SHORT).show()
                }
            }
        )

        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometria_titulo))
            .setSubtitle(getString(R.string.biometria_subtitulo))
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_WEAK or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()

        prompt.authenticate(info)
    }

    private fun irAHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
