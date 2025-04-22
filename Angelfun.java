package main;

// Imports
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import games.Game1.SnakeGame;
import games.Game2.Canvagame;

public class Angelfun extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public Angelfun() {
        // Set up the main window
        setTitle("Interactive Java App");
        
        // Window size for a uniform look
        setSize(750, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize CardLayout and main panel
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add the main menu and games as separate "cards"
        mainPanel.add(new LoginPage(this), "LoginPage");  // Add this line  
        mainPanel.add(createMainMenu(), "MainMenu");
        mainPanel.add(new SnakeGame(this), "Game1");
        mainPanel.add(new Canvagame(this), "Game2");
        mainPanel.add(new RegistrationPage(this), "RegisterPage");


        // Add the main panel to the frame
        add(mainPanel);
    }

    // Switch to a different panel (main menu, Snake Game, or Canvas Game)
    public void switchToPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }

    // Create the main menu panel with buttons to launch each game
    private JPanel createMainMenu() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    
        // Complementary background (teal-like) for contrast
        panel.setBackground(new Color(64, 128, 128));
    
        // Title label
        JLabel title = new JLabel("Welcome to AngelFun App!", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(40, 10, 40, 10));
        // Center horizontally within the BoxLayout
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);
    
        // Button to launch the Snake Game
        JButton game1Button = new JButton("Play Snake Game");
        styleMenuButton(game1Button);  // We’ll update styleMenuButton() for larger sizing
        game1Button.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(game1Button);
        game1Button.addActionListener(e -> switchToPanel("Game1"));
    
        // Add some vertical space
        panel.add(Box.createVerticalStrut(30));
    
        // Button to launch the Canvas Game
        JButton game2Button = new JButton("Play Canvas Game");
        styleMenuButton(game2Button);
        game2Button.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(game2Button);
        game2Button.addActionListener(e -> switchToPanel("Game2"));
    
        return panel;
    }
    
    // Helper method to style menu buttons (for uniform UI design)
    private void styleMenuButton(JButton button) {
        // A bigger font for better visibility
        button.setFont(new Font("SansSerif", Font.BOLD, 30));
    
        // Center text in the button (usually default, but this makes it explicit)
        button.setHorizontalAlignment(SwingConstants.CENTER);
    
        // White text on a teal-like background
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 102, 102));
        button.setFocusPainted(false);
    
        // Rounded border
        button.setBorder(new LineBorder(Color.WHITE, 2, true));
    
        // Increased dimension to make them bigger
        button.setPreferredSize(new Dimension(350, 60));
    }
    
    // Entry point
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Angelfun app = new Angelfun();
            app.switchToPanel("LoginPage");  // Show login first
            app.setVisible(true);
        });
    }
}
