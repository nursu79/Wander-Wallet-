package com.mobile.wanderwallet.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.ui.graphics.vector.ImageVector
import com.mobile.wanderwallet.data.model.Category
import com.mobile.wanderwallet.data.model.Expense
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getIconForCategory(category: Category): ImageVector {
    return when (category) {
        Category.FOOD -> Icons.Filled.Fastfood
        Category.ACCOMMODATION -> Icons.Filled.Hotel
        Category.TRANSPORTATION -> Icons.Filled.LocalTaxi
        Category.ENTERTAINMENT -> Icons.Filled.Games
        Category.SHOPPING -> Icons.Filled.ShoppingBag
        Category.OTHER -> Icons.Filled.MoreHoriz
    }
}

fun getExpensesByCategory(expenses: List<Expense>?, category: Category): List<Expense> {
    return expenses?.filter { expense -> expense.category == category } ?: listOf()
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(millis)
}

fun convertDateToFormattedString(date: Date?): String? {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return date?.let { formatter.format(it) }
}
