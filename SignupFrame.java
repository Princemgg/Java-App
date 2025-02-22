import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignupFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public SignupFrame() {
        setTitle("Sign Up");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);

        JButton signupButton = new JButton("Sign Up");

        signupButton.addActionListener(new SignupAction());

        setLayout(new GridLayout(3, 2));
        add(new JLabel("Username:"));
        add(usernameField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(signupButton);
    }

    private class SignupAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "INSERT INTO users (username, password) VALUES (?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(null, "Signup successful! You can now log in.");
                dispose();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
