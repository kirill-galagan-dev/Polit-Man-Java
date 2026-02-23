package PACMAN.Project.Kirill;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Ghost {
    private int row, column;
    private Image[] ghostImages;
    private Image frozenImage;
    private boolean frozen = false;
    private int currentImageIndex = 0;
    private Thread animationThread;
    private Random random = new Random();

    public Ghost(Point position) {
        this.row = position.x;
        this.column = position.y;
        loadGhostImages();
        startAnimation();
    }

    private void loadGhostImages() {
        ghostImages = new Image[]{
                ImageLoader.loadImage(Images.ghostBrown),
                ImageLoader.loadImage(Images.ghostBrown2),
                ImageLoader.loadImage(Images.ghostBrown3)
        };
        frozenImage = ImageLoader.loadImage(Images.frozenGhost);
    }

    private void startAnimation() {
        animationThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(180);
                    currentImageIndex = (currentImageIndex + 1) % ghostImages.length;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        animationThread.start();
    }
    public void move(char[][] grid, int pacmanRow, int pacmanColumn) {
        if (frozen) return;
        if (isPacmanInSight(grid, pacmanRow, pacmanColumn)) {
            moveTowardsPacman(grid, pacmanRow, pacmanColumn);
        } else {
            moveRandomly(grid);
        }
    }

    public void updateAnimation() {
        if (frozen) return;
        currentImageIndex = (currentImageIndex + 1) % ghostImages.length;
    }


    public void freeze() {
        frozen = true;
    }

    public void unfreeze() {
        frozen = false;
    }

    private boolean isPacmanInSight(char[][] grid, int pacmanRow, int pacmanColumn) {
        int sightRange = 5;

        for (int i = -sightRange; i <= sightRange; i++) {
            int checkRow = row + i;
            int checkCol = column + i;
            if (checkRow >= 0 && checkRow < grid.length && grid[checkRow][column] != 'X' && pacmanRow == checkRow) {
                return true;
            }
            if (checkCol >= 0 && checkCol < grid[0].length && grid[row][checkCol] != 'X' && pacmanColumn == checkCol) {
                return true;
            }
        }

        return false;
    }

    private void moveTowardsPacman(char[][] grid, int pacmanRow, int pacmanColumn) {
        int[] dRow = {-1, 1, 0, 0};
        int[] dCol = {0, 0, -1, 1};

        int[] distances = new int[4];
        for (int i = 0; i < 4; i++) {
            int newRow = row + dRow[i];
            int newCol = column + dCol[i];
            if (isValidMove(grid, newRow, newCol)) {
                distances[i] = Math.abs(newRow - pacmanRow) + Math.abs(newCol - pacmanColumn);
            } else {
                distances[i] = Integer.MAX_VALUE;
            }
        }

        int minDistance = Integer.MAX_VALUE;
        int bestDirection = -1;
        for (int i = 0; i < 4; i++) {
            if (distances[i] < minDistance) {
                minDistance = distances[i];
                bestDirection = i;
            }
        }

        if (bestDirection != -1) {
            row += dRow[bestDirection];
            column += dCol[bestDirection];
        }
    }

    private void moveRandomly(char[][] grid) {
        int[] dRow = {-1, 1, 0, 0};
        int[] dCol = {0, 0, -1, 1};
        int direction = random.nextInt(4);
        int newRow = row + dRow[direction];
        int newCol = column + dCol[direction];

        if (isValidMove(grid, newRow, newCol)) {
            row = newRow;
            column = newCol;
        } else {
            for (int i = 0; i < 4; i++) {
                direction = (direction + 1) % 4;
                newRow = row + dRow[direction];
                newCol = column + dCol[direction];
                if (isValidMove(grid, newRow, newCol)) {
                    row = newRow;
                    column = newCol;
                    break;
                }
            }
        }
    }

    private boolean isValidMove(char[][] grid, int newRow, int newCol) {
        return newRow >= 0 && newRow < grid.length && newCol >= 0 && newCol < grid[0].length && grid[newRow][newCol] != 'X';
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }



    public Image getCurrentImage() {
        return frozen ? frozenImage : ghostImages[currentImageIndex];
    }

    public void stopAnimation() {
        animationThread.interrupt();
    }
}
