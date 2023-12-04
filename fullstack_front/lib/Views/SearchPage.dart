import 'dart:developer';
import 'dart:io';

import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:path_provider/path_provider.dart';
import 'package:provider/provider.dart';

import '../Configs/Configuration.dart';
import '../Models/UserDTO.dart';

class SearchPage extends StatefulWidget{
  const SearchPage({super.key});

  @override
  State<StatefulWidget> createState() => _SearchPageState();
}

class _SearchPageState extends State<SearchPage> {
  TextEditingController searchBartextController = TextEditingController();
  UserDTO? user;
  late String dirPath = "NOT_INITIALIZED";
  String stateText = "";


  @override
  void initState() {
    super.initState();
    Future<Directory> documentDirectory = getApplicationSupportDirectory();
    documentDirectory.then((value) => dirPath = value.path);
  }

  // asset 폴더에 이미지 저장
  Future<void> saveImage(List<int> imageSource, String name) async {
    try {
      // 애플리케이션 디렉터리에 저장할 파일 경로 생성
      // 경로: /data/user/0/com.example.fullstack_front/files/filename
      String filePath = "$dirPath/$name.jpg";

      // 파일에 이미지 데이터 쓰기
      File file = File(filePath);

      if(!file.existsSync()) {
        File(filePath).createSync(recursive: true);
      }

      file.writeAsBytes(imageSource);
      print('Image saved to: $filePath');
    } catch (e) {
      print('Error saving image: $e');
    }
  }

  Future<void> _getImage(String path, String name) async {
    Dio dio = Dio();
    dio.options.baseUrl = dotenv.env["BASE_URL"]!;
    dio.options.headers = {
      "Authorization": "Bearer ${context
          .read<Configuration>()
          .token}"
    };
    dio.options.responseType = ResponseType.bytes;

    Response response = await dio.get("/api/image$path");

    if(response.statusCode == 200) {

      await saveImage(response.data, name);
    }
  }

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
        setState(() => {
          user = UserDTO.fromJson(response.data),
          _getImage(user!.path.replaceAll("\\", "/"), user!.nickName)
        });
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
        Response response = await dio.post("/api/friend/$userName");

        setState(() {
          if(response.statusCode == 200) {
            user = null;
            stateText = "친구가 등록되었습니다.";
          }
        });
      } on Exception {
        setState(() {
          user = null;
          stateText = "이미 등록된 친구입니다.";
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
            Padding(
              padding: EdgeInsets.only(left: 8,right: 8),
              child: TextField(
                onSubmitted: (value) => {
                  findUser(value),
                },
                decoration: InputDecoration(
                  hintText: "Search...",
                  hintStyle: TextStyle(color: Colors.grey.shade600),
                  prefixIcon: Icon(Icons.search,color: Colors.grey.shade600, size: 20,),
                  filled: true,
                  fillColor: Colors.white,
                  contentPadding: EdgeInsets.symmetric(horizontal: 8),
                  enabledBorder: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(20),
                      borderSide: BorderSide(
                          color: Colors.grey.shade100
                      )
                  ),
                ),
              ),
            ),

            const SizedBox(
                height:20
            ),
            (dirPath != "NOT_INITIALIZED") ?
            Expanded(child: ListView.builder(
                itemCount: 1,
                itemBuilder:  (context, index) {
                    return (user != null) ? ListTile(
                      leading: ClipRRect(
                        child: SizedBox(child: Image.file(File('$dirPath/${user!.nickName}.jpg'),fit:BoxFit.fill), height:56, width: 56,),
                        borderRadius: BorderRadius.circular(10),
                      ),
                      title:  Text(user!.nickName),
                      subtitle: Text(user!.nickName),
                    ) : Text(stateText!);
                }
            )) : Text("친구를 추가해보세요")
          ]
        ),
      ),
    );
  }
}