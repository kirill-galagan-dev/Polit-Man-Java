package PACMAN.Project.Kirill;

import java.awt.Image;
import java.awt.Point;

public class PowerUp {
    public enum Type {
        SPEED_INCREASE,
        EXTRA_LIFE,
        DOUBLE_POINTS,
        FREEZE_GHOSTS,
        TELEPORT_PACMAN

    }

    private Type type;
    private Point position;
    private Image image;

    public PowerUp(Type type, Point position, Image image) {
        this.type = type;
        this.position = position;
        this.image = image;
    }

    public Type getType() {
        return type;
    }
    public Point getPosition() {
        return position;
    }

    public Image getImage() {
        return image;
    }
}
