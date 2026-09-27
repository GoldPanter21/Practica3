import 'package:flutter/material.dart';
import '../../domain/entities/file_item.dart';

class SortOptionsSheet {
  static Future<void> show(
    BuildContext context, {
    required SortCriteria current,
    required bool ascending,
    required void Function(SortCriteria, bool) onSelect,
  }) {
    Widget option(SortCriteria c, String label, IconData icon) {
      final selected = c == current;
      return ListTile(
        leading: Icon(icon),
        title: Text(label),
        trailing: selected
            ? Icon(ascending ? Icons.arrow_upward_rounded : Icons.arrow_downward_rounded)
            : null,
        onTap: () {
          Navigator.pop(context);
          onSelect(c, selected ? !ascending : true);
        },
      );
    }

    return showModalBottomSheet(
      context: context,
      showDragHandle: true,
      builder: (_) => SafeArea(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            const Padding(padding: EdgeInsets.all(12), child: Text('Ordenar por', style: TextStyle(fontWeight: FontWeight.bold))),
            option(SortCriteria.name, 'Nombre', Icons.sort_by_alpha_rounded),
            option(SortCriteria.date, 'Fecha', Icons.schedule_rounded),
            option(SortCriteria.size, 'Tamaño', Icons.straighten_rounded),
          ],
        ),
      ),
    );
  }
}