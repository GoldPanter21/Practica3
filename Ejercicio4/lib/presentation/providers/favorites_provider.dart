import 'package:flutter/foundation.dart';
import '../../domain/entities/file_item.dart';
import '../../domain/repositories/file_repository.dart';
import '../../domain/repositories/settings_repository.dart';

/// Mantiene la lista persistente de rutas favoritas y resuelve sus
/// FileItem actuales (por si cambiaron de tamaño/fecha desde que se
/// marcaron como favoritos, o si fueron eliminados).
class FavoritesProvider extends ChangeNotifier {
  final SettingsRepository settingsRepository;
  final FileRepository fileRepository;

  FavoritesProvider(this.settingsRepository, this.fileRepository);

  List<FileItem> favorites = [];
  bool isLoading = true;

  /// Carga (o recarga) la lista de favoritos desde Hive, y consulta el
  /// estado ACTUAL de cada archivo en disco (por si cambió o fue borrado).
  Future<void> load() async {
    isLoading = true;
    notifyListeners();

    final paths = await settingsRepository.getFavorites();
    final items = <FileItem>[];

    for (final path in paths) {
      try {
        items.add(await fileRepository.statOf(path));
      } catch (_) {
        // El archivo pudo haber sido borrado externamente (o desde
        // el explorador). Lo quitamos silenciosamente de favoritos.
        await settingsRepository.removeFavorite(path);
      }
    }

    favorites = items;
    isLoading = false;
    notifyListeners();
  }

  bool isFavorite(String path) => favorites.any((f) => f.path == path);

  Future<void> toggle(FileItem item) async {
    if (isFavorite(item.path)) {
      await settingsRepository.removeFavorite(item.path);
    } else {
      await settingsRepository.addFavorite(item.path);
    }
    await load();
  }
}