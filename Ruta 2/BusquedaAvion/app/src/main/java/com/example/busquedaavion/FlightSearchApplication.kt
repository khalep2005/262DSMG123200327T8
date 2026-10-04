package com.example.busquedaavion

import android.app.Application
import com.example.busquedaavion.data.FlightSearchDatabase
import com.example.busquedaavion.data.UserPreferencesRepository
import com.example.busquedaavion.data.dataStore

class FlightSearchApplication : Application() {
    lateinit var database: FlightSearchDatabase
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate() {
        super.onCreate()
        database = FlightSearchDatabase.getDatabase(this)
        userPreferencesRepository = UserPreferencesRepository(dataStore)
    }
}
