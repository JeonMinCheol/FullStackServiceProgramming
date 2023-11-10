import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:provider/provider.dart';

import 'Configuration.dart';
import 'UserDTO.dart';

class SearchPage extends StatefulWidget{
  @override
  State<StatefulWidget> createState() => _SearchPageState();
}

class _SearchPageState extends State<SearchPage> {
  TextEditingController searchBartextController = TextEditingController();
  UserDTO? user;

  void findUser() async {
    //

    Dio dio = Dio();
    dio.options.baseUrl = dotenv.env["BASE_URL"]!;
    dio.options.headers = {
      "Authorization": "Bearer ${context
          .read<Configuration>()
          .token}"
    };

    final response = await dio.get("/api/user/${searchBartextController.text}");

    if(response.statusCode == 202) {
      dynamic json = response.data;
      setState(() {
        user = UserDTO.fromJson(json);
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

            SizedBox.fromSize(size:Size(200,10)),

            IconButton(icon: const Icon(Icons.add, size:28, weight: 600,), onPressed: () {
              // TODO :
            }),
          ],
        ),
      ),
      body: Padding(
        padding: const EdgeInsets.all(10),
        child: Column(
          children:[
            SearchBar(
              onSubmitted: (value) => findUser(),
            ),
            const SizedBox(
                height:20
            ),
            Expanded(child: ListView.builder(
                itemCount: 1,
                itemBuilder: (context, index) => (user != null) ? Text(user!.nickName) : Text("검색 결과가 존재하지 않습니다.")
            ))
          ]
        ),
      ),
    );
  }
}