import 'package:flutter/material.dart';
import 'package:open_filex/open_filex.dart';
import '../../domain/entities/file_item.dart';
import '../../domain/repositories/file_repository.dart';
import '../../domain/repositories/settings_repository.dart';

class RecentScreen extends StatefulWidget {
  final SettingsRepository settingsRepository;
  final FileRepository fileRepository;

  const RecentScreen({super.key, required this.settingsRepository, required this.fileRepository});

  @override
  State<RecentScreen> createState() => _RecentScreenState();
}

class _RecentScreenState extends State<RecentScreen> {
  List<FileItem> _items = [];
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    setState(() => _loading = true);
    final paths = await widget.settingsRepository.getRecent();
    final items = <FileItem>[];
    for (final path in paths) {
      try {
        items.add(await widget.fileRepository.statOf(path));
      } catch (_) {}
    }
    setState(() {
      _items = items;
      _loading = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Recientes'),
        actions: [
          IconButton(
            icon: const Icon(Icons.delete_sweep_rounded),
            onPressed: () async {
              await widget.settingsRepository.clearRecent();
              _load();
            },
          ),
        ],
      ),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : _items.isEmpty
              ? const Center(child: Text('Sin actividad reciente.'))
              : ListView.builder(
                  itemCount: _items.length,
                  itemBuilder: (context, index) {
                    final item = _items[index];
                    return ListTile(
                      leading: Icon(item.isDirectory ? Icons.folder_rounded : Icons.insert_drive_file_rounded),
                      title: Text(item.name),
                      onTap: () => OpenFilex.open(item.path),
                    );
                  },
                ),
    );
  }
}