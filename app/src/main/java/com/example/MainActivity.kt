package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.TemplateRepository
import com.example.ui.TemplatesViewModel
import com.example.ui.TemplatesViewModelFactory
import com.example.ui.screens.CreateTemplateScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.UploadScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize Room DB
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "reelcutter-db"
        ).build()
        val repository = TemplateRepository(db.templateDao())

        setContent {
            AppTheme {
                val navController = rememberNavController()
                val viewModel: TemplatesViewModel = viewModel(
                    factory = TemplatesViewModelFactory(repository)
                )

                NavHost(navController = navController, startDestination = "dashboard") {
                    composable("dashboard") {
                        DashboardScreen(
                            viewModel = viewModel,
                            onNavigateToCreate = { navController.navigate("create") },
                            onNavigateToUpload = { templateId -> 
                                navController.navigate("upload/$templateId")
                            },
                            onNavigateToSettings = { navController.navigate("settings") }
                        )
                    }
                    composable("settings") {
                        SettingsScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable("create") {
                        CreateTemplateScreen(
                            viewModel = viewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable(
                        route = "upload/{templateId}",
                        arguments = listOf(navArgument("templateId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getInt("templateId") ?: return@composable
                        UploadScreen(
                            templateId = id,
                            repository = repository,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
