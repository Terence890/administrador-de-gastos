### **Expense Manager**  
📝 **Description**  
Expense Manager is a desktop application developed in Java that allows users to efficiently manage and track their personal expenses. With an intuitive and modern graphical interface, users can record, edit, and analyze their daily expenses.  

✨ **Key Features**  
👤 User authentication system  
💰 Expense recording and management  
📊 Visualization of expenses through charts  
🗂️ Categorization of expenses  
📅 Monthly and total expense tracking  
🌐 Multilingual support (Spanish, English, French, German)  
🎨 Light/Dark theme  

🛠️ **System Requirements**  
- Java 8 or higher  
- MySQL Database  
- Operating System: Windows, Linux, or macOS  

🚀 **Installation**  
Clone the repository:  
```bash
git clone https://github.com/Terence890/administrador-de-gastos.git
```
Set up the database:  
```bash
cd ExpenseManager/database  
./setup_mysql.bat  
```
Run the application:  
```bash
./run.bat  
```

📱 **Usage**  
- Log in or register as a new user.  
- On the main dashboard, you can:  
  - Add new expenses  
  - View expense summaries  
  - Edit or delete existing expenses  
  - Visualize expense charts by category  
  - Change the application's language  
  - Toggle between light and dark themes  

🔧 **Configuration**  
The application uses a `config.properties` file for database configuration:  
```properties
db.url=jdbc:mysql://localhost:3306/expense_manager  
db.user=root  
db.password=your_password  
```

🗂️ **Project Structure**  
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

🤝 **Contribution**  
Contributions are welcome. Please:  
1. Fork the project.  
2. Create a branch for your feature.  
3. Submit a pull request.  

📄 **License**  
This project is licensed under the MIT License. See the `LICENSE` file for more details.  

👥 **Authors**  
- **Terence**  

🙏 **Acknowledgements**  
- **JFreeChart** for charts  
- **FlatLaf** for modern themes  
- **MySQL** for the database  
