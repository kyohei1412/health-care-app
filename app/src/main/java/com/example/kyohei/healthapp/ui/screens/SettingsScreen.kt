package com.example.kyohei.healthapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Height
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kyohei.healthapp.ui.HealthViewModel

@Composable
fun SettingsScreen(viewModel: HealthViewModel) {
    val heightCm by viewModel.heightCm.collectAsStateWithLifecycle()
    var heightInput by remember(heightCm) { 
        mutableStateOf(heightCm?.let { if (it > 0) it.toString() else "" } ?: "") 
    }

    val isInputValid = heightInput.toDoubleOrNull() != null && heightInput.toDoubleOrNull()!! > 0.0

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // ヘッダー
        Text(
            text = "設定",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "アプリの基本設定を行います",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (heightCm == null || heightCm!! <= 0.0) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Text(
                    text = "BMIを計算するため、身長を設定してください",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = MaterialTheme.shapes.large
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "プロフィール",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = heightInput,
                    onValueChange = { heightInput = it },
                    label = { Text("身長") },
                    placeholder = { Text("170.0") },
                    leadingIcon = { Icon(Icons.Filled.Height, contentDescription = "身長") },
                    trailingIcon = { Text("cm", modifier = Modifier.padding(end = 12.dp)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val height = heightInput.toDoubleOrNull()
                        if (height != null && height > 0) {
                            viewModel.saveHeight(height)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = isInputValid,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("保存する", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
