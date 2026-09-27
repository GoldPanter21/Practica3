import 'package:flutter/material.dart';

class NameInputDialog extends StatefulWidget {
  final String title;
  final String initialValue;
  final String confirmLabel;

  const NameInputDialog({
    super.key,
    required this.title,
    this.initialValue = '',
    this.confirmLabel = 'Aceptar',
  });

  static Future<String?> show(
    BuildContext context, {
    required String title,
    String initialValue = '',
    String confirmLabel = 'Aceptar',
  }) {
    return showDialog<String>(
      context: context,
      builder: (_) => NameInputDialog(
        title: title,
        initialValue: initialValue,
        confirmLabel: confirmLabel,
      ),
    );
  }

  @override
  State<NameInputDialog> createState() => _NameInputDialogState();
}

class _NameInputDialogState extends State<NameInputDialog> {
  late final TextEditingController _controller = TextEditingController(text: widget.initialValue);

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: Text(widget.title),
      content: TextField(
        controller: _controller,
        autofocus: true,
        decoration: const InputDecoration(hintText: 'Nombre'),
        onSubmitted: (_) => _submit(),
      ),
      actions: [
        TextButton(onPressed: () => Navigator.pop(context), child: const Text('Cancelar')),
        FilledButton(onPressed: _submit, child: Text(widget.confirmLabel)),
      ],
    );
  }

  void _submit() {
    final value = _controller.text.trim();
    if (value.isEmpty) return;
    Navigator.pop(context, value);
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }
}