package com.temi.temiSDK;

import android.util.Log;
import androidx.lifecycle.ViewModel;
import com.robotemi.sdk.TtsRequest;
import dagger.hilt.android.lifecycle.HiltViewModel;
import javax.inject.Inject;
import kotlin.math.*;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0088\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0011\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0011\u0010\u0007\u001a\u00020)H\u0082@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010*J)\u0010+\u001a\u00020)2\f\u0010,\u001a\b\u0012\u0004\u0012\u00020.0-2\b\b\u0002\u0010/\u001a\u000200H\u0082@\u00f8\u0001\u0000\u00a2\u0006\u0002\u00101J\'\u00102\u001a\u00020)2\f\u0010,\u001a\b\u0012\u0004\u0012\u00020.0-2\u0006\u00103\u001a\u00020\u0016H\u0082@\u00f8\u0001\u0000\u00a2\u0006\u0002\u00104J\u0018\u00105\u001a\u00020\u00062\u0006\u00106\u001a\u00020\u00062\u0006\u00107\u001a\u00020\u0006H\u0002J\u000e\u00108\u001a\u00020)2\u0006\u00109\u001a\u00020.J\u0006\u0010:\u001a\u00020.J\u000e\u0010;\u001a\u00020)2\u0006\u0010\u0015\u001a\u00020\u0016J\u0019\u0010<\u001a\u00020)2\u0006\u0010=\u001a\u000200H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010>J\u000e\u0010?\u001a\u00020)2\u0006\u0010@\u001a\u00020\u0016R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0006X\u0082D\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00120\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00140\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0016X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00180\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u001a0\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001b\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001d\u001a\u00020\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u001f0\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010 \u001a\u00020!X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\"\u001a\u00020#X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010$\u001a\u00020!X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010%\u001a\u00020&X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\'\u001a\u00020(X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006A"}, d2 = {"Lcom/temi/temiSDK/MainViewModel;", "Landroidx/lifecycle/ViewModel;", "robotController", "Lcom/temi/temiSDK/RobotController;", "(Lcom/temi/temiSDK/RobotController;)V", "boundary", "", "buffer", "", "currentState", "Lcom/temi/temiSDK/State;", "currentUserAngle", "currentUserDistance", "defaultAngle", "detectionData", "Lkotlinx/coroutines/flow/StateFlow;", "Lcom/temi/temiSDK/DetectionDataChangedStatus;", "detectionStatus", "Lcom/temi/temiSDK/DetectionStateChangedStatus;", "dragged", "Lcom/temi/temiSDK/Dragged;", "int", "", "lifted", "Lcom/temi/temiSDK/Lifted;", "movementStatus", "Lcom/temi/temiSDK/MovementStatusChangedStatus;", "previousUserAngle", "previousUserDistance", "stateMode", "ttsStatus", "Lcom/temi/temiSDK/TtsStatus;", "userRelativeDirection", "Lcom/temi/temiSDK/XDirection;", "xMotion", "Lcom/temi/temiSDK/XMovement;", "xPosition", "yMotion", "Lcom/temi/temiSDK/YMovement;", "yPosition", "Lcom/temi/temiSDK/YDirection;", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "conditionGate", "trigger", "Lkotlin/Function0;", "", "log", "", "(Lkotlin/jvm/functions/Function0;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "conditionTimer", "time", "(Lkotlin/jvm/functions/Function0;ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getDirectedAngle", "a1", "a2", "idleMode", "setIdleMode", "isMissuesState", "resultSpeech", "speech", "text", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "volumeControl", "volume", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel
public final class MainViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull
    private final com.temi.temiSDK.RobotController robotController = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<com.temi.temiSDK.TtsStatus> ttsStatus = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<com.temi.temiSDK.DetectionStateChangedStatus> detectionStatus = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<com.temi.temiSDK.DetectionDataChangedStatus> detectionData = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<com.temi.temiSDK.MovementStatusChangedStatus> movementStatus = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<com.temi.temiSDK.Lifted> lifted = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<com.temi.temiSDK.Dragged> dragged = null;
    private final long buffer = 100L;
    @org.jetbrains.annotations.NotNull
    private com.temi.temiSDK.State currentState = com.temi.temiSDK.State.NULL;
    @org.jetbrains.annotations.NotNull
    private com.temi.temiSDK.State stateMode = com.temi.temiSDK.State.NULL;
    private final double defaultAngle = 180.0;
    private final double boundary = 90.0;
    @org.jetbrains.annotations.NotNull
    private com.temi.temiSDK.XDirection userRelativeDirection = com.temi.temiSDK.XDirection.GONE;
    private double previousUserAngle = 0.0;
    private double currentUserAngle = 0.0;
    @org.jetbrains.annotations.NotNull
    private com.temi.temiSDK.XDirection xPosition = com.temi.temiSDK.XDirection.GONE;
    @org.jetbrains.annotations.NotNull
    private com.temi.temiSDK.XMovement xMotion = com.temi.temiSDK.XMovement.NOWHERE;
    private double previousUserDistance = 0.0;
    private double currentUserDistance = 0.0;
    @org.jetbrains.annotations.NotNull
    private com.temi.temiSDK.YDirection yPosition = com.temi.temiSDK.YDirection.MISSING;
    @org.jetbrains.annotations.NotNull
    private com.temi.temiSDK.YMovement yMotion = com.temi.temiSDK.YMovement.NOWHERE;
    
    @javax.inject.Inject
    public MainViewModel(@org.jetbrains.annotations.NotNull
    com.temi.temiSDK.RobotController robotController) {
        super();
    }
    
    public final void resultSpeech(int p0_52215) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object speech(@org.jetbrains.annotations.NotNull
    java.lang.String text, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    public final void idleMode(boolean setIdleMode) {
    }
    
    public final boolean isMissuesState() {
        return false;
    }
    
    public final void volumeControl(int volume) {
    }
    
    private final java.lang.Object buffer(kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final java.lang.Object conditionTimer(kotlin.jvm.functions.Function0<java.lang.Boolean> trigger, int time, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final java.lang.Object conditionGate(kotlin.jvm.functions.Function0<java.lang.Boolean> trigger, java.lang.String log, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final double getDirectedAngle(double a1, double a2) {
        return 0.0;
    }
}