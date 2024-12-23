package com.temi.temiSDK;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.robotemi.sdk.TtsRequest;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
import java.util.Locale;
import javax.inject.Inject;
import kotlin.math.*;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\n\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\n\u00a8\u0006\u000b"}, d2 = {"Lcom/temi/temiSDK/State;", "", "(Ljava/lang/String;I)V", "TALK", "DISTANCE", "ANGLE", "CONSTRAINT_FOLLOW", "TEST_MOVEMENT", "DETECTION_LOGIC", "TEST", "NULL", "app_debug"})
public enum State {
    /*public static final*/ TALK /* = new TALK() */,
    /*public static final*/ DISTANCE /* = new DISTANCE() */,
    /*public static final*/ ANGLE /* = new ANGLE() */,
    /*public static final*/ CONSTRAINT_FOLLOW /* = new CONSTRAINT_FOLLOW() */,
    /*public static final*/ TEST_MOVEMENT /* = new TEST_MOVEMENT() */,
    /*public static final*/ DETECTION_LOGIC /* = new DETECTION_LOGIC() */,
    /*public static final*/ TEST /* = new TEST() */,
    /*public static final*/ NULL /* = new NULL() */;
    
    State() {
    }
    
    @org.jetbrains.annotations.NotNull
    public static kotlin.enums.EnumEntries<com.temi.temiSDK.State> getEntries() {
        return null;
    }
}