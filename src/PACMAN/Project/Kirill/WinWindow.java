package PACMAN.Project.Kirill;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class WinWindow extends JFrame {
    public WinWindow(int score) {
        setTitle("Polit-Man®");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        Image icon = ImageLoader.loadImage(Images.logo);
        if (icon != null) {
            setIconImage(icon);
        }

        JLabel background = new JLabel(new ImageIcon(Images.Win));
        setContentPane(background);
        background.setLayout(new BorderLayout());


        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridBagLayout());
        bottomPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel scoreLabel = new JLabel("YOUR SCORE: " + score);
        scoreLabel.setFont(new Font("Serif", Font.BOLD, 33));
        scoreLabel.setForeground(Color.ORANGE);

        JButton mainMenuButton = createButton("Main Menu");
        mainMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                SwingUtilities.invokeLater(() -> {
                    MainMenu mainMenu = new MainMenu();
                    mainMenu.setVisible(true);
                });
            }
        });

        bottomPanel.add(scoreLabel, gbc);
        bottomPanel.add(mainMenuButton, gbc);
        background.add(bottomPanel, BorderLayout.SOUTH);
    }
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Serif", Font.BOLD, 33));
        button.setForeground(Color.ORANGE);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);

        return button;
    }
}
