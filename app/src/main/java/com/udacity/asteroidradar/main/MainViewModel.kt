package com.udacity.asteroidradar.main

import android.app.Application
import android.text.format.DateFormat
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.Constants.API_QUERY_DATE_FORMAT
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class MainViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val TAG = "MainViewModel"
    }

    private val database = getDatabase(application)
    private val asteroidsRepository = AsteroidsRepository(database)

    init {
        viewModelScope.launch {
            asteroidsRepository.refreshAsteroids()
            getPictureOfDay()
        }
    }

    val asteroids = asteroidsRepository.asteroids

    private val _pictureOfDay = MutableLiveData<PictureOfDay?>()
    val pictureOfDay: LiveData<PictureOfDay?>
        get() = _pictureOfDay

    private val _navigateToSingleAsteroid = MutableLiveData<Asteroid?>()
    val navigateToSingleAsteroid: LiveData<Asteroid?>
        get() = _navigateToSingleAsteroid

    fun navigateToSingleAsteroid(asteroidId: Asteroid) {
        _navigateToSingleAsteroid.value = asteroidId
    }

    fun onNavigationComplete() {
        _navigateToSingleAsteroid.value = null
    }

    private suspend fun getPictureOfDay() {
        try {
            _pictureOfDay.value = AsteroidApi.retrofitService.getPictureOfDay(API_KEY)
        } catch (e: Exception) {
            e.message?.let { Log.d(TAG, it) }
        }
    }
}