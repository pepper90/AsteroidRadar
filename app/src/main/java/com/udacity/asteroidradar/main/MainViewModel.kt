package com.udacity.asteroidradar.main

import android.text.format.DateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.Constants.API_QUERY_DATE_FORMAT
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

enum class AsteroidApiStatus { LOADING, ERROR, DONE }

class MainViewModel : ViewModel() {
    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
    get() = _asteroids

    private val _pictureOfDay = MutableLiveData<PictureOfDay?>()
    val pictureOfDay: LiveData<PictureOfDay?>
    get() = _pictureOfDay

    private val _status = MutableLiveData<AsteroidApiStatus>()
    val status: LiveData<AsteroidApiStatus>
        get() = _status

    private val _navigateToSingleAsteroid = MutableLiveData<Asteroid?>()
    val navigateToSingleAsteroid : LiveData<Asteroid?>
    get() = _navigateToSingleAsteroid

    init {
        fetchData()
    }

    fun navigateToSingleAsteroid(asteroidId: Asteroid) {
        _navigateToSingleAsteroid.value = asteroidId
    }

    fun onNavigationComplete() {
        _navigateToSingleAsteroid.value = null
    }



    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun getAsteroids(startDate: String, endDate: String) {
        val responseBody = AsteroidApi.retrofitService.getAsteroids(startDate, endDate, API_KEY)
        val jsonObject = JSONObject(responseBody.string())
        _asteroids.value = parseAsteroidsJsonResult(jsonObject)
    }

    private fun getToday() : String {
        val date = Calendar.getInstance()
        date.add(Calendar.DAY_OF_YEAR,0)
        return DateFormat.format(API_QUERY_DATE_FORMAT, date).toString()
    }

    private fun getSevenDays(): String {
        val date = Calendar.getInstance()
        date.add(Calendar.DAY_OF_YEAR, +7)
        return DateFormat.format(API_QUERY_DATE_FORMAT, date).toString()
    }

    private suspend fun getPictureOfDay() {
        _pictureOfDay.value = AsteroidApi.retrofitService.getPictureOfDay(API_KEY)
    }

    private fun fetchData() {
        viewModelScope.launch {
            _status.value = AsteroidApiStatus.LOADING
            try {
                getPictureOfDay()
                getAsteroids(getToday(),getSevenDays())
                _status.value = AsteroidApiStatus.DONE
            } catch (e: Exception) {
                _status.value = AsteroidApiStatus.ERROR
                _pictureOfDay.value = null
                _asteroids.value = ArrayList()
            }
        }
    }
}