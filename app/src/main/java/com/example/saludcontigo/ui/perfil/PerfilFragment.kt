package com.example.saludcontigo.ui.perfil

import android.content.Intent
import android.os.Bundle
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.saludcontigo.LoginActivity
import com.example.saludcontigo.R
import com.example.saludcontigo.Sesion
import com.example.saludcontigo.data.local.EstadoCita
import com.example.saludcontigo.data.repository.AppointmentRepository
import com.example.saludcontigo.data.repository.UserRepository
import com.example.saludcontigo.databinding.FragmentPerfilBinding
import com.example.saludcontigo.util.BiometricKeyManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    private lateinit var userRepository: UserRepository
    private lateinit var appointmentRepository: AppointmentRepository
    private var cedulaActual: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userRepository = UserRepository(requireContext())
        appointmentRepository = AppointmentRepository(requireContext())
        cedulaActual = Sesion.obtenerCedula(requireContext())

        configurarOpciones()
        configurarMenu()

        viewLifecycleOwner.lifecycleScope.launch {
            val usuario = userRepository.buscarPorCedula(cedulaActual)
            if (usuario != null) {
                binding.tvNombre.text = usuario.nombre
                binding.tvCedulaEdad.text = buildString {
                    append(getString(R.string.perfil_cedula, cedulaActual))
                    usuario.edad?.let { append(" · ").append(getString(R.string.perfil_anios, it)) }
                }
                configurarSwitchHuella(usuario.huellaActiva)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                appointmentRepository.obtenerCitasDe(cedulaActual).collect { citas ->
                    binding.tvStatTotal.text = citas.size.toString()

                    val anioActual = Calendar.getInstance().get(Calendar.YEAR)
                    val esteAnio = citas.count {
                        Calendar.getInstance().apply { timeInMillis = it.fechaMillis }
                            .get(Calendar.YEAR) == anioActual
                    }
                    binding.tvStatAnio.text = esteAnio.toString()

                    val proxima = citas.filter { it.estado == EstadoCita.PROXIMA.name }
                        .minByOrNull { it.fechaMillis }
                    binding.tvStatProxima.text = if (proxima != null) {
                        SimpleDateFormat("MMM d", Locale("es", "ES"))
                            .format(proxima.fechaMillis)
                            .replaceFirstChar { it.uppercase() }
                    } else {
                        getString(R.string.perfil_sin_proxima)
                    }
                }
            }
        }
    }

    private fun configurarOpciones() {
        binding.rowHuella.tvIcono.text = "👆"
        binding.rowHuella.tvTitulo.text = getString(R.string.perfil_huella_titulo)

        binding.rowNotificaciones.tvIcono.text = "🔔"
        binding.rowNotificaciones.tvTitulo.text = getString(R.string.opt_notificaciones_titulo)
        binding.rowNotificaciones.tvSubtitulo.text = getString(R.string.opt_notificaciones_subtitulo)

        binding.rowHistorial.tvIcono.text = "📄"
        binding.rowHistorial.tvTitulo.text = getString(R.string.opt_historial_titulo)
        binding.rowHistorial.tvSubtitulo.text = getString(R.string.opt_historial_subtitulo)

        binding.rowCuidador.tvIcono.text = "👥"
        binding.rowCuidador.tvTitulo.text = getString(R.string.opt_cuidador_titulo)
        binding.rowCuidador.tvSubtitulo.text = getString(R.string.opt_cuidador_subtitulo)

        val avisarProximamente = View.OnClickListener {
            Toast.makeText(requireContext(), getString(R.string.opt_proximamente), Toast.LENGTH_SHORT).show()
        }
        binding.rowNotificaciones.root.setOnClickListener(avisarProximamente)
        binding.rowHistorial.root.setOnClickListener(avisarProximamente)
        binding.rowCuidador.root.setOnClickListener(avisarProximamente)
    }

    private fun configurarMenu() {
        binding.btnMenu.setOnClickListener { anchor ->
            val popup = PopupMenu(requireContext(), anchor)
            popup.menuInflater.inflate(R.menu.menu_perfil, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                if (item.itemId == R.id.itemCerrarSesion) {
                    confirmarCerrarSesion()
                    true
                } else {
                    false
                }
            }
            popup.show()
        }
    }

    private fun confirmarCerrarSesion() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialogo_cerrar_sesion_titulo)
            .setMessage(R.string.dialogo_cerrar_sesion_mensaje)
            .setPositiveButton(R.string.dialogo_cerrar_sesion_confirmar) { _, _ -> cerrarSesion() }
            .setNegativeButton(R.string.dialogo_cancelar, null)
            .show()
    }

    private fun cerrarSesion() {
        Sesion.cerrarSesion(requireContext())
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    /**
     * El interruptor de huella queda ligado a esta cedula (ver BiometricKeyManager):
     * activarlo crea/usa una clave de Keystore solo para esta cuenta.
     */
    private fun configurarSwitchHuella(huellaActiva: Boolean) {
        actualizarSubtituloHuella(huellaActiva)
        binding.rowHuella.switchToggle.setOnCheckedChangeListener(null)
        binding.rowHuella.switchToggle.isChecked = huellaActiva
        binding.rowHuella.switchToggle.setOnCheckedChangeListener { _, activar -> manejarCambioHuella(activar) }
        binding.rowHuella.root.setOnClickListener { binding.rowHuella.switchToggle.toggle() }
    }

    private fun actualizarSubtituloHuella(activa: Boolean) {
        binding.rowHuella.tvSubtitulo.text = getString(
            if (activa) R.string.perfil_huella_subtitulo_activa else R.string.perfil_huella_subtitulo_inactiva
        )
    }

    private fun revertirSwitchHuella(checked: Boolean) {
        binding.rowHuella.switchToggle.setOnCheckedChangeListener(null)
        binding.rowHuella.switchToggle.isChecked = checked
        binding.rowHuella.switchToggle.setOnCheckedChangeListener { _, activar -> manejarCambioHuella(activar) }
    }

    private fun manejarCambioHuella(activar: Boolean) {
        if (!activar) {
            viewLifecycleOwner.lifecycleScope.launch {
                userRepository.desactivarHuella(cedulaActual)
                BiometricKeyManager.eliminarClave(cedulaActual)
                actualizarSubtituloHuella(false)
                Toast.makeText(requireContext(), getString(R.string.perfil_huella_desactivada), Toast.LENGTH_SHORT).show()
            }
            return
        }

        val gestor = BiometricManager.from(requireContext())
        val puedeAutenticar = gestor.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
        if (puedeAutenticar != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(requireContext(), getString(R.string.biometria_no_disponible), Toast.LENGTH_LONG).show()
            revertirSwitchHuella(false)
            return
        }

        BiometricKeyManager.crearClave(cedulaActual)
        val cipher = try {
            BiometricKeyManager.obtenerCipher(cedulaActual)
        } catch (e: KeyPermanentlyInvalidatedException) {
            null
        }
        if (cipher == null) {
            Toast.makeText(requireContext(), getString(R.string.perfil_huella_error), Toast.LENGTH_SHORT).show()
            revertirSwitchHuella(false)
            return
        }

        val prompt = BiometricPrompt(
            this,
            ContextCompat.getMainExecutor(requireContext()),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        userRepository.activarHuella(cedulaActual)
                        actualizarSubtituloHuella(true)
                        Toast.makeText(requireContext(), getString(R.string.perfil_huella_activada), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    Toast.makeText(requireContext(), errString, Toast.LENGTH_SHORT).show()
                    revertirSwitchHuella(false)
                }
            }
        )

        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometria_titulo))
            .setSubtitle(getString(R.string.biometria_subtitulo))
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .setNegativeButtonText(getString(R.string.dialogo_cancelar))
            .build()

        prompt.authenticate(info, BiometricPrompt.CryptoObject(cipher))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
