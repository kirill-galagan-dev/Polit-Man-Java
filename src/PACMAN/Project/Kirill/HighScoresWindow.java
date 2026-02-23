package PACMAN.Project.Kirill;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class HighScoresWindow extends JFrame {

    private JList<String> highScoresList;
    private DefaultListModel<String> listModel;

    public HighScoresWindow() {
        setTitle("LeaderBoard Scores:");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        Image icon = ImageLoader.loadImage(Images.logo);
        if (icon != null) {
            setIconImage(icon);
        }

        // Create a panel with a background image
        JLabel background = new JLabel(new ImageIcon(Images.HighScoresBackground));
        setContentPane(background);
        background.setLayout(new GridBagLayout());

        listModel = new DefaultListModel<>();
        highScoresList = new JList<>(listModel);
        highScoresList.setOpaque(false); // Make list transparent
        highScoresList.setBackground(new Color(0, 0, 0, 0)); // Set background to transparent
        highScoresList.setForeground(Color.WHITE); // Set text color to white
        highScoresList.setFont(new Font("Serif", Font.BOLD, 16));
        highScoresList.setCellRenderer(new CenteredListCellRenderer());

        JScrollPane scrollPane = new JScrollPane(highScoresList);
        scrollPane.setOpaque(false); // Make scroll pane transparent
        scrollPane.getViewport().setOpaque(false); // Make viewport transparent
        scrollPane.setBorder(null); // Remove border

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;

        background.add(scrollPane, gbc);

        loadHighScores();
    }

    public void loadHighScores() {
        List<HighScore> highScores = HighScoresManager.loadHighScores();
        for (HighScore score : highScores) {
            listModel.addElement(score.getName() + " - " + score.getScore());
        }
    }

    private static class CenteredListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            return label;
        }
    }

}
