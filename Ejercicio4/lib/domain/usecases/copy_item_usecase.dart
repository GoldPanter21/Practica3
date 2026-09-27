import '../entities/file_item.dart';
import '../repositories/file_repository.dart';

class CopyItemUseCase {
  final FileRepository repository;
  CopyItemUseCase(this.repository);

  Future<FileItem> call(String sourcePath, String destinationDir) {
    return repository.copyItem(sourcePath, destinationDir);
  }
}