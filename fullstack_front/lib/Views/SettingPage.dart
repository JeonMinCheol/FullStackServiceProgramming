import 'dart:convert';
import 'dart:io';
import 'dart:typed_data';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:fullstack_front/Configs/Configuration.dart';
import 'package:image_picker/image_picker.dart';
import 'package:provider/provider.dart';
import 'package:stomp_dart_client/stomp.dart';
import 'package:stomp_dart_client/stomp_config.dart';
import 'package:stomp_dart_client/stomp_frame.dart';

import 'FriendsPage.dart';

class Settings extends StatefulWidget{
  @override
  State<StatefulWidget> createState() => _SettingsState();
}

class _SettingsState extends State<Settings>{
  // logout
  Future<bool> _popEvent() async{
    return await showDialog(context: context, builder: (BuildContext context) {
      return AlertDialog(
        actions: [
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.end,
            children: [
              TextButton(onPressed: () async {
                SystemNavigator.pop();
              }, child: const Text("종료")),

              TextButton(onPressed: () {
                Navigator.pop(context, false);
              }, child: const Text("취소"))
            ],
          )
        ],
      );
    });

  }

  @override
  Widget build(BuildContext context) {
    final _dropDownValues = ["한국어", "English", "日本語", "繁體中文", "簡體中文", "Deutsche", "français", "italiana", "español"];

    return WillPopScope(onWillPop: () { return _popEvent(); },
    child : Scaffold(
      appBar:  AppBar(
          shape: (
              const Border(
                  bottom: BorderSide(
                      color : Colors.black12,
                      width:1
                  )
              )
          ),
          title: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const Text("Settings",
                style: TextStyle(fontSize:20, fontWeight: FontWeight.w600),
              ),
            ],
          ),
          automaticallyImplyLeading: false
      ),
      body: Column(
        children: [
          DropdownButton(
            value: context.read<Configuration>().dropDownValues,
            items: _dropDownValues
                .map((e) => DropdownMenuItem(
              value: e, // 선택 시 onChanged 를 통해 반환할 value
              child: Text(e),
            ))
                .toList(),
            onChanged: (value) { // items 의 DropdownMenuItem 의 value 반환
              setState(() {
                if(value! == "한국어")
                  context.read<Configuration>().targetLang = "ko";
                else if(value! == "English")
                  context.read<Configuration>().targetLang = "en";
                else if(value! == "日本語")
                  context.read<Configuration>().targetLang = "ja";
                else if(value! == "繁體中文")
                  context.read<Configuration>().targetLang = "zh-CN";
                else if(value! == "簡體中文")
                  context.read<Configuration>().targetLang = "zh-TW";
                else if(value! == "Deutsche")
                  context.read<Configuration>().targetLang = "de";
                else if(value! == "français")
                  context.read<Configuration>().targetLang = "fr";
                else if(value! == "italiana")
                  context.read<Configuration>().targetLang = "it";
                else if(value! == "español")
                  context.read<Configuration>().targetLang = "es";

                context.read<Configuration>().dropDownValues = value!;
              });
            },
          ),
          TextButton(
          child: Center(child: Text("해당 기기에서 로그아웃 하기",),),
          onPressed: () {
            Configuration().logoutRequest(context.read<Configuration>().token);
            Navigator.pop(context);
            },
          ),
        ],
      )
    )
    );
  }
}