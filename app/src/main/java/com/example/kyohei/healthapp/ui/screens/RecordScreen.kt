package com.example.kyohei.healthapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kyohei.healthapp.ui.HealthViewModel
import com.example.kyohei.healthapp.ui.RecordUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RecordScreen(viewModel: HealthViewModel) {
    val uiRecords by viewModel.uiRecords.collectAsStateWithLifecycle()

    var weightInput by remember { mutableStateOf("") }
    var systolicInput by remember { mutableStateOf("") }
    var diastolicInput by remember { mutableStateOf("") }
    var bodyFatPctInput by remember { mutableStateOf("") }

    val isInputValid = weightInput.toDoubleOrNull() != null &&
                       systolicInput.toIntOrNull() != null &&
                       diastolicInput.toIntOrNull() != null

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // ヘッダー
        Text(
            text = "健康記録",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "今日の測定を追加しましょう",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 入力フォームカード
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = MaterialTheme.shapes.large
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "記録を追加",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    label = { Text("体重") },
                    placeholder = { Text("59.0") },
                    leadingIcon = { Icon(Icons.Filled.Scale, contentDescription = "体重") },
                    trailingIcon = { Text("kg", modifier = Modifier.padding(end = 12.dp)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = bodyFatPctInput,
                    onValueChange = { bodyFatPctInput = it },
                    label = { Text("体脂肪率") },
                    placeholder = { Text("12.0") },
                    leadingIcon = { Icon(Icons.Filled.FitnessCenter, contentDescription = "体脂肪率") },
                    trailingIcon = { Text("%", modifier = Modifier.padding(end = 12.dp)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = systolicInput,
                        onValueChange = { systolicInput = it },
                        label = { Text("最高血圧") },
                        placeholder = { Text("120") },
                        leadingIcon = { Icon(Icons.Filled.Favorite, contentDescription = "最高血圧") },
                        trailingIcon = { Text("mmHg", modifier = Modifier.padding(end = 8.dp)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = diastolicInput,
                        onValueChange = { diastolicInput = it },
                        label = { Text("最低血圧") },
                        placeholder = { Text("80") },
                        leadingIcon = { Icon(Icons.Filled.Favorite, contentDescription = "最低血圧") },
                        trailingIcon = { Text("mmHg", modifier = Modifier.padding(end = 8.dp)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val weight = weightInput.toDoubleOrNull()
                        val systolic = systolicInput.toIntOrNull()
                        val diastolic = diastolicInput.toIntOrNull()
                        val bodyFat = bodyFatPctInput.toDoubleOrNull()
                        if (weight != null && systolic != null && diastolic != null) {
                            viewModel.addRecord(weight, systolic, diastolic, bodyFat)
                            weightInput = ""
                            systolicInput = ""
                            diastolicInput = ""
                            bodyFatPctInput = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = isInputValid,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("保存する", style = MaterialTheme.typography.titleMedium)
                }
                if (!isInputValid && (weightInput.isNotEmpty() || systolicInput.isNotEmpty() || diastolicInput.isNotEmpty())) {
                    Text(
                        text = "入力を確認してください",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "記録一覧",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 80.dp) // 余白をとる
        ) {
            items(uiRecords, key = { it.record.id }) { uiState ->
                RecordListItemCard(
                    uiState = uiState,
                    onDelete = { viewModel.deleteRecord(uiState.record) }
                )
            }
        }
    }
}

@Composable
fun RecordListItemCard(uiState: RecordUiState, onDelete: () -> Unit) {
    val record = uiState.record
    val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
    val dateStr = dateFormat.format(Date(record.recordedAt))

    val bmiStr = uiState.bmi?.let { String.format(Locale.getDefault(), "%.1f", it) } ?: "未設定"
    
    val diffStr = when {
        uiState.weightDiff == null -> ""
        uiState.weightDiff > 0 -> String.format(Locale.getDefault(), "(+%.1fkg)", uiState.weightDiff)
        uiState.weightDiff < 0 -> String.format(Locale.getDefault(), "(%.1fkg)", uiState.weightDiff)
        else -> "(±0.0kg)"
    }
    
    val diffColor = when {
        uiState.weightDiff == null -> Color.Unspecified
        uiState.weightDiff > 0 -> MaterialTheme.colorScheme.error
        uiState.weightDiff < 0 -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            // 上段
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "BMI: $bmiStr",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            
            // 下段
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "${record.weightKg} kg",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (diffStr.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = diffStr,
                                color = diffColor,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AssistChip(
                            onClick = { },
                            label = { Text("血圧: ${record.systolic}/${record.diastolic}", style = MaterialTheme.typography.labelSmall) }
                        )
                        if (record.bodyFatPct != null) {
                            AssistChip(
                                onClick = { },
                                label = { Text("体脂肪: ${record.bodyFatPct}%", style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                }
                
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "削除",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
