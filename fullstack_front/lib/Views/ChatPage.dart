import 'dart:io';

import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:fullstack_front/Configs/Configuration.dart';
import 'package:path_provider/path_provider.dart';
import 'package:provider/provider.dart';

import 'ChatDetailsPage.dart';
import '../Models/RoomDTO.dart';

class ChatPage extends StatefulWidget{
  const ChatPage({super.key});

  @override
  State<ChatPage> createState() => _MainState();
}

class _MainState extends State<ChatPage> with WidgetsBindingObserver, RouteAware{
  late String token;
  late List<RoomDTO> rooms = [];
  String dirPath = "NOT_INITIALIZED";
  bool loaded = false;
  late ScrollController scrollController;

  @override
  void didChangeDependencies() {
      super.didChangeDependencies();
      _getRooms();
  }

  // 파일이 존재하는 지 확인
  Future<bool> assetExists(String assetName) async {
    try {
      await rootBundle.load(assetName);
      return true;
    } catch (e) {
      return false;
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

  // friendList
  void _getRooms() async {
    Dio dio = Dio();
    dio.options.baseUrl = dotenv.env["BASE_URL"]!;
    dio.options.headers = {
      "Authorization": "Bearer ${context
          .read<Configuration>()
          .token}"
    };

    final response = await dio.get("/api/rooms");

    if(response.statusCode == 200) {
      List<dynamic> jsonList = response.data;

      print(response.data);

      if(jsonList.isNotEmpty) {
        rooms.clear();

        for(dynamic json in jsonList) {
          RoomDTO roomData = RoomDTO.fromJson(json);

          // 서버에서 이미지 요청
          _getImage(roomData.path.replaceAll("\\", "/"), roomData.nickName);

          setState(() {
            rooms?.add(roomData);
          });
        }
      }
    }
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
    Future<Directory> documentDirectory = getApplicationSupportDirectory();
    documentDirectory.then((value) => dirPath = value.path);
    scrollController = ScrollController();

    _getRooms();
    loaded = true;
  }

  @override
  void dispose() {
    super.dispose();
    loaded = false;
  }

  Widget body() {
    if(loaded == false) {
      return const CircularProgressIndicator();
    }

    return ListView.builder(
      padding: const EdgeInsets.all(0),
      itemCount: (rooms == null) ? 1 : rooms!.length,
      itemBuilder: (BuildContext context, int index) {
        try {
          return Card(
            elevation:0.5,
            shape: const RoundedRectangleBorder(
                borderRadius: BorderRadius.zero
            ),
            color: Colors.white,
            shadowColor: Colors.black,
            margin: const EdgeInsets.symmetric(vertical: 2),
            child: ListTile(
              onTap: () {
                Navigator.push(context, MaterialPageRoute(builder: (context) => ChatDetailsPage(rooms![index].id,rooms![index].userId1,rooms![index].userId2, rooms![index].nickName)))
                    .then((value) {
                  _getRooms();
                });
              },
              leading: ClipRRect(
                  borderRadius: BorderRadius.circular(10),
                  child: SizedBox(child: Image.file(File('$dirPath/${rooms![index].nickName}.jpg'),fit:BoxFit.fill), height:56, width: 56,),
              ),
              title: (rooms == null) ? const Text("생성된 대화방이 없습니다.") : Text(rooms![index].nickName),
              subtitle: (rooms == null) ? const Text("생성된 대화방이 없습니다.") : (rooms![index].lastComment != null) ? Text(rooms![index].lastComment!) : Text(""),
              // trailing: Text("Today"),
            ),
          );
        } catch(e) {
          return Image.network("https://support.start.me/hc/article_attachments/12421647432722");
        }
      },
    );
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
              title: const Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text("Chat",
                    style: TextStyle(fontSize:20, fontWeight: FontWeight.w600),
                  ),

                ],
              ),
              automaticallyImplyLeading: false
          ),

          body: body(),
        ), onWillPop: () {
          return _popEvent();
        });

  }
}

