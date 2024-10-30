package com.temi.temiSDK

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageFormat
import  android.hardware.camera2.*
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.ImageReader
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Messenger
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfRect
import org.opencv.core.Point
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import org.tensorflow.lite.InterpreterApi
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.StandardOpenOption
import kotlin.math.abs
import org.opencv.videoio.VideoCapture
import android.os.Message
import android.os.RemoteException


class EmotionDetection : Service(), CameraBridgeViewBase.CvCameraViewListener2 {

    private lateinit var cameraDevice: CameraDevice
    private lateinit var imageReader: ImageReader

    // OpenCV params
    private lateinit var mOpenCvCameraView: CameraBridgeViewBase
    private lateinit var faceCascade: CascadeClassifier

    private lateinit var interpreter: InterpreterApi

    /**
     * Emotion dictionary to map indices to emotion labels
     */
    val emotionDict = mapOf(
        0 to "Angry",
        1 to "Disgusted",
        2 to "Fearful",
        3 to "Happy",
        4 to "Neutral",
        5 to "Sad",
        6 to "Surprised"
    )

    /**
     * List that stores previous generation of faces and corresponding classifier results.
     */
    var facesArrayMemory: MutableList<Array<Pair<Rect, FloatArray>>> =
        mutableListOf() // Change size to change how far system remembers

    /**
     * Number of previous iterations of faces stored in memory.
     */
    var memorySize = 5

    var currentframe = 5

    var boo = false

    private val TAG = "HELLO!"

    private val binder = EmotionDetectionBinder()

    inner class EmotionDetectionBinder : Binder() {
        fun getService(): EmotionDetection = this@EmotionDetection
    }

    init {
        Log.i(TAG, "Instantiated new " + this.javaClass)
        for (i in 0 until memorySize) {
            facesArrayMemory.add(arrayOf<Pair<Rect, FloatArray>>()) // Add an empty array of Rect
        }
        Log.i("SIZE", "Boo  ${facesArrayMemory.size}")
    }

    private lateinit var surfaceHolder: SurfaceHolder

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Service created")
        val cameraId = getFrontCameraIDY(this)
        // Initialize OpenCV
        if (OpenCVLoader.initDebug()) {
            Log.i(TAG, "OpenCV loaded successfully")
            // Set a callback for when the surface is created
            if (cameraId != null) {
                startCameraStream(cameraId)
            } else {
                Log.e(TAG, "Front camera not found.")
                stopSelf()
            }
        } else {
            Log.e(TAG, "OpenCV initialization failed!")
            Toast.makeText(this, "OpenCV initialization failed!", Toast.LENGTH_LONG).show()
            stopSelf() // Stop the service if OpenCV fails to load
        }
    }

    @SuppressLint("MissingPermission")
    private fun startCameraStream(cameraId: String) {
        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        try {
            cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    cameraDevice = camera
                    setUpImageReader()
                    createCameraCaptureSession()
                }

                override fun onDisconnected(camera: CameraDevice) {
                    camera.close()
                    stopSelf()
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    Log.e(TAG, "Camera error: $error")
                    camera.close()
                    stopSelf()
                }
            }, null)
        } catch (e: CameraAccessException) {
            Log.e(TAG, "Error accessing camera", e)
            stopSelf()
        }
    }

    private fun stopCameraStream() {
        // Close camera and cleanup resources
        if (::cameraDevice.isInitialized) {
            cameraDevice.close()
        }
    }

    fun setSurfaceHolder(provider: SurfaceHolderProvider) {
        val surfaceHolder = provider.getSurfaceHolder()
        Log.i(TAG, "SurfaceHolder set in EmotionDetection service: ${surfaceHolder != null}")
        // Now you can use this SurfaceHolder for your camera preview or processing
    }

    private fun renderFrame(frame: Mat) {
        if (::surfaceHolder.isInitialized) {
            val canvas = surfaceHolder.lockCanvas()
            if (canvas != null) {
                // Create a bitmap from the OpenCV Mat
                val bitmap =
                    Bitmap.createBitmap(frame.cols(), frame.rows(), Bitmap.Config.ARGB_8888)
                Utils.matToBitmap(frame, bitmap)
                // Draw the bitmap on the canvas
                canvas.drawBitmap(bitmap, 0f, 0f, null)
                // Unlock and post the canvas
                surfaceHolder.unlockCanvasAndPost(canvas)
            }
        }
    }
    override fun onCameraFrame(inputFrame: CvCameraViewFrame): Mat {
        val output = inputFrame.rgba()
        renderFrame(output) // Render the frame to the SurfaceView
        return output
    }

    private fun setUpImageReader() {
        // Set the size and format for the ImageReader
        imageReader = ImageReader.newInstance(640, 480, ImageFormat.YUV_420_888, 2)

        // Set the listener for image availability
        imageReader.setOnImageAvailableListener({ reader ->
            val image = reader.acquireLatestImage()
            image?.let {
                //Log.i(TAG, "Frame captured!")
                it.close() // Make sure to close the image
            }
        }, null)
    }

    private fun createCameraCaptureSession() {
        val surface = imageReader.surface
        val captureRequestBuilder =
            cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
                addTarget(surface)
            }

        cameraDevice.createCaptureSession(
            listOf(surface),
            object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    session.setRepeatingRequest(captureRequestBuilder.build(), null, null)
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    Log.e(TAG, "Camera configuration failed")
                    stopSelf()
                }
            },
            null
        )
    }

    private fun getFrontCameraIDY(context: Context): String? {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            for (cameraId in cameraManager.cameraIdList) {
                val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                val cameraFacing = characteristics.get(CameraCharacteristics.LENS_FACING)

                if (cameraFacing != null && cameraFacing == CameraCharacteristics.LENS_FACING_FRONT) {
                    Log.i("CameraService", "Front camera ID found: $cameraId")
                    return cameraId // Return the ID of the front camera
                }
            }
        } catch (e: Exception) {
            Log.e("CameraService", "Error accessing camera ID", e)
        }

        Log.i("CameraService", "Front camera not found.")
        return null // Return null if no front camera is found
    }
    //*************************************************************************

    override fun onDestroy() {
        super.onDestroy()
        mOpenCvCameraView.disableView()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder // Not binding to any activity
    }

    override fun onCameraViewStarted(width: Int, height: Int) {
        Log.i(TAG, "Camera view started with resolution $width x $height")
    }

    override fun onCameraViewStopped() {
        Log.i(TAG, "Camera view stopped")
    }

    /*
    override fun onCameraFrame(inputFrame: CvCameraViewFrame): Mat {
        Log.i(TAG, "FUCK YOU")
        // Get the RGBA frame from the camera input
        val output = inputFrame.rgba()
        val grayFrame = inputFrame.gray()

        // Define the region of interest (x, y, width, height)
        // 1920 by 1080
        val roiX = 460  // Change these values as needed
        val roiY = 240
        val roiWidth = 1000
        val roiHeight = 600

        Imgproc.rectangle(
            output, // Change this to roi for checking range
            Point(roiX.toDouble(), roiY.toDouble()),
            Point((roiX + roiWidth).toDouble(), (roiY + roiHeight).toDouble()),
            Scalar(0.0, 0.0, 0.0), // Green color for the rectangle
            3
        )

        // Extract the region of interest from the gray frame
        val roi = grayFrame.submat(roiY, roiY + roiHeight, roiX, roiX + roiWidth)

        /**
         * Check if two points are within `threshold` distance of each other on the x and y plane.
         *
         * @param corner1 first point
         * @param corner2 second point
         * @param threshold threshold distance
         *
         * @return if both points are within `threshold` distance of each other.
         */
        fun isWithinThreshold(corner1: Point, corner2: Point, threshold: Int): Boolean {
            val dx = abs(corner1.x - corner2.x)
            val dy = abs(corner1.y - corner2.y)
            return dx <= threshold && dy <= threshold
        }

        /**
         * Check if two faces have corners that are within `threshold` distance of each other.
         *
         * @param rect1 first face to compare
         * @param rect2 second face to compare
         * @param threshold threshold distance
         *
         * @return if both faces are within `threshold` distance of each other.
         */
        fun compareRects(rect1: Rect, rect2: Rect, threshold: Int): Boolean {
            // Extract corners for rect1
            val rect1TopLeft = rect1.tl()  // Top-left corner
            val rect1BottomRight = rect1.br()  // Bottom-right corner
            val rect1TopRight = rect1.tl().apply { x += rect1.width }  // Top-right corner
            val rect1BottomLeft = rect1.tl().apply { y += rect1.height }  // Bottom-left corner

            // Extract corners for rect2
            val rect2TopLeft = rect2.tl()
            val rect2BottomRight = rect2.br()
            val rect2TopRight = rect2.tl().apply { x += rect2.width }
            val rect2BottomLeft = rect2.tl().apply { y += rect2.height }

            // Compare the corresponding corners
            return isWithinThreshold(rect1TopLeft, rect2TopLeft, threshold) &&
                    isWithinThreshold(rect1TopRight, rect2TopRight, threshold) &&
                    isWithinThreshold(rect1BottomLeft, rect2BottomLeft, threshold) &&
                    isWithinThreshold(rect1BottomRight, rect2BottomRight, threshold)
        }

        /**
         * Get classifier result for detected face.
         *
         * @param face face detected
         *
         * @return float array containing results of classifier model.
         */
        fun getClassifierResult(face: Rect): FloatArray {
            // Process the face for emotion recognition
            val faceRegion = roi.submat(face)
            val resizedFace = Mat()
            Imgproc.resize(faceRegion, resizedFace, Size(48.0, 48.0))

            val faceFloat32 = Mat()
            resizedFace.convertTo(faceFloat32, CvType.CV_32F, 1.0 / 255.0)

            val faceArray = FloatArray(48 * 48)
            faceFloat32.get(0, 0, faceArray)

            // Prepare the input tensor for TFLite
            val inputArray = Array(1) { Array(48) { Array(48) { FloatArray(1) } } }
            for (i in 0 until 48) {
                for (j in 0 until 48) {
                    inputArray[0][i][j][0] = faceArray[i * 48 + j]
                }
            }

            // Prepare output for the interpreter: [1, 7]
            val outputArray = Array(1) { FloatArray(7) }

            // Run TFLite inference
            interpreter.run(inputArray, outputArray)

            // Process the output
            val predictions = outputArray[0]

            return predictions
        }

        /**
         * Predict emotion for face with past `memorySize` generations of faces.
         *
         * @param face face to be classified
         * @param threshold threshold distance to determine if faces of different generations are the same face
         *
         * @return Prediction result. If face is not valid, returns `null`.
         */
        fun predictEmotion(face: Rect, threshold: Int = 15): String? {
            // Perform a BFS to find if there are predecessors in the past (memorySize) generations
            val queue = mutableListOf( Pair(face, getClassifierResult(face)) )

            for (generation: Int in 1 until memorySize) {
                val queueLen: Int = queue.size
                for (i in 1..queueLen) {
                    val (popFace: Rect, popResult: FloatArray) = queue.removeAt(index = 0)

                    for ((predecessorFace: Rect, predecessorResult: FloatArray) in facesArrayMemory[generation]) {
                        if (compareRects(popFace, predecessorFace, threshold)) {
                            queue.add( Pair(predecessorFace, popResult.mapIndexed { index, fl -> fl + predecessorResult[index] }.toFloatArray() ) )
                        }
                    }
                }

                if (queue.size == 0) return null
            }

            val finalResult: FloatArray = queue[0].second
            val predictedClassIndex = finalResult.indices.maxByOrNull { finalResult[it] } ?: -1
            return if (predictedClassIndex != -1) emotionDict[predictedClassIndex] else "Unknown"
        }

        // Main face detection logic
        val faces = MatOfRect()
        faceCascade.detectMultiScale(roi, faces)

        val facesArray = faces.toArray()
        val facesArrayResult: List< Pair<Rect, FloatArray> > = facesArray.map{ face: Rect ->
            Pair(face, getClassifierResult(face))
        }

        // Add the new array at index 0
        facesArrayMemory.add(0, facesArrayResult.toTypedArray())

        // Remove the last element
        facesArrayMemory.removeAt(facesArrayMemory.size - 1)


        Log.i("Face!", "Number of faces detected: ${facesArray.size}")

        Log.i("Size!", "Size of memory: ${facesArrayMemory.size}")

        facesArray.forEach { face: Rect ->
            val adjustedTopLeft = Point(face.x + roiX.toDouble(), face.y + roiY.toDouble())
            val adjustedBottomRight = Point(
                face.x + roiX.toDouble() + face.width.toDouble(),
                face.y + roiY.toDouble() + face.height.toDouble()
            )

            predictEmotion(face)?.let { emotionLabel: String ->
                // Draw rectangle
                Imgproc.rectangle(
                    output,
                    adjustedTopLeft,
                    adjustedBottomRight,
                    Scalar(0.0, 255.0, 0.0),
                    3
                )

                val labelPosition = Point(adjustedTopLeft.x, adjustedTopLeft.y - 10) // Position above the rectangle
                Imgproc.putText(
                    output,
                    emotionLabel,
                    labelPosition,
                    Imgproc.FONT_HERSHEY_SIMPLEX,
                    2.0,
                    Scalar(0.0, 255.0, 0.0),
                    10
                )
                Log.i("TREE!", "Emotion: $emotionLabel")
            } ?: run {
                // Draw rectangle
                Imgproc.rectangle(
                    output,
                    adjustedTopLeft,
                    adjustedBottomRight,
                    Scalar(255.0, 0.0, 0.0),
                    3
                )
            }
        }


        currentframe = (currentframe + 1) % 6
        return output
    }
     */


    //**************************************************************************************
    companion object {
        private const val TAG = "OCVSample::Activity"
    }

    private fun loadCascade(): CascadeClassifier {
        val tempFile = File.createTempFile("haarcascade_frontalface_alt", ".xml", cacheDir)

        val inputStream: InputStream =
            resources.openRawResource(R.raw.haarcascade_frontalface_default)
        val outputStream: OutputStream = FileOutputStream(tempFile)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        val classifier = CascadeClassifier(tempFile.absolutePath)

        tempFile.delete()

        return classifier
    }

    private fun getFrontCameraID(context: Context): String? {
        val cManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

        for (cameraId in cManager.getCameraIdList()) {
            val characteristics: CameraCharacteristics =
                cManager.getCameraCharacteristics(cameraId);
            val cOrientation = characteristics.get(CameraCharacteristics.LENS_FACING)

            if (cOrientation == CameraCharacteristics.LENS_FACING_FRONT) return cameraId
        }

        return null
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun loadModelFile(context: Context): ByteBuffer {
    // Create a temporary file to store the raw resource
    val tempFile = File(context.cacheDir, "temp_file.dat")

    // Write the raw resource to the temporary file

    context.resources.openRawResource(R.raw.model).use { inputStream ->
        FileOutputStream(tempFile).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }

    // Create a MappedByteBuffer from the temporary file
    return FileChannel.open(tempFile.toPath(), StandardOpenOption.READ).use { channel ->
        channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
    }
}