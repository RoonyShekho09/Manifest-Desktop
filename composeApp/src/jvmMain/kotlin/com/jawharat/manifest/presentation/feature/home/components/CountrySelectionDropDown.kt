package com.jawharat.manifest.presentation.feature.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.jawharat.manifest.resources.Res
import com.jawharat.manifest.resources.ic_search
import com.jawharat.manifest.resources.search
import com.jawharat.manifest.utils.string
import com.joelkanyi.jcomposecountrycodepicker.data.Country
import org.jetbrains.compose.resources.painterResource
import java.text.Normalizer
import java.util.Locale.getDefault

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountrySelectionDropDown(
    countryList: List<Country>,
    containerColor: Color,
    contentColor: Color,
    onDismissRequest: () -> Unit,
    onSelect: (item: Country) -> Unit,
    expanded: Boolean,
    modifier: Modifier = Modifier,
) {
    var searchValue by remember { mutableStateOf("") }
    val filteredItems = remember(searchValue, countryList) {
        if (searchValue.isEmpty()) countryList else countryList.searchForAnItem(searchValue)
    }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(expanded) {
        focusRequester.requestFocus()
    }

    if (expanded)
        Popup(
            onDismissRequest = onDismissRequest,
            properties = PopupProperties(focusable = true)
        ) {
            Surface(
                modifier = modifier
                    .width(300.dp)
                    .heightIn(max = 400.dp),
                shape = MaterialTheme.shapes.medium,
                color = containerColor,
                shadowElevation = 8.dp
            ) {
                Column {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .padding(8.dp)
                            .focusRequester(focusRequester),
                        value = searchValue,
                        onValueChange = { searchValue = it },
                        placeholder = {
                            Text(
                                text = Res.string.search.string,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(Res.drawable.ic_search),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = contentColor,
                            unfocusedTextColor = contentColor,
                            cursorColor = contentColor
                        )
                    )

                    HorizontalDivider(color = contentColor.copy(alpha = 0.12f))

                    LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                        items(filteredItems, key = { it.code }) { countryItem ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSelect(countryItem)
                                        searchValue = ""
                                    }
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Image(
                                    modifier = Modifier.width(28.dp),
                                    painter = painterResource(countryItem.flag),
                                    contentDescription = null
                                )
                                Text(
                                    text = countryItem.name,
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = contentColor
                                )
                                Text(
                                    text = countryItem.code.uppercase(getDefault()),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = contentColor.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        }
}

fun List<Country>.searchForAnItem(
    searchStr: String,
): List<Country> {
    val filteredItems = filter {
        it.name.unaccent().contains(
            searchStr,
            ignoreCase = true,
        ) ||
                it.phoneNoCode.contains(
                    searchStr,
                    ignoreCase = true,
                ) ||
                it.code.contains(
                    searchStr,
                    ignoreCase = true,
                )
    }
    return filteredItems.toList()
}

private val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()
fun CharSequence.unaccent(): String {
    val temp = this.toString().normalizeUnicode()
    return REGEX_UNACCENT.replace(temp, "")
}

fun String.normalizeUnicode(): String = Normalizer.normalize(this, Normalizer.Form.NFD)
