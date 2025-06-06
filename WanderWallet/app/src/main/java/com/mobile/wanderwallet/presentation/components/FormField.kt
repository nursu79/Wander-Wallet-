package com.mobile.wanderwallet.presentation.components

import android.app.DatePickerDialog
import android.net.Uri
import android.widget.DatePicker
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.mobile.wanderwallet.data.model.Category
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun FormField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    isPassword: Boolean = false,
    isNumber: Boolean = false,
    isFinal: Boolean = false
) {
    var passwordVisible by remember { mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(start = 8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(placeholder, style = MaterialTheme.typography.labelMedium)
            },
            visualTransformation = when {
                isPassword && !passwordVisible -> PasswordVisualTransformation()
                else -> VisualTransformation.None
            },
            keyboardOptions = when {
                isPassword -> KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = if (isFinal) ImeAction.Done else ImeAction.Next
                )
                isNumber -> KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = if (isFinal) ImeAction.Done else ImeAction.Next
                )
                else -> KeyboardOptions(
                    imeAction = if (isFinal) ImeAction.Done else ImeAction.Next
                )
            },
            isError = (errorMessage != null),
            shape = MaterialTheme.shapes.small,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            trailingIcon = {
                if (isPassword) {
                    val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = icon, contentDescription = "Toggle visibility")
                    }
                }
            },
            textStyle = MaterialTheme.typography.labelLarge,
            modifier = Modifier.fillMaxWidth()
        )
        if (errorMessage != null) {
            Text(errorMessage, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Composable
fun SelectImageField(
    label: String,
    launcher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(start = 8.dp))
        OutlinedButton(
            onClick = {
                launcher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        ) {
            Text("Select an image", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Composable
fun DatePickerField(
    label: String,
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String? = null
) {
    val context = LocalContext.current

    // Store the current date for default value
    val calendar = remember { Calendar.getInstance() }

    // Open the DatePickerDialog when icon is clicked
    val openDatePicker = {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            context,
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                val formattedDate = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(calendar.time)
                onDateSelected(formattedDate)
            },
            year, month, day
        ).apply {
            // Optional: You can style the dialog here if needed
            show()
        }
    }

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(start = 8.dp))
        OutlinedTextField(
            value = selectedDate.ifEmpty { "Select $label" },
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                IconButton(
                    onClick = {openDatePicker()},
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Select $label",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            ),
            textStyle = MaterialTheme.typography.labelLarge,
            modifier = modifier.fillMaxWidth()
        )
        if (errorMessage != null) {
            Text(errorMessage, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.error)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectCategoryTextField(
    label: String,
    onItemSelected: (Category) -> Unit,
    modifier: Modifier = Modifier,
    selectedCategory: Category? = Category.FOOD,
    options: Array<Category> = Category.entries.toTypedArray(),
    errorMessage: String? = null,
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(
        selectedCategory?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: ""
    )}

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(start = 8.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = modifier
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                readOnly = true,
                value = selectedOptionText,
                onValueChange = { /* Not needed as it's read-only */ },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
                textStyle = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryEditable, true) // Attach the menu to the TextField
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(
                            selectionOption.name.lowercase().replaceFirstChar { it.uppercase() }
                        ) },
                        onClick = {
                            selectedOptionText = selectionOption.name.lowercase().replaceFirstChar { it.uppercase() }
                            expanded = false
                            onItemSelected(selectionOption)
                        }
                    )
                }
            }
        }
        if (errorMessage != null) {
            Text(errorMessage, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(start = 8.dp))
        }
    }
}

