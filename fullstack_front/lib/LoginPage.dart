import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:fullstack_front/Configuration.dart';
import 'package:fullstack_front/MainPage.dart';
import 'package:fullstack_front/RegisterPage.dart';
import 'package:provider/provider.dart';
import 'HexColor.dart';

class Login extends StatefulWidget {
  const Login({super.key});

  @override
  State<Login> createState() => _LogInState();
}

class _LogInState extends State<Login> {
  final Configuration configuration = Configuration();
  static const storage = FlutterSecureStorage();
  TextEditingController emailTextController = TextEditingController();
  TextEditingController passwordTextController = TextEditingController();


  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _getUserInfo();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
          ),
      // email, password 입력하는 부분을 제외한 화면을 탭하면, 키보드 사라지게 GestureDetector 사용
      body: GestureDetector(
        onTap: () {
          FocusScope.of(context).unfocus();
        },
        child: SingleChildScrollView(
          child: Column(
            children: [
              const Padding(padding: EdgeInsets.only(top: 50)),
              const Center(
                  child: Text("로그인",
                      style: TextStyle(
                        fontSize: 24,
                        fontWeight: FontWeight.w700,
                      ))),
              Form(
                  child: Theme(
                data: ThemeData(
                  primaryColor: Colors.grey,
                ),
                child: Container(
                    padding:
                        const EdgeInsets.symmetric(vertical: 50.0, horizontal: 30),
                    child: Builder(builder: (context) {
                      return Column(
                        children: [
                          email(),
                          const SizedBox(
                            height: 24.0,
                          ), // 공백

                          password(),
                          const SizedBox(
                            height: 30.0,
                          ), // 공백

                          const Text("아직 계정이 없으신가요?",
                              style: TextStyle(
                                fontSize: 14,
                                fontWeight: FontWeight.w600,
                              )),

                          Row(
                            mainAxisAlignment: MainAxisAlignment.center,
                            //Center Row contents horizontally,
                            children: [
                              TextButton(
                                  onPressed: () {
                                    Navigator.push(
                                      context,
                                      MaterialPageRoute(
                                          builder: (context) => Register()),
                                    );
                                  },
                                  style: TextButton.styleFrom(
                                      padding: EdgeInsets.zero,
                                      tapTargetSize:
                                          MaterialTapTargetSize.shrinkWrap,
                                      minimumSize: const Size(0, 0)),
                                  child: const Text("여기",
                                      style: TextStyle(
                                        fontSize: 14,
                                        fontWeight: FontWeight.w600,
                                      ))),
                              const Text("를 눌러 회원가입 해보세요",
                                  style: TextStyle(
                                    fontSize: 14,
                                    fontWeight: FontWeight.w600,
                                  )),
                            ],
                          ),
                          const SizedBox(
                            height: 30,
                          ),
                          loginButton(context)
                        ],
                      );
                    })),
              ))
            ],
          ),
        ),
      ),
    );
  }

  ButtonTheme loginButton(BuildContext context) {
    return ButtonTheme(
        minWidth: 100.0,
        height: 50.0,
        child: ElevatedButton(
            onPressed: () {
              loginRequest();
            },
            style: ElevatedButton.styleFrom(
              minimumSize: const Size(400, 50),
              backgroundColor: HexColor("#002de3"),
              shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(30.0)),
            ),
            child: const Text(
              'Login',
              style: TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.w600,
                color: Color.fromRGBO(255, 255, 255, 1)
              ),
            )));
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
      keyboardType: TextInputType.text,
      obscureText: true, // 비밀번호 안보이도록 하는 것
    );
  }

  TextField email() {
    return TextField(
      controller: emailTextController,
      autofocus: true,
      style: TextStyle(
          color: Colors.grey[700], fontWeight: FontWeight.w600, fontSize: 14),
      decoration: InputDecoration(
          border: const OutlineInputBorder(
            borderRadius: BorderRadius.all(Radius.circular(4)),
            borderSide: BorderSide.none,
          ),
          filled: true,
          fillColor: HexColor("#F7F7FC"),
          hintStyle: TextStyle(color: HexColor("#ACB5BD")),
          hintText: 'Email',
          contentPadding:
              const EdgeInsets.symmetric(vertical: 5.0, horizontal: 10.0)),
      keyboardType: TextInputType.emailAddress,
    );
  }

  Future loginRequest() async {
    Dio dio = Dio();
    dio.options.baseUrl=dotenv.env["BASE_URL"]!;
    dio.options.contentType = "application/json";
    dio.options.responseType = ResponseType.plain;
    dio.options.validateStatus = (status) {
      return status! < 500;
    };

    final response = await dio.post("/api/auth/login", data: {
      "email" : emailTextController.text,
      "password" : passwordTextController.text
    });

    if(response.statusCode == 200) {
      // ignore: use_build_context_synchronously
      context.read<Configuration>().token = response.data; // provider에 토큰 값 저장

      // storage에 토큰 저장 (앱 강제종료 시 로그인 가능)
      if (await storage.read(key: 'token') == null) {
        await storage.write(
          key: 'token',
          value: response.data,
        );
      }

      // ignore: use_build_context_synchronously
      Navigator.push(
        context,
        MaterialPageRoute(
            builder: (context) => const MainPage()),
      );
    }
    else {
      configuration.showSnackBar(context, Text(response.data));
    }
  }

  void _getUserInfo() async {
    // read 함수로 key값에 맞는 정보를 불러오고 데이터타입은 String 타입
    // 데이터가 없을때는 null을 반환
    dynamic userInfo = await storage.read(key:'token');

    // user의 정보가 있다면 메인 페이지로 바로 이동.
    if (userInfo != null) {
      // ignore: use_build_context_synchronously
      context.read<Configuration>().token = userInfo;
      Navigator.push(context, MaterialPageRoute(builder: (context) => const MainPage()));
    }
  }
}

