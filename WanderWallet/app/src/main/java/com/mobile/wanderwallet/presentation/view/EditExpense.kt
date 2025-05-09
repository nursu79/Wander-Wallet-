//package com.mobile.wanderwallet.presentation.view
//
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.width
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import com.mobile.wanderwallet.presentation.components.DatePickerField
//import com.mobile.wanderwallet.presentation.components.FormField
//import com.mobile.wanderwallet.presentation.components.RectangularButton
//
//
//@Composable
//fun EditExpense() {
//
//    var title by remember {
//        mutableStateOf("")
//    }
//    var amount by remember {
//        mutableStateOf("")
//    }
//    var note by remember {
//        mutableStateOf("")
//    }
//    var titleError by remember { mutableStateOf<String?>(null) }
//    var amountError by remember { mutableStateOf<String?>(null) }
//    var noteError by remember { mutableStateOf<String?>(null) }
//    var selectedCurrency by remember { mutableStateOf<String?>(null) }
//    var selectedDate by remember { mutableStateOf("") }
//    val currencies = listOf("USD", "EUR", "GBP", "JPY", "CAD")
//    var selectedCategory by remember { mutableStateOf<String?>(null) }
//    val categories = listOf("Food", "Transport", "Accommodation", "Entertainment", "Other")
//
//    Column(modifier = Modifier.padding(16.dp)) {
//        FormField(
//            "Expense Title",
//            "Pizza",
//             title,
//            onValueChange = { title = it},
//            errorMessage = titleError
//        )
//        Spacer(Modifier.height(16.dp))
//        FormField(
//            "Amount",
//            "0.0",
//            amount,
//            onValueChange = { amount = it},
//            errorMessage = amountError
//        )
//        Spacer(Modifier.height(16.dp))
//        Dropdown(
//            items = currencies,
//            selectedValue = selectedCurrency,
//            onValueSelected = { selectedCurrency = it },
//            label = "Currency",
//            placeholder = "USD",
//            modifier = Modifier.fillMaxWidth()
//        )
//        Spacer(Modifier.height(16.dp))
//        Dropdown(
//            items = categories,
//            selectedValue = selectedCategory,
//            onValueSelected = { selectedCategory = it },
//            label = "Category",
//            placeholder = "choose currency",
//            modifier = Modifier.fillMaxWidth()
//        )
//        Spacer(Modifier.height(16.dp))
//        DatePickerField(
//            label = "Date",
//            selectedDate = selectedDate,
//            onDateSelected = { newDate ->
//                selectedDate = newDate
//            },
//            modifier = Modifier.fillMaxWidth()
//        )
//        Spacer(Modifier.height(16.dp))
//        FormField(
//            "Note",
//            "Add your note",
//            note,
//            onValueChange = { title = it},
//            errorMessage = noteError
//        )
//        Spacer(Modifier.height(16.dp))
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 32.dp, vertical = 16.dp),
//            horizontalArrangement = Arrangement.Center,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            RectangularButton(
//                onClick = {},
//                modifier = Modifier.weight(0.3f),
//                color = Color(0xFF449494)
//            ) {
//                Text("Cancel")
//            }
//
//            Spacer(Modifier.width(16.dp))
//
//            RectangularButton(
//                onClick = {},
//                modifier = Modifier.weight(0.3f),
//                color = Color(0xFF449494)
//            ) {
//                Text("Save")
//            }
//        }
//    }
//}
//@Preview(showBackground = true)
//@Composable
//fun EditExpensePreview() {
//    EditExpense()
//}
