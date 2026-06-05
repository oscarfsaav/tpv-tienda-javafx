# 🛒 Terminal Punto de Venta (TPV) Profesional

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-UI-47A248?style=for-the-badge&logo=java&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-Database-003B57?style=for-the-badge&logo=sqlite&logoColor=white)

Aplicación de escritorio robusta orientada a la gestión de ventas de una tienda o entorno de hostelería. Este sistema permite a los cajeros iniciar sesión, gestionar un catálogo visual de productos, procesar cobros descontando automáticamente el stock y exportar tickets contables.

Este software ha sido desarrollado como proyecto académico principal para el Grado Superior en Desarrollo de Aplicaciones Multiplataforma (DAM), aplicando patrones de diseño profesionales y buenas prácticas de ingeniería de software.

## ✨ Funcionalidades Principales

*   **Autenticación de Usuarios (Login):** Barrera de seguridad inicial para proteger el acceso al sistema.
*   **Catálogo Visual Dinámico:** Carga automática de imágenes de productos desde el sistema de archivos (`.png`) emparejadas con los registros de la base de datos.
*   **Filtrado en Tiempo Real:** Búsqueda instantánea de artículos por categoría (Bebidas, Comida, etc.) o por nombre mediante *Listeners*, sin sobrecargar la base de datos.
*   **Gestión de Transacciones ACID:** Lógica de cobro segura. Al realizar una venta, el sistema registra el ticket, guarda el detalle de los productos y descuenta el inventario exacto en una única transacción atómica. Si ocurre un error, se ejecuta un *rollback*.
*   **Exportación a Excel (CSV):** Generación de tickets digitales con codificación UTF-8 y carácter BOM (`\ufeff`), garantizando una compatibilidad perfecta con Microsoft Excel (respetando tildes, decimales y el símbolo €).
*   **Interfaz Desacoplada (CSS):** Diseño UI moderno con bordes redondeados y efectos *hover* gestionados íntegramente a través de hojas de estilo nativas de JavaFX.

## 🏗️ Arquitectura y Tecnologías

El proyecto sigue una separación estricta de responsabilidades utilizando el patrón **Modelo-Vista-Controlador (MVC)**, apoyado por el patrón **Data Access Object (DAO)** para aislar la capa de persistencia.

*   **Lenguaje:** Java (JDK 17)
*   **Framework UI:** JavaFX + CSS
*   **Herramienta de Diseño:** Gluon Scene Builder (`.fxml`)
*   **Base de Datos:** SQLite (Embebida, archivo local `tienda.db`)
*   **Gestor de Dependencias:** Maven

## 🗄️ Estructura de la Base de Datos

El sistema utiliza un modelo relacional normalizado:
*   `productos`: Catálogo principal (id, código de barras, nombre, precio, stock, categoría).
*   `ventas`: Cabecera del ticket con timestamp y total.
*   `lineas_venta`: Detalle de cada transacción (cantidades y subtotales), enlazada mediante claves foráneas.

## 🚀 Instalación y Uso

1.  **Clonar el repositorio:**
```bash
    git clone [https://github.com/oscarfsaav/tpv-tienda-javafx.git](https://github.com/oscarfsaav/tpv-tienda-javafx.git)
    ```
2.  **Importar el proyecto:**
    Abre el proyecto en tu IDE favorito (Eclipse, IntelliJ IDEA) como un proyecto Maven.
3.  **Ejecutar:**
    Inicia la aplicación desde la clase principal `App.java`.
4.  **Credenciales de acceso por defecto:**
    *   **Usuario:** `admin`
    *   **Contraseña:** `1234`


## 👤 Autor

**Óscar Fragueiro Saavedra**
*   Desarrollador de Aplicaciones Multiplataforma.
*   [Perfil de GitHub](https://github.com/oscarfsaav)