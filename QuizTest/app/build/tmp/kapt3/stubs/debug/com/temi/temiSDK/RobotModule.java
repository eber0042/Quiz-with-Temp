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

@dagger.Module
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0007\u00a8\u0006\u0005"}, d2 = {"Lcom/temi/temiSDK/RobotModule;", "", "()V", "provideRobotController", "Lcom/temi/temiSDK/RobotController;", "app_debug"})
@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
public final class RobotModule {
    @org.jetbrains.annotations.NotNull
    public static final com.temi.temiSDK.RobotModule INSTANCE = null;
    
    private RobotModule() {
        super();
    }
    
    @dagger.Provides
    @javax.inject.Singleton
    @org.jetbrains.annotations.NotNull
    public final com.temi.temiSDK.RobotController provideRobotController() {
        return null;
    }
}