package com.example.push_demos;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;

public class MainActivity extends FlutterActivity {
    private static final String TAG = "MainActivity";
    private MobPushMessagePlugin pushMessagePlugin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handlePushMsg(getIntent());
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        handlePushMsg(intent);
    }

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        pushMessagePlugin = new MobPushMessagePlugin(flutterEngine.getDartExecutor().getBinaryMessenger());
    }


    /**
     * 解析intent,看是否有包含推送消息
     *
     * @param intent
     */
    public void handlePushMsg(Intent intent) {
        if (intent == null) {
            return;
        }
//        Bundle extras1 = intent.getExtras();
//        Set<String> keySet = extras1.keySet();
//        for (String key : keySet) {
//            String value = extras1.getString(key);
//            Log.e(TAG, "handlePushMsg: key,value:" + key + "," + value);
//        }

        //清除本地消息缓存
        LastPushMessageCache.msgs.clear();
        //解析scheme中的数据,目前已测试:小米,华为,鸿蒙,原生Android,oppo
        String schemeData = null;

        //oppo手机
        Uri data = intent.getData();
        if (data != null) {
            schemeData = data.getQueryParameter("schemeData");
        }

        if (TextUtils.isEmpty(schemeData)) {
            Bundle extras = intent.getExtras();
            if (extras != null && extras.containsKey("schemeData")) {
                schemeData = extras.getString("schemeData");
            }
        }

        Log.e(TAG, "handlePushMsg: schemeData:" + schemeData);
        //没有拿到scheme中的数据
        if (TextUtils.isEmpty(schemeData)) {
            return;
        }

        try {
            JSONObject o = new JSONObject(schemeData);
            //遍历scheme中的数据
            Iterator<String> keys = o.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object value = o.get(key);
                LastPushMessageCache.msgs.put(key, value.toString());
            }
            //向flutter发送接收到的推送消息
            if (pushMessagePlugin != null) {
                pushMessagePlugin.sendPushMessage();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
