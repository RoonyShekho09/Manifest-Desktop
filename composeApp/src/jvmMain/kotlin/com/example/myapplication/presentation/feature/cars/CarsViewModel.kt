package com.example.myapplication.presentation.feature.cars

import com.example.myapplication.presentation.base.BaseViewModel

class CarsViewModel : BaseViewModel<CarsUiState, Unit>(CarsUiState()) {


    fun onEditClick(id: String) {
        updateState { copy(isDialogVisible = true) }
    }

    fun onQrCodeClick(id: String) {

    }

    fun onDismissDialog() = updateState { copy(isDialogVisible = false) }
}