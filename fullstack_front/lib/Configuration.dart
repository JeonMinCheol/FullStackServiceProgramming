import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'HexColor.dart';

class Configuration with ChangeNotifier{
  String _baseUrl = "http://172.21.94.197:8080";
  String _token = "EMPTY_TOKEN";

  void showSnackBar(BuildContext context, Text text) {
    final snackBar = SnackBar(
      content: text,
      backgroundColor: HexColor("#002de3"),
    );

    ScaffoldMessenger.of(context).showSnackBar(snackBar);
  }

  String get token => _token;

  set token(String value) {
    _token = value;
    notifyListeners();
  }

  String get baseUrl => _baseUrl;

  set baseUrl(String value) {
    _baseUrl = value;
    notifyListeners();
  }

  void setToken(String token) {
    _token =token;
    notifyListeners();
  }
}