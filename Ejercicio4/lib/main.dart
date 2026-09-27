import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'core/theme/app_theme.dart';

import 'data/datasources/file_system_datasource.dart';
import 'data/datasources/local_storage_datasource.dart';
import 'data/repositories/file_repository_impl.dart';
import 'data/repositories/settings_repository_impl.dart';

import 'domain/repositories/file_repository.dart';
import 'domain/repositories/settings_repository.dart';
import 'domain/usecases/list_directory_usecase.dart';
import 'domain/usecases/create_folder_usecase.dart';
import 'domain/usecases/rename_item_usecase.dart';
import 'domain/usecases/delete_item_usecase.dart';
import 'domain/usecases/copy_item_usecase.dart';
import 'domain/usecases/move_item_usecase.dart';
import 'domain/usecases/sort_and_filter_files_usecase.dart';

import 'presentation/providers/theme_provider.dart';
import 'presentation/providers/file_explorer_provider.dart';
import 'presentation/providers/favorites_provider.dart';
import 'presentation/screens/home_shell.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // ===================================================================
  // COMPOSICIÓN DE DEPENDENCIAS (Clean Architecture)
  // Único lugar de la app donde se "arman" manualmente todas las capas.
  // ===================================================================

  // --- Datasources ---
  final localStorage = LocalStorageDataSource();
  await localStorage.init();
  final fileSystemDataSource = FileSystemDataSource();

  // --- Repositorios (implementaciones concretas) ---
  final SettingsRepository settingsRepository =
      SettingsRepositoryImpl(localStorage);
  final FileRepository fileRepository =
      FileRepositoryImpl(fileSystemDataSource);

  // --- Casos de uso ---
  final listDirectoryUseCase = ListDirectoryUseCase(fileRepository);
  final createFolderUseCase = CreateFolderUseCase(fileRepository);
  final renameItemUseCase = RenameItemUseCase(fileRepository);
  final deleteItemUseCase = DeleteItemUseCase(fileRepository);
  final copyItemUseCase = CopyItemUseCase(fileRepository);
  final moveItemUseCase = MoveItemUseCase(fileRepository);
  final sortAndFilterUseCase = SortAndFilterFilesUseCase();

  // --- Providers ---
  final themeProvider = ThemeProvider(settingsRepository);
  await themeProvider.loadFromStorage();

  final fileExplorerProvider = FileExplorerProvider(
    listDirectory: listDirectoryUseCase,
    createFolder: createFolderUseCase,
    renameItem: renameItemUseCase,
    deleteItem: deleteItemUseCase,
    copyItem: copyItemUseCase,
    moveItem: moveItemUseCase,
    sortAndFilter: sortAndFilterUseCase,
    fileRepository: fileRepository,
    settingsRepository: settingsRepository,
  );

  final favoritesProvider =
      FavoritesProvider(settingsRepository, fileRepository);

  // --- Carpeta inicial: Documents del sandbox ---
  final roots = await fileRepository.getRootDirectories();
  final startPath = roots['Documents']!;

  await fileExplorerProvider.initRoots(startPath, rootLabel: 'Documents');
  await favoritesProvider.load();

  runApp(MyApp(
    themeProvider: themeProvider,
    fileExplorerProvider: fileExplorerProvider,
    favoritesProvider: favoritesProvider,
    fileRepository: fileRepository,
    settingsRepository: settingsRepository,
  ));
}

class MyApp extends StatelessWidget {
  final ThemeProvider themeProvider;
  final FileExplorerProvider fileExplorerProvider;
  final FavoritesProvider favoritesProvider;
  final FileRepository fileRepository;
  final SettingsRepository settingsRepository;

  const MyApp({
    super.key,
    required this.themeProvider,
    required this.fileExplorerProvider,
    required this.favoritesProvider,
    required this.fileRepository,
    required this.settingsRepository,
  });

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        ChangeNotifierProvider.value(value: themeProvider),
        ChangeNotifierProvider.value(value: fileExplorerProvider),
        ChangeNotifierProvider.value(value: favoritesProvider),
      ],
      child: Consumer<ThemeProvider>(
        builder: (context, theme, _) {
          return MaterialApp(
            title: 'Gestor de Archivos',
            debugShowCheckedModeBanner: false,
            themeMode: theme.themeMode,
            theme: AppTheme.light(theme.colorScheme),
            darkTheme: AppTheme.dark(theme.colorScheme),
            home: HomeShell(
              fileRepository: fileRepository,
              settingsRepository: settingsRepository,
            ),
          );
        },
      ),
    );
  }
}