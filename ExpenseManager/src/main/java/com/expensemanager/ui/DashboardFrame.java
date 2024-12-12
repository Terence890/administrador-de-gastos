package com.expensemanager.ui;

import com.expensemanager.models.User;
import com.expensemanager.models.Expense;
import com.expensemanager.dao.ExpenseDAO;
import com.expensemanager.dao.BudgetDAO;
import com.expensemanager.dao.RecurringExpenseDAO;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.general.DefaultPieDataset;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.GradientPaint;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class DashboardFrame extends JFrame {
    // Theme Colors
    private static final Color LIGHT_PRIMARY = new Color(52, 152, 219);    // Blue
    private static final Color LIGHT_DANGER = new Color(231, 76, 60);      // Red
    private static final Color LIGHT_TEXT = new Color(44, 62, 80);         // Dark blue-gray

    // Icons
    private static final String ICON_EDIT = "✏️";
    private static final String ICON_DELETE = "🗑️";
    private static final String ICON_WELCOME = "👋";
    private static final String ICON_MONEY = "💰";
    private static final String ICON_CALENDAR = "📅";
    private static final String ICON_FOOD = "🍽️";
    private static final String ICON_TRANSPORT = "🚗";
    private static final String ICON_HOUSING = "🏠";
    private static final String ICON_ENTERTAINMENT = "🎮";
    private static final String ICON_SHOPPING = "🛍️";
    private static final String ICON_HEALTHCARE = "🏥";
    private static final String ICON_EDUCATION = "📚";
    private static final String ICON_OTHERS = "📦";
    private static final String ICON_SAVE = "💾";
    private static final String ICON_CANCEL = "❌";
    private static final String ICON_ADD = "➕";
    private static final String ICON_CATEGORY = "📂";
    private static final String ICON_AMOUNT = "💵";
    private static final String ICON_DATE = "📆";
    private static final String ICON_DESCRIPTION = "📝";
    private static final String ICON_CHART = "📊";
    private static final String ICON_LANGUAGE = "🌐";
    private static final String ICON_THEME = "🎨";
    private static final String ICON_TOTAL = "💵";
    private static final String ICON_TOTAL_MONEY = "💸";

    private static final Map<String, String> CATEGORY_ICONS;
    static {
        Map<String, String> icons = new HashMap<>();
        icons.put("Food", ICON_FOOD);
        icons.put("Transport", ICON_TRANSPORT);
        icons.put("Housing", ICON_HOUSING);
        icons.put("Entertainment", ICON_ENTERTAINMENT);
        icons.put("Shopping", ICON_SHOPPING);
        icons.put("Healthcare", ICON_HEALTHCARE);
        icons.put("Education", ICON_EDUCATION);
        icons.put("Others", ICON_OTHERS);
        CATEGORY_ICONS = Collections.unmodifiableMap(icons);
    }

    private final User currentUser;
    private final ExpenseDAO expenseDAO;
    private final BudgetDAO budgetDAO;
    private final RecurringExpenseDAO recurringExpenseDAO;
    private final Map<String, BigDecimal> budgetProgressBars;
    private JTable expenseTable;
    private DefaultTableModel tableModel;
    private JLabel totalExpenseLabel;
    private JLabel monthlyExpenseLabel;
    private JComboBox<String> categoryComboBox;
    private JTextField amountField;
    private JTextField dateField;
    private JTextField descriptionField;
    private ThemeToggleSwitch themeToggle;
    private JPanel chartPanel;
    private JPanel expenseFormPanel;
    private JPanel leftTopPanel;
    private JButton editButton;
    private JButton deleteButton;
    private JComboBox<String> languageComboBox;
    private ResourceBundle messages;
    private Locale currentLocale;
    private JLabel welcomeLabel;
    private BigDecimal totalAmount = BigDecimal.ZERO;
    private BigDecimal monthlyAmount = BigDecimal.ZERO;
    private Color currentPrimary = LIGHT_PRIMARY;
    private Color currentDanger = LIGHT_DANGER;
    private Color currentText = LIGHT_TEXT;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Constants
    private static final String[] CATEGORIES = {
        "Food", "Transport", "Housing", "Entertainment", 
        "Shopping", "Healthcare", "Education", "Others"
    };

    private static final Map<String, Locale> LANGUAGE_LOCALES = Map.of(
        "English", new Locale("en", "US"),
        "Spanish", new Locale("es", "ES"),
        "French", new Locale("fr", "FR"),
        "German", new Locale("de", "DE")
    );

    private static final Map<String, String> CATEGORY_TRANSLATIONS = Map.of(
        "Comida", "Food",
        "Transporte", "Transport",
        "Vivienda", "Housing",
        "Entretenimiento", "Entertainment",
        "Compras", "Shopping",
        "Salud", "Healthcare",
        "Educación", "Education",
        "Otros", "Others"
    );

    public DashboardFrame(User user) {
        super("Expense Manager");
        this.currentUser = user;
        this.expenseDAO = new ExpenseDAO();
        this.budgetDAO = new BudgetDAO();
        this.recurringExpenseDAO = new RecurringExpenseDAO();
        this.budgetProgressBars = new HashMap<>();
        this.totalAmount = BigDecimal.ZERO;
        this.monthlyAmount = BigDecimal.ZERO;
        
        // Initialize locale and messages first
        this.currentLocale = new Locale("en", "US");
        try {
            this.messages = ResourceBundle.getBundle("messages", currentLocale);
        } catch (Exception e) {
            System.err.println("Warning: Could not load messages bundle. Using default strings.");
            this.messages = createDefaultResourceBundle();
        }
        
        // Initialize components
        initComponents();
        setupUI();
        setupListeners();
        loadExpenses();
        updateCharts();
        
        // Set initial language to English
        if (languageComboBox != null) {
            languageComboBox.setSelectedItem("English");
            updateLanguage("English");
        }
        
        setVisible(true);
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize labels first
        welcomeLabel = new JLabel(ICON_WELCOME + " Welcome");
        totalExpenseLabel = new JLabel(ICON_MONEY + " Total: $0.00");
        monthlyExpenseLabel = new JLabel(ICON_CALENDAR + " This Month: $0.00");
        
        // Set label properties with emoji font
        Font labelFont = new Font("Segoe UI Emoji", Font.BOLD, 14);
        welcomeLabel.setFont(labelFont);
        totalExpenseLabel.setFont(labelFont);
        monthlyExpenseLabel.setFont(labelFont);
        
        // Initialize table model
        tableModel = new DefaultTableModel(
            new String[]{"ID", "Date", "Category", "Amount", "Description"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Initialize table
        expenseTable = new JTable(tableModel);
        setupTable();
        
        // Initialize form components
        categoryComboBox = new JComboBox<>(CATEGORIES);
        amountField = new JTextField(10);
        dateField = new JTextField(LocalDate.now().format(dateFormatter), 10);
        descriptionField = new JTextField(20);
        
        // Initialize buttons with icons
        editButton = createStyledButton(messages.getString("edit"), currentPrimary, ICON_EDIT);
        deleteButton = createStyledButton(messages.getString("delete"), currentDanger, ICON_DELETE);
        
        // Initialize theme toggle with icon
        themeToggle = new ThemeToggleSwitch();
        themeToggle.setToolTipText(ICON_THEME + " Toggle Theme");
        
        // Initialize panels
        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(currentPrimary),
            ICON_CHART + " Expense Chart"
        ));
        
        expenseFormPanel = createExpenseForm();
        leftTopPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftTopPanel.setBackground(currentPrimary);
        
        // Initialize language combo box with icon
        languageComboBox = new JComboBox<>(new String[]{"English", "Spanish", "French", "German"});
        languageComboBox.setPreferredSize(new Dimension(120, 30));
        languageComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        languageComboBox.setToolTipText(ICON_LANGUAGE + " Select Language");
        
        // Update initial text
        updateWelcomeLabel();
        updateStatsLabels();
    }

    private void updateStatsLabels() {
        try {
            if (totalExpenseLabel == null) {
                totalExpenseLabel = new JLabel(ICON_MONEY + " Total: $0.00");
            }
            if (monthlyExpenseLabel == null) {
                monthlyExpenseLabel = new JLabel(ICON_CALENDAR + " This Month: $0.00");
            }
            if (totalAmount == null) {
                totalAmount = BigDecimal.ZERO;
            }
            if (monthlyAmount == null) {
                monthlyAmount = BigDecimal.ZERO;
            }

            totalExpenseLabel.setText(ICON_MONEY + " " + messages.getString("total") + ": $" + 
                totalAmount.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            monthlyExpenseLabel.setText(ICON_CALENDAR + " " + messages.getString("thisMonth") + ": $" + 
                monthlyAmount.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            
            // Update font to support emoji
            Font labelFont = new Font("Segoe UI Emoji", Font.BOLD, 14);
            totalExpenseLabel.setFont(labelFont);
            monthlyExpenseLabel.setFont(labelFont);
            totalExpenseLabel.setForeground(currentText);
            monthlyExpenseLabel.setForeground(currentText);
        } catch (Exception e) {
            System.err.println("Error updating stats labels: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadExpenses() {
        try {
            if (totalAmount == null) totalAmount = BigDecimal.ZERO;
            if (monthlyAmount == null) monthlyAmount = BigDecimal.ZERO;
            
            List<Expense> expenses = expenseDAO.findByUserId(currentUser.getId());
            
            tableModel.setRowCount(0);
            totalAmount = BigDecimal.ZERO;
            monthlyAmount = BigDecimal.ZERO;
            LocalDate now = LocalDate.now();
            
            for (Expense expense : expenses) {
                if (expense != null && expense.getAmount() != null) {
                    Object[] row = {
                        expense.getId(),
                        expense.getDate().format(dateFormatter),
                        expense.getCategory(),
                        String.format("$%.2f", expense.getAmount()),
                        expense.getDescription()
                    };
                    tableModel.addRow(row);
                    totalAmount = totalAmount.add(expense.getAmount());
                    
                    if (expense.getDate().getMonth() == now.getMonth() && 
                        expense.getDate().getYear() == now.getYear()) {
                        monthlyAmount = monthlyAmount.add(expense.getAmount());
                    }
                }
            }
            
            updateStatsLabels();
            
        } catch (SQLException e) {
            System.err.println("Error loading expenses: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading expenses: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private ResourceBundle createDefaultResourceBundle() {
        return new ListResourceBundle() {
            @Override
            protected Object[][] getContents() {
                return new Object[][] {
                    {"welcome", "Welcome"},
                    {"title", "Expense Manager"},
                    {"edit", "Edit"},
                    {"delete", "Delete"},
                    {"total", "Total"},
                    {"thisMonth", "This Month"},
                    {"category", "Category"},
                    {"amount", "Amount"},
                    {"date", "Date"},
                    {"description", "Description"},
                    {"addNewExpense", "Add New Expense"},
                    {"expenseAdded", "Expense added successfully!"},
                    {"error", "Error"},
                    {"categories", "Food,Transport,Housing,Entertainment,Shopping,Healthcare,Education,Others"}
                };
            }
        };
    }

    private void setupUI() {
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setBackground(currentPrimary);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        leftTopPanel.add(welcomeLabel);
        leftTopPanel.add(languageComboBox);
        
        topPanel.add(leftTopPanel, BorderLayout.WEST);
        topPanel.add(themeToggle, BorderLayout.EAST);
        
        // Split pane setup
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBorder(null);
        splitPane.setDividerSize(5);
        
        // Left panel
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(Color.WHITE);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        JScrollPane scrollPane = new JScrollPane(expenseTable);
        
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        statsPanel.add(totalExpenseLabel);
        statsPanel.add(monthlyExpenseLabel);
        
        leftPanel.add(buttonPanel, BorderLayout.NORTH);
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        leftPanel.add(statsPanel, BorderLayout.SOUTH);
        
        // Right panel
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.add(expenseFormPanel, BorderLayout.NORTH);
        rightPanel.add(chartPanel, BorderLayout.CENTER);
        
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        splitPane.setDividerLocation(700);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        
        add(mainPanel);
    }

    private void setupListeners() {
        themeToggle.addItemListener(e -> {
            boolean isDark = e.getStateChange() == ItemEvent.SELECTED;
            try {
                UIManager.setLookAndFeel(isDark ? new FlatDarkLaf() : new FlatLightLaf());
                SwingUtilities.updateComponentTreeUI(this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        expenseTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = expenseTable.getSelectedRow() != -1;
            editButton.setEnabled(hasSelection);
            deleteButton.setEnabled(hasSelection);
        });

        editButton.addActionListener(e -> editSelectedExpense());
        deleteButton.addActionListener(e -> deleteSelectedExpense());

        // Add language change listener
        languageComboBox.addActionListener(e -> {
            if (e.getSource() == languageComboBox && e.getActionCommand().equals("comboBoxChanged")) {
                String selectedLanguage = (String) languageComboBox.getSelectedItem();
                if (selectedLanguage != null) {
                    updateLanguage(selectedLanguage);
                }
            }
        });
    }

    private void updateLanguage(String language) {
        try {
            currentLocale = LANGUAGE_LOCALES.get(language);
            if (currentLocale == null) {
                currentLocale = new Locale("en", "US");
            }
            messages = ResourceBundle.getBundle("messages", currentLocale);
            
            // Update window title
            setTitle(messages.getString("title"));
            
            // Update labels
            updateWelcomeLabel();
            updateStatsLabels();
            
            // Update buttons
            editButton.setText(ICON_EDIT + " " + messages.getString("edit"));
            deleteButton.setText(ICON_DELETE + " " + messages.getString("delete"));
            
            // Recreate expense form with new language
            if (expenseFormPanel != null && expenseFormPanel.getParent() != null) {
                Container parent = expenseFormPanel.getParent();
                parent.remove(expenseFormPanel);
                expenseFormPanel = createExpenseForm();
                parent.add(expenseFormPanel, BorderLayout.NORTH);
            }
            
            // Force UI refresh
            SwingUtilities.updateComponentTreeUI(this);
            revalidate();
            repaint();
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading language: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupTable() {
        expenseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        expenseTable.setRowHeight(35);
        expenseTable.setShowGrid(false);
        expenseTable.setIntercellSpacing(new Dimension(0, 0));
        expenseTable.getTableHeader().setBackground(currentPrimary);
        expenseTable.getTableHeader().setForeground(Color.WHITE);
        expenseTable.getTableHeader().setFont(expenseTable.getTableHeader().getFont().deriveFont(Font.BOLD));
        expenseTable.setSelectionBackground(new Color(currentPrimary.getRed(), currentPrimary.getGreen(), currentPrimary.getBlue(), 50));
        
        // Hide ID column
        expenseTable.getColumnModel().getColumn(0).setMinWidth(0);
        expenseTable.getColumnModel().getColumn(0).setMaxWidth(0);
        expenseTable.getColumnModel().getColumn(0).setWidth(0);
    }

    private JButton createStyledButton(String text, Color color, String icon) {
        JButton button = new JButton(icon + " " + text);
        button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        
        return button;
    }

    private void updateCharts() {
        try {
            DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
            Map<String, BigDecimal> categoryTotals = new HashMap<>();
            
            List<Expense> expenses = expenseDAO.findByUserId(currentUser.getId());
            for (Expense expense : expenses) {
                categoryTotals.merge(expense.getCategory(), expense.getAmount(), BigDecimal::add);
            }
            
            categoryTotals.forEach((category, total) -> 
                dataset.setValue(category, total.doubleValue()));
            
            // Create 3D pie chart
            JFreeChart chart = ChartFactory.createPieChart3D(
                "Expenses by Category",
                dataset,
                true,  // legend
                true,  // tooltips
                false  // urls
            );
            
            // Apply modern styling
            PiePlot3D plot = (PiePlot3D) chart.getPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setOutlineVisible(false);
            plot.setShadowPaint(null);
            plot.setLabelBackgroundPaint(new Color(255, 255, 255, 200));
            plot.setLabelOutlinePaint(null);
            plot.setLabelShadowPaint(null);
            plot.setInteriorGap(0.04);
            plot.setLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
            plot.setLabelPaint(new Color(44, 62, 80));
            plot.setDepthFactor(0.15);  // Adjust 3D depth
            plot.setStartAngle(290);    // Rotate for better 3D view
            
            // Modern color scheme with gradients
            Color[] colors = {
                new Color(52, 152, 219),  // Blue
                new Color(46, 204, 113),  // Green
                new Color(155, 89, 182),  // Purple
                new Color(52, 73, 94),    // Dark Gray
                new Color(231, 76, 60),   // Red
                new Color(241, 196, 15),  // Yellow
                new Color(230, 126, 34),  // Orange
                new Color(149, 165, 166)  // Light Gray
            };
            
            int colorIndex = 0;
            for (String key : dataset.getKeys()) {
                Color baseColor = colors[colorIndex % colors.length];
                // Create diagonal gradient for enhanced 3D effect
                GradientPaint gradient = new GradientPaint(
                    0, 0, baseColor,
                    100, 100, baseColor.brighter().brighter()
                );
                plot.setSectionPaint(key, gradient);
                colorIndex++;
            }
            
            // Style the chart title
            chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 16));
            chart.getTitle().setPaint(new Color(44, 62, 80));
            
            // Style the legend
            LegendTitle legend = chart.getLegend();
            legend.setBackgroundPaint(Color.WHITE);
            legend.setItemFont(new Font("Segoe UI", Font.PLAIN, 12));
            legend.setItemPaint(new Color(44, 62, 80));
            
            // Update the chart panel with modern look
            chartPanel.removeAll();
            ChartPanel newChartPanel = new ChartPanel(chart) {
                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(400, 300);
                }
            };
            newChartPanel.setBackground(Color.WHITE);
            chartPanel.add(newChartPanel, BorderLayout.CENTER);
            chartPanel.revalidate();
            chartPanel.repaint();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JPanel createExpenseForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(currentPrimary),
            messages.getString("addNewExpense")
        );
        titledBorder.setTitleFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.setBorder(titledBorder);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Add form fields
        addFormField(formPanel, gbc, "category", categoryComboBox, 0);
        addFormField(formPanel, gbc, "amount", amountField, 1);
        addFormField(formPanel, gbc, "date", dateField, 2);
        addFormField(formPanel, gbc, "description", descriptionField, 3);
        
        // Add button
        JButton addButton = createStyledButton(messages.getString("addNewExpense"), currentPrimary, "💾");
        addButton.addActionListener(e -> addExpense());
        
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(20, 8, 8, 8);
        formPanel.add(addButton, gbc);
        
        return formPanel;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelKey, JComponent field, int row) {
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 1;
        String icon;
        if (labelKey.equals("category")) {
            icon = ICON_CATEGORY;
        } else if (labelKey.equals("amount")) {
            icon = ICON_AMOUNT;
        } else if (labelKey.equals("date")) {
            icon = ICON_DATE;
        } else if (labelKey.equals("description")) {
            icon = ICON_DESCRIPTION;
        } else {
            icon = "";
        }
        JLabel label = new JLabel(icon + " " + messages.getString(labelKey) + ":");
        label.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        panel.add(label, gbc);
        
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void updateWelcomeLabel() {
        if (welcomeLabel != null && messages != null && currentUser != null) {
            welcomeLabel.setText(ICON_WELCOME + " " + messages.getString("welcome") + ", " + currentUser.getUsername());
            welcomeLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
            welcomeLabel.setForeground(currentText);
        }
    }

    private void editSelectedExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow == -1) return;

        try {
            int expenseId = (int) tableModel.getValueAt(selectedRow, 0);
            String category = (String) tableModel.getValueAt(selectedRow, 2);
            String amountStr = ((String) tableModel.getValueAt(selectedRow, 3)).replace("$", "");
            String dateStr = (String) tableModel.getValueAt(selectedRow, 1);
            String description = (String) tableModel.getValueAt(selectedRow, 4);

            // Create edit dialog
            JDialog editDialog = createEditDialog(expenseId, category, amountStr, dateStr, description);
            editDialog.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error editing expense: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow == -1) return;

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this expense?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int expenseId = (int) tableModel.getValueAt(selectedRow, 0);
                expenseDAO.deleteExpense(expenseId, currentUser.getId());
                loadExpenses();
                updateCharts();
                JOptionPane.showMessageDialog(this, "Expense deleted successfully!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Error deleting expense: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addExpense() {
        try {
            BigDecimal amount = new BigDecimal(amountField.getText());
            LocalDate date = LocalDate.parse(dateField.getText(), dateFormatter);
            String category = (String) categoryComboBox.getSelectedItem();
            String description = descriptionField.getText();
            
            Expense expense = new Expense(
                currentUser.getId(),
                amount,
                category,
                date,
                description
            );
            
            expenseDAO.createExpense(expense);
            loadExpenses();
            updateCharts();
            
            // Clear form
            amountField.setText("");
            dateField.setText(LocalDate.now().format(dateFormatter));
            descriptionField.setText("");
            
            JOptionPane.showMessageDialog(this, messages.getString("expenseAdded"));
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                messages.getString("error") + ": " + ex.getMessage(),
                messages.getString("error"),
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private JDialog createEditDialog(int expenseId, String category, String amountStr, String dateStr, String description) {
        JDialog dialog = new JDialog(this, "Edit Expense", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Category
        JComboBox<String> editCategoryCombo = new JComboBox<>(CATEGORIES);
        editCategoryCombo.setSelectedItem(category);
        addFormField(formPanel, gbc, "category", editCategoryCombo, 0);

        // Amount
        JTextField editAmountField = new JTextField(amountStr);
        addFormField(formPanel, gbc, "amount", editAmountField, 1);

        // Date
        JTextField editDateField = new JTextField(dateStr);
        addFormField(formPanel, gbc, "date", editDateField, 2);

        // Description
        JTextField editDescriptionField = new JTextField(description);
        addFormField(formPanel, gbc, "description", editDescriptionField, 3);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton saveButton = createStyledButton("Save", currentPrimary, "💾");
        JButton cancelButton = createStyledButton("Cancel", currentDanger, "❌");

        saveButton.addActionListener(e -> {
            try {
                BigDecimal amount = new BigDecimal(editAmountField.getText());
                LocalDate date = LocalDate.parse(editDateField.getText(), dateFormatter);
                String newCategory = (String) editCategoryCombo.getSelectedItem();
                String newDescription = editDescriptionField.getText();

                Expense expense = new Expense(
                    currentUser.getId(),
                    amount,
                    newCategory,
                    date,
                    newDescription
                );
                expense.setId(expenseId);

                expenseDAO.updateExpense(expense);
                loadExpenses();
                updateCharts();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Expense updated successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error updating expense: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        
        return dialog;
    }
} 