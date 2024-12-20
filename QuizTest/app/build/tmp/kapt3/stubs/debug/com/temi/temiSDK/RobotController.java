package com.temi.temiSDK;

import android.content.pm.PackageManager;
import android.health.connect.datatypes.units.Volume;
import android.util.Log;
import androidx.core.content.ContextCompat;
import com.robotemi.sdk.ISdkService;
import com.robotemi.sdk.Robot;
import com.robotemi.sdk.listeners.OnRobotReadyListener;
import com.robotemi.sdk.listeners.OnDetectionStateChangedListener;
import com.robotemi.sdk.listeners.OnDetectionDataChangedListener;
import com.robotemi.sdk.listeners.OnMovementStatusChangedListener;
import com.robotemi.sdk.listeners.OnRobotLiftedListener;
import com.robotemi.sdk.listeners.OnRobotDragStateChangedListener;
import com.robotemi.sdk.TtsRequest;
import com.robotemi.sdk.constants.HardButton;
import com.robotemi.sdk.model.DetectionData;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Singleton;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u00a2\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\t\u0018\u00002\u00020\u00012\u00020\u00022\u00020\u00032\u00020\u00042\u00020\u00052\u00020\u00062\u00020\u0007B\u0005\u00a2\u0006\u0002\u0010\bJ\u000e\u0010(\u001a\u00020)2\u0006\u0010\u001e\u001a\u00020\u001fJ\u0006\u0010*\u001a\u00020+J\u0010\u0010,\u001a\u00020)2\u0006\u0010-\u001a\u00020.H\u0016J\u0010\u0010/\u001a\u00020)2\u0006\u00100\u001a\u000201H\u0016J\u0018\u00102\u001a\u00020)2\u0006\u00103\u001a\u0002042\u0006\u00105\u001a\u000204H\u0016J\u0010\u00106\u001a\u00020)2\u0006\u00107\u001a\u000208H\u0016J\u0018\u00109\u001a\u00020)2\u0006\u0010:\u001a\u0002082\u0006\u0010;\u001a\u000204H\u0016J\u0010\u0010<\u001a\u00020)2\u0006\u0010=\u001a\u000208H\u0016J\u0010\u0010>\u001a\u00020)2\u0006\u0010?\u001a\u00020@H\u0016J!\u0010A\u001a\u00020)2\u0006\u0010B\u001a\u0002042\u0006\u0010C\u001a\u00020DH\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010EJ+\u0010F\u001a\u00020)2\u0006\u0010G\u001a\u0002012\b\b\u0002\u0010H\u001a\u00020+2\u0006\u0010C\u001a\u00020DH\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010IJ+\u0010J\u001a\u00020)2\u0006\u0010G\u001a\u0002012\b\b\u0002\u0010H\u001a\u00020+2\u0006\u0010C\u001a\u00020DH\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010IJ\u000e\u0010K\u001a\u00020)2\u0006\u0010L\u001a\u000201R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\r0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00130\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00150\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0017\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\r0\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0019R\u0017\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0019R\u000e\u0010\u001e\u001a\u00020\u001fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010 \u001a\b\u0012\u0004\u0012\u00020\u00110\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u0019R\u0017\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u00130\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u0019R\u000e\u0010$\u001a\u00020%X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010&\u001a\b\u0012\u0004\u0012\u00020\u00150\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\b\'\u0010\u0019\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006M"}, d2 = {"Lcom/temi/temiSDK/RobotController;", "Lcom/robotemi/sdk/listeners/OnRobotReadyListener;", "Lcom/robotemi/sdk/listeners/OnDetectionStateChangedListener;", "Lcom/robotemi/sdk/Robot$TtsListener;", "Lcom/robotemi/sdk/listeners/OnDetectionDataChangedListener;", "Lcom/robotemi/sdk/listeners/OnMovementStatusChangedListener;", "Lcom/robotemi/sdk/listeners/OnRobotLiftedListener;", "Lcom/robotemi/sdk/listeners/OnRobotDragStateChangedListener;", "()V", "_detectionDataChangedStatus", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/temi/temiSDK/DetectionDataChangedStatus;", "_detectionStateChangedStatus", "Lcom/temi/temiSDK/DetectionStateChangedStatus;", "_dragged", "Lcom/temi/temiSDK/Dragged;", "_lifted", "Lcom/temi/temiSDK/Lifted;", "_movementStatusChangedStatus", "Lcom/temi/temiSDK/MovementStatusChangedStatus;", "_ttsStatus", "Lcom/temi/temiSDK/TtsStatus;", "detectionDataChangedStatus", "Lkotlinx/coroutines/flow/StateFlow;", "getDetectionDataChangedStatus", "()Lkotlinx/coroutines/flow/StateFlow;", "detectionStateChangedStatus", "getDetectionStateChangedStatus", "dragged", "getDragged", "language", "Lcom/robotemi/sdk/TtsRequest$Language;", "lifted", "getLifted", "movementStatusChangedStatus", "getMovementStatusChangedStatus", "robot", "Lcom/robotemi/sdk/Robot;", "ttsStatus", "getTtsStatus", "changeLanguage", "", "getPositionYaw", "", "onDetectionDataChanged", "detectionData", "Lcom/robotemi/sdk/model/DetectionData;", "onDetectionStateChanged", "state", "", "onMovementStatusChanged", "type", "", "status", "onRobotDragStateChanged", "isDragged", "", "onRobotLifted", "isLifted", "reason", "onRobotReady", "isReady", "onTtsStatusChanged", "ttsRequest", "Lcom/robotemi/sdk/TtsRequest;", "speak", "speech", "buffer", "", "(Ljava/lang/String;JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "tiltAngle", "degree", "speed", "(IFJLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "turnBy", "volumeControl", "volume", "app_debug"})
public final class RobotController implements com.robotemi.sdk.listeners.OnRobotReadyListener, com.robotemi.sdk.listeners.OnDetectionStateChangedListener, com.robotemi.sdk.Robot.TtsListener, com.robotemi.sdk.listeners.OnDetectionDataChangedListener, com.robotemi.sdk.listeners.OnMovementStatusChangedListener, com.robotemi.sdk.listeners.OnRobotLiftedListener, com.robotemi.sdk.listeners.OnRobotDragStateChangedListener {
    @org.jetbrains.annotations.NotNull
    private com.robotemi.sdk.TtsRequest.Language language = com.robotemi.sdk.TtsRequest.Language.EN_US;
    @org.jetbrains.annotations.NotNull
    private final com.robotemi.sdk.Robot robot = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<com.temi.temiSDK.TtsStatus> _ttsStatus = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<com.temi.temiSDK.TtsStatus> ttsStatus = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<com.temi.temiSDK.DetectionStateChangedStatus> _detectionStateChangedStatus = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<com.temi.temiSDK.DetectionStateChangedStatus> detectionStateChangedStatus = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<com.temi.temiSDK.DetectionDataChangedStatus> _detectionDataChangedStatus = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<com.temi.temiSDK.DetectionDataChangedStatus> detectionDataChangedStatus = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<com.temi.temiSDK.MovementStatusChangedStatus> _movementStatusChangedStatus = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<com.temi.temiSDK.MovementStatusChangedStatus> movementStatusChangedStatus = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<com.temi.temiSDK.Dragged> _dragged = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<com.temi.temiSDK.Dragged> dragged = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<com.temi.temiSDK.Lifted> _lifted = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<com.temi.temiSDK.Lifted> lifted = null;
    
