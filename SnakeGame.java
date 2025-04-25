    package games.Game1;

    // Imports
    import javax.swing.*;
    import javax.swing.border.LineBorder;

    import java.awt.*;
    import java.awt.event.KeyAdapter;
    import java.awt.event.KeyEvent;
    import java.util.Random;
    import java.util.LinkedList;
    import main.Angelfun;
    import java.awt.event.MouseEvent;
    import java.awt.event.MouseMotionAdapter;
    import java.awt.event.MouseMotionListener;


    public class SnakeGame extends JPanel {
        // Game settings
        private static final int TILE_SIZE = 30;
        private static final int GRID_SIZE = 25;
        private static final int SCREEN_SIZE = TILE_SIZE * GRID_SIZE; // 750
        private static int delay = 180;  // Increase if you want more time to see the apple

        private Angelfun parentFrame;

        // Game state
        private LinkedList<Point> snake;
        private Point apple;
        private Direction direction;
        private Direction lastMoveDirection;
        private boolean isRunning;
        private Timer gameTimer;
        private boolean gameStarted = false;

        // Buttons
        private JButton startButton;
        private JButton restartButton;
        private JButton exitButton;

        private int score = 0;
        
        private enum Direction {
            UP, DOWN, LEFT, RIGHT
        }
        
        public SnakeGame(Angelfun parentFrame) {
            this.parentFrame = parentFrame;
            setupGamePanel();
            setupButtons();
            startNewGame(); // If you want to wait until the user presses Start, remove this call
            setBackground(Color.BLACK);

            // Optional label at the top
            JLabel label = new JLabel("Snake Game");
            // Use modern Sans Serif font
            label.setFont(new Font("SansSerif", Font.BOLD, 18));
            label.setForeground(Color.WHITE);
            add(label);

            // Mouse Listener
           /*  addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int mouseX = e.getX();
                    int mouseY = e.getY();
                    System.out.println("Mouse at: (" + mouseX + ", " + mouseY + ")");
                }
            }); */
            
        }
        
        private void setupGamePanel() {
            setPreferredSize(new Dimension(SCREEN_SIZE, SCREEN_SIZE));
            setBackground(Color.WHITE);
            setFocusable(true);
            setLayout(null); // absolute positioning for the buttons
            addKeyListener(new GameKeyListener());
        }
        
        private void setupButtons() {
            // Dimensions & center
            int buttonWidth = 160;
            int buttonHeight = 50;
            int spacing = 20;

            int position_x = 360;
            int position_y = 310;

            // ---- START BUTTON ----
            startButton = new JButton("Start Game");
            startButton.setBounds(position_x,position_y,buttonWidth,buttonHeight);
            styleButton(startButton, new Color(200, 255, 200)); // light green
            // When clicked: hide all buttons, set gameStarted, begin the game
            startButton.addActionListener(e -> {
                startButton.setVisible(false);
                restartButton.setVisible(false);
                exitButton.setVisible(false);
                gameStarted = true;
                startNewGame();
            });
            add(startButton);
            
            // ---- RESTART BUTTON ----
            restartButton = new JButton("Restart Game");
            restartButton.setBounds(position_x,position_y - 20,buttonWidth,buttonHeight);
            styleButton(restartButton, new Color(240, 240, 255));
            restartButton.setVisible(false); // hidden until game over
            // When clicked: hide buttons, reset game
            restartButton.addActionListener(e -> {
                restartButton.setVisible(false);
                exitButton.setVisible(false);
                startButton.setVisible(false);
                gameStarted = true;
                startNewGame();
            });
            add(restartButton);

            // ---- EXIT BUTTON ----
            exitButton = new JButton("Exit to Menu");
            exitButton.setBounds(position_x,position_y + 40,buttonWidth,buttonHeight);
            styleButton(exitButton, new Color(240, 240, 255));
            exitButton.setVisible(false); // hidden until game over
            // Instead of System.exit(0), switch back to main menu:
            exitButton.addActionListener(e -> {
                if (parentFrame != null) {
                    parentFrame.switchToPanel("MainMenu");
                }
            });
            add(exitButton);
        }

        // Helper method to apply consistent styling to all buttons
        private void styleButton(JButton button, Color bgColor) {
            button.setBackground(bgColor);
            button.setFont(new Font("SansSerif", Font.BOLD, 16));
            button.setBorder(new LineBorder(Color.GRAY, 2, true));
        }

        // ---- Core game logic ----
        private void startNewGame() {
            snake = new LinkedList<>();
            // Place an initial 3-segment snake near (90, 90)
            for (int i = 0; i < 3; i++) {
                snake.add(new Point(90 - i * TILE_SIZE, 90));
            }

            direction = Direction.RIGHT;
            lastMoveDirection = Direction.RIGHT;
            spawnNewApple();
            repaint();

            isRunning = true;
            score = 0;
            delay = 180; 

            if (gameTimer != null) {
                gameTimer.stop();
            }
            gameTimer = new Timer(delay, e -> gameLoop());
            gameTimer.start();
        }
        
        private void spawnNewApple() {
            // If snake occupies entire board -> no space for apple
            if (snake.size() == GRID_SIZE * GRID_SIZE) {
                gameOver();
                return;
            }

            Random random = new Random();
            Point newApple;
            // Attempt to find a free tile
            do {
                int x = random.nextInt(GRID_SIZE) * TILE_SIZE;
                int y = random.nextInt(GRID_SIZE) * TILE_SIZE;
                newApple = new Point(x, y);
            } while (snake.contains(newApple));

            apple = newApple;
        }
        
        private void gameLoop() {
            if (!isRunning || !gameStarted) return;
            moveSnake();
            checkCollisions();
            checkAppleEaten();
            repaint();
        }
        
        private void moveSnake() {
            Point head = snake.getFirst();
            Point newHead = new Point(head);
            
            switch (direction) {
                case UP -> newHead.y -= TILE_SIZE;
                case DOWN -> newHead.y += TILE_SIZE;
                case LEFT -> newHead.x -= TILE_SIZE;
                case RIGHT -> newHead.x += TILE_SIZE;
            }
            
            lastMoveDirection = direction;
            snake.addFirst(newHead);
            snake.removeLast();
        }
        
        private void checkCollisions() {
            Point head = snake.getFirst();

            // 1. Wall collision
            if (head.x < 0 || head.x >= SCREEN_SIZE || 
                head.y < 0 || head.y >= SCREEN_SIZE) {
                gameOver();
                return;
            }
            
            // 2. Self collision
            for (int i = 1; i < snake.size(); i++) {
                if (head.equals(snake.get(i))) {
                    gameOver();
                    return;
                }
            }
        }
        
        private void checkAppleEaten() {

            if (apple == null) return;
            Point head = snake.getFirst();
            if (head.equals(apple)) {
                snake.addLast(new Point(snake.getLast()));
                spawnNewApple(); 
                score += 10;

                // Speed up but not below 50ms
                if (delay > 50) {
                    delay -= 5;
                    gameTimer.setDelay(delay);
                }
            }
        }
        
        private void gameOver() {
            isRunning = false;
            gameTimer.stop();
            
            // Show the restart & exit buttons
            restartButton.setVisible(true);
            exitButton.setVisible(true);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            
            // Antialiasing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Gradient background
            GradientPaint gradientBackground = new GradientPaint(
                0, 0, new Color(40, 40, 40),  
                0, getHeight(), new Color(10, 10, 10)
            );
            g2d.setPaint(gradientBackground);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            if (isRunning) {
                drawGame(g2d);
            } else {
                drawGameOver(g2d);
            }
        }
        
        private void drawGame(Graphics g) {
            // Draw apple
            if (apple != null) {
                g.setColor(Color.RED);
                g.fillOval(apple.x, apple.y, TILE_SIZE, TILE_SIZE);
            }
            
            // Draw snake
            boolean isHead = true;
            int segments = snake.size();
            for (int i = 0; i < segments; i++) {
                Point p = snake.get(i);
                if (isHead) {
                    g.setColor(new Color(255, 192, 203)); // Pink for head
                } else {
                    float ratio = (float) (segments - i) / segments;
                    int pink = 255;
                    int lighter = 192 + (int)((255 - 192) * (1 - ratio));
                    g.setColor(new Color(pink, lighter, lighter));
                }
                g.fillRect(p.x, p.y, TILE_SIZE, TILE_SIZE);
                isHead = false;
            }
            
            // Draw Score with SansSerif
            g.setFont(new Font("SansSerif", Font.BOLD, 24));
            // Shadow effect
            g.setColor(new Color(0, 0, 0, 160));
            g.drawString("Score: " + score, 12, 37);

            // Foreground
            g.setColor(new Color(255, 255, 255, 240));
            g.drawString("Score: " + score, 10, 35);
        }
        
        private void drawGameOver(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
        
            // Use SansSerif for consistency
            Font gameOverFont = new Font("SansSerif", Font.BOLD, 60);
            Font scoreFont = new Font("SansSerif", Font.BOLD, 40);
            g2d.setFont(gameOverFont);
        
            String gameOverText = "Game Over";
            String scoreText = "Final Score: " + score;
        
            // Measure text
            FontMetrics goMetrics = g2d.getFontMetrics(gameOverFont);
            FontMetrics scoreMetrics = g2d.getFontMetrics(scoreFont);
        
            int gameOverX = (getWidth() - goMetrics.stringWidth(gameOverText)) / 2;
            int gameOverY = getHeight() / 2 - 100;
        
            int scoreX = (getWidth() - scoreMetrics.stringWidth(scoreText)) / 2;
            int scoreY = gameOverY + 60;
        
            // Translucent background
            g2d.setColor(new Color(0, 0, 0, 160));
            g2d.fillRoundRect(getWidth() / 2 - 300, gameOverY - 120, 600, 370, 30, 10);
        
            // Shadow + text
            g2d.setColor(new Color(0, 0, 0, 160));
            g2d.drawString(gameOverText, gameOverX + 3, gameOverY + 3);
            g2d.setColor(new Color(255, 192, 203));  // Pink
            g2d.drawString(gameOverText, gameOverX, gameOverY);
        
            // Final Score
            g2d.setFont(scoreFont);
            g2d.setColor(Color.WHITE);
            g2d.drawString(scoreText, scoreX, scoreY);
        }
            
        // Keyboard handling
        private class GameKeyListener extends KeyAdapter {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!gameStarted) return;
                
                Direction newDirection = direction;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> {
                        if (lastMoveDirection != Direction.RIGHT) 
                            newDirection = Direction.LEFT;
                    }
                    case KeyEvent.VK_RIGHT -> {
                        if (lastMoveDirection != Direction.LEFT) 
                            newDirection = Direction.RIGHT;
                    }
                    case KeyEvent.VK_UP -> {
                        if (lastMoveDirection != Direction.DOWN) 
                            newDirection = Direction.UP;
                    }
                    case KeyEvent.VK_DOWN -> {
                        if (lastMoveDirection != Direction.UP) 
                            newDirection = Direction.DOWN;
                    }
                }
                direction = newDirection;
            }
        }


        
        // Test main
        public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> {
                JFrame frame = new JFrame("Snake Game");
                frame.add(new SnakeGame(null)); // parentFrame is null => "Exit to Menu" won't work
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            });
        }
    }
