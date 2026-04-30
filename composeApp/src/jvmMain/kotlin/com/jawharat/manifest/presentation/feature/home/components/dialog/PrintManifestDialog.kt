package com.jawharat.manifest.presentation.feature.home.components.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jawharat.manifest.presentation.components.AppTextField
import com.jawharat.manifest.resources.Res
import com.jawharat.manifest.resources.confirm
import com.jawharat.manifest.resources.dismiss
import com.jawharat.manifest.resources.enter_manifest_id
import com.jawharat.manifest.utils.handPointerHover
import com.jawharat.manifest.utils.string
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrintManifestDialog(onConfirm: (id: String, year: String) -> Unit, onDismiss: () -> Unit) {
    var idTextField by remember { mutableStateOf("") }
    var yearTextField by remember { mutableStateOf(LocalDate.now().year.toString()) }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.widthIn(min = 320.dp, max = 480.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 2.dp,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = Res.string.enter_manifest_id.string,
                    style = MaterialTheme.typography.headlineSmall
                )

                AppTextField(
                    value = idTextField,
                    placeholder = "",
                    onValueChange = { idTextField = it },
                    modifier = Modifier.height(24.dp)
                )

                AppTextField(
                    value = yearTextField,
                    onValueChange = { yearTextField = it },
                    placeholder = "",
                    modifier = Modifier.height(24.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { onConfirm(idTextField, yearTextField) },
                        modifier = Modifier.handPointerHover()
                    ) {
                        Text(text = Res.string.confirm.string)
                    }

                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.handPointerHover()
                    ) {
                        Text(text = Res.string.dismiss.string)
                    }
                }
            }
        }
    }
}
