import 'package:flutter/material.dart';
import '../../core/utils/file_utils.dart';
import '../../domain/entities/file_item.dart';

class FileTile extends StatelessWidget {
  final FileItem item;
  final bool isFavorite;
  final VoidCallback onTap;
  final VoidCallback onDelete;
  final VoidCallback onRename;
  final VoidCallback onCopy;
  final VoidCallback onMove;
  final VoidCallback onShare;
  final VoidCallback onToggleFavorite;

  const FileTile({
    super.key,
    required this.item,
    required this.isFavorite,
    required this.onTap,
    required this.onDelete,
    required this.onRename,
    required this.onCopy,
    required this.onMove,
    required this.onShare,
    required this.onToggleFavorite,
  });

  @override
  Widget build(BuildContext context) {
    final scheme = Theme.of(context).colorScheme;

    return Dismissible(
      key: ValueKey(item.path),
      direction: DismissDirection.endToStart,
      background: Container(
        alignment: Alignment.centerRight,
        padding: const EdgeInsets.symmetric(horizontal: 20),
        color: scheme.errorContainer,
        child: Icon(Icons.delete_rounded, color: scheme.onErrorContainer),
      ),
      confirmDismiss: (_) => _confirmDelete(context),
      onDismissed: (_) => onDelete(),
      child: GestureDetector(
        onLongPress: () => _showContextMenu(context),
        child: ListTile(
          leading: Icon(FileUtils.iconFor(item), size: 30),
          title: Text(item.name, maxLines: 1, overflow: TextOverflow.ellipsis),
          subtitle: Text(
            item.isDirectory
                ? 'Carpeta · ${FileUtils.formatDate(item.modifiedAt)}'
                : '${FileUtils.formatSize(item.sizeBytes)} · ${FileUtils.formatDate(item.modifiedAt)}',
          ),
          trailing: IconButton(
            icon: Icon(
              isFavorite ? Icons.star_rounded : Icons.star_border_rounded,
              color: isFavorite ? Colors.amber : scheme.outline,
            ),
            onPressed: onToggleFavorite,
          ),
          onTap: onTap,
        ),
      ),
    );
  }

  Future<bool> _confirmDelete(BuildContext context) async {
    final result = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Eliminar elemento'),
        content: Text('¿Deseas eliminar "${item.name}"? Esta acción no se puede deshacer.'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx, false),
            child: const Text('Cancelar'),
          ),
          FilledButton.tonal(
            onPressed: () => Navigator.pop(ctx, true),
            child: const Text('Eliminar'),
          ),
        ],
      ),
    );
    return result ?? false;
  }
    void _showContextMenu(BuildContext context) {
    showModalBottomSheet(
      context: context,
      showDragHandle: true,
      builder: (ctx) => SafeArea(
        child: Wrap(
          children: [
            ListTile(
              leading: const Icon(Icons.drive_file_rename_outline_rounded),
              title: const Text('Renombrar'),
              onTap: () {
                Navigator.pop(ctx);
                onRename();
              },
            ),
            ListTile(
              leading: const Icon(Icons.copy_rounded),
              title: const Text('Copiar a...'),
              onTap: () {
                Navigator.pop(ctx);
                onCopy();
              },
            ),
            ListTile(
              leading: const Icon(Icons.drive_file_move_rounded),
              title: const Text('Mover a...'),
              onTap: () {
                Navigator.pop(ctx);
                onMove();
              },
            ),
            if (!item.isDirectory)
              ListTile(
                leading: const Icon(Icons.ios_share_rounded),
                title: const Text('Compartir / Exportar'),
                onTap: () {
                  Navigator.pop(ctx);
                  onShare();
                },
              ),
            ListTile(
              leading: Icon(
                isFavorite ? Icons.star_rounded : Icons.star_border_rounded,
              ),
              title: Text(isFavorite ? 'Quitar de favoritos' : 'Añadir a favoritos'),
              onTap: () {
                Navigator.pop(ctx);
                onToggleFavorite();
              },
            ),
            ListTile(
              leading: const Icon(Icons.delete_outline_rounded),
              title: const Text('Eliminar'),
              onTap: () async {
                Navigator.pop(ctx);
                if (await _confirmDelete(context)) onDelete();
              },
            ),
          ],
        ),
      ),
    );
  }
}