# Rick and Morty Character Viewer

Bienvenido a la aplicación **Rick and Morty Character Viewer**, una aplicación Android construida con Jetpack Compose y Kotlin para visualizar los personajes de Rick y Morty. Esta aplicación consume datos de una API externa y utiliza varias tecnologías y bibliotecas modernas de Android.

## Características

- Visualización de personajes de Rick y Morty.
- Consumo de datos desde una API externa.
- Interfaz de usuario moderna utilizando Jetpack Compose.
- Gestión de dependencias con Koin.
- Manejo de solicitudes de red con Ktor.
- Ciclo de vida gestionado con Lifecycle.
- Carga eficiente de imágenes con Coil.
- Operaciones asíncronas con Coroutines.

## Tecnologías Utilizadas

- **Jetpack Compose**: Para construir la interfaz de usuario de forma declarativa.
- **Kotlin**: El lenguaje de programación utilizado.
- **Koin**: Para la inyección de dependencias.
- **Ktor**: Para realizar las solicitudes de red.
- **Lifecycle**: Para gestionar el ciclo de vida de los componentes.
- **Coil**: Para la carga y visualización de imágenes.
- **Coroutines**: Para manejar operaciones asíncronas.

## Arquitectura del Proyecto

El proyecto sigue la arquitectura MVVM (Model-View-ViewModel) para separar claramente las responsabilidades y facilitar el mantenimiento del código.

- **Model**: Contiene las clases de datos y las interfaces de la API.
- **View**: Contiene las composables de Jetpack Compose para la interfaz de usuario.
- **ViewModel**: Gestiona la lógica de negocio y la preparación de los datos para la vista.
