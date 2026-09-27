import 'file_item.dart';

/// Identificador del esquema de color elegido por el usuario.
enum AppColorScheme { guinda, azul }

/// Modo de tema: junto con el sistema, o forzado claro/oscuro.
enum AppThemeMode { system, light, dark }

/// Preferencias de sesión que se conservan entre ejecuciones de la app:
/// última carpeta visitada, criterio de ordenamiento y tema seleccionado.
class AppSettings {
  final AppThemeMode themeMode;
  final AppColorScheme colorScheme;
  final String? lastFolderPath;
  final SortCriteria sortCriteria;
  final bool sortAscending;

  const AppSettings({
    this.themeMode = AppThemeMode.system,
    this.colorScheme = AppColorScheme.guinda,
    this.lastFolderPath,
    this.sortCriteria = SortCriteria.name,
    this.sortAscending = true,
  });

  /// Crea una copia de este objeto, reemplazando solo los campos indicados.
  /// Útil porque AppSettings es inmutable (todos sus campos son `final`).
  AppSettings copyWith({
    AppThemeMode? themeMode,
    AppColorScheme? colorScheme,
    String? lastFolderPath,
    SortCriteria? sortCriteria,
    bool? sortAscending,
  }) {
    return AppSettings(
      themeMode: themeMode ?? this.themeMode,
      colorScheme: colorScheme ?? this.colorScheme,
      lastFolderPath: lastFolderPath ?? this.lastFolderPath,
      sortCriteria: sortCriteria ?? this.sortCriteria,
      sortAscending: sortAscending ?? this.sortAscending,
    );
  }
}