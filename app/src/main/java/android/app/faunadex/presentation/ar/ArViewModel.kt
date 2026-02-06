package android.app.faunadex.presentation.ar

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.app.faunadex.domain.model.Animal
import android.app.faunadex.domain.usecase.GetAnimalDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArViewModel @Inject constructor(
    private val getAnimalDetailUseCase: GetAnimalDetailUseCase,
    private val application: Application
) : ViewModel() {

    private val _uiState = MutableStateFlow<ArUiState>(ArUiState.Initializing)
    val uiState: StateFlow<ArUiState> = _uiState.asStateFlow()

    private val _sessionState = MutableStateFlow(ArSessionState())
    val sessionState: StateFlow<ArSessionState> = _sessionState.asStateFlow()

    init {
        checkArSupport()
    }

    private fun checkArSupport() {
        viewModelScope.launch {
            try {
                _sessionState.value = _sessionState.value.copy(isArSupported = true)
                _uiState.value = ArUiState.CameraPermissionRequired
            } catch (e: Exception) {
                _sessionState.value = _sessionState.value.copy(
                    isArSupported = false,
                    errorMessage = "AR is not supported on this device"
                )
                _uiState.value = ArUiState.Error("AR is not supported on this device")
            }
        }
    }

    fun onPermissionGranted() {
        _uiState.value = ArUiState.Ready
    }

    fun onPermissionDenied() {
        _uiState.value = ArUiState.Error("Camera permission is required for AR")
    }

    fun startScanning() {
        _uiState.value = ArUiState.Scanning(0)
        _sessionState.value = _sessionState.value.copy(isSessionInitialized = true)
    }

    fun onPlaneDetected(planeCount: Int) {
        _sessionState.value = _sessionState.value.copy(detectedPlanes = planeCount)
        if (_uiState.value is ArUiState.Scanning) {
            _uiState.value = ArUiState.Scanning(planeCount)
        }
    }

    fun loadAnimalForAr(animalId: String) {
        viewModelScope.launch {
            try {
                val result = getAnimalDetailUseCase(animalId)
                result.onSuccess { animal ->
                    _sessionState.value = _sessionState.value.copy(selectedAnimal = animal)
                }.onFailure { e ->
                    _uiState.value = ArUiState.Error("Failed to load animal: ${e.message}")
                }
            } catch (e: Exception) {
                _uiState.value = ArUiState.Error("Error: ${e.message}")
            }
        }
    }

    fun placeAnimal(animal: Animal, x: Float, y: Float, z: Float) {
        val placedAnimal = PlacedAnimal(
            animal = animal,
            positionX = x,
            positionY = y,
            positionZ = z
        )
        val updatedList = _sessionState.value.placedAnimals + placedAnimal
        _sessionState.value = _sessionState.value.copy(placedAnimals = updatedList)
        _uiState.value = ArUiState.AnimalPlaced(animal)
    }

    fun clearPlacedAnimals() {
        _sessionState.value = _sessionState.value.copy(placedAnimals = emptyList())
        _uiState.value = ArUiState.Scanning(_sessionState.value.detectedPlanes)
    }

    fun onSessionError(error: String) {
        _uiState.value = ArUiState.Error(error)
        _sessionState.value = _sessionState.value.copy(errorMessage = error)
    }

    override fun onCleared() {
        super.onCleared()
    }
}
