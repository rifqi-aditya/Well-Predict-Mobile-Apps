package com.will.capstonebangkit.data.repository

import SymptomPreference
import android.util.Log
import androidx.datastore.core.IOException
import androidx.lifecycle.liveData
import com.will.capstonebangkit.data.ResultState
import com.will.capstonebangkit.data.api.retrofit.WellPredictApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class SymptomsRepository(
    private val wellPredictApiService: WellPredictApiService,
    private val symptomPreference: SymptomPreference
) {

    fun getSymptomsList() = liveData(Dispatchers.IO) {
        emit(ResultState.Loading)
        try {
            val successResponse = wellPredictApiService.getSymptomsList()
            Log.d("asd", "getSymptomsList: $successResponse")
            emit(ResultState.Success(successResponse.symptoms))
        } catch (e: Exception) {
            emit(ResultState.Error(e.localizedMessage ?: "Unknown Error"))
            Log.d("asd", "getSymptomsList: $e")
        }
    }

    fun getSymptomsFlow(): Flow<List<String>> {
        return symptomPreference.symptomsFlow
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyList<String>()) // Handle error case, emit default value
                } else {
                    throw exception
                }
            }
    }

    suspend fun removeSelectedSymptom(symptom: String) {
        symptomPreference.removeSelectedSymptom(symptom)
    }

    suspend fun clearSelectedSymptoms() {
        symptomPreference.clearSelectedSymptoms()
    }

    companion object {
        @Volatile
        private var instance: SymptomsRepository? = null
        fun getInstance(
            wellPredictApiService: WellPredictApiService,
            symptomPreference: SymptomPreference
        ): SymptomsRepository =
            instance ?: synchronized(this) {
                instance ?: SymptomsRepository(wellPredictApiService, symptomPreference)
            }.also { instance = it }
    }
}