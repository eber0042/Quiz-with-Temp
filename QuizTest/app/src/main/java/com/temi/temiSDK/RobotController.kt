package com.temi.temiSDK

import android.content.pm.PackageManager
import android.health.connect.datatypes.units.Volume
import android.util.Log
import androidx.core.content.ContextCompat
import com.robotemi.sdk.ISdkService
import com.robotemi.sdk.Robot
import com.robotemi.sdk.listeners.OnRobotReadyListener
import com.robotemi.sdk.listeners.OnDetectionStateChangedListener
import com.robotemi.sdk.listeners.OnDetectionDataChangedListener
import com.robotemi.sdk.listeners.OnMovementStatusChangedListener
import com.robotemi.sdk.listeners.OnRobotLiftedListener
import com.robotemi.sdk.listeners.OnRobotDragStateChangedListener
import com.robotemi.sdk.TtsRequest
import com.robotemi.sdk.constants.HardButton
import com.robotemi.sdk.model.DetectionData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.lang.reflect.Array.set
import javax.inject.Singleton
import kotlin.random.Random

data class TtsStatus(val status: TtsRequest.Status)

enum class DetectionStateChangedStatus(val state: Int) { // Why is it like this?
    DETECTED(state = 2),
    LOST(state = 1),
    IDLE(state = 0);

    companion object {
        fun fromState(state: Int): DetectionStateChangedStatus? = entries.find { it.state == state }
    }
}

data class DetectionDataChangedStatus( val angle: Double, val distance: Double)

enum class MovementType {
    SKID_JOY,
    TURN_BY,
    NONE
}
enum class MovementStatus {
    START,
    GOING,
    OBSTACLE_DETECTED,
    NODE_INACTIVE,
    CALCULATING,
    COMPLETE,
    ABORT
}
data class MovementStatusChangedStatus(
    val type: MovementType,   // Use the MovementType enum
    val status: MovementStatus  // Use the MovementStatus enum
)

data class Dragged(
    val state: Boolean
)

data class Lifted(
    val state: Boolean
)

@Module
@InstallIn(SingletonComponent::class)
object RobotModule {
    @Provides
    @Singleton
    fun provideRobotController() = RobotController()
}

