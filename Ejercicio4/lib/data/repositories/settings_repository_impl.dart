import '../../domain/entities/app_settings.dart';
import '../../domain/entities/file_item.dart';
import '../../domain/repositories/settings_repository.dart';
import '../datasources/local_storage_datasource.dart';

class SettingsRepositoryImpl implements SettingsRepository {
  final LocalStorageDataSource local;

  SettingsRepositoryImpl(this.local);

  @override
  Future<AppSettings> loadSettings() async {
    final themeModeStr = local.getSetting<String>('theme_mode');
    final colorStr = local.getSetting<String>('theme_color');
    final lastFolder = local.getSetting<String>('last_folder');
    final sortStr = local.getSetting<String>('sort_criteria');
    final ascending = local.getSetting<bool>('sort_ascending') ?? true;

    return AppSettings(
      themeMode: _themeModeFromString(themeModeStr),
      colorScheme:
          colorStr == 'azul' ? AppColorScheme.azul : AppColorScheme.guinda,
      lastFolderPath: lastFolder,
      sortCriteria: _sortCriteriaFromString(sortStr),
      sortAscending: ascending,
    );
  }

  @override
  Future<void> saveSettings(AppSettings settings) async {
    await local.setSetting(
        'theme_mode', _themeModeToString(settings.themeMode));
    await local.setSetting('theme_color',
        settings.colorScheme == AppColorScheme.azul ? 'azul' : 'guinda');
    if (settings.lastFolderPath != null) {
      await local.setSetting('last_folder', settings.lastFolderPath);
    }
    await local.setSetting('sort_criteria', settings.sortCriteria.name);
    await local.setSetting('sort_ascending', settings.sortAscending);
  }
    @override
  Future<List<String>> getFavorites() async => local.getFavorites();

  @override
  Future<void> addFavorite(String path) => local.addFavorite(path);

  @override
  Future<void> removeFavorite(String path) => local.removeFavorite(path);

  @override
  Future<bool> isFavorite(String path) async => local.isFavorite(path);

  @override
  Future<List<String>> getRecent() async => local.getRecent();

  @override
  Future<void> addRecent(String path) => local.addRecent(path);

  @override
  Future<void> clearRecent() => local.clearRecent();

  // ---------- Funciones auxiliares de conversión enum <-> String ----------

  AppThemeMode _themeModeFromString(String? value) {
    switch (value) {
      case 'light':
        return AppThemeMode.light;
      case 'dark':
        return AppThemeMode.dark;
      default:
        return AppThemeMode.system;
    }
  }

  String _themeModeToString(AppThemeMode mode) {
    switch (mode) {
      case AppThemeMode.light:
        return 'light';
      case AppThemeMode.dark:
        return 'dark';
      case AppThemeMode.system:
        return 'system';
    }
  }

  SortCriteria _sortCriteriaFromString(String? value) {
    switch (value) {
      case 'date':
        return SortCriteria.date;
      case 'size':
        return SortCriteria.size;
      default:
        return SortCriteria.name;
    }
  }
}