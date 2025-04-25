package games.Game2;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import main.Angelfun;

public class Canvagame extends JPanel {

    private int x, y, brushSize = 5;  // Default brush size
    private boolean eraserMode = false;
    private BufferedImage image;      // Buffered image for drawing
    private Graphics2D g2d;          // Graphics2D for drawing
    private Color currentColor = Color.WHITE; // Default brush color
    private Color canvasColor = Color.BLACK;  // Default canvas color
    private Angelfun parentFrame;

    public Canvagame(Angelfun parentFrame) {
        this.parentFrame = parentFrame;

        // Prevent fullscreen by fixing the window size and disabling resize
        this.parentFrame.setSize(900, 600);
        this.parentFrame.setResizable(false);

        // Set up our panel
        setPreferredSize(new Dimension(900, 520));
        setBackground(canvasColor);
        setLayout(new BorderLayout());

        // Create a blank image for drawing & initialize Graphics2D
        image = new BufferedImage(900, 520, BufferedImage.TYPE_INT_ARGB);
        g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Fill the background with the initial canvas color
        g2d.setColor(canvasColor);
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
        
        // Set the default brush color
        g2d.setColor(currentColor);

        // Mouse event listeners to handle drawing
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                x = e.getX();
                y = e.getY();
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                g2d.setColor(eraserMode ? canvasColor : currentColor);
                g2d.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawLine(x, y, e.getX(), e.getY());
                x = e.getX();
                y = e.getY();
                repaint();
            }
        });

        // Build the user interface (buttons, sliders, etc.)
        setupUI();
    }

    private void setupUI() {
        // Two rows of controls
        JPanel topButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JPanel bottomButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        // 1. Top row
        JButton menuButton = new JButton("Return to Menu");
        menuButton.addActionListener(e -> {
            parentFrame.switchToPanel("MainMenu");
            parentFrame.revalidate();
            parentFrame.repaint();
        });
        topButtonsPanel.add(menuButton);

        JButton saveButton = new JButton("Save as PNG");
        saveButton.addActionListener(e -> saveCanvas());
        topButtonsPanel.add(saveButton);

        JToggleButton eraserToggle = new JToggleButton("Eraser OFF");
        eraserToggle.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                setEraser(true);
                eraserToggle.setText("Eraser ON");
            } else {
                setEraser(false);
                eraserToggle.setText("Eraser OFF");
            }
        });
        topButtonsPanel.add(eraserToggle);

        JLabel brushLabel = new JLabel("Brush Size:");
        topButtonsPanel.add(brushLabel);

        JSlider brushSlider = new JSlider(JSlider.HORIZONTAL, 1, 50, brushSize);
        brushSlider.setMajorTickSpacing(10);
        brushSlider.setPaintTicks(true);
        // No numeric labels
        brushSlider.addChangeListener(e -> {
            int size = ((JSlider) e.getSource()).getValue();
            setBrushSize(size);
        });
        topButtonsPanel.add(brushSlider);

        // 2. Bottom row
        JButton brushColorButton = new JButton("Brush Color");
        brushColorButton.addActionListener(e -> {
            Color chosenColor = JColorChooser.showDialog(this, "Choose Brush Color", currentColor);
            if (chosenColor != null) {
                setColor(chosenColor);
            }
        });
        bottomButtonsPanel.add(brushColorButton);

        JButton canvasColorButton = new JButton("Canvas Color");
        canvasColorButton.addActionListener(e -> {
            Color chosenColor = JColorChooser.showDialog(this, "Choose Canvas Color", canvasColor);
            if (chosenColor != null) {
                setCanvasColor(chosenColor);
            }
        });
        bottomButtonsPanel.add(canvasColorButton);

        JButton clearButton = new JButton("Clear Canvas");
        clearButton.addActionListener(e -> clearCanvas());
        bottomButtonsPanel.add(clearButton);

        // Put top & bottom rows in a container
        JPanel bottomContainer = new JPanel();
        bottomContainer.setLayout(new BoxLayout(bottomContainer, BoxLayout.Y_AXIS));
        bottomContainer.add(topButtonsPanel);
        bottomContainer.add(bottomButtonsPanel);

        // Attach bottomContainer at the SOUTH region
        add(bottomContainer, BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
    }

    // --------------------------------------------------------------------------
    //  Helper methods for brush color, canvas color, eraser mode, clearing, saving
    // --------------------------------------------------------------------------

    public void setColor(Color color) {
        currentColor = color;
    }

    public void setBrushSize(int size) {
        brushSize = size;
    }

    public void setEraser(boolean eraser) {
        eraserMode = eraser;
    }

    /**
     * Changes the canvas color WITHOUT erasing the existing drawing.
     * We replace only pixels matching the old color, so all drawn strokes remain.
     */
    public void setCanvasColor(Color newCanvasColor) {
        // 1) For each pixel that exactly matches the old background color,
        //    replace it with the new color.
        int oldRGB = canvasColor.getRGB();
        int newRGB = newCanvasColor.getRGB();

        for (int ix = 0; ix < image.getWidth(); ix++) {
            for (int iy = 0; iy < image.getHeight(); iy++) {
                if (image.getRGB(ix, iy) == oldRGB) {
                    image.setRGB(ix, iy, newRGB);
                }
            }
        }

        // 2) Update our local reference to the new color
        canvasColor = newCanvasColor;
        repaint();
    }

    public void clearCanvas() {
        // Fill the entire canvas with the current canvasColor (erases the drawing)
        g2d.setColor(canvasColor);
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
        repaint();
    }

    public void saveCanvas() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Canvas");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try {
                String fileName = fileToSave.getAbsolutePath();
                if (!fileName.toLowerCase().endsWith(".png")) {
                    fileName += ".png";
                }
                ImageIO.write(image, "PNG", new File(fileName));
                JOptionPane.showMessageDialog(this,
                    "Canvas saved as " + fileName);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                    "Error saving canvas: " + e.getMessage());
            }
        }
    }
}
