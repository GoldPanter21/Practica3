/// Categorías de archivo, usadas para elegir el ícono y el visor adecuado.
enum FileCategory {
  folder,
  text,
  image,
  pdf,
  audio,
  video,
  archive,
  code,
  other,
}

/// Entidad de dominio que representa un archivo o carpeta.
/// No sabe nada de Flutter ni de dart:io — es un objeto de negocio puro.
class FileItem {
  final String path;
  final String name;
  final bool isDirectory;
  final int sizeBytes;
  final DateTime modifiedAt;
  final FileCategory category;

  const FileItem({
    required this.path,
    required this.name,
    required this.isDirectory,
    required this.sizeBytes,
    required this.modifiedAt,
    required this.category,
  });
}

/// Criterios de ordenamiento disponibles en la lista de archivos.
enum SortCriteria { name, date, size }