package com.temi.temiSDK;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.hardware.camera2.*;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.ImageReader;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import kotlinx.coroutines.Dispatchers;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
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
import org.opencv.videoio.VideoCapture;
import android.os.Message;
import android.os.RemoteException;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u00a6\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010$\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0014\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u0000 P2\u00020\u00012\u00020\u0002:\u0002PQB\u0005\u00a2\u0006\u0002\u0010\u0003J\b\u00102\u001a\u000203H\u0002J\u0012\u00104\u001a\u0004\u0018\u00010\u00052\u0006\u00105\u001a\u000206H\u0002J\u0012\u00107\u001a\u0004\u0018\u00010\u00052\u0006\u00105\u001a\u000206H\u0002J\b\u00108\u001a\u00020\u001cH\u0002J\u0012\u00109\u001a\u00020:2\b\u0010;\u001a\u0004\u0018\u00010<H\u0016J\u0010\u0010=\u001a\u00020>2\u0006\u0010?\u001a\u00020@H\u0016J\u0018\u0010A\u001a\u0002032\u0006\u0010B\u001a\u00020\u00122\u0006\u0010C\u001a\u00020\u0012H\u0016J\b\u0010D\u001a\u000203H\u0016J\b\u0010E\u001a\u000203H\u0017J\b\u0010F\u001a\u000203H\u0016J\u0010\u0010G\u001a\u0002032\u0006\u0010H\u001a\u00020>H\u0002J\u000e\u0010I\u001a\u0002032\u0006\u0010J\u001a\u00020KJ\b\u0010L\u001a\u000203H\u0002J\u0010\u0010M\u001a\u0002032\u0006\u0010N\u001a\u00020\u0005H\u0003J\b\u0010O\u001a\u000203H\u0002R\u0010\u0010\u0004\u001a\u00020\u0005X\u0082D\u00a2\u0006\u0004\n\u0002\b\u0006R\u0012\u0010\u0007\u001a\u00060\bR\u00020\u0000X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\t\u001a\u00020\nX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u000e\u0010\u000f\u001a\u00020\u0010X\u0082.\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0011\u001a\u00020\u0012X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0014\"\u0004\b\u0015\u0010\u0016R\u001d\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\u0012\u0012\u0004\u0012\u00020\u00050\u0018\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u000e\u0010\u001b\u001a\u00020\u001cX\u0082.\u00a2\u0006\u0002\n\u0000R2\u0010\u001d\u001a\u001a\u0012\u0016\u0012\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020!\u0012\u0004\u0012\u00020\"0 0\u001f0\u001eX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b#\u0010$\"\u0004\b%\u0010&R\u000e\u0010\'\u001a\u00020(X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010)\u001a\u00020*X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010+\u001a\u00020,X\u0082.\u00a2\u0006\u0002\n\u0000R\u001a\u0010-\u001a\u00020\u0012X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b.\u0010\u0014\"\u0004\b/\u0010\u0016R\u000e\u00100\u001a\u000201X\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006R"}, d2 = {"Lcom/temi/temiSDK/EmotionDetection;", "Landroid/app/Service;", "Lorg/opencv/android/CameraBridgeViewBase$CvCameraViewListener2;", "()V", "TAG", "", "TAG$1", "binder", "Lcom/temi/temiSDK/EmotionDetection$EmotionDetectionBinder;", "boo", "", "getBoo", "()Z", "setBoo", "(Z)V", "cameraDevice", "Landroid/hardware/camera2/CameraDevice;", "currentframe", "", "getCurrentframe", "()I", "setCurrentframe", "(I)V", "emotionDict", "", "getEmotionDict", "()Ljava/util/Map;", "faceCascade", "Lorg/opencv/objdetect/CascadeClassifier;", "facesArrayMemory", "", "", "Lkotlin/Pair;", "Lorg/opencv/core/Rect;", "", "getFacesArrayMemory", "()Ljava/util/List;", "setFacesArrayMemory", "(Ljava/util/List;)V", "imageReader", "Landroid/media/ImageReader;", "interpreter", "Lorg/tensorflow/lite/InterpreterApi;", "mOpenCvCameraView", "Lorg/opencv/android/CameraBridgeViewBase;", "memorySize", "getMemorySize", "setMemorySize", "surfaceHolder", "Landroid/view/SurfaceHolder;", "createCameraCaptureSession", "", "getFrontCameraID", "context", "Landroid/content/Context;", "getFrontCameraIDY", "loadCascade", "onBind", "Landroid/os/IBinder;", "intent", "Landroid/content/Intent;", "onCameraFrame", "Lorg/opencv/core/Mat;", "inputFrame", "Lorg/opencv/android/CameraBridgeViewBase$CvCameraViewFrame;", "onCameraViewStarted", "width", "height", "onCameraViewStopped", "onCreate", "onDestroy", "renderFrame", "frame", "setSurfaceHolder", "provider", "Lcom/temi/temiSDK/SurfaceHolderProvider;", "setUpImageReader", "startCameraStream", "cameraId", "stopCameraStream", "Companion", "EmotionDetectionBinder", "app_debug"})
public final class EmotionDetection extends android.app.Service implements org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2 {
    private android.hardware.camera2.CameraDevice cameraDevice;
    private android.media.ImageReader imageReader;
    private org.opencv.android.CameraBridgeViewBase mOpenCvCameraView;
    private org.opencv.objdetect.CascadeClassifier faceCascade;
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
    private boolean boo = false;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String TAG$1 = "HELLO!";
    @org.jetbrains.annotations.NotNull
    private final com.temi.temiSDK.EmotionDetection.EmotionDetectionBinder binder = null;
    private android.view.SurfaceHolder surfaceHolder;
    @org.jetbrains.annotations.NotNull
    private static final java.lang.String TAG = "OCVSample::Activity";
    @org.jetbrains.annotations.NotNull
    public static final com.temi.temiSDK.EmotionDetection.Companion Companion = null;
    
