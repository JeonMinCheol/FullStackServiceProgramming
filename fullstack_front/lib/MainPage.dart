import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:fullstack_front/BtmBar.dart';
import 'package:fullstack_front/Configuration.dart';
import 'package:provider/provider.dart';

class MainPage extends StatefulWidget {
  @override
  State<MainPage> createState() => _MainState();

}

class _MainState extends State<MainPage>{
  Configuration configuration = Configuration();
  Future<bool> _popEvent() async{

    return await showDialog(context: context, builder: (BuildContext context) {
      return AlertDialog(
        actions: [
          TextButton(onPressed: () {
            Dio dio = Dio();
            dio.options.baseUrl=Configuration().baseUrl;
            dio.options.responseType = ResponseType.plain;
            dio.options.validateStatus = (status) {
              return status! < 500;
            };

            dio.options.headers= {"Authorization" : "Bearer " + context.read<Configuration>().token};
            dynamic response = dio.get("/api/auth/logout");

            Navigator.pop(
              context,
              true
            );
          }, child: Text("종료")),

          TextButton(onPressed: () {
            Navigator.pop(context, false);
            }, child: Text("취소"))
        ],
      );
    });

  }

  @override
  Widget build(BuildContext context) {
    return  WillPopScope(child: Scaffold(
      appBar: AppBar(
          shape: (
            Border(
              bottom: BorderSide(
                color : Colors.black12,
                width:1
              )
            )
          ),
          title: Row(
            children: [
              SizedBox.fromSize(size:Size(1,10)),

              Text("Friends",
                style: TextStyle(fontSize:20, fontWeight: FontWeight.w600),
              ),

              SizedBox.fromSize(size:Size(250,10)),

              IconButton(icon: Icon(Icons.add_circle, size:28, weight: 600,), onPressed: () {
                // do something
              }),

              SizedBox.fromSize(size:Size(1,10)),
            ],
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
          ),
          automaticallyImplyLeading: false
      ),

      bottomNavigationBar: BtmBar(),
    ), onWillPop: () {
      return _popEvent();
    });
  }
}