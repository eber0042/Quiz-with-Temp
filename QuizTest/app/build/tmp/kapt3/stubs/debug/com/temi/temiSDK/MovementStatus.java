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

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\t\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\t\u00a8\u0006\n"}, d2 = {"Lcom/temi/temiSDK/MovementStatus;", "", "(Ljava/lang/String;I)V", "START", "GOING", "OBSTACLE_DETECTED", "NODE_INACTIVE", "CALCULATING", "COMPLETE", "ABORT", "app_debug"})
public enum MovementStatus {
    /*public static final*/ START /* = new START() */,
    /*public static final*/ GOING /* = new GOING() */,
    /*public static final*/ OBSTACLE_DETECTED /* = new OBSTACLE_DETECTED() */,
    /*public static final*/ NODE_INACTIVE /* = new NODE_INACTIVE() */,
    /*public static final*/ CALCULATING /* = new CALCULATING() */,
    /*public static final*/ COMPLETE /* = new COMPLETE() */,
    /*public static final*/ ABORT /* = new ABORT() */;
    
    MovementStatus() {
    }
    
    @org.jetbrains.annotations.NotNull
    public static kotlin.enums.EnumEntries<com.temi.temiSDK.MovementStatus> getEntries() {
        return null;
    }
}