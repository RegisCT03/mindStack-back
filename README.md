# MindStack

MindStack es una plataforma orientada al bienestar personal y la gestión de energía para estudiantes universitarios. A través del monitoreo de datos de sueño, estado de ánimo y la interacción con minijuegos cognitivos, el sistema calcula métricas clave como la **Batería Cognitiva** y un **Semáforo de Riesgo** para ayudar a los usuarios a optimizar su rendimiento diario.

## Equipo de Desarrollo
* María Regina Calderón Trejo
* Ian Mauricio Morales Montejo
* Omar Kalid Selvas Alvarez

---

## Arquitectura del Sistema
El proyecto sigue los principios de la **Arquitectura Orientada a Servicios (SOA)** con una clara separación de capas y responsabilidades:

1. **Frontend (Aplicación Móvil):** Desarrollado nativamente en Android utilizando Jetpack Compose. Se encarga exclusivamente de la capa de presentación y la recolección de datos del usuario, delegando la lógica de negocio al backend a través de peticiones HTTP.
2. **Backend (API REST):** Desarrollado en Kotlin utilizando el framework **Ktor**. Implementa el patrón MVC/Capas (Controllers/Rutas, Servicios, Repositorios) para garantizar un código desacoplado y escalable. Gestiona la autenticación (JWT), la lógica del Semáforo de Riesgo y el procesamiento de datos.
3. **Capa de Datos:** Base de datos relacional en **PostgreSQL**. Cuenta con un diseño robusto que incluye vistas, triggers, funciones y procedimientos almacenados para garantizar la integridad de los datos a nivel de base.
4. **Despliegue:**La basa de datos se encuentra desplegada en Neon y la API en Render, por motivos de licencia y permisos la app se encuentra por el momento unicamente por APK, el cual se puede descargar en una landing page mindstack.com.mx desplegada en Vercel
---

## Endpoints Principales (API Spec)
La API REST consume y retorna datos en formato application/json. Aquí están los módulos principales:
- Autenticación (/api/v1/auth): /login, /register (Protegidos con JWT).
- Check-in Diario (/api/v1/checkin): Registro de estado de ánimo y métricas iniciales del día.
- Sueño (/api/v1/sleep): Registro y cálculo de horas de descanso.
- Juegos/Batería Cognitiva (/api/v1/games): Interacción con el juego de memoria y reflejos para actualizar el estado del Semáforo de Riesgo.
- Dashboard (/api/v1/dashboard): Retorna el resumen consolidado del usuario para la vista principal de la app móvil.

## Pruebas
Se ha implementado una suite de pruebas End-to-End (E2E) para validar el correcto funcionamiento de los endpoints, el manejo de errores (códigos 200, 201, 400, 404, etc.) y la validación de tokens JWT.
El archivo de la colección de pruebas se encuentra incluido en el repositorio bajo el nombre: MindStack_API_Tests.postman_collection.json. Puede ser importado directamente a Postman para ejecutar todos los flujos de la aplicación.

## Uso de IA y Recursos Externos
En cumplimiento con los lineamientos de integridad y evaluación del proyecto, se declara de manera transparente el uso de herramientas de Inteligencia Artificial (IA) y recursos externos durante el desarrollo de este sistema:

### Generación y Optimización de Código (Gemini)
- Se utilizaron modelos de lenguaje (LLMs) para asistir en la redacción de consultas SQL complejas (triggers y stored procedures en PostgreSQL).
- Se empleó IA como soporte para estructurar la configuración de Docker y docker-compose.yml.
- Se consultaron sugerencias para la implementación de buenas prácticas en la validación de tokens JWT con Ktor y el manejo de excepciones (StatusPages).
- Verificación de dos pasos

Esto con el fin de celerar el proceso de configuración del entorno de desarrollo y depurar errores de sintaxis en consultas relacionales complejas.

### Librerías y Recursos Externos:
- Ktor Framework: Utilizado para la construcción del servidor web y cliente. Documentación oficial empleada como referencia principal.
- Jetpack Compose: Utilizado en el frontend.

Todo el código de la lógica de negocio (Cálculo de batería cognitiva, semáforo de riesgo, y conexión de módulos) fue desarrollado, adaptado y estructurado por los autores del proyecto.

**Ninguna porción de código generada por IA fue integrada sin previa validación, revisión de seguridad y adaptación específica a la arquitectura y requerimientos de MindStack.**
