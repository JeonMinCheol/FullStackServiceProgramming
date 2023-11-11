import 'package:flutter/material.dart';

import 'Settings.dart';

class BtmBar extends StatefulWidget {
  const BtmBar({super.key});

  @override
  State<StatefulWidget> createState() => _BtmBarState();

}

class _BtmBarState extends State<BtmBar> with SingleTickerProviderStateMixin {
  late TabController _tabController;
  int _index = 0;

  void _onItemTapped(int index) {
    setState(() {
      Navigator.push(context, MaterialPageRoute(builder: (context) => Settings()));
      _index = index;
    });
  }


  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    List<Widget> screenList = [const Text('홈스크린'), const Text('마이 스크린')];

    return BottomNavigationBar(
      currentIndex: _index,
      selectedItemColor: Colors.indigo,
      items: const [
        BottomNavigationBarItem(icon: Icon(Icons.chat), label: 'Chats'),
        BottomNavigationBarItem(icon: Icon(Icons.add_circle_outline), label: 'More')
      ],
      onTap: (value) {
          _onItemTapped(value);
      },
    );
  }

}
