import 'package:flutter/material.dart';
import '../../domain/entities/file_item.dart';
import '../../domain/repositories/file_repository.dart';

class TextViewerScreen extends StatefulWidget {
  final FileItem item;
  final FileRepository fileRepository;

  const TextViewerScreen({super.key, required this.item, required this.fileRepository});

  @override
  State<TextViewerScreen> createState() => _TextViewerScreenState();
}

class _TextViewerScreenState extends State<TextViewerScreen> {
  final _controller = TextEditingController();
  bool _loading = true;
  bool _dirty = false;
  String? _error;

  @override
  void initState() {
    super.initState();
    _controller.addListener(() => setState(() => _dirty = true));
    _load();
  }

  Future<void> _load() async {
    try {
      _controller.text = await widget.fileRepository.readTextFile(widget.item.path);
      _dirty = false;
    } catch (_) {
      _error = 'No fue posible abrir este archivo: tipo no soportado o corrupto.';
    }
    setState(() => _loading = false);
  }

  Future<void> _save() async {
    await widget.fileRepository.writeTextFile(widget.item.path, _controller.text);
    setState(() => _dirty = false);
    if (mounted) ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Guardado.')));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.item.name, overflow: TextOverflow.ellipsis),
        actions: [if (_dirty) IconButton(icon: const Icon(Icons.save_rounded), onPressed: _save)],
      ),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : _error != null
              ? Center(child: Padding(padding: const EdgeInsets.all(24), child: Text(_error!)))
              : Padding(
                  padding: const EdgeInsets.all(16),
                  child: TextField(
                    controller: _controller,
                    maxLines: null,
                    expands: true,
                    style: const TextStyle(fontFamily: 'monospace'),
                    decoration: const InputDecoration(border: InputBorder.none),
                  ),
                ),
    );
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }
}