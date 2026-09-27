import '../entities/file_item.dart';

/// Ordena y filtra (búsqueda) una lista de FileItem ya obtenida.
/// No hace I/O: es lógica de negocio pura, por eso vive como caso de uso
/// independiente y no dentro del repositorio.
class SortAndFilterFilesUseCase {
  List<FileItem> call({
    required List<FileItem> items,
    required SortCriteria criteria,
    required bool ascending,
    String query = '',
  }) {
    var result = items;

    // 1. Filtrar por texto de búsqueda (si hay alguno).
    if (query.trim().isNotEmpty) {
      final q = query.toLowerCase();
      result = result.where((f) => f.name.toLowerCase().contains(q)).toList();
    }

    // 2. Copiamos la lista antes de ordenar, para no modificar la original.
    result = List<FileItem>.from(result);

    // 3. Ordenar.
    result.sort((a, b) {
      // Las carpetas siempre van primero, como en Archivos/Finder/Explorador.
      if (a.isDirectory != b.isDirectory) {
        return a.isDirectory ? -1 : 1;
      }

      int cmp;
      switch (criteria) {
        case SortCriteria.name:
          cmp = a.name.toLowerCase().compareTo(b.name.toLowerCase());
          break;
        case SortCriteria.date:
          cmp = a.modifiedAt.compareTo(b.modifiedAt);
          break;
        case SortCriteria.size:
          cmp = a.sizeBytes.compareTo(b.sizeBytes);
          break;
      }

      return ascending ? cmp : -cmp;
    });

    return result;
  }
}