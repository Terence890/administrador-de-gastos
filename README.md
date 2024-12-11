# Administrador de Gastos (Expense Manager)

## 📝 Descripción
Administrador de Gastos es una aplicación de escritorio desarrollada en Java que permite a los usuarios gestionar y dar seguimiento a sus gastos personales de manera eficiente. Con una interfaz gráfica intuitiva y moderna, los usuarios pueden registrar, editar y analizar sus gastos diarios.

## ✨ Características Principales
- 👤 Sistema de autenticación de usuarios
- 💰 Registro y gestión de gastos
- 📊 Visualización de gastos mediante gráficos
- 🗂️ Categorización de gastos
- 📅 Seguimiento de gastos mensuales y totales
- 🌐 Soporte multilingüe (Español, Inglés, Francés, Alemán)
- 🎨 Tema claro/oscuro

## 🛠️ Requisitos del Sistema
- Java 8 o superior
- MySQL Database
- Sistema operativo: Windows, Linux o macOS

## 🚀 Instalación
1. Clone el repositorio:
```bash
git clone 
```

2. Configure la base de datos:
```bash
cd ExpenseManager/database
./setup_mysql.bat
```

3. Ejecute la aplicación:
```bash
./run.bat
```

## 📱 Uso
1. Inicie sesión o regístrese como nuevo usuario
2. En el panel principal, puede:
   - Agregar nuevos gastos
   - Ver el resumen de gastos
   - Editar o eliminar gastos existentes
   - Visualizar gráficos de gastos por categoría
   - Cambiar el idioma de la aplicación
   - Alternar entre tema claro y oscuro

## 🔧 Configuración
La aplicación utiliza un archivo `config.properties` para la configuración de la base de datos:
```properties
db.url=jdbc:mysql://localhost:3306/expense_manager
db.user=root
db.password=your_password
```

## 🗂️ Estructura del Proyecto
```
ExpenseManager/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── expensemanager/
│   │   │           ├── dao/
│   │   │           ├── models/
│   │   │           ├── ui/
│   │   │           └── utils/
│   │   └── resources/
│   │       └── messages/
├── database/
└── README.md
```

## 🤝 Contribución
Las contribuciones son bienvenidas. Por favor:
1. Haga fork del proyecto
2. Cree una rama para su funcionalidad
3. Envíe un pull request

## 📄 Licencia
Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## 👥 Autores
- Terence

## 🙏 Agradecimientos
- JFreeChart por los gráficos
- FlatLaf por los temas modernos
- MySQL por la base de datos
