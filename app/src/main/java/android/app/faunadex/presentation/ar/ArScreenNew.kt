package android.app.faunadex.presentation.ar

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.PixelCopy
import android.app.faunadex.R
import android.app.faunadex.ui.theme.DarkGreen
import android.app.faunadex.ui.theme.JerseyFont
import android.app.faunadex.ui.theme.PastelYellow
import android.app.faunadex.ui.theme.PrimaryGreen
import android.app.faunadex.ui.theme.PrimaryGreenLime
import android.app.faunadex.ui.theme.White
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Rotate90DegreesCw
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL
import java.security.MessageDigest

private const val TAG = "ArScreenNew"

private const val MODEL_SCALE = 1f

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ArScreenNew(
    onNavigateBack: () -> Unit = {},
    animalId: String? = null,
    viewModel: ArViewModel = hiltViewModel()
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
        Log.d(TAG, "ArScreenNew launched with animalId: $animalId")
        animalId?.let {
            Log.d(TAG, "Loading animal for AR: $it")
            viewModel.loadAnimalForAr(it)
        }
    }

    val sessionState by viewModel.sessionState.collectAsState()

    LaunchedEffect(sessionState.selectedAnimal) {
        Log.d(TAG, "Selected animal updated: ${sessionState.selectedAnimal?.name}, URL: ${sessionState.selectedAnimal?.arModelUrl}")
    }

    if (cameraPermissionState.status.isGranted) {
        ArContent(
            sessionState = sessionState,
            onNavigateBack = onNavigateBack
        )
    } else {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.camera_permission_fail),
                color = White,
                fontSize = 18.sp
            )
        }
    }
}

enum class ArState {
    SCANNING,
    READY,
    PLACING,
    PLACED,
    ERROR
}

@Composable
private fun ArContent(
    sessionState: ArSessionState,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var arSceneView by remember { mutableStateOf<ARSceneView?>(null) }

    val animal = sessionState.selectedAnimal
    val modelUrl = animal?.arModelUrl
    val isAnimalLoaded = animal != null && !modelUrl.isNullOrBlank()

    LaunchedEffect(isAnimalLoaded) {
        Log.d(TAG, "Animal loaded: $isAnimalLoaded, name: ${animal?.name}")
    }

    var arState by remember { mutableStateOf(ArState.SCANNING) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var loadingProgress by remember { mutableStateOf("") }

    var isCapturing by remember { mutableStateOf(false) }
    var showCaptureSuccess by remember { mutableStateOf(false) }

    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    var childNodes by remember { mutableStateOf<List<AnchorNode>>(emptyList()) }

    var hasPlane by remember { mutableStateOf(false) }
    var currentFrame by remember { mutableStateOf<Frame?>(null) }

    var cachedModelPath by remember { mutableStateOf<String?>(null) }
    var isPreloading by remember { mutableStateOf(false) }

    LaunchedEffect(modelUrl) {
        if (modelUrl != null && cachedModelPath == null && !isPreloading) {
            isPreloading = true
            Log.d(TAG, "Pre-loading model in background: $modelUrl")
            try {
                val path = getOrDownloadModel(context, modelUrl) { progress ->
                    loadingProgress = progress
                }
                cachedModelPath = path
                loadingProgress = ""
                Log.d(TAG, "Model pre-loaded: $path")
            } catch (e: Exception) {
                Log.e(TAG, "Pre-load failed: ${e.message}")
            }
            isPreloading = false
        }
    }

    if (!isAnimalLoaded) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator(color = PrimaryGreenLime)
                Text(
                    text = if (animal == null) stringResource(R.string.ar_loading_animal_data) else stringResource(R.string.ar_no_3d_model_available),
                    color = White,
                    fontSize = 16.sp,
                    fontFamily = JerseyFont,
                    textAlign = TextAlign.Center
                )
                if (animal != null && modelUrl.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onNavigateBack,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                    ) {
                        Text(stringResource(R.string.ar_go_back), color = White)
                    }
                }
            }
        }
        return
    }

    val isModelPlaced = arState == ArState.PLACED

    Box(modifier = Modifier.fillMaxSize()) {
        ARScene(
            modifier = Modifier.fillMaxSize(),
            engine = engine,
            modelLoader = modelLoader,
            childNodes = childNodes,
            planeRenderer = !isModelPlaced,
            onViewUpdated = {
                if (arSceneView == null) {
                    arSceneView = this
                }
            },
            sessionConfiguration = { session, config ->
                config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
                config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
                config.focusMode = Config.FocusMode.AUTO
                if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                    config.depthMode = Config.DepthMode.AUTOMATIC
                }
                config.instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP
            },
            onSessionUpdated = { session, frame ->
                currentFrame = frame

                val planes = session.getAllTrackables(Plane::class.java)
                    .filter { it.trackingState == TrackingState.TRACKING }

                if (planes.isNotEmpty() && !hasPlane) {
                    hasPlane = true
                    if (arState == ArState.SCANNING) {
                        arState = ArState.READY
                        Log.d(TAG, "Plane detected (${planes.size} planes)")
                    }
                }

                if (!hasPlane && arState == ArState.SCANNING) {
                    val timestamp = frame.timestamp
                    if (timestamp > 3_000_000_000L) {
                        hasPlane = true
                        arState = ArState.READY
                        Log.d(TAG, "Auto-enabled placement")
                    }
                }
            },
            onGestureListener = rememberOnGestureListener(
                onSingleTapConfirmed = { motionEvent, node ->
                    if (arState != ArState.READY) {
                        Log.d(TAG, "Tap blocked - state: $arState")
                        return@rememberOnGestureListener
                    }

                    if (isPreloading && cachedModelPath == null) {
                        Log.d(TAG, "Tap blocked - model still downloading")
                        return@rememberOnGestureListener
                    }

                    val frame = currentFrame ?: return@rememberOnGestureListener

                    Log.d(TAG, "Tap at ${motionEvent.x}, ${motionEvent.y}")

                    val hitResults = frame.hitTest(motionEvent.x, motionEvent.y)

                    var anchor: com.google.ar.core.Anchor? = null
                    var anchorSource = "unknown"

                    val planeHit = hitResults.firstOrNull { hit ->
                        hit.trackable is Plane && hit.trackable.trackingState == TrackingState.TRACKING
                    }

                    if (planeHit != null) {
                        anchor = planeHit.createAnchor()
                        anchorSource = "Plane"
                    } else {
                        val depthHit = hitResults.firstOrNull { hit ->
                            hit.trackable?.trackingState == TrackingState.TRACKING
                        }
                        if (depthHit != null) {
                            anchor = depthHit.createAnchor()
                            anchorSource = depthHit.trackable?.javaClass?.simpleName ?: "DepthPoint"
                        } else {
                            val instantHits = frame.hitTestInstantPlacement(motionEvent.x, motionEvent.y, 2.0f)
                            instantHits.firstOrNull()?.let { hit ->
                                anchor = hit.createAnchor()
                                anchorSource = "instant_placement"
                            }
                        }
                    }

                    if (anchor == null) {
                        Log.e(TAG, "No anchor created")
                        return@rememberOnGestureListener
                    }

                    val placementAnchor = anchor!!

                    Log.d(TAG, "Anchor: $anchorSource")
                    arState = ArState.PLACING

                    childNodes.forEach { node ->
                        try { node.anchor.detach(); node.destroy() } catch (_: Exception) {}
                    }
                    childNodes = emptyList()

                    scope.launch {
                        try {
                            val modelPath = cachedModelPath ?: run {
                                loadingProgress = "Loading model..."
                                getOrDownloadModel(context, modelUrl) { loadingProgress = it }
                            }

                            loadingProgress = "Rendering..."

                            var modelInstance = modelLoader.loadModelInstance(modelPath)

                            if (modelInstance == null) {
                                Log.w(TAG, "First load attempt failed, retrying...")
                                kotlinx.coroutines.delay(100)
                                modelInstance = modelLoader.loadModelInstance(modelPath)
                            }

                            if (modelInstance != null) {
                                val modelNode = ModelNode(
                                    modelInstance = modelInstance,
                                    scaleToUnits = MODEL_SCALE
                                ).apply {
                                    isEditable = true
                                    position = position.copy(y = position.y + 0.001f)
                                }

                                val anchorNode = AnchorNode(
                                    engine = engine,
                                    anchor = placementAnchor
                                ).apply {
                                    addChildNode(modelNode)
                                }

                                childNodes = listOf(anchorNode)
                                arState = ArState.PLACED
                                loadingProgress = ""
                                Log.d(TAG, "Model placed from $anchorSource")
                            } else {
                                cachedModelPath?.let { path ->
                                    File(path.removePrefix("file://")).delete()
                                    cachedModelPath = null
                                }
                                throw Exception("Model failed to load. Tap to retry.")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error: ${e.message}")
                            errorMessage = e.message
                            arState = ArState.ERROR
                            try { placementAnchor.detach() } catch (_: Exception) {}
                        }
                    }
                }
            )
        )

        ArOverlay(
            arState = arState,
            loadingProgress = loadingProgress,
            errorMessage = errorMessage,
            animalName = animal.name,
            scientificName = animal.scientificName,
            isPreloading = isPreloading && cachedModelPath == null,
            isCapturing = isCapturing,
            showCaptureSuccess = showCaptureSuccess,
            onNavigateBack = onNavigateBack,
            onClear = {
                childNodes.forEach { node ->
                    try { node.anchor.detach(); node.destroy() } catch (_: Exception) {}
                }
                childNodes = emptyList()
                arState = if (hasPlane) ArState.READY else ArState.SCANNING
                errorMessage = null
            },
            onRetry = {
                errorMessage = null
                arState = if (hasPlane) ArState.READY else ArState.SCANNING
            },
            onCapture = {
                scope.launch {
                    isCapturing = true
                    try {
                        val sceneView = arSceneView
                        if (sceneView != null) {
                            captureArScreenshot(context, sceneView)
                            showCaptureSuccess = true
                            kotlinx.coroutines.delay(2000L)
                            showCaptureSuccess = false
                        } else {
                            Log.e(TAG, "AR SceneView not available for capture")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Capture failed: ${e.message}")
                    }
                    isCapturing = false
                }
            }
        )
    }
}