class RobotController():
    OnRobotReadyListener,
    OnDetectionStateChangedListener,
    Robot.TtsListener,
    OnDetectionDataChangedListener,
    OnMovementStatusChangedListener,
    OnRobotLiftedListener,
    OnRobotDragStateChangedListener
{
    private var language = TtsRequest.Language.EN_US

    private val robot = Robot.getInstance() //This is needed to reference the data coming from Temi

    // Setting up the Stateflows here
    private val _ttsStatus = MutableStateFlow( TtsStatus(status = TtsRequest.Status.PENDING) )
    val ttsStatus = _ttsStatus.asStateFlow()

    private val _detectionStateChangedStatus = MutableStateFlow(DetectionStateChangedStatus.IDLE)
    val detectionStateChangedStatus = _detectionStateChangedStatus.asStateFlow()

    private val _detectionDataChangedStatus = MutableStateFlow(DetectionDataChangedStatus(angle = 0.0, distance = 0.0))
    val detectionDataChangedStatus = _detectionDataChangedStatus.asStateFlow() // This can include talking state as well

    private val _movementStatusChangedStatus = MutableStateFlow(MovementStatusChangedStatus(MovementType.NONE, MovementStatus.NODE_INACTIVE))
    val movementStatusChangedStatus = _movementStatusChangedStatus.asStateFlow() // This can include talking state as well

    private val _dragged = MutableStateFlow(Dragged(false))
    val dragged = _dragged.asStateFlow() // This can include talking state as well

    private val _lifted = MutableStateFlow(Lifted(false))
    val lifted = _lifted.asStateFlow() // This can include talking state as well

    init {
        robot.addOnRobotReadyListener(this)
        robot.addTtsListener(this)
        robot.addOnDetectionStateChangedListener((this))
        robot.addOnDetectionDataChangedListener(this)
        robot.addOnMovementStatusChangedListener(this)
        robot.addOnRobotLiftedListener(this)
        robot.addOnRobotDragStateChangedListener(this)

//        robot.setTrackUserOn()
    }
    //********************************* General Functions
    suspend fun speak(speech: String, buffer: Long) {
        delay(buffer)
        val request = TtsRequest.create(
            speech = speech,
            isShowOnConversationLayer = false,
            showAnimationOnly = false,
            language = language
        ) // Need to create TtsRequest
        robot.speak(request)
        delay(buffer)
    }

    suspend fun turnBy(degree: Int, speed: Float = 1f, buffer: Long) {
        delay(buffer)
        robot.turnBy(degree, speed)
        delay(buffer)
    }

    suspend fun tiltAngle(degree: Int, speed: Float = 1f, buffer: Long) {
        delay(buffer)
        robot.tiltAngle(degree, speed)
        delay(buffer)
    }



    fun changeLanguage(language: TtsRequest.Language) {
        this.language = language
    }
    //********************************* General Data
    fun getPositionYaw(): Float
    {
        return robot.getPosition().yaw
    }

    fun volumeControl (volume: Int) {
        robot.volume = volume
    }

    //********************************* Override is below
    /**
     * Called when connection with robot was established.
     *
     * @param isReady `true` when connection is open. `false` otherwise.
     */
    override fun onRobotReady(isReady: Boolean) {

        if (!isReady) return
        robot.setDetectionModeOn(on = true, distance = 2.0f) // Set how far it can detect stuff
        robot.setKioskModeOn(on = false)
        robot.volume = 4// set volume to 4
//        robot.setHardButtonMode(HardButton.VOLUME, HardButton.Mode.DISABLED)
//        robot.setHardButtonMode(HardButton.MAIN, HardButton.Mode.DISABLED)
//        robot.setHardButtonMode(HardButton.POWER, HardButton.Mode.DISABLED)
//        robot.hideTopBar()

        robot.setDetectionModeOn(true,2f)
        Log.i ("Detection State", "${robot.detectionModeOn}")
        robot.setHardButtonMode(HardButton.VOLUME, HardButton.Mode.ENABLED)
        robot.setHardButtonMode(HardButton.MAIN, HardButton.Mode.ENABLED)
        robot.setHardButtonMode(HardButton.POWER, HardButton.Mode.ENABLED)
        robot.showTopBar()
    }

    override fun onTtsStatusChanged(ttsRequest: TtsRequest) {
//        Log.i("onTtsStatusChanged", "status: ${ttsRequest.status}")
        _ttsStatus.update {
            TtsStatus(status = ttsRequest.status)
        }
    }

    override fun onDetectionStateChanged(state: Int) {
        _detectionStateChangedStatus.update {
//            Log.d("DetectionState", "Detection state changed: ${DetectionStateChangedStatus.fromState(state)}")
            DetectionStateChangedStatus.fromState(state = state) ?: return@update it
        }
    }

    override fun onDetectionDataChanged(detectionData: DetectionData) {
        _detectionDataChangedStatus.update {
            DetectionDataChangedStatus(angle = detectionData.angle, distance = detectionData.distance)
        }
    }

    override fun onMovementStatusChanged(type: String, status: String) {
        _movementStatusChangedStatus.update { currentStatus ->
            // Convert the type and status to their respective enums
            val movementType = when (type) {
                "skidJoy" -> MovementType.SKID_JOY
                "turnBy" -> MovementType.TURN_BY
                else -> return@update currentStatus // If the type is unknown, return the current state
            }
            val movementStatus = when (status) {
                "start" -> MovementStatus.START
                "going" -> MovementStatus.GOING
                "obstacle detected" -> MovementStatus.OBSTACLE_DETECTED
                "node inactive" -> MovementStatus.NODE_INACTIVE
                "calculating" -> MovementStatus.CALCULATING
                "complete" -> MovementStatus.COMPLETE
                "abort" -> MovementStatus.ABORT
                else -> return@update currentStatus // If the status is unknown, return the current state
            }
            // Create a new MovementStatusChangedStatus from the enums
            MovementStatusChangedStatus(movementType, movementStatus)
        }
    }

    override fun onRobotLifted(isLifted: Boolean, reason: String) {
        _lifted.update {
            Lifted(isLifted)
        }
    }

    override fun onRobotDragStateChanged(isDragged: Boolean) {
        _dragged.update {
            Dragged(isDragged)
        }
    }
}
