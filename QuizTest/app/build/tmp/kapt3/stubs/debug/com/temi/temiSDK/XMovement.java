package com.temi.temiSDK;

import android.annotation.SuppressLint;
import android.util.Log;
import androidx.lifecycle.ViewModel;
import com.robotemi.sdk.TtsRequest;
import dagger.hilt.android.lifecycle.HiltViewModel;
import javax.inject.Inject;
import kotlin.math.*;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0005\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005\u00a8\u0006\u0006"}, d2 = {"Lcom/temi/temiSDK/XMovement;", "", "(Ljava/lang/String;I)V", "LEFTER", "RIGHTER", "NOWHERE", "app_debug"})
public enum XMovement {
    /*public static final*/ LEFTER /* = new LEFTER() */,
    /*public static final*/ RIGHTER /* = new RIGHTER() */,
    /*public static final*/ NOWHERE /* = new NOWHERE() */;
    
    XMovement() {
    }
    
    @org.jetbrains.annotations.NotNull
    public static kotlin.enums.EnumEntries<com.temi.temiSDK.XMovement> getEntries() {
        return null;
    }
}