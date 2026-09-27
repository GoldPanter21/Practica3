import 'package:flutter/material.dart';
import 'package:open_filex/open_filex.dart';
import 'package:provider/provider.dart';
import '../../domain/entities/file_item.dart';
import '../../domain/repositories/file_repository.dart';
import '../providers/favorites_provider.dart';
import '../providers/file_explorer_provider.dart';
import '../widgets/breadcrumb_bar.dart';
import '../widgets/file_tile.dart';
import '../widgets/name_input_dialog.dart';
import '../widgets/sort_options_sheet.dart';
import 'folder_picker_screen.dart';
import 'image_viewer_screen.dart';
import 'text_viewer_screen.dart';

class FileExplorerScreen extends StatefulWidget {
  final FileRepository fileRepository;
  const FileExplorerScreen({super.key, required this.fileRepository});

  @override
  State<FileExplorerScreen> createState() => _FileExplorerScreenState();
}

class _FileExplorerScreenState extends State<FileExplorerScreen> {
  bool _searching = false;
  final _searchController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    final explorer = context.watch<FileExplorerProvider>();
    final favorites = context.watch<FavoritesProvider>();

    return Scaffold(
      appBar: AppBar(
        leading: explorer.canGoBack
            ? IconButton(icon: const Icon(Icons.arrow_back_rounded), onPressed: explorer.goBack)
            : null,
        title: _searching
            ? TextField(
                controller: _searchController,
                autofocus: true,
                decoration: const InputDecoration(hintText: 'Buscar...', border: InputBorder.none),
                onChanged: explorer.setSearchQuery,
              )
            : const Text('Gestor de Archivos'),
        actions: [
          IconButton(
            icon: Icon(_searching ? Icons.close_rounded : Icons.search_rounded),
            onPressed: () {
              setState(() => _searching = !_searching);
              if (!_searching) {
                _searchController.clear();
                explorer.setSearchQuery('');
              }
            },
          ),
          IconButton(
            icon: const Icon(Icons.sort_rounded),
            onPressed: () => SortOptionsSheet.show(
              context,
              current: explorer.sortCriteria,
              ascending: explorer.sortAscending,
              onSelect: (criteria, asc) => explorer.setSortCriteria(criteria, ascending: asc),
            ),
          ),
        ],
        bottom: PreferredSize(
          preferredSize: const Size.fromHeight(40),
          child: BreadcrumbBar(
            pathStack: explorer.pathStack,
            rootLabel: explorer.rootLabel,
            onTapSegment: explorer.goToBreadcrumb,
          ),
        ),
      ),
      body: RefreshIndicator(
        onRefresh: explorer.refresh,
        child: _buildBody(explorer, favorites),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => _showCreateMenu(context, explorer),
        child: const Icon(Icons.add_rounded),
      ),
    );
  }

  Widget _buildBody(FileExplorerProvider explorer, FavoritesProvider favorites) {
    if (explorer.status == ExplorerStatus.loading) {
      return const Center(child: CircularProgressIndicator());
    }
    if (explorer.status == ExplorerStatus.error) {
      return ListView(children: [
        const SizedBox(height: 100),
        Center(child: Text(explorer.errorMessage, textAlign: TextAlign.center)),
      ]);
    }
    if (explorer.visibleItems.isEmpty) {
      return ListView(children: const [
        SizedBox(height: 100),
        Center(child: Text('Esta carpeta está vacía.')),
      ]);
    }
    return ListView.builder(
      itemCount: explorer.visibleItems.length,
      itemBuilder: (context, index) {
        final item = explorer.visibleItems[index];
        return FileTile(
          item: item,
          isFavorite: favorites.isFavorite(item.path),
          onTap: () => _openItem(explorer, item),
          onDelete: () => explorer.delete(item),
          onRename: () => _rename(explorer, item),
          onCopy: () => _copyOrMove(explorer, item, isCopy: true),
          onMove: () => _copyOrMove(explorer, item, isCopy: false),
          onShare: () => explorer.share(item),
          onToggleFavorite: () => favorites.toggle(item),
        );
      },
    );
  }

  void _showCreateMenu(BuildContext context, FileExplorerProvider explorer) {
    showModalBottomSheet(
      context: context,
      showDragHandle: true,
      builder: (ctx) => SafeArea(
        child: Wrap(children: [
          ListTile(
            leading: const Icon(Icons.create_new_folder_rounded),
            title: const Text('Nueva carpeta'),
            onTap: () async {
              Navigator.pop(ctx);
              final name = await NameInputDialog.show(context, title: 'Nueva carpeta', confirmLabel: 'Crear');
              if (name != null) await explorer.createNewFolder(name);
            },
          ),
          ListTile(
            leading: const Icon(Icons.file_upload_rounded),
            title: const Text('Importar archivo(s)'),
            onTap: () async {
              Navigator.pop(ctx);
              final count = await explorer.import();
              if (context.mounted) {
                ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('$count archivo(s) importado(s).')));
              }
            },
          ),
        ]),
      ),
    );
  }

  Future<void> _rename(FileExplorerProvider explorer, FileItem item) async {
    final newName = await NameInputDialog.show(context, title: 'Renombrar', initialValue: item.name, confirmLabel: 'Guardar');
    if (newName != null && newName != item.name) await explorer.rename(item, newName);
  }

  Future<void> _copyOrMove(FileExplorerProvider explorer, FileItem item, {required bool isCopy}) async {
    final destination = await FolderPickerScreen.open(
      context,
      repository: widget.fileRepository,
      startPath: explorer.pathStack.first,
    );
    if (destination == null) return;
    if (isCopy) {
      await explorer.copyInto(item, destination);
    } else {
      await explorer.moveInto(item, destination);
    }
  }

  void _openItem(FileExplorerProvider explorer, FileItem item) {
    if (item.isDirectory) {
      explorer.openFolder(item);
      return;
    }
    if (item.category == FileCategory.text || item.category == FileCategory.code) {
      Navigator.push(context, MaterialPageRoute(
        builder: (_) => TextViewerScreen(item: item, fileRepository: widget.fileRepository),
      ));
    } else if (item.category == FileCategory.image) {
      Navigator.push(context, MaterialPageRoute(builder: (_) => ImageViewerScreen(item: item)));
    } else {
      OpenFilex.open(item.path);
    }
  }

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }
}