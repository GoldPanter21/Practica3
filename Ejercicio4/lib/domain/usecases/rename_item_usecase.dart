import '../entities/file_item.dart';
import '../repositories/file_repository.dart';

class RenameItemUseCase {
  final FileRepository repository;
  RenameItemUseCase(this.repository);

  Future<FileItem> call(String path, String newName) {
    return repository.renameItem(path, newName);
  }
}