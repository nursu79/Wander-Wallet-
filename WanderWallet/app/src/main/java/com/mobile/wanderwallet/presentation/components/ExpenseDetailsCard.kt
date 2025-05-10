package com.mobile.wanderwallet.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mobile.wanderwallet.data.model.Expense
import com.mobile.wanderwallet.utils.convertDateToFormattedString

@Composable
fun ExpenseDetailsCard(
    expense: Expense,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxWidth()
        ) {
            Text("Title", style = MaterialTheme.typography.bodyMedium)
            Text(expense.name, style = MaterialTheme.typography.labelLarge)
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxWidth()
        ) {
            Text("Amount", style = MaterialTheme.typography.bodyMedium)
            Text("$${expense.amount}", style = MaterialTheme.typography.labelLarge)
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxWidth()
        ) {
            Text("Category", style = MaterialTheme.typography.bodyMedium)
            Text(expense.category.name.lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelLarge)
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxWidth()
        ) {
            Text("Date", style = MaterialTheme.typography.bodyMedium)
            Text(convertDateToFormattedString(expense.date) ?: "", style = MaterialTheme.typography.labelLarge)
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxWidth()
        ) {
            Text("Notes", style = MaterialTheme.typography.bodyMedium)
            Text(expense.notes ?: "", style = MaterialTheme.typography.labelLarge)
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
    }
}