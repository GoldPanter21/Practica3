import '../entities/file_item.dart';
import '../repositories/file_repository.dart';

class CreateFolderUseCase {
  final FileRepository repository;
  CreateFolderUseCase(this.repository);

  Future<FileItem> call(String parentPath, String folderName) {
    return repository.createFolder(parentPath, folderName);
  }
}