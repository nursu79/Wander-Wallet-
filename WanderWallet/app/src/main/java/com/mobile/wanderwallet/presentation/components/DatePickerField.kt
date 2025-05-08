package com.mobile.wanderwallet.presentation.components

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DatePickerField(
    label: String,
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val datePicker = remember { MaterialDatePicker.Builder.datePicker().build() }

    datePicker.addOnPositiveButtonClickListener {
        val formattedDate = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            .format(Date(it))
        onDateSelected(formattedDate)
    }

    OutlinedTextField(
        value = selectedDate.ifEmpty { "Select $label" },
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        trailingIcon = {
            IconButton(
                onClick = { showDatePicker(context, datePicker) },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Select date"
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),
        modifier = modifier.fillMaxWidth()
    )
}

private fun showDatePicker(context: Context, picker: MaterialDatePicker<Long>) {
    if (context is FragmentActivity) {
        if (!picker.isAdded) {
            picker.show(context.supportFragmentManager, picker.toString())
        }
    }
}