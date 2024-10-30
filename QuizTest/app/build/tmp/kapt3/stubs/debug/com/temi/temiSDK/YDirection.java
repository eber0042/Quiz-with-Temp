package com.temi.temiSDK;

import android.annotation.SuppressLint;
import android.util.Log;
import androidx.lifecycle.ViewModel;
import com.robotemi.sdk.TtsRequest;
import dagger.hilt.android.lifecycle.HiltViewModel;
import javax.inject.Inject;
import kotlin.math.*;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/temi/temiSDK/YDirection;", "", "(Ljava/lang/String;I)V", "FAR", "MIDRANGE", "CLOSE", "MISSING", "app_debug"})
public enum YDirection {
    /*public static final*/ FAR /* = new FAR() */,
    /*public static final*/ MIDRANGE /* = new MIDRANGE() */,
    /*public static final*/ CLOSE /* = new CLOSE() */,
    /*public static final*/ MISSING /* = new MISSING() */;
    
    YDirection() {
    }
    
    @org.jetbrains.annotations.NotNull
    public static kotlin.enums.EnumEntries<com.temi.temiSDK.YDirection> getEntries() {
        return null;
    }
}