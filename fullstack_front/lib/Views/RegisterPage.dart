import 'dart:convert';
import 'dart:io';

import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:fullstack_front/Configs/Configuration.dart';
import 'package:image_picker/image_picker.dart';
import '../HexColor.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:http_parser/http_parser.dart';

class Register extends StatefulWidget {

  @override
  State<StatefulWidget> createState() {
    return _RegisterState();
  }
}

class _RegisterState extends State<Register> {
  TextEditingController emailTextController = TextEditingController(); // email
  TextEditingController passwordTextController = TextEditingController(); // password
  TextEditingController realNameTextController = TextEditingController(); // real name
  TextEditingController profileNameTextController = TextEditingController(); // profile name
  String? imagePath;
  File? _selectedImage;

  TextField email() {
    return TextField(
        controller: emailTextController,
        style: TextStyle(
            color: Colors.grey[700], fontWeight: FontWeight.w600, fontSize: 14),
        decoration: InputDecoration(
            border: const OutlineInputBorder(
              borderRadius: BorderRadius.all(Radius.circular(4)),
              borderSide: BorderSide.none,
            ),
            filled: true,
            fillColor: HexColor("#F7F7FC"),
            hintText: 'Email',
            hintStyle: TextStyle(color: HexColor("#ACB5BD")),
            contentPadding:
                const EdgeInsets.symmetric(vertical: 5.0, horizontal: 10.0)));
  }

  TextField password() {
    return TextField(
        controller: passwordTextController,
        style: TextStyle(
            color: Colors.grey[700], fontWeight: FontWeight.w600, fontSize: 14),
        decoration: InputDecoration(
            border: const OutlineInputBorder(
              borderRadius: BorderRadius.all(Radius.circular(4)),
              borderSide: BorderSide.none,
            ),
            filled: true,
            fillColor: HexColor("#F7F7FC"),
            hintText: 'Password',
            hintStyle: TextStyle(color: HexColor("#ACB5BD")),
            contentPadding:
                const EdgeInsets.symmetric(vertical: 5.0, horizontal: 10.0)),
            obscureText: true // 비밀번호 안보이도록 하는 것
    );
  }

  TextField realName() {
    return TextField(
        controller: realNameTextController,
        style: TextStyle(
            color: Colors.grey[700], fontWeight: FontWeight.w600, fontSize: 14),
        decoration: InputDecoration(
            border: const OutlineInputBorder(
              borderRadius: BorderRadius.all(Radius.circular(4)),
              borderSide: BorderSide.none,
            ),
            filled: true,
            fillColor: HexColor("#F7F7FC"),
            hintText: 'Real Name',
            hintStyle: TextStyle(color: HexColor("#ACB5BD")),
            contentPadding:
                const EdgeInsets.symmetric(vertical: 5.0, horizontal: 10.0)));
  }

  TextField profileName() {
    return TextField(
        controller: profileNameTextController,
        style: TextStyle(
            color: Colors.grey[700], fontWeight: FontWeight.w600, fontSize: 14),
        decoration: InputDecoration(
            border: const OutlineInputBorder(
              borderRadius: BorderRadius.all(Radius.circular(4)),
              borderSide: BorderSide.none,
            ),
            filled: true,
            fillColor: HexColor("#F7F7FC"),
            hintText: 'Profile Name',
            hintStyle: TextStyle(color: HexColor("#ACB5BD")),
            contentPadding:
                const EdgeInsets.symmetric(vertical: 5.0, horizontal: 10.0)));
  }

  ButtonTheme registerButton(BuildContext context) {
    return ButtonTheme(
        minWidth: 100.0,
        height: 50.0,
        child: ElevatedButton(
            onPressed: () {
              _registerRequest();
            },
            style: ElevatedButton.styleFrom(
              minimumSize: const Size(400, 50),
              backgroundColor: HexColor("#002de3"),
              shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(30.0)),
            ),
            child: const Text(
              'Register',
              style: TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.w600, color: Color.fromRGBO(255, 255, 255, 1)
              ),
            )));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: buildAppBar(context),
        body: buildGestureDetector(context));
  }

  GestureDetector buildGestureDetector(BuildContext context) {
    return GestureDetector(
          onTap: () {
            FocusScope.of(context).unfocus();
          },
          child: SingleChildScrollView(
              child: Column(children: [
            const Padding(padding: EdgeInsets.only(top: 10)),
            Center(
              child: profileImageSelector()
            ),
            Form(
                child: Theme(
                    data: ThemeData(
                      primaryColor: Colors.grey,
                    ),
                    child: Container(
                      padding: const EdgeInsets.symmetric(
                          vertical: 19.0, horizontal: 30),
                      child: Builder(builder: (context) {
                        return Column(children: [
                          email(),
                          const SizedBox(
                            height: 24.0,
                          ), // 공백
                          password(),
                          const SizedBox(
                            height: 24.0,
                          ), // 공백
                          realName(),
                          const SizedBox(
                            height: 24.0,
                          ), // 공백
                          profileName(),
                          const SizedBox(
                            height: 24.0,
                          ), // 공백
                          registerButton(context)
                        ]);
                      }),
                    )))
          ])));
  }

  AppBar buildAppBar(BuildContext context) {
    return AppBar(
        title: const Text(
          '회원가입',
          style: TextStyle(fontSize: 18, fontWeight: FontWeight.w600),
        ),
        elevation: 0.0,
        leading: IconButton(
            icon: const Icon(
              FontAwesomeIcons.arrowRotateLeft,
              size: 16,
            ),
            onPressed: () {
              Navigator.pop(
                context,
              );
            }),
      );
  }

  GestureDetector profileImageSelector() {
    dynamic profileImage = _selectedImage == null ? const AssetImage("assets/Avatar.png") : FileImage(_selectedImage!);

    return GestureDetector(
                  child: Container(
                      width: 100,
                      height: 100,
                      decoration: BoxDecoration(
                          borderRadius: const BorderRadius.all(Radius.circular(50)),
                          color: Colors.transparent,
                          image: DecorationImage(
                              image:profileImage,
                              fit:BoxFit.fill
                          ),
                      )
                  ),onTap:(){
                _PickerImageFromGallery();
              }
              );
  }

  Future _PickerImageFromGallery() async {
    var returnedImage = await ImagePicker().pickImage(
        source: ImageSource.gallery,
        imageQuality: 50
    );

    if(returnedImage == null) return ;

    imagePath = returnedImage!.path;

    setState(() {
      _selectedImage = File(imagePath!);
    });
  }

  Future _registerRequest() async {
    Dio dio = Dio();

    dio.options.baseUrl=dotenv.env["BASE_URL"]!;
    dio.options.responseType = ResponseType.plain;
    dio.options.validateStatus = (status) {
      return status! < 500;
    };

    FormData _formdata;

    dynamic request = {
      "email" : emailTextController.text,
      "password" : passwordTextController.text,
      "name" : realNameTextController.text,
      "nickName" : profileNameTextController.text
    };

    _formdata = FormData.fromMap({
      "request": MultipartFile.fromString(jsonEncode(request), contentType: MediaType.parse("application/json")),
      "profile" : MultipartFile.fromFileSync(imagePath!)
    });

    final response = await dio.post("/api/auth/register", data: _formdata);

    if(response.statusCode == 201) {
      Navigator.pop(context);
    }
  }
}
