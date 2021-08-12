import 'dart:convert' as convert;
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:mobpush_plugin/mobpush_plugin.dart';

void main() {
  runApp(MaterialApp(
    home: HomePage(),
  ));
}

class HomePage extends StatefulWidget {
  @override
  _HomePageState createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  String msg = '';
  String rid = '';

  @override
  void initState() {
    super.initState();
    print('initState');
    initMobPush();
  }

  @override
  Widget build(BuildContext context) {
    print('build');
    return Scaffold(
      appBar: AppBar(
        title: Text('push_demos'),
      ),
      body: Center(
        child: Column(
          children: [
            ElevatedButton(
                onPressed: () async {
                  ///获取注册Id
                  Map<String, dynamic> ridMap =
                      await MobpushPlugin.getRegistrationId();
                  String regId = ridMap['res'].toString();
                  print('获取注册id2:registrationId: ' + regId);
                  if (regId.isNotEmpty) {
                    setState(() {
                      rid = regId.toString();
                    });
                  }
                },
                child: Text('获取注册id')),
            Text('msg: $msg'),
            Text('rid: $rid'),
          ],
        ),
      ),
    );
  }

  ///初始化Mob推送配置
  initMobPush() async {
    ///设置隐私协议授权状态
    MobpushPlugin.updatePrivacyPermissionStatus(true);

    if (Platform.isIOS) {
      //设置远程推送环境，向用户授权（仅 iOS）
      MobpushPlugin.setCustomNotification();
      //设置远程推送环境 (仅 iOS)
      MobpushPlugin.setAPNsForProduction(false);
      //设置应用在前台有 Badge、Sound、Alert 三种类型,默认3个选项都有,iOS 10以上设置生效.(仅 iOS)
      MobpushPlugin.setAPNsShowForegroundType(7);
    }

    ///设置推送消息点击监听
    setOnMessageClickCallback((data) {
      print('setOnMessageClickCallback: $data');
      setState(() {
        msg = data.toString();
      });
    });

    ///获取注册Id
    Map<String, dynamic> ridMap = await MobpushPlugin.getRegistrationId();
    String regId = ridMap['res'].toString();
    print('获取注册id2:registrationId: ' + regId);
    if (regId.isNotEmpty) {
      setState(() {
        rid = regId.toString();
      });
    }
    //todo 获取到注册id后,需要通过接口把值传给后端绑定用户名
  }

  ///推送通道,仅android用
  static const channel = const MethodChannel("ljb_mob_push");

  ///设置点击状态栏消息后的回调
  void setOnMessageClickCallback(Function(Map data) fun) async {
    ///ios解析推送消息
    if (Platform.isIOS) {
      //添加推送回调监听（接收自定义透传消息回调、接收通知消息回调、接收点击通知消息回调、接收别名或标签操作回调）
      WidgetsBinding.instance.addPostFrameCallback((timeStamp) {
        MobpushPlugin.addPushReceiver((event) {
          Map data = convert.jsonDecode(event.toString());
          if (data['result']['extrasMap']['schemeData'] != null) {
            Map schemeData =
                convert.jsonDecode(data['result']['extrasMap']['schemeData']);
            int action = data['action'];
            //判断当点击消息时,才调用
            if (action == 2) {
              fun(schemeData);
            }
          }
        }, (error) {
          print('error: $error');
        });
      });

      ///android解析推送消息
    } else if (Platform.isAndroid) {
      channel.setMethodCallHandler((call) async {
        if (call.method == 'mobPushMessageCallback') {
          Map schemeData = call.arguments;
          fun(schemeData);
        }
      });

      //检测是否有最新的推送消息,应用在离线状态拉起的情况下,手动回去一下缓存消息
      await channel.invokeMethod('getLastPushMessage');
    }
  }
}
