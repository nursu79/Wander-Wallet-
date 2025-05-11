package com.mobile.wanderwallet.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.PlotType
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.BarStyle
import co.yml.charts.ui.piechart.charts.PieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.mobile.wanderwallet.data.model.Category
import com.mobile.wanderwallet.data.model.CategorySpending
import com.mobile.wanderwallet.ui.theme.WanderWalletTheme

@Composable
fun SpendingByCategoryCard(
    spendingByCategory: List<CategorySpending>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = "Spending By Category",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            SpendingByCategoryBarChart(spendingByCategory = spendingByCategory)

            Spacer(modifier = Modifier.height(12.dp))

            SpendingByCategoryPieChart(spendingByCategory = spendingByCategory)
        }
    }
}

@Composable
fun SpendingByCategoryBarChart(
    spendingByCategory: List<CategorySpending>,
    modifier: Modifier = Modifier
) {
    val categories = spendingByCategory.map { categorySpending -> categorySpending.category }
    val spendings = spendingByCategory.map { categorySpending -> categorySpending.amount }
    val stepSize = 5
    val barsData = mutableListOf(
        BarData(Point(0f, 0f))
    )
    spendings.forEachIndexed { i, spending ->
        barsData.add(
            BarData(
                point = Point((i + 1).toFloat(), spending),
                color = MaterialTheme.colorScheme.primary,
                label = categories[i].name,
                description = categories[i].name
            )
        )
    }

    val xAxisData = AxisData.Builder()
        .axisStepSize(200.dp)
        .steps(barsData.size - 1)
        .bottomPadding(40.dp)
        .axisLabelAngle(45f)
        .labelData { i -> if (i == 0) "" else categories[i - 1].name.substring(0..3) }
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .build()

    val yAxisData = AxisData.Builder()
        .steps(stepSize)
        .labelAndAxisLinePadding(20.dp)
        .axisOffset(20.dp)
        .labelData { i -> ((i * (spendings.max() / stepSize).toInt()).toString()) }
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .build()

    val barChartData = BarChartData(
        chartData = barsData,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        backgroundColor = MaterialTheme.colorScheme.surface
    )

    BarChart(
        barChartData = barChartData,
        modifier = modifier
            .height(350.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
    )
}

@Composable
fun SpendingByCategoryPieChart(
    spendingByCategory: List<CategorySpending>,
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        Color(0xFF5F0A87),
        Color(0xFF20BF55),
        Color(0xFFEC9F05),
        Color(0xFFF53844),
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.primaryContainer
    )

    val slicesData = PieChartData(
        slices = spendingByCategory.mapIndexed { i, item ->
            PieChartData.Slice(
                item.category.name, item.amount, colors[i]
            )
        },
        plotType = PlotType.Pie
    )
    val pieChartConfig = PieChartConfig(
        isAnimationEnable = true,
        showSliceLabels = true,
        activeSliceAlpha = 0.5f,
        animationDuration = 600,
        backgroundColor = MaterialTheme.colorScheme.surface
    )

    PieChart(
        pieChartData = slicesData,
        pieChartConfig = pieChartConfig,
        modifier = modifier
            .height(300.dp)
            .width(300.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun SpendingByCategoryBarChartPreview() {
    WanderWalletTheme {
        SpendingByCategoryBarChart(
            spendingByCategory = listOf(
                CategorySpending(category = Category.FOOD, amount = 200f),
                CategorySpending(category = Category.SHOPPING, amount = 250f),
                CategorySpending(category = Category.ACCOMMODATION, amount = 180f),
                CategorySpending(category = Category.ENTERTAINMENT, amount = 230f)
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}