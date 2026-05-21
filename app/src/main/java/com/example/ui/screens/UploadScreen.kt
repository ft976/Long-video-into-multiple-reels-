package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.ContentCut
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.CutTemplate
import com.example.data.TemplateRepository
import com.example.ui.components.VideoPlayer
import kotlinx.coroutines.delay

enum class ProcessingState {
    IDLE, UPLOADING, ANALYZING, COMPLETE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(
    templateId: Int,
    repository: TemplateRepository,
    onNavigateBack: () -> Unit
) {
    var template by remember { mutableStateOf<CutTemplate?>(null) }
    var state by remember { mutableStateOf(ProcessingState.IDLE) }
    var progress by remember { mutableFloatStateOf(0f) }
    var cutPoints by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }
    var manualMode by remember { mutableStateOf(false) }

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedVideoUri = uri
            state = ProcessingState.IDLE // reset state
        }
    }

    LaunchedEffect(templateId) {
        template = repository.getTemplate(templateId)
    }

    LaunchedEffect(state) {
        when (state) {
            ProcessingState.UPLOADING -> {
                for (i in 1..100) {
                    delay(20)
                    progress = i / 100f
                }
                state = ProcessingState.ANALYZING
            }
            ProcessingState.ANALYZING -> {
                delay(2000)
                // Simulate smart scene detection algorithm
                cutPoints = listOf(
                    "00:00:15 - Fast motion detected",
                    "00:00:42 - Audio volume spike (Highlight)",
                    "00:01:20 - Strong scene transition",
                    "00:02:05 - Visual contrast change",
                    "00:03:15 - High action detected"
                )
                state = ProcessingState.COMPLETE
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upload Video") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            // verticalArrangement = Arrangement.Center
        ) {
            template?.let { t ->
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Active Template", style = MaterialTheme.typography.labelMedium)
                        Text(t.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Output: ${t.aspectRatio} | ${t.durationSecs}s | SmartCut: ${t.smartCutEnabled}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))

                if (selectedVideoUri == null) {
                    Surface(
                        shape = RoundedCornerShape(28.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                border = BorderStroke(8.dp, MaterialTheme.colorScheme.surface),
                                modifier = Modifier
                                    .size(80.dp)
                                    .padding(bottom = 8.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Rounded.CloudUpload,
                                        contentDescription = null,
                                        modifier = Modifier.size(36.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Text(
                                "Upload Large Assets",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Select a video from your device for processing.",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { 
                                    pickMedia.launch("video/*")
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text("SELECT VIDEO", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Video Preview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        TextButton(onClick = {
                            pickMedia.launch("video/*")
                        }) {
                            Text("Change Video")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Video Player Preview
                    Box(modifier = Modifier.fillMaxWidth().height(250.dp).clip(RoundedCornerShape(16.dp)).background(Color.Black)) {
                        VideoPlayer(videoUri = selectedVideoUri.toString(), modifier = Modifier.fillMaxSize())
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    if (state == ProcessingState.IDLE) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            OutlinedButton(
                                onClick = { 
                                    manualMode = !manualMode
                                },
                                modifier = Modifier.weight(1f).height(48.dp)
                            ) {
                                Icon(Icons.Rounded.ContentCut, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Manual Cut", fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { 
                                    manualMode = false
                                    state = ProcessingState.UPLOADING 
                                },
                                modifier = Modifier.weight(1f).height(48.dp)
                            ) {
                                Icon(Icons.Rounded.AutoFixHigh, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("AI Auto Cut", fontWeight = FontWeight.Bold)
                            }
                        }

                        if (manualMode) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Manual Cutting Tools", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text("Video starts at: 00:00:00", style = MaterialTheme.typography.bodyMedium)
                                    Slider(value = 0f, onValueChange = {}, enabled = false) // Simulated timeline
                                    Text("Video ends at: --:--:--", style = MaterialTheme.typography.bodyMedium)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(onClick = { /* Simulate Save */ }, modifier = Modifier.fillMaxWidth()) {
                                        Text("Save Segments")
                                    }
                                }
                            }
                        }

                    } else if (state == ProcessingState.UPLOADING) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(24.dp)
                            ) {
                                LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Processing Video...",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else if (state == ProcessingState.ANALYZING) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(24.dp)
                            ) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Smart Scene Detection Running...",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Analyzing video frames for scene transitions, motion blur, and audio spikes. Utilizing fast backend...",
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else if (state == ProcessingState.COMPLETE) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = Color.Green)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Analysis Complete",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Generated Reels (${t.aspectRatio}, ${t.durationSecs}s):", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                cutPoints.forEachIndexed { index, point ->
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                    ) {
                                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Box(modifier = Modifier.size(50.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.secondaryContainer), contentAlignment = Alignment.Center) {
                                                Icon(Icons.Rounded.Movie, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text("Reel Part ${index + 1}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                                Text(point, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                            IconButton(onClick = { /* play/preview this reel */ }) {
                                                Icon(Icons.Rounded.PlayArrow, contentDescription = "Play")
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = { onNavigateBack() },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text("APPROVE & GENERATE", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
