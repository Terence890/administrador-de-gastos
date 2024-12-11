package com.expensemanager.dao;

import com.expensemanager.models.Expense;
import com.expensemanager.utils.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDAO {
    
    public Expense createExpense(Expense expense) throws SQLException {
        // First ensure the category exists
        String checkCategorySql = "INSERT IGNORE INTO categories (name) VALUES (?)";
        String insertExpenseSql = "INSERT INTO expenses (user_id, category_id, amount, description, date) " +
                                "SELECT ?, id, ?, ?, ? FROM categories WHERE name = ?";
        
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);  // Start transaction
            
            // First ensure category exists
            try (PreparedStatement categoryStmt = conn.prepareStatement(checkCategorySql)) {
                categoryStmt.setString(1, expense.getCategory());
                categoryStmt.executeUpdate();
            }
            
            // Then create the expense
            try (PreparedStatement expenseStmt = conn.prepareStatement(insertExpenseSql, Statement.RETURN_GENERATED_KEYS)) {
                expenseStmt.setInt(1, expense.getUserId());
                expenseStmt.setBigDecimal(2, expense.getAmount());
                expenseStmt.setString(3, expense.getDescription());
                expenseStmt.setDate(4, Date.valueOf(expense.getDate()));
                expenseStmt.setString(5, expense.getCategory());
                
                int affectedRows = expenseStmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating expense failed, no rows affected.");
                }

                try (ResultSet generatedKeys = expenseStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        expense.setId(generatedKeys.getInt(1));
                        conn.commit();  // Commit transaction
                        return expense;
                    } else {
                        throw new SQLException("Creating expense failed, no ID obtained.");
                    }
                }
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();  // Rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);  // Reset auto-commit
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Expense> findByUserId(int userId) throws SQLException {
        String sql = "SELECT e.*, c.name as category_name FROM expenses e " +
                    "JOIN categories c ON e.category_id = c.id " +
                    "WHERE e.user_id = ? ORDER BY e.date DESC";
        
        List<Expense> expenses = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Expense expense = new Expense(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getBigDecimal("amount"),
                        rs.getString("category_name"),
                        rs.getDate("date").toLocalDate(),
                        rs.getString("description")
                    );
                    expenses.add(expense);
                }
            }
        }
        return expenses;
    }

    public boolean deleteExpense(int expenseId, int userId) throws SQLException {
        String sql = "DELETE FROM expenses WHERE id = ? AND user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, expenseId);
            pstmt.setInt(2, userId);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean updateExpense(Expense expense) throws SQLException {
        String sql = "UPDATE expenses e " +
                    "SET amount = ?, description = ?, date = ?, category_id = " +
                    "(SELECT id FROM categories WHERE name = ?) " +
                    "WHERE e.id = ? AND e.user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBigDecimal(1, expense.getAmount());
            pstmt.setString(2, expense.getDescription());
            pstmt.setDate(3, Date.valueOf(expense.getDate()));
            pstmt.setString(4, expense.getCategory());
            pstmt.setInt(5, expense.getId());
            pstmt.setInt(6, expense.getUserId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
} 