import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:provider/provider.dart';

import '../HexColor.dart';
import '../Models/UserDTO.dart';

class Configuration with ChangeNotifier{
  String _token = "EMPTY_TOKEN";
  String _targetLang = "ko";
  String _dropDownValues = "한국어";
  UserDTO _user = UserDTO(id: -1, nickName: "NULL", name: "NULL", email: "NULL", role: "NULL", path: "NULL");
  static const storage = FlutterSecureStorage();

  void showSnackBar(BuildContext context, Text text) {
    final snackBar = SnackBar(
      content: text,
      backgroundColor: HexColor("#002de3"),
    );

    ScaffoldMessenger.of(context).showSnackBar(snackBar);
  }

  Future<dynamic> logoutRequest(String token) async {
    Dio dio = Dio();
    dio.options.baseUrl=dotenv.env["BASE_URL"]!;
    dio.options.responseType = ResponseType.plain;
    dio.options.validateStatus = (status) {
      return status! < 500;
    };

    dio.options.headers= {"Authorization" : "Bearer $token"};
    dio.delete("/api/auth/logout");

    // 로그아웃 시 토큰 제거
    storage.delete(key: 'token');
  }

  String get token => _token;

  set token(String value) {
    _token = value;
    notifyListeners();
  }

  UserDTO get user => _user!;

  set user(UserDTO value) {
    _user = value;
    notifyListeners();
  }

  String get targetLang => _targetLang!;

  set targetLang(String value) {
    _targetLang = value;
    notifyListeners();
  }

  String get dropDownValues => _dropDownValues!;

  set dropDownValues(String value) {
    _dropDownValues = value;
    notifyListeners();
  }
}