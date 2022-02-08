package com.udacity.asteroidradar.repository

import android.text.format.DateFormat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.Constants.DEFAULT_END_DATE_DAYS
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.*
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.SocketTimeoutException
import java.util.*

enum class AsteroidsFilter {TODAY, WEEK, ALL}

class AsteroidsRepository(private val database: AsteroidsDatabase) {

    companion object {
        const val TAG = "AsteroidsRepository"
    }

    fun sortAsteroids(filter: AsteroidsFilter) : LiveData<List<Asteroid>> {
        return when (filter) {
            AsteroidsFilter.TODAY -> {
                Transformations.map(
                    database.asteroidDao.getTodayAsteroids(today())) {
                    it.asDomainModel()
                }
            }
            AsteroidsFilter.WEEK -> {
                Transformations.map(
                    database.asteroidDao.getWeeklyAsteroids(tomorrow(), plusSevenDays())) {
                    it.asDomainModel()
                }
            }
            AsteroidsFilter.ALL -> {
                Transformations.map(
                    database.asteroidDao.getAsteroids()) {
                    it.asDomainModel()
                }
            }
        }
    }

    suspend fun getImageOfToday(): PictureOfDay {
        var imageOfToday: PictureOfDay
        withContext(Dispatchers.IO) {
            imageOfToday = AsteroidApi.retrofitService.getPictureOfDay(API_KEY).asDomainModel()
        }
        return imageOfToday
    }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val asteroidsResponse = AsteroidApi.retrofitService.getAsteroids(API_KEY)
                val jsonObject = JSONObject(asteroidsResponse)
                val asteroids = parseAsteroidsJsonResult(jsonObject)
                val networkAsteroidContainer = AsteroidContainer(asteroids)
                database.asteroidDao.insertAll(*networkAsteroidContainer.asDatabaseModel())
            } catch (e: SocketTimeoutException) {
                refreshAsteroids()
            }  catch (e: Exception) {
                Log.e(TAG, e.printStackTrace().toString())
            }
        }
    }

    private fun today() : String {
        val date = Calendar.getInstance()
        date.add(Calendar.DAY_OF_YEAR,0)
        return DateFormat.format(Constants.API_QUERY_DATE_FORMAT, date).toString()
    }

    private fun tomorrow() : String {
        val date = Calendar.getInstance()
        date.add(Calendar.DAY_OF_YEAR, 1)
        return DateFormat.format(Constants.API_QUERY_DATE_FORMAT, date).toString()
    }

    private fun plusSevenDays(): String {
        val date = Calendar.getInstance()
        date.add(Calendar.DAY_OF_YEAR, DEFAULT_END_DATE_DAYS)
        return DateFormat.format(Constants.API_QUERY_DATE_FORMAT, date).toString()
    }
}