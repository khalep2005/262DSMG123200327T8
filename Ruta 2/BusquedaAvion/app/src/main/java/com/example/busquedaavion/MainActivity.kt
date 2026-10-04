package com.example.busquedaavion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.busquedaavion.data.FlightSearchDatabase
import com.example.busquedaavion.data.UserPreferencesRepository
import com.example.busquedaavion.data.dataStore
import com.example.busquedaavion.ui.FlightSearchApp
import com.example.busquedaavion.ui.theme.BusquedaAvionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BusquedaAvionTheme {
                val application = application as FlightSearchApplication

                FlightSearchApp(
                    airportDao = application.database.airportDao(),
                    favoriteDao = application.database.favoriteDao(),
                    userPreferencesRepository = application.userPreferencesRepository
                )
            }
        }
    }
}
