package PACMAN.Project.Kirill;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JFrame {
    public MainMenu() {
        setTitle("Polit-Man®");
        setSize(768, 1024);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        Image icon = ImageLoader.loadImage(Images.logo);
        if (icon != null) {
            setIconImage(icon);
        }

        MainMenuPanel mainPanel = new MainMenuPanel();
        setContentPane(mainPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;

        JButton newGameButton = createButton("New Game");
        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openNewGameMenu();
            }
        });

        JButton highScoresButton = createButton("High Scores");
        highScoresButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openHighScoresWindow();
            }
        });

        JButton exitButton = createButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        mainPanel.add(newGameButton, gbc);
        mainPanel.add(highScoresButton, gbc);
        mainPanel.add(exitButton, gbc);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("ITALIC", Font.BOLD, 32));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);

        // Set button to light gray when pressed
        button.getModel().addChangeListener(e -> {
            if (button.getModel().isPressed()) {
                button.setForeground(Color.LIGHT_GRAY);
            } else {
                button.setForeground(Color.WHITE);
            }
        });

        return button;
    }

    private void openNewGameMenu() {
        JFrame newGameMenu = new JFrame("Select Field Size");
        newGameMenu.setSize(400, 299);
        newGameMenu.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        newGameMenu.setLocationRelativeTo(null);
        newGameMenu.setResizable(false);

        Image icon = ImageLoader.loadImage(Images.logo);
        if (icon != null) {
            newGameMenu.setIconImage(icon);
        }

        newGameMenu.setLayout(new GridLayout(5, 1));

        JButton smallButton = createMapSizeButton("Small", Images.ButtonsBackGroundSmall);
        JButton mediumButton = createMapSizeButton("Medium", Images.ButtonsBackGroundMed);
        JButton largeButton = createMapSizeButton("Large", Images.ButtonsBackGroundLarge);
        JButton extraLargeButton = createMapSizeButton("Extra Large", Images.ButtonsBackGroundExtraLarge);
        JButton hugeButton = createMapSizeButton("Gigantic", Images.ButtonsBackGroundGigant);

        newGameMenu.add(smallButton);
        newGameMenu.add(mediumButton);
        newGameMenu.add(largeButton);
        newGameMenu.add(extraLargeButton);
        newGameMenu.add(hugeButton);

        newGameMenu.setVisible(true);
    }

    private JButton createMapSizeButton(String size, String imagePath) {
        JButton button = new JButton();
        ImageIcon icon = new ImageIcon(imagePath);
        button.setIcon(icon);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String playerName = JOptionPane.showInputDialog(button, "Enter your name:");
                if (playerName != null && !playerName.isEmpty()) {
                    int rows, columns;
                    switch (size) {
                        case "Small":
                            rows = 10;
                            columns = 15;
                            break;
                        case "Medium":
                            rows = 15;
                            columns = 15;
                            break;
                        case "Large":
                            rows = 20;
                            columns = 20;
                            break;
                        case "Extra Large":
                            rows = 25;
                            columns = 25;
                            break;
                        case "Gigantic":
                            rows = 25;
                            columns = 30;
                            break;
                        default:
                            rows = 15;
                            columns = 15;
                    }
                    new GameFieldFrame(rows, columns, size, playerName).setVisible(true);
                    JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(button);
                    frame.dispose();
                    dispose();
                }
            }
        });

        return button;
    }

    private void openHighScoresWindow() {
        HighScoresWindow highScoresWindow = new HighScoresWindow();
        highScoresWindow.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainMenu().setVisible(true);
        });
    }
}
