import 'package:flutter/cupertino.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:fullstack_front/Models/UserDTO.dart';

import 'ChatPage.dart';
import 'FriendsPage.dart';
import 'SettingPage.dart';

class ScreenPage extends StatefulWidget {
  const ScreenPage({super.key});

  @override
  State<StatefulWidget> createState() {
    return _ScreenPageState();
  }
}

class _ScreenPageState extends State<ScreenPage> with TickerProviderStateMixin{
  late TabController _tabController;
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: TabBarView(
        children: [
          const FriendPage(),
          const ChatPage(),
          Settings()
        ],
        controller: _tabController,
      ),
      bottomNavigationBar: btmBar(),
    );
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  TabBar btmBar() {
    return TabBar(
      controller: _tabController,
      indicatorColor: Colors.indigo,
      tabs: const [
        Tab(
            icon: Icon(Icons.account_circle), text: 'Friends'),
        Tab(icon: Icon(Icons.chat), text: 'Chats'),
        Tab(
            icon: Icon(Icons.add_circle), text: 'More')
      ],
    );
  }

  @override
  void initState() {
    _tabController = TabController(length: 3, vsync: this);
    super.initState();
  }
}