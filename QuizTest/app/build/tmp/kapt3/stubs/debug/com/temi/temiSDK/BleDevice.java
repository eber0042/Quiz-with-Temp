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
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
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

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u001b\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u000b\u0010\u000f\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u0010\u0010\u001a\u00020\u0005H\u00c6\u0003J\u001f\u0010\u0011\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0015\u001a\u00020\u0016H\u00d6\u0001J\t\u0010\u0017\u001a\u00020\u0018H\u00d6\u0001R\u001c\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000e\u00a8\u0006\u0019"}, d2 = {"Lcom/temi/temiSDK/BleDevice;", "", "device", "Landroid/bluetooth/BluetoothDevice;", "state", "Lcom/temi/temiSDK/ConnectionState;", "(Landroid/bluetooth/BluetoothDevice;Lcom/temi/temiSDK/ConnectionState;)V", "getDevice", "()Landroid/bluetooth/BluetoothDevice;", "setDevice", "(Landroid/bluetooth/BluetoothDevice;)V", "getState", "()Lcom/temi/temiSDK/ConnectionState;", "setState", "(Lcom/temi/temiSDK/ConnectionState;)V", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "", "app_debug"})
public final class BleDevice {
    @org.jetbrains.annotations.Nullable
    private android.bluetooth.BluetoothDevice device;
    @org.jetbrains.annotations.NotNull
    private com.temi.temiSDK.ConnectionState state;
    
    public BleDevice(@org.jetbrains.annotations.Nullable
    android.bluetooth.BluetoothDevice device, @org.jetbrains.annotations.NotNull
    com.temi.temiSDK.ConnectionState state) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable
    public final android.bluetooth.BluetoothDevice getDevice() {
        return null;
    }
    
    public final void setDevice(@org.jetbrains.annotations.Nullable
    android.bluetooth.BluetoothDevice p0) {
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.temi.temiSDK.ConnectionState getState() {
        return null;
    }
    
    public final void setState(@org.jetbrains.annotations.NotNull
    com.temi.temiSDK.ConnectionState p0) {
    }
    
    public BleDevice() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable
    public final android.bluetooth.BluetoothDevice component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.temi.temiSDK.ConnectionState component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.temi.temiSDK.BleDevice copy(@org.jetbrains.annotations.Nullable
    android.bluetooth.BluetoothDevice device, @org.jetbrains.annotations.NotNull
    com.temi.temiSDK.ConnectionState state) {
        return null;
    }
    
    @java.lang.Override
    public boolean equals(@org.jetbrains.annotations.Nullable
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public java.lang.String toString() {
        return null;
    }
}