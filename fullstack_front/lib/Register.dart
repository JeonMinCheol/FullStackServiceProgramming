import 'dart:io';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'HexColor.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';

class Register extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return _RegisterState();
  }
}

class _RegisterState extends State<Register> {
  TextEditingController controller1 = TextEditingController(); // email
  TextEditingController controller2 = TextEditingController(); // password
  TextEditingController controller3 = TextEditingController(); // real name
  TextEditingController controller4 = TextEditingController(); // profile name
  File? _selectedImage;

  TextField email() {
    return TextField(
        controller: controller1,
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
                EdgeInsets.symmetric(vertical: 5.0, horizontal: 10.0)));
  }

  TextField password() {
    return TextField(
        controller: controller2,
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
                EdgeInsets.symmetric(vertical: 5.0, horizontal: 10.0)));
  }

  TextField realName() {
    return TextField(
        controller: controller3,
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
                EdgeInsets.symmetric(vertical: 5.0, horizontal: 10.0)));
  }

  TextField profileName() {
    return TextField(
        controller: controller4,
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
                EdgeInsets.symmetric(vertical: 5.0, horizontal: 10.0)));
  }

  ButtonTheme registerButton(BuildContext context) {
    return ButtonTheme(
        minWidth: 100.0,
        height: 50.0,
        child: ElevatedButton(
            onPressed: () {},
            child: Text(
              'Register',
              style: TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.w600,
              ),
            ),
            style: ElevatedButton.styleFrom(
              minimumSize: Size(400, 50),
              backgroundColor: HexColor("#002de3"),
              shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(30.0)),
            )));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: Text(
            '로그인',
            style: TextStyle(fontSize: 18, fontWeight: FontWeight.w600),
          ),
          elevation: 0.0,
          leading: IconButton(
              icon: Icon(
                FontAwesomeIcons.arrowRotateLeft,
                size: 16,
              ),
              onPressed: () {
                Navigator.pop(
                  context,
                );
              }),
        ),
        body: GestureDetector(
            onTap: () {
              FocusScope.of(context).unfocus();
            },
            child: SingleChildScrollView(
                child: Column(children: [
              Padding(padding: EdgeInsets.only(top: 10)),
              Center(
                child: profileImageSelector()
              ),
              Form(
                  child: Theme(
                      data: ThemeData(
                        primaryColor: Colors.grey,
                      ),
                      child: Container(
                        padding: EdgeInsets.symmetric(
                            vertical: 19.0, horizontal: 30),
                        child: Builder(builder: (context) {
                          return Column(children: [
                            email(),
                            SizedBox(
                              height: 24.0,
                            ), // 공백
                            password(),
                            SizedBox(
                              height: 24.0,
                            ), // 공백
                            realName(),
                            SizedBox(
                              height: 24.0,
                            ), // 공백
                            profileName(),
                            SizedBox(
                              height: 24.0,
                            ), // 공백
                            registerButton(context)
                          ]);
                        }),
                      )))
            ]))));
  }

  GestureDetector profileImageSelector() {
    dynamic profileImage = _selectedImage == null ? const AssetImage("assets/Avatar.png") : FileImage(_selectedImage!);

    return GestureDetector(
                  child: Container(
                      width: 100,
                      height: 100,
                      decoration: BoxDecoration(
                          borderRadius: BorderRadius.all(Radius.circular(50)),
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
    var returnedImage = await ImagePicker().pickImage(source: ImageSource.gallery);

    if(returnedImage == null) return ;
    setState(() {
      _selectedImage = File(returnedImage!.path);
    });
  }
}
