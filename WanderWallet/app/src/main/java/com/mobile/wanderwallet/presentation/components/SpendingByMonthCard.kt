package com.mobile.wanderwallet.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.mobile.wanderwallet.ui.theme.WanderWalletTheme

@Composable
fun SpendingByMonthCard(
    monthlySpending: List<Map<String, Float>>,
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
                text = "Spending By Month",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            SpendingByMonthLineChart(monthlySpending = monthlySpending)
        }
    }
}

@Composable
fun SpendingByMonthLineChart(
    monthlySpending: List<Map<String, Float>>,
    modifier: Modifier = Modifier
) {
    val steps = 5
    val months = mutableListOf<String>()
    val amounts = mutableListOf<Float>()
    val pointsData = mutableListOf(
        Point(0f, 0f)
    )

    monthlySpending.asReversed().forEach { map ->
        months.addAll(map.keys)
        amounts.addAll(map.values)
    }
    amounts.forEachIndexed { i, amount ->
        pointsData.add(
            Point((i + 1).toFloat(), amount)
        )
    }

    val xAxisData = AxisData.Builder()
        .axisStepSize(100.dp)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .steps(pointsData.size - 1)
        .labelData { i -> if (i == 0) "" else months[i - 1] }
        .labelAndAxisLinePadding(15.dp)
        .build()
    val yAxisData = AxisData.Builder()
        .steps(steps)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .labelAndAxisLinePadding(20.dp)
        .labelData { i -> (i * (amounts.max() / steps).toInt()).toString()}
        .build()
    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsData,
                    LineStyle(color = MaterialTheme.colorScheme.primary),
                    IntersectionPoint(),
                    SelectionHighlightPoint(),
                    ShadowUnderLine(color = MaterialTheme.colorScheme.primary),
                    SelectionHighlightPopUp()
                )
            ),
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(),
        backgroundColor = MaterialTheme.colorScheme.surface
    )

    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        lineChartData = lineChartData
    )
}

@Preview
@Composable
private fun SpendingMyMonthLineChartPreview() {
    WanderWalletTheme {
        SpendingByMonthLineChart(
            monthlySpending = listOf(
                mapOf("Jan" to 120f),
                mapOf("Dec" to 135f),
                mapOf("Nov" to 90f),
                mapOf("Oct" to 160f),
                mapOf("Sep" to 50f)
            )
        )
    }

}
