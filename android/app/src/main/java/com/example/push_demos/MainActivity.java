package com.example.push_demos;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.mob.pushsdk.MobPushUtils;

import org.json.JSONArray;
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
        //清除本地消息缓存
        LastPushMessageCache.msgs.clear();
        //解析intent
        JSONArray var2 = MobPushUtils.parseMainPluginPushIntent(intent);
        try {
            if (var2.length() > 0) {
                JSONObject o = var2.getJSONObject(0);
                //遍历并存储新收到的推送消息
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
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
