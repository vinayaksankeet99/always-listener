import 'package:always_listener/config/app_color.dart';
import 'package:flutter/material.dart';

const TextTheme _textTheme = TextTheme(
  displayLarge: TextStyle(
    fontSize: 93,
    fontWeight: FontWeight.w300,
    color: AppColors.text,
  ),
  displayMedium: TextStyle(
      // 42 in design system
      fontSize: 42,
      fontWeight: FontWeight.bold,
      color: AppColors.text),
  displaySmall: TextStyle(
    fontSize: 32,
    fontWeight: FontWeight.bold,
    color: AppColors.text,
  ),
  headlineMedium: TextStyle(
    fontSize: 28,
    fontWeight: FontWeight.bold,
    color: AppColors.text,
  ),
  headlineSmall: TextStyle(
    fontSize: 24,
    fontWeight: FontWeight.bold,
    color: AppColors.text,
  ),
  titleLarge: TextStyle(
      fontSize: 20, fontWeight: FontWeight.bold, color: AppColors.text),
  bodyLarge: TextStyle(
    fontSize: 18,
    fontWeight: FontWeight.bold,
    color: AppColors.text,
  ),
  bodyMedium: TextStyle(
    fontSize: 16,
    fontWeight: FontWeight.bold,
    color: AppColors.text,
  ),
  titleMedium: TextStyle(
    fontSize: 14,
    fontWeight: FontWeight.w600,
    color: AppColors.text,
  ),
  titleSmall: TextStyle(
    fontSize: 12,
    fontWeight: FontWeight.w600,
    color: AppColors.text,
  ),
  labelLarge: TextStyle(
    fontSize: 11,
    fontWeight: FontWeight.w600,
    color: AppColors.text,
  ),
  bodySmall: TextStyle(
    fontSize: 12,
    fontWeight: FontWeight.w500,
    color: AppColors.text,
  ),
  labelSmall: TextStyle(
    fontSize: 10,
    fontWeight: FontWeight.w400,
    color: AppColors.text,
  ),
);

class AppTheme {
  static ThemeData getThemeData(BuildContext context) {
    return ThemeData(
      textTheme: _textTheme,
      brightness: Brightness.light,
      bottomSheetTheme:
          const BottomSheetThemeData(backgroundColor: Colors.transparent),
      appBarTheme: AppBarTheme(
        elevation: 0,
        backgroundColor: AppColors.white,
        iconTheme: const IconThemeData(color: AppColors.baseColor2),
        titleTextStyle: _textTheme.titleSmall!.copyWith(
          color: AppColors.text,
          fontSize: 18,
        ),
      ),
      textButtonTheme: TextButtonThemeData(
          style: TextButton.styleFrom(textStyle: _textTheme.titleMedium)),
      chipTheme: ChipThemeData(
        backgroundColor: AppColors.baseColor1.withOpacity(.1),
      ),
      scaffoldBackgroundColor: AppColors.white,
    );
  }
}
