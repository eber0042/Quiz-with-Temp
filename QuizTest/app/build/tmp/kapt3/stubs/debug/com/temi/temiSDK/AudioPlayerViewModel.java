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
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.RequiresPermission;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.robotemi.sdk.TtsRequest;
import java.io.IOException;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\r\u001a\u0004\u0018\u00010\u0004J\b\u0010\u000e\u001a\u0004\u0018\u00010\u0004J\b\u0010\u000f\u001a\u0004\u0018\u00010\u0004J\b\u0010\u0010\u001a\u0004\u0018\u00010\u0004J\b\u0010\u0011\u001a\u0004\u0018\u00010\u0004J\b\u0010\u0012\u001a\u0004\u0018\u00010\u0004J\b\u0010\u0013\u001a\u0004\u0018\u00010\u0004J\b\u0010\u0014\u001a\u0004\u0018\u00010\u0004J\b\u0010\u0015\u001a\u0004\u0018\u00010\u0004J\u000e\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0019R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000b\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001a"}, d2 = {"Lcom/temi/temiSDK/AudioPlayerViewModel;", "Landroidx/lifecycle/ViewModel;", "()V", "audioPlayer", "Lcom/temi/temiSDK/AudioPlayer;", "audioPlayer1", "audioPlayer2", "audioPlayer3", "audioPlayer4", "audioPlayer5", "audioPlayer6", "audioPlayer7", "audioPlayer8", "getAudioPlayer", "getAudioPlayer1", "getAudioPlayer2", "getAudioPlayer3", "getAudioPlayer4", "getAudioPlayer5", "getAudioPlayer6", "getAudioPlayer7", "getAudioPlayer8", "initAudioPlayers", "", "context", "Landroid/content/Context;", "app_debug"})
public final class AudioPlayerViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.Nullable
    private com.temi.temiSDK.AudioPlayer audioPlayer;
    @org.jetbrains.annotations.Nullable
    private com.temi.temiSDK.AudioPlayer audioPlayer1;
    @org.jetbrains.annotations.Nullable
    private com.temi.temiSDK.AudioPlayer audioPlayer2;
    @org.jetbrains.annotations.Nullable
    private com.temi.temiSDK.AudioPlayer audioPlayer3;
    @org.jetbrains.annotations.Nullable
    private com.temi.temiSDK.AudioPlayer audioPlayer4;
    @org.jetbrains.annotations.Nullable
    private com.temi.temiSDK.AudioPlayer audioPlayer5;
    @org.jetbrains.annotations.Nullable
    private com.temi.temiSDK.AudioPlayer audioPlayer6;
    @org.jetbrains.annotations.Nullable
    private com.temi.temiSDK.AudioPlayer audioPlayer7;
    @org.jetbrains.annotations.Nullable
    private com.temi.temiSDK.AudioPlayer audioPlayer8;
    
    public AudioPlayerViewModel() {
        super();
    }
    
    public final void initAudioPlayers(@org.jetbrains.annotations.NotNull
    android.content.Context context) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final com.temi.temiSDK.AudioPlayer getAudioPlayer() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final com.temi.temiSDK.AudioPlayer getAudioPlayer1() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final com.temi.temiSDK.AudioPlayer getAudioPlayer2() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final com.temi.temiSDK.AudioPlayer getAudioPlayer3() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final com.temi.temiSDK.AudioPlayer getAudioPlayer4() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final com.temi.temiSDK.AudioPlayer getAudioPlayer5() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final com.temi.temiSDK.AudioPlayer getAudioPlayer6() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final com.temi.temiSDK.AudioPlayer getAudioPlayer7() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final com.temi.temiSDK.AudioPlayer getAudioPlayer8() {
        return null;
    }
}