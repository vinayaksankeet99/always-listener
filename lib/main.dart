import 'package:always_listener/config/app_theme.dart';
import 'package:always_listener/homepage.dart';
import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Always listener',
      debugShowCheckedModeBanner: false,
      theme: AppTheme.getThemeData(context),
      home: const HomePage(),
    );
  }
}
