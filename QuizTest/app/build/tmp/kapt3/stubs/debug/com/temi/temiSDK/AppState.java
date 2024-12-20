package com.temi.temiSDK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import androidx.activity.ComponentActivity;
import androidx.compose.animation.ExperimentalAnimationApi;
import androidx.compose.animation.core.RepeatMode;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.material3.ButtonDefaults;
import androidx.compose.material3.ExperimentalMaterial3Api;
import androidx.compose.runtime.*;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.ContentScale;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontStyle;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.input.PasswordVisualTransformation;
import androidx.compose.ui.text.style.TextAlign;
import androidx.core.app.ActivityCompat;
import com.google.accompanist.permissions.ExperimentalPermissionsApi;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dagger.hilt.android.AndroidEntryPoint;
import java.util.Locale;
import java.util.UUID;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.annotation.RequiresPermission;
import com.robotemi.sdk.TtsRequest;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\u0005\u0003\u0004\u0005\u0006\u0007B\u0007\b\u0004\u00a2\u0006\u0002\u0010\u0002\u0082\u0001\u0005\b\t\n\u000b\f\u00a8\u0006\r"}, d2 = {"Lcom/temi/temiSDK/AppState;", "", "()V", "Bluetooth", "Quiz", "QuizHome", "ScoreBoard", "Test", "Lcom/temi/temiSDK/AppState$Bluetooth;", "Lcom/temi/temiSDK/AppState$Quiz;", "Lcom/temi/temiSDK/AppState$QuizHome;", "Lcom/temi/temiSDK/AppState$ScoreBoard;", "Lcom/temi/temiSDK/AppState$Test;", "app_debug"})
public abstract class AppState {
    
    private AppState() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/temi/temiSDK/AppState$Bluetooth;", "Lcom/temi/temiSDK/AppState;", "()V", "app_debug"})
    public static final class Bluetooth extends com.temi.temiSDK.AppState {
        @org.jetbrains.annotations.NotNull
        public static final com.temi.temiSDK.AppState.Bluetooth INSTANCE = null;
        
        private Bluetooth() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/temi/temiSDK/AppState$Quiz;", "Lcom/temi/temiSDK/AppState;", "()V", "app_debug"})
    public static final class Quiz extends com.temi.temiSDK.AppState {
        @org.jetbrains.annotations.NotNull
        public static final com.temi.temiSDK.AppState.Quiz INSTANCE = null;
        
        private Quiz() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/temi/temiSDK/AppState$QuizHome;", "Lcom/temi/temiSDK/AppState;", "()V", "app_debug"})
    public static final class QuizHome extends com.temi.temiSDK.AppState {
        @org.jetbrains.annotations.NotNull
        public static final com.temi.temiSDK.AppState.QuizHome INSTANCE = null;
        
        private QuizHome() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/temi/temiSDK/AppState$ScoreBoard;", "Lcom/temi/temiSDK/AppState;", "()V", "app_debug"})
    public static final class ScoreBoard extends com.temi.temiSDK.AppState {
        @org.jetbrains.annotations.NotNull
        public static final com.temi.temiSDK.AppState.ScoreBoard INSTANCE = null;
        
        private ScoreBoard() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/temi/temiSDK/AppState$Test;", "Lcom/temi/temiSDK/AppState;", "()V", "app_debug"})
    public static final class Test extends com.temi.temiSDK.AppState {
        @org.jetbrains.annotations.NotNull
        public static final com.temi.temiSDK.AppState.Test INSTANCE = null;
        
        private Test() {
        }
    }
}