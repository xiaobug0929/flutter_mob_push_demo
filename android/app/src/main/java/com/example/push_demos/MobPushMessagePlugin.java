package com.example.push_demos;

import android.util.Log;

import androidx.annotation.NonNull;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class MobPushMessagePlugin implements MethodChannel.MethodCallHandler {
    private static final String TAG = "MobPushMessagePlugin";
    private final MethodChannel methodChannel;

    public MobPushMessagePlugin(BinaryMessenger messenger) {
        methodChannel = new MethodChannel(messenger, "ljb_mob_push");
        methodChannel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        handleCall(call, result);
    }

    private void handleCall(MethodCall call, @NonNull MethodChannel.Result result) {
        //获取最新的推送消息
        if (call.method.equals("getLastPushMessage")) {
            sendPushMessage();
            result.success(true);
        } else {
            result.notImplemented();
        }
    }

    /**
     * 向flutter发送推送消息
     */
    public void sendPushMessage() {
        Log.e(TAG, "sendPushMessage: " + LastPushMessageCache.msgs);
        if (!LastPushMessageCache.msgs.isEmpty()) {
            methodChannel.invokeMethod("mobPushMessageCallback", LastPushMessageCache.msgs);
        }
    }
}
