import '../../domain/entities/file_item.dart';
import '../../domain/repositories/file_repository.dart';
import '../datasources/file_system_datasource.dart';

/// Implementación concreta del contrato FileRepository.
///
/// Es la única "bisagra" entre el dominio (que no conoce dart:io ni
/// plugins) y el datasource concreto. Si algún día quisiéramos cambiar
/// de motor de archivos, solo se toca esta clase, sin afectar a los
/// casos de uso ni a la interfaz de usuario.
class FileRepositoryImpl implements FileRepository {
  final FileSystemDataSource dataSource;

  FileRepositoryImpl(this.dataSource);

  @override
  Future<Map<String, String>> getRootDirectories() =>
      dataSource.getRootDirectories();

  @override
  Future<List<FileItem>> listDirectory(String directoryPath) =>
      dataSource.listDirectory(directoryPath);

  @override
  Future<String> readTextFile(String path) => dataSource.readTextFile(path);

  @override
  Future<void> writeTextFile(String path, String content) =>
      dataSource.writeTextFile(path, content);

  @override
  Future<FileItem> createFolder(String parentPath, String folderName) =>
      dataSource.createFolder(parentPath, folderName);

  @override
  Future<FileItem> renameItem(String path, String newName) =>
      dataSource.renameItem(path, newName);

  @override
  Future<FileItem> copyItem(String sourcePath, String destinationDir) =>
      dataSource.copyItem(sourcePath, destinationDir);

  @override
  Future<FileItem> moveItem(String sourcePath, String destinationDir) =>
      dataSource.moveItem(sourcePath, destinationDir);

  @override
  Future<void> deleteItem(String path) => dataSource.deleteItem(path);

  @override
  Future<List<FileItem>> importFiles(String destinationDir) =>
      dataSource.importFiles(destinationDir);

  @override
  Future<void> shareFile(String path) => dataSource.shareFile(path);

  @override
  Future<FileItem> statOf(String path) => dataSource.statOf(path);
}