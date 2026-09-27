import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../../domain/entities/file_item.dart';

class FileUtils {
  FileUtils._();

  /// Convierte bytes a un texto legible: "2.3 MB", "512 KB", etc.
  static String formatSize(int bytes) {
    if (bytes <= 0) return '—';
    const units = ['B', 'KB', 'MB', 'GB'];
    double size = bytes.toDouble();
    int unitIndex = 0;
    while (size >= 1024 && unitIndex < units.length - 1) {
      size /= 1024;
      unitIndex++;
    }
    return '${size.toStringAsFixed(unitIndex > 0 ? 1 : 0)} ${units[unitIndex]}';
  }

  static String formatDate(DateTime date) {
    return DateFormat('dd/MM/yyyy HH:mm').format(date);
  }

  /// Ícono representativo según la categoría del archivo — equivalente
  /// funcional a mapear un UTType a un símbolo, como pedía el ejercicio
  /// original de iOS.
  static IconData iconFor(FileItem item) {
    if (item.isDirectory) return Icons.folder_rounded;
    switch (item.category) {
      case FileCategory.text:
        return Icons.description_rounded;
      case FileCategory.image:
        return Icons.image_rounded;
      case FileCategory.pdf:
        return Icons.picture_as_pdf_rounded;
      case FileCategory.audio:
        return Icons.audiotrack_rounded;
      case FileCategory.video:
        return Icons.movie_rounded;
      case FileCategory.archive:
        return Icons.folder_zip_rounded;
      case FileCategory.code:
        return Icons.code_rounded;
      case FileCategory.folder:
        return Icons.folder_rounded;
      case FileCategory.other:
        return Icons.insert_drive_file_rounded;
    }
  }
}