    public RobotController() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<com.temi.temiSDK.TtsStatus> getTtsStatus() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<com.temi.temiSDK.DetectionStateChangedStatus> getDetectionStateChangedStatus() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<com.temi.temiSDK.DetectionDataChangedStatus> getDetectionDataChangedStatus() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<com.temi.temiSDK.MovementStatusChangedStatus> getMovementStatusChangedStatus() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<com.temi.temiSDK.Dragged> getDragged() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<com.temi.temiSDK.Lifted> getLifted() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object speak(@org.jetbrains.annotations.NotNull
    java.lang.String speech, long buffer, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object turnBy(int degree, float speed, long buffer, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object tiltAngle(int degree, float speed, long buffer, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    public final void changeLanguage(@org.jetbrains.annotations.NotNull
    com.robotemi.sdk.TtsRequest.Language language) {
    }
    
    public final float getPositionYaw() {
        return 0.0F;
    }
    
    public final void volumeControl(int volume) {
    }
    
    /**
     * Called when connection with robot was established.
     *
     * @param isReady `true` when connection is open. `false` otherwise.
     */
    @java.lang.Override
    public void onRobotReady(boolean isReady) {
    }
    
    @java.lang.Override
    public void onTtsStatusChanged(@org.jetbrains.annotations.NotNull
    com.robotemi.sdk.TtsRequest ttsRequest) {
    }
    
    @java.lang.Override
    public void onDetectionStateChanged(int state) {
    }
    
    @java.lang.Override
    public void onDetectionDataChanged(@org.jetbrains.annotations.NotNull
    com.robotemi.sdk.model.DetectionData detectionData) {
    }
    
    @java.lang.Override
    public void onMovementStatusChanged(@org.jetbrains.annotations.NotNull
    java.lang.String type, @org.jetbrains.annotations.NotNull
    java.lang.String status) {
    }
    
    @java.lang.Override
    public void onRobotLifted(boolean isLifted, @org.jetbrains.annotations.NotNull
    java.lang.String reason) {
    }
    
    @java.lang.Override
    public void onRobotDragStateChanged(boolean isDragged) {
    }
}