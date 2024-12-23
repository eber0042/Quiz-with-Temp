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

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0007\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007\u00a8\u0006\b"}, d2 = {"Lcom/temi/temiSDK/SpeechState;", "", "(Ljava/lang/String;I)V", "SCOREBOARD", "UNANSWERED", "EXIT_EARLY", "THANKYOU", "QUIZ", "app_debug"})
public enum SpeechState {
    /*public static final*/ SCOREBOARD /* = new SCOREBOARD() */,
    /*public static final*/ UNANSWERED /* = new UNANSWERED() */,
    /*public static final*/ EXIT_EARLY /* = new EXIT_EARLY() */,
    /*public static final*/ THANKYOU /* = new THANKYOU() */,
    /*public static final*/ QUIZ /* = new QUIZ() */;
    
    SpeechState() {
    }
    
    @org.jetbrains.annotations.NotNull
    public static kotlin.enums.EnumEntries<com.temi.temiSDK.SpeechState> getEntries() {
        return null;
    }
}