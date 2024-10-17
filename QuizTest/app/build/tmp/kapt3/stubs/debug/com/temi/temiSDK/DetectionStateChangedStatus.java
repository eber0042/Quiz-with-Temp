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

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\b\n\u0002\b\b\b\u0086\u0081\u0002\u0018\u0000 \n2\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001\nB\u000f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\t\u00a8\u0006\u000b"}, d2 = {"Lcom/temi/temiSDK/DetectionStateChangedStatus;", "", "state", "", "(Ljava/lang/String;II)V", "getState", "()I", "DETECTED", "LOST", "IDLE", "Companion", "app_debug"})
public enum DetectionStateChangedStatus {
    /*public static final*/ DETECTED /* = new DETECTED(0) */,
    /*public static final*/ LOST /* = new LOST(0) */,
    /*public static final*/ IDLE /* = new IDLE(0) */;
    private final int state = 0;
    @org.jetbrains.annotations.NotNull
    public static final com.temi.temiSDK.DetectionStateChangedStatus.Companion Companion = null;
    
    DetectionStateChangedStatus(int state) {
    }
    
    public final int getState() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull
    public static kotlin.enums.EnumEntries<com.temi.temiSDK.DetectionStateChangedStatus> getEntries() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0005\u001a\u00020\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/temi/temiSDK/DetectionStateChangedStatus$Companion;", "", "()V", "fromState", "Lcom/temi/temiSDK/DetectionStateChangedStatus;", "state", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.Nullable
        public final com.temi.temiSDK.DetectionStateChangedStatus fromState(int state) {
            return null;
        }
    }
}