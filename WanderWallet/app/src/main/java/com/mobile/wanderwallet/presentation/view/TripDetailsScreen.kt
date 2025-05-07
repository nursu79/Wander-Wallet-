package com.mobile.wanderwallet.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobile.wanderwallet.R

data class Expense(
    val category: String,
    val amount: Double
)

@Composable
fun TripDetailsScreen(
    id: String,
    onLoggedOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDetails by remember { mutableStateOf(false) }
    var selectedExpense by remember { mutableStateOf<Expense?>(null) }

    val expenses = listOf(
        Expense("Food", 300.0),
        Expense("Travel", 200.0),
        Expense("Accommodation", 1000.0),
    )

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            BudgetSummarySection(
                totalBudget = 3000,
                totalExpense = 1500,
                progress = 0.5f
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (showDetails && selectedExpense != null) {
                ExpenseDetailSection(
                    expense = selectedExpense!!,
                    onBack = { showDetails = false },
                    onLoggedOut = onLoggedOut
                )
            } else {
                Column {
                    Text(
                        text = "Expenses",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    ExpenseCardsSection(
                        expenses = expenses,
                        onCardClick = { expense ->
                            selectedExpense = expense
                            showDetails = true
                        }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = {  },
            containerColor = Color(0xFF449494),
            modifier = Modifier.size(100.dp).align(Alignment.BottomEnd).padding(20.dp),
            shape = CircleShape,
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                "Add",
                tint = Color.White
            )
        }
    }

}

@Composable
fun ExpenseDetailSection(
    expense: Expense,
    onBack: () -> Unit,
    onLoggedOut: () -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            TextButton(
                onClick = onBack,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Minimize",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Minimize")
            }

            Text(
                text = expense.category,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )

            IconButton(
                onClick = onLoggedOut,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Details",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                BudgetRow(label = "Amount", value = "$${expense.amount}")
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ExpenseIcon(category = expense.category)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Category: ${expense.category}")
                }
            }
        }
    }
}

@Composable
fun ExpenseCardsSection(
    expenses: List<Expense>,
    onCardClick: (Expense) -> Unit
) {
    LazyColumn {
        items(expenses) { expense ->
            ExpenseCard(
                expense = expense,
                onClick = { onCardClick(expense) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

}

@Composable
fun ExpenseCard(expense: Expense, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF2F2F2)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExpenseIcon(category = expense.category)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.category,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Total Expense",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = "$${expense.amount}",
                style = MaterialTheme.typography.bodyMedium
            )
//            Icon(
//                Icons.AutoMirrored.Filled.ArrowForward,
//                contentDescription = "View details",
//                modifier = Modifier.size(24.dp)
//            )
        }
    }
}

@Composable
fun ExpenseIcon(category: String) {
    val iconRes = when (category) {
        "Travel" -> R.drawable.travel
        else -> R.drawable.food
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .background(Color(0xFF449494), CircleShape)
            .border(
                width = 1.dp,
                color = Color(0xFF20D3A7),
                shape = CircleShape
            )
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = category,
            modifier = Modifier.size(20.dp),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun BudgetSummarySection(
    totalBudget: Int,
    totalExpense: Int,
    progress: Float
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )

    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            BudgetRow(label = "Total Budget", value = "$$totalBudget")
            Spacer(modifier = Modifier.height(12.dp))


            BudgetRow(label = "Total Expense", value = "$$totalExpense")
            Spacer(modifier = Modifier.height(12.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Budget Progress")
                Text(text = "${(progress * 100).toInt()}%")
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .padding(top = 8.dp),
                color = Color(0xFF377474),
                trackColor = Color.LightGray.copy(alpha = 0.3f),
            )
        }
    }
}

@Composable
fun BudgetRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label)
        Text(text = value)
    }
}

@Composable
fun MyFloatingActionButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = Color(0xFF6200EE),
        modifier = Modifier.size(56.dp) // Standard FAB size
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            "Add"
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TripDetailsScreenPreview() {
//    MaterialTheme {
//
//    }
    TripDetailsScreen(
        id = "test123",
        onLoggedOut = { /* Do nothing for preview */ }
    )
}