import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:fullstack_front/Configuration.dart';
import 'package:provider/provider.dart';

class Settings extends StatefulWidget{
  @override
  State<StatefulWidget> createState() => _SettingsState();
}

class _SettingsState extends State<Settings>{
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body:TextButton(
        child: Center(child: Text("logout"),),
        onPressed: () {Configuration().logoutRequest(context.read<Configuration>().token);},
      )
    );
  }
  
}