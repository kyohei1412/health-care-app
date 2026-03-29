package com.example.kyohei.healthapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kyohei.healthapp.ui.HealthViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.FloatEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GraphScreen(viewModel: HealthViewModel) {
    val records by viewModel.allRecords.collectAsStateWithLifecycle()
    val heightCm by viewModel.heightCm.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("1週間", "1ヶ月", "3ヶ月", "全期間")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // ヘッダー
        Text(
            text = "推移グラフ",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "体重とBMIの変化を確認できます",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, style = MaterialTheme.typography.labelLarge) }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        val currentTime = System.currentTimeMillis()
        val filterTime = when (selectedTab) {
            0 -> currentTime - 7L * 24 * 60 * 60 * 1000   // 1 week
            1 -> currentTime - 30L * 24 * 60 * 60 * 1000  // 1 month
            2 -> currentTime - 90L * 24 * 60 * 60 * 1000  // 3 months
            else -> 0L                                     // All
        }

        // 記録を recordedAt 昇順にソートしてフィルタリング
        val filteredRecords = records.filter { it.recordedAt >= filterTime }.sortedBy { it.recordedAt }

        Card(
            modifier = Modifier.fillMaxWidth().weight(1f),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                if (filteredRecords.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                        Text(
                            text = "この期間の記録がありません",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    val dateFormat = SimpleDateFormat("MM/dd", Locale.getDefault())
                    val dateMap = mutableMapOf<Float, String>()
                    val weightMap = mutableMapOf<Float, Float>()

                    filteredRecords.forEachIndexed { index, record ->
                        val xValue = index.toFloat()
                        dateMap[xValue] = dateFormat.format(Date(record.recordedAt))
                        weightMap[xValue] = record.weightKg.toFloat()
                    }

                    val bmiEntries = if (heightCm != null && heightCm!! > 0f) {
                        filteredRecords.mapIndexed { index, record ->
                            val bmi = viewModel.calculateBMI(record.weightKg, heightCm!!)
                            FloatEntry(x = index.toFloat(), y = bmi.toFloat())
                        }
                    } else emptyList()

                    val bottomAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
                        dateMap[value] ?: ""
                    }
                    
                    if (bmiEntries.isNotEmpty()) {
                        val marker = rememberMarker(weightMap = weightMap)
                        Chart(
                            chart = lineChart(),
                            model = entryModelOf(bmiEntries),
                            startAxis = rememberStartAxis(
                                title = "BMI",
                                titleComponent = textComponent(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    margins = dimensionsOf(end = 4.dp)
                                )
                            ),
                            bottomAxis = rememberBottomAxis(
                                title = "日付",
                                titleComponent = textComponent(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    margins = dimensionsOf(top = 4.dp)
                                ),
                                valueFormatter = bottomAxisValueFormatter
                            ),
                            marker = marker,
                            modifier = Modifier.fillMaxWidth().weight(1f)
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = androidx.compose.ui.Alignment.Center) {
                            Text(
                                text = "※設定画面で身長を登録するとBMIグラフが表示されます",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
