package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.FolderZip
import androidx.compose.material.icons.rounded.ContentCut
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
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.components.VideoPlayer
import kotlinx.coroutines.delay
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun saveDummyVideoToGallery(context: Context, filename: String, sourceUri: Uri? = null) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/Reels")
        }
        try {
            val uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                resolver.openOutputStream(uri)?.use { outputStream ->
                    if (sourceUri != null) {
                        resolver.openInputStream(sourceUri)?.use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    } else {
                        outputStream.write(byteArrayOf(0))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

enum class ProcessingState {
    IDLE, UPLOADING, COMPLETE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(
    onNavigateToSettings: () -> Unit
) {
    val context = LocalContext.current
    data class ReelPart(val title: String, val startSecs: Int)

    var state by remember { mutableStateOf(ProcessingState.IDLE) }
    var progress by remember { mutableFloatStateOf(0f) }
    var cutPoints by remember { mutableStateOf<List<ReelPart>>(emptyList()) }
    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }
    
    // Cutting options
    val durationOptions = listOf(60, 90, 120, 150)
    var selectedDuration by remember { mutableStateOf(60) }
    
    val aspectOptions = listOf("9:16 (Reel)", "16:9 (YT)", "1:1 (Insta)")
    var selectedAspect by remember { mutableStateOf(aspectOptions[0]) }
    
    // Playback and UI state
    var previewSeekSeconds by remember { mutableStateOf<Int?>(null) }
    var activeReelIndex by remember { mutableStateOf<Int?>(null) }
    var currentPlaybackPositionMs by remember { mutableStateOf(0L) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedVideoUri = uri
            state = ProcessingState.IDLE // reset state
            cutPoints = emptyList()
        }
    }

    LaunchedEffect(state) {
        when (state) {
            ProcessingState.UPLOADING -> {
                for (i in 1..100) {
                    delay(20)
                    progress = i / 100f
                }
                
                // Simulate cutting based on selected duration
                // Changing the mock duration to 1 hour (3600 seconds) 
                val totalMockVideoLength = 3600 
                val pts = mutableListOf<ReelPart>()
                var currentSec = 0
                var partNum = 1
                while (currentSec < totalMockVideoLength) {
                    val startStr = String.format("%02d:%02d:%02d", currentSec/3600, (currentSec%3600)/60, currentSec%60)
                    val endSec = currentSec + selectedDuration
                    val endStr = String.format("%02d:%02d:%02d", endSec/3600, (endSec%3600)/60, endSec%60)
                    pts.add(ReelPart("Reel $partNum ($startStr to $endStr)", currentSec))
                    currentSec += selectedDuration
                    partNum++
                }
                cutPoints = pts
                
                state = ProcessingState.COMPLETE
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Long Video to Reels Cutter") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Rounded.Settings, contentDescription = "Settings")
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
        ) {
            
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
                            "Upload Long Video",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Select a video to automatically split into perfectly sized reels.",
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
                val expectedRatio = when (selectedAspect) {
                    "9:16 (Reel)" -> 9f / 16f
                    "16:9 (YT)" -> 16f / 9f
                    "1:1 (Insta)" -> 1f
                    else -> 1f
                }
                Box(
                    modifier = Modifier.fillMaxWidth().height(350.dp).background(Color.Black, RoundedCornerShape(16.dp)).clip(RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    VideoPlayer(
                        videoUri = selectedVideoUri.toString(), 
                        seekToSeconds = previewSeekSeconds, 
                        onPositionChanged = { ms -> currentPlaybackPositionMs = ms },
                        modifier = Modifier.fillMaxHeight().aspectRatio(expectedRatio)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))

                if (state == ProcessingState.IDLE) {
                    // Split configuration
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Shape and Size", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Select output aspect ratio:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            val aspectOptionsLocal = listOf("9:16 (Reel)", "16:9 (YT)", "1:1 (Insta)")

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                aspectOptionsLocal.forEach { aspect ->
                                    val isSelected = selectedAspect == aspect
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                        border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
                                        modifier = Modifier.weight(1f).clickable { selectedAspect = aspect }
                                    ) {
                                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 12.dp)) {
                                            Text(
                                                aspect, 
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                                textAlign = TextAlign.Center,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Text("Split Duration", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Cut the entire video into segments of:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                durationOptions.forEach { duration ->
                                    val isSelected = selectedDuration == duration
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                        border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
                                        modifier = Modifier.weight(1f).clickable { selectedDuration = duration }
                                    ) {
                                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 12.dp)) {
                                            Text(
                                                "${duration}s", 
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface 
                                            )
                                        }
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Button(
                                onClick = { state = ProcessingState.UPLOADING },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(Icons.Rounded.ContentCut, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("SPLIT INTO REELS", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
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
                                "Splitting Video into ${selectedDuration}s Reels...",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
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
                                    "Split Complete",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Generated Reels (${selectedDuration}s each):", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            cutPoints.forEachIndexed { index, part ->
                                val isActive = activeReelIndex == index
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.background
                                    ),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    border = BorderStroke(1.dp, if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
                                ) {
                                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(50.dp).clip(RoundedCornerShape(8.dp)).background(if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer), contentAlignment = Alignment.Center) {
                                            Icon(Icons.Rounded.Movie, contentDescription = null, tint = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer)
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Part ${index + 1}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge, color = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onBackground)
                                            Text(part.title, style = MaterialTheme.typography.bodySmall, color = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        IconButton(onClick = {
                                            activeReelIndex = index
                                            previewSeekSeconds = part.startSecs
                                            scope.launch { snackbarHostState.showSnackbar("Playing Part ${index + 1} from ${part.startSecs}s") }
                                        }) {
                                            Icon(Icons.Rounded.PlayArrow, contentDescription = "Play", tint = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        IconButton(onClick = {
                                            scope.launch { 
                                                snackbarHostState.showSnackbar("Downloading Part ${index + 1}.mp4 to Movies/Reels...") 
                                                withContext(Dispatchers.IO) {
                                                    saveDummyVideoToGallery(context, "Reel_Part_${index + 1}.mp4", selectedVideoUri)
                                                }
                                                snackbarHostState.showSnackbar("Saved in Gallery (Movies/Reels)") 
                                            }
                                        }) {
                                            Icon(Icons.Rounded.Download, contentDescription = "Download", tint = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = {
                                    scope.launch { 
                                        snackbarHostState.showSnackbar("Saving ${cutPoints.size} reels to target folder...") 
                                        withContext(Dispatchers.IO) {
                                            cutPoints.forEachIndexed { i, _ ->
                                                saveDummyVideoToGallery(context, "Reel_Part_${i + 1}.mp4", selectedVideoUri)
                                            }
                                        }
                                        snackbarHostState.showSnackbar("All reels saved in Gallery (Movies/Reels)!") 
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(50)
                            ) {
                                Icon(Icons.Rounded.FolderZip, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("DOWNLOAD ALL", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
