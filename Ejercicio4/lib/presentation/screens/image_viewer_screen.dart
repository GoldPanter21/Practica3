import 'dart:io';
import 'package:flutter/material.dart';
import 'package:photo_view/photo_view.dart';
import '../../domain/entities/file_item.dart';

class ImageViewerScreen extends StatelessWidget {
  final FileItem item;
  const ImageViewerScreen({super.key, required this.item});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      appBar: AppBar(
        backgroundColor: Colors.black,
        foregroundColor: Colors.white,
        title: Text(item.name),
      ),
      body: PhotoView(
        imageProvider: FileImage(File(item.path)),
        minScale: PhotoViewComputedScale.contained,
        maxScale: PhotoViewComputedScale.covered * 4,
        enableRotation: true,
      ),
    );
  }
}