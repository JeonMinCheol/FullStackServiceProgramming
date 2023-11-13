import 'dart:math';

import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:fullstack_front/BtmBar.dart';
import 'package:fullstack_front/Configuration.dart';
import 'package:provider/provider.dart';

import 'SearchPage.dart';
import 'FriendDTO.dart';

class MainPage extends StatefulWidget{
  const MainPage({super.key});

  @override
  State<MainPage> createState() => _MainState();
}

class _MainState extends State<MainPage> with WidgetsBindingObserver, RouteAware{
  late String token;
  List<FriendDTO>? friendList;

  @override
  void didChangeDependencies() {
      super.didChangeDependencies();
      _getFriendList();
  }


  // friendList
  void _getFriendList() async {
    // showDialog(
    //     context: context,
    //     builder: (context) => const Center(child:CircularProgressIndicator()));

    print("getFriendList");

    Dio dio = Dio();
    dio.options.baseUrl = dotenv.env["BASE_URL"]!;
    dio.options.headers = {
      "Authorization": "Bearer ${context
          .read<Configuration>()
          .token}"
    };

    final response = await dio.get("/api/friend");

    if(response.statusCode == 202) {
      List<dynamic> jsonList = response.data;
      setState(() {
        if(jsonList.isNotEmpty) {
          friendList = jsonList.map((json) => FriendDTO.fromJson(json)).toList();
        }
      });
    }
  }

  void _moveToFindPage() {
    Navigator.push(context, MaterialPageRoute(builder: (context) => const SearchPage())).then((value) => {
      setState(() {_getFriendList();})
    });
  }

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
  void initState() {
    super.initState();
    token = context.read<Configuration>().token;
    _getFriendList();
  }

  @override
  void dispose() {
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    // ignore: deprecated_member_use
    return WillPopScope(child: Scaffold(
          appBar: AppBar(
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
                  SizedBox.fromSize(size:const Size(1,10)),

                  const Text("Friends",
                    style: TextStyle(fontSize:20, fontWeight: FontWeight.w600),
                  ),

                  SizedBox.fromSize(size:const Size(250,10)),

                  IconButton(icon: const Icon(Icons.account_box, size:28, weight: 600,), onPressed: () {
                    _moveToFindPage();
                  }),

                  SizedBox.fromSize(size:const Size(1,10)),
                ],
              ),
              automaticallyImplyLeading: false
          ),

          body: ListView.builder(
            itemCount: (friendList == null) ? 1 : friendList!.length,
            itemBuilder: (BuildContext context, int index) {
              return Card(
                child: ListTile(
                  leading: FlutterLogo(size: 56.0),
                  title: (friendList == null) ? Text("생성된 대화방이 없습니다.") : Text(friendList![index].nickName),
                  subtitle: (friendList == null) ? Text("생성된 대화방이 없습니다.") : Text('Here is a second line'),
                  trailing: Icon(Icons.more_vert),
                ),
              );
            },
          ),

          bottomNavigationBar: const BtmBar(),
        ), onWillPop: () {
          return _popEvent();
        });

  }
}

