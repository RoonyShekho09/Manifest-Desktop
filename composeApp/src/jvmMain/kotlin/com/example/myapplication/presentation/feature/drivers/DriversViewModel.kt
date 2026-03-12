package com.example.myapplication.presentation.feature.drivers

import com.example.myapplication.presentation.base.BaseViewModel

class DriversViewModel : BaseViewModel<DriverUiState, Unit>(DriverUiState()) {

    fun onEditClick(id: String) {
        updateState { copy(isDialogVisible = true) }
    }

    fun onQrCodeClick(id: String) {

    }

    fun onDismissDialog() = updateState { copy(isDialogVisible = false) }

}