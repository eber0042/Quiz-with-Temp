package com.temi.temiSDK;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import androidx.lifecycle.ViewModel;
import com.robotemi.sdk.TtsRequest;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
import javax.inject.Inject;
import kotlin.math.*;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u00a6\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0017\b\u0007\u0018\u00002\u00020\u0001B\u0019\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0011\u0010\t\u001a\u000204H\u0082@\u00f8\u0001\u0000\u00a2\u0006\u0002\u00105J\u000e\u00106\u001a\u0002042\u0006\u00107\u001a\u000208J)\u00109\u001a\u0002042\f\u0010:\u001a\b\u0012\u0004\u0012\u00020<0;2\b\b\u0002\u0010=\u001a\u00020\u001aH\u0082@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010>J\'\u0010?\u001a\u0002042\f\u0010:\u001a\b\u0012\u0004\u0012\u00020<0;2\u0006\u0010@\u001a\u00020\fH\u0082@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010AJ\u0018\u0010B\u001a\u00020\b2\u0006\u0010C\u001a\u00020\b2\u0006\u0010D\u001a\u00020\bH\u0002J\u000e\u0010E\u001a\u0002042\u0006\u0010F\u001a\u00020<J\u0006\u0010G\u001a\u00020<J$\u0010H\u001a\u0002042\b\b\u0002\u0010\u001b\u001a\u00020\f2\b\b\u0002\u0010I\u001a\u00020\'2\b\b\u0002\u0010%\u001a\u00020\u001aJ\u0019\u0010J\u001a\u0002042\u0006\u0010K\u001a\u00020\u001aH\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010LJ\u000e\u0010M\u001a\u0002042\u0006\u0010K\u001a\u00020\u001aJ!\u0010N\u001a\u0002042\u0006\u0010I\u001a\u00020\f2\u0006\u0010\t\u001a\u00020\nH\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010OJ\u0010\u0010P\u001a\u0002042\b\u0010\u0019\u001a\u0004\u0018\u00010\u001aJ\u000e\u0010Q\u001a\u0002042\u0006\u0010R\u001a\u00020\fR\u000e\u0010\u0007\u001a\u00020\bX\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\bX\u0082D\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00140\u0013X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00160\u0013X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00180\u0013X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0019\u001a\u0004\u0018\u00010\u001aX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001b\u001a\u00020\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u001d0\u0013X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u001f0\u0013X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010 \u001a\b\u0012\u0004\u0012\u00020\f0!X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\"\u001a\u00020\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010#\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010$\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010%\u001a\u00020\u001aX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010&\u001a\u00020\'X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010(\u001a\u00020\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010)\u001a\b\u0012\u0004\u0012\u00020*0\u0013X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010+\u001a\u00020,X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010-\u001a\u00020.X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010/\u001a\u00020,X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u00100\u001a\u000201X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u00102\u001a\u000203X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006S"}, d2 = {"Lcom/temi/temiSDK/MainViewModel;", "Landroidx/lifecycle/ViewModel;", "robotController", "Lcom/temi/temiSDK/RobotController;", "context", "Landroid/content/Context;", "(Lcom/temi/temiSDK/RobotController;Landroid/content/Context;)V", "boundary", "", "buffer", "", "currentIndex", "", "currentState", "Lcom/temi/temiSDK/State;", "currentUserAngle", "currentUserDistance", "defaultAngle", "detectionData", "Lkotlinx/coroutines/flow/StateFlow;", "Lcom/temi/temiSDK/DetectionDataChangedStatus;", "detectionStatus", "Lcom/temi/temiSDK/DetectionStateChangedStatus;", "dragged", "Lcom/temi/temiSDK/Dragged;", "emotion", "", "int", "lifted", "Lcom/temi/temiSDK/Lifted;", "movementStatus", "Lcom/temi/temiSDK/MovementStatusChangedStatus;", "numberArray", "", "previousLastChoice", "previousUserAngle", "previousUserDistance", "say", "speechState", "Lcom/temi/temiSDK/SpeechState;", "stateMode", "ttsStatus", "Lcom/temi/temiSDK/TtsStatus;", "userRelativeDirection", "Lcom/temi/temiSDK/XDirection;", "xMotion", "Lcom/temi/temiSDK/XMovement;", "xPosition", "yMotion", "Lcom/temi/temiSDK/YMovement;", "yPosition", "Lcom/temi/temiSDK/YDirection;", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "changeLanguage", "language", "Lcom/robotemi/sdk/TtsRequest$Language;", "conditionGate", "trigger", "Lkotlin/Function0;", "", "log", "(Lkotlin/jvm/functions/Function0;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "conditionTimer", "time", "(Lkotlin/jvm/functions/Function0;ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getDirectedAngle", "a1", "a2", "idleMode", "setIdleMode", "isMissuesState", "resultSpeech", "state", "speech", "text", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "speechUi", "textModelChoice", "(IJLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateEmotion", "volumeControl", "volume", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel
public final class MainViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull
    private final com.temi.temiSDK.RobotController robotController = null;
    @org.jetbrains.annotations.NotNull
    private final android.content.Context context = null;
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
    @org.jetbrains.annotations.NotNull
    private com.temi.temiSDK.SpeechState speechState = com.temi.temiSDK.SpeechState.QUIZ;
    @org.jetbrains.annotations.NotNull
    private java.lang.String say = "Hello, World";
    @org.jetbrains.annotations.Nullable
    private java.lang.String emotion;
    @org.jetbrains.annotations.NotNull
    private final java.util.List<java.lang.Integer> numberArray = null;
    private int currentIndex = 0;
    private int previousLastChoice = -1;
    
    @javax.inject.Inject
    public MainViewModel(@org.jetbrains.annotations.NotNull
    com.temi.temiSDK.RobotController robotController, @dagger.hilt.android.qualifiers.ApplicationContext
    @org.jetbrains.annotations.NotNull
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object textModelChoice(int state, long buffer, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    public final void resultSpeech(int p0_52215, @org.jetbrains.annotations.NotNull
    com.temi.temiSDK.SpeechState state, @org.jetbrains.annotations.NotNull
    java.lang.String say) {
    }
    
    public final void speechUi(@org.jetbrains.annotations.NotNull
    java.lang.String text) {
    }
    
    public final void changeLanguage(@org.jetbrains.annotations.NotNull
    com.robotemi.sdk.TtsRequest.Language language) {
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
    
    public final void updateEmotion(@org.jetbrains.annotations.Nullable
    java.lang.String emotion) {
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