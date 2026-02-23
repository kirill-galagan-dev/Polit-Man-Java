package PACMAN.Project.Kirill;

import java.awt.Image;

public class DefaultImageProvider implements ImageProvider {
    private Image wallImage;
    private Image foodImage;
    private Image roadImage;
    private Image speedImage;
    private Image lifeImage;

    private Image pointsImage;

    private Image freezeImage;

    private Image teleportImage;


    public DefaultImageProvider() {
        wallImage = ImageLoader.loadImage(Images.wall);
        foodImage = ImageLoader.loadImage(Images.money);
        roadImage = ImageLoader.loadImage(Images.road);
        speedImage = ImageLoader.loadImage(Images.speed);
        lifeImage = ImageLoader.loadImage(Images.heart);
        pointsImage = ImageLoader.loadImage(Images.bonusMoney);
        freezeImage = ImageLoader.loadImage(Images.frozenIcon);
        teleportImage = ImageLoader.loadImage(Images.portal);
    }

    @Override
    public Image getWallImage() {
        return wallImage;
    }

    @Override
    public Image getFoodImage() {
        return foodImage;
    }

    @Override
    public Image getRoadImage() {
        return roadImage;
    }

    @Override
    public Image getSpeedIncreaseImage() {
        return speedImage;
    }

    @Override
    public Image getExtraLifeImage() {
        return lifeImage;
    }

    @Override
    public Image getBonusMoneyImage() {
        return pointsImage;
    }

    @Override
    public Image getFreezeGhostsImage() {
        return freezeImage;
    }

    @Override
    public Image getTeleportImage() {
        return teleportImage;
    }


}
