package com.temi.temiSDK;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.robotemi.sdk.TtsRequest;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
import java.util.Locale;
import javax.inject.Inject;
import kotlin.math.*;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u00b2\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0018\b\u0007\u0018\u00002\u00020\u0001:\u0001[B\u0019\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0011\u0010\f\u001a\u00020:H\u0082@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010;J\u000e\u0010<\u001a\u00020:2\u0006\u0010=\u001a\u00020>J\u000e\u0010?\u001a\u00020:2\u0006\u0010@\u001a\u00020\tJ)\u0010A\u001a\u00020:2\f\u0010B\u001a\b\u0012\u0004\u0012\u00020D0C2\b\b\u0002\u0010E\u001a\u00020\tH\u0082@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010FJ\'\u0010G\u001a\u00020:2\f\u0010B\u001a\b\u0012\u0004\u0012\u00020D0C2\u0006\u0010H\u001a\u00020\u000fH\u0082@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010IJ\u0018\u0010J\u001a\u00020\u000b2\u0006\u0010K\u001a\u00020\u000b2\u0006\u0010L\u001a\u00020\u000bH\u0002J\u000e\u0010M\u001a\u00020:2\u0006\u0010N\u001a\u00020DJ\u0006\u0010O\u001a\u00020DJ$\u0010P\u001a\u00020:2\b\b\u0002\u0010!\u001a\u00020\u000f2\b\b\u0002\u0010Q\u001a\u00020-2\b\b\u0002\u0010+\u001a\u00020\tJ\u0019\u0010R\u001a\u00020:2\u0006\u0010S\u001a\u00020\tH\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010TJ\u000e\u0010U\u001a\u00020:2\u0006\u0010S\u001a\u00020\tJ!\u0010V\u001a\u00020:2\u0006\u0010Q\u001a\u00020\u000f2\u0006\u0010\f\u001a\u00020\rH\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010WJ\u0010\u0010X\u001a\u00020:2\b\u0010 \u001a\u0004\u0018\u00010\tJ\u000e\u0010Y\u001a\u00020:2\u0006\u0010Z\u001a\u00020\u000fR\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\t0\u00118F\u00a2\u0006\u0006\u001a\u0004\b\u0012\u0010\u0013R\u000e\u0010\u0014\u001a\u00020\u0015X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u000bX\u0082D\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u001b0\u001aX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u001d0\u001aX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u001f0\u001aX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010 \u001a\u0004\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010!\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\"\u001a\b\u0012\u0004\u0012\u00020#0\u001aX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010$\u001a\b\u0012\u0004\u0012\u00020%0\u001aX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010&\u001a\b\u0012\u0004\u0012\u00020\u000f0\'X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010(\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010)\u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010*\u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010+\u001a\u00020\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010,\u001a\u00020-X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010.\u001a\u00020\u0015X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010/\u001a\b\u0012\u0004\u0012\u0002000\u001aX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u00101\u001a\u000202X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u00103\u001a\u000204X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u00105\u001a\u000202X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u00106\u001a\u000207X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u00108\u001a\u000209X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\\"}, d2 = {"Lcom/temi/temiSDK/MainViewModel;", "Landroidx/lifecycle/ViewModel;", "robotController", "Lcom/temi/temiSDK/RobotController;", "context", "Landroid/content/Context;", "(Lcom/temi/temiSDK/RobotController;Landroid/content/Context;)V", "_currentLanguage", "Landroidx/lifecycle/MutableLiveData;", "", "boundary", "", "buffer", "", "currentIndex", "", "currentLanguage", "Landroidx/lifecycle/LiveData;", "getCurrentLanguage", "()Landroidx/lifecycle/LiveData;", "currentState", "Lcom/temi/temiSDK/State;", "currentUserAngle", "currentUserDistance", "defaultAngle", "detectionData", "Lkotlinx/coroutines/flow/StateFlow;", "Lcom/temi/temiSDK/DetectionDataChangedStatus;", "detectionStatus", "Lcom/temi/temiSDK/DetectionStateChangedStatus;", "dragged", "Lcom/temi/temiSDK/Dragged;", "emotion", "int", "lifted", "Lcom/temi/temiSDK/Lifted;", "movementStatus", "Lcom/temi/temiSDK/MovementStatusChangedStatus;", "numberArray", "", "previousLastChoice", "previousUserAngle", "previousUserDistance", "say", "speechState", "Lcom/temi/temiSDK/SpeechState;", "stateMode", "ttsStatus", "Lcom/temi/temiSDK/TtsStatus;", "userRelativeDirection", "Lcom/temi/temiSDK/XDirection;", "xMotion", "Lcom/temi/temiSDK/XMovement;", "xPosition", "yMotion", "Lcom/temi/temiSDK/YMovement;", "yPosition", "Lcom/temi/temiSDK/YDirection;", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "changeLanguage", "language", "Lcom/robotemi/sdk/TtsRequest$Language;", "changeLanguageString", "languageCode", "conditionGate", "trigger", "Lkotlin/Function0;", "", "log", "(Lkotlin/jvm/functions/Function0;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "conditionTimer", "time", "(Lkotlin/jvm/functions/Function0;ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getDirectedAngle", "a1", "a2", "idleMode", "setIdleMode", "isMissuesState", "resultSpeech", "state", "speech", "text", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "speechUi", "textModelChoice", "(IJLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateEmotion", "volumeControl", "volume", "LocaleManager", "app_debug"})
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
    @org.jetbrains.annotations.NotNull
    private final androidx.lifecycle.MutableLiveData<java.lang.String> _currentLanguage = null;
    
    @javax.inject.Inject
    public MainViewModel(@org.jetbrains.annotations.NotNull
    com.temi.temiSDK.RobotController robotController, @dagger.hilt.android.qualifiers.ApplicationContext
    @org.jetbrains.annotations.NotNull
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final androidx.lifecycle.LiveData<java.lang.String> getCurrentLanguage() {
        return null;
    }
    
    public final void changeLanguageString(@org.jetbrains.annotations.NotNull
    java.lang.String languageCode) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b\u00a8\u0006\t"}, d2 = {"Lcom/temi/temiSDK/MainViewModel$LocaleManager;", "", "()V", "setLocale", "", "context", "Landroid/content/Context;", "languageCode", "", "app_debug"})
    public static final class LocaleManager {
        @org.jetbrains.annotations.NotNull
        public static final com.temi.temiSDK.MainViewModel.LocaleManager INSTANCE = null;
        
        private LocaleManager() {
            super();
        }
        
        public final void setLocale(@org.jetbrains.annotations.NotNull
        android.content.Context context, @org.jetbrains.annotations.NotNull
        java.lang.String languageCode) {
        }
    }
}