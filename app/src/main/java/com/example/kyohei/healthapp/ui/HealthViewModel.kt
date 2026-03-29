package com.example.kyohei.healthapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kyohei.healthapp.data.HealthRecord
import com.example.kyohei.healthapp.data.HealthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class RecordUiState(
    val record: HealthRecord,
    val bmi: Double?,
    val weightDiff: Double?
)

class HealthViewModel(private val repository: HealthRepository) : ViewModel() {

    val allRecords: StateFlow<List<HealthRecord>> = repository.allRecords
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val heightCm: StateFlow<Double?> = repository.heightSetting
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun calculateBMI(weightKg: Double, heightCm: Double): Double {
        val heightM = heightCm / 100.0
        return weightKg / (heightM * heightM)
    }

    val uiRecords: StateFlow<List<RecordUiState>> = combine(
        repository.allRecords,
        repository.heightSetting
    ) { records, heightSetting ->
        records.mapIndexed { index, record ->
            val bmi = if (heightSetting != null && heightSetting > 0) {
                calculateBMI(record.weightKg, heightSetting)
            } else null

            val olderRecord = records.getOrNull(index + 1)
            val diff = if (olderRecord != null) record.weightKg - olderRecord.weightKg else null
            RecordUiState(record = record, bmi = bmi, weightDiff = diff)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addRecord(weightKg: Double, systolic: Int, diastolic: Int, bodyFatPct: Double?) {
        viewModelScope.launch {
            val record = HealthRecord(
                weightKg = weightKg,
                systolic = systolic,
                diastolic = diastolic,
                bodyFatPct = bodyFatPct,
                recordedAt = System.currentTimeMillis()
            )
            repository.insertRecord(record)
        }
    }

    fun deleteRecord(record: HealthRecord) {
        viewModelScope.launch {
            repository.deleteRecord(record)
        }
    }

    fun saveHeight(height: Double) {
        viewModelScope.launch {
            repository.saveHeight(height)
        }
    }
}

class HealthViewModelFactory(private val repository: HealthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HealthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HealthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
