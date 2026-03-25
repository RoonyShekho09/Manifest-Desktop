package com.jawharat.manifest.presentation.feature.home.components.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jawharat.manifest.presentation.components.AppTextField
import com.jawharat.manifest.presentation.feature.home.components.CountrySelectionDropDown
import com.jawharat.manifest.resources.Res
import com.jawharat.manifest.resources.ic_add
import com.jawharat.manifest.utils.painter
import com.joelkanyi.jcomposecountrycodepicker.component.rememberKomposeCountryCodePickerState

class PassengerFieldState {
    val id = TextFieldState()
    val name = TextFieldState()
    val country = TextFieldState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPassengerDialog(
    onDismiss: () -> Unit,
) {
    val passengers = remember { mutableStateListOf(PassengerFieldState()) }
    var isDropDownExpanded by remember { mutableStateOf(false) }


    BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.width(1000.dp),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Add Passengers",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(24.dp))

                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(passengers) { passenger ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                AppTextField(
                                    state = passenger.id,
                                    placeholder = "Passport ID / National ID"
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                AppTextField(state = passenger.name, placeholder = "Full name")
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                val state = rememberKomposeCountryCodePickerState(
                                    showCountryCode = false,
                                    showCountryFlag = true,
                                    defaultCountryCode = "IQ",
                                    priorityCountries = listOf("IQ", "Turkey", "Iran")
                                )

                                Column {
                                    Box {
                                        AppTextField(
                                            state = passenger.country,
                                            placeholder = "Country",
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        Box(
                                            modifier = Modifier
                                                .matchParentSize()
                                                .clickable {
                                                    isDropDownExpanded = true
                                                }
                                        )
                                    }

                                    CountrySelectionDropDown(
                                        countryList = state.countryList,
                                        containerColor = MaterialTheme.colorScheme.background,
                                        contentColor = MaterialTheme.colorScheme.onBackground,
                                        onDismissRequest = { isDropDownExpanded = false },
                                        onSelect = {
                                            isDropDownExpanded = false
                                            passenger.country.clearText()
                                            passenger.country.edit {
                                                append(it.name)
                                            }
                                        },
                                        expanded = isDropDownExpanded
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                FilledIconButton(
                    onClick = { passengers.add(PassengerFieldState()) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Icon(
                        painter = Res.drawable.ic_add.painter,
                        contentDescription = null
                    )
                }
            }
        }
    }
}