package com.udacity.asteroidradar.repository

import android.text.format.DateFormat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.AsteroidContainer
import com.udacity.asteroidradar.api.asDatabaseModel
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.SocketTimeoutException
import java.util.*

class AsteroidsRepository(private val database: AsteroidsDatabase) {

    companion object {
        const val TAG = "AsteroidsRepository"
    }

    val asteroids: LiveData<List<Asteroid>> = Transformations.map(
        database.asteroidDao.getAsteroids(today())) {
        it.asDomainModel()
    }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val asteroidsResponse = AsteroidApi.retrofitService.getAsteroids(
                    today(),
                    plusSevenDays(),
                    API_KEY
                )
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

    private fun plusSevenDays(): String {
        val date = Calendar.getInstance()
        date.add(Calendar.DAY_OF_YEAR, +7)
        return DateFormat.format(Constants.API_QUERY_DATE_FORMAT, date).toString()
    }
}