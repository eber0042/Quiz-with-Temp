package com.temi.temiSDK;

import android.content.Context;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tflite.java.TfLite;
import kotlinx.coroutines.Dispatchers;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.tensorflow.lite.InterpreterApi;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u008e\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0014\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u0000 @2\u00020\u00012\u00020\u0002:\u0001@B\u0005\u00a2\u0006\u0002\u0010\u0003J\u000e\u0010+\u001a\b\u0012\u0004\u0012\u00020\'0,H\u0014J\u0012\u0010-\u001a\u0004\u0018\u00010\u000e2\u0006\u0010.\u001a\u00020/H\u0002J\b\u00100\u001a\u00020\u0012H\u0002J\u0010\u00101\u001a\u0002022\u0006\u00103\u001a\u000204H\u0016J\u0018\u00105\u001a\u0002062\u0006\u00107\u001a\u00020\u00072\u0006\u00108\u001a\u00020\u0007H\u0016J\b\u00109\u001a\u000206H\u0016J\u0012\u0010:\u001a\u0002062\b\u0010;\u001a\u0004\u0018\u00010<H\u0017J\b\u0010=\u001a\u000206H\u0016J\b\u0010>\u001a\u000206H\u0016J\b\u0010?\u001a\u000206H\u0016R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0006\u001a\u00020\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u001d\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u000e0\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082.\u00a2\u0006\u0002\n\u0000R2\u0010\u0013\u001a\u001a\u0012\u0016\u0012\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0017\u0012\u0004\u0012\u00020\u00180\u00160\u00150\u0014X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u001a\"\u0004\b\u001b\u0010\u001cR!\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u001f0\u001e8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\"\u0010#\u001a\u0004\b \u0010!R\u000e\u0010$\u001a\u00020%X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010&\u001a\u00020\'X\u0082.\u00a2\u0006\u0002\n\u0000R\u001a\u0010(\u001a\u00020\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b)\u0010\t\"\u0004\b*\u0010\u000b\u00a8\u0006A"}, d2 = {"Lcom/temi/temiSDK/EmotionDetection;", "Lorg/opencv/android/CameraActivity;", "Lorg/opencv/android/CameraBridgeViewBase$CvCameraViewListener2;", "()V", "coroutineScope", "Lkotlinx/coroutines/CoroutineScope;", "currentframe", "", "getCurrentframe", "()I", "setCurrentframe", "(I)V", "emotionDict", "", "", "getEmotionDict", "()Ljava/util/Map;", "faceCascade", "Lorg/opencv/objdetect/CascadeClassifier;", "facesArrayMemory", "", "", "Lkotlin/Pair;", "Lorg/opencv/core/Rect;", "", "getFacesArrayMemory", "()Ljava/util/List;", "setFacesArrayMemory", "(Ljava/util/List;)V", "initializeTask", "Lcom/google/android/gms/tasks/Task;", "Ljava/lang/Void;", "getInitializeTask", "()Lcom/google/android/gms/tasks/Task;", "initializeTask$delegate", "Lkotlin/Lazy;", "interpreter", "Lorg/tensorflow/lite/InterpreterApi;", "mOpenCvCameraView", "Lorg/opencv/android/CameraBridgeViewBase;", "memorySize", "getMemorySize", "setMemorySize", "getCameraViewList", "", "getFrontCameraID", "context", "Landroid/content/Context;", "loadCascade", "onCameraFrame", "Lorg/opencv/core/Mat;", "inputFrame", "Lorg/opencv/android/CameraBridgeViewBase$CvCameraViewFrame;", "onCameraViewStarted", "", "width", "height", "onCameraViewStopped", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onDestroy", "onPause", "onResume", "Companion", "app_debug"})
public final class EmotionDetection extends org.opencv.android.CameraActivity implements org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2 {
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.CoroutineScope coroutineScope = null;
    private org.opencv.android.CameraBridgeViewBase mOpenCvCameraView;
    private org.opencv.objdetect.CascadeClassifier faceCascade;
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy initializeTask$delegate = null;
    private org.tensorflow.lite.InterpreterApi interpreter;
    
    /**
     * Emotion dictionary to map indices to emotion labels
     */
    @org.jetbrains.annotations.NotNull
    private final java.util.Map<java.lang.Integer, java.lang.String> emotionDict = null;
    
    /**
     * List that stores previous generation of faces and corresponding classifier results.
     */
    @org.jetbrains.annotations.NotNull
    private java.util.List<kotlin.Pair<org.opencv.core.Rect, float[]>[]> facesArrayMemory;
    
    /**
     * Number of previous iterations of faces stored in memory.
     */
    private int memorySize = 5;
    private int currentframe = 5;
    @org.jetbrains.annotations.NotNull
    private static final java.lang.String TAG = "OCVSample::Activity";
    @org.jetbrains.annotations.NotNull
    public static final com.temi.temiSDK.EmotionDetection.Companion Companion = null;
    
    public EmotionDetection() {
        super();
    }
    
    private final com.google.android.gms.tasks.Task<java.lang.Void> getInitializeTask() {
        return null;
    }
    
    /**
     * Emotion dictionary to map indices to emotion labels
     */
    @org.jetbrains.annotations.NotNull
    public final java.util.Map<java.lang.Integer, java.lang.String> getEmotionDict() {
        return null;
    }
    
    /**
     * List that stores previous generation of faces and corresponding classifier results.
     */
    @org.jetbrains.annotations.NotNull
    public final java.util.List<kotlin.Pair<org.opencv.core.Rect, float[]>[]> getFacesArrayMemory() {
        return null;
    }
    
    /**
     * List that stores previous generation of faces and corresponding classifier results.
     */
    public final void setFacesArrayMemory(@org.jetbrains.annotations.NotNull
    java.util.List<kotlin.Pair<org.opencv.core.Rect, float[]>[]> p0) {
    }
    
    /**
     * Number of previous iterations of faces stored in memory.
     */
    public final int getMemorySize() {
        return 0;
    }
    
    /**
     * Number of previous iterations of faces stored in memory.
     */
    public final void setMemorySize(int p0) {
    }
    
    public final int getCurrentframe() {
        return 0;
    }
    
    public final void setCurrentframe(int p0) {
    }
    
    @java.lang.Override
    @androidx.annotation.RequiresApi(value = android.os.Build.VERSION_CODES.O)
    public void onCreate(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override
    public void onPause() {
    }
    
    @java.lang.Override
    public void onResume() {
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    protected java.util.List<org.opencv.android.CameraBridgeViewBase> getCameraViewList() {
        return null;
    }
    
    @java.lang.Override
    public void onDestroy() {
    }
    
    @java.lang.Override
    public void onCameraViewStarted(int width, int height) {
    }
    
    @java.lang.Override
    public void onCameraViewStopped() {
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public org.opencv.core.Mat onCameraFrame(@org.jetbrains.annotations.NotNull
    org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        return null;
    }
    
    private final org.opencv.objdetect.CascadeClassifier loadCascade() {
        return null;
    }
    
    private final java.lang.String getFrontCameraID(android.content.Context context) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/temi/temiSDK/EmotionDetection$Companion;", "", "()V", "TAG", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}