    public EmotionDetection() {
        super();
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
    
    public final boolean getBoo() {
        return false;
    }
    
    public final void setBoo(boolean p0) {
    }
    
    @java.lang.Override
    @androidx.annotation.RequiresApi(value = android.os.Build.VERSION_CODES.O)
    public void onCreate() {
    }
    
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    private final void startCameraStream(java.lang.String cameraId) {
    }
    
    private final void stopCameraStream() {
    }
    
    public final void setSurfaceHolder(@org.jetbrains.annotations.NotNull
    com.temi.temiSDK.SurfaceHolderProvider provider) {
    }
    
    private final void renderFrame(org.opencv.core.Mat frame) {
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public org.opencv.core.Mat onCameraFrame(@org.jetbrains.annotations.NotNull
    org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        return null;
    }
    
    private final void setUpImageReader() {
    }
    
    private final void createCameraCaptureSession() {
    }
    
    private final java.lang.String getFrontCameraIDY(android.content.Context context) {
        return null;
    }
    
    @java.lang.Override
    public void onDestroy() {
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public android.os.IBinder onBind(@org.jetbrains.annotations.Nullable
    android.content.Intent intent) {
        return null;
    }
    
    @java.lang.Override
    public void onCameraViewStarted(int width, int height) {
    }
    
    @java.lang.Override
    public void onCameraViewStopped() {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0003\u001a\u00020\u0004\u00a8\u0006\u0005"}, d2 = {"Lcom/temi/temiSDK/EmotionDetection$EmotionDetectionBinder;", "Landroid/os/Binder;", "(Lcom/temi/temiSDK/EmotionDetection;)V", "getService", "Lcom/temi/temiSDK/EmotionDetection;", "app_debug"})
    public final class EmotionDetectionBinder extends android.os.Binder {
        
        public EmotionDetectionBinder() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        public final com.temi.temiSDK.EmotionDetection getService() {
            return null;
        }
    }
}