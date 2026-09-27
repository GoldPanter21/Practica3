import 'package:flutter/material.dart';
import 'package:path/path.dart' as p;

class BreadcrumbBar extends StatelessWidget {
  final List<String> pathStack;
  final String rootLabel;
  final ValueChanged<int> onTapSegment;

  const BreadcrumbBar({
    super.key,
    required this.pathStack,
    required this.rootLabel,
    required this.onTapSegment,
  });

  @override
  Widget build(BuildContext context) {
    final scheme = Theme.of(context).colorScheme;
    return SizedBox(
      height: 40,
      child: ListView.separated(
        scrollDirection: Axis.horizontal,
        padding: const EdgeInsets.symmetric(horizontal: 12),
        itemCount: pathStack.length,
        separatorBuilder: (_, __) => Icon(Icons.chevron_right_rounded, size: 18, color: scheme.outline),
        itemBuilder: (context, index) {
          final isLast = index == pathStack.length - 1;
          final label = index == 0 ? rootLabel : p.basename(pathStack[index]);
          return InkWell(
            onTap: () => onTapSegment(index),
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 6),
              child: Text(
                label,
                style: TextStyle(
                  fontWeight: isLast ? FontWeight.w600 : FontWeight.normal,
                  color: isLast ? scheme.primary : scheme.onSurfaceVariant,
                ),
              ),
            ),
          );
        },
      ),
    );
  }
}