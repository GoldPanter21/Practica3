import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../domain/entities/app_settings.dart';
import '../providers/theme_provider.dart';

class SettingsScreen extends StatelessWidget {
  const SettingsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final theme = context.watch<ThemeProvider>();

    return Scaffold(
      appBar: AppBar(title: const Text('Configuración')),
      body: ListView(
        children: [
          const Padding(padding: EdgeInsets.all(16), child: Text('Tema de color', style: TextStyle(fontWeight: FontWeight.bold))),
          RadioListTile<AppColorScheme>(
            title: const Text('Guinda (IPN)'),
            value: AppColorScheme.guinda,
            groupValue: theme.colorScheme,
            onChanged: (v) => theme.setColorScheme(v!),
          ),
          RadioListTile<AppColorScheme>(
            title: const Text('Azul (ESCOM)'),
            value: AppColorScheme.azul,
            groupValue: theme.colorScheme,
            onChanged: (v) => theme.setColorScheme(v!),
          ),
          const Divider(),
          const Padding(padding: EdgeInsets.all(16), child: Text('Modo de apariencia', style: TextStyle(fontWeight: FontWeight.bold))),
          RadioListTile<ThemeMode>(
            title: const Text('Automático (sistema)'),
            value: ThemeMode.system,
            groupValue: theme.themeMode,
            onChanged: (v) => theme.setThemeMode(v!),
          ),
          RadioListTile<ThemeMode>(
            title: const Text('Claro'),
            value: ThemeMode.light,
            groupValue: theme.themeMode,
            onChanged: (v) => theme.setThemeMode(v!),
          ),
          RadioListTile<ThemeMode>(
            title: const Text('Oscuro'),
            value: ThemeMode.dark,
            groupValue: theme.themeMode,
            onChanged: (v) => theme.setThemeMode(v!),
          ),
        ],
      ),
    );
  }
}