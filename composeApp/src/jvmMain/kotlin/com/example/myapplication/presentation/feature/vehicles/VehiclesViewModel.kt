package com.example.myapplication.presentation.feature.vehicles

import com.example.myapplication.domain.repository.ManifestRepository
import com.example.myapplication.presentation.base.BaseViewModel

class VehiclesViewModel(private val repository: ManifestRepository) :
    BaseViewModel<VehiclesUiState, Unit>(VehiclesUiState()) {

    init {
        initializeVehicles()
    }

    private fun initializeVehicles() = tryToExecute(
        onStart = { updateState { copy(isLoading = true) } },
        block = repository::getVehicles,
        onSuccess = { updateState { copy(cars = it.toUiState()) } },
        onCompleted = { updateState { copy(isLoading = false) } }
    )

    fun onEditClick(id: String) {
        updateState { copy(isDialogVisible = true) }
    }

    fun onQrCodeClick(id: String) {

    }

    fun onDismissDialog() = updateState { copy(isDialogVisible = false) }
}
