import 'package:flutter/material.dart';
import '../../domain/entities/app_settings.dart';
import 'app_colors.dart';

class AppTheme {
  AppTheme._();

  static ThemeData light(AppColorScheme scheme) {
    final seed = scheme == AppColorScheme.azul
        ? AppColors.azulSeed
        : AppColors.guindaSeed;

    final colorScheme = ColorScheme.fromSeed(
      seedColor: seed,
      brightness: Brightness.light,
    );

    return _base(colorScheme);
  }

  static ThemeData dark(AppColorScheme scheme) {
    final seed = scheme == AppColorScheme.azul
        ? AppColors.azulSeed
        : AppColors.guindaSeed;

    final colorScheme = ColorScheme.fromSeed(
      seedColor: seed,
      brightness: Brightness.dark,
    );

    return _base(colorScheme);
  }

  static ThemeData _base(ColorScheme colorScheme) {
    return ThemeData(
      useMaterial3: true,
      colorScheme: colorScheme,
      scaffoldBackgroundColor: colorScheme.surface,
      appBarTheme: AppBarTheme(
        backgroundColor: colorScheme.surface,
        foregroundColor: colorScheme.onSurface,
        elevation: 0,
      ),
    );
  }
}