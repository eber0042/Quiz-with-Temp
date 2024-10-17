package com.temi.temiSDK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import androidx.activity.ComponentActivity;
import androidx.compose.animation.ExperimentalAnimationApi;
import androidx.compose.animation.core.RepeatMode;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.material3.ButtonDefaults;
import androidx.compose.material3.ExperimentalMaterial3Api;
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.ContentScale;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontStyle;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.input.PasswordVisualTransformation;
import androidx.compose.ui.text.style.TextAlign;
import coil.decode.GifDecoder;
import coil.decode.ImageDecoderDecoder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dagger.hilt.android.AndroidEntryPoint;
import kotlinx.coroutines.Dispatchers;
import java.util.Locale;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0010\u000b\n\u0002\b\u0018\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B3\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u0012\b\b\u0002\u0010\b\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\tJ\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0007H\u00c6\u0003J7\u0010\u001b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\b\b\u0002\u0010\b\u001a\u00020\u0007H\u00c6\u0001J\u0013\u0010\u001c\u001a\u00020\u00072\b\u0010\u001d\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001e\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u001f\u001a\u00020 H\u00d6\u0001R\u001a\u0010\b\u001a\u00020\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u000b\"\u0004\b\f\u0010\rR \u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\u001a\u0010\u0004\u001a\u00020\u0003X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0012\"\u0004\b\u0016\u0010\u0014\u00a8\u0006!"}, d2 = {"Lcom/temi/temiSDK/ResultForOption;", "", "score", "", "outOf", "isToggleList", "", "", "initialized", "(IILjava/util/List;Z)V", "getInitialized", "()Z", "setInitialized", "(Z)V", "()Ljava/util/List;", "setToggleList", "(Ljava/util/List;)V", "getOutOf", "()I", "setOutOf", "(I)V", "getScore", "setScore", "component1", "component2", "component3", "component4", "copy", "equals", "other", "hashCode", "toString", "", "app_debug"})
public final class ResultForOption {
    private int score;
    private int outOf;
    @org.jetbrains.annotations.NotNull
    private java.util.List<java.lang.Boolean> isToggleList;
    private boolean initialized;
    
    public ResultForOption(int score, int outOf, @org.jetbrains.annotations.NotNull
    java.util.List<java.lang.Boolean> isToggleList, boolean initialized) {
        super();
    }
    
    public final int getScore() {
        return 0;
    }
    
    public final void setScore(int p0) {
    }
    
    public final int getOutOf() {
        return 0;
    }
    
    public final void setOutOf(int p0) {
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<java.lang.Boolean> isToggleList() {
        return null;
    }
    
    public final void setToggleList(@org.jetbrains.annotations.NotNull
    java.util.List<java.lang.Boolean> p0) {
    }
    
    public final boolean getInitialized() {
        return false;
    }
    
    public final void setInitialized(boolean p0) {
    }
    
    public ResultForOption() {
        super();
    }
    
    public final int component1() {
        return 0;
    }
    
    public final int component2() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<java.lang.Boolean> component3() {
        return null;
    }
    
    public final boolean component4() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.temi.temiSDK.ResultForOption copy(int score, int outOf, @org.jetbrains.annotations.NotNull
    java.util.List<java.lang.Boolean> isToggleList, boolean initialized) {
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