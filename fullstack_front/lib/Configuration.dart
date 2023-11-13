import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:provider/provider.dart';

import 'HexColor.dart';

class Configuration with ChangeNotifier{
  String _token = "EMPTY_TOKEN";
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
    dio.get("/api/auth/logout");

    // 로그아웃 시 토큰 제거
    storage.delete(key: 'token');
  }

  String get token => _token;

  set token(String value) {
    _token = value;
    notifyListeners();
  }
}