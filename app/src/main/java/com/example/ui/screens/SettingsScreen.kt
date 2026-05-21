package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.SupportAgent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings & Info") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                Text(
                    text = "Developer Info",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 8.dp)
                )
            }

            item {
                ListItem(
                    headlineContent = { Text("Developer") },
                    supportingContent = { Text("Rehan97") },
                    leadingContent = {
                        Icon(Icons.Rounded.Code, contentDescription = null)
                    }
                )
            }

            item {
                ListItem(
                    headlineContent = { Text("LinkedIn") },
                    supportingContent = { Text("Connect on LinkedIn") },
                    leadingContent = {
                        Icon(Icons.Rounded.Link, contentDescription = null)
                    },
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/rehan-ahmad-863386382?utm_source=share_via&utm_content=profile&utm_medium=member_android"))
                        context.startActivity(intent)
                    }
                )
            }

            item {
                Divider(modifier = Modifier.padding(vertical = 16.dp))
                
                Text(
                    text = "Support & Documentation",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                )
            }

            item {
                ListItem(
                    headlineContent = { Text("Documentation") },
                    supportingContent = { Text("App guides and API info") },
                    leadingContent = {
                        Icon(Icons.Rounded.Description, contentDescription = null)
                    },
                    modifier = Modifier.clickable { /* TODO */ }
                )
            }
            
            item {
                ListItem(
                    headlineContent = { Text("Support") },
                    supportingContent = { Text("Get help or report issues") },
                    leadingContent = {
                        Icon(Icons.Rounded.SupportAgent, contentDescription = null)
                    },
                    modifier = Modifier.clickable { /* TODO */ }
                )
            }

            item {
                ListItem(
                    headlineContent = { Text("About App") },
                    supportingContent = { Text("Version 1.0.0 (Pro Backend)") },
                    leadingContent = {
                        Icon(Icons.Rounded.Info, contentDescription = null)
                    }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(40.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text("ReelCut AI", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
