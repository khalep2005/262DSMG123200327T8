package com.example.busquedaavion.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.busquedaavion.data.AirportDao
import com.example.busquedaavion.data.FavoriteDao
import com.example.busquedaavion.data.UserPreferencesRepository
import com.example.busquedaavion.model.Airport
import com.example.busquedaavion.model.Favorite
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FlightSearchViewModel(
    private val airportDao: AirportDao,
    private val favoriteDao: FavoriteDao,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedAirport = MutableStateFlow<Airport?>(null)
    val selectedAirport: StateFlow<Airport?> = _selectedAirport.asStateFlow()

    init {
        viewModelScope.launch {
            _searchQuery.value = userPreferencesRepository.searchQuery.first()
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        _selectedAirport.value = null // Clear selection when typing
        viewModelScope.launch {
            userPreferencesRepository.saveSearchQuery(query)
        }
    }

    fun onAirportSelected(airport: Airport) {
        _selectedAirport.value = airport
        _searchQuery.value = airport.iataCode
        viewModelScope.launch {
            userPreferencesRepository.saveSearchQuery(airport.iataCode)
        }
    }

    // Airport suggestions based on query
    val airportSuggestions: StateFlow<List<Airport>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isNotBlank() && _selectedAirport.value == null) {
                airportDao.searchAirports(query)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // Flights generated from selected airport
    val availableFlights: StateFlow<List<Airport>> = _selectedAirport
        .flatMapLatest { airport ->
            if (airport != null) {
                airportDao.getAllAirportsExcept(airport.iataCode)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val favorites: StateFlow<List<Favorite>> = favoriteDao.getAllFavorites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun toggleFavorite(departureCode: String, destinationCode: String, isFavorite: Boolean) {
        viewModelScope.launch {
            if (isFavorite) {
                favoriteDao.deleteFavoriteByCodes(departureCode, destinationCode)
            } else {
                favoriteDao.insertFavorite(
                    Favorite(
                        departureCode = departureCode,
                        destinationCode = destinationCode
                    )
                )
            }
        }
    }

    companion object {
        fun provideFactory(
            airportDao: AirportDao,
            favoriteDao: FavoriteDao,
            userPreferencesRepository: UserPreferencesRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FlightSearchViewModel(airportDao, favoriteDao, userPreferencesRepository) as T
            }
        }
    }
}
