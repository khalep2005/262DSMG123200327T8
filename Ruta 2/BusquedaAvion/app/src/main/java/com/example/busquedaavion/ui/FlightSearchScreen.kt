package com.example.busquedaavion.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.busquedaavion.model.Airport
import com.example.busquedaavion.model.Favorite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchScreen(
    viewModel: FlightSearchViewModel
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedAirport by viewModel.selectedAirport.collectAsState()
    val airportSuggestions by viewModel.airportSuggestions.collectAsState()
    val availableFlights by viewModel.availableFlights.collectAsState()
    val favorites by viewModel.favorites.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Flight Search") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::updateSearchQuery,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search airports") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (searchQuery.isBlank()) {
                // Show favorites
                Text(
                    text = "Favorite Routes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyColumn {
                    items(favorites) { favorite ->
                        FavoriteCard(favorite = favorite, onFavoriteToggle = {
                            viewModel.toggleFavorite(favorite.departureCode, favorite.destinationCode, true)
                        })
                    }
                }
            } else if (selectedAirport != null) {
                // Show flights from selected airport
                Text(
                    text = "Flights from ${selectedAirport!!.iataCode}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyColumn {
                    items(availableFlights) { destination ->
                        val isFav = favorites.any {
                            it.departureCode == selectedAirport!!.iataCode && it.destinationCode == destination.iataCode
                        }
                        FlightCard(
                            departureCode = selectedAirport!!.iataCode,
                            departureName = selectedAirport!!.name,
                            destinationCode = destination.iataCode,
                            destinationName = destination.name,
                            isFavorite = isFav,
                            onFavoriteToggle = {
                                viewModel.toggleFavorite(selectedAirport!!.iataCode, destination.iataCode, isFav)
                            }
                        )
                    }
                }
            } else {
                // Show autocomplete suggestions
                LazyColumn {
                    items(airportSuggestions) { airport ->
                        AirportSuggestionItem(
                            airport = airport,
                            onClick = { viewModel.onAirportSelected(airport) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AirportSuggestionItem(airport: Airport, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = airport.iataCode,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(60.dp)
        )
        Text(text = airport.name)
    }
}

@Composable
fun FlightCard(
    departureCode: String,
    departureName: String,
    destinationCode: String,
    destinationName: String,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Depart", style = MaterialTheme.typography.labelSmall)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = departureCode, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = departureName, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Arrive", style = MaterialTheme.typography.labelSmall)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = destinationCode, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = destinationName, style = MaterialTheme.typography.bodySmall)
                }
            }
            IconButton(onClick = onFavoriteToggle) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Toggle Favorite",
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun FavoriteCard(favorite: Favorite, onFavoriteToggle: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Depart", style = MaterialTheme.typography.labelSmall)
                Text(text = favorite.departureCode, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Arrive", style = MaterialTheme.typography.labelSmall)
                Text(text = favorite.destinationCode, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = onFavoriteToggle) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Remove Favorite",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
