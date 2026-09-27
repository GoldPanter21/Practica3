import 'dart:io';
import 'package:mime/mime.dart';
import 'package:path/path.dart' as p;
import 'package:path_provider/path_provider.dart';
import '../../domain/entities/file_item.dart';
import 'package:file_picker/file_picker.dart';
import 'package:share_plus/share_plus.dart';

/// Excepción de dominio para archivos inaccesibles, corruptos o de tipo
/// no soportado. La capturamos en la pantalla para mostrar mensajes
/// claros al usuario, en vez de dejar que se rompa la app con un error
/// crudo de dart:io.
class FileAccessException implements Exception {
  final String message;
  FileAccessException(this.message);

  @override
  String toString() => message;
}

/// Datasource de bajo nivel: única capa que toca dart:io y plugins de
/// acceso a disco. Traduce entre el sistema de archivos real y las
/// entidades de dominio (FileItem).
class FileSystemDataSource {
  /// Devuelve las carpetas raíz accesibles, similar a Documents/Inbox/tmp
  /// en iOS. path_provider siempre entrega rutas dentro del contenedor
  /// privado de la app (el "sandbox"), por lo que nunca navegamos fuera
  /// de esos directorios.
  Future<Map<String, String>> getRootDirectories() async {
    final Map<String, String> roots = {};

    final docsDir = await getApplicationDocumentsDirectory();
    await docsDir.create(recursive: true);
    roots['Documents'] = docsDir.path;

    final tempDir = await getTemporaryDirectory();
    roots['tmp'] = tempDir.path;

    return roots;
  }
    Future<List<FileItem>> listDirectory(String directoryPath) async {
    final dir = Directory(directoryPath);
    if (!await dir.exists()) {
      throw FileAccessException('La carpeta no existe o fue eliminada.');
    }

    final List<FileItem> items = [];
    await for (final entity in dir.list(followLinks: false)) {
      try {
        items.add(await _toFileItem(entity));
      } catch (_) {
        // Ignoramos archivos individuales corruptos/inaccesibles,
        // sin interrumpir el listado completo.
        continue;
      }
    }
    return items;
  }

  /// Convierte un FileSystemEntity (File o Directory) en nuestra entidad
  /// de dominio FileItem.
  Future<FileItem> _toFileItem(FileSystemEntity entity) async {
    final stat = await entity.stat();
    final isDir = stat.type == FileSystemEntityType.directory;
    final name = p.basename(entity.path);

    return FileItem(
      path: entity.path,
      name: name,
      isDirectory: isDir,
      sizeBytes: isDir ? 0 : stat.size,
      modifiedAt: stat.modified,
      category: isDir ? FileCategory.folder : _categoryFor(name),
    );
  }

  /// Clasifica el archivo según su extensión / tipo MIME — juega el
  /// mismo papel que UTType en la versión nativa de iOS.
  FileCategory _categoryFor(String name) {
    final mimeType = lookupMimeType(name) ?? '';
    final ext = p.extension(name).replaceFirst('.', '').toLowerCase();

    if (mimeType.startsWith('image/')) return FileCategory.image;
    if (mimeType.startsWith('audio/')) return FileCategory.audio;
    if (mimeType.startsWith('video/')) return FileCategory.video;
    if (ext == 'pdf') return FileCategory.pdf;
    if (['zip', 'rar', '7z', 'tar', 'gz'].contains(ext)) {
      return FileCategory.archive;
    }
    if (['txt', 'md', 'json', 'log', 'csv'].contains(ext)) {
      return FileCategory.text;
    }
    if (['dart', 'swift', 'java', 'kt', 'py', 'js', 'html', 'css'].contains(ext)) {
      return FileCategory.code;
    }
    return FileCategory.other;
  }
    Future<String> readTextFile(String path) async {
    final file = File(path);
    if (!await file.exists()) {
      throw FileAccessException('El archivo no existe.');
    }
    try {
      return await file.readAsString();
    } catch (_) {
      throw FileAccessException(
          'No se pudo leer el archivo: formato no soportado o corrupto.');
    }
  }

  Future<void> writeTextFile(String path, String content) async {
    final file = File(path);
    await file.writeAsString(content);
  }
    Future<FileItem> createFolder(String parentPath, String folderName) async {
    final newDir = Directory(p.join(parentPath, folderName));
    if (await newDir.exists()) {
      throw FileAccessException('Ya existe una carpeta con ese nombre.');
    }
    await newDir.create(recursive: true);
    return _toFileItem(newDir);
  }
    Future<FileItem> renameItem(String path, String newName) async {
    final parent = p.dirname(path);
    final newPath = p.join(parent, newName);

    if (await File(newPath).exists() || await Directory(newPath).exists()) {
      throw FileAccessException('Ya existe un elemento con ese nombre.');
    }

    final isDir = await Directory(path).exists();
    final FileSystemEntity renamed = isDir
        ? await Directory(path).rename(newPath)
        : await File(path).rename(newPath);

    return _toFileItem(renamed);
  }
    Future<void> deleteItem(String path) async {
    final isDir = await Directory(path).exists();
    if (isDir) {
      await Directory(path).delete(recursive: true);
    } else {
      final file = File(path);
      if (await file.exists()) await file.delete();
    }
  }
    Future<FileItem> copyItem(String sourcePath, String destinationDir) async {
    final isDir = await Directory(sourcePath).exists();

    if (isDir) {
      final newDirPath = p.join(destinationDir, p.basename(sourcePath));
      await _copyDirectoryRecursive(Directory(sourcePath), Directory(newDirPath));
      return _toFileItem(Directory(newDirPath));
    } else {
      final newFilePath = p.join(destinationDir, p.basename(sourcePath));
      final newFile = await File(sourcePath).copy(newFilePath);
      return _toFileItem(newFile);
    }
  }

  /// dart:io no trae un método nativo para copiar carpetas completas,
  /// así que lo hacemos manualmente: creamos la carpeta destino y
  /// copiamos cada elemento interno, llamándonos a nosotros mismos
  /// (recursión) cuando encontramos una subcarpeta.
  Future<void> _copyDirectoryRecursive(Directory src, Directory dst) async {
    await dst.create(recursive: true);
    await for (final entity in src.list(recursive: false)) {
      final newPath = p.join(dst.path, p.basename(entity.path));
      if (entity is Directory) {
        await _copyDirectoryRecursive(entity, Directory(newPath));
      } else if (entity is File) {
        await entity.copy(newPath);
      }
    }
  }
    Future<FileItem> moveItem(String sourcePath, String destinationDir) async {
    final newPath = p.join(destinationDir, p.basename(sourcePath));
    final isDir = await Directory(sourcePath).exists();

    final FileSystemEntity moved = isDir
        ? await Directory(sourcePath).rename(newPath)
        : await File(sourcePath).rename(newPath);

    return _toFileItem(moved);
  }
    /// Abre el selector nativo de archivos (Archivos/iCloud en iOS,
  /// selector del sistema en Android) y copia lo elegido al sandbox
  /// de la app, dentro de destinationDir.
      Future<List<FileItem>> importFiles(String destinationDir) async {
    final List<PlatformFile> pickedFiles = await FilePicker.pickFiles();

    final List<FileItem> imported = [];
    for (final picked in pickedFiles) {
      if (picked.path == null) continue;
      final destPath = p.join(destinationDir, picked.name);
      final copied = await File(picked.path!).copy(destPath);
      imported.add(await _toFileItem(copied));
    }
    return imported;
  }
    Future<void> shareFile(String path) async {
    final file = File(path);
    if (!await file.exists()) {
      throw FileAccessException('El archivo ya no existe.');
    }
    await Share.shareXFiles([XFile(path)]);
  }
    /// Obtiene la información actualizada de un archivo/carpeta puntual
  /// dada su ruta. Lo usaremos, por ejemplo, para refrescar los datos
  /// de un favorito guardado (verificar que siga existiendo, tamaño
  /// actual, etc.).
  Future<FileItem> statOf(String path) async {
    final entity = FileSystemEntity.typeSync(path) == FileSystemEntityType.directory
        ? Directory(path)
        : File(path);
    return _toFileItem(entity);
  }

}