package com.udacity.asteroidradar.main

import android.app.Application
import android.text.format.DateFormat
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

enum class AsteroidApiStatus { LOADING, ERROR, DONE }

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = getDatabase(application)
    private val asteroidsRepository = AsteroidsRepository(database)

    init {
        viewModelScope.launch {
            asteroidsRepository.refreshAsteroids()
            getPictureOfDay()
        }
//        fetchData()
    }

    val asteroids = asteroidsRepository.asteroids

//    private val _asteroids = MutableLiveData<List<Asteroid>>()
//    val asteroids: LiveData<List<Asteroid>>
//    get() = _asteroids

    private val _pictureOfDay = MutableLiveData<PictureOfDay?>()
    val pictureOfDay: LiveData<PictureOfDay?>
    get() = _pictureOfDay

//    private val _status = MutableLiveData<AsteroidApiStatus>()
//    val status: LiveData<AsteroidApiStatus>
//        get() = _status

    private val _navigateToSingleAsteroid = MutableLiveData<Asteroid?>()
    val navigateToSingleAsteroid : LiveData<Asteroid?>
    get() = _navigateToSingleAsteroid



    fun navigateToSingleAsteroid(asteroidId: Asteroid) {
        _navigateToSingleAsteroid.value = asteroidId
    }

    fun onNavigationComplete() {
        _navigateToSingleAsteroid.value = null
    }



//    @Suppress("BlockingMethodInNonBlockingContext")
//    private suspend fun getAsteroids(startDate: String, endDate: String) {
//        val responseBody = AsteroidApi.retrofitService.getAsteroids(startDate, endDate, API_KEY)
//        val jsonObject = JSONObject(responseBody.string())
//        _asteroids.value = parseAsteroidsJsonResult(jsonObject)
//    }

    fun today() : String {
        val date = Calendar.getInstance()
        date.add(Calendar.DAY_OF_YEAR,0)
        return DateFormat.format(API_QUERY_DATE_FORMAT, date).toString()
    }

    fun plusSevenDays(): String {
        val date = Calendar.getInstance()
        date.add(Calendar.DAY_OF_YEAR, +7)
        return DateFormat.format(API_QUERY_DATE_FORMAT, date).toString()
    }

    private suspend fun getPictureOfDay() {
        _pictureOfDay.value = AsteroidApi.retrofitService.getPictureOfDay(API_KEY)
    }

//    private fun fetchData() {
//        viewModelScope.launch {
//                getPictureOfDay()
//        }
//    }
}