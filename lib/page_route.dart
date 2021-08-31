import 'dart:async';

import 'package:flutter/material.dart';

import 'page_message.dart';

/// 创建日期: 2021/8/19
/// 作者: lijianbin
/// 描述:
class RoutePage extends StatefulWidget {
  @override
  _RoutePageState createState() => _RoutePageState();
}

class _RoutePageState extends State<RoutePage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('RoutePage'),
      ),
      body: Center(
        child: Column(
          children: [
            ElevatedButton(
              onPressed: () {
                Navigator.of(context).push(
                  MaterialPageRoute(
                    builder: (_) => HomePage(),
                    settings: RouteSettings(name: 'home'),
                  ),
                );

                Timer.periodic(Duration(seconds: 3), (_) {
                  print('timer 打开MessagePage');
                  Navigator.of(context).pushAndRemoveUntil(
                    MaterialPageRoute(
                        builder: (_) {
                          return MessagePage(
                            msg: 'message',
                          );
                        },
                        settings: RouteSettings(name: 'message')),
                    (Route<dynamic> route) {
                      return !route.willHandlePopInternally &&
                          route is ModalRoute &&
                          route.settings.name != 'message';
                    },
                  );
                });
              },
              child: Text('打开'),
            )
          ],
        ),
      ),
    );
  }
}

class HomePage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('HomePage'),
      ),
    );
  }
}
