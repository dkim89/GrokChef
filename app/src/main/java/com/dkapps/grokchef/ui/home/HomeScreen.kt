package com.dkapps.grokchef.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.dkapps.grokchef.BuildConfig
import com.dkapps.grokchef.R
import com.dkapps.grokchef.ui.shared.GrokIcon
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigate: (String) -> Unit
) {
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // This function creates a temporary file and returns a URI for it.
    fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile(
            "tmp_image_file",
            ".png",
            context.cacheDir
        ).apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(
            context,
            "${BuildConfig.APPLICATION_ID}.provider",
            tmpFile
        )
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                if (tempImageUri != null) {
                    onNavigate(tempImageUri.toString())
                }
            }
        }
    )

    val launchCameraAction = { uri: Uri ->
        tempImageUri = uri
        cameraLauncher.launch(uri)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // Permission granted, launch the camera.
                launchCameraAction(getTmpFileUri())
            } else {
                // Permission denied, show a message to the user.
                scope.launch {
                    snackbarHostState.showSnackbar("Camera permission is required to take photos.")
                }
            }
        }
    )

    val onCameraFabClick = {
        val cameraPermissionState =
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        when (cameraPermissionState) {
            PackageManager.PERMISSION_GRANTED -> {
                launchCameraAction(getTmpFileUri())
            }
            else -> {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            // Only show the FAB if no image has been taken yet.
            if (tempImageUri == null) {
                CameraButton(onClick = onCameraFabClick)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.title),
                style = MaterialTheme.typography.titleLarge,
            )
            GrokIcon(modifier = Modifier.fillMaxWidth())
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = stringResource(R.string.capture_prompt),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
fun CameraButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    FloatingActionButton(onClick = onClick, modifier = modifier) {
        Icon(Icons.Filled.CameraAlt, contentDescription = stringResource(R.string.capture_image_button))
    }
}