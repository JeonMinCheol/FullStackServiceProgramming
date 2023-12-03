import 'dart:convert';
import 'dart:io';

import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/scheduler.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:fullstack_front/Models/ChatDTO.dart';
import 'package:fullstack_front/Views/ChatPage.dart';
import 'package:image_picker/image_picker.dart';
import 'package:path_provider/path_provider.dart';
import 'package:provider/provider.dart';
import 'package:stomp_dart_client/stomp.dart';
import 'package:stomp_dart_client/stomp_config.dart';
import 'package:stomp_dart_client/stomp_frame.dart';

import '../Configs/Configuration.dart';
import '../Models/UserDTO.dart';

class ChatDetailsPage extends StatefulWidget {
  final int roomNumber;
  final int userId;
  final int friendId;
  final String roomName;
  const ChatDetailsPage(this.roomNumber,this.userId, this.friendId, this.roomName, {super.key});

  @override
  State<StatefulWidget> createState() => _ChatDetailsPageState();
}

class _ChatDetailsPageState extends State<ChatDetailsPage> {
  StompClient? stompClient;
  var socketUrl = dotenv.env["WEB_SOCKET_URL"]!+"/ws";
  final TextEditingController _textController = TextEditingController();
  late String dirPath = "NOT_INITIALIZED";
  List<ChatDTO> messages = [];

  void onConnect(StompFrame frame) {
    stompClient!.subscribe(
        destination: '/sub/room/${widget.roomNumber}',
        callback: (StompFrame frame) {
          if (frame.body != null) {
            Map<String, dynamic> response = json.decode(frame.body!);

            ChatDTO message = ChatDTO.fromJson(response);
            debugPrint(response.keys.toString());
            debugPrint(response.values.toString());

            setState(() {
              if(message.image != "null"){
                _getImage(message.image!.replaceAll("\\", "/"), message.image!.replaceAll("\\", "/")).whenComplete(() => {
                    messages.add(message),
                    print("이미지 로딩.")
                });
              }
              else {
                messages.add(message);
              }
            });
          }
        });
  }

  @override
  void dispose() {
    stompClient!.deactivate();
    messages = [];
    super.dispose();
  }

  Future<void> saveImage(List<int> imageSource, String name) async {
    try {
      // 애플리케이션 디렉터리에 저장할 파일 경로 생성
      // 경로: /data/user/0/com.example.fullstack_front/files/filename
      String filePath = "$dirPath$name";

      // 파일에 이미지 데이터 쓰기
      File file = File(filePath);

      if(!file.existsSync()) {
        File(filePath).createSync(recursive: true);
      }

      setState(() {
        file.writeAsBytes(imageSource);
      });
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

    Response response = await dio.get("/api/image/$path");

    if(response.statusCode == 200) {
      await saveImage(response.data, name);
    }
  }

  // 방 입장 시 메세지 받아오기
  void _getMessages() async {
    Dio dio = Dio();
    dio.options.baseUrl = dotenv.env["BASE_URL"]!;
    dio.options.headers = {
      "Authorization": "Bearer ${context
          .read<Configuration>()
          .token}"
    };

    final response = await dio.get("/api/room/${widget.roomNumber}/chats");
    if(response.statusCode == 200) {
      List<dynamic> jsonList = response.data;

      if(jsonList.isNotEmpty) {
        messages.clear();

        for(dynamic json in jsonList) {
          ChatDTO chatData = ChatDTO.fromJson(json);
          print(chatData.nickName);
          print(chatData.text);
          print(chatData.translate);
          print(chatData.image);

            // 서버에서 이미지 요청
            if(chatData.image != "null") {
              // 서버에 저장된 이름 그대로 저장
              _getImage(chatData.image!.replaceAll("\\", "/"), chatData.image!.replaceAll("\\", "/"))
                  .whenComplete(() => {});
            }

            messages?.add(chatData);
        }
      }
    }
    setState(() {
      print(messages.length.toString());
    });
  }

  @override
  void initState() {
    super.initState();
    Future<Directory> documentDirectory = getApplicationSupportDirectory();
    documentDirectory.then((value) => dirPath = value.path);
    _getMessages();

    if (stompClient == null) {
      stompClient = StompClient(
          config: StompConfig.sockJS(
            url: socketUrl,
            onConnect: onConnect,
            onStompError: (dynamic error) => print(error.toString()),
            onWebSocketError: (dynamic error) => print(error.toString()),
            stompConnectHeaders: {'Authorization': "Bearer ${context
                .read<Configuration>()
                .token}"},
            webSocketConnectHeaders: {'Authorization': "Bearer ${context
                .read<Configuration>()
                .token}"},
          ));
      stompClient!.activate();
    }
  }

  Future getImageFromGallery() async {
    var image = await ImagePicker().pickImage(source: ImageSource.gallery, imageQuality: 100, maxHeight: 1320, maxWidth: 130);
    final bytes = File(image!.path).readAsBytesSync();
    String img = base64Encode(bytes);
    return img;
  }

  sendMessage() async {
      String nickName = context.read<Configuration>().user.nickName;
      stompClient!.send(destination: '/pub/chat', body: json.encode({"nickName" : nickName, "text" : _textController.text, "roomId" : widget.roomNumber, "binaryImage" : null, "targetLang" : context.read<Configuration>().targetLang}));
      _textController.text = "";
  }

  sendImage() async {
    String? bytes = await getImageFromGallery();
    String nickName = context.read<Configuration>().user.nickName;
    stompClient!.send(destination: '/pub/chat', body: json.encode({"nickName" : nickName, "text" : null, "roomId" : widget.roomNumber, "binaryImage" : bytes}));
  }

  @override
  Widget build(BuildContext context) {
    print("build!!");
    return Scaffold(
        appBar: AppBar(
          title: Text(widget.roomName),
        ),
      body: Stack(
        children: [
          SizedBox(
            height:650,
            child:
          ListView.builder(
            itemCount: messages.length,
            // shrinkWrap: true,
            // padding: const EdgeInsets.only(top: 10,bottom: 10),
            itemBuilder: (context, index){
              var nickName = context.read<Configuration>().user.nickName;

              bool isMe = messages[index].nickName == nickName;
              return Container(
                padding: const EdgeInsets.only(left: 14,right: 14,top: 10,bottom: 10),
                child: SingleChildScrollView(
                  child: Align(
                  alignment: (isMe ? Alignment.topRight : Alignment.topLeft),
                    child :
                    Column(
                      children: [
                      // 사용자 이름
                      Align(
                        child: Text(messages[index].nickName!),
                        alignment: (isMe ? Alignment.topRight : Alignment.topLeft),
                      ),

                    // 채팅 박스
                        Align(
                          child:Container(
                              decoration: BoxDecoration(
                                borderRadius: BorderRadius.circular(20),
                                color: (isMe ? Colors.indigo : Colors.grey.shade200),
                              ),
                              padding: const EdgeInsets.all(16),
                              child:Column(
                                  crossAxisAlignment: CrossAxisAlignment.end,
                                  children: [

                                    ClipRRect(
                                      child: Offstage(offstage: messages[index]!.image == "null", child: Image.file(File('$dirPath${messages[index]!.image!.replaceAll("\\", "/")}'),fit:BoxFit.fill)),
                                      borderRadius: BorderRadius.circular(10),
                                    ),
                                    Offstage(offstage: messages[index]!.text == "null", child: (isMe
                                        ? SizedBox(child: Text((messages[index].text == "null") ? "" : messages[index].text!, style: const TextStyle(fontSize: 15, color: Colors.white), textAlign: TextAlign.right,),)
                                        : SizedBox(child: Text((messages[index].text == "null") ? "" : messages[index].text!, style: const TextStyle(fontSize: 15), textAlign: TextAlign.left,),))),
                                    (isMe ? Offstage(
                                      offstage: ((messages[index].translate == "" || messages[index].translate == "null")),child:Text((messages[index].translate == "null") ? "" : messages[index].translate!, textAlign: TextAlign.right, style: TextStyle(fontSize: 15, color:Colors.white)),) :
                                    Offstage(offstage: ((messages[index].translate == "" || messages[index].translate == "null")),child:Text(messages[index].translate!, textAlign: TextAlign.right, style: TextStyle(fontSize: 15)),)),
                                    (isMe ? Container(child: Text("${messages[index].time!.hour.toString()!} : ${messages[index].time!.minute.toString()!}", style: const TextStyle(fontSize: 10, color: Colors.white), textAlign: TextAlign.right,),) : Container(child: Text(messages[index].time!.hour.toString()! + " : " + messages[index].time!.minute.toString()!, style: const TextStyle(fontSize: 10), textAlign: TextAlign.left,),)),
                                  ]
                              )
                          ),
                          alignment: (isMe ? Alignment.topRight : Alignment.topLeft),
                        )
                    ],)
                ),
              )
              );
            },
          ),

          ),
      SizedBox(
        child:
          Align(
            alignment: Alignment.bottomLeft,
            child: Container(
              padding: const EdgeInsets.only(left: 10,bottom: 10,top: 10),
              height: 60,
              width: double.infinity,
              color: Colors.white,
              child: Row(
                children: <Widget>[
                  GestureDetector(
                    onTap: (){
                      sendImage();
                    },
                    child: Container(
                      height: 30,
                      width: 30,
                      decoration: const BoxDecoration(
                        color: Colors.transparent,
                      ),
                      child: const Icon(Icons.add, color: Colors.grey, size: 24, ),
                    ),
                  ),
                  const SizedBox(width: 15,),
                  Expanded(
                    child: TextField(
                      controller: _textController,
                      decoration: const InputDecoration(
                          hintText: "Write message...",
                          hintStyle: TextStyle(color: Colors.black54),
                          border: InputBorder.none
                      ),
                    ),
                  ),
                  const SizedBox(width: 15,),
                  FloatingActionButton(
                    onPressed: (){sendMessage();},
                    backgroundColor: Colors.transparent,
                    elevation: 0,
                    child: const Icon(Icons.send, color: Colors.indigo,size: 18,),
                  ),
                ],

              ),
            ),
          )),
        ],
      ),
    );
  }
}