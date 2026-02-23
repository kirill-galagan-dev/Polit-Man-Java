package PACMAN.Project.Kirill;

import javax.swing.*;
import java.awt.*;

public class GameFieldFrame extends JFrame {

    public GameFieldFrame(int rows, int columns, String fieldSize, String playerName) {
        setTitle("Polit-Man®");
        int cellSize = 30;
        setSize(columns * cellSize + 20, rows * cellSize + 60);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);



        Image icon = ImageLoader.loadImage(Images.logo);
        if (icon != null) {
            setIconImage(icon);
        }

        JLabel livesLabel = new JLabel("Lives: 3");
        JLabel scoreLabel = new JLabel("Score: 0");
        JLabel timeLabel = new JLabel("Time: 0");
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(1, 3));
        infoPanel.add(livesLabel);
        infoPanel.add(scoreLabel);
        infoPanel.add(timeLabel);

        GameField gameField = new GameField(rows, columns, livesLabel, scoreLabel, timeLabel, fieldSize, playerName);
        add(infoPanel, BorderLayout.NORTH);
        add(gameField, BorderLayout.CENTER);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                gameField.exitToMainMenu();
            }
        });

        setVisible(true);
    }
}
