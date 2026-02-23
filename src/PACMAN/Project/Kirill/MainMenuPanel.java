package PACMAN.Project.Kirill;

import javax.swing.*;
import java.awt.*;
public class MainMenuPanel extends JPanel {
    private Image backgroundImage;
    private JLabel backgroundLabel;

    public MainMenuPanel() {
        backgroundImage = ImageLoader.loadImage(Images.BackgroundMainMenu);

        if (backgroundImage != null) {
            backgroundLabel = new JLabel(new ImageIcon(backgroundImage));
            backgroundLabel.setLayout(new GridBagLayout());
            setLayout(new OverlayLayout(this));
            add(backgroundLabel);
        } else {
            setLayout(new GridBagLayout());
        }
    }

    @Override
    public void add(Component comp, Object constraints) {
        if (backgroundLabel != null) {
            backgroundLabel.add(comp, constraints);
        } else {
            super.add(comp, constraints);
        }
    }
}