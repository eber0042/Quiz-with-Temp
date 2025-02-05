package com.temi.temiSDK

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robotemi.sdk.TtsRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import kotlin.math.*

enum class State {
    TALK,          // Testing talking feature
    DISTANCE,      // Track distance of user
    ANGLE,
    CONSTRAINT_FOLLOW,
    TEST_MOVEMENT,
    DETECTION_LOGIC,
    TEST,
    NULL
}

enum class YDirection {
    FAR,
    MIDRANGE,
    CLOSE,
    MISSING
}

enum class XDirection {
    LEFT,
    RIGHT,
    MIDDLE,
    GONE

}

enum class YMovement {
    CLOSER,
    FURTHER,
    NOWHERE
}

enum class XMovement {
    LEFTER,
    RIGHTER,
    NOWHERE
}

enum class SpeechState {
    SCOREBOARD,
    UNANSWERED,
    EXIT_EARLY,
    THANKYOU,
    QUIZ
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val robotController: RobotController,
    @ApplicationContext private val context: Context // This will allow accessing the system files
) : ViewModel() {

    private val ttsStatus = robotController.ttsStatus // Current speech state
    private val detectionStatus = robotController.detectionStateChangedStatus
    private val detectionData = robotController.detectionDataChangedStatus
    private val movementStatus = robotController.movementStatusChangedStatus
    private val lifted = robotController.lifted
    private val dragged = robotController.dragged

    // These are important global variables
    private val buffer = 100L
    private var currentState = State.NULL // Current state of the system
    private var stateMode = State.NULL
    private val defaultAngle =
        180.0 // 180 + round(Math.toDegrees(robotController.getPositionYaw().toDouble())) // Default angle temi will go to.
    private val boundary = 90.0
    private var userRelativeDirection =
        XDirection.GONE // Used for checking direction user was lost

//    private var misuseState = arrayOf(false, false) // [0] is lifted and [1] is dragged

    // Used for handle user position
    private var previousUserAngle = 0.0
    private var currentUserAngle = 0.0
    private var xPosition = XDirection.GONE
    private var xMotion = XMovement.NOWHERE

    // Used for handle user position
    private var previousUserDistance = 0.0
    private var currentUserDistance = 0.0
    private var yPosition = YDirection.MISSING
    private var yMotion = YMovement.NOWHERE

    private var int: Int = 0

    // Used to allow the system to cause Temi to say certain lines under conditions
    private var speechState = SpeechState.QUIZ
    private var say = "Hello, World"

    private var emotion: String? = null

    // Move these outside the function to maintain state across calls
    private val numberArray = (1..5).toMutableList()
    private var currentIndex = 0
    private var previousLastChoice = -1

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

    private val _currentLanguage = MutableLiveData<String>()
    val currentLanguage: LiveData<String> get() = _currentLanguage

    // Method to change the language
    fun changeLanguageString(languageCode: String) {
        // Update the LiveData
        _currentLanguage.value = languageCode

        // Update the locale
        LocaleManager.setLocale(context, languageCode)
    }

    // This is the responses of the temi when someone answers a question
    suspend fun textModelChoice(state: Int, buffer: Long) {
        // Function to get the next random number in the shuffled array
        fun getRandomChoice(): Int {
            if (currentIndex >= numberArray.size) {
                numberArray.shuffle()  // Reshuffle when the array is exhausted
                currentIndex = 0

                // Ensure the first choice isn't the same as the last choice from the previous array
                if (numberArray[0] == previousLastChoice) {
                    // Find a random index to swap with the first element
                    val swapIndex = (1 until numberArray.size).random()  // Get a random index (1..4)
                    val temp = numberArray[0]
                    numberArray[0] = numberArray[swapIndex]
                    numberArray[swapIndex] = temp
                }
            }

            val choice = numberArray[currentIndex]  // Get the current choice
            currentIndex++  // Move to the next index
            previousLastChoice = choice  // Update the last choice to the current choice

            return choice
        }

        val choice = getRandomChoice()  // Get a randomized choice

        when (state) {
            0 -> { // All answers correct
                Log.d("Quiz", "Perfect")
                when (choice) {
                    1 -> robotController.speak(speech = context.getString(R.string.oh_you_got_it_right_you_want_a_medal_or_something), buffer)
                    2 -> robotController.speak(speech = context.getString(R.string.congratulations_you_must_be_so_proud_of_answering_a_quiz_question), buffer)
                    3 -> robotController.speak(speech = context.getString(R.string.wow_you_did_it_now_go_do_something_actually_challenging), buffer)
                    4 -> robotController.speak(speech = context.getString(R.string.you_got_it_right_big_deal_let_s_not_get_carried_away), buffer)
                    5 -> robotController.speak(speech = context.getString(R.string.perfect_score_huh_enjoy_your_moment_of_glory_it_s_not_lasting_long), buffer)
                }
            }

            1 -> { // Partially correct
                Log.d("Quiz", "Partial")
                when (choice) {
                    1 -> robotController.speak(speech = context.getString(R.string.almost_there_but_not_quite_story_of_your_life_huh), buffer)
                    2 -> robotController.speak(speech = context.getString(R.string.half_right_so_close_yet_so_far_keep_trying_maybe_you_ll_get_it_one_day), buffer)
                    3 -> robotController.speak(speech = context.getString(R.string.some_of_it_was_right_but_seriously_you_can_do_better_than_that), buffer)
                    4 -> robotController.speak(speech = context.getString(R.string.you_re_halfway_there_but_no_that_doesn_t_count_as_winning), buffer)
                    5 -> robotController.speak(speech = context.getString(R.string.partial_credit_i_mean_do_you_want_a_participation_trophy_or_what), buffer)
                }
            }

            2 -> { // All answers wrong
                Log.d("Quiz", "Incorrect")
                when (choice) {
                    1 -> robotController.speak(speech = context.getString(R.string.wow_how_did_you_manage_to_get_that_wrong), buffer)
                    2 -> robotController.speak(speech = context.getString(R.string.not_a_single_answer_right_impressive_in_all_the_wrong_ways), buffer)
                    3 -> robotController.speak(speech = context.getString(R.string.oh_you_really_went_for_zero_huh_bold_strategy_let_s_see_how_it_works_out), buffer)
                    4 -> robotController.speak(speech = context.getString(R.string.all_wrong_i_didn_t_even_think_that_was_possible_with_how_easy_these_questions_are_and_yet_here_we_are), buffer)
                    5 -> robotController.speak(speech = context.getString(R.string.you_do_realize_that_you_are_meant_to_select_the_correct_answers_right), buffer)
                }
            }
        }
    }

    init {

        /*
        viewModelScope.launch {
            while (true) { // This while loop is used to refresh the state to allow for refresh
                when (currentState) {
                    State.TALK -> { // Creating a method for getting multiple lines of dialogue to work
                        // This method will allow play once per detection
                        detectionStatus.collect { detectionStatus: DetectionStateChangedStatus ->
                            Log.d("DetectStatus", detectionStatus.toString())
                            Log.i("DetectData", detectionData.value.distance.toString())

                            if (detectionStatus == DetectionStateChangedStatus.DETECTED) {
                                robotController.speak(
                                    "Hi there, I am Temi. What can I do for you today?",
                                    buffer
                                )
                                conditionGate({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })

                                robotController.speak("Wow, that is really interesting", buffer)
                                conditionGate({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
                            }
                        }
                    }

                    State.DISTANCE -> { // Used to test the distance feature of Temi
                        // This method will allow play multiple per detection
                        var isDetected = false

                        // Launch a coroutine to monitor detectionStatus
                        val job = launch {
                            detectionStatus.collect { status ->
                                if (status == DetectionStateChangedStatus.DETECTED) {
                                    isDetected = true
                                    buffer()
                                } else {
                                    isDetected = false
                                }
                            }
                        }

                        Log.d("DetectStatus", detectionStatus.toString())
                        Log.i("DetectData", detectionData.value.distance.toString())

                        if (isDetected) {
                            robotController.speak(
                                "You are " + detectionData.value.distance.toString() + " meters away from me",
                                buffer
                            )
                            conditionGate({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
                        }
                        // Ensure to cancel the monitoring job if the loop finishes
                        job.cancel()
                    }

                    State.ANGLE -> {
                        // This method will allow play multiple per detection
                        var isDetected = false

                        // Launch a coroutine to monitor detectionStatus
                        val job = launch {
                            detectionStatus.collect { status ->
                                if (status == DetectionStateChangedStatus.DETECTED) {
                                    isDetected = true
                                    buffer()
                                } else {
                                    isDetected = false
                                }
                            }
                        }

//                        Log.d("DetectStatus", detectionStatus.toString())
//                        Log.i("DetectData", detectionData.value.distance.toString())

                        if (isDetected) {
                            robotController.speak(
                                "You are " + detectionData.value.angle.toString() + " degrees from my normal",
                                buffer
                            )
                            conditionGate({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
                        }
                        // Ensure to cancel the monitoring job if the loop finishes
                        conditionTimer({ !isDetected }, time = 50)
                        job.cancel()
                    }

                    State.CONSTRAINT_FOLLOW -> {
                        val currentAngle =
                            180 + round(Math.toDegrees(robotController.getPositionYaw().toDouble()))
                        val userRelativeAngle =
                            round(Math.toDegrees(detectionData.value.angle)) / 1.70
                        val turnAngle = (userRelativeAngle).toInt()

                        Log.i("currentAngle", currentAngle.toString())
                        Log.i("userRelativeAngle", userRelativeAngle.toString())
                        Log.i("new", turnAngle.toString())

                        // Use this to determine which direction the user was lost in
                        when {
                            userRelativeAngle > 0 -> {
                                userRelativeDirection = XDirection.LEFT
                            }

                            userRelativeAngle < 0 -> {
                                userRelativeDirection = XDirection.RIGHT
                            }

                            else -> {
                                // Do nothing
                            }
                        }

                        // This method will allow play multiple per detection
                        var isDetected = false
                        var isLost = false

                        // Launch a coroutine to monitor detectionStatus
                        val job = launch {
                            detectionStatus.collect { status ->
                                when (status) {
                                    DetectionStateChangedStatus.DETECTED -> {
                                        isDetected = true
                                        isLost = false
                                        buffer()
                                    }

                                    DetectionStateChangedStatus.LOST -> {
                                        isDetected = false
                                        isLost = true
                                        buffer()
                                    }

                                    else -> {
                                        isDetected = false
                                        isLost = false
                                        buffer()
                                    }
                                }
                            }
                        }

                        Log.i("Movement", movementStatus.value.status.toString())

                        if (isDetected && (turnAngle > 10 || turnAngle < -10) && (currentAngle < defaultAngle + 90 && currentAngle > defaultAngle - 90)) {
                            robotController.turnBy(turnAngle, 1f, buffer)
                            // The conditionGate makes this system more janky, not good to use in this case
//                            conditionGate ({ movementStatus.value.status !in listOf(MovementStatus.COMPLETE,MovementStatus.ABORT) })
                        } else if (isLost && (currentAngle < defaultAngle + 90 && currentAngle > defaultAngle - 90)) {
                            when (userRelativeDirection) {
                                XDirection.LEFT -> {
                                    robotController.turnBy(90, 0.1f, buffer)
                                    userRelativeDirection = XDirection.GONE
                                }

                                XDirection.RIGHT -> {
                                    robotController.turnBy(-90, 0.1f, buffer)
                                    userRelativeDirection = XDirection.GONE
                                }

                                else -> {
                                    // Do nothing
                                }
                            }
                        } else if (!isDetected && !isLost) {
                            if (defaultAngle != currentAngle) {
                                robotController.turnBy(
                                    getDirectedAngle(
                                        defaultAngle,
                                        currentAngle
                                    ).toInt(), 1f, buffer
                                )
                                conditionGate({
                                    movementStatus.value.status !in listOf(
                                        MovementStatus.COMPLETE,
                                        MovementStatus.ABORT
                                    )
                                }, movementStatus.value.status.toString())
                            }
                        }
                        // Ensure to cancel the monitoring job if the loop finishes
                        job.cancel()
                    }

                    State.TEST_MOVEMENT -> {
                        // This method will allow play multiple per detection
                        var isDetected = false

                        // Launch a coroutine to monitor detectionStatus
                        val job = launch {
                            detectionStatus.collect { status ->
                                if (status == DetectionStateChangedStatus.DETECTED) {
                                    isDetected = true
                                    buffer()
                                } else {
                                    isDetected = false
                                }
                            }
                        }

                        Log.i("Movement", movementStatus.value.status.toString())

                        if (isDetected) {
                        } else {
                            robotController.turnBy(50, 1f, buffer)
                            conditionGate({
                                movementStatus.value.status !in listOf(
                                    MovementStatus.COMPLETE,
                                    MovementStatus.ABORT
                                )
                            }, movementStatus.value.status.toString())
                            robotController.turnBy(-50, 1f, buffer)
                            conditionGate({
                                movementStatus.value.status !in listOf(
                                    MovementStatus.COMPLETE,
                                    MovementStatus.ABORT
                                )
                            }, movementStatus.value.status.toString())
                        }
                        // Ensure to cancel the monitoring job if the loop finishes
                        job.cancel()
                    }

                    State.DETECTION_LOGIC -> {

                        // This method will allow play multiple per detection
                        var isDetected = false

                        // Launch a coroutine to monitor detectionStatus
                        val job = launch {
                            detectionStatus.collect { status ->
                                if (status == DetectionStateChangedStatus.DETECTED) {
                                    isDetected = true
                                    buffer()
                                } else {
                                    isDetected = false
                                }
                            }
                        }

                        previousUserAngle = currentUserAngle
                        previousUserDistance = currentUserDistance
                        delay(500L)
                        currentUserAngle = detectionData.value.angle
                        currentUserDistance = detectionData.value.distance


//                        Log.i("currentUserAngle", (currentUserDistance).toString())
//                        Log.i("previousUserAngle", (previousUserDistance).toString())
//                        Log.i("Direction", (currentUserDistance - previousUserDistance).toString())

                        if (isDetected) { //&& previousUserDistance != 0.0 && previousUserDistance == currentUserDistance) {
                            // logic for close or far position
                            when {
                                // System for detecting how far
                            }
//                                currentUserDistance < 0.75 -> {
//                                    robotController.speak("You are close", buffer)
//                                    conditionGate ({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
//                                }
//                                currentUserDistance < 1.35 -> {
//                                    robotController.speak("You are midrange", buffer)
//                                    conditionGate ({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
//                                }
//                                else -> {
//                                    robotController.speak("You are far", buffer)
//                                    conditionGate ({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
//                                }
//                            }

                            Log.i("currentUserAngle", (currentUserDistance).toString())
                            Log.i("previousUserAngle", (previousUserDistance).toString())
                            Log.i(
                                "Direction",
                                (currentUserDistance - previousUserDistance).toString()
                            )
                            when {
                                currentUserDistance - previousUserDistance > 0.04 -> {
                                    Log.i("Type", "going away")
//                                    robotController.speak("You are going left to right", buffer)
//                                    conditionGate ({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
                                }

                                currentUserDistance - previousUserDistance < -0.04 -> {
                                    Log.i("Type", "going towards")
//                                    robotController.speak("You are going left to right", buffer)
//                                    conditionGate ({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
                                }

                                else -> {
                                    Log.i("Type", "no change")
//                                    robotController.speak("You did not move", buffer)
//                                    conditionGate ({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
                                }
                            }
                        } else {
                            currentUserDistance = 0.0
                        }


                        if (isDetected && previousUserAngle != 0.0 && previousUserAngle == currentUserAngle && false) {
                            // logic for left right position
//                            when {
//                                currentUserAngle > 0.1 -> {
//                                    robotController.speak("You are to my left", buffer)
//                                    conditionGate ({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
//                                }
//                                currentUserAngle < -0.1 -> {
//                                    robotController.speak("You are to my right", buffer)
//                                    conditionGate ({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
//                                }
//                                else -> {
//                                    robotController.speak("You are in front of me", buffer)
//                                    conditionGate ({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
//                                }
//                            }

                            Log.i("currentUserAngle", (currentUserAngle).toString())
                            Log.i("previousUserAngle", (previousUserAngle).toString())
                            Log.i("Direction", (currentUserAngle - previousUserAngle).toString())
                            when {
                                currentUserAngle - previousUserAngle > 0.125 -> {
                                    Log.i("Type", "left to right")
//                                    robotController.speak("You are going left to right", buffer)
//                                    conditionGate ({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
                                }

                                currentUserAngle - previousUserAngle < -0.125 -> {
                                    Log.i("Type", "right to left")
//                                    robotController.speak("You are going left to right", buffer)
//                                    conditionGate ({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
                                }

                                else -> {
                                    Log.i("Type", "no change")
//                                    robotController.speak("You did not move", buffer)
//                                    conditionGate ({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
                                }
                            }
                        } else {
                            currentUserAngle = 0.0
                        }

//                        conditionTimer({!isDetected}, time = 50)

                        // Ensure to cancel the monitoring job if the loop finishes
                        job.cancel()


                    }

                    State.TEST -> {
                        // This method will allow play multiple per detection
                        var isDetected = false

                        // Launch a coroutine to monitor detectionStatus
                        val job = launch {
                            detectionStatus.collect { status ->
                                if (status == DetectionStateChangedStatus.DETECTED) {
                                    isDetected = true
                                    buffer()
                                } else {
                                    isDetected = false
                                }
                            }
                        }

//                        Log.d("DetectStatus", detectionStatus.toString())
//                        Log.i("DetectData", detectionData.value.distance.toString())

                        if (isDetected && xPosition != XDirection.GONE) {
                            robotController.speak(
                                "$xMotion", //"You are $yPosition and getting $yMotion",
                                buffer
                            )
                            conditionGate({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
                        }
                        // Ensure to cancel the monitoring job if the loop finishes
                        conditionTimer(
                            { !(isDetected && xPosition != XDirection.GONE) },
                            time = 5
                        )
                        job.cancel()
                    }

                    State.NULL -> TODO()
                }
                buffer() // Add delay to ensure system work properly
            }
        }
*/
        viewModelScope.launch {
            while (true) {
                when (stateMode) {

                    State.TALK -> {
                        if (!lifted.value.state && !dragged.value.state) {
                            when (speechState) {
                                SpeechState.SCOREBOARD -> {
                                    robotController.speak(say, buffer)
                                    if (emotion == "Happy") {
                                        robotController.speak("I see that you are Happy. Does your name Happen to be on my list?", buffer)
                                    }
                                    stateMode = State.NULL
                                }
                                SpeechState.UNANSWERED -> {robotController.speak(say, buffer); stateMode = State.NULL}
                                SpeechState.EXIT_EARLY -> {robotController.speak(say, buffer); stateMode = State.NULL}
                                SpeechState.THANKYOU -> {robotController.speak(say, buffer); stateMode = State.NULL}
                                SpeechState.QUIZ -> {
                                    //                        conditionGate({ ttsStatus.value.status in listOf(TtsRequest.Status.COMPLETED, TtsRequest.Status.PENDING, TtsRequest.Status.STARTED)})
                                    textModelChoice(int, buffer)
//                        conditionGate({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
                                    stateMode = State.NULL
                                }
                            }
                        }
                    }

                    State.DISTANCE -> TODO()
                    State.ANGLE -> TODO()
                    State.CONSTRAINT_FOLLOW -> {
                        //' check to see if the state is not in misuse
                        if (!dragged.value.state && !lifted.value.state) {

                            val currentAngle =
                                180 + round(
                                    Math.toDegrees(
                                        robotController.getPositionYaw().toDouble()
                                    )
                                )
                            val userRelativeAngle =
                                round(Math.toDegrees(detectionData.value.angle)) / 1.70
                            val turnAngle = (userRelativeAngle).toInt()

//                        Log.i("currentAngle", currentAngle.toString())
//                        Log.i("userRelativeAngle", userRelativeAngle.toString())
//                        Log.i("new", turnAngle.toString())

                            // Use this to determine which direction the user was lost in
                            when {
                                userRelativeAngle > 0 -> {
                                    userRelativeDirection = XDirection.LEFT
                                }

                                userRelativeAngle < 0 -> {
                                    userRelativeDirection = XDirection.RIGHT
                                }

                                else -> {
                                    // Do nothing
                                }
                            }

                            // This method will allow play multiple per detection
                            var isDetected = false
                            var isLost = false

                            // Launch a coroutine to monitor detectionStatus
                            val job = launch {
                                detectionStatus.collect { status ->
                                    when (status) {
                                        DetectionStateChangedStatus.DETECTED -> {
                                            isDetected = true
                                            isLost = false
                                            buffer()
                                        }

                                        DetectionStateChangedStatus.LOST -> {
                                            isDetected = false
                                            isLost = true
                                            buffer()
                                        }

                                        else -> {
                                            isDetected = false
                                            isLost = false
                                            buffer()
                                        }
                                    }
                                }
                            }

//                        Log.i("Movement", movementStatus.value.status.toString())


                            fun normalizeAngle(angle: Double): Double {
                                var normalizedAngle =
                                    angle % 360  // Ensure the angle is within 0-360 range

                                if (normalizedAngle < 0) {
                                    normalizedAngle += 360  // Adjust for negative angles
                                }

                                return normalizedAngle
                            }

                            val lowerBound = normalizeAngle(defaultAngle - boundary)
                            val upperBound = normalizeAngle(defaultAngle + boundary)

                            // Helper function to calculate the adjusted turn angle that keeps within the bounds
                            fun clampTurnAngle(
                                currentAngle: Double,
                                targetTurnAngle: Double
                            ): Double {
                                val newAngle = normalizeAngle(currentAngle + targetTurnAngle)

                                return when {
                                    // If the new angle is within the bounds, return the target turn angle
                                    lowerBound < upperBound && newAngle in lowerBound..upperBound -> targetTurnAngle
                                    lowerBound > upperBound && (newAngle >= lowerBound || newAngle <= upperBound) -> targetTurnAngle

                                    // Otherwise, return the angle that brings it closest to the boundary
                                    lowerBound < upperBound -> {
                                        if (newAngle < lowerBound) lowerBound + 1 - currentAngle
                                        else upperBound - 1 - currentAngle
                                    }

                                    else -> {
                                        if (abs(upperBound - currentAngle) < abs(lowerBound - currentAngle)) {
                                            upperBound - 1 - currentAngle
                                        } else {
                                            lowerBound + 1 - currentAngle
                                        }
                                    }
                                }
                            }

                            // Now clamp the turn angle before turning the robot
                            val adjustedTurnAngle =
                                clampTurnAngle(currentAngle, turnAngle.toDouble())

//                        Log.i("Angle", currentAngle.toString())
//                        Log.i("Normed",  normalizeAngle(defaultAngle + boundary).toString())
//                        Log.i("Normed",  normalizeAngle(defaultAngle - boundary).toString())

                            if (Math.abs(adjustedTurnAngle) > 0.1 && yPosition != YDirection.CLOSE) {  // Only turn if there's a meaningful adjustment to make
                                robotController.turnBy(adjustedTurnAngle.toInt(), 1f, buffer)
                            } else if (isLost && (currentAngle < defaultAngle + boundary && currentAngle > defaultAngle - boundary)) {
                                // Handles condition when the user is lost
                                when (userRelativeDirection) {
                                    XDirection.LEFT -> {
                                        robotController.turnBy(45, 0.1f, buffer)
                                        userRelativeDirection = XDirection.GONE
                                    }

                                    XDirection.RIGHT -> {
                                        robotController.turnBy(-45, 0.1f, buffer)
                                        userRelativeDirection = XDirection.GONE
                                    }

                                    else -> {
                                        // Do nothing
                                    }
                                }
                            } else if (!isDetected && !isLost) {
                                // Handles conditions were the robot has detected someone
                                val angleThreshold = 2.0 // Example threshold, adjust as needed

                                if (abs(defaultAngle - currentAngle) > angleThreshold) {
                                    robotController.turnBy(
                                        getDirectedAngle(
                                            defaultAngle,
                                            currentAngle
                                        ).toInt(), 1f, buffer
                                    )
                                    conditionGate({
                                        movementStatus.value.status !in listOf(
                                            MovementStatus.COMPLETE,
                                            MovementStatus.ABORT
                                        )
                                    }, movementStatus.value.status.toString())
                                }
                            }
                            // Ensure to cancel the monitoring job if the loop finishes
                            job.cancel()
                        }
                    }

                    State.TEST_MOVEMENT -> TODO()
                    State.DETECTION_LOGIC -> TODO()
                    State.TEST -> TODO()
                    State.NULL -> {}
                }
                buffer()
            }
        }

        // Control speech based on user
        viewModelScope.launch {
            while (true) {
                while (!lifted.value.state && !dragged.value.state) {
                    while (stateMode == State.CONSTRAINT_FOLLOW) {
                        var isDetected = false

                        // Launch a coroutine to monitor detectionStatus
                        val job = launch {
                            detectionStatus.collect { status ->
                                if (status == DetectionStateChangedStatus.DETECTED) {
                                    isDetected = true
                                    buffer()
                                } else {
                                    isDetected = false
                                }
                            }
                        }

                        //                    Log.i("Testing", yPosition.toString())
                        /*
                                            when (yPosition) {
                           YDirection.FAR -> {
                               robotController.speak(
                                   "Would you like to do a quiz, just come closer",
                                   buffer
                               )
                               conditionGate({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
                               conditionTimer({!isDetected}, time = 20)
                           }

                           YDirection.MIDRANGE -> {
                               robotController.speak("Would you like to do a quiz", buffer)
                               conditionGate({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
                               conditionTimer({!isDetected}, time = 20)
                           }

                           YDirection.CLOSE -> {
                               robotController.speak("Press the Start Button to start the quiz", buffer)
                               conditionGate({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
                               conditionTimer({!isDetected}, time = 20)
                           }

                           YDirection.MISSING -> {}
                       }
                         */

                        when (xPosition) {
                            XDirection.LEFT -> {
                                when (yPosition) {
                                    YDirection.FAR -> {
                                        if (stateMode == State.CONSTRAINT_FOLLOW) {
                                            robotController.speak(
                                                context.getString(R.string.you_re_quite_far_to_my_left_would_you_like_to_come_closer_for_a_quiz),
                                                buffer
                                            )
                                        }
                                        conditionGate({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
                                        conditionTimer({ !isDetected }, time = 20)
                                    }

                                    YDirection.MIDRANGE -> {
                                        if (stateMode == State.CONSTRAINT_FOLLOW) {
                                            robotController.speak(
                                                context.getString(R.string.you_re_in_the_midrange_on_my_left_would_you_like_to_do_a_quiz),
                                                buffer
                                            )
                                        }

                                        conditionGate({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
                                        conditionTimer({ !isDetected }, time = 20)
                                    }

                                    YDirection.CLOSE -> {
                                        if (stateMode == State.CONSTRAINT_FOLLOW) {
                                            robotController.speak(
                                                context.getString(R.string.you_re_close_on_my_left_press_the_start_button_to_start_the_quiz),
                                                buffer
                                            )
                                        }

                                        conditionGate({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
                                        conditionTimer({ !isDetected }, time = 20)
                                    }

                                    YDirection.MISSING -> {
                                        // No action needed for MISSING
                                    }
                                }

                                when (xMotion) {
                                    XMovement.LEFTER -> {
                                        if (stateMode == State.CONSTRAINT_FOLLOW) {
                                            robotController.speak(
                                                context.getString(R.string.you_re_moving_further_to_my_left),
                                                buffer
                                            )
                                        }

                                        conditionGate({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
                                        conditionTimer({ !isDetected }, time = 20)
                                    }

                                    XMovement.RIGHTER -> {
                                        if (stateMode == State.CONSTRAINT_FOLLOW) {
                                            robotController.speak(context.getString(R.string.you_re_moving_to_my_right), buffer)
                                        }

                                        conditionGate({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
                                        conditionTimer({ !isDetected }, time = 20)
                                    }

                                    XMovement.NOWHERE -> {
                                        if (stateMode == State.CONSTRAINT_FOLLOW) {
                                            robotController.speak(
                                                context.getString(R.string.you_re_staying_still_on_my_left),
                                                buffer
                                            )
                                        }

                                        conditionGate({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
                                        conditionTimer({ !isDetected }, time = 20)
                                    }
                                }
                            }

                            XDirection.RIGHT -> {
                                when (yPosition) {
                                    YDirection.FAR -> {
                                        if (stateMode == State.CONSTRAINT_FOLLOW) {
                                            robotController.speak(
                                                context.getString(R.string.you_re_quite_far_to_my_right_would_you_like_to_come_closer_for_a_quiz),
                                                buffer
                                            )
                                        }

                                        conditionGate({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
                                        conditionTimer({ !isDetected }, time = 20)
                                    }

                                    YDirection.MIDRANGE -> {
                                        if (stateMode == State.CONSTRAINT_FOLLOW) {
                                            robotController.speak(
                                                context.getString(R.string.you_re_in_the_midrange_on_my_right_would_you_like_to_do_a_quiz),
                                                buffer
                                            )
                                        }

                                        conditionGate({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
                                        conditionTimer({ !isDetected }, time = 20)
                                    }

                                    YDirection.CLOSE -> {
                                        if (stateMode == State.CONSTRAINT_FOLLOW) {
                                            robotController.speak(
                                                context.getString(R.string.you_re_close_on_my_right_press_the_start_button_to_start_the_quiz),
                                                buffer
                                            )
                                        }

                                        conditionGate({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
                                        conditionTimer({ !isDetected }, time = 20)
                                    }

                                    YDirection.MISSING -> {
                                        // No action needed for MISSING
                                    }
                                }

                                when (xMotion) {
                                    XMovement.LEFTER -> {
                                        if (stateMode == State.CONSTRAINT_FOLLOW) {
                                            robotController.speak(context.getString(R.string.you_re_moving_to_my_left), buffer)
                                        }

                                        conditionGate({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
                                        conditionTimer({ !isDetected }, time = 20)
                                    }

                                    XMovement.RIGHTER -> {
                                        if (stateMode == State.CONSTRAINT_FOLLOW) {
                                            robotController.speak(
                                                context.getString(R.string.you_re_moving_further_to_my_right),
                                                buffer
                                            )
                                        }

                                        conditionGate({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
                                        conditionTimer({ !isDetected }, time = 20)
                                    }

                                    XMovement.NOWHERE -> {
                                        if (stateMode == State.CONSTRAINT_FOLLOW) {
                                            robotController.speak(
                                                context.getString(R.string.you_re_staying_still_on_my_right),
                                                buffer
                                            )
                                        }

                                        conditionGate({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
                                        conditionTimer({ !isDetected }, time = 20)
                                    }
                                }
                            }

                            XDirection.MIDDLE -> {
                                when (yPosition) {
                                    YDirection.FAR -> {
                                        if (stateMode == State.CONSTRAINT_FOLLOW) {
                                            robotController.speak(
                                                context.getString(R.string.you_re_far_away_from_me_would_you_like_to_come_closer_for_a_quiz),
                                                buffer
                                            )
                                        }

                                        conditionGate({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
                                        conditionTimer({ !isDetected }, time = 20)
                                    }

                                    YDirection.MIDRANGE -> {
                                        if (stateMode == State.CONSTRAINT_FOLLOW) {
                                            robotController.speak(
                                                context.getString(R.string.you_re_in_the_midrange_relative_to_me_would_you_like_to_do_a_quiz),
                                                buffer
                                            )
                                        }

                                        conditionGate({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
                                        conditionTimer({ !isDetected }, time = 20)
                                    }

                                    YDirection.CLOSE -> {
                                        if (stateMode == State.CONSTRAINT_FOLLOW) {
                                            robotController.speak(
                                                context.getString(R.string.you_re_close_to_me_press_the_start_button_to_start_the_quiz),
                                                buffer
                                            )
                                        }

                                        conditionGate({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
                                        conditionTimer({ !isDetected }, time = 20)
                                    }

                                    YDirection.MISSING -> {
                                        // No action needed for MISSING
                                    }
                                }

                                when (xMotion) {
                                    XMovement.LEFTER -> {
                                        if (stateMode == State.CONSTRAINT_FOLLOW) {
                                            robotController.speak(context.getString(R.string.you_re_moving_to_my_left), buffer)
                                        }

                                        conditionGate({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
                                        conditionTimer({ !isDetected }, time = 20)
                                    }

                                    XMovement.RIGHTER -> {
                                        if (stateMode == State.CONSTRAINT_FOLLOW) {
                                            robotController.speak(context.getString(R.string.you_re_moving_to_my_right), buffer)
                                        }

                                        conditionGate({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
                                        conditionTimer({ !isDetected }, time = 20)
                                    }

                                    XMovement.NOWHERE -> {
                                        if (stateMode == State.CONSTRAINT_FOLLOW) {
                                            robotController.speak(
                                                context.getString(R.string.you_re_staying_still_in_the_middle),
                                                buffer
                                            )
                                        }

                                        conditionGate({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
                                        conditionTimer({ !isDetected }, time = 20)
                                    }
                                }
                            }

                            XDirection.GONE -> {
                                // No action needed for GONE
                            }
                        }

                        // Now handle the YDirection movement


                        buffer()
                        job.cancel()
                    }
                    buffer()
                }
                buffer()
            }
        }

        // Title head of Temi based on how far away the user is.
        viewModelScope.launch {
            var closeTrigger = false
            while (true) {
                while (!dragged.value.state && !lifted.value.state) {
                    if (stateMode == State.CONSTRAINT_FOLLOW) {
                        when (yPosition) {
                            YDirection.FAR -> {
                                robotController.tiltAngle(10, 1f, buffer)
                                closeTrigger = false
                            }

                            YDirection.MIDRANGE -> {
                                robotController.tiltAngle(25, 1f, buffer)
                                closeTrigger = false
                            }

                            YDirection.CLOSE -> {
                                if (!closeTrigger) {
                                    robotController.tiltAngle(30, 1f, buffer)
                                    closeTrigger = true
                                }
                            }

                            YDirection.MISSING -> {
                                robotController.tiltAngle(-5, 1f, buffer)
                                closeTrigger = false
                            }
                        }
                    } else {
                        if (!closeTrigger) {
                            robotController.tiltAngle(35, 1f, buffer)
                            closeTrigger = true
                        }
                    }
                    buffer()
                }
                buffer()
            }
        }

//        // If picked up or dragged
//        viewModelScope.launch {
//            while (true) {
////                Log.i("State Lifted", lifted.value.state.toString())
////                buffer()
////                Log.i("State Dragged", dragged.value.state.toString())
////                buffer()
//                if (lifted.value.state) {
//                    robotController.speak("Please put me down", buffer)
//                    conditionGate({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
//                } else if (dragged.value.state) {
//                    robotController.speak("Please do not drag me", buffer)
//                    conditionGate({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
//                }
//                buffer()
//            }
//        }

        // x-detection
        viewModelScope.launch { // Used to get state for x-direction and motion
            while (true) {
                // This method will allow play multiple per detection
                var isDetected = false

                // Launch a coroutine to monitor detectionStatus
                val job = launch {
                    detectionStatus.collect { status ->
                        if (status == DetectionStateChangedStatus.DETECTED) {
                            isDetected = true
                            buffer()
                        } else {
                            isDetected = false
                        }
                    }
                }

                previousUserAngle = currentUserAngle
                delay(500L)
                currentUserAngle = detectionData.value.angle

//                Log.i("currentUserAngle", (currentUserAngle).toString())
//                Log.i("previousUserAngle", (previousUserAngle).toString())
//                Log.i("Direction", (currentUserAngle - previousUserAngle).toString())

                if (isDetected && previousUserDistance != 0.0) { //&& previousUserDistance != 0.0 && previousUserDistance == currentUserDistance) {
                    // logic for close or far position
//                    Log.i("STATE", (yPosition).toString())
                    xPosition = when {
                        currentUserAngle > 0.1 -> {
                            XDirection.LEFT
                        }

                        currentUserAngle < -0.1 -> {
                            XDirection.RIGHT
                        }

                        else -> {
                            XDirection.MIDDLE
                        }
                    }
                } else {
                    xPosition = XDirection.GONE
                }

                if (isDetected && previousUserAngle != 0.0 && previousUserAngle != currentUserAngle) {

                    when (yPosition) {
                        YDirection.FAR -> {
                            xMotion = when {
                                currentUserAngle - previousUserAngle > 0.07 -> XMovement.LEFTER
                                currentUserAngle - previousUserAngle < -0.07 -> XMovement.RIGHTER
                                else -> XMovement.NOWHERE
                            }
                        }

                        YDirection.MIDRANGE -> {
                            xMotion = when {
                                currentUserAngle - previousUserAngle > 0.12 -> XMovement.LEFTER
                                currentUserAngle - previousUserAngle < -0.12 -> XMovement.RIGHTER
                                else -> XMovement.NOWHERE
                            }
                        }

                        YDirection.CLOSE -> {
                            xMotion = when {
                                currentUserAngle - previousUserAngle > 0.17 -> XMovement.LEFTER
                                currentUserAngle - previousUserAngle < -0.17 -> XMovement.RIGHTER
                                else -> XMovement.NOWHERE
                            }
                        }

                        YDirection.MISSING -> {
                            XMovement.NOWHERE
                        }
                    }
                }

//                Log.i("STATE", (xMotion).toString())

                job.cancel()
            }
        }

        // y-detection
        viewModelScope.launch { // Used to get state for y-direction and motion
            while (true) {
                // This method will allow play multiple per detection
                var isDetected = false

                // Launch a coroutine to monitor detectionStatus
                val job = launch {
                    detectionStatus.collect { status ->
                        if (status == DetectionStateChangedStatus.DETECTED) {
                            isDetected = true
                            buffer()
                        } else {
                            isDetected = false
                        }
                    }
                }

                previousUserDistance = currentUserDistance
                delay(500L)
                currentUserDistance = detectionData.value.distance

//                Log.i("currentUserAngle", (currentUserDistance).toString())
//                Log.i("previousUserAngle", (previousUserDistance).toString())
//                Log.i("Direction", (currentUserDistance - previousUserDistance).toString())

                if (isDetected && previousUserDistance != 0.0) { //&& previousUserDistance != 0.0 && previousUserDistance == currentUserDistance) {
                    // logic for close or far position
                    yPosition = when {
                        currentUserDistance < 0.90 -> {
                            YDirection.CLOSE
                        }

                        currentUserDistance < 1.35 -> {
                            YDirection.MIDRANGE
                        }

                        else -> {
                            YDirection.FAR
                        }
                    }
                } else {
                    yPosition = YDirection.MISSING
                }

                if (isDetected && previousUserDistance != 0.0 && previousUserDistance != currentUserDistance) { //&& previousUserDistance != 0.0 && previousUserDistance == currentUserDistance) {
                    yMotion = when {
                        currentUserDistance - previousUserDistance > 0.01 -> {
                            YMovement.FURTHER
                        }

                        currentUserDistance - previousUserDistance < -0.01 -> {
                            YMovement.CLOSER
                        }

                        else -> {
                            YMovement.NOWHERE
                        }
                    }
                }
//                Log.i("STATE", (yMotion).toString())

                job.cancel()
            }
        }
    }

    fun resultSpeech(int: Int = 0, state: SpeechState = SpeechState.QUIZ, say: String = "Hello, World") {
        this.int = int
        this.stateMode = State.TALK
        this.speechState = state
        this.say = say
    }

    fun speechUi(text: String) {
        viewModelScope.launch {
            speech(text)
        }
    }

    fun changeLanguage(language: TtsRequest.Language) {
        robotController.changeLanguage(language)
    }
    suspend fun speech(text: String) {
        robotController.speak(text, buffer)
        conditionGate({ ttsStatus.value.status != TtsRequest.Status.COMPLETED })
        conditionTimer({ !(dragged.value.state || lifted.value.state) }, time = 2)
    }

    fun idleMode(setIdleMode: Boolean) {
        if (setIdleMode) {
            this.stateMode = State.CONSTRAINT_FOLLOW
        } else {
            this.stateMode = State.NULL
        }
    }

    fun isMissuesState(): Boolean {
        // Log.i("State", (dragged.value.state || lifted.value.state).toString())
        return (dragged.value.state || lifted.value.state)
    }

    fun volumeControl(volume: Int) {
        robotController.volumeControl(volume)
    }

    fun updateEmotion(emotion: String?) {
        this.emotion = emotion
    }
    //**************************System Function
    private suspend fun buffer() {
        // Increase buffer time to ensure enough delay between checks
        delay(this.buffer)
    }

    private suspend fun conditionTimer(trigger: () -> Boolean, time: Int) {
        if (!trigger()) {
            for (i in 1..time) {
                buffer()
//            Log.i("Trigger", trigger().toString())
                if (trigger()) {
                    break
                }
            }
        }
    }

    private suspend fun conditionGate(trigger: () -> Boolean, log: String = "Null") {
        // Loop until the trigger condition returns false
        while (trigger()) {
//        Log.i("ConditionGate", "Trigger: $log")
            buffer() // Pause between checks to prevent busy-waiting
        }
//    Log.i("ConditionGate", "End")
    }

    private fun getDirectedAngle(a1: Double, a2: Double): Double {
        var difference = a1 - a2
        // Normalize the angle to keep it between -180 and 180 degrees
        if (difference > 180) difference -= 360
        if (difference < -180) difference += 360
        return difference
    }
//**************************Sequence Functions

}

//    private suspend fun conditionGate(trigger: () -> Boolean) {
//        // Infinite loop to keep checking until the trigger condition becomes false
//        var gate = true
//        while (gate) {
//            // Collect from ttsStatus and wait for a new value
//            ttsStatus.collect { ttsState ->
//                // Check the trigger condition
//                if (!trigger()) {
//                    // If the trigger condition is false, break the loop
//                    gate = false
//                }
//            }
//        }
//    }

//****************************************OLD USELESS CODE
//    enum class Trigger {
//        IDLE,          // Testing talking feature
//        DETECTION_STATE      // Track distance of user
//    }
//    private var trigger = Trigger.IDLE
//viewModelScope.launch { // Used to create forced reset
//    detectionStatus.collect { currentStatus: DetectionStateChangedStatus ->
//        if (currentStatus == DetectionStateChangedStatus.DETECTED) {
//            handleDetectionReset(
//                emitTrigger = { collectedTrigger ->
//                    // Handle the emitted trigger, e.g., pass it to another system
//                    trigger = collectedTrigger
//                },
//                condition = true, // Pass in the condition for the check
//                timer = 1000       // Optional timer parameter
//            )
//        }
//    }
//}
//private suspend fun handleDetectionReset(emitTrigger: (Trigger) -> Unit, condition: Boolean, timer: Int = 1) {
//    // Flag to check if detection state remains the same
//    var isStillDetected = true
//
//    // Launch a coroutine to monitor detectionStatus
//    val job = viewModelScope.launch {
//        detectionStatus.collect { status ->
//            if (status != DetectionStateChangedStatus.DETECTED) {
//                isStillDetected = false
//                cancel() // Cancel the monitoring coroutine if the status changes
//            }
//        }
//    }
//
//    while (condition) {
//        buffer() // Assuming buffer() is a suspend function
//
//        // Check the flag after the delay
//        if (!isStillDetected) {
//            break // Exit the loop if the status changed
//        }
//    }
//
//    for (i in 1..timer) {
//        buffer() // Assuming buffer() is a suspend function
//
//        // Check the flag after the delay
//        if (!isStillDetected) {
//            break // Exit the loop if the status changed
//        }
//    }
//
//    // Ensure to cancel the monitoring job if the loop finishes
//    job.cancel()
//
//    if (isStillDetected) {
//        emitTrigger(Trigger.DETECTION_STATE)
//        Log.i("ForcedRefresh", "DetectionStatus has been forced refreshed.")
//    } else {
//        Log.i("ForcedRefresh", "DetectionStatus changed before completion.")
//    }
//}