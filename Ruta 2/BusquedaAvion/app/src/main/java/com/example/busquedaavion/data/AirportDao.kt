package com.example.busquedaavion.data

import androidx.room.Dao
import androidx.room.Query
import com.example.busquedaavion.model.Airport
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportDao {
    @Query("SELECT * FROM airport WHERE name LIKE '%' || :query || '%' OR iata_code LIKE '%' || :query || '%' ORDER BY passengers DESC")
    fun searchAirports(query: String): Flow<List<Airport>>

    @Query("SELECT * FROM airport WHERE iata_code != :iataCode ORDER BY passengers DESC")
    fun getAllAirportsExcept(iataCode: String): Flow<List<Airport>>
    
    @Query("SELECT * FROM airport WHERE iata_code = :iataCode")
    suspend fun getAirportByIataCode(iataCode: String): Airport?
}
