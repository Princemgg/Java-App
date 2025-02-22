import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AdminDashboard extends JFrame {
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField searchField;
    private JComboBox<String> searchType;
    private Image backgroundImage;

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Load background image
        backgroundImage = new ImageIcon("src/Image/background1.jpg").getImage();  // Update path as needed

        // Set up the custom background panel
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());

        // Search components
        searchField = new JTextField(15);
        searchType = new JComboBox<>(new String[]{"Course", "Unit"});
        JButton searchButton = new JButton("Search");

        // Table setup
        tableModel = new DefaultTableModel(new String[]{"ID", "Course", "Registration No", "Name", "Unit"}, 0);
        table = new JTable(tableModel);
        loadStudentData("", "");

        // Search action
        searchButton.addActionListener(new SearchAction());

        // CRUD buttons
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");

        // Add action listeners for CRUD buttons
        addButton.addActionListener(new AddAction());
        updateButton.addActionListener(new UpdateAction());
        deleteButton.addActionListener(new DeleteAction());

        // Search panel
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search by:"));
        searchPanel.add(searchType);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // CRUD panel
        JPanel crudPanel = new JPanel();
        crudPanel.add(addButton);
        crudPanel.add(updateButton);
        crudPanel.add(deleteButton);

        // Layout setup
        backgroundPanel.add(searchPanel, BorderLayout.NORTH);
        backgroundPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        backgroundPanel.add(crudPanel, BorderLayout.SOUTH);

        setContentPane(backgroundPanel);
    }

    private void loadStudentData(String filterValue, String filterType) {
        tableModel.setRowCount(0);
        String query = filterValue.isEmpty() ? "SELECT * FROM students" : "SELECT * FROM students WHERE " + filterType + " = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            if (!filterValue.isEmpty()) {
                pstmt.setString(1, filterValue);
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("course"),
                        rs.getString("registration_no"),
                        rs.getString("name"),
                        rs.getString("unit")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Custom JPanel class to paint the background image
    private class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    // Search action class
    private class SearchAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String filterValue = searchField.getText();
            String filterType = searchType.getSelectedItem().toString().toLowerCase();
            loadStudentData(filterValue, filterType);
        }
    }

    // Add action class
    private class AddAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JTextField courseField = new JTextField(10);
            JTextField regNoField = new JTextField(10);
            JTextField nameField = new JTextField(10);
            JTextField unitField = new JTextField(10);

            JPanel panel = new JPanel(new GridLayout(4, 2));
            panel.add(new JLabel("Course:"));
            panel.add(courseField);
            panel.add(new JLabel("Registration No:"));
            panel.add(regNoField);
            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Unit:"));
            panel.add(unitField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Add Student", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement("INSERT INTO students (course, registration_no, name, unit) VALUES (?, ?, ?, ?)")) {
                    stmt.setString(1, courseField.getText());
                    stmt.setString(2, regNoField.getText());
                    stmt.setString(3, nameField.getText());
                    stmt.setString(4, unitField.getText());
                    stmt.executeUpdate();
                    loadStudentData("", "");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    // Update action class
    private class UpdateAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                String course = (String) tableModel.getValueAt(selectedRow, 1);
                String regNo = (String) tableModel.getValueAt(selectedRow, 2);
                String name = (String) tableModel.getValueAt(selectedRow, 3);
                String unit = (String) tableModel.getValueAt(selectedRow, 4);

                JTextField courseField = new JTextField(course);
                JTextField regNoField = new JTextField(regNo);
                JTextField nameField = new JTextField(name);
                JTextField unitField = new JTextField(unit);

                JPanel panel = new JPanel(new GridLayout(4, 2));
                panel.add(new JLabel("Course:"));
                panel.add(courseField);
                panel.add(new JLabel("Registration No:"));
                panel.add(regNoField);
                panel.add(new JLabel("Name:"));
                panel.add(nameField);
                panel.add(new JLabel("Unit:"));
                panel.add(unitField);

                int result = JOptionPane.showConfirmDialog(null, panel, "Update Student", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    try (Connection conn = DatabaseConnection.getConnection();
                         PreparedStatement stmt = conn.prepareStatement("UPDATE students SET course = ?, registration_no = ?, name = ?, unit = ? WHERE id = ?")) {
                        stmt.setString(1, courseField.getText());
                        stmt.setString(2, regNoField.getText());
                        stmt.setString(3, nameField.getText());
                        stmt.setString(4, unitField.getText());
                        stmt.setInt(5, id);
                        stmt.executeUpdate();
                        loadStudentData("", "");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a row to update.");
            }
        }
    }

    // Delete action class
    private class DeleteAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this student?", "Delete Student", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try (Connection conn = DatabaseConnection.getConnection();
                         PreparedStatement stmt = conn.prepareStatement("DELETE FROM students WHERE id = ?")) {
                        stmt.setInt(1, id);
                        stmt.executeUpdate();
                        loadStudentData("", "");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a row to delete.");
            }
        }
    }
}
