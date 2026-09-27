import 'package:flutter/material.dart';
import 'package:open_filex/open_filex.dart';
import 'package:provider/provider.dart';
import '../../core/utils/file_utils.dart';
import '../providers/favorites_provider.dart';

class FavoritesScreen extends StatefulWidget {
  const FavoritesScreen({super.key});

  @override
  State<FavoritesScreen> createState() => _FavoritesScreenState();
}

class _FavoritesScreenState extends State<FavoritesScreen> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) => context.read<FavoritesProvider>().load());
  }

  @override
  Widget build(BuildContext context) {
    final favorites = context.watch<FavoritesProvider>();

    return Scaffold(
      appBar: AppBar(title: const Text('Favoritos')),
      body: favorites.isLoading
          ? const Center(child: CircularProgressIndicator())
          : favorites.favorites.isEmpty
              ? const Center(child: Text('Aún no tienes favoritos.'))
              : ListView.builder(
                  itemCount: favorites.favorites.length,
                  itemBuilder: (context, index) {
                    final item = favorites.favorites[index];
                    return ListTile(
                      leading: Icon(FileUtils.iconFor(item)),
                      title: Text(item.name),
                      subtitle: Text(FileUtils.formatDate(item.modifiedAt)),
                      trailing: IconButton(
                        icon: const Icon(Icons.star_rounded, color: Colors.amber),
                        onPressed: () => favorites.toggle(item),
                      ),
                      onTap: () => OpenFilex.open(item.path),
                    );
                  },
                ),
    );
  }
}