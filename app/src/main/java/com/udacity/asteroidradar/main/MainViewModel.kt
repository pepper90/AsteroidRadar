package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsFilter
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch

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

    private val _pictureOfDay = MutableLiveData<PictureOfDay?>()
    val pictureOfDay: LiveData<PictureOfDay?>
        get() = _pictureOfDay

    private val _filter = MutableLiveData<AsteroidsFilter>()
    val filter: LiveData<AsteroidsFilter>
        get() = _filter

    private val _navigateToSingleAsteroid = MutableLiveData<Asteroid?>()
    val navigateToSingleAsteroid: LiveData<Asteroid?>
        get() = _navigateToSingleAsteroid

    val asteroids = Transformations.switchMap(filter) {
        asteroidsRepository.sortAsteroids(it)
    }

    fun updateFilters(filter: AsteroidsFilter) {
        _filter.value = filter
    }

    fun navigateToSingleAsteroid(asteroidId: Asteroid) {
        _navigateToSingleAsteroid.value = asteroidId
    }

    fun onNavigationComplete() {
        _navigateToSingleAsteroid.value = null
    }

    private suspend fun getPictureOfDay() {
        try {
            _pictureOfDay.value = asteroidsRepository.getImageOfToday()
        } catch (e: Exception) {
            e.message?.let { Log.d(TAG, it) }
        }
    }
}