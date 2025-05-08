package com.mobile.wanderwallet.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mobile.wanderwallet.data.model.Category
import com.mobile.wanderwallet.data.model.Expense
import com.mobile.wanderwallet.data.model.ExpenseByCategory
import com.mobile.wanderwallet.utils.getIconForCategory
import java.util.Locale

@Composable
fun BudgetSummarySection(
    totalBudget: Float,
    totalExpense: Float
) {
    val progress = totalExpense / totalBudget
    Column(modifier = Modifier.padding(16.dp)) {
        BudgetRow(label = "Total Budget", value = "$$totalBudget")
        Spacer(modifier = Modifier.height(12.dp))

        BudgetRow(label = "Total Expense", value = "$$totalExpense")
        Spacer(modifier = Modifier.height(12.dp))

        BudgetRow(label = "Budget Progress", value = "${ (progress * 100).toInt() }%")
        Spacer(modifier = Modifier.height(12.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .padding(top = 8.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = Color.LightGray.copy(alpha = 0.3f),
        )
    }
}

@Composable
fun BudgetRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall)
        Text(text = value, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun ExpensesByCategoriesCardsSection(
    expensesByCategories: List<ExpenseByCategory>,
    onCardClick: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(expensesByCategories) { expenseByCategory ->
            CategoryCard(
                expense = expenseByCategory,
                onClick = { onCardClick(expenseByCategory.category) },
                modifier = Modifier
                    .padding(vertical = 12.dp)
            )
        }
    }
}

@Composable
fun CategoryCard(
    expense: ExpenseByCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryIcon(category = expense.category)
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                val locale = Locale.getDefault()
                Text(
                    text = expense.category
                        .toString()
                        .lowercase()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() },
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Total Expense",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = "$${expense.sum.amount}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun CategoryIcon(category: Category, modifier: Modifier = Modifier) {
    val icon = getIconForCategory(category)

    Box(
        modifier = modifier
            .size(40.dp)
            .background(MaterialTheme.colorScheme.primary, CircleShape)
            .border(
                width = 1.dp,
                color = Color(0xFF20D3A7),
                shape = CircleShape
            )
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = category.toString(),
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun ExpenseCard(expense: Expense, onClick: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .clickable { onClick(expense.id) }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(expense.name, style = MaterialTheme.typography.bodyMedium)
            Text("$${expense.amount}", style = MaterialTheme.typography.bodySmall)
        }

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth()
        )
    }
}
