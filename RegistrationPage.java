package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import db.DatabaseManager;

public class RegistrationPage extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;
    private Angelfun parentFrame;

    public RegistrationPage(Angelfun parentFrame) {
        this.parentFrame = parentFrame;
        setupUI();
    }

    private void setupUI() {
        setLayout(new GridBagLayout());
        setBackground(new Color(60, 63, 65));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Register Account");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.gridy = 1;
        gbc.gridx = 0;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.WHITE);
        add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        usernameField = new JTextField(15);
        add(usernameField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        passwordField = new JPasswordField(15);
        add(passwordField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton registerButton = new JButton("Register");
        add(registerButton, gbc);

        gbc.gridy = 4;
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.PINK);
        add(statusLabel, gbc);

        registerButton.addActionListener(this::registerUser);
    }

    private void registerUser(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
    
        if (username.isBlank() || password.isBlank()) {
            statusLabel.setText("Username and password can't be empty.");
            return;
        }
    
        String sql = "INSERT INTO Credentials (username, password) VALUES (?, ?)";
    
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, username);
            stmt.setString(2, password);
    
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                statusLabel.setText("Account created successfully! Redirecting...");
                statusLabel.setForeground(new Color(0, 200, 0));
    
                // Add loading animation or a timer-based message
                Timer timer = new Timer(1000, event -> {
                    parentFrame.switchToPanel("LoginPage");
                });
                timer.setRepeats(false);
                timer.start();
            } else {
                statusLabel.setText("Registration failed.");
                statusLabel.setForeground(Color.RED);
            }
    
        } catch (SQLException ex) {
            statusLabel.setText("Username already exists or DB error.");
            statusLabel.setForeground(Color.RED);
            ex.printStackTrace();
        }
    }
    
}

