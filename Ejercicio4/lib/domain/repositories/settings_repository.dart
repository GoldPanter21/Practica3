import '../entities/app_settings.dart';

/// Contrato para persistencia local de preferencias, favoritos e historial
/// reciente. La implementación concreta usará Hive (la veremos en la
/// capa `data/`).
abstract class SettingsRepository {
  Future<AppSettings> loadSettings();
  Future<void> saveSettings(AppSettings settings);

  Future<List<String>> getFavorites();
  Future<void> addFavorite(String path);
  Future<void> removeFavorite(String path);
  Future<bool> isFavorite(String path);

  Future<List<String>> getRecent();
  Future<void> addRecent(String path);
  Future<void> clearRecent();
}