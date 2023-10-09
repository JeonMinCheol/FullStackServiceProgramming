import 'package:flutter/material.dart';
import 'package:fullstack_front/Register.dart';
import 'HexColor.dart';

class Login extends StatefulWidget {
  @override
  State<Login> createState() => _LogInState();
}

class _LogInState extends State<Login> {
  TextEditingController controller = TextEditingController();
  TextEditingController controller2 = TextEditingController();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
          // title: Text('Log in'),
          // elevation: 0.0,
          // backgroundColor: Colors.redAccent,
          // centerTitle: true,
          // 아래 줄은 메인 페이지에서 활용할 수 있음.
          // leading: IconButton(icon: Icon(Icons.menu), onPressed: () {}),
          // actions: <Widget>[
          //   IconButton(icon: Icon(Icons.search), onPressed: () {})
          // ],
          ),
      // email, password 입력하는 부분을 제외한 화면을 탭하면, 키보드 사라지게 GestureDetector 사용
      body: GestureDetector(
        onTap: () {
          FocusScope.of(context).unfocus();
        },
        child: SingleChildScrollView(
          child: Column(
            children: [
              Padding(padding: EdgeInsets.only(top: 50)),
              Center(
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
                        EdgeInsets.symmetric(vertical: 50.0, horizontal: 30),
                    child: Builder(builder: (context) {
                      return Column(
                        children: [
                          email(),
                          SizedBox(
                            height: 24.0,
                          ), // 공백

                          password(),
                          SizedBox(
                            height: 30.0,
                          ), // 공백

                          Text("아직 계정이 없으신가요?",
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
                                      minimumSize: Size(0, 0)),
                                  child: Text("여기",
                                      style: TextStyle(
                                        fontSize: 14,
                                        fontWeight: FontWeight.w600,
                                      ))),
                              Text("를 눌러 회원가입 해보세요",
                                  style: TextStyle(
                                    fontSize: 14,
                                    fontWeight: FontWeight.w600,
                                  )),
                            ],
                          ),
                          SizedBox(
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
              if (controller.text == '1' && controller2.text == '1234') {
                Navigator.push(
                    context,
                    MaterialPageRoute(
                        builder: (BuildContext context) => NextPage()));
              } else if (controller.text == '1' && controller2.text != '1234') {
                showSnackBar(context, Text('Wrong password'));
              } else if (controller.text != '1' && controller2.text == '1234') {
                showSnackBar(context, Text('Wrong email'));
              } else {
                showSnackBar(context, Text('Check your info again'));
              }
            },
            child: Text(
              'Login',
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
              EdgeInsets.symmetric(vertical: 5.0, horizontal: 10.0)),
      keyboardType: TextInputType.text,
      obscureText: true, // 비밀번호 안보이도록 하는 것
    );
  }

  TextField email() {
    return TextField(
      controller: controller,
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
              EdgeInsets.symmetric(vertical: 5.0, horizontal: 10.0)),
      keyboardType: TextInputType.emailAddress,
    );
  }
}

void showSnackBar(BuildContext context, Text text) {
  final snackBar = SnackBar(
    content: text,
    backgroundColor: HexColor("#002de3"),
  );

// Find the ScaffoldMessenger in the widget tree
// and use it to show a SnackBar.
  ScaffoldMessenger.of(context).showSnackBar(snackBar);
}

class NextPage extends StatelessWidget {
  const NextPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Container();
  }
}
