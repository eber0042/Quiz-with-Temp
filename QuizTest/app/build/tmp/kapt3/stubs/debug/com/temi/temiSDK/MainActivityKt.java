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
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.robotemi.sdk.TtsRequest;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\u008c\u0001\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u001f\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a\b\u0010\u0000\u001a\u00020\u0001H\u0007\u001aR\u0010\u0002\u001a\u00020\u00012\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00010\u00042\u0006\u0010\u0005\u001a\u00020\u00062\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00062\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\t2\b\b\u0002\u0010\u000b\u001a\u00020\t2\b\b\u0002\u0010\f\u001a\u00020\rH\u0007\u001a(\u0010\u000e\u001a\u00020\u00012\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00010\u00042\u0006\u0010\u000f\u001a\u00020\u00102\b\b\u0002\u0010\f\u001a\u00020\rH\u0007\u001a\b\u0010\u0011\u001a\u00020\u0001H\u0007\u001a\u0010\u0010\u0012\u001a\u00020\u00012\u0006\u0010\u0013\u001a\u00020\u0014H\u0007\u001a4\u0010\u0015\u001a\u00020\u00012\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00180\u00172\b\b\u0002\u0010\u0019\u001a\u00020\u001a2\b\b\u0002\u0010\u001b\u001a\u00020\t2\b\b\u0002\u0010\f\u001a\u00020\rH\u0007\u001a\b\u0010\u001c\u001a\u00020\u0001H\u0007\u001a\u0010\u0010\u001d\u001a\u00020\u00012\u0006\u0010\u001e\u001a\u00020\u001fH\u0007\u001a\u0093\u0001\u0010 \u001a\u00020\u00012\u000e\b\u0002\u0010!\u001a\b\u0012\u0004\u0012\u00020\"0\u00172\b\b\u0002\u0010#\u001a\u00020\t2\b\b\u0002\u0010$\u001a\u00020\t2\b\b\u0002\u0010%\u001a\u00020\t2\b\b\u0002\u0010&\u001a\u00020\u00182\f\u0010\'\u001a\b\u0012\u0004\u0012\u00020\t0\u00042\b\b\u0002\u0010(\u001a\u00020\u00062\u0012\u0010)\u001a\u000e\u0012\u0004\u0012\u00020+\u0012\u0004\u0012\u00020\u00010*2\u0012\u0010,\u001a\u000e\u0012\u0004\u0012\u00020-\u0012\u0004\u0012\u00020\u00010*2\u0006\u0010.\u001a\u00020-2\u0006\u0010/\u001a\u000200H\u0007\u00f8\u0001\u0000\u001a\u00ae\u0001\u00101\u001a\u00020\u00012\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u000f\u001a\u00020\u00102\b\b\u0002\u00102\u001a\u00020\u00102\b\b\u0002\u00103\u001a\u00020\t2\b\b\u0002\u00104\u001a\u00020\t2\f\u0010\'\u001a\b\u0012\u0004\u0012\u00020\t0\u00042\u000e\b\u0002\u0010!\u001a\b\u0012\u0004\u0012\u00020\"0\u00172\b\b\u0002\u0010#\u001a\u00020\t2\b\b\u0002\u0010$\u001a\u00020\t2\b\b\u0002\u0010%\u001a\u00020\t2\b\b\u0002\u0010&\u001a\u00020\u00182\b\b\u0002\u0010(\u001a\u00020\u00062\u0012\u0010,\u001a\u000e\u0012\u0004\u0012\u00020-\u0012\u0004\u0012\u00020\u00010*2\u0006\u0010.\u001a\u00020-2\u0006\u0010/\u001a\u000200H\u0007\u001a\u0010\u00105\u001a\u00020\u00012\u0006\u00106\u001a\u00020\tH\u0007\u001aF\u00107\u001a\u00020\u00012\u0006\u0010/\u001a\u0002002\u0018\u00108\u001a\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u0002090\u0017\u0012\u0004\u0012\u00020\u00010*2\f\u0010:\u001a\b\u0012\u0004\u0012\u00020\u00010\u00042\f\u0010;\u001a\b\u0012\u0004\u0012\u00020\u00010\u0004H\u0007\u001a\u0010\u0010<\u001a\u00020\u00012\u0006\u0010/\u001a\u000200H\u0007\u001a&\u0010=\u001a\u00020\u00012\f\u0010;\u001a\b\u0012\u0004\u0012\u00020\u00010\u00042\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010/\u001a\u000200H\u0007\u001a\b\u0010>\u001a\u00020\u0001H\u0007\u001a\u0010\u0010?\u001a\u00020\u00012\u0006\u0010/\u001a\u000200H\u0007\u001aL\u0010@\u001a\u00020\u00012\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010A\u001a\u00020\u00182\b\b\u0002\u0010B\u001a\u00020\u00062\b\b\u0002\u0010C\u001a\u00020\t2\b\b\u0002\u0010D\u001a\u00020\t2\b\b\u0002\u0010E\u001a\u00020\u00062\u0006\u0010\f\u001a\u00020\rH\u0007\u001a\u0018\u0010F\u001a\u00020\t2\u0006\u0010G\u001a\u00020\u00062\b\b\u0002\u0010H\u001a\u00020\t\u001a\u0018\u0010I\u001a\u00020\u00182\u0006\u0010G\u001a\u00020\u00062\b\b\u0002\u0010H\u001a\u00020\u0018\u001a\u001a\u0010J\u001a\u00020\u00182\u0006\u0010G\u001a\u00020\u00062\b\b\u0002\u0010H\u001a\u00020\u0018H\u0007\u001a\u0015\u0010K\u001a\u0004\u0018\u00010\u00182\u0006\u0010G\u001a\u00020\u0006\u00a2\u0006\u0002\u0010L\u001a\u0015\u0010M\u001a\u0004\u0018\u00010\u00182\u0006\u0010G\u001a\u00020\u0006\u00a2\u0006\u0002\u0010L\u001a\"\u0010N\u001a\b\u0012\u0004\u0012\u00020\"0\u00172\f\u0010O\u001a\b\u0012\u0004\u0012\u00020\u00060\u00172\u0006\u0010G\u001a\u00020\u0018\u001a\u0010\u0010P\u001a\u0004\u0018\u00010\u00062\u0006\u0010G\u001a\u00020\u0006\u001a\u001c\u0010Q\u001a\b\u0012\u0004\u0012\u00020\u00060\u00172\u0006\u0010/\u001a\u0002002\u0006\u0010R\u001a\u00020\u0018\u001a\u0010\u0010S\u001a\u0004\u0018\u00010\u00062\u0006\u0010G\u001a\u00020\u0006\u001a\u000e\u0010T\u001a\u00020\u00062\u0006\u0010/\u001a\u000200\u001a\u0014\u0010U\u001a\b\u0012\u0004\u0012\u00020\u00060\u00172\u0006\u0010/\u001a\u000200\u001a\u0016\u0010V\u001a\u00020+2\u0006\u0010B\u001a\u00020\u0006\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010W\u001a\u000e\u0010X\u001a\u00020Y2\u0006\u0010B\u001a\u00020\u0006\u001a\u0014\u0010Z\u001a\b\u0012\u0004\u0012\u00020\u00060\u00172\u0006\u0010[\u001a\u00020\\\u001a\u0014\u0010]\u001a\b\u0012\u0004\u0012\u00020\u00060\u00172\u0006\u0010/\u001a\u000200\u001a\u000e\u0010^\u001a\u00020\u00012\u0006\u0010/\u001a\u000200\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006_"}, d2 = {"BluetoothScreen", "", "BoxWithClickable", "onClick", "Lkotlin/Function0;", "text", "", "type", "grow", "", "animatedPress", "cycleColor", "modifier", "Landroidx/compose/ui/Modifier;", "ButtonIconShrinkGrow", "image", "Landroidx/compose/ui/graphics/painter/Painter;", "CircularProgressBar", "DropdownMenuSample", "audioPlayer", "Lcom/temi/temiSDK/AudioPlayer;", "ImageCycler", "imageResIds", "", "", "durationMillis", "", "fadeTrue", "ImageDisplayApp", "LinearProgressBar", "progress", "", "MultipleChoiceOption", "texts", "Lcom/temi/temiSDK/question;", "singleOn", "onCorrectOn", "surveyOn", "columnLength", "onSubmitted", "scoreType", "onConditionChange", "Lkotlin/Function1;", "Landroidx/compose/ui/graphics/Color;", "onResult", "Lcom/temi/temiSDK/ResultForOption;", "getResult", "context", "Landroid/content/Context;", "MultipleChoiceQuestion", "background", "textQuestion", "imageQuestion", "PasswordPromptBox", "clear", "Quiz", "save", "Lcom/temi/temiSDK/QuizQuestion;", "onShowOverlay", "onStartClicked", "QuizApp", "QuizHome", "ScoreBoard", "Test", "TextHeading", "size", "weight", "italicTrue", "endTrue", "color", "extractBooleanFromInput", "input", "defaultValue", "extractIntFromInput", "extractPainterResourceFromInput", "extractPoolingNumberFromInput", "(Ljava/lang/String;)Ljava/lang/Integer;", "extractPriorityPoolingNumberFromInput", "extractQuestionsFromInput", "user", "extractScoreTypeFromInput", "extractTXT", "ID", "extractTextFromInput", "getAllPreferences", "getBlacklist", "getColor", "(Ljava/lang/String;)J", "getFontWeight", "Landroidx/compose/ui/text/font/FontWeight;", "getInstalledTemiApps", "packageManager", "Landroid/content/pm/PackageManager;", "getWhitelist", "sendPreferencesByEmail", "app_debug"})
public final class MainActivityKt {
    
    @org.jetbrains.annotations.NotNull
    public static final java.util.List<java.lang.String> getInstalledTemiApps(@org.jetbrains.annotations.NotNull
    android.content.pm.PackageManager packageManager) {
        return null;
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.animation.ExperimentalAnimationApi.class})
    @androidx.compose.runtime.Composable
    public static final void ImageCycler(@org.jetbrains.annotations.NotNull
    java.util.List<java.lang.Integer> imageResIds, long durationMillis, boolean fadeTrue, @org.jetbrains.annotations.NotNull
    androidx.compose.ui.Modifier modifier) {
    }
    
    @org.jetbrains.annotations.NotNull
    public static final java.util.List<java.lang.String> getBlacklist(@org.jetbrains.annotations.NotNull
    android.content.Context context) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public static final java.util.List<java.lang.String> getWhitelist(@org.jetbrains.annotations.NotNull
    android.content.Context context) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public static final java.util.List<java.lang.String> extractTXT(@org.jetbrains.annotations.NotNull
    android.content.Context context, int ID) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public static final java.util.List<com.temi.temiSDK.question> extractQuestionsFromInput(@org.jetbrains.annotations.NotNull
    java.util.List<java.lang.String> user, int input) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public static final java.lang.String extractTextFromInput(@org.jetbrains.annotations.NotNull
    java.lang.String input) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public static final java.lang.String extractScoreTypeFromInput(@org.jetbrains.annotations.NotNull
    java.lang.String input) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public static final java.lang.Integer extractPoolingNumberFromInput(@org.jetbrains.annotations.NotNull
    java.lang.String input) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public static final java.lang.Integer extractPriorityPoolingNumberFromInput(@org.jetbrains.annotations.NotNull
    java.lang.String input) {
        return null;
    }
    
    public static final boolean extractBooleanFromInput(@org.jetbrains.annotations.NotNull
    java.lang.String input, boolean defaultValue) {
        return false;
    }
    
    public static final int extractIntFromInput(@org.jetbrains.annotations.NotNull
    java.lang.String input, int defaultValue) {
        return 0;
    }
    
    @androidx.compose.runtime.Composable
    public static final int extractPainterResourceFromInput(@org.jetbrains.annotations.NotNull
    java.lang.String input, int defaultValue) {
        return 0;
    }
    
    @androidx.compose.runtime.Composable
    public static final void PasswordPromptBox(boolean clear) {
    }
    
    public static final void sendPreferencesByEmail(@org.jetbrains.annotations.NotNull
    android.content.Context context) {
    }
    
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String getAllPreferences(@org.jetbrains.annotations.NotNull
    android.content.Context context) {
        return null;
    }
    
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    @kotlin.OptIn(markerClass = {com.google.accompanist.permissions.ExperimentalPermissionsApi.class})
    @androidx.compose.runtime.Composable
    public static final void BluetoothScreen() {
    }
    
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    @kotlin.OptIn(markerClass = {com.google.accompanist.permissions.ExperimentalPermissionsApi.class})
    @androidx.compose.runtime.Composable
    public static final void QuizApp(@org.jetbrains.annotations.NotNull
    android.content.Context context) {
    }
    
    @androidx.compose.runtime.Composable
    public static final void Test(@org.jetbrains.annotations.NotNull
    android.content.Context context) {
    }
    
    @android.annotation.SuppressLint(value = {"StringFormatMatches"})
    @androidx.compose.runtime.Composable
    public static final void ScoreBoard() {
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable
    public static final void QuizHome(@org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function0<kotlin.Unit> onStartClicked, @org.jetbrains.annotations.NotNull
    com.temi.temiSDK.AudioPlayer audioPlayer, @org.jetbrains.annotations.NotNull
    android.content.Context context) {
    }
    
    @androidx.compose.runtime.Composable
    public static final void ImageDisplayApp() {
    }
    
    @android.annotation.SuppressLint(value = {"UnrememberedMutableState"})
    @androidx.compose.runtime.Composable
    public static final void Quiz(@org.jetbrains.annotations.NotNull
    android.content.Context context, @org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function1<? super java.util.List<com.temi.temiSDK.QuizQuestion>, kotlin.Unit> save, @org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function0<kotlin.Unit> onShowOverlay, @org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function0<kotlin.Unit> onStartClicked) {
    }
    
    @androidx.compose.runtime.Composable
    public static final void MultipleChoiceQuestion(@org.jetbrains.annotations.NotNull
    java.lang.String text, @org.jetbrains.annotations.NotNull
    androidx.compose.ui.graphics.painter.Painter image, @org.jetbrains.annotations.NotNull
    androidx.compose.ui.graphics.painter.Painter background, boolean textQuestion, boolean imageQuestion, @org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function0<java.lang.Boolean> onSubmitted, @org.jetbrains.annotations.NotNull
    java.util.List<com.temi.temiSDK.question> texts, boolean singleOn, boolean onCorrectOn, boolean surveyOn, int columnLength, @org.jetbrains.annotations.NotNull
    java.lang.String scoreType, @org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function1<? super com.temi.temiSDK.ResultForOption, kotlin.Unit> onResult, @org.jetbrains.annotations.NotNull
    com.temi.temiSDK.ResultForOption getResult, @org.jetbrains.annotations.NotNull
    android.content.Context context) {
    }
    
    @androidx.compose.runtime.Composable
    public static final void MultipleChoiceOption(@org.jetbrains.annotations.NotNull
    java.util.List<com.temi.temiSDK.question> texts, boolean singleOn, boolean onCorrectOn, boolean surveyOn, int columnLength, @org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function0<java.lang.Boolean> onSubmitted, @org.jetbrains.annotations.NotNull
    java.lang.String scoreType, @org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function1<? super androidx.compose.ui.graphics.Color, kotlin.Unit> onConditionChange, @org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function1<? super com.temi.temiSDK.ResultForOption, kotlin.Unit> onResult, @org.jetbrains.annotations.NotNull
    com.temi.temiSDK.ResultForOption getResult, @org.jetbrains.annotations.NotNull
    android.content.Context context) {
    }
    
    @androidx.compose.runtime.Composable
    public static final void TextHeading(@org.jetbrains.annotations.NotNull
    java.lang.String text, int size, @org.jetbrains.annotations.NotNull
    java.lang.String weight, boolean italicTrue, boolean endTrue, @org.jetbrains.annotations.NotNull
    java.lang.String color, @org.jetbrains.annotations.NotNull
    androidx.compose.ui.Modifier modifier) {
    }
    
    public static final long getColor(@org.jetbrains.annotations.NotNull
    java.lang.String weight) {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull
    public static final androidx.compose.ui.text.font.FontWeight getFontWeight(@org.jetbrains.annotations.NotNull
    java.lang.String weight) {
        return null;
    }
    
    @androidx.compose.runtime.Composable
    public static final void LinearProgressBar(float progress) {
    }
    
    @androidx.compose.runtime.Composable
    public static final void CircularProgressBar() {
    }
    
    @androidx.compose.runtime.Composable
    public static final void DropdownMenuSample(@org.jetbrains.annotations.NotNull
    com.temi.temiSDK.AudioPlayer audioPlayer) {
    }
    
    @androidx.compose.runtime.Composable
    public static final void ButtonIconShrinkGrow(@org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function0<kotlin.Unit> onClick, @org.jetbrains.annotations.NotNull
    androidx.compose.ui.graphics.painter.Painter image, @org.jetbrains.annotations.NotNull
    androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable
    public static final void BoxWithClickable(@org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function0<kotlin.Unit> onClick, @org.jetbrains.annotations.NotNull
    java.lang.String text, @org.jetbrains.annotations.Nullable
    java.lang.String type, boolean grow, boolean animatedPress, boolean cycleColor, @org.jetbrains.annotations.NotNull
    androidx.compose.ui.Modifier modifier) {
    }
}