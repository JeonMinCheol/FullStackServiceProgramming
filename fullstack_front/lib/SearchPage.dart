import 'dart:developer';

import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:provider/provider.dart';

import 'Configuration.dart';
import 'UserDTO.dart';

class SearchPage extends StatefulWidget{
  const SearchPage({super.key});

  @override
  State<StatefulWidget> createState() => _SearchPageState();
}

class _SearchPageState extends State<SearchPage> {
  TextEditingController searchBartextController = TextEditingController();
  UserDTO? user;
  String stateText = "";

  void findUser(String userName) async {
    Dio dio = Dio();
    dio.options.baseUrl = dotenv.env["BASE_URL"]!;
    dio.options.headers = {
      "Authorization": "Bearer ${context
          .read<Configuration>()
          .token}"
    };

    try{
      dynamic response = await dio.get("/api/user/$userName");
      if(response.statusCode == 202) {
        setState(() => {user = UserDTO.fromJson(response.data)});
      }
    }
    on Exception{
      setState(() {
        user = null;
        stateText = "검색 결과가 없습니다.";
      });
    }
  }

  void makeFriend(String userName) async {
    Dio dio = Dio();
    dio.options.baseUrl = dotenv.env["BASE_URL"]!;
    dio.options.headers = {
      "Authorization": "Bearer ${context
          .read<Configuration>()
          .token}"
    };
      try{
        dynamic status = await dio.post("/api/friend/$userName")..statusCode;

        setState(() {
          if(status == 200) {
            user = null;
            stateText = "친구가 등록되었습니다.";
          }

          if(status == 208) {
            user = null;
            stateText = "이미 등록된 친구입니다.";
          }
        });

      } on Exception {
        setState(() {
          user = null;
        });
      }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
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
            const Text("Search",
              style: TextStyle(fontSize:20, fontWeight: FontWeight.w600),
            ),

            SizedBox.fromSize(size:const Size(200,10)),

            Visibility(
                child: IconButton(icon: const Icon(Icons.add, size:28, weight: 600,), onPressed: () {
                  makeFriend(user!.nickName);
                }),
            )
            ,
          ],
        ),
      ),
      body: Padding(
        padding: const EdgeInsets.all(10),
        child: Column(
          children:[
            SearchBar(
              onSubmitted: (value) => {
                findUser(value),
              },
            ),
            const SizedBox(
                height:20
            ),
            Expanded(child: ListView.builder(
                itemCount: 1,
                itemBuilder:  (context, index) {
                    return (user != null) ? Text(user!.email) : Text(stateText);
                }
            ))
          ]
        ),
      ),
    );
  }
}