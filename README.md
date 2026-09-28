# Documentación Principal - Práctica 3

**Instituto Politécnico Nacional**  
**Escuela Superior de Cómputo**  
**Ingeniería en Sistemas Computacionales**  
**Unidad de Aprendizaje:** Desarrollo de aplicaciones móviles nativas  

**Integrantes del Equipo:**
* Orozco Aguilar Angel Isai - Boleta: 2024630437
* Téllez Girón Castro Angel Ricardo - Boleta: 2024630154
* Sánchez Valadez Zyanya Maxi - Boleta: 2024630593

**Fecha de Entrega:** 28 de septiembre de 2026

---

## Índice de Ejercicios

### [Ejercicio 1: Configuración del Entorno de Desarrollo iOS](./Ejercicio1)
* **Objetivo:** Virtualización de macOS Ventura mediante contenedores Docker (`docker-osx`) sobre un entorno Windows/WSL2 para viabilizar el desarrollo nativo en el ecosistema de Apple
* **Entregables:** Instalación de Xcode 14.3.1, resolución de incompatibilidades de arquitectura para gestores de dependencias y ejecución estable de un proyecto de prueba en el simulador de iOS 16.4

### [Ejercicio 2: Gestor de Archivos para iPhone](./Ejercicio2)
* **Objetivo:** Desarrollar un Gestor de Archivos nativo para iPhone utilizando Swift y SwiftUI (o UIKit), enfocado en la exploración, visualización y manipulación del *Sandbox* de iOS
* **Entregables:** 
  * Navegación jerárquica de directorios locales (`Documents`, `Inbox`, `tmp`) utilizando `FileManager` y categorización visual mediante `UTType`
  * Implementación de vista previa nativa con `QLPreviewController` y visualización de imágenes con soporte de gestos táctiles (zoom, rotación, pinza)
  * Operaciones de gestión de archivos (crear carpetas, copiar, mover, renombrar, eliminar), así como importación/exportación empleando `UIDocumentPickerViewController` y `UIActivityViewController`
  * Almacenamiento de historial, sistema de favoritos y caché de miniaturas mediante `UserDefaults` o `Core Data`
  * Interfaz de usuario con temas institucionales (Guinda IPN / Azul ESCOM) compatibles con el modo oscuro del sistema, y exposición de archivos habilitando las claves `UIFileSharingEnabled` y `LSSupportsOpeningDocumentsInPlace`

### [Ejercicio 3: Integración Multimedia y Persistencia Local](./Ejercicio3)
* **Objetivo:** Desarrollar una aplicación nativa que integre el uso de hardware multimedia (`AVFoundation`) con un sistema de almacenamiento dual, combinando persistencia relacional y gestión de caché física en el *Sandbox* del dispositivo
* **Entregables:** 
  * Lógica de grabación de voz con medición de decibelios en tiempo real y codificación de audio
  * Módulo de captura fotográfica con opciones de personalización (filtros, flash, temporizador) y gestión de permisos nativos
  * Gestor de almacenamiento físico mediante `FileManager` para la administración de los archivos multimedia generados
  * Galería interactiva con filtrado por categorías, construida sobre una base de datos de `Core Data` para la lectura de metadatos (tipo de archivo, nombre y timestamp)
  * Interfaz de usuario en SwiftUI con arquitectura estructurada y paleta de colores dinámicos (temas institucionales Guinda / Azul)

### [Ejercicio 4: Desarrollo Multiplataforma con Flutter](./Ejercicio4)
* **Objetivo:** Desarrollar una aplicación multiplataforma (iOS y Android) completamente funcional sin conexión a Internet, utilizando el framework Flutter (Dart) y basándose en una arquitectura limpia (Clean Architecture)
* **Entregables:** 
  * Implementación de un Gestor de Archivos (Opción A) o una App de Cámara/Micrófono (Opción B)
  * Control del estado de la aplicación mediante manejadores como Provider, Bloc o Riverpod
  * Interfaz de usuario consistente (Material Design 3 o Cupertino) con soporte de adaptación al modo oscuro y a los temas institucionales Guinda y Azul
  * Persistencia local de datos empleando soluciones propias del entorno como Hive o SQLite (`sqflite`)

### [Ejercicio 5: Desarrollo Multiplataforma con Kotlin Multiplatform](./Ejercicio5)
* **Objetivo:** Desarrollar una segunda aplicación multiplataforma utilizando Kotlin Multiplatform (KMP), contrastando este ecosistema frente a Flutter al compartir la lógica de negocio en un módulo común y compilar binarios nativos
* **Entregables:** 
  * Implementación de la opción de aplicación contraria a la elegida en el Ejercicio 4, garantizando el funcionamiento offline
  * Uso del mecanismo `expect/actual` para la resolución nativa del acceso a recursos del dispositivo, sistema de archivos y permisos en iOS y Android
  * Manejo de asincronía estructural y flujos de estado empleando Kotlin Coroutines y Flow
  * Implementación de persistencia multiplataforma utilizando herramientas como SQLDelight, Room para KMP o DataStore
  * Elaboración de una tabla comparativa exhaustiva y conclusión técnica contrastando Flutter frente a Kotlin Multiplatform en rubros como curva de aprendizaje, tamaño de binario y gestión de APIs nativas

---

## Tecnologías y Requisitos Previos Generales
Para ejecutar cualquiera de los proyectos contenidos en estas carpetas, el entorno debe contar con:
* **IDE:** Xcode 14.3.1 (con este se desarrolló la práctica, así que se asegura el funcionamiento en esta versión) o superior.
* **Lenguaje:** Swift 5+
* **Framework UI:** SwiftUI
* **Gestor de Dependencias:** RubyGems para sortear restricciones de procesadores x86_64 emulados.

## Autores del Equipo
* **Orozco Aguilar Angel Isai** - Boleta: 2024630437 - GitHub: [@GoldPanter21](https://github.com/GoldPanter21)
* **Sánchez Valadez Zyanya Maxi** - Boleta: 2024630593 - Github: [@Zyanya](https://github.com/NyaSanchez)
* **Téllez Girón Castro Angel Ricardo** - Boleta: 2024630154 - Github: [@Angelricardotgc](https://github.com/Angelricardotgc)