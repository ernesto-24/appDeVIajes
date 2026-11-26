package com.example.appdeviajes.ui.navegacion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.appdeviajes.ViajaloVosApp
import com.example.appdeviajes.ui.ViajaloVosViewModelFactory
import com.example.appdeviajes.ui.destinos.DestinosScreen
import com.example.appdeviajes.ui.destinos.DestinosViewModel
import com.example.appdeviajes.ui.inicio.InicioScreen
import com.example.appdeviajes.ui.inicio.InicioViewModel
import com.example.appdeviajes.ui.resumen.ResumenViajeScreen
import com.example.appdeviajes.ui.resumen.ResumenViajeViewModel

object Rutas {
    const val INICIO = "inicio"
    const val DESTINOS = "destinos"
    const val ARG_CODIGO_PAIS = "codigoPais"
    const val RESUMEN = "resumen/{$ARG_CODIGO_PAIS}"

    fun resumen(codigoPais: String): String = "resumen/$codigoPais"
}

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()
    val aplicacion = LocalContext.current.applicationContext as ViajaloVosApp
    val factory = remember(aplicacion) { ViajaloVosViewModelFactory(aplicacion.repositorio) }

    NavHost(navController = navController, startDestination = Rutas.INICIO) {
        composable(Rutas.INICIO) {
            val viewModel: InicioViewModel = viewModel(factory = factory)
            InicioScreen(
                viewModel = viewModel,
                onElegirDestino = { navController.navigate(Rutas.DESTINOS) { launchSingleTop = true } }
            )
        }
        composable(Rutas.DESTINOS) {
            val viewModel: DestinosViewModel = viewModel(factory = factory)
            DestinosScreen(
                viewModel = viewModel,
                onDestinoElegido = { codigo -> navController.navigate(Rutas.resumen(codigo)) { launchSingleTop = true } },
                onVolver = { navController.navigateUp() }
            )
        }
        composable(
            route = Rutas.RESUMEN,
            arguments = listOf(navArgument(Rutas.ARG_CODIGO_PAIS) { type = NavType.StringType })
        ) {
            val viewModel: ResumenViajeViewModel = viewModel(factory = factory)
            ResumenViajeScreen(
                viewModel = viewModel,
                onVolver = { navController.navigateUp() }
            )
        }
    }
}
