package com.mobile.wanderwallet.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dropdown(
    items: List<String>,
    selectedValue: String?,
    onValueSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    placeholder: String = "Select",
    enabled: Boolean = true,
    cornerRadius: Dp = 12.dp,
    itemFontSize: TextUnit = 16.sp
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        Box {
            OutlinedTextField(
                value = selectedValue ?: "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(placeholder) },
                trailingIcon = {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ArrowDropUp
                        else Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFFAFAFA),
                    unfocusedContainerColor = Color(0xFFFAFAFA),
                    disabledContainerColor = Color.White.copy(alpha = 0.5f)
                ),
                readOnly = true,
                enabled = enabled,
                singleLine = true,
                label = {}
            )
            Button(
                onClick = { if (enabled) expanded = true },
                modifier = Modifier
                    .matchParentSize()
                    .alpha(0f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Transparent
                ),
                enabled = enabled
            ) {}
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            onValueSelected(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
