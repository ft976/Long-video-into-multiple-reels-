package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.CutTemplate
import com.example.ui.TemplatesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTemplateScreen(
    viewModel: TemplatesViewModel,
    onNavigateBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var durationSecs by remember { mutableStateOf("15") }
    
    var expandedRatio by remember { mutableStateOf(false) }
    var selectedRatio by remember { mutableStateOf("9:16 (Portrait)") }
    val ratios = listOf("9:16 (Portrait)", "16:9 (Landscape)", "1:1 (Square)", "4:5 (Vertical)")

    var expandedPlatform by remember { mutableStateOf(false) }
    var selectedPlatform by remember { mutableStateOf("Instagram Reels") }
    val platforms = listOf("Instagram Reels", "TikTok", "YouTube Shorts", "Universal")

    var smartCutEnabled by remember { mutableStateOf(true) }
    var skipSilentParts by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Template") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            val durationInt = durationSecs.toIntOrNull() ?: 15
                            val actualRatio = selectedRatio.split(" ").first()
                            viewModel.saveTemplate(
                                CutTemplate(
                                    name = name.ifBlank { "Untitled Template" },
                                    aspectRatio = actualRatio,
                                    durationSecs = durationInt,
                                    targetPlatform = selectedPlatform,
                                    smartCutEnabled = smartCutEnabled,
                                    skipSilentParts = skipSilentParts
                                )
                            )
                            onNavigateBack()
                        },
                        enabled = durationSecs.isNotBlank() && name.isNotBlank()
                    ) {
                        Text("SAVE")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("General Specs", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Template Name") },
                placeholder = { Text("e.g. Daily Vlog Shorts") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Aspect Ratio Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedRatio,
                onExpandedChange = { expandedRatio = !expandedRatio }
            ) {
                OutlinedTextField(
                    value = selectedRatio,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Video Aspect Ratio") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRatio) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expandedRatio,
                    onDismissRequest = { expandedRatio = false }
                ) {
                    ratios.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                selectedRatio = selectionOption
                                expandedRatio = false
                            }
                        )
                    }
                }
            }

            // Target Platform Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedPlatform,
                onExpandedChange = { expandedPlatform = !expandedPlatform }
            ) {
                OutlinedTextField(
                    value = selectedPlatform,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Target Platform") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPlatform) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expandedPlatform,
                    onDismissRequest = { expandedPlatform = false }
                ) {
                    platforms.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                selectedPlatform = selectionOption
                                expandedPlatform = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = durationSecs,
                onValueChange = { durationSecs = it },
                label = { Text("Part Length (seconds)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            Text("AI Cutting Preferences", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Enable Smart Cut", fontWeight = FontWeight.SemiBold)
                    Text("Uses AI backend to find natural pauses and scene changes for cleaner splits.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(checked = smartCutEnabled, onCheckedChange = { smartCutEnabled = it })
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Skip Silent Parts", fontWeight = FontWeight.SemiBold)
                    Text("Automatically trims dead air (requires advanced backend processing).", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(checked = skipSilentParts, onCheckedChange = { skipSilentParts = it })
            }
        }
    }
}
