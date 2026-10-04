package com.example.busquedaavion.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.busquedaavion.data.AirportDao
import com.example.busquedaavion.data.FavoriteDao
import com.example.busquedaavion.data.UserPreferencesRepository

@Composable
fun FlightSearchApp(
    airportDao: AirportDao,
    favoriteDao: FavoriteDao,
    userPreferencesRepository: UserPreferencesRepository
) {
    val viewModel: FlightSearchViewModel = viewModel(
        factory = FlightSearchViewModel.provideFactory(
            airportDao,
            favoriteDao,
            userPreferencesRepository
        )
    )

    FlightSearchScreen(viewModel = viewModel)
}
