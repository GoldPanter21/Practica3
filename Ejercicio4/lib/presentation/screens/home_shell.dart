import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../domain/repositories/file_repository.dart';
import '../../domain/repositories/settings_repository.dart';
import '../providers/favorites_provider.dart';
import 'favorites_screen.dart';
import 'file_explorer_screen.dart';
import 'recent_screen.dart';
import 'settings_screen.dart';

class HomeShell extends StatefulWidget {
  final FileRepository fileRepository;
  final SettingsRepository settingsRepository;

  const HomeShell({super.key, required this.fileRepository, required this.settingsRepository});

  @override
  State<HomeShell> createState() => _HomeShellState();
}

class _HomeShellState extends State<HomeShell> {
  int _index = 0;
  final _recentKey = GlobalKey<RecentScreenState>();

  void _onDestinationSelected(int i) {
    setState(() => _index = i);
    // Al entrar a "Recientes" o "Favoritos", forzamos una recarga,
    // ya que IndexedStack mantiene las pantallas vivas y no vuelve a
    // llamar initState() al cambiar de pestaña.
    if (i == 1) context.read<FavoritesProvider>().load();
    if (i == 2) _recentKey.currentState?.reload();
  }

  @override
  Widget build(BuildContext context) {
    final screens = [
      FileExplorerScreen(fileRepository: widget.fileRepository),
      const FavoritesScreen(),
      RecentScreen(
        key: _recentKey,
        settingsRepository: widget.settingsRepository,
        fileRepository: widget.fileRepository,
      ),
      const SettingsScreen(),
    ];

    return Scaffold(
      body: IndexedStack(index: _index, children: screens),
      bottomNavigationBar: NavigationBar(
        selectedIndex: _index,
        onDestinationSelected: _onDestinationSelected,
        destinations: const [
          NavigationDestination(icon: Icon(Icons.folder_rounded), label: 'Archivos'),
          NavigationDestination(icon: Icon(Icons.star_rounded), label: 'Favoritos'),
          NavigationDestination(icon: Icon(Icons.history_rounded), label: 'Recientes'),
          NavigationDestination(icon: Icon(Icons.settings_rounded), label: 'Ajustes'),
        ],
      ),
    );
  }
}