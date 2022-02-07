package com.udacity.asteroidradar.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.NetworkAsteroidContainer
import com.udacity.asteroidradar.api.asDatabaseModel
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidsRepository(private val database: AsteroidsDatabase) {

    companion object {
        const val TAG = "AsteroidsRepository"
        val viewModel = MainViewModel(Application())
    }

    val asteroids: LiveData<List<Asteroid>> = Transformations.map(database.asteroidDao.getAsteroids()) {
        it.asDomainModel()
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val asteroidsResponse = AsteroidApi.retrofitService.getAsteroids(
                    viewModel.today(),
                    viewModel.plusSevenDays(),
                    API_KEY
                )
                val jsonObject = JSONObject(asteroidsResponse.string())
                val asteroids = parseAsteroidsJsonResult(jsonObject)
                val networkAsteroidContainer = NetworkAsteroidContainer(asteroids)
                database.asteroidDao.insertAll(*networkAsteroidContainer.asDatabaseModel())
            } catch (e: Exception) {
                Log.e(TAG, e.printStackTrace().toString())
            }
        }
    }
}