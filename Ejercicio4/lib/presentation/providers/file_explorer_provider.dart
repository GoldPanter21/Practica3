import 'package:flutter/foundation.dart';
import '../../data/datasources/file_system_datasource.dart';
import '../../domain/entities/file_item.dart';
import '../../domain/repositories/file_repository.dart';
import '../../domain/repositories/settings_repository.dart';
import '../../domain/usecases/list_directory_usecase.dart';
import '../../domain/usecases/create_folder_usecase.dart';
import '../../domain/usecases/rename_item_usecase.dart';
import '../../domain/usecases/delete_item_usecase.dart';
import '../../domain/usecases/copy_item_usecase.dart';
import '../../domain/usecases/move_item_usecase.dart';
import '../../domain/usecases/sort_and_filter_files_usecase.dart';

/// Estados posibles de la pantalla del explorador.
enum ExplorerStatus { loading, ready, error }

class FileExplorerProvider extends ChangeNotifier {
  // Casos de uso inyectados (no creamos nada aquí, todo llega de afuera).
  final ListDirectoryUseCase listDirectory;
  final CreateFolderUseCase createFolder;
  final RenameItemUseCase renameItem;
  final DeleteItemUseCase deleteItem;
  final CopyItemUseCase copyItem;
  final MoveItemUseCase moveItem;
  final SortAndFilterFilesUseCase sortAndFilter;
  final FileRepository fileRepository; // para importar/compartir directo
  final SettingsRepository settingsRepository; // para guardar "recientes"

  FileExplorerProvider({
    required this.listDirectory,
    required this.createFolder,
    required this.renameItem,
    required this.deleteItem,
    required this.copyItem,
    required this.moveItem,
    required this.sortAndFilter,
    required this.fileRepository,
    required this.settingsRepository,
  });

  // ----- Estado -----
  ExplorerStatus status = ExplorerStatus.loading;
  String errorMessage = '';

  String currentPath = '';
  String rootLabel = 'Documents';

  List<FileItem> _rawItems = [];
  List<FileItem> visibleItems = [];

  String searchQuery = '';
  SortCriteria sortCriteria = SortCriteria.name;
  bool sortAscending = true;

  /// Pila de rutas visitadas, para la navegación jerárquica (breadcrumb
  /// y botón "atrás").
  final List<String> _pathStack = [];
  List<String> get pathStack => List.unmodifiable(_pathStack);

  bool get canGoBack => _pathStack.length > 1;
    /// Se llama UNA VEZ al iniciar el explorador: recibe la ruta inicial
  /// (normalmente "Documents") y carga su contenido.
  Future<void> initRoots(String startPath, {String rootLabel = 'Documents'}) async {
    this.rootLabel = rootLabel;
    currentPath = startPath;
    _pathStack
      ..clear()
      ..add(startPath);
    await refresh();
  }

  /// Entra a una carpeta: la agrega a la pila de navegación y recarga.
  Future<void> openFolder(FileItem folder) async {
    if (!folder.isDirectory) return;
    _pathStack.add(folder.path);
    currentPath = folder.path;
    await refresh();
  }

  /// Navega directamente a una posición del breadcrumb (por su índice
  /// en la pila), descartando todo lo que estaba "más adentro".
  Future<void> goToBreadcrumb(int index) async {
    if (index < 0 || index >= _pathStack.length) return;
    _pathStack.removeRange(index + 1, _pathStack.length);
    currentPath = _pathStack.last;
    await refresh();
  }

  /// Retrocede un nivel (equivalente al botón "atrás").
  Future<void> goBack() async {
    if (!canGoBack) return;
    _pathStack.removeLast();
    currentPath = _pathStack.last;
    await refresh();
  }

  /// Recarga el contenido de la carpeta actual desde disco.
  Future<void> refresh() async {
    status = ExplorerStatus.loading;
    notifyListeners();

    try {
      _rawItems = await listDirectory(currentPath);
      await settingsRepository.addRecent(currentPath);
      _applyFilters();
      status = ExplorerStatus.ready;
    } on FileAccessException catch (e) {
      errorMessage = e.message;
      status = ExplorerStatus.error;
    } catch (e) {
      errorMessage = 'Ocurrió un error inesperado al leer la carpeta.';
      status = ExplorerStatus.error;
    }

    notifyListeners();
  }

  /// Aplica el ordenamiento y la búsqueda actuales sobre los datos
  /// crudos ya cargados (sin volver a leer el disco).
  void _applyFilters() {
    visibleItems = sortAndFilter(
      items: _rawItems,
      criteria: sortCriteria,
      ascending: sortAscending,
      query: searchQuery,
    );
  }
    // ----- Búsqueda y ordenamiento -----

  void setSearchQuery(String query) {
    searchQuery = query;
    _applyFilters();
    notifyListeners();
  }

  void setSortCriteria(SortCriteria criteria, {bool? ascending}) {
    sortCriteria = criteria;
    if (ascending != null) sortAscending = ascending;
    _applyFilters();
    notifyListeners();
  }

  void toggleSortDirection() {
    sortAscending = !sortAscending;
    _applyFilters();
    notifyListeners();
  }

  // ----- Operaciones de gestión de archivos -----

  Future<void> createNewFolder(String name) async {
    await createFolder(currentPath, name);
    await refresh();
  }

  Future<void> rename(FileItem item, String newName) async {
    await renameItem(item.path, newName);
    await refresh();
  }

  Future<void> delete(FileItem item) async {
    await deleteItem(item.path);
    await refresh();
  }

  Future<void> copyInto(FileItem item, String destinationDir) async {
    await copyItem(item.path, destinationDir);
    if (destinationDir == currentPath) await refresh();
  }

  Future<void> moveInto(FileItem item, String destinationDir) async {
    await moveItem(item.path, destinationDir);
    await refresh();
  }

  Future<int> import() async {
    final imported = await fileRepository.importFiles(currentPath);
    await refresh();
    return imported.length;
  }

  Future<void> share(FileItem item) async {
    await fileRepository.shareFile(item.path);
  }
}