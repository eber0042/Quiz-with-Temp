package com.temi.temiSDK

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID
import kotlin.random.Random
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import com.temi.temiSDK.ui.theme.GreetMiTheme

/*
@Composable
fun Greeting() {
    val viewModel: MainViewModel = hiltViewModel()
    val context = LocalContext.current

    val gifEnabledLoader = ImageLoader.Builder(context)
        .components {
            if ( SDK_INT >= 28 ) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }.build()

//    val permissionsLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission()
//    ) { success: Boolean ->
//        if (success) Log.i("success", "success")
//        else Log.i("fail", "fail")
//    }

// Trying to get permissions to work
//    LaunchedEffect(true) {
//        val intent = Intent.createIntent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//
//        permissionsLauncher.launch(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//    }

    AsyncImage(
        model = R.drawable.idle, // URL or resource of the GIF
        contentDescription = "GIF image",
        imageLoader = gifEnabledLoader,
        modifier = Modifier.fillMaxSize(), // Fill the whole screen
        contentScale = ContentScale.Crop // Crop to fit the entire screen
    )
}
*/

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the PackageManager instance
        val packageManager = packageManager

        // Retrieve installed apps
        getInstalledTemiApps(packageManager)

        // Enable edge-to-edge layout
        enableEdgeToEdge()

        // Start the emotional detection application
        val intent = packageManager.getLaunchIntentForPackage("com.temi.emotiondetection")
        // Check if the application is present, if so run it
        if (intent != null) {
            Log.w("FUCK!", "WORKING")
            startActivity(intent) // Start the Emotion Detection app
        }  else {
            Log.e("FUCK!", "NOT WORKING")
        }
         // Set up method for receiving messages form emotional detection
        val receiver = EmotionReceiver()
        val filter = IntentFilter("com.quizapp.RECEIVE_DATA")
        registerReceiver(receiver, filter)

        setContent {
            GreetMiTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    val context = LocalContext.current
                    QuizApp(context = context)
                }
            }
        }
    }
}

fun getInstalledTemiApps(packageManager: PackageManager): List<String> {
    val apps = mutableListOf<String>()
    val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

    for (packageInfo in packages) {
        // Check if the package name starts with "com.temi"
        if (packageInfo.packageName.startsWith("com.temi")) {
            // Log the found app
            Log.d("FUCK!", "Found Temi app: ${packageInfo.packageName}")
            apps.add(packageInfo.packageName)
        }
    }

    return apps
}

object GlobalData {
    var emotionResult by mutableStateOf<String?>(null)
}

// Set up broadcaster for emotional detection
class EmotionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra("message")

        if (message.isNullOrBlank()) {
            Log.w("FUCK!", "Received an empty or null message")
            return
        }

        Log.d("FUCK!", "Received full message: $message")

        // Pattern to match "Emotion" and "Face" values in the message
        val emotionPattern = Regex(""""(\w+)"""")
        val facePattern = Regex("""Face:\s+(\d+)""")

        val emotionMatch = emotionPattern.find(message)
        val faceMatch = facePattern.find(message)

        // Extract values if found
        val emotion = emotionMatch?.groupValues?.get(1)
        val face = faceMatch?.groupValues?.get(1)?.toIntOrNull()

        // Log.d("FUCK!", "$emotion")
        // Log.d("FUCK!", "$face")

        if (face == 0 && emotion != null) {
            // Update GlobalData with the extracted emotion
            GlobalData.emotionResult = emotion
            //Log.d("FUCK!", "Stored emotion for face 0: $emotion")
        } else {
            Log.d("FUCK!", "Ignored message as face is not 0 or emotion is null")
        }
    }
}
//***************************************** TOOLS
sealed class AppState {
    object Test : AppState()
    object QuizHome : AppState()
    object Quiz : AppState()
    object ScoreBoard : AppState()
    object Bluetooth: AppState()
}

sealed class QuizState {
    data class Question(val id: Int) : QuizState()
}

class AudioPlayer(context: Context, private val mediaResId: Int) {

    private val mediaPlayer: MediaPlayer = MediaPlayer.create(context, mediaResId)
    private val handler = Handler(Looper.getMainLooper())
    private var volumeRunnable: Runnable? = null

    init {
        // Optionally, you can configure the media player here
    }

    fun play() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    fun stop() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            mediaPlayer.seekTo(0)
        }
    }

    fun pause() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    fun reset() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.seekTo(0)
        }
    }

    fun release() {
        mediaPlayer.release()
        volumeRunnable?.let { handler.removeCallbacks(it) }
    }

    fun setLooping(isLooping: Boolean) {
        mediaPlayer.isLooping = isLooping
    }

    fun setVolume(volume: Float) {
        // Volume ranges from 0.0f (silent) to 1.0f (loudest)
        mediaPlayer.setVolume(volume, volume)
    }

    fun setVolumeOverTime(startVolume: Float, endVolume: Float, durationMs: Long) {
        val volumeStep = (endVolume - startVolume) / (durationMs / 50) // Update every 50ms
        var currentVolume = startVolume

        // Remove any previously scheduled volume change
        volumeRunnable?.let { handler.removeCallbacks(it) }

        volumeRunnable = object : Runnable {
            override fun run() {
                currentVolume += volumeStep
                if (currentVolume > endVolume) {
                    currentVolume = endVolume
                }
                mediaPlayer.setVolume(currentVolume, currentVolume)
                if (currentVolume < endVolume) {
                    handler.postDelayed(this, 50)
                }
            }
        }

        handler.post(volumeRunnable!!)
    }
}

@OptIn(ExperimentalAnimationApi::class)

@Composable
fun ImageCycler(
    imageResIds: List<Int>, durationMillis: Long = 2000L, // Duration for each image
    fadeTrue: Boolean = false, modifier: Modifier = Modifier
) {
    var currentIndex by remember { mutableStateOf(0) }
    var fadeDuration = 0

    // Cycle through images
    LaunchedEffect(Unit) {
        while (true) {
            delay(durationMillis)
            currentIndex = (currentIndex + 1) % imageResIds.size
        }
    }

    if (fadeTrue) fadeDuration = 500
    Box(
        modifier = modifier.fillMaxSize() // Adjust size as needed
    ) {
        AnimatedContent(targetState = imageResIds[currentIndex], transitionSpec = {
            // Specify the fade-in and fade-out animation
            fadeIn(animationSpec = tween(durationMillis = fadeDuration)) togetherWith fadeOut(
                animationSpec = tween(durationMillis = fadeDuration)
            )
        }) { targetImageResId ->
            Image(
                painter = painterResource(id = targetImageResId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize() // Adjust size as needed
            )
        }
    }
}

//***************************************** TXT FILE EXTRACTION
fun getBlacklist(context: Context): List<String> {
    val inputStream = context.resources.openRawResource(R.raw.blacklist) // Get resource
    val blacklist = mutableListOf<String>() // Create empty list

    inputStream.bufferedReader().useLines { lines -> // Get each line and put into list
        lines.forEach { blacklist.add(it.trim()) }
    }
    return blacklist // return the list
}

fun getWhitelist(context: Context): List<String> {
    val inputStream = context.resources.openRawResource(R.raw.whitelist) // Get resource
    val whitelist = mutableListOf<String>() // Create empty list

    inputStream.bufferedReader().useLines { lines -> // Get each line and put into list
        lines.forEach { whitelist.add(it.trim()) }
    }
    return whitelist // return the list
}

fun extractTXT(context: Context, ID: Int): List<String> {
    val inputStream = context.resources.openRawResource(ID) // Get resource
    val mainquiz = mutableListOf<String>() // Create empty list

    inputStream.bufferedReader().useLines { lines -> // Get each line and put into list
        lines.forEach { mainquiz.add(it.trim()) }
    }
    return mainquiz // return the list
}

fun extractQuestionsFromInput(user: List<String>, input: Int): List<question> {
    var lines = 0
    while (user[input + lines + 1].first() == '*') {
        lines++
    }
    // Regex to match the question format: "* question text ; boolean"
    val regex = "\\* (.+?) ; (true|false)".toRegex()

    // List to store extracted questions
    val questionsList = mutableListOf<question>()

    Log.d("List_Check", user[input + 1])

    // Iterate over each line
    repeat(lines) { i ->
        // Try to match the line to the regex pattern
        val matchResult = regex.find(user[input + i + 1])
        if (matchResult != null) {
            // Extract the text and boolean value
            val questionText = matchResult.groupValues[1].trim()
            val isCorrect = matchResult.groupValues[2].toBoolean()

            // Create a new question object and add it to the list
            questionsList.add(question(questionText, isCorrect))
        }
    }

    // Return the list of extracted questions
    Log.d("List_Check", "$questionsList")
    return questionsList
}

fun extractTextFromInput(input: String): String? {
    // Define a regular expression to match the text after 'text:->'
    val regex = "text:->\\s*(.*)".toRegex()

    // Find the first match
    val matchResult = regex.find(input)

    // Extract and return the content after 'text:->'
    return matchResult?.groupValues?.get(1)
}

fun extractScoreTypeFromInput(input: String): String? {
    // Define a regular expression to match the text after 'text:->'
    val regex = "scoreType:->\\s*(.*)".toRegex()

    // Find the first match
    val matchResult = regex.find(input)

    // Extract and return the content after 'text:->'
    return matchResult?.groupValues?.get(1)
}

fun extractBooleanFromInput(input: String, defaultValue: Boolean = false): Boolean {
    // Define a regular expression to match the boolean value
    val regex = "\\w+:-> (true|false)".toRegex()

    // Find the first match
    val matchResult = regex.find(input)

    // Extract and return the boolean value, or default if null
    return matchResult?.groupValues?.get(1)?.toBoolean() ?: defaultValue
}

fun extractIntFromInput(input: String, defaultValue: Int = 0): Int {
    // Define a regular expression to match the integer value
    val regex = "\\w+:-> (\\d+)".toRegex()

    // Find the first match
    val matchResult = regex.find(input)

    // Extract and return the integer value, or default if null
    return matchResult?.groupValues?.get(1)?.toIntOrNull() ?: defaultValue
}

@Composable
fun extractPainterResourceFromInput(input: String, defaultValue: Int = 0): Int {
    // Define a regular expression to match the painter resource in the format `R.drawable.resource_name`
    val regex = "\\w+:-> R\\.drawable\\.(\\w+)".toRegex()

    // Find the first match
    val matchResult = regex.find(input)

    // Extract and return the resource ID, or default if no match
    return matchResult?.groupValues?.get(1)?.let { resourceName ->
        // Return the resource ID based on the resource name using context
        val context = LocalContext.current// your context here
        val resourceId =
            context.resources.getIdentifier(resourceName, "drawable", context.packageName)
        if (resourceId != 0) resourceId else defaultValue
    } ?: defaultValue
}

//***************************************** STUFF
data class QuizQuestion(
    val text: String,
    var name: String = "Empty",
    var isSubmitted: Boolean = false,
    var result: ResultForOption = ResultForOption() // Add this field to store the result
)

data class ResultForOption(
    var score: Int = 0,                    // Default value of 0 for score
    var outOf: Int = 0, var isToggleList: List<Boolean> = listOf(), // Default to an empty list
    var initialized: Boolean = false
)

// Data class to hold a text and its corresponding boolean state
data class question(val text: String, val isActive: Boolean)

object PreferencesManager {
    private const val PREFS_NAME = "my_prefs"
    private val gson = Gson()

    // Private makes it so it can only be accessed and changed from this object
    // A const makes it so that the variable is stored in immediate memory allow the code to run...
    // efficiently
    // Val makes it so it cannot be changed

    fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // The getSharedPreferences above is not the same as the function we made.
    }

    // The SharedPreferences is the function used to save and retrieve information from the...
    // persistent memory.
    // This is memory that is saved even after the application is closed.
    // context indicated the device that the memory is being accessed
    // For getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE), the PREFS_NAME is the file...
    // of the stored information
    // MODE_PRIVATE tells the code how information should be transferred.
    // In this case it tells it to make sure data cannot be accessed from outside of system

    fun saveQuizQuestions(context: Context, key: String, questions: List<QuizQuestion>) {
        val prefs = getSharedPreferences(context)
        val json = gson.toJson(questions)
        with(prefs.edit()) {
            putString(key, json)
            apply()
            // Apply is a function that will overwrite values in the persistent memory with...
            // pending values.
        }
    }

    fun clearQuizQuestions(context: Context, key: String) {
        val prefs = getSharedPreferences(context)
        with(prefs.edit()) {
            putString(key, null)
            apply()
            // Apply is a function that will overwrite values in the persistent memory with...
            // pending values.
        }
    }

    fun getQuizQuestions(context: Context, key: String): List<QuizQuestion>? {
        val prefs = getSharedPreferences(context)
        val json = prefs.getString(key, null)
        return if (json != null) {
            val type = object : TypeToken<List<QuizQuestion>>() {}.type
            gson.fromJson(json, type)
        } else {
            null
        }
    }

    // The String? indicates that a string or null can be returned.
    // If there is nothing, it has been set to return a null.
}

data class Ranking(
    val name: String,  // Use String for text data
    val score: Int
)

object LocaleManager {
    fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = context.resources
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}

object ResourceManager {
    private var currentResource: Int = R.raw.quizen // Default resource

    fun getResource(): Int = currentResource

    fun setResource(resourceId: Int) {
        currentResource = resourceId
    }
}

@Composable
fun PasswordPromptBox(clear: Boolean) {
    var showPasswordDialog by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var saveNumber by remember { mutableStateOf(0) }
    val context = LocalContext.current

    // Box for your clickable button
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp, end = 30.dp)
            .zIndex(5f),
        contentAlignment = if (clear) Alignment.BottomEnd else Alignment.TopEnd
    ) {
        BoxWithClickable(
            onClick = {
                // Show the password dialog when clicked
                showPasswordDialog = true
            },
            text = if (clear) stringResource(R.string.clear) else stringResource(R.string.data),
            type = "cool",
            grow = false,
            animatedPress = true,
            cycleColor = false,
            modifier = Modifier
                .padding(16.dp)
                .background(Color.LightGray)
        )
    }

    // Dialog to ask for password input
    if (showPasswordDialog) {
        if (password == "1234") Log.d("Input", password)
        AlertDialog(onDismissRequest = { showPasswordDialog = false },
            title = { Text(stringResource(R.string.enter_password)) },
            text = {
                Column {
                    OutlinedTextField(value = password,
                        onValueChange = { password = it },
                        label = { Text(stringResource(R.string.password)) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (password == "1234") {
                        if (clear) {
                            while (PreferencesManager.getQuizQuestions(
                                    context, "save$saveNumber"
                                ) != null
                            ) {
                                // Want to clear the PreferenceManager
                                PreferencesManager.clearQuizQuestions(
                                    context, "save$saveNumber"
                                )
                                saveNumber++
                            }
                        } else {
                            Log.d("Correct Password", "Correct Password Inputted")
                            // Check the memory and if current spot not empty increment the saveNumber
                            while (PreferencesManager.getQuizQuestions(
                                    context, "save$saveNumber"
                                ) != null
                            ) {
                                // If valid save spot found, save new memory at that spot
                                Log.d(
                                    "Saved", "${
                                        PreferencesManager.getQuizQuestions(
                                            context, "save$saveNumber"
                                        )
                                    }"
                                )
                                saveNumber++
                            }
                            sendPreferencesByEmail(context)
                        }
                        password = ""
                        showPasswordDialog = false
                    }
                }) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                Button(onClick = { showPasswordDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            })
    }
}

fun sendPreferencesByEmail(context: Context) {
    val preferencesData = getAllPreferences(context)

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_EMAIL, arrayOf("eber0042@flinders.edu.au"))
        putExtra(Intent.EXTRA_SUBJECT, "App Preferences Data")
        putExtra(Intent.EXTRA_TEXT, preferencesData)
    }

    try {
        context.startActivity(Intent.createChooser(intent, "Send Email"))
    } catch (e: Exception) {
        Toast.makeText(context, "No email client found", Toast.LENGTH_SHORT).show()
    }
}

fun getAllPreferences(context: Context): String {
    val prefs = PreferencesManager.getSharedPreferences(context)
    val allEntries = prefs.all

    return buildString {
        for ((key, value) in allEntries) {
            append("$key: $value\n")
        }
    }
}

//***************************************** BLUETOOTH
enum class ConnectionState() {
    CONNECTED,
    DISCONNECTED,
    DISCONNECTED_WITHOUT_INTENT
}

data class BleDevice(
    var device: BluetoothDevice? = null,
    var state: ConnectionState = ConnectionState.DISCONNECTED
)

class BleManager(private val context: Context, private val deviceData: BleDevice) {
    private var bluetoothGatt: BluetoothGatt? = null
    private var isDiscoveryCompleted = false
    private val serviceUUIDs = mutableListOf<UUID>()
    private val characteristicsUUIDs = mutableMapOf<UUID, MutableList<UUID>>() // Map of service UUID -> List of characteristic UUIDs

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                Log.e("Bluetooth!", "Bluetooth permission not granted.")
                return
            }
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.i("Bluetooth!", "Device connected: ${gatt?.device?.name}")
                    bluetoothGatt?.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.i("Bluetooth!", "Disconnected from GATT server.")
                    bluetoothGatt?.close()
                    bluetoothGatt = null
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i("Bluetooth!", "Services discovered")

                // Clear the lists to avoid storing duplicate UUIDs if this method is called multiple times
                serviceUUIDs.clear()
                characteristicsUUIDs.clear()

                // Iterate through the discovered services
                for (service in gatt.services) {
                    val serviceUUID = service.uuid
                    Log.d("Service!", "Service UUID: $serviceUUID")
                    serviceUUIDs.add(serviceUUID) // Add the service UUID to the list

                    // For each service, get its characteristics
                    val characteristicUUIDsList = mutableListOf<UUID>()
                    for (characteristic in service.characteristics) {
                        val characteristicUUID = characteristic.uuid
                        Log.d("Characteristic!", "Characteristic UUID: $characteristicUUID")
                        characteristicUUIDsList.add(characteristicUUID) // Add the characteristic UUID to the list
                    }
                    // Store the characteristics associated with the service UUID
                    characteristicsUUIDs[serviceUUID] = characteristicUUIDsList
                }
                isDiscoveryCompleted = true
                // Now, the serviceUUIDs list and characteristicsUUIDs map are populated and can be used later
            } else {
                Log.w("Bluetooth!", "onServicesDiscovered received: $status")
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)

            if (status == BluetoothGatt.GATT_SUCCESS && characteristic != null) {
                // Check if this is the battery level characteristic
                if (characteristic.uuid == UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb")) {
                    // Get the battery level directly from the byte array
                    val batteryLevel = characteristic.value[0].toInt() and 0xFF // Convert to unsigned
                    Log.i("Bluetooth!", "Battery level: $batteryLevel%")
                }
                // Check if this is the device name characteristic
                else if (characteristic.uuid == UUID.fromString("00002a00-0000-1000-8000-00805f9b34fb")) {
                    // Read the device name as a String
                    val deviceName = characteristic.getStringValue(0)
                    Log.i("Bluetooth!", "Device Name: $deviceName")
                }
                else if (characteristic.uuid == UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e")) {

                }
            } else {
                Log.w("Bluetooth!", "onCharacteristicRead failed with status: $status")
            }
        }

        // Check Characteristic changed
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            // Format Read data to string value
            val result = characteristic.getStringValue(0)
            // Send to Read data to string variable

            Log.w("Bluetooth!", "onCharacteristicRead: $result")
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)

            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i("Bluetooth!", "Message sent successfully!")
            } else {
                Log.e("Bluetooth!", "Failed to send message. Status: $status")
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getData() {
        if (bluetoothGatt == null) {
            Log.e("Bluetooth!", "BluetoothGatt is null, cannot read characteristic.")
            return
        }
        while (!isDiscoveryCompleted) {}
        val UartService = bluetoothGatt?.getService(UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e"))
        val RxCharacteristic = UartService?.getCharacteristic(UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e"))

//        //         Enable notifications to receive messages
        val RxDescriptor = RxCharacteristic?.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
        bluetoothGatt!!.setCharacteristicNotification(RxCharacteristic, true)
        RxDescriptor?.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
        bluetoothGatt!!.writeDescriptor(RxDescriptor)
    }

    @SuppressLint("MissingPermission")
    fun moveServo() {
        val UartService = bluetoothGatt?.getService(UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e"))
        val TxCharacteristic = UartService?.getCharacteristic(UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e"))

        val s_data = "s"

        // Formatting write Data for Arduino
        if (TxCharacteristic != null) {
            TxCharacteristic.writeType = TxCharacteristic.writeType
            TxCharacteristic.setValue(s_data)
        }

        if (TxCharacteristic == null) {
            Log.e("Bluetooth!", "Characteristic not found.")
            return
        }
        val success = bluetoothGatt?.writeCharacteristic(TxCharacteristic) ?: false
        if (!success) {
            Log.e("Bluetooth!", "Failed to initiate characteristic")
        }
    }

    fun connectToDevice(): ConnectionState {
        Log.i("Bluetooth!", "Trying to connect to: ${deviceData.device?.name} (${deviceData.device?.address})")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.e("Bluetooth!", "Missing BLUETOOTH_CONNECT permission")
                return ConnectionState.DISCONNECTED_WITHOUT_INTENT
            }
        }

        bluetoothGatt = deviceData.device?.connectGatt(context, false, gattCallback)

        if (bluetoothGatt != null) {
            Log.i("Bluetooth!", "GATT connection initiated")
            return ConnectionState.CONNECTED
        } else {
            Log.e("Bluetooth!", "Failed to initiate GATT connection")
            return ConnectionState.DISCONNECTED_WITHOUT_INTENT
        }
    }


    @SuppressLint("MissingPermission")
    fun disconnect() {
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        bluetoothGatt = null
        isDiscoveryCompleted = false
        Log.i("Bluetooth!", "Disconnected from GATT server.")
    }
}

// I had an issue with trying to figure out how to share the class for the BleManager
// Ended up just adding this function straight into the code.
// Code is used but function is redundant.
@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BluetoothScreen() {
    val context = LocalContext.current
    val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    val bluetoothAdapter = bluetoothManager.adapter

    // Permissions
    val permissionState = rememberPermissionState(Manifest.permission.BLUETOOTH)
    val fineLocationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    val discoveredDevices = remember { mutableStateOf(mutableSetOf<BleDevice>()) } // Using Set for unique devices
    var isScanning by remember { mutableStateOf(false) }
    var dots by remember { mutableIntStateOf(0) }

    var bleManager: BleManager? = null

    // Dots animation for scanning indication
    LaunchedEffect(isScanning) {
        if (isScanning) {
            while (isScanning) {
                delay(500)
                dots = (dots + 1) % 4
            }
        } else {
            dots = 0
        }
    }

    // Handle discovery timeout
    LaunchedEffect(isScanning) {
        if (isScanning) {
            discoveredDevices.value.clear() // Clear previous devices
            delay(12000) // Scanning timeout
            bluetoothAdapter.cancelDiscovery()
            isScanning = false
            Log.i("Bluetooth!", "Discovery timed out")
        }
    }

    // BLE scan callback
    val leScanCallback = rememberUpdatedState(object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: android.bluetooth.le.ScanResult) {
            super.onScanResult(callbackType, result)
            val device = result.device
            if (device.name != null) {
                discoveredDevices.value.add(BleDevice(device = device))
            }
        }

        override fun onBatchScanResults(results: List<android.bluetooth.le.ScanResult>) {
            super.onBatchScanResults(results)
            results.forEach { result ->
                val device = result.device
                if (device.name != null) {
                    discoveredDevices.value.add(BleDevice(device = device))
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.e("Bluetooth!", "Scan failed with error code: $errorCode")
        }
    })

    // Start or stop scanning
    fun toggleScan() {
        val scanner = bluetoothManager.adapter.bluetoothLeScanner
        if (isScanning) {
            scanner.stopScan(leScanCallback.value)
            isScanning = false
        } else {
            scanner.startScan(leScanCallback.value)
            isScanning = true
        }
    }

    // UI layout

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!fineLocationPermissionState.status.isGranted || !permissionState.status.isGranted) {
                Text(
                    "Permissions denied.",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", context.packageName, null)
                        intent.data = uri
                        context.startActivity(intent)
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Go to Settings", textAlign = TextAlign.Center)
                }
            } else {
                Button(
                    onClick = { toggleScan() },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(if (isScanning) "Stop Scanning${".".repeat(dots)}" else "Start Scanning", textAlign = TextAlign.Center)
                }

                Button(
                    onClick = { bleManager?.moveServo() },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Move Servo", textAlign = TextAlign.Center)
                }

                if (isScanning) {
                    // Optional loading indicator can go here
                } else if (discoveredDevices.value.isNotEmpty()) {
                    Text(
                        "--Discovered Devices--",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(discoveredDevices.value.toList()) { bleDevice -> // Convert Set to List for LazyColumn
                            Box(
                                modifier = Modifier
                                    .clickable {
                                        // Made a system were only one device can be connect at a time
                                        bleManager?.disconnect() // Disconnect previous
                                        bleManager = BleManager(context, bleDevice) // Set new
                                        bleManager?.connectToDevice() // Connect new
                                    }
                                    .padding(vertical = 8.dp)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${bleDevice.device?.name} || ${bleDevice.device?.address}",
                                    textAlign = TextAlign.Center,
                                    color = when (bleDevice.state) {
                                        ConnectionState.CONNECTED -> Color.Green
                                        ConnectionState.DISCONNECTED_WITHOUT_INTENT -> Color.Red
                                        else -> Color.Blue
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                } else {
                    Text("No devices discovered.", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}
//***************************************** MENUS

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QuizApp(context: Context) {
    var appState by remember { mutableStateOf<AppState>(AppState.QuizHome) } // Switch to test/quizhome
    val audioPlayer = remember { AudioPlayer(context, R.raw.greeting1) }
    audioPlayer.setVolume(0.4f)
    val audioPlayer1 = remember { AudioPlayer(context, R.raw.buttonsound) }
    val audioPlayer2 = remember { AudioPlayer(context, R.raw.thememusic) }
    audioPlayer2.setVolume(0.5f)
    val audioPlayer3 = remember { AudioPlayer(context, R.raw.alert) }
    val audioPlayer4 = remember { AudioPlayer(context, R.raw.alarm) }

    audioPlayer4.setLooping(true)

    var showDialogExitQuizEarly by remember { mutableStateOf(false) }
    var showDialogSubmitThankYou by remember { mutableStateOf(false) }
    var save by remember { mutableStateOf<List<QuizQuestion>>(listOf()) }
    var saveNumber by remember { mutableStateOf(0) }

    val viewModel: MainViewModel = hiltViewModel()

    // Update the viewmodel with the emotion and after delay change it to null
    LaunchedEffect (GlobalData.emotionResult) {
        viewModel.updateEmotion(GlobalData.emotionResult)
        delay(5000)
        viewModel.updateEmotion(null)
    }

    var passwordCheck by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }

    // Stuff for handling Bluetooth
    val context = LocalContext.current
    val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    val bluetoothAdapter = bluetoothManager.adapter

    // Permissions
    val permissionState = rememberPermissionState(Manifest.permission.BLUETOOTH)
    val fineLocationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    val discoveredDevices = remember { mutableStateOf(mutableSetOf<BleDevice>()) } // Using Set for unique devices
    var isScanning by remember { mutableStateOf(false) }
    var dots by remember { mutableIntStateOf(0) }

    // var bleManager: BleManager? = null
    var bleManager by remember { mutableStateOf(BleManager(context, deviceData = BleDevice())) }

    val emotionDetection = EmotionDetection

    // Handles the misuse cases
    LaunchedEffect(Unit) {
        var warning = true
        val volumeMax = 10
        val volumeDefault = 5

        while (true) {
            when (appState) {
                AppState.Quiz -> {
                    if (viewModel.isMissuesState()) {
                        delay(500L)
                        if (!(viewModel.isMissuesState())) continue
                        audioPlayer2.stop()
                        viewModel.volumeControl(volumeMax)
                        if (warning) {
                            delay(100)
                            viewModel.speech("Please do not touch me")
                            if (!(viewModel.isMissuesState())) continue
                            viewModel.speech("I am warning you, if you do not stop touching me I will scream")
                            if (!(viewModel.isMissuesState())) continue
                            viewModel.speech("3")
                            if (!(viewModel.isMissuesState())) continue
                            viewModel.speech("2")
                            if (!(viewModel.isMissuesState())) continue
                            viewModel.speech("1")
                            if (!(viewModel.isMissuesState())) continue
                            warning = false
                        }
                        if (!warning) {
                            audioPlayer4.play()
                        }
                    } else {
                        viewModel.volumeControl(volumeDefault)
                        delay(500)
                        if (appState == AppState.Quiz) {
                            audioPlayer2.play()
                        }
                        audioPlayer4.stop()
                        audioPlayer4.reset()
                        warning = true
                    }
                }
                AppState.QuizHome -> {
                    if (viewModel.isMissuesState()) {
                        delay(500L)
                        if (!(viewModel.isMissuesState())) continue
                        audioPlayer.stop()
                        viewModel.volumeControl(volumeMax)
                        if (warning) {
                            delay(100)
                            viewModel.speech("Please do not touch me")
                            if (!(viewModel.isMissuesState())) continue
                            viewModel.speech("I am warning you, if you do not stop touching me I will scream")
                            if (!(viewModel.isMissuesState())) continue
                            viewModel.speech("3")
                            if (!(viewModel.isMissuesState())) continue
                            viewModel.speech("2")
                            if (!(viewModel.isMissuesState())) continue
                            viewModel.speech("1")
                            if (!(viewModel.isMissuesState())) continue
                            warning = false
                        }
                        if (!warning) {
                            audioPlayer4.play()
                        }
                    } else {
                        viewModel.volumeControl(volumeDefault)
                        delay(100)
                        if (appState == AppState.QuizHome) {
                            audioPlayer.play()
                        }
                        audioPlayer4.stop()
                        audioPlayer4.reset()
                        warning = true
                    }
                }
                AppState.ScoreBoard -> {
                    if (viewModel.isMissuesState()) {
                        delay(500L)
                        if (!(viewModel.isMissuesState())) continue
                        audioPlayer.stop()
                        viewModel.volumeControl(volumeMax)
                        if (warning) {
                            delay(100)
                            viewModel.speech("Please do not touch me")
                            if (!(viewModel.isMissuesState())) continue
                            viewModel.speech("I am warning you, if you do not stop touching me I will scream")
                            if (!(viewModel.isMissuesState())) continue
                            viewModel.speech("3")
                            if (!(viewModel.isMissuesState())) continue
                            viewModel.speech("2")
                            if (!(viewModel.isMissuesState())) continue
                            viewModel.speech("1")
                            if (!(viewModel.isMissuesState())) continue
                            warning = false
                        }
                        if (!warning) {
                            audioPlayer4.play()
                        }
                    } else {
                        viewModel.volumeControl(volumeDefault)
                        delay(100)
                        if (appState == AppState.ScoreBoard) {
                            audioPlayer.play()
                        }
                        audioPlayer4.stop()
                        audioPlayer4.reset()
                        warning = true
                    }
                }
                AppState.Test -> {}
                AppState.Bluetooth -> {}
            }

            delay(100L)
        }
    }

    //Emotion
    LaunchedEffect(Unit) {

    }

    // Used to create a job to create a timeout system
    var timeoutJob by remember { mutableStateOf<Job?>(null) }

    // Triggered each time `appState` changes
    LaunchedEffect(appState) {
        // Cancel any existing job to reset the timer
        timeoutJob?.cancel()

        if (appState != AppState.QuizHome) {
            // Start a new timer job for the current `appState`
            timeoutJob = launch {
                delay(300000L) // Wait for 5 min
                audioPlayer2.stop()
                appState = AppState.QuizHome // Set back to QuizHome if timeout completes
            }
        }
    }

        when (appState) {
            is AppState.Test -> {
                Test(context)
            }

            is AppState.QuizHome -> { //*** HOME SCREEN
                viewModel.idleMode(true)
                audioPlayer.play()
                audioPlayer.setLooping(true)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = 120.dp, start = 30.dp
                        )
                        .zIndex(5f), // Higher ZIndex value places it on top, // Padding around the Box
                    contentAlignment = Alignment.TopStart // Align content to top end (right)
                ) {
                    BoxWithClickable(
                        onClick = {
                            appState = AppState.ScoreBoard;
                        },
                        text = stringResource(R.string.score_board),
                        type = "cool",
                        grow = false,
                        animatedPress = true,
                        cycleColor = false,
                        modifier = Modifier.background(Color.LightGray) // Background color for the BoxWithClickable
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            bottom = 120.dp, start = 30.dp
                        )
                        .zIndex(5f), // Higher ZIndex value places it on top, // Padding around the Box
                    contentAlignment = Alignment.BottomStart // Align content to top end (right)
                ) {
                    BoxWithClickable(
                        onClick = {
                            passwordCheck = true
                            // appState = AppState.Bluetooth;
                        },
                        text = "BLE",
                        type = "cool",
                        grow = false,
                        animatedPress = true,
                        cycleColor = false,
                        modifier = Modifier.background(Color.LightGray) // Background color for the BoxWithClickable
                    )
                }

                // Dialog to ask for password input
                if (passwordCheck) {
                    if (password == "1234") Log.d("Input", password)
                    AlertDialog(onDismissRequest = { passwordCheck = false },
                        title = { Text(stringResource(R.string.enter_password)) },
                        text = {
                            Column {
                                OutlinedTextField(value = password,
                                    onValueChange = { password = it },
                                    label = { Text(stringResource(R.string.password)) },
                                    visualTransformation = PasswordVisualTransformation(),
                                    singleLine = true
                                )
                            }
                        },
                        confirmButton = {
                            Button(onClick = {
                                if (password == "1234") {
                                    appState = AppState.Bluetooth
                                    // Reset password and state
                                    passwordCheck = false
                                    password = ""
                                }
                            }) {
                                Text(stringResource(R.string.confirm))
                            }
                        },
                        dismissButton = {
                            Button(onClick = {
                                passwordCheck = false
                                password = ""
                            }) {
                                Text(stringResource(R.string.cancel))
                            }
                        })
                }

                if (showDialogSubmitThankYou) {
                    // Launch an effect to close the dialog after a delay
                    LaunchedEffect(Unit) {
                        delay(60000L) // Delay in milliseconds (e.g., 60 seconds)
                        showDialogSubmitThankYou = false
                    }

                    var userInput by remember { mutableStateOf("") }
                    viewModel.resultSpeech(state = SpeechState.THANKYOU, say = "Thank you for doing this quiz, please add your name so it can be added to my scoreboard.")
                    if (save.sumOf { it.result.score } == save.sumOf { it.result.outOf }) {
                        viewModel.resultSpeech(state = SpeechState.THANKYOU, say = "I see you have done very well on my quiz. Here, have a chocolate on me.  Please add your name so your glory can be saved and shown to all.")
                        bleManager?.moveServo()
                    }
                    AlertDialog(onDismissRequest = { showDialogSubmitThankYou = false },
                        title = { Text(stringResource(R.string.thank_you)) },
                        text = {
                            Column {
                                Text(when {
                                    save.sumOf { it.result.score } == save.sumOf { it.result.outOf } -> {
                                        stringResource(R.string.you_got) + " ${save.sumOf { it.result.score }}/${save.sumOf { it.result.outOf }}. " + stringResource(
                                            R.string.well_done
                                        )
                                    }

                                    save.sumOf { it.result.score } >= save.sumOf { it.result.outOf } / 2 -> {
                                        stringResource(R.string.you_got) + " ${save.sumOf { it.result.score }}/${save.sumOf { it.result.outOf }}. " + stringResource(
                                            R.string.good_try
                                        )
                                    }

                                    else -> {
                                        stringResource(R.string.you_got) + " ${save.sumOf { it.result.score }}/${save.sumOf { it.result.outOf }}. " + stringResource(
                                            R.string.keep_trying
                                        )
                                    }
                                }
                                )
                                OutlinedTextField(value = userInput,
                                    onValueChange = { userInput = it },
                                    label = { Text(stringResource(R.string.input)) },
                                    singleLine = true
                                )
                            }
                        },
                        confirmButton = {
                            Button(onClick = {
                                showDialogSubmitThankYou = false

                                // Find the last save
                                saveNumber = 0
                                while (PreferencesManager.getQuizQuestions(
                                        context, "save$saveNumber"
                                    ) != null
                                ) {
                                    saveNumber++
                                }

                                // Retrieve and update the last quiz data
                                val temp = PreferencesManager.getQuizQuestions(
                                    context, "save${saveNumber - 1}"
                                )

                                userInput = userInput.split(" ").firstOrNull().toString()

                                if (temp != null && userInput.isNotEmpty() && !getBlacklist(context).contains(
                                        userInput.lowercase()
                                    ) && (userInput.length <= 20)
                                ) { // Save user input
                                    temp.forEach { it.name = userInput }
                                    PreferencesManager.saveQuizQuestions(
                                        context, "save${saveNumber - 1}", temp
                                    )
                                } else if (temp != null) {
                                    temp.forEach { it.name = getWhitelist(context).random() }
                                    PreferencesManager.saveQuizQuestions(
                                        context, "save${saveNumber - 1}", temp
                                    )
                                }

                                // Debugging Log
                                Log.d(
                                    "User Name Check", "${
                                        PreferencesManager.getQuizQuestions(
                                            context, "save${saveNumber - 1}"
                                        )
                                    }"
                                )

                            }) {
                                Text(stringResource(R.string.ok))
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showDialogSubmitThankYou = false }) {
                                Text(stringResource(R.string.cancel))
                            }
                        })
                }

                QuizHome(onStartClicked = {
                    appState = AppState.Quiz;
                    audioPlayer.setLooping(false)
                    audioPlayer.stop();
                    audioPlayer1.play();
                }, audioPlayer, context)
            }

            is AppState.ScoreBoard -> {
                viewModel.idleMode(false)
                viewModel.resultSpeech(state = SpeechState.SCOREBOARD, say = "Welcome to the Hall of Fame. Come see the many people who have done my Quiz")
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = 120.dp, start = 100.dp
                        )
                        .zIndex(5f), // Higher ZIndex value places it on top, // Padding around the Box
                    contentAlignment = Alignment.TopStart // Align content to top end (right)
                ) {
                    BoxWithClickable(
                        onClick = {
                            appState = AppState.QuizHome;
                        },
                        text = stringResource(R.string.exit),
                        type = "cool",
                        grow = false,
                        animatedPress = true,
                        cycleColor = false,
                        modifier = Modifier.background(Color.LightGray) // Background color for the BoxWithClickable
                    )
                }
                ScoreBoard()
            }

            is AppState.Quiz -> { //*** QUIZ
                viewModel.idleMode(false)
                if (!showDialogExitQuizEarly) {
                    audioPlayer2.setLooping(true)
                    audioPlayer2.play()
                }

                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Dialog used to make sure if someone want to leave the quiz early
                    if (showDialogExitQuizEarly) {
                        audioPlayer2.setLooping(false)
                        audioPlayer2.pause()
                        audioPlayer3.play()
                        viewModel.resultSpeech(state = SpeechState.EXIT_EARLY, say = "Hang on, you are about to submit this quiz without completing all the questions. If you do this, your quiz will not be valid. Are you sure you want to do this?")
                        AlertDialog(onDismissRequest = { showDialogExitQuizEarly = false },
                            title = { Text(stringResource(R.string.warning)) },
                            text = { Text(stringResource(R.string.warning_message)) },
                            confirmButton = {
                                Button(onClick = {
                                    audioPlayer1.play();
                                    audioPlayer2.reset();
                                    appState = AppState.QuizHome
                                    showDialogExitQuizEarly = false
                                    // Handle exit logic here (e.g., navigate away, save state, etc.)
                                }) {
                                    Text(stringResource(R.string.ok))
                                }
                            },
                            dismissButton = {
                                Button(onClick = {
                                    showDialogExitQuizEarly = false
                                    // Handle cancel logic here if needed
                                }) {
                                    Text(stringResource(R.string.cancel))
                                }
                            })
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                top = 80.dp, end = 30.dp
                            )
                            .zIndex(5f), // Higher ZIndex value places it on top, // Padding around the Box
                        contentAlignment = Alignment.TopEnd // Align content to top end (right)
                    ) {
                        BoxWithClickable(
                            onClick = {
                                appState = AppState.QuizHome;
                                audioPlayer1.play();
                                audioPlayer2.stop();
                            },
                            text = stringResource(R.string.exit_quiz),
                            type = "cool",
                            grow = false,
                            animatedPress = true,
                            cycleColor = false,
                            modifier = Modifier
                                .clip(RoundedCornerShape(2.dp)) // Rounds the corners
                                .border(
                                    width = 2.dp,
                                    color = Color.Black,
                                    shape = RoundedCornerShape(5.dp) // Adjust shape as needed
                                ) // Adds a border
                                .padding(8.dp) // Padding inside the box
                                .background(Color.LightGray) // Background color for the BoxWithClickable
                        )
                    }
                    Quiz(context,
                        onShowOverlay = { showDialogExitQuizEarly = true },
                        save = { updatedQuestions -> // This gets the saved data from the quiz
                            save = updatedQuestions
                            Log.d("Save Check2", "${save[0].result}")
                        },
                        onStartClicked = {
                            saveNumber = 0;
                            // Check the memory and if current spot not empty increment the saveNumber
                            while (PreferencesManager.getQuizQuestions(
                                    context, "save$saveNumber"
                                ) != null
                            ) saveNumber++
                            // If valid save spot found, save new memory at that spot
                            Log.d("Save Check", "${save}")
                            PreferencesManager.saveQuizQuestions(
                                context, "save$saveNumber", save
                            ); // This saves the data
                            Log.d(
                                "Saved",
                                "${PreferencesManager.getQuizQuestions(context, "save$saveNumber")}"
                            )
                            showDialogSubmitThankYou = true
                            appState = AppState.QuizHome;
                            audioPlayer2.setLooping(false);
                            audioPlayer2.stop();
                            audioPlayer1.play();
                        })
                }
            }

            is AppState.Bluetooth -> {
                viewModel.idleMode(false)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            bottom = 120.dp, start = 30.dp
                        )
                        .zIndex(5f), // Higher ZIndex value places it on top, // Padding around the Box
                    contentAlignment = Alignment.BottomStart // Align content to top end (right)
                ) {
                    BoxWithClickable(
                        onClick = {
                            appState = AppState.QuizHome;
                        },
                        text = "Home",
                        type = "cool",
                        grow = false,
                        animatedPress = true,
                        cycleColor = false,
                        modifier = Modifier.background(Color.LightGray) // Background color for the BoxWithClickable
                    )
                }

                // Dots animation for scanning indication
                LaunchedEffect(isScanning) {
                    if (isScanning) {
                        while (isScanning) {
                            delay(500)
                            dots = (dots + 1) % 4
                        }
                    } else {
                        dots = 0
                    }
                }

                // Handle discovery timeout
                LaunchedEffect(isScanning) {
                    if (isScanning) {
                        discoveredDevices.value.clear() // Clear previous devices
                        delay(12000) // Scanning timeout
                        bluetoothAdapter.cancelDiscovery()
                        isScanning = false
                        Log.i("Bluetooth!", "Discovery timed out")
                    }
                }

                // BLE scan callback
                val leScanCallback = rememberUpdatedState(object : ScanCallback() {
                    override fun onScanResult(callbackType: Int, result: android.bluetooth.le.ScanResult) {
                        super.onScanResult(callbackType, result)
                        val device = result.device
                        if (device.name != null) {
                            discoveredDevices.value.add(BleDevice(device = device))
                        }
                    }

                    override fun onBatchScanResults(results: List<android.bluetooth.le.ScanResult>) {
                        super.onBatchScanResults(results)
                        results.forEach { result ->
                            val device = result.device
                            if (device.name != null) {
                                discoveredDevices.value.add(BleDevice(device = device))
                            }
                        }
                    }

                    override fun onScanFailed(errorCode: Int) {
                        super.onScanFailed(errorCode)
                        Log.e("Bluetooth!", "Scan failed with error code: $errorCode")
                    }
                })

                // Start or stop scanning
                fun toggleScan() {
                    val scanner = bluetoothManager.adapter.bluetoothLeScanner
                    if (isScanning) {
                        scanner.stopScan(leScanCallback.value)
                        isScanning = false
                    } else {
                        scanner.startScan(leScanCallback.value)
                        isScanning = true
                    }
                }

                // UI layout

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (!fineLocationPermissionState.status.isGranted || !permissionState.status.isGranted) {
                            Text(
                                "Permissions denied.",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Button(
                                onClick = {
                                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    val uri = Uri.fromParts("package", context.packageName, null)
                                    intent.data = uri
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("Go to Settings", textAlign = TextAlign.Center)
                            }
                        } else {
                            Button(
                                onClick = { toggleScan() },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text(if (isScanning) "Stop Scanning${".".repeat(dots)}" else "Start Scanning", textAlign = TextAlign.Center)
                            }

                            Button(
                                onClick = { bleManager?.moveServo() },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("Move Servo", textAlign = TextAlign.Center)
                            }

                            if (isScanning) {
                                // Optional loading indicator can go here
                            } else if (discoveredDevices.value.isNotEmpty()) {
                                Text(
                                    "--Discovered Devices--",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                LazyColumn(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    items(discoveredDevices.value.toList()) { bleDevice -> // Convert Set to List for LazyColumn
                                        Box(
                                            modifier = Modifier
                                                .clickable {
                                                    // Made a system were only one device can be connect at a time
                                                    bleManager?.disconnect() // Disconnect previous
                                                    bleManager = BleManager(context, bleDevice) // Set new
                                                    bleManager?.connectToDevice() // Connect new
                                                }
                                                .padding(vertical = 8.dp)
                                                .fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${bleDevice.device?.name} || ${bleDevice.device?.address}",
                                                textAlign = TextAlign.Center,
                                                color = when (bleDevice.state) {
                                                    ConnectionState.CONNECTED -> Color.Green
                                                    ConnectionState.DISCONNECTED_WITHOUT_INTENT -> Color.Red
                                                    else -> Color.Blue
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                            )
                                        }
                                    }
                                }
                            } else {
                                Text("No devices discovered.", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun Test(context: Context) {

        // State to hold the result from MultipleChoiceQuestion
        var result by remember { mutableStateOf<ResultForOption?>(null) }

        // Define a callback function to handle results
        val handleResult: (ResultForOption) -> Unit = { resultFromChild ->
            result = resultFromChild
            // You can perform additional actions here with the result
            Log.d("ParentComposable", "Score: ${resultFromChild.score}")
            Log.d("ParentComposable", "Toggled List: ${resultFromChild.isToggleList}")
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(
                    top = 30.dp, start = 30.dp
                ), // Padding around the Box
            contentAlignment = Alignment.TopStart, // Align content to top end (right)
        ) {
            Text(
                text = stringResource(R.string.test_page_welcome_message),
                color = Color.Black,
                modifier = Modifier.padding(bottom = 50.dp)
            )
        }

        var onSubmitted by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//        MultipleChoiceQuestion(
//            text = "What Country is the Gumtree from?",
//            textQuestion = true,
//            imageQuestion = true,
//            onSubmitted = {
//                onSubmitted
//            },
//            texts = listOf(
//                question("Australia", false),
//                question("New Zealand", false),
//                question("South Africa", false),
//                question("Brazil", false),
//                question("Canada", false)
//            ),
//            columnLength = 2,
//            scoreType = "s",
//            singleOn = true,
//            onCorrectOn = false,
//            surveyOn = true,
//            onResult = handleResult, // Pass the callback here
//            context = context
//        )
        }
        Row {
            Button( // THIS IS THE IMPORTANT BUTTON
                onClick = {
                    // Handle the submit button press
                    onSubmitted = true
                }) {
                if (onSubmitted) Text(text = "Submitted") else Text(text = "Submit Answer")
            }
        }
    }

    @Composable
    fun ScoreBoard() {
        val context = LocalContext.current
        var saveNumber by remember { mutableStateOf(0) }
        var quizQuestions by remember { mutableStateOf<List<QuizQuestion>?>(null) }
        var message by remember { mutableStateOf("Loading...") }
        var currentIndex by remember { mutableStateOf(0) } // Keeps track of the current position in the list

        var numberQuestions by remember { mutableStateOf(0) }
        var outOfTemp by remember { mutableStateOf(0) }
        var scoreTemp by remember { mutableStateOf(0) }
        var nameTemp by remember { mutableStateOf("Empty") }
        var ranking by remember { mutableStateOf<List<Ranking>>(emptyList()) }

        LaunchedEffect(Unit) {
            val initialQuestions = PreferencesManager.getQuizQuestions(context, "save0")
            if (initialQuestions == null) {
                message = "Nothing stored here"
            } else {
                val loadedQuestions = mutableListOf<QuizQuestion>()
                while (PreferencesManager.getQuizQuestions(context, "save$saveNumber") != null) {
                    val questions = PreferencesManager.getQuizQuestions(context, "save$saveNumber")
                    if (questions != null) {
                        loadedQuestions.addAll(questions)
                    }
                    saveNumber++
                }
                quizQuestions = loadedQuestions
                message = "Loaded ${loadedQuestions.size} questions."
                quizQuestions?.let { questions -> // This will get the number of questions in the quiz
                    if (numberQuestions < questions.size) {
                        Log.d("Checking loop", questions[numberQuestions].text)
                        Log.d("Checking loop", "questions.size: ${questions.size}")

                        // Increment numberQuestions while the condition holds and within bounds
                        while (numberQuestions < questions.size && questions[numberQuestions].text == "Quiz${numberQuestions + 1}") {
                            numberQuestions++
                        }

                        // Process the questions in batches
                        while (currentIndex < questions.size) {
                            // Initialize temporary variables to accumulate scores
                            nameTemp = questions[currentIndex].name
                            outOfTemp = 0
                            scoreTemp = 0 // Reset scoreTemp before each batch

                            // Repeat for up to 9 items, but ensure you don't go out of bounds
                            repeat(minOf(numberQuestions, questions.size - currentIndex)) {
                                scoreTemp += questions[currentIndex].result.score
                                outOfTemp += questions[currentIndex].result.outOf
                                currentIndex++
                            }

                            ranking += Ranking(score = scoreTemp, name = nameTemp)
                        }
                        ranking = ranking.sortedByDescending { it.score }
                    } else {
                        Log.d("Error", "Invalid number of questions or index out of bounds.")
                    }
                }
            }
        }

        // PasswordPromptBox(clear = false)
        // A map to store the press counts for each ranking item
        val pressCounts = remember { mutableStateOf(mutableMapOf<Int, Int>()) }
        var passwordCheck by remember { mutableStateOf(false) }
        var password by remember { mutableStateOf("") }
        var removeAtIndex by remember { mutableStateOf(0) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp), // Fills the entire screen
            contentAlignment = Alignment.Center // Centers the content inside the Box
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp) // Optional: add padding around the content
            ) {
                itemsIndexed(ranking) { index, rank ->
                    Log.d("Error_Fix", "$rank")
                    Button(
                        onClick = {
                            val currentCount = pressCounts.value[index] ?: 0
                            val newCount = currentCount + 1
                            pressCounts.value = pressCounts.value.toMutableMap().apply {
                                this[index] = newCount
                            }

                            if (newCount >= 5) {
                                // Trigger the desired event
                                // For example, show a toast or navigate to another screen
                                Log.d("RankingDisplay", "Rank ${index + 1} pressed 5 times")
                                passwordCheck = true
                                pressCounts.value = pressCounts.value.toMutableMap().apply {
                                    this[index] = 0
                                    removeAtIndex = index
                                }
                            }

                        }, modifier = Modifier.fillMaxWidth() // Make buttons take up the full width
                    ) {
                        Text(
                            text = "${index + 1} - ${rank.name}: ${rank.score}/$outOfTemp",
                            fontSize = 50.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp)) // Optional: add space between buttons
                }
            }
        }

        var removeNames by remember { mutableStateOf("") }
        var preferenceIndex by remember { mutableStateOf(0) }

        // Dialog to ask for password input
        if (passwordCheck) {
            if (password == "1234") Log.d("Input", password)
            AlertDialog(onDismissRequest = { passwordCheck = false },
                title = { Text(stringResource(R.string.enter_password)) },
                text = {
                    Column {
                        OutlinedTextField(value = password,
                            onValueChange = { password = it },
                            label = { Text(stringResource(R.string.password)) },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (password == "1234") {
                            val removeName =
                                ranking[removeAtIndex].name.lowercase() // Changed to val for immutability

                            // Iterate through preferences
                            while (PreferencesManager.getQuizQuestions(
                                    context, "save$preferenceIndex"
                                ) != null
                            ) {
                                var temp = PreferencesManager.getQuizQuestions(
                                    context, "save$preferenceIndex"
                                )?.toMutableList() // Convert to mutable list if needed
                                temp?.let {
                                    repeat(numberQuestions) { i ->
                                        if (it.getOrNull(i)?.name?.lowercase() == removeName) {
                                            it[i] =
                                                it[i].copy(name = "Empty") // Ensure that the list is mutable and items are copyable
                                        }
                                    }
                                    PreferencesManager.saveQuizQuestions(
                                        context, "save$preferenceIndex", it
                                    ) // Save back the updated list
                                }
                                preferenceIndex++
                            }

                            // Reset password and state
                            passwordCheck = false
                            password = ""
                        }
                    }) {
                        Text(stringResource(R.string.confirm))
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        passwordCheck = false
                        password = ""
                    }) {
                        Text(stringResource(R.string.cancel))
                    }
                })
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun QuizHome(onStartClicked: () -> Unit, audioPlayer: AudioPlayer, context: Context) {
        // This is how to make a list.
        // These are used for the image cycle
        val imageResIds = listOf(
//        R.drawable.unknown
            R.drawable.image1,
            R.drawable.image2,
            R.drawable.image3,
            R.drawable.image4,
            R.drawable.image5,
        )

        // Define animation state for scaling
        var isGrowing by remember { mutableStateOf(true) }
        // AnimationSpec for scaling
        val scale by animateFloatAsState(
            targetValue = if (isGrowing) 0.6f else 0.5f, animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        var robotController = RobotController()

//    viewModelScope.launch {
//        detectionState.collect { detectionState: DetectionState ->
//
//            if (detectionState == DetectionState.DETECTED && ttsStatus.value.status == TtsRequest.Status.COMPLETED) { // If Temi detects someone
//                Log.i("check", "hi")
//                textModelChoice() // Say a text line using the made model
//                while (ttsStatus.value.status != TtsRequest.Status.COMPLETED) // Wait until Temi done talking
//                    delay(responseRefreshRate)
//            }
//    }

        DropdownMenuSample(audioPlayer)

        // Update `isGrowing` state to trigger scaling animation
        LaunchedEffect(Unit) {
            // Toggle `isGrowing` state to repeat the animation
            while (true) {
                isGrowing = !isGrowing
                delay(2000.toLong())
            }
        }

        PasswordPromptBox(clear = false)

        PasswordPromptBox(clear = true)

        Box(
            modifier = Modifier.fillMaxSize()
            //.graphicsLayer(scaleX = scale + 0.3f, scaleY = scale + 0.3f, alpha = fade), // Apply scale,
        ) {
            ImageCycler(imageResIds, fadeTrue = true)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .graphicsLayer(scaleX = scale, scaleY = scale), // Apply scale,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BoxWithClickable(
                onClick = onStartClicked,
                text = stringResource(R.string.enter_quiz_prompt),
                type = "null",
                grow = false,
                cycleColor = true,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp)) // Rounds the corners
                    .border(
                        width = 20.dp, color = Color.Black, shape = RoundedCornerShape(20.dp)
                    ) // Adds a border
                    .padding(16.dp) // Padding inside the box
            )
        }
    }

    @SuppressLint("UnrememberedMutableState")
    @Composable
    fun Quiz(
        context: Context,
        save: (List<QuizQuestion>) -> Unit,
        onShowOverlay: () -> Unit,
        onStartClicked: () -> Unit,
    ) {
        val audioPlayer = remember { AudioPlayer(context, R.raw.buttonsound) }
        var showDialogSubmitWithNoAnswer by remember { mutableStateOf(false) }
        //*******************************
        // State to hold the result from MultipleChoiceQuestion
        var result by remember { mutableStateOf<ResultForOption?>(null) }

        val coroutineScope = rememberCoroutineScope()

        //*******************************

        DisposableEffect(Unit) {
            onDispose {
                audioPlayer.release() // Release resources when composable is disposed
            }
        }

        fun extractTextBetweenBrackets(line: String): String {
            // Define a regular expression to match text between < and >
            val regex = "<(.*?)>".toRegex()

            // Find all occurrences that match the pattern
            val matches = regex.findAll(line)

            // Extract and return the first valid content inside the brackets, excluding 'end'
            return matches.mapNotNull { match ->
                val content = match.groupValues[1]
                if (content.isNotEmpty() && content != "end") content else null
            }.firstOrNull() ?: "" // Return the first valid result or an empty string if none found
        }

        val resourceId = ResourceManager.getResource()
        val userMadeQuiz by remember { mutableStateOf(extractTXT(context, resourceId)) }
        // Initialize the list of quiz questions as empty
        var quizQuestions by remember {
            mutableStateOf(mutableStateListOf<QuizQuestion>())
        }
        var trigger by remember { mutableStateOf(true) }

        if (trigger) {
            repeat(userMadeQuiz.size) { i ->
                // Extract text between brackets
                val extractedText = extractTextBetweenBrackets(userMadeQuiz[i])

                // Check if there is a valid extracted text
                if (extractedText.isNotEmpty()) {
                    // Add to quizQuestions
                    quizQuestions += QuizQuestion(extractedText)
                }
            }

            // Log the size of quizQuestions list
            Log.d("test", "${quizQuestions.size}")

            // Set trigger to false after processing
            trigger = false
        }

        val random by remember { mutableStateOf(true) } // this will allow randomising of
        // questions
        // Non random does not work

        fun generateRandomSequence(start: Int, end: Int): List<Int> {
            require(start <= end) { "Start must be less than or equal to end" }

            // Create a list of numbers from start to end
            val numbers = (start..end).toList()

            // Determine if we should shuffle or not based on the `random` flag
            val shuffledNumbers = if (random) {
                numbers.shuffled(Random(System.currentTimeMillis()))
            } else {
                numbers
            }

            // Append end + 1 to the shuffled list
            // Essentially, this make it so that last question is always int hes ame spot
            val result = shuffledNumbers + (end + 1)

            return result
        }

        var randomQuestionList by remember {
            mutableStateOf(
                generateRandomSequence(
                    0, quizQuestions.size - 2
                )
            )
        }
        var indexForRandom by remember { mutableStateOf(0) }

        var questionIndex by remember { mutableStateOf(randomQuestionList.firstOrNull() ?: 0) }
        val currentQuestion by derivedStateOf { quizQuestions[questionIndex] } // need this otherwise...
        // the system does not update properly

        val viewModel: MainViewModel = hiltViewModel()

        // Update the viewmodel with the emotion and after delay change it to null
        LaunchedEffect (GlobalData.emotionResult) {
            viewModel.updateEmotion(GlobalData.emotionResult)
            delay(5000)
            viewModel.updateEmotion(null)
        }

        if (showDialogSubmitWithNoAnswer) {
            viewModel.resultSpeech(state = SpeechState.EXIT_EARLY, say = "Hang on, you are about to submit this question without adding an answer. Are you sure you want to do this?")
            AlertDialog(onDismissRequest = { showDialogSubmitWithNoAnswer = false },
                title = { Text(stringResource(R.string.mild_warning)) },
                text = { Text(stringResource(R.string.you_have_not_selected_any_answers_are_you_sure_you_wish_to_proceed)) },
                confirmButton = {
                    Button(onClick = {
                        quizQuestions[questionIndex] = currentQuestion.copy(isSubmitted = true)
                        showDialogSubmitWithNoAnswer = false
                        // Handle exit logic here (e.g., navigate away, save state, etc.)
                    }) {
                        Text(stringResource(R.string.ok))
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showDialogSubmitWithNoAnswer = false
                        // Handle cancel logic here if needed
                    }) {
                        Text(stringResource(R.string.cancel))
                    }
                })
        }

        fun updateIndex(update: Int) { // Used to update index
            if (!random) {
                val newIndex = (questionIndex + update).coerceIn(0, quizQuestions.size - 1)
                questionIndex = newIndex
            } else {
                val newIndex = (indexForRandom + update).coerceIn(0, quizQuestions.size - 1)
                indexForRandom = newIndex
                // Log the result
                Log.d("MyTag", "Random sequence: $indexForRandom")
                questionIndex = randomQuestionList[indexForRandom]
            }
        }

//    Box(
//        modifier = Modifier
//            .background(Color.White)
//            .padding(top = 100.dp, start = 30.dp)
//            .zIndex(5f), // Higher ZIndex value places it on top,
//        contentAlignment = Alignment.TopStart
//    ) {
////         Use for debugging
//        Column {
////            Text(stringResource(R.string.num_of_question, questionIndex + 1, quizQuestions.size))
////            Text("Score will be added here")
////            Text("${indexForRandom}")
////            Text("${randomQuestionList}")
//            Text("${questionIndex}")
//            Text("${quizQuestions[questionIndex]}")
//        }
//    }
        // This code section does the position of the button for question control

        var isButtonEnabled by remember { mutableStateOf(true) } // Control button's state


        Box(
            modifier = Modifier
                .fillMaxSize() // Fill the entire screen
                .padding(bottom = 50.dp)
                .zIndex(5f), // Higher ZIndex value places it on top,, // Optional padding from the top
            contentAlignment = Alignment.BottomStart // Align the content to the top center
        ) {
            Button(
                onClick = {

                    if (isButtonEnabled) {
                        isButtonEnabled = false
                        if (! viewModel.isMissuesState()) audioPlayer.play()

                        updateIndex(-1)

                        coroutineScope.launch {
                            delay(350)
                            isButtonEnabled = true
                        }

                    }

                }, modifier = Modifier
                    .width(175.dp) // Fixed width for button
                    .height(100.dp)
            ) {
                Text(text = if (indexForRandom != 0) stringResource(R.string.previous_question) else " ")
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize() // Fill the entire screen
                .padding(bottom = 50.dp)
                .zIndex(5f), // Higher ZIndex value places it on top,, // Optional padding from the top
            contentAlignment = Alignment.BottomCenter // Align the content to the top center
        ) {
            Button(
                onClick = {
                    if (! viewModel.isMissuesState()) audioPlayer.play()
//                audioPlayer.play() //not sure what this does
                    if (quizQuestions[questionIndex].result.isToggleList.all { !it } && !quizQuestions[questionIndex].isSubmitted) {
                        showDialogSubmitWithNoAnswer = true
                    } else {
                        quizQuestions[questionIndex] = currentQuestion.copy(isSubmitted = true)
                    }
                }, modifier = Modifier
                    .width(150.dp) // Fixed width for button
                    .height(100.dp)
            ) {
                Text(
                    text = if (currentQuestion.isSubmitted) stringResource(R.string.submitted) else stringResource(
                        R.string.submit_answer
                    )
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize() // Fill the entire screen
                .padding(bottom = 50.dp)
                .zIndex(5f), // Higher ZIndex value places it on top,, // Optional padding from the top
            contentAlignment = Alignment.BottomEnd // Align the content to the top center
        ) {
            Button(
                onClick = {
                    if (isButtonEnabled) {
                        isButtonEnabled = false

                        if (! viewModel.isMissuesState()) audioPlayer.play()
                        if ((questionIndex == quizQuestions.size - 1) && !quizQuestions.all { it.isSubmitted }) {
                            onShowOverlay()
                        } else if ((questionIndex == quizQuestions.size - 1) && quizQuestions.all { it.isSubmitted }) {
                            save(quizQuestions)
                            onStartClicked()
                        }
                        updateIndex(1)

                        coroutineScope.launch {
                            delay(350)
                            isButtonEnabled = true
                        }

                    }

                }, modifier = Modifier
                    .width(175.dp) // Fixed width for button
                    .height(100.dp)
            ) {
                Text(
                    text = if (questionIndex != quizQuestions.size - 1) stringResource(R.string.next_question) else stringResource(
                        R.string.finish_quiz
                    )
                )
            }
        }
        // ***********************************************************
        fun updateQuestionResult(index: Int, result: ResultForOption) {
            quizQuestions[index] = quizQuestions[index].copy(result = result)
        }

        fun getQuestionResult(index: Int): ResultForOption {
            return quizQuestions[index].result
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp)
                .zIndex(5f),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LinearProgressBar(((indexForRandom.toFloat() + 1) / (quizQuestions.size).toFloat()))
            Text(text = "${quizQuestions.sumOf { it.result.score }}/${quizQuestions.sumOf { it.result.outOf }}",
                fontSize = 40.sp
            )
        }



        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // *********************** QUIZ STUFF HERE!!!!!!!!
            // Switch to different Questions based on current index
            // Will need to change this out to be able to use text files

            var indexUser = 0

            while (quizQuestions[questionIndex].text != extractTextBetweenBrackets(userMadeQuiz[indexUser])) {
                indexUser++
            }

            val text: String = extractTextFromInput(userMadeQuiz[indexUser + 1]).toString()
            var image: Painter =
                painterResource(extractPainterResourceFromInput(userMadeQuiz[indexUser + 2]))
            var background: Painter =
                painterResource(extractPainterResourceFromInput(userMadeQuiz[indexUser + 3]))
            var textQuestion: Boolean = extractBooleanFromInput(userMadeQuiz[indexUser + 4])
            var imageQuestion: Boolean = extractBooleanFromInput(userMadeQuiz[indexUser + 5])
            var singleOn: Boolean = extractBooleanFromInput(userMadeQuiz[indexUser + 6])
            var onCorrect: Boolean = extractBooleanFromInput(userMadeQuiz[indexUser + 7])
            var surveyOn: Boolean = extractBooleanFromInput(userMadeQuiz[indexUser + 8])
            var columnLength: Int = extractIntFromInput(userMadeQuiz[indexUser + 9])
            var scoreType: String =
                extractScoreTypeFromInput(userMadeQuiz[indexUser + 10]).toString()
            val texts: List<question> = extractQuestionsFromInput(userMadeQuiz, indexUser + 11)

//        Log.i("scoreType", columnLength.toString())
//        Log.i("scoreType", scoreType.toString())
//        Text("${quizQuestions[questionIndex]}")
//        Text("${userMadeQuiz[indexUser + 1]}")

//        var changeIndexfromRandom = if (random) indexForRandom else questionIndex
            var changeIndexfromRandom = indexForRandom

            if (indexForRandom % 2 == 0) {
                MultipleChoiceQuestion(
                    text = "Q${changeIndexfromRandom + 1}: " + text,
                    image = image,
                    background = background,
                    textQuestion = textQuestion,
                    imageQuestion = imageQuestion,
                    // background = painterResource(R.drawable.mars),
                    onSubmitted = {
                        quizQuestions[questionIndex].isSubmitted
                    },
                    texts = texts,
                    columnLength = columnLength,
                    scoreType = scoreType,
                    singleOn = singleOn,
                    onCorrectOn = onCorrect,
                    surveyOn = surveyOn,
                    onResult = { result ->
                        updateQuestionResult(questionIndex, result)
                    },
                    getResult = getQuestionResult(questionIndex),
                    context = context
                )
            } else {
                MultipleChoiceQuestion(
                    text = "Q${changeIndexfromRandom + 1}: " + text,
                    textQuestion = textQuestion,
                    imageQuestion = imageQuestion,
                    // background = painterResource(R.drawable.mars),
                    onSubmitted = {
                        quizQuestions[questionIndex].isSubmitted
                    },
                    texts = texts,
                    columnLength = columnLength,
                    scoreType = scoreType,
                    singleOn = singleOn,
                    onCorrectOn = onCorrect,
                    surveyOn = surveyOn,
                    onResult = { result ->
                        updateQuestionResult(questionIndex, result)
                    },
                    getResult = getQuestionResult(questionIndex),
                    context = context
                )
            }

// Used for testing
            // Display current question and its submission status
//        Text(
//            text = "${quizQuestions.map { it.isSubmitted }}", style = TextStyle(fontSize = 24.sp)
//        )
//        Text(
//            text = "This is question ${questionIndex + 1}", style = TextStyle(fontSize = 24.sp)
//        )
        }
    }

//***************************************** MULTIPLE CHOICE

    @Composable
    fun MultipleChoiceQuestion(
        text: String = stringResource(R.string.default_message),
        image: Painter = painterResource(id = R.drawable.image4),
        background: Painter = painterResource(id = R.drawable.image3),
        textQuestion: Boolean = true,
        imageQuestion: Boolean = false,
        onSubmitted: () -> Boolean,
        texts: List<question> = listOf(question(stringResource(R.string.default_message), false)),
        singleOn: Boolean = true,
        onCorrectOn: Boolean = false,
        surveyOn: Boolean = false,
        columnLength: Int = 4,
        scoreType: String = "all",
        onResult: (ResultForOption) -> Unit, // Callback to send result back
        getResult: ResultForOption, // Function to get data from parent
        context: Context
    ) {
        var backgroundColor by remember { mutableStateOf(Color.White) }

        Box(
            modifier = Modifier
                .background(Color.Gray)
                .fillMaxSize()
                .zIndex(1f), // Higher ZIndex value places it on top
            contentAlignment = Alignment.Center // Center contents within the Box
        ) {
            Image(
                painter = background,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize() // Ensure it fills the screen
                    .align(Alignment.Center), // Center alignment
                contentScale = ContentScale.Crop // Scale image to cover the Box
            )

            Column(
                modifier = Modifier
                    .background(backgroundColor) // Set your desired background color here
                    .border(width = 2.dp, color = Color.Black) // Add black border
                    .padding(
                        end = 15.dp, start = 15.dp
                    ), // Optional: Add padding around the content
                horizontalAlignment = Alignment.CenterHorizontally, // Center Column contents horizontally
                verticalArrangement = Arrangement.Center // Center Column contents vertically
            ) {
                if (imageQuestion) Image(painter = image, contentDescription = null)
                if (textQuestion) Text(
                    text = text, style = TextStyle(fontSize = 24.sp), // Adjust the size as needed
                    modifier = Modifier.padding(top = 24.dp)
                )
                MultipleChoiceOption(
                    texts = texts,
                    columnLength = columnLength,
                    onSubmitted = onSubmitted,
                    scoreType = scoreType,
                    singleOn = singleOn,
                    onCorrectOn = onCorrectOn,
                    surveyOn = surveyOn,
                    onConditionChange = { newColor ->
                        backgroundColor = newColor
                    }, // This will allow transferring information...
                    // from the child to the parent
                    onResult = { resultForOption ->
                        // Pass result further up or handle it as needed
                        onResult(resultForOption)
                    },
                    getResult = getResult, // Function to get data from parent
                    context = context
                )
                // State to track if answer has been submitted
            }
        }
    }

    @Composable
    fun MultipleChoiceOption(
        texts: List<question> = listOf(question(stringResource(R.string.default_message), false)),
        singleOn: Boolean = true,
        onCorrectOn: Boolean = false,
        surveyOn: Boolean = false,
        columnLength: Int = 4,
        onSubmitted: () -> Boolean,
        scoreType: String = "all",
        onConditionChange: (Color) -> Unit,
        onResult: (ResultForOption) -> Unit, // Callback to send result back
        getResult: ResultForOption, // Function to get data from parent
        context: Context,
    ) {

        val random by remember { mutableStateOf(true) } // this will allow randomising of
        // questions

        // Initialize AudioPlayer instances with remember and mutableStateOf
        val audioPlayer by remember {
            mutableStateOf(AudioPlayer(context, R.raw.buttonsound1))
        }
        val correct by remember {
            mutableStateOf(AudioPlayer(context, R.raw.correct))
        }
        correct.setVolume(0.5f)
        val ok by remember {
            mutableStateOf(AudioPlayer(context, R.raw.ok))
        }
        ok.setVolume(0.2f)
        val incorrect by remember {
            mutableStateOf(AudioPlayer(context, R.raw.incorrect))
        }
        incorrect.setVolume(0.5f)

        // Define lighter versions of green and red
        val lighterGreen = Color(0xFF66BB6A) // Light green color
        val brighterGreen = Color(0xFF9CCC65) // Brighter green color
        val lighterRed = Color(0xFFEF9A9A) // Light red color

        DisposableEffect(Unit) { // releases resources button is not in use
            onDispose {
                audioPlayer.release() // Release resources when composable is disposed
            }
        }

        // Initialize the mutable state for the list
        val answers = texts.size
        var result = remember { getResult }

        if (!result.initialized) {
            result.initialized = true
            Log.d("Before", "${result.isToggleList}")
            result.isToggleList = List(texts.size) { false } // Adjust based on your requirements
            Log.d("After", "${result.isToggleList}")
        }
        Log.d("check", "${result.isToggleList}")
        var isToggledList by remember { mutableStateOf(result.isToggleList) }
        var score = result.score
        var total = result.outOf

//    // I m testing things here
//    var test = ResultForOption()
//
//    Log.d("test", "${test.score}")

        // Function to toggle state
        fun toggleState(
            index: Int,
            singleOn: Boolean,
            onCorrectOn: Boolean,
            correct: Int,
            onSubmitted: () -> Boolean
        ) {
            if (!onSubmitted()) {
                // Update the list based on the condition
                isToggledList = if (singleOn) {
                    // Set all elements to false
                    List(isToggledList.size) { false }.toMutableList().apply {
                        // Toggle the specific index
                        if (index in indices) {
                            this[index] = !this[index]
                        }
                    }
                } else if (onCorrectOn) { // This will allow the number of answers equal to correct
                    // Check if the number of true values in the list is less than or equal to 'correct'
                    if (isToggledList.count { it } < correct) {
                        // Toggle the specific index
                        isToggledList.mapIndexed { i, toggled ->
                            if (i == index) !toggled else toggled
                        }.toMutableList()
                    } else {
                        // Toggle the specific index
                        isToggledList.mapIndexed { i, toggled ->
                            if (i == index) {
                                // Toggle the value: make it false if it is true, otherwise keep it false
                                if (toggled) false else toggled
                            } else {
                                toggled
                            }
                        }.toMutableList()
                    }
                } else {
                    // Toggle the specific index if neither singleOn nor onCorrectOn is true
                    isToggledList.mapIndexed { i, toggled ->
                        if (i == index) !toggled else toggled
                    }.toMutableList()
                }


            }
        }

//    Text("$isToggledList")
//    // Text(text = "${isToggledList}")
//    Text(text = "${texts.map { it.isActive }}") // Use this to print out the isActive only
        var displayText by remember { mutableStateOf("") }


        if (onSubmitted() && score == 0) { // this section does the score system
//        Log.i("test", scoreType.toString())
            if (scoreType == "all" && !singleOn && !onCorrectOn) { // will all everything to be scored
                // will only work if all on and single answer turned on
//            Log.i("test", "hellooooooooooooooooooooooooooooooo")
                repeat(texts.size) { i -> //will repeat the number of times the number fo questions
                    if (texts[i].isActive == isToggledList[i]) score++
                }
                total = texts.size // Get total number of answers
                if (!surveyOn) Text(text = "$score/${texts.size}")
            } else {
                repeat(texts.size) { i -> // will repeat the number of times the number of questions
                    if (texts[i].isActive) {
                        if (texts[i].isActive == isToggledList[i]) score++
                        total = texts.count { it.isActive }
                    }
                }
                // if survey mode not on, do not show score on submit
                if (!surveyOn) Text(text = "$score/${texts.count { it.isActive }}")
            }
        } else if (onSubmitted()) { // just display results    // LaunchedEffect to run logic when composable is first launched or parameters change
            LaunchedEffect(scoreType, singleOn, onCorrectOn, surveyOn, score, texts) {
                displayText = when {
                    scoreType == "all" && !singleOn && !onCorrectOn -> {
                        if (!surveyOn) "$score/${texts.size}"
                        else ""
                    }

                    else -> {
                        val activeCount = texts.count { it.isActive }
                        if (!surveyOn) "$score/$activeCount"
                        else ""
                    }
                }
            }

            // Display the text based on the computed result
            Text(text = displayText)
        }

        val viewModel: MainViewModel = hiltViewModel()

        // Update the viewmodel with the emotion and after delay change it to null
        LaunchedEffect (GlobalData.emotionResult) {
            viewModel.updateEmotion(GlobalData.emotionResult)
            delay(5000)
            viewModel.updateEmotion(null)
        }

        Log.d("Check_Score", "Out of: ${texts.count { it.isActive }}")
        Log.d("Check_Score", "Score: ${score}")
        if (onSubmitted() && !surveyOn) { // play sound based on how good the score was
            if (scoreType == "all") {
                if (score >= texts.size) {
                    if (!viewModel.isMissuesState()) {
                        correct.play()
                        viewModel.resultSpeech(0)
                    }

                    onConditionChange(brighterGreen)
                } else if (score == 0) {
                    if (!viewModel.isMissuesState()) {
                        incorrect.play()
                        viewModel.resultSpeech(2)
                    }
              onConditionChange(Color.Red)
                } else {
                    if (!viewModel.isMissuesState()) {
                        ok.play()
                        viewModel.resultSpeech(1)
                    }
                    onConditionChange(lighterGreen)
                }
            } else {

                if (score >= texts.count { it.isActive }) {
                    if (!viewModel.isMissuesState()) {
                        correct.play()
                        viewModel.resultSpeech(0)
                    }
                    onConditionChange(brighterGreen)
                } else if (score == 0) {
                    if (!viewModel.isMissuesState()) {
                        incorrect.play()
                        viewModel.resultSpeech(2)
                    }
                    onConditionChange(Color.Red)
                } else {
                    if (!viewModel.isMissuesState()) {
                        ok.play()
                        viewModel.resultSpeech(1)
                    }
                    onConditionChange(lighterGreen)
                }
            }
            // Pass the result to the parent via the callback
            onResult(ResultForOption(score, total, isToggledList, result.initialized))

        } else { // This should ensure that input a remembered
            // Pass the result to the parent via the callback
            onResult(ResultForOption(result.score, result.outOf, isToggledList, result.initialized))
        }

        // Adding system to randomise the questions

        fun generateRandomSequence(start: Int, end: Int): List<Int> {
            require(start <= end) { "Start must be less than or equal to end" }

            // Create a list of numbers from start to end
            val numbers = (start..end).toList()

            // Determine if we should shuffle or not based on the `random` flag
            val shuffledNumbers = if (random) {
                numbers.shuffled(Random(System.currentTimeMillis()))
            } else {
                numbers
            }

            // Append end + 1 to the shuffled list
            val result = shuffledNumbers + (end + 1)

            return result
        }

        var randomQuestionList by remember {
            mutableStateOf(
                generateRandomSequence(
                    0, answers - 1
                )
            )
        }

        Row {
            repeat(10) { i ->
                if (answers > columnLength * i) { // May be an issue
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Create buttons for each state
                        for (indexPending in (0 + columnLength * i)..((columnLength - 1) + columnLength * i).coerceAtMost(
                            isToggledList.size - 1
                        )) {
                            var index by remember {
                                mutableStateOf(
                                    if (random && !surveyOn) {
                                        // Check if randomQuestionList is not empty before accessing elements
                                        randomQuestionList.getOrNull(indexPending) ?: 0
                                    } else {
                                        indexPending
                                    }
                                )
                            }

                            Row(
                                modifier = Modifier
                                    //.fillMaxHeight() // Ensure the Row takes up the full height of its parent
                                    .padding(16.dp), // Optional padding around the Row
                                horizontalArrangement = Arrangement.Center, // Center horizontally
                                verticalAlignment = Alignment.CenterVertically // Center vertically
                            ) {
                                // use the below for checking values
//                            Text(text = "${isToggledList[index]}")
//                            Text(text = "${texts[index].isActive}")
////                            Text(text = "${isToggledList[index] == (texts[index].isActive)}")
//                            Text(text = "${(texts[index].isActive) && onSubmitted()}")
//                            Text(text = "${isToggledList.getOrNull(index)}")


//                            val backgroundColor = // for simple condition
//                                if (isToggledList[index] && !onSubmitted()) Color.Blue else Color.Gray;
                                val isItemToggled = isToggledList.getOrNull(index)
                                val isItemActive = texts[index].isActive
                                val hasBeenSubmitted = onSubmitted()
                                val isSpecialCondition = onCorrectOn || singleOn

                                val backgroundColor: Color = when {
                                    // Blue: onSubmitted() is false and isToggledList[index] is true
                                    !hasBeenSubmitted && isItemToggled == true -> Color.Blue

                                    // Gray: Either onCorrectOn or singleOn is true, and both isToggledList[index] and texts[index].isActive are false
                                    isSpecialCondition && isItemToggled == false && isItemActive == false -> Color.Gray

                                    // Gray: Either onCorrectOn or singleOn is true, and both isToggledList[index] is true and texts[index].isActive is false
                                    isSpecialCondition && isItemToggled == true && isItemActive == false -> Color.Gray

                                    // Green: isToggledList[index] matches texts[index].isActive and onSubmitted() is true
                                    isItemToggled == isItemActive && hasBeenSubmitted -> Color.Green

                                    // Red: onSubmitted() is true but isToggledList[index] does not match texts[index].isActive
                                    hasBeenSubmitted && isItemToggled != isItemActive -> Color.Red


                                    // Default case: if none of the above conditions are met
                                    else -> Color.Gray
                                }

                                val contentColor: Color = when {
                                    isToggledList.getOrNull(index) == true && !onSubmitted() -> Color.White

                                    else -> Color.Black
                                }

                                Button(
                                    onClick = {
                                        toggleState(
                                            index,
                                            singleOn,
                                            onCorrectOn,
                                            correct = texts.count { it.isActive },
                                            onSubmitted
                                        );
                                        if (!onSubmitted() && !viewModel.isMissuesState()) audioPlayer.play()
                                    },
                                    modifier = Modifier.padding(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = backgroundColor,
                                        contentColor = contentColor
                                    )
                                ) {
                                    Text(
                                        text = if (isToggledList[index]) stringResource(R.string.yes) else stringResource(
                                            R.string.no
                                        ), style = TextStyle(fontSize = 16.sp)
                                    )
                                }

// Compute the background color based on conditions
                                val backgroundColorBox = when {
                                    !onSubmitted() || surveyOn -> Color.LightGray
                                    texts[index].isActive -> lighterGreen
                                    else -> lighterRed
                                }

// Compute the border color based on conditions
                                val borderColor = when {
                                    !onSubmitted() || surveyOn -> Color.Black // Assuming a border color when not submitted
                                    texts[index].isActive -> Color.Green
                                    else -> Color.Red
                                }

// Apply the computed colors to the Box modifier
                                Box(modifier = Modifier
                                    .clip(RoundedCornerShape(2.dp)) // Rounds the corners of the Box
                                    .background(backgroundColorBox) // Apply the computed background color
                                    .border(
                                        width = 2.dp,
                                        color = borderColor,
                                        shape = RoundedCornerShape(5.dp) // Border shape with rounded corners
                                    ) // Adds a border
                                    .clickable {
                                        toggleState(
                                            index,
                                            singleOn,
                                            onCorrectOn,
                                            correct = texts.count { it.isActive },
                                            onSubmitted
                                        );
                                        if (!onSubmitted() && !viewModel.isMissuesState()) audioPlayer.play()
                                    }) {
                                    Text(
                                        text = texts[index].text,
                                        style = TextStyle(fontSize = 24.sp), // Adjust the size as needed
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        // if survey mode is on send zero as score else send the score
    }

    //***************************************** TEXT CREATOR
    @Composable
// This function is used for all creation of text within the application
// The idea is to design it with all the functions I want to I can edit all be text
// In the input of the function, all input are defined with a default and any new ones should as...
// well
// This will allow expanding upon the function easier as all other pre-existing references to the...
// function will not need to be edited.
    fun TextHeading(
        text: String = stringResource(R.string.default_message),
        size: Int = 10,
        weight: String = "normal",
        italicTrue: Boolean = false,
        endTrue: Boolean = false,
        color: String = "black",
        modifier: Modifier
    ) {
        // the modifier input variable is used to that in the input, the user can put the...
        // modifier that they want
        // A modifier is used to tell a composable how it should be formated and interact with the...
        // parent function.
        Text(
            text = text, color = getColor(color), // This sets the color of the text
            style = TextStyle( // <-This will change all the styling of the text
                fontSize = size.sp,
                fontWeight = getFontWeight(weight), // Made a function with a switch case...
                // That will allow me to select what fontWeight I be chosen based on an input
                fontStyle = if (italicTrue) FontStyle.Italic else FontStyle.Normal,
                // The above will allow be to make tex Italic
                textAlign = if (endTrue) TextAlign.End else TextAlign.Center
                // The above will change if text is centered, left or right
            ), modifier = modifier // will use the input to function to apply modifiers
        )
    }

    fun getColor(weight: String): Color {
        return when (weight.lowercase()) {// This is Kotlin's version of switch cases
            "magenta" -> Color.Magenta // All of these will change the font text
            "transparent" -> Color.Transparent
            "yellow" -> Color.Yellow
            "blue" -> Color.Blue
            "red" -> Color.Red
            "white" -> Color.White
            "black" -> Color.Black
            else -> Color.Black // Default value
        }
    }

    fun getFontWeight(weight: String): FontWeight {
        return when (weight.lowercase()) {// This is Kotlin's version of switch cases
            "normal" -> FontWeight.Normal // All of these will change the font text
            "bold" -> FontWeight.Bold
            "light" -> FontWeight.Light
            "extra_light" -> FontWeight.ExtraLight
            "semi_bold" -> FontWeight.SemiBold
            "extra_bold" -> FontWeight.ExtraBold
            "black" -> FontWeight.Black
            else -> FontWeight.Normal // Default value
        }
    }


    //***************************************** BUTTONS & STUFF
    @Composable
    fun LinearProgressBar(progress: Float) {
        LinearProgressIndicator( // This is a function that is provided in Android Studio
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp) // Adjust height as needed
        )
    }

    @Composable
    fun CircularProgressBar() {
        CircularProgressIndicator( // Used to create a spinning Icon
            modifier = Modifier.size(48.dp) // Adjust size as needed
        )
    }

    @Composable
    fun DropdownMenuSample(audioPlayer: AudioPlayer) {
        /*
        This function used used for being able to switch two different languages in the code.
        Please note that this function has not been set up to be reused for other applications.
        */
        val context = LocalContext.current
        var expanded by remember { mutableStateOf(false) } // Tell system if drop down should...
        // be open.
        var selectedOption by remember { mutableStateOf(R.string.language) }/*
         This sets the default language to be english. Can switch language to be set to any other...
         available language.
         */


        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(100.dp)
                .zIndex(5f) // This sets the lay the box should be on
        ) {
            Column(
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                Button(onClick = { expanded = !expanded }) {
                    /*
                    If button to change language is pressed, it will turn the expanded variable to false.
                     */
                    Text(stringResource(selectedOption), fontSize = 50.sp)
                }

                if (expanded) {
                    Box(
                        modifier = Modifier
                            .offset(x = 15.dp)
                            .zIndex(6f)
                    ) {
                        DropdownMenu(expanded = expanded, // Used to expand window
                            onDismissRequest = { expanded = false } // does what it says
                        ) {
                            DropdownMenuItem(text = {
                                Text(
                                    stringResource(R.string.english),
                                    fontSize = 50.sp
                                )
                            },
                                onClick = {
                                    selectedOption = R.string.english
                                    expanded = false

                                    //  This will stop the audio player music and release resources.
                                    audioPlayer.stop()
                                    audioPlayer.release()

                                    // Update resource for English
                                    ResourceManager.setResource(R.raw.quizen)

                                    // Change locale to English
                                    LocaleManager.setLocale(context, "en")

                                    // Restart activity to apply changes
                                    (context as? Activity)?.recreate()
                                })

                            DropdownMenuItem(text = {
                                Text(
                                    stringResource(R.string.japanese), fontSize = 50.sp
                                )
                            }, onClick = {
                                selectedOption = R.string.japanese
                                expanded = false

                                //  This will stop the audio player music and release resources.
                                audioPlayer.stop()
                                audioPlayer.release()

                                // Update resource for Japanese
                                ResourceManager.setResource(R.raw.quizjp)

                                // Change locale to Japanese
                                LocaleManager.setLocale(context, "ja")

                                // Restart activity to apply changes
                                (context as? Activity)?.recreate()
                            })


                            DropdownMenuItem(text = {
                                Text(
                                    stringResource(R.string.german_bavarian), fontSize = 25.sp
                                )
                            }, onClick = {
                                selectedOption = R.string.german_bavarian
                                expanded = false

                                //  This will stop the audio player music and release resources.
                                audioPlayer.stop()
                                audioPlayer.release()

                                // Update resource for Japanese
                                ResourceManager.setResource(R.raw.quizba)

                                // Change locale to Japanese
                                LocaleManager.setLocale(context, "ba")

                                // Restart activity to apply changes
                                (context as? Activity)?.recreate()
                            })

                            DropdownMenuItem(text = {
                                Text(
                                    stringResource(R.string.test),
                                    fontSize = 25.sp
                                )
                            },
                                onClick = {
                                    selectedOption = R.string.test
                                    expanded = false

                                    //  This will stop the audio player music and release resources.
                                    audioPlayer.stop()
                                    audioPlayer.release()

                                    // Update resource for Japanese
                                    ResourceManager.setResource(R.raw.quizte)

                                    // Change locale to Japanese
                                    LocaleManager.setLocale(context, "te")

                                    // Restart activity to apply changes
                                    (context as? Activity)?.recreate()
                                })
                        }
                    }
                }
            }
        }
    }

    @Composable //same as icon but will shrink and grow image when pressed
    fun ButtonIconShrinkGrow(
        onClick: () -> Unit, image: Painter, modifier: Modifier = Modifier
    ) {
        var isPressed by remember { mutableStateOf(false) } // Keeps track if button pressed
        val scale by animateFloatAsState( // This changes scale on the effect isPressed
            targetValue = if (isPressed) 0.5f else 1f, // Change scale of growth
            animationSpec = tween(durationMillis = 150) // Adjust growth period
        )

        Box(modifier = modifier
            .size(100.dp) // Adjust size as needed
            .graphicsLayer(scaleX = scale, scaleY = scale) // Apply scaling
            .pointerInput(Unit) {
                detectTapGestures(onPress = { // Detects button being held
                    isPressed = true
                    try {
                        awaitRelease() // Wait until the press is released
                    } finally {
                        isPressed = false
                    }
                }, onTap = {
                    onClick() // Call the provided onClick lambda, triggers on button press
                })
            }) {
            Image(
                painter = image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize() //fills in the box
            )
        }
    }

    @Composable //box with button
    fun BoxWithClickable(
        onClick: () -> Unit,
        text: String,
        type: String? = null,
        grow: Boolean = false,
        animatedPress: Boolean = false,
        cycleColor: Boolean = false,
        modifier: Modifier = Modifier
    ) {
        // Color Cycling of box *****************************
        val colors = listOf(
            Color.Red, Color.Blue, Color.Magenta, Color.Green, Color.Yellow
        ) // create a list of color that can be cycled through
        val colorState =
            remember { Animatable(colors.first()) } // Set the color state default to red
        var currentIndex by remember { mutableStateOf(0) } // Keep track of pos in colors
        val scope = rememberCoroutineScope()
        // Cycle through colors
        LaunchedEffect(Unit) {
            while (true) {
                delay(800)
                val nextIndex = (currentIndex + 1) % colors.size
                scope.launch {
                    colorState.animateTo(
                        targetValue = colors[nextIndex],
                        animationSpec = tween(durationMillis = 1000) // Adjust fade duration as needed
                    )
                }
                currentIndex = nextIndex
            }
        }

        // Grow and Shrink on press *****************************
        var isPressed by remember { mutableStateOf(false) }
        val scalePress by animateFloatAsState(
            // This changes scale on the effect isPressed
            targetValue = if (isPressed) 0.5f else 1f, // Sets the scale
            animationSpec = tween(durationMillis = 150) // Set period of growth
        )

        // Growing Animation here *****************************
        var isGrowing by remember { mutableStateOf(grow) } // Used to set if grow animation occurs
        val scale by animateFloatAsState( // This does an animation over time
            targetValue = if (isGrowing) 10f else 1f, animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ) // Set period of growth and make it saw it will repeat indefinitely
            // Will also switch between growing and shrinking
        )

        var boxModifier = modifier.clickable(onClick = onClick) // Handle click events


        if (cycleColor) {
            boxModifier = boxModifier.background(colorState.value)
        }
        if (grow and animatedPress) {
            boxModifier = boxModifier
                .graphicsLayer(
                    scaleX = scale + scalePress, scaleY = scale + scalePress
                ) // Apply scale
                .pointerInput(Unit) {
                    detectTapGestures(onPress = {
                        isPressed = true
                        try {
                            awaitRelease() // Wait until the press is released
                        } finally {
                            isPressed = false
                        }
//                        audioPlayer.play()
//                        onClick()
                    }, onTap = {
                        onClick() // Call the provided onClick lambda
                    })
                }
        }
        if (grow) {
            boxModifier = boxModifier.graphicsLayer(scaleX = scale, scaleY = scale) // Apply scale
        } else if (animatedPress) {
            boxModifier = boxModifier
                .graphicsLayer(scaleX = scalePress, scaleY = scalePress) // Apply scale
                .pointerInput(Unit) {
                    detectTapGestures(onPress = {
                        isPressed = true
                        try {
                            awaitRelease() // Wait until the press is released
                        } finally {
                            isPressed = false
                        }
//                        audioPlayer.play()
//                        onClick()
                    }, onTap = {
                        onClick() // Call the provided onClick lambda
                    })
                }
        }
        Box(
            modifier = boxModifier
        ) {
            when (type) { // this is just a system to all the control of the Text style and layout
                "cool" -> { // If the type is set up as cool, then o this
                    TextHeading(
                        text = text,
                        size = 40,
                        italicTrue = true,
                        weight = "bold",
                        modifier = Modifier.padding(
                            10.dp
//                            start = 0.dp,
//                            top = 0.dp,
//                            end = 0.dp,
//                            bottom = 0.dp
                        )
                    )
                }

                else -> {
                    TextHeading( // Calls a separate function for text formatting
                        text = text,
                        size = 150,
                        italicTrue = false,
                        weight = "bold",
                        color = "black",
                        modifier = Modifier.padding(150.dp)
                    )
                }
            }
        }
    }