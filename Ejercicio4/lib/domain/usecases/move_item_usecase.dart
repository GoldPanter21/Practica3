import '../entities/file_item.dart';
import '../repositories/file_repository.dart';

class MoveItemUseCase {
  final FileRepository repository;
  MoveItemUseCase(this.repository);

  Future<FileItem> call(String sourcePath, String destinationDir) {
    return repository.moveItem(sourcePath, destinationDir);
  }
}