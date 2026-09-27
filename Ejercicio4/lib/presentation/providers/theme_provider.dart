import 'package:flutter/material.dart';
import '../../domain/entities/app_settings.dart';
import '../../domain/repositories/settings_repository.dart';

/// Expone el estado del tema (modo + paleta) a toda la app.
///
/// Al cambiar cualquiera de las dos propiedades, se notifica a los
/// widgets suscritos (rebuild automático) y se persiste la preferencia
/// usando el SettingsRepository.
class ThemeProvider extends ChangeNotifier {
  final SettingsRepository settingsRepository;

  ThemeMode _themeMode = ThemeMode.system;
  AppColorScheme _colorScheme = AppColorScheme.guinda;

  ThemeProvider(this.settingsRepository);

  ThemeMode get themeMode => _themeMode;
  AppColorScheme get colorScheme => _colorScheme;

  /// Se llama una vez al iniciar la app, para cargar la preferencia
  /// guardada previamente (si existe).
  Future<void> loadFromStorage() async {
    final settings = await settingsRepository.loadSettings();
    _themeMode = _toFlutterThemeMode(settings.themeMode);
    _colorScheme = settings.colorScheme;
    notifyListeners();
  }

  Future<void> setThemeMode(ThemeMode mode) async {
    _themeMode = mode;
    notifyListeners();
    await _persist();
  }

  Future<void> setColorScheme(AppColorScheme scheme) async {
    _colorScheme = scheme;
    notifyListeners();
    await _persist();
  }

  Future<void> _persist() async {
    final current = await settingsRepository.loadSettings();
    await settingsRepository.saveSettings(current.copyWith(
      themeMode: _toAppThemeMode(_themeMode),
      colorScheme: _colorScheme,
    ));
  }

  ThemeMode _toFlutterThemeMode(AppThemeMode mode) {
    switch (mode) {
      case AppThemeMode.light:
        return ThemeMode.light;
      case AppThemeMode.dark:
        return ThemeMode.dark;
      case AppThemeMode.system:
        return ThemeMode.system;
    }
  }

  AppThemeMode _toAppThemeMode(ThemeMode mode) {
    switch (mode) {
      case ThemeMode.light:
        return AppThemeMode.light;
      case ThemeMode.dark:
        return AppThemeMode.dark;
      case ThemeMode.system:
        return AppThemeMode.system;
    }
  }
}