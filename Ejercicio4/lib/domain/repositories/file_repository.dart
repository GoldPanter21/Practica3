import '../entities/file_item.dart';

/// Contrato que define las operaciones de negocio sobre el sistema de
/// archivos, sin exponer detalles de dart:io. La capa de dominio depende
/// únicamente de esta abstracción; la implementación concreta vivirá en
/// data/repositories/file_repository_impl.dart.
abstract class FileRepository {
  /// Rutas raíz accesibles dentro del "sandbox" de la app
  /// (equivalente a Documents / Inbox / tmp en iOS).
  Future<Map<String, String>> getRootDirectories();

  /// Lista el contenido de una carpeta.
  Future<List<FileItem>> listDirectory(String directoryPath);

  /// Lee el contenido de texto de un archivo.
  Future<String> readTextFile(String path);

  Future<void> writeTextFile(String path, String content);

  Future<FileItem> createFolder(String parentPath, String folderName);

  Future<FileItem> renameItem(String path, String newName);

  Future<FileItem> copyItem(String sourcePath, String destinationDir);

  Future<FileItem> moveItem(String sourcePath, String destinationDir);

  Future<void> deleteItem(String path);

  Future<List<FileItem>> importFiles(String destinationDir);

  Future<void> shareFile(String path);

  Future<FileItem> statOf(String path);
}