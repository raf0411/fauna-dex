package android.app.faunadex.presentation.ar

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.app.faunadex.R
import android.app.faunadex.ui.theme.DarkGreen
import android.app.faunadex.ui.theme.FaunaDexTheme
import android.app.faunadex.ui.theme.JerseyFont
import android.app.faunadex.ui.theme.MediumGreenSage
import android.app.faunadex.ui.theme.PastelYellow
import android.app.faunadex.ui.theme.PrimaryGreen
import android.app.faunadex.ui.theme.PrimaryGreenLight
import android.app.faunadex.ui.theme.PrimaryGreenLime
import android.app.faunadex.ui.theme.White
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CenterFocusWeak
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Rotate90DegreesCw
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.getUpdatedPlanes
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberOnGestureListener
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import android.app.faunadex.utils.ModelCache
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ArScreen(
    onNavigateBack: () -> Unit = {},
    animalId: String? = null,
    viewModel: ArViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val sessionState by viewModel.sessionState.collectAsState()

    val cameraPermissionState = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    ) { isGranted ->
        if (isGranted) {
            viewModel.onPermissionGranted()
            viewModel.startScanning()
        } else {
            viewModel.onPermissionDenied()
        }
    }

    LaunchedEffect(animalId) {
        animalId?.let { viewModel.loadAnimalForAr(it) }
    }

    LaunchedEffect(Unit) {
        if (cameraPermissionState.status.isGranted) {
            viewModel.onPermissionGranted()
            viewModel.startScanning()
        } else {
            cameraPermissionState.launchPermissionRequest()
        }
    }


    if (cameraPermissionState.status.isGranted &&
        (uiState is ArUiState.Ready || uiState is ArUiState.Scanning || uiState is ArUiState.AnimalPlaced)) {
        ArCameraContent(
            uiState = uiState,
            sessionState = sessionState,
            onNavigateBack = onNavigateBack,
            onPlaneDetected = viewModel::onPlaneDetected,
            onAnimalPlaced = { animal ->
                viewModel.placeAnimal(animal, 0f, 0f, 0f)
            },
            onClearAnimals = viewModel::clearPlacedAnimals
        )
    } else {
        ArScreenContent(
            uiState = uiState,
            sessionState = sessionState,
            onNavigateBack = onNavigateBack,
            onRequestPermission = {
                cameraPermissionState.launchPermissionRequest()
            },
            onClearAnimals = viewModel::clearPlacedAnimals,
            showRationale = cameraPermissionState.status.shouldShowRationale
        )
    }
}

private const val DUMMY_MODEL_URL = "https://ampugrpczxyluircynug.supabase.co/storage/v1/object/public/wildar-3d-models/models/dummy/dummy_animal.glb"

@Suppress("UNUSED_PARAMETER")
@Composable
fun ArCameraContent(
    uiState: ArUiState,
    sessionState: ArSessionState,
    onNavigateBack: () -> Unit,
    onPlaneDetected: (Int) -> Unit,
    onAnimalPlaced: (android.app.faunadex.domain.model.Animal) -> Unit,
    onClearAnimals: () -> Unit
) {
    val currentSessionState by rememberUpdatedState(sessionState)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var planeCount by remember { mutableIntStateOf(0) }
    var isModelPlaced by remember { mutableStateOf(false) }
    var isModelLoading by remember { mutableStateOf(false) }
    var isTrackingLost by remember { mutableStateOf(false) }
    var modelLoadError by remember { mutableStateOf<String?>(null) }
    var modelLoadTimedOut by remember { mutableStateOf(false) }
    var childNodes by remember { mutableStateOf<List<AnchorNode>>(emptyList()) }

    var showCaptureSuccess by remember { mutableStateOf(false) }
    var isCapturing by remember { mutableStateOf(false) }
    var timeoutJob by remember { mutableStateOf<Job?>(null) }
    var modelLoadRetryCount by remember { mutableIntStateOf(0) }
    val maxRetries = 3

    // Track download progress for UI
    var isDownloading by remember { mutableStateOf(false) }
    var isCached by remember { mutableStateOf(false) }

    // Counter to debounce tracking lost state - only show lost after sustained loss
    var trackingLostCounter by remember { mutableIntStateOf(0) }
    val trackingLostThreshold = 30 // About 0.5 seconds at 60fps

    // SceneView 2.x - use rememberEngine and rememberModelLoader
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)

    LaunchedEffect(showCaptureSuccess) {
        if (showCaptureSuccess) {
            kotlinx.coroutines.delay(3000L)
            showCaptureSuccess = false
        }
    }

    // Clean up timeout job when leaving the screen
    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose {
            timeoutJob?.cancel()
            timeoutJob = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        var arFrame by remember { mutableStateOf<com.google.ar.core.Frame?>(null) }
        var arSession by remember { mutableStateOf<com.google.ar.core.Session?>(null) }

        // Function to handle tap and place model
        fun handleTapToPlace(motionEvent: android.view.MotionEvent) {
            Log.d("ArScreen", "Tap detected - isModelPlaced: $isModelPlaced, isModelLoading: $isModelLoading, planeCount: $planeCount")

            if (!isModelPlaced && !isModelLoading) {
                val currentFrame = arFrame
                if (currentFrame == null) {
                    Log.w("ArScreen", "No AR frame available for hit test")
                    return
                }

                // Get tap coordinates
                val x = motionEvent.x
                val y = motionEvent.y
                Log.d("ArScreen", "Tap coordinates: x=$x, y=$y")

                // Try hit test with coordinates
                val hitResults = currentFrame.hitTest(x, y)
                Log.d("ArScreen", "Hit test returned ${hitResults.size} results")

                // Log what types of trackables we're hitting
                hitResults.forEachIndexed { index, hit ->
                    val trackable = hit.trackable
                    Log.d("ArScreen", "Hit $index: type=${trackable?.javaClass?.simpleName}, tracking=${trackable?.trackingState}")
                }

                // Priority 1: Accept Plane hits (best quality)
                var validHit = hitResults.firstOrNull { hit ->
                    val trackable = hit.trackable
                    trackable is Plane && trackable.trackingState == TrackingState.TRACKING
                }

                // Priority 2: Accept DepthPoint hits (good quality, from depth sensor)
                // DepthPoint is valid for anchoring - it's a 3D point from the depth map
                if (validHit == null) {
                    validHit = hitResults.firstOrNull { hit ->
                        val trackable = hit.trackable
                        trackable?.trackingState == TrackingState.TRACKING &&
                        trackable.javaClass.simpleName == "DepthPoint"
                    }
                    if (validHit != null) {
                        Log.d("ArScreen", "Using DepthPoint hit for placement")
                    }
                }

                // Priority 3: Accept ANY tracking hit
                if (validHit == null && hitResults.isNotEmpty()) {
                    validHit = hitResults.firstOrNull { hit ->
                        hit.trackable?.trackingState == TrackingState.TRACKING
                    }
                    if (validHit != null) {
                        Log.d("ArScreen", "Using generic trackable hit as fallback")
                    }
                }

                // Priority 4: If no hit but actual planes exist, anchor at plane center
                var fallbackAnchor: Anchor? = null
                if (validHit == null) {
                    Log.d("ArScreen", "No valid hit, attempting fallback anchor creation")
                    try {
                        val planes = arSession?.getAllTrackables(Plane::class.java)
                            ?.filter { it.trackingState == TrackingState.TRACKING }

                        val firstPlane = planes?.firstOrNull()
                        if (firstPlane != null) {
                            // Create anchor at plane's center pose
                            fallbackAnchor = firstPlane.createAnchor(firstPlane.centerPose)
                            Log.d("ArScreen", "Created fallback anchor at plane center")
                        } else {
                            // Priority 5 (FINAL FALLBACK): Create anchor in front of camera
                            // This uses "instant placement" approach - place at estimated distance
                            Log.d("ArScreen", "No planes found, creating anchor in front of camera")
                            val camera = currentFrame.camera
                            if (camera.trackingState == TrackingState.TRACKING) {
                                val cameraPose = camera.pose
                                // Create a pose 1.5 meters in front of the camera, on the ground plane
                                val translation = floatArrayOf(0f, -0.5f, -1.5f) // x, y (down), z (forward)
                                val rotation = floatArrayOf(0f, 0f, 0f, 1f) // No rotation (quaternion)
                                val anchorPose = cameraPose.compose(
                                    com.google.ar.core.Pose(translation, rotation)
                                )
                                fallbackAnchor = arSession?.createAnchor(anchorPose)
                                Log.d("ArScreen", "Created anchor 1.5m in front of camera")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("ArScreen", "Failed to create fallback anchor: ${e.message}")
                    }
                }

                if (validHit != null || fallbackAnchor != null) {
                    Log.d("ArScreen", "Placing model - hitType: ${validHit?.trackable?.javaClass?.simpleName ?: "fallback"}")

                    // Update plane count if needed
                    if (planeCount == 0) {
                        planeCount = 1
                        onPlaneDetected(1)
                    }

                    // Valid plane tap - load model
                    val animalArUrl = currentSessionState.selectedAnimal?.arModelUrl
                    val modelUrl = if (!animalArUrl.isNullOrBlank()) animalArUrl.trim() else DUMMY_MODEL_URL

                    // Validate URL format
                    val isValidUrl = modelUrl.startsWith("http://") || modelUrl.startsWith("https://")

                    if (!isValidUrl) {
                        Log.e("ArScreen", "Invalid model URL format: $modelUrl")
                        modelLoadError = "Invalid model URL format"
                    } else {
                        isModelLoading = true
                        modelLoadError = null
                        modelLoadTimedOut = false
                        modelLoadRetryCount = 0

                        Log.d("ArScreen", "Loading AR model from URL: $modelUrl")

                        // Cancel any existing timeout job
                        timeoutJob?.cancel()
                        timeoutJob = scope.launch {
                            kotlinx.coroutines.delay(45000L)
                            if (isModelLoading) {
                                Log.e("ArScreen", "Model loading timed out after 45 seconds")
                                isModelLoading = false
                                modelLoadTimedOut = true
                                childNodes = emptyList()
                            }
                        }

                        // Create anchor - use hit result anchor OR fallback anchor
                        val anchor: Anchor? = if (validHit != null) {
                            validHit.createAnchorOrNull()
                        } else {
                            fallbackAnchor // Already created above
                        }

                        if (anchor != null) {
                            Log.d("ArScreen", "Anchor created successfully")

                            scope.launch {
                                try {
                                    // Check if model is cached
                                    isCached = ModelCache.isModelCached(context, modelUrl)

                                    val modelPathToLoad: String = if (isCached) {
                                        // Load from cache - use file:// URI format
                                        Log.d("ArScreen", "Loading model from cache...")
                                        val cachedPath = ModelCache.getCachedFilePath(context, modelUrl)
                                        if (cachedPath != null) {
                                            "file://$cachedPath"
                                        } else {
                                            // Cache file missing, re-download
                                            Log.d("ArScreen", "Cache file missing, re-downloading...")
                                            isDownloading = true
                                            val path = ModelCache.getCachedModelPath(context, modelUrl)
                                            isDownloading = false
                                            if (path != null) "file://$path" else modelUrl
                                        }
                                    } else {
                                        // Download and cache the model
                                        Log.d("ArScreen", "Downloading model (first time)...")
                                        isDownloading = true
                                        val path = ModelCache.getCachedModelPath(context, modelUrl)
                                        isDownloading = false
                                        if (path != null) {
                                            "file://$path"
                                        } else {
                                            // Fallback to direct URL if caching fails
                                            Log.w("ArScreen", "Caching failed, loading directly from URL")
                                            modelUrl
                                        }
                                    }

                                    // Load the model
                                    Log.d("ArScreen", "Loading model from: $modelPathToLoad")
                                    val modelInstance = modelLoader.loadModelInstance(modelPathToLoad)

                                    if (modelInstance != null) {
                                        Log.d("ArScreen", "Model loaded successfully")

                                        // Create model node with larger scale for better visibility
                                        // scaleToUnits = 1.0f means model will be scaled to 1 meter
                                        val modelNode = ModelNode(
                                            modelInstance = modelInstance,
                                            scaleToUnits = 1.0f  // Increased from 0.5f for better visibility
                                        ).apply {
                                            isEditable = true
                                        }

                                        // Create anchor node and attach model
                                        val anchorNode = AnchorNode(
                                            engine = engine,
                                            anchor = anchor
                                        ).apply {
                                            addChildNode(modelNode)
                                        }

                                        childNodes = listOf(anchorNode)

                                        // Cancel timeout
                                        timeoutJob?.cancel()
                                        timeoutJob = null

                                        isModelLoading = false
                                        isDownloading = false
                                        isModelPlaced = true
                                        modelLoadError = null
                                        modelLoadTimedOut = false

                                        currentSessionState.selectedAnimal?.let { onAnimalPlaced(it) }
                                        Log.d("ArScreen", "Model placed successfully with scale 1.0")
                                    } else {
                                        throw Exception("Failed to load model instance")
                                    }
                                } catch (e: Exception) {
                                    Log.e("ArScreen", "Model load error: ${e.message}", e)

                                    timeoutJob?.cancel()
                                    timeoutJob = null
                                    isDownloading = false

                                    modelLoadRetryCount++
                                    if (modelLoadRetryCount < maxRetries) {
                                        Log.d("ArScreen", "Retrying model load, attempt ${modelLoadRetryCount + 1}/$maxRetries")
                                        isModelLoading = false
                                    } else {
                                        isModelLoading = false
                                        modelLoadError = e.message ?: "Failed to load model after $maxRetries attempts"
                                    }

                                    // Clean up anchor
                                    try {
                                        anchor.detach()
                                    } catch (_: Exception) {}
                                }
                            }
                        } else {
                            Log.e("ArScreen", "Failed to create anchor")
                            isModelLoading = false
                            modelLoadError = "Failed to create anchor at this location"
                        }
                    }
                } else {
                    Log.d("ArScreen", "No valid plane hit at tap location - tap on a detected surface")
                }
            } else {
                Log.d("ArScreen", "Tap ignored - model already placed or loading")
            }
        }

        // SceneView 2.x ARScene Composable
        ARScene(
            modifier = Modifier.fillMaxSize(),
            engine = engine,
            modelLoader = modelLoader,
            childNodes = childNodes,
            planeRenderer = true,
            sessionConfiguration = { session, config ->
                config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
                config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
                config.depthMode = if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                    Config.DepthMode.AUTOMATIC
                } else {
                    Config.DepthMode.DISABLED
                }
                config.instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP
            },
            onSessionUpdated = { session, updatedFrame ->
                arFrame = updatedFrame
                arSession = session

                // Track ALL planes from session, not just updated ones
                // getUpdatedPlanes() only returns planes modified in this frame
                // getAllTrackables() returns all detected planes
                try {
                    val allPlanes = session.getAllTrackables(Plane::class.java)
                        .filter { it.trackingState == TrackingState.TRACKING }

                    val newCount = allPlanes.size
                    // Only update and log if count changed significantly (reduce log spam)
                    if (newCount > 0 && (planeCount == 0 || kotlin.math.abs(newCount - planeCount) >= 5)) {
                        val oldCount = planeCount
                        planeCount = newCount
                        if (oldCount == 0) {
                            onPlaneDetected(newCount)
                            Log.d("ArScreen", "First plane detection: $newCount planes")
                        }
                    } else if (newCount > 0 && planeCount == 0) {
                        planeCount = newCount
                        onPlaneDetected(newCount)
                    }

                    // Auto-enable placement after 3 seconds even if no planes detected
                    // DepthPoint hits from the depth sensor can still work for placement
                    if (planeCount == 0 && !isModelPlaced) {
                        val frameTimestamp = updatedFrame.timestamp
                        // frameTimestamp is in nanoseconds, check if > 3 seconds since first frame
                        if (frameTimestamp > 3_000_000_000L) {
                            planeCount = 1 // Enable tapping
                            onPlaneDetected(1)
                            Log.d("ArScreen", "Auto-enabled placement after timeout (DepthPoint fallback available)")
                        }
                    }
                } catch (e: Exception) {
                    // Fallback to getUpdatedPlanes if getAllTrackables fails
                    val detectedPlanes = updatedFrame.getUpdatedPlanes()
                        .filter { it.trackingState == TrackingState.TRACKING }
                    if (detectedPlanes.isNotEmpty() && planeCount == 0) {
                        planeCount = detectedPlanes.size
                        onPlaneDetected(detectedPlanes.size)
                        Log.d("ArScreen", "Detected ${detectedPlanes.size} planes (updated)")
                    }
                }

                // Check tracking state for placed models with debouncing
                // Only show tracking lost after sustained loss to prevent flickering
                if (isModelPlaced && childNodes.isNotEmpty()) {
                    val anchorNode = childNodes.firstOrNull()
                    val trackingState = anchorNode?.anchor?.trackingState

                    if (trackingState == TrackingState.STOPPED) {
                        trackingLostCounter++
                        // Only show tracking lost after sustained loss
                        if (trackingLostCounter >= trackingLostThreshold && !isTrackingLost) {
                            isTrackingLost = true
                            Log.w("ArScreen", "Tracking lost (sustained for $trackingLostCounter frames)")
                        }
                    } else if (trackingState == TrackingState.TRACKING) {
                        // Reset counter when tracking recovers
                        if (trackingLostCounter > 0) {
                            trackingLostCounter = 0
                        }
                        if (isTrackingLost) {
                            isTrackingLost = false
                            Log.d("ArScreen", "Tracking recovered")
                        }
                    }
                    // Ignore PAUSED state - it's normal during quick camera movements
                }
            },
            onGestureListener = rememberOnGestureListener(
                onSingleTapConfirmed = { e, node ->
                    if (node == null) {
                        handleTapToPlace(e)
                    }
                }
            )
        )

        if (isModelLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(color = PrimaryGreenLime, strokeWidth = 4.dp)
                    Text(
                        text = if (isDownloading) {
                            "Downloading model... (first time only)"
                        } else if (isCached) {
                            "Loading from cache..."
                        } else {
                            stringResource(R.string.ar_loading_model)
                        },
                        color = PastelYellow,
                        fontSize = 18.sp,
                        fontFamily = JerseyFont,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                    if (isDownloading) {
                        Text(
                            text = "This will be instant next time!",
                            color = White.copy(alpha = 0.7f),
                            fontSize = 14.sp,
                            fontFamily = JerseyFont
                        )
                    }
                }
            }
        }

        if (isTrackingLost && isModelPlaced) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    color = Color.Red.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Warning,
                            contentDescription = null,
                            tint = White,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = stringResource(R.string.ar_tracking_lost),
                            color = White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = JerseyFont,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = stringResource(R.string.ar_tracking_lost_hint),
                            color = White.copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        if (modelLoadTimedOut) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    color = Color(0xFFFF6B6B).copy(alpha = 0.95f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = White,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = stringResource(R.string.ar_model_load_timeout),
                            color = White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = JerseyFont,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = stringResource(R.string.ar_model_load_timeout_hint),
                            color = White.copy(alpha = 0.9f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                        Button(
                            onClick = {
                                modelLoadTimedOut = false
                                modelLoadError = null
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = White,
                                contentColor = Color(0xFFFF6B6B)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Replay,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.retry),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        if (modelLoadError != null && !modelLoadTimedOut) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    color = Color(0xFFFF6B6B).copy(alpha = 0.95f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = White,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = stringResource(R.string.ar_model_load_failed),
                            color = White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = JerseyFont,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = stringResource(R.string.ar_model_load_failed_hint),
                            color = White.copy(alpha = 0.9f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                        Button(
                            onClick = {
                                modelLoadError = null
                                modelLoadTimedOut = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = White,
                                contentColor = Color(0xFFFF6B6B)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Replay,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.retry),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        ArCameraOverlay(
            planeCount = planeCount,
            isModelPlaced = isModelPlaced,
            isModelLoading = isModelLoading,
            sessionState = sessionState,
            onNavigateBack = onNavigateBack,
            onClearAnimals = {
                // Cancel any pending timeout job
                timeoutJob?.cancel()
                timeoutJob = null

                // Clear all child nodes (SceneView 2.x approach)
                childNodes.forEach { anchorNode ->
                    try {
                        anchorNode.anchor.detach()
                        anchorNode.destroy()
                    } catch (e: Exception) {
                        Log.e("ArScreen", "Error clearing model: ${e.message}")
                    }
                }
                childNodes = emptyList()

                // Reset all state to allow placing a new model
                isModelPlaced = false
                isTrackingLost = false
                trackingLostCounter = 0
                modelLoadRetryCount = 0
                modelLoadError = null
                modelLoadTimedOut = false
                // Keep planeCount - planes are still detected, no need to reset
                // This allows immediate re-placement without waiting for plane detection again

                Log.d("ArScreen", "Model cleared, ready for new placement. Planes: $planeCount")

                onClearAnimals()
            },
            showCaptureSuccess = showCaptureSuccess,
            isCapturing = isCapturing,
            onCapture = {
                // TODO: Implement screenshot capture for SceneView 2.x
                // The capture API has changed in SceneView 2.x
                Log.d("ArScreen", "Capture requested")
            }
        )
    }
}

@Composable
fun BoxScope.ArCameraOverlay(
    planeCount: Int,
    isModelPlaced: Boolean,
    isModelLoading: Boolean = false,
    sessionState: ArSessionState,
    onNavigateBack: () -> Unit,
    onClearAnimals: () -> Unit,
    showCaptureSuccess: Boolean = false,
    isCapturing: Boolean = false,
    onCapture: () -> Unit = {}
) {
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues()
    val navigationBarPadding = WindowInsets.navigationBars.asPaddingValues()

    var showSuccessMessage by remember { mutableStateOf(false) }
    var showGestureHint by remember { mutableStateOf(false) }

    LaunchedEffect(isModelPlaced) {
        if (isModelPlaced) {
            showSuccessMessage = true
            kotlinx.coroutines.delay(2000L)
            showSuccessMessage = false
            showGestureHint = true
            kotlinx.coroutines.delay(5000L)
            showGestureHint = false
        } else {
            showSuccessMessage = false
            showGestureHint = false
        }
    }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.TopCenter)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.7f),
                        Color.Transparent
                    )
                )
            )
            .padding(top = statusBarPadding.calculateTopPadding())
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .size(40.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                modifier = Modifier
                    .size(48.dp),
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Back",
                tint = PrimaryGreenLight
            )
        }

        if (planeCount == 0 && !isModelPlaced) {
            Surface(
                color = Color.Gray.copy(alpha = 0.7f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CenterFocusWeak,
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = stringResource(R.string.ar_scanning),
                        color = White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
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
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = stringResource(R.string.ar_photo_captured),
                    color = White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = JerseyFont
                )
                Text(
                    text = stringResource(R.string.ar_saved_to_gallery),
                    color = White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
        }
    }

    if (!isModelPlaced && !isModelLoading) {
        if (planeCount == 0) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = statusBarPadding.calculateTopPadding() + 70.dp)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    color = Color.Black.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Warning,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Surface(
                    color = Color.Black.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(64.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Text(
                            text = stringResource(R.string.ar_move_camera),
                            color = Color.Red,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = JerseyFont
                        )
                    }
                }
            }
        } else {
            ArReticle()
        }
    }

    if (!isModelPlaced && !isModelLoading && planeCount > 0) {
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = navigationBarPadding.calculateBottomPadding())
                .padding(24.dp),
            color = PrimaryGreen.copy(alpha = 0.9f),
            shape = RoundedCornerShape(64.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CenterFocusWeak,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = stringResource(
                        R.string.ar_tap_to_place_named,
                        sessionState.selectedAnimal?.name ?: stringResource(R.string.ar_tap_to_place_animal)
                    ),
                    color = White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = JerseyFont
                )
            }
        }
    }

    if (isModelPlaced) {
        val animal = sessionState.selectedAnimal
        val animalName = animal?.name ?: "3D Model"
        val scientificName = animal?.scientificName ?: "Demo Model"

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = statusBarPadding.calculateTopPadding() + 70.dp)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AnimatedVisibility(visible = showSuccessMessage) {
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

            Surface(
                color = Color.Black.copy(alpha = 0.8f),
                shape = RoundedCornerShape(64.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
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
                        color = MediumGreenSage,
                        fontSize = 16.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        fontFamily = JerseyFont,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    AnimatedVisibility(
        visible = showGestureHint && isModelPlaced,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = navigationBarPadding.calculateBottomPadding() + 150.dp)
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
                    text = stringResource(R.string.ar_interact_with_model),
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
        visible = isModelPlaced,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = navigationBarPadding.calculateBottomPadding())
            .padding(24.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onClearAnimals,
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.Red.copy(alpha = 0.8f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Replay,
                    contentDescription = "Reset",
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
                            contentDescription = "Capture",
                            tint = DarkGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ArScreenContent(
    uiState: ArUiState,
    sessionState: ArSessionState,
    onNavigateBack: () -> Unit = {},
    onRequestPermission: () -> Unit = {},
    onClearAnimals: () -> Unit = {},
    showRationale: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0D1B2A),
                            Color(0xFF1B263B),
                            Color(0xFF0D1B2A)
                        )
                    )
                )
        )

        if (uiState is ArUiState.Scanning || uiState is ArUiState.AnimalPlaced) {
            ArScanningGrid()
        }

        ArTopBar(
            onNavigateBack = onNavigateBack,
            planesDetected = sessionState.detectedPlanes
        )

        when (uiState) {
            is ArUiState.Initializing -> {
                ArLoadingState()
            }
            is ArUiState.CameraPermissionRequired -> {
                ArPermissionRequired(
                    onRequestPermission = onRequestPermission,
                    showRationale = showRationale
                )
            }
            is ArUiState.Ready, is ArUiState.Scanning -> {
                ArScanningInstructions(
                    planesDetected = if (uiState is ArUiState.Scanning) uiState.planesDetected else 0
                )
            }
            is ArUiState.AnimalPlaced -> {
                ArAnimalPlacedInfo(animal = uiState.animal)
            }
            is ArUiState.Error -> {
                ArErrorState(message = uiState.message)
            }
        }

        if (uiState is ArUiState.Scanning || uiState is ArUiState.AnimalPlaced) {
            ArBottomControls(
                placedAnimalsCount = sessionState.placedAnimals.size,
                onClearAnimals = onClearAnimals,
                onCapture = { /* TODO: Implement capture */ }
            )
        }

        if (uiState is ArUiState.Scanning) {
            ArReticle()
        }
    }
}

@Composable
fun BoxScope.ArTopBar(
    onNavigateBack: () -> Unit,
    planesDetected: Int
) {
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.TopCenter)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.7f),
                        Color.Transparent
                    )
                )
            )
            .padding(top = statusBarPadding.calculateTopPadding())
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .size(40.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                modifier = Modifier
                    .size(48.dp),
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Back",
                tint = PrimaryGreenLight
            )
        }

        if (planesDetected > 0) {
            Surface(
                color = PrimaryGreen.copy(alpha = 0.9f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = stringResource(R.string.ar_surfaces_detected, planesDetected, if (planesDetected > 1) "s" else ""),
                        color = White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun BoxScope.ArLoadingState() {
    Column(
        modifier = Modifier.align(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(60.dp),
            color = PrimaryGreenLime,
            strokeWidth = 4.dp
        )
        Text(
            text = stringResource(R.string.ar_initializing),
            color = White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun BoxScope.ArPermissionRequired(
    onRequestPermission: () -> Unit,
    showRationale: Boolean
) {
    Column(
        modifier = Modifier
            .align(Alignment.Center)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = null,
            tint = PastelYellow,
            modifier = Modifier.size(80.dp)
        )
        Text(
            text = if (showRationale)
                stringResource(R.string.camera_permission_fail)
            else
                stringResource(R.string.camera_permission_grant),
            color = White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = onRequestPermission,
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryGreen,
                contentColor = White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.height(50.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.ar_grant_permission),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun BoxScope.ArScanningInstructions(planesDetected: Int) {
    Column(
        modifier = Modifier
            .align(Alignment.Center)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "scan")
        val scale by infiniteTransition.animateFloat(
            initialValue = 0.8f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale"
        )

        Icon(
            imageVector = Icons.Default.CenterFocusWeak,
            contentDescription = null,
            tint = PrimaryGreenLime,
            modifier = Modifier
                .size(100.dp)
                .scale(scale)
        )

        Surface(
            color = Color.Black.copy(alpha = 0.7f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (planesDetected == 0)
                        "Point camera at a flat surface"
                    else
                        "Tap to place an animal",
                    color = White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = if (planesDetected == 0)
                        "Move your device slowly to detect surfaces"
                    else
                        "Surface detected! Ready to place 3D models",
                    color = White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun BoxScope.ArAnimalPlacedInfo(animal: android.app.faunadex.domain.model.Animal) {
    Column(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(top = 80.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            color = PrimaryGreen.copy(alpha = 0.95f),
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(32.dp)
                )
                Column {
                    Text(
                        text = stringResource(R.string.ar_animal_placed),
                        color = White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = animal.name,
                        color = White.copy(alpha = 0.9f),
                        fontSize = 14.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        }
    }
}

@Composable
fun BoxScope.ArErrorState(message: String) {
    Column(
        modifier = Modifier
            .align(Alignment.Center)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            tint = Color.Red,
            modifier = Modifier.size(80.dp)
        )
        Text(
            text = stringResource(R.string.ar_error),
            color = White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = message,
            color = White.copy(alpha = 0.8f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun BoxScope.ArBottomControls(
    placedAnimalsCount: Int,
    onClearAnimals: () -> Unit,
    onCapture: () -> Unit
) {
    val navigationBarPadding = WindowInsets.navigationBars.asPaddingValues()

    Row(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.8f)
                    )
                )
            )
            .padding(bottom = navigationBarPadding.calculateBottomPadding())
            .padding(24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedVisibility(visible = placedAnimalsCount > 0) {
            IconButton(
                onClick = onClearAnimals,
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.Red.copy(alpha = 0.8f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Clear Animals",
                    tint = White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .size(70.dp)
                .background(White, CircleShape)
                .border(4.dp, PrimaryGreen, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onCapture,
                modifier = Modifier.size(62.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Capture",
                    tint = DarkGreen,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Surface(
            color = PrimaryGreen.copy(alpha = 0.9f),
            shape = CircleShape
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Pets,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "$placedAnimalsCount",
                    color = White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun BoxScope.ArReticle() {

    Canvas(
        modifier = Modifier
            .align(Alignment.Center)
            .size(80.dp)
    ) {
        val radius = size.minDimension / 2
        val center = Offset(size.width / 2, size.height / 2)

        drawCircle(
            color = PrimaryGreenLime.copy(alpha = 0.5f),
            radius = radius,
            center = center,
            style = Stroke(width = 2.dp.toPx())
        )

        drawLine(
            color = PrimaryGreenLime,
            start = Offset(center.x - radius * 0.3f, center.y),
            end = Offset(center.x + radius * 0.3f, center.y),
            strokeWidth = 2.dp.toPx()
        )
        drawLine(
            color = PrimaryGreenLime,
            start = Offset(center.x, center.y - radius * 0.3f),
            end = Offset(center.x, center.y + radius * 0.3f),
            strokeWidth = 2.dp.toPx()
        )

        val bracketLength = radius * 0.4f
        val bracketDistance = radius * 0.8f

        // Top-left corner
        drawLine(
            color = PrimaryGreenLime,
            start = Offset(center.x - bracketDistance, center.y - bracketDistance),
            end = Offset(center.x - bracketDistance + bracketLength, center.y - bracketDistance),
            strokeWidth = 3.dp.toPx()
        )
        drawLine(
            color = PrimaryGreenLime,
            start = Offset(center.x - bracketDistance, center.y - bracketDistance),
            end = Offset(center.x - bracketDistance, center.y - bracketDistance + bracketLength),
            strokeWidth = 3.dp.toPx()
        )

        // Top-right corner
        drawLine(
            color = PrimaryGreenLime,
            start = Offset(center.x + bracketDistance, center.y - bracketDistance),
            end = Offset(center.x + bracketDistance - bracketLength, center.y - bracketDistance),
            strokeWidth = 3.dp.toPx()
        )
        drawLine(
            color = PrimaryGreenLime,
            start = Offset(center.x + bracketDistance, center.y - bracketDistance),
            end = Offset(center.x + bracketDistance, center.y - bracketDistance + bracketLength),
            strokeWidth = 3.dp.toPx()
        )

        // Bottom-left corner
        drawLine(
            color = PrimaryGreenLime,
            start = Offset(center.x - bracketDistance, center.y + bracketDistance),
            end = Offset(center.x - bracketDistance + bracketLength, center.y + bracketDistance),
            strokeWidth = 3.dp.toPx()
        )
        drawLine(
            color = PrimaryGreenLime,
            start = Offset(center.x - bracketDistance, center.y + bracketDistance),
            end = Offset(center.x - bracketDistance, center.y + bracketDistance - bracketLength),
            strokeWidth = 3.dp.toPx()
        )

        // Bottom-right corner
        drawLine(
            color = PrimaryGreenLime,
            start = Offset(center.x + bracketDistance, center.y + bracketDistance),
            end = Offset(center.x + bracketDistance - bracketLength, center.y + bracketDistance),
            strokeWidth = 3.dp.toPx()
        )
        drawLine(
            color = PrimaryGreenLime,
            start = Offset(center.x + bracketDistance, center.y + bracketDistance),
            end = Offset(center.x + bracketDistance, center.y + bracketDistance - bracketLength),
            strokeWidth = 3.dp.toPx()
        )
    }
}

@Composable
fun ArScanningGrid() {
    val infiniteTransition = rememberInfiniteTransition(label = "grid")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val gridSpacing = 100.dp.toPx()
        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)

        var x = 0f
        while (x < size.width) {
            drawLine(
                color = PrimaryGreenLime.copy(alpha = alpha),
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 1.dp.toPx(),
                pathEffect = pathEffect
            )
            x += gridSpacing
        }

        var y = 0f
        while (y < size.height) {
            drawLine(
                color = PrimaryGreenLime.copy(alpha = alpha),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = pathEffect
            )
            y += gridSpacing
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ArScreenPreview() {
    FaunaDexTheme {
        ArScreenContent(
            uiState = ArUiState.Scanning(planesDetected = 2),
            sessionState = ArSessionState(
                detectedPlanes = 2,
                placedAnimals = listOf()
            )
        )
    }
}

// TODO: Implement screenshot capture for SceneView 2.x
// The capture API has changed - use View.drawToBitmap() or similar approach

private fun saveBitmapToGallery(context: Context, bitmap: Bitmap): Boolean {
    val filename = "WildAR!_AR_${System.currentTimeMillis()}.jpg"

    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/WildAR!")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }

            val uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )

            uri?.let {
                context.contentResolver.openOutputStream(it)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)
                }

                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                context.contentResolver.update(uri, contentValues, null, null)

                true
            } ?: false
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val faunaDexDir = java.io.File(imagesDir, "WildAR!")
            if (!faunaDexDir.exists()) {
                faunaDexDir.mkdirs()
            }

            val imageFile = java.io.File(faunaDexDir, filename)
            java.io.FileOutputStream(imageFile).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)
            }

            @Suppress("DEPRECATION")
            val mediaScanIntent = android.content.Intent(android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            mediaScanIntent.data = android.net.Uri.fromFile(imageFile)
            context.sendBroadcast(mediaScanIntent)

            true
        }
    } catch (_: Exception) {
        false
    }
}
