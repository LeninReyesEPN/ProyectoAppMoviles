package com.example.saludcontigo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.saludcontigo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNav.setupWithNavController(navController)

        // Sincroniza manualmente el item activo del nav inferior y lo oculta en la pantalla de Exito,
        // ya que las pantallas del flujo de reserva (Especialidad/FechaHora/Confirmacion) no son
        // destinos del menu pero deben resaltar "Citas" (asi se ve en el mockup).
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.exito -> binding.bottomNav.visibility = android.view.View.GONE
                else -> {
                    binding.bottomNav.visibility = android.view.View.VISIBLE
                    val idResaltado = when (destination.id) {
                        R.id.especialidad, R.id.fechaHora, R.id.confirmacion, R.id.misCitas -> R.id.misCitas
                        R.id.perfil -> R.id.perfil
                        else -> R.id.home
                    }
                    binding.bottomNav.menu.findItem(idResaltado)?.isChecked = true
                }
            }
        }
    }
}
