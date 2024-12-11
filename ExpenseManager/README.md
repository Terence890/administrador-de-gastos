# Expense Manager Application

A Java Swing application for managing personal expenses with SQLite database.

## Prerequisites

- Java Development Kit (JDK) 11 or higher
- NetBeans IDE 12.0 or higher
- Maven (usually comes bundled with NetBeans)

## Importing to NetBeans

1. Open NetBeans IDE
2. Go to File → Open Project
3. Navigate to the `ExpenseManager` folder
4. Select the project and click Open Project
5. NetBeans will automatically recognize it as a Maven project

## First Run Setup

1. The application will automatically create the SQLite database on first run
2. Database file will be created at `database/expense_manager.db`

## Dependencies

All dependencies are managed through Maven in the `pom.xml` file:
- SQLite JDBC
- FlatLaf (Modern Look and Feel)
- BCrypt (Password Hashing)
- JFreeChart (For Reports)
- Apache PDFBox (PDF Export)

## Building and Running

1. Right-click the project in NetBeans
2. Select "Clean and Build"
3. Right-click again and select "Run"

## Troubleshooting

If you encounter any build issues:
1. Right-click the project
2. Select "Clean and Build"
3. If problems persist, right-click → Properties → Libraries → "Resolve Problems" 