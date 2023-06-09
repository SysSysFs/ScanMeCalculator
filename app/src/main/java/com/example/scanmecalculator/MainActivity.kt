package com.example.scanmecalculator

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.scanmecalculator.domain.repository.StorageType
import com.example.scanmecalculator.navigation.Route
import com.example.scanmecalculator.presentation.camera.CameraScreen
import com.example.scanmecalculator.presentation.home.HomeScreen
import com.example.scanmecalculator.presentation.image_picker.ImagePickerResultScreen
import com.example.scanmecalculator.presentation.ui.theme.ScanMeCalculatorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScanMeCalculatorTheme {
                val navController = rememberNavController()
                val scaffoldState = rememberScaffoldState()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    scaffoldState = scaffoldState
                )
                { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = Route.HOME
                    ) {
                        composable(Route.HOME) {
                            HomeScreen(
                                onLaunchCamera = { storageType->
                                    navController.navigate(Route.CAMERA+ "/$storageType")
                                },
                                onImageSelected = { pair ->
                                    val fileUri = pair.first
                                    val storageType = pair.second
                                    navController.navigate(
                                        Route.IMAGE_PICKER_RESULT
                                                + "/${Uri.encode(fileUri.toString())}"
                                                + "/$storageType"
                                    )
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        composable(route = Route.CAMERA + "/{storageType}",
                            arguments = listOf(
                                navArgument("storageType") {
                                    type = NavType.StringType
                                }
                            ))
                        {
                            val storageTypeString = it.arguments?.getString("storageType")
                            val storageType = StorageType.values()
                                .firstOrNull { storageType -> storageType.label == storageTypeString }
                                ?: StorageType.FILE
                            CameraScreen(
                                storageType = storageType,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        composable(route = Route.IMAGE_PICKER_RESULT + "/{fileUri}/{storageType}",
                            arguments = listOf(
                                navArgument("fileUri") {
                                    type = NavType.StringType
                                },
                                navArgument("storageType") {
                                    type = NavType.StringType
                                }
                            )
                        ) {
                            val fileUriString =
                                it.arguments?.getString("fileUri") ?: emptyImageUri.toString()
                            val fileUri = Uri.parse(fileUriString)

                            val storageTypeString = it.arguments?.getString("storageType")
                            val storageType = StorageType.values()
                                .firstOrNull { storageType -> storageType.label == storageTypeString }
                                ?: StorageType.FILE

                            ImagePickerResultScreen(
                                storageType = storageType,
                                imageUri = fileUri,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }

    companion object {
        val emptyImageUri: Uri = Uri.parse("file://dev/null")
    }
}

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}