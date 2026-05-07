package com.jawharat.manifest.presentation.feature.home.components.dialog

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jawharat.manifest.presentation.components.AppTextField
import com.jawharat.manifest.presentation.feature.home.PassengerFieldState
import com.jawharat.manifest.presentation.feature.home.components.CountrySelectionDropDown
import com.jawharat.manifest.resources.Res
import com.jawharat.manifest.resources.add_passengers
import com.jawharat.manifest.resources.country
import com.jawharat.manifest.resources.full_name
import com.jawharat.manifest.resources.ic_add
import com.jawharat.manifest.resources.ic_remove
import com.jawharat.manifest.resources.id_placeholder
import com.jawharat.manifest.resources.save_changes
import com.jawharat.manifest.utils.allCountries
import com.jawharat.manifest.utils.handClickable
import com.jawharat.manifest.utils.painter
import com.jawharat.manifest.utils.string

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPassengersDialog(
    addedPassengers: List<PassengerFieldState>,
    onSave: (List<PassengerFieldState>) -> Unit,
    onDismiss: () -> Unit,
) {
    val passengers = remember {
        val initialList = addedPassengers.ifEmpty {
            listOf(PassengerFieldState())
        }
        mutableStateListOf(*initialList.toTypedArray())
    }

    BasicAlertDialog(onDismissRequest = onDismiss) {
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
                    text = Res.string.add_passengers.string,
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Res.string.id_placeholder.string,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = Res.string.full_name.string,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = Res.string.country.string,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Start
                    )
                }

                Spacer(Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(
                        items = passengers,
                        key = { _, passenger -> passenger.hashCode() }
                    ) { _, passenger ->
                        var isDropDownExpanded by remember { mutableStateOf(false) }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                AppTextField(
                                    state = passenger.id,
                                    readOnly = !passenger.isEditable
                                )
                            }

                            Box(modifier = Modifier.weight(1f)) {
                                AppTextField(
                                    state = passenger.name,
                                    readOnly = !passenger.isEditable
                                )
                            }

                            Box(modifier = Modifier.weight(1f)) {
                                Column {
                                    Box {
                                        AppTextField(
                                            state = passenger.countryCode,
                                            readOnly = !passenger.isEditable,
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        Box(
                                            modifier = Modifier
                                                .matchParentSize()
                                                .handClickable {
                                                    isDropDownExpanded = passenger.isEditable
                                                }
                                        )
                                    }

                                    CountrySelectionDropDown(
                                        countryList = allCountries,
                                        containerColor = MaterialTheme.colorScheme.background,
                                        contentColor = MaterialTheme.colorScheme.onBackground,
                                        onDismissRequest = { isDropDownExpanded = false },
                                        onSelect = {
                                            isDropDownExpanded = false
                                            passenger.countryCode.clearText()
                                            passenger.countryCode.edit { append(it.name) }
                                        },
                                        expanded = isDropDownExpanded
                                    )
                                }
                            }

                            IconButton(
                                onClick = {
                                    if (passengers.size == 1) {
                                        passengers.remove(passenger)
                                    } else {
                                        passengers.remove(passenger)
                                    }
                                }
                            ) {
                                Icon(
                                    painter = Res.drawable.ic_remove.painter,
                                    contentDescription = null
                                )
                            }
                        }
                    }

                    item {
                        TextButton(
                            onClick = { passengers.add(PassengerFieldState()) },
                            enabled = passengers.size <= 15,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            Icon(
                                painter = Res.drawable.ic_add.painter,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(text = Res.string.add_passengers.string)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onSave(passengers.filter { it.id.text.isNotBlank() })
                    },
                    enabled = passengers.any { it.name.text.isNotBlank() } || passengers.isEmpty()
                ) {
                    Text(text = Res.string.save_changes.string)
                }
            }
        }
    }
}