@Composable
private fun BoxScope.ArOverlay(
    arState: ArState,
    loadingProgress: String,
    errorMessage: String?,
    animalName: String,
    scientificName: String,
    isPreloading: Boolean,
    isCapturing: Boolean,
    showCaptureSuccess: Boolean,
    onNavigateBack: () -> Unit,
    onClear: () -> Unit,
    onRetry: () -> Unit,
    onCapture: () -> Unit
) {
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues()
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()

    IconButton(
        onClick = onNavigateBack,
        modifier = Modifier
            .align(Alignment.TopStart)
            .padding(top = statusBarPadding.calculateTopPadding() + 8.dp, start = 8.dp)
            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = stringResource(R.string.ar_back),
            tint = White,
            modifier = Modifier.size(32.dp)
        )
    }

    when (arState) {
        ArState.SCANNING -> {
            StatusCard(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = statusBarPadding.calculateTopPadding() + 60.dp),
                text = stringResource(R.string.ar_scanning_for_surfaces),
                subText = stringResource(R.string.ar_point_at_surface_hint)
            )
        }

        ArState.READY -> {
            if (isPreloading) {
                StatusCard(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = navBarPadding.calculateBottomPadding() + 24.dp),
                    text = stringResource(R.string.ar_downloading_model, animalName),
                    subText = if (loadingProgress.isNotBlank()) loadingProgress else stringResource(R.string.ar_please_wait),
                    backgroundColor = Color.DarkGray
                )
            } else {
                StatusCard(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = navBarPadding.calculateBottomPadding() + 24.dp),
                    text = stringResource(R.string.ar_tap_to_place_named, animalName),
                    subText = stringResource(R.string.ar_tap_where_to_place),
                    backgroundColor = PrimaryGreen
                )
            }
        }

        ArState.PLACING -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = PrimaryGreenLime)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = loadingProgress,
                            color = White,
                            fontSize = 16.sp,
                            fontFamily = JerseyFont
                        )
                    }
                }
            }
        }

        ArState.PLACED -> {
            var showSuccessMessage by remember { mutableStateOf(true) }
            var showGestureHint by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(2000L)
                showSuccessMessage = false
                showGestureHint = true
                kotlinx.coroutines.delay(5000L)
                showGestureHint = false
            }

            AnimatedVisibility(
                visible = showSuccessMessage,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = statusBarPadding.calculateTopPadding() + 60.dp)
            ) {
                Surface(
                    color = PrimaryGreen.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(64.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = White,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = stringResource(R.string.ar_animal_placed),
                            color = White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = !showSuccessMessage,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = statusBarPadding.calculateTopPadding() + 60.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Surface(
                    color = Color.Black.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(64.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = animalName,
                            color = PastelYellow,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = JerseyFont,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = scientificName,
                            color = Color(0xFF8FBC8F),
                            fontSize = 16.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            fontFamily = JerseyFont,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = showGestureHint,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = navBarPadding.calculateBottomPadding() + 150.dp)
            ) {
                Surface(
                    color = Color.Black.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.ar_interact_with_your_model),
                            color = PastelYellow,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = JerseyFont
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ZoomOutMap,
                                    contentDescription = null,
                                    tint = White,
                                    modifier = Modifier.size(28.dp)
                                )
                                Text(
                                    text = stringResource(R.string.ar_pinch_zoom),
                                    color = White.copy(alpha = 0.8f),
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Rotate90DegreesCw,
                                    contentDescription = null,
                                    tint = White,
                                    modifier = Modifier.size(28.dp)
                                )
                                Text(
                                    text = stringResource(R.string.ar_two_finger_rotate),
                                    color = White.copy(alpha = 0.8f),
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OpenWith,
                                    contentDescription = null,
                                    tint = White,
                                    modifier = Modifier.size(28.dp)
                                )
                                Text(
                                    text = stringResource(R.string.ar_drag_to_move),
                                    color = White.copy(alpha = 0.8f),
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = showCaptureSuccess,
                modifier = Modifier
                    .align(Alignment.Center)
            ) {
                Surface(
                    color = PrimaryGreen.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = White,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = stringResource(R.string.ar_photo_saved_to_gallery),
                            color = White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = JerseyFont
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = navBarPadding.calculateBottomPadding())
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onClear,
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color.Red.copy(alpha = 0.8f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay,
                        contentDescription = stringResource(R.string.ar_reset),
                        tint = White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(White, CircleShape)
                        .border(4.dp, PrimaryGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = onCapture,
                        enabled = !isCapturing,
                        modifier = Modifier.size(48.dp)
                    ) {
                        if (isCapturing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = PrimaryGreen,
                                strokeWidth = 3.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = stringResource(R.string.ar_capture),
                                tint = DarkGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }

        ArState.ERROR -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.9f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.error),
                            color = White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage ?: stringResource(R.string.error_unknown),
                            color = White.copy(alpha = 0.9f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onRetry,
                            colors = ButtonDefaults.buttonColors(containerColor = White)
                        ) {
                            Text(stringResource(R.string.retry), color = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusCard(
    modifier: Modifier = Modifier,
    text: String,
    subText: String? = null,
    backgroundColor: Color = Color.Black.copy(alpha = 0.7f)
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = text,
                color = White,
                fontSize = 16.sp,
                fontFamily = JerseyFont,
                textAlign = TextAlign.Center
            )
            if (subText != null) {
                Text(
                    text = subText,
                    color = White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private suspend fun getOrDownloadModel(
    context: Context,
    url: String,
    onProgress: (String) -> Unit
): String = withContext(Dispatchers.IO) {
    val cacheDir = File(context.cacheDir, "ar_models")
    if (!cacheDir.exists()) cacheDir.mkdirs()

    val fileName = url.md5() + ".glb"
    val cachedFile = File(cacheDir, fileName)

    if (cachedFile.exists() && isValidGlbFile(cachedFile)) {
        Log.d(TAG, "Loading from cache: ${cachedFile.absolutePath} (${cachedFile.length()} bytes)")
        onProgress(context.getString(R.string.ar_loading_from_cache))
        return@withContext "file://${cachedFile.absolutePath}"
    } else if (cachedFile.exists()) {
        Log.w(TAG, "Cached file is invalid, deleting: ${cachedFile.absolutePath}")
        cachedFile.delete()
    }

    Log.d(TAG, "Downloading model: $url")
    onProgress(context.getString(R.string.ar_downloading))

    try {
        val connection = URL(url).openConnection()
        connection.connectTimeout = 30000
        connection.readTimeout = 120000

        val totalSize = connection.contentLength
        var downloaded = 0L

        connection.getInputStream().use { input ->
            cachedFile.outputStream().use { output ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    output.write(buffer, 0, bytesRead)
                    downloaded += bytesRead
                    if (totalSize > 0) {
                        val percent = (downloaded * 100 / totalSize).toInt()
                        onProgress("${context.getString(R.string.ar_downloading)} $percent%")
                    }
                }
            }
        }

        if (!isValidGlbFile(cachedFile)) {
            cachedFile.delete()
            throw Exception("Downloaded file is not a valid GLB model")
        }

        Log.d(TAG, "Download complete: ${cachedFile.length()} bytes")
        onProgress(context.getString(R.string.ar_download_complete))

        "file://${cachedFile.absolutePath}"
    } catch (e: Exception) {
        cachedFile.delete()
        throw e
    }
}

private fun isValidGlbFile(file: File): Boolean {
    if (!file.exists() || file.length() < 12) return false

    return try {
        file.inputStream().use { input ->
            val header = ByteArray(4)
            if (input.read(header) != 4) return@use false

            val magic = (header[0].toInt() and 0xFF) or
                       ((header[1].toInt() and 0xFF) shl 8) or
                       ((header[2].toInt() and 0xFF) shl 16) or
                       ((header[3].toInt() and 0xFF) shl 24)

            val isValid = magic == 0x46546C67
            if (!isValid) {
                Log.w(TAG, "Invalid GLB magic: ${header.contentToString()}, expected glTF")
            }
            isValid
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error validating GLB file: ${e.message}")
        false
    }
}

private fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    return md.digest(toByteArray()).joinToString("") { "%02x".format(it) }
}

@Composable
private fun rememberOnGestureListener(
    onSingleTapConfirmed: (android.view.MotionEvent, io.github.sceneview.node.Node?) -> Unit
) = io.github.sceneview.rememberOnGestureListener(
    onSingleTapConfirmed = onSingleTapConfirmed
)

private suspend fun captureArScreenshot(context: Context, arSceneView: ARSceneView) = withContext(Dispatchers.Main) {
    try {
        val bitmap = Bitmap.createBitmap(
            arSceneView.width,
            arSceneView.height,
            Bitmap.Config.ARGB_8888
        )

        val copyResult = suspendCancellableCoroutine { continuation ->
            PixelCopy.request(
                arSceneView,
                bitmap,
                { result: Int ->
                    continuation.resume(result)
                },
                android.os.Handler(android.os.Looper.getMainLooper())
            )
        }

        if (copyResult != PixelCopy.SUCCESS) {
            Log.e(TAG, "PixelCopy failed with code: $copyResult")
            throw Exception("Failed to capture AR scene (error code: $copyResult)")
        }

        withContext(Dispatchers.IO) {
            saveBitmapToGallery(context, bitmap)
        }

        Log.d(TAG, "AR Screenshot captured and saved (${bitmap.width}x${bitmap.height})")
    } catch (e: Exception) {
        Log.e(TAG, "Failed to capture AR screenshot: ${e.message}")
        throw e
    }
}

private fun saveBitmapToGallery(context: Context, bitmap: Bitmap) {
    val filename = "WildAR!_AR_${System.currentTimeMillis()}.jpg"

    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/WildAR!")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    uri?.let {
        resolver.openOutputStream(it)?.use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)
        }

        Log.d(TAG, "Image saved to gallery: $uri")
    } ?: throw Exception("Failed to create media entry")
}
