import 'package:flutter/material.dart';
import 'package:path/path.dart' as p;
import '../../domain/entities/file_item.dart';
import '../../domain/repositories/file_repository.dart';

class FolderPickerScreen extends StatefulWidget {
  final String startPath;
  final FileRepository repository;

  const FolderPickerScreen({super.key, required this.startPath, required this.repository});

  @override
  State<FolderPickerScreen> createState() => _FolderPickerScreenState();

  static Future<String?> open(BuildContext context, {required FileRepository repository, required String startPath}) {
    return Navigator.push<String>(
      context,
      MaterialPageRoute(builder: (_) => FolderPickerScreen(startPath: startPath, repository: repository)),
    );
  }
}

class _FolderPickerScreenState extends State<FolderPickerScreen> {
  late String currentPath = widget.startPath;
  List<FileItem> _folders = [];
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    setState(() => _loading = true);
    final items = await widget.repository.listDirectory(currentPath);
    setState(() {
      _folders = items.where((f) => f.isDirectory).toList()
        ..sort((a, b) => a.name.toLowerCase().compareTo(b.name.toLowerCase()));
      _loading = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(p.basename(currentPath).isEmpty ? 'Elegir carpeta' : p.basename(currentPath))),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : ListView(
              children: [
                for (final folder in _folders)
                  ListTile(
                    leading: const Icon(Icons.folder_rounded),
                    title: Text(folder.name),
                    onTap: () {
                      setState(() => currentPath = folder.path);
                      _load();
                    },
                  ),
                if (_folders.isEmpty) const Padding(padding: EdgeInsets.all(24), child: Text('No hay subcarpetas aquí.')),
              ],
            ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () => Navigator.pop(context, currentPath),
        icon: const Icon(Icons.check_rounded),
        label: const Text('Elegir esta carpeta'),
      ),
    );
  }
}