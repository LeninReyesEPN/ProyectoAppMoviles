package com.example.saludcontigo

import android.content.Intent
import android.os.Bundle
import android.text.InputType
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
    private var passwordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRepository = UserRepository(this)

        binding.btnTogglePassword.setOnClickListener { alternarVisibilidadPassword() }

        // Ingresar con cedula y contrasena -> valida contra el hash guardado en Room
        binding.btnIngresar.setOnClickListener {
            val cedula = binding.txtCedula.text?.toString().orEmpty().trim()
            val password = binding.txtPassword.text?.toString().orEmpty()

            if (cedula.isBlank()) {
                Toast.makeText(this, getString(R.string.login_cedula_vacia), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.isBlank()) {
                Toast.makeText(this, getString(R.string.login_password_vacia), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val usuario = userRepository.validarCredenciales(cedula, password)
                if (usuario == null) {
                    Toast.makeText(this@LoginActivity, getString(R.string.login_credenciales_invalidas), Toast.LENGTH_LONG).show()
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

    private fun alternarVisibilidadPassword() {
        passwordVisible = !passwordVisible
        binding.txtPassword.inputType = if (passwordVisible) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        binding.txtPassword.setSelection(binding.txtPassword.text?.length ?: 0)
        binding.btnTogglePassword.setImageResource(if (passwordVisible) R.drawable.ic_eye else R.drawable.ic_eye_off)
        binding.btnTogglePassword.contentDescription = getString(
            if (passwordVisible) R.string.desc_ocultar_password else R.string.desc_mostrar_password
        )
    }

    /**
     * La huella funciona para la cuenta que ya inicio sesion en este dispositivo
     * (Sesion.obtenerCedula) y que activo el interruptor en Mi Perfil. Se usa un
     * BiometricPrompt simple (sin CryptoObject/Keystore): Android no permite de todas
     * formas distinguir que dedo enrolado toco el sensor, y atar la huella a una clave
     * criptografica por cedula resulto poco confiable en varios equipos.
     */
    private fun intentarIngresoBiometrico() {
        val cedula = Sesion.obtenerCedula(this)
        if (cedula.isBlank()) {
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

        lifecycleScope.launch {
            val usuario = userRepository.buscarPorCedula(cedula)
            if (usuario == null) {
                Toast.makeText(this@LoginActivity, getString(R.string.login_usuario_no_encontrado), Toast.LENGTH_LONG).show()
                return@launch
            }
            if (!usuario.huellaActiva) {
                Toast.makeText(this@LoginActivity, getString(R.string.biometria_cuenta_sin_huella), Toast.LENGTH_LONG).show()
                return@launch
            }
            mostrarPromptBiometrico(cedula)
        }
    }

    private fun mostrarPromptBiometrico(cedula: String) {
        val prompt = BiometricPrompt(
            this,
            ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    Sesion.iniciarSesion(this@LoginActivity, cedula)
                    irAHome()
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
