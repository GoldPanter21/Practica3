import '../entities/file_item.dart';
import '../repositories/file_repository.dart';

/// Caso de uso: obtener el listado de archivos/carpetas de una ruta dada.
///
/// Depende solo de la ABSTRACCIÓN FileRepository, nunca de la
/// implementación concreta (dart:io). Esto permite probarlo en el futuro
/// con un repositorio falso (mock), sin tocar disco de verdad.
class ListDirectoryUseCase {
  final FileRepository repository;

  ListDirectoryUseCase(this.repository);

  /// Al llamar `listDirectoryUseCase(path)` como si fuera una función,
  /// gracias al método especial `call`.
  Future<List<FileItem>> call(String path) {
    return repository.listDirectory(path);
  }
}