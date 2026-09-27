import 'package:hive_flutter/hive_flutter.dart';

class LocalStorageDataSource {
  late Box _settingsBox;
  late Box _favoritesBox;
  late Box _recentBox;

  static const String maxRecentItems = 'items';

  Future<void> init() async {
    await Hive.initFlutter();
    _settingsBox = await Hive.openBox('settings_box');
    _favoritesBox = await Hive.openBox('favorites_box');
    _recentBox = await Hive.openBox('recent_box');
  }

  // ---------- Preferencias ----------

  T? getSetting<T>(String key) => _settingsBox.get(key) as T?;

  Future<void> setSetting(String key, dynamic value) =>
      _settingsBox.put(key, value);

  // ---------- Favoritos ----------

  List<String> getFavorites() => _favoritesBox.keys.cast<String>().toList();

  Future<void> addFavorite(String path) => _favoritesBox.put(path, true);

  Future<void> removeFavorite(String path) => _favoritesBox.delete(path);

  bool isFavorite(String path) => _favoritesBox.containsKey(path);

  // ---------- Recientes ----------

  List<String> getRecent() {
    final raw = _recentBox.get(maxRecentItems);
    if (raw == null) return [];
    return List<String>.from(raw as List);
  }

  Future<void> addRecent(String path) async {
    final list = getRecent();
    list.remove(path);
    list.insert(0, path);
    if (list.length > 30) {
      list.removeRange(30, list.length);
    }
    await _recentBox.put(maxRecentItems, list);
  }

  Future<void> clearRecent() => _recentBox.delete(maxRecentItems);
}