import '../repositories/file_repository.dart';

class DeleteItemUseCase {
  final FileRepository repository;
  DeleteItemUseCase(this.repository);

  Future<void> call(String path) {
    return repository.deleteItem(path);
  }
}