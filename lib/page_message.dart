import 'package:flutter/material.dart';

/// 创建日期: 2021/8/4
/// 作者: lijianbin
/// 描述:

class MessagePage extends StatelessWidget {
  final msg;

  const MessagePage({Key key, this.msg = ''}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    print('MessagePage build');
    return Scaffold(
      appBar: AppBar(
        title: Text('消息页面'),
      ),
      body: Center(
        child: Container(
          child: Text(msg),
        ),
      ),
    );
  }
}
