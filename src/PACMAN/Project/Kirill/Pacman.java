package PACMAN.Project.Kirill;

import java.awt.*;
import java.awt.event.KeyEvent;
public class Pacman {
    private int row, column;
    private int lives;
    private int score;
    private Image[] rightImages;
    private Image[] leftImages;
    private Image[] upImages;
    private Image[] downImages;
    private Image[] invincibleImages;
    private Image[] speedRightImages;
    private Image[] speedLeftImages;
    private Image[] speedUpImages;
    private Image[] speedDownImages;
    private Image[] currentImages;
    private int currentImageIndex = 0;
    private boolean invincible;
    private int speed = 150;
    private boolean isSpeedIncreased = false;
    private Thread speedIncreaseThread;
    private boolean doublePoints;
    private Thread doublePointsThread;

    public Pacman(Point position) {
        this.row = position.x;
        this.column = position.y;
        this.lives = 3;
        this.score = 0;
        this.invincible = false;
        loadPacmanImages();
    }
    private void loadPacmanImages() {
        rightImages = new Image[]{
                ImageLoader.loadImage(Images.politManRight),
                ImageLoader.loadImage(Images.politManRightEat)
        };
        leftImages = new Image[]{
                ImageLoader.loadImage(Images.politManLeft),
                ImageLoader.loadImage(Images.politManLeftEat)
        };
        upImages = new Image[]{
                ImageLoader.loadImage(Images.politManUp),
                ImageLoader.loadImage(Images.politManUpEat)
        };
        downImages = new Image[]{
                ImageLoader.loadImage(Images.politManDown),
                ImageLoader.loadImage(Images.politManDownEat)
        };
        invincibleImages = new Image[]{
                ImageLoader.loadImage(Images.politDead),
                ImageLoader.loadImage(Images.politDead)
        };
        speedRightImages = new Image[]{
                ImageLoader.loadImage(Images.flashPolitManRight),
                ImageLoader.loadImage(Images.flashPolitManRight)
        };
        speedLeftImages = new Image[]{
                ImageLoader.loadImage(Images.flashPolitManLeft),
                ImageLoader.loadImage(Images.flashPolitManLeft)
        };
        speedUpImages = new Image[]{
                ImageLoader.loadImage(Images.flashPolitManUp),
                ImageLoader.loadImage(Images.flashPolitManUp)
        };
        speedDownImages = new Image[]{
                ImageLoader.loadImage(Images.flashPolitManDown),
                ImageLoader.loadImage(Images.flashPolitManDown)
        };

        currentImages = rightImages;
    }
    public void updateAnimation() {
        currentImageIndex = (currentImageIndex + 1) % currentImages.length;
    }
    public void move(int newRow, int newColumn, int direction) {
        this.row = newRow;
        this.column = newColumn;

        if (!invincible && !isSpeedIncreased) {
            switch (direction) {
                case KeyEvent.VK_UP:
                    currentImages = upImages;
                    break;
                case KeyEvent.VK_DOWN:
                    currentImages = downImages;
                    break;
                case KeyEvent.VK_LEFT:
                    currentImages = leftImages;
                    break;
                case KeyEvent.VK_RIGHT:
                    currentImages = rightImages;
                    break;
            }
        } else if (isSpeedIncreased) {
            switch (direction) {
                case KeyEvent.VK_UP:
                    currentImages = speedUpImages;
                    break;
                case KeyEvent.VK_DOWN:
                    currentImages = speedDownImages;
                    break;
                case KeyEvent.VK_LEFT:
                    currentImages = speedLeftImages;
                    break;
                case KeyEvent.VK_RIGHT:
                    currentImages = speedRightImages;
                    break;
            }
        }
    }
    public int getRow() {
        return row;
    }
    public int getColumn() {
        return column;
    }
    public int getLives() {
        return lives;
    }
    public void loseLife() {
        lives--;
    }
    public void addScore(int points) {
        if (doublePoints) {
            points *= 2;
        }
        score += points;
    }
    public int getScore() {
        return score;
    }
    public Image getCurrentImage() {
        return currentImages[currentImageIndex];
    }
    public void addLife() {
        lives++;
    }

    public void startInvincibility() {
        invincible = true;
        currentImages = invincibleImages;
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                invincible = false;
                currentImages = rightImages; // back to reality XD
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    public boolean isInvincible() {
        return invincible;
    }
    public void increaseSpeed() {
        speed = 30;
        isSpeedIncreased = true;
        setSpeedImages();

        if (speedIncreaseThread != null && speedIncreaseThread.isAlive()) {
            speedIncreaseThread.interrupt();
        }
        speedIncreaseThread = new Thread(() -> {
            try {
                Thread.sleep(6000);
                speed = 150;
                isSpeedIncreased = false;
                setNormalImages();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        speedIncreaseThread.start();
    }
    public int getSpeed() {
        return speed;
    }
    public void setSpeedImages() {
        if (currentImages == rightImages) {
            currentImages = speedRightImages;
        } else if (currentImages == leftImages) {
            currentImages = speedLeftImages;
        } else if (currentImages == upImages) {
            currentImages = speedUpImages;
        } else if (currentImages == downImages) {
            currentImages = speedDownImages;
        }
    }
    public void setNormalImages() {
        if (currentImages == speedRightImages) {
            currentImages = rightImages;
        } else if (currentImages == speedLeftImages) {
            currentImages = leftImages;
        } else if (currentImages == speedUpImages) {
            currentImages = upImages;
        } else if (currentImages == speedDownImages) {
            currentImages = downImages;
        }
    }
    public void startDoublePoints() {
        doublePoints = true;
        if (doublePointsThread != null && doublePointsThread.isAlive()) {
            doublePointsThread.interrupt();
        }
        doublePointsThread = new Thread(() -> {
            try {
                Thread.sleep(20000);
                doublePoints = false;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        doublePointsThread.start();
    }
    public void teleport(Point newPosition) {
        this.row = newPosition.x;
        this.column = newPosition.y;
    }
}
