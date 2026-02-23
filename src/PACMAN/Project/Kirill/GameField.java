package PACMAN.Project.Kirill;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.*;

public class GameField extends JPanel {
    private char[][] grid;
    private int rows, columns;
    private Pacman pacman;
    private Ghost[] ghosts;
    private JLabel livesLabel;
    private JLabel scoreLabel;
    private JLabel timeLabel;
    private Set<Point> food;
    private Set<PowerUp> powerUps;
    private String playerName;
    private Thread gameThread;
    private ImageProvider imageProvider;
    private boolean gameEnded = false;

    long startTime;

    private final int cellSize = 30;
    private long lastMoveTime = 0;
    private JLabel[][] gridLabels;

    private BufferedImage offScreenImage;
    private Graphics2D offScreenGraphics;
    private Random random = new Random();

    public GameField(int rows, int columns, JLabel livesLabel, JLabel scoreLabel, JLabel timeLabel, String fieldSize, String playerName) {
        this.rows = rows;
        this.columns = columns;
        this.livesLabel = livesLabel;
        this.scoreLabel = scoreLabel;
        this.timeLabel = timeLabel;
        this.grid = new char[rows][columns];
        this.food = new HashSet<>();
        this.powerUps = new HashSet<>();
        this.playerName = playerName;
        this.imageProvider = new DefaultImageProvider();
        gridLabels = new JLabel[rows][columns];

        setLayout(new GridLayout(rows, columns));
        generateMaze();
        addBoundaryPassages();
        pacman = new Pacman(findFreePosition());
        initializeGhosts(fieldSize);
        initializeGrid();

        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

        offScreenImage = new BufferedImage(columns * cellSize, rows * cellSize, BufferedImage.TYPE_INT_ARGB);
        offScreenGraphics = offScreenImage.createGraphics();

        startGame();
        startTimer();
    }

    private void startGame() {
        gameEnded = false;
        gameThread = new Thread(() -> {
            long lastUpdateTime = System.currentTimeMillis();
            long lastRenderTime = System.currentTimeMillis();
            long lastPowerUpTime = System.currentTimeMillis();

            while (!Thread.currentThread().isInterrupted() && !gameEnded) {
                long currentTime = System.currentTimeMillis();

                if (currentTime - lastUpdateTime >= 300) {
                    updateGameLogic();
                    lastUpdateTime = currentTime;
                }

                if (currentTime - lastRenderTime >= 80) {
                    renderGame();
                    lastRenderTime = currentTime;
                }

                if (currentTime - lastPowerUpTime >= 5000) {
                    generatePowerUp();
                    lastPowerUpTime = currentTime;
                }

                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        gameThread.start();
    }

    private void updateGameLogic() {
        pacman.updateAnimation();
        for (Ghost ghost : ghosts) {
            ghost.updateAnimation();
            ghost.move(grid, pacman.getRow(), pacman.getColumn());
        }
        checkCollisionWithGhost();
        checkCollisionWithPowerUp();
    }

    private void renderGame() {
        SwingUtilities.invokeLater(this::updatePacmanAndGhosts);
    }

    private void updatePacmanAndGhosts() {
        if (offScreenGraphics == null) {
            return;
        }

        offScreenGraphics.clearRect(0, 0, offScreenImage.getWidth(), offScreenImage.getHeight());

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (grid[i][j] != 'X') {
                    offScreenGraphics.drawImage(imageProvider.getRoadImage().getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH), j * cellSize, i * cellSize, null);
                    if (food.contains(new Point(i, j))) {
                        offScreenGraphics.drawImage(imageProvider.getFoodImage().getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH), j * cellSize, i * cellSize, null);
                    }
                } else {
                    offScreenGraphics.drawImage(imageProvider.getWallImage().getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH), j * cellSize, i * cellSize, null);
                }
            }
        }

        // Polit-man and game
        offScreenGraphics.drawImage(pacman.getCurrentImage().getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH), pacman.getColumn() * cellSize, pacman.getRow() * cellSize, null);
        for (Ghost ghost : ghosts) {
            offScreenGraphics.drawImage(ghost.getCurrentImage().getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH), ghost.getColumn() * cellSize, ghost.getRow() * cellSize, null);
        }

        // PowerUps...
        for (PowerUp powerUp : powerUps) {
            offScreenGraphics.drawImage(powerUp.getImage().getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH), powerUp.getPosition().y * cellSize, powerUp.getPosition().x * cellSize, null);
        }

        Graphics g = getGraphics();
        if (g != null) {
            g.drawImage(offScreenImage, 0, 0, this);
            g.dispose();
        }
    }

    private void generatePowerUp() {
        if (random.nextFloat() < 0.25) {
            Point position = findFreePosition();
            PowerUp.Type powerUpType = PowerUp.Type.values()[random.nextInt(PowerUp.Type.values().length)];
            Image powerUpImage;
            switch (powerUpType) {
                case SPEED_INCREASE:
                    powerUpImage = imageProvider.getSpeedIncreaseImage();
                    break;
                case DOUBLE_POINTS:
                    powerUpImage = imageProvider.getBonusMoneyImage();
                    break;
                case EXTRA_LIFE:
                    powerUpImage = imageProvider.getExtraLifeImage();
                    break;
                case FREEZE_GHOSTS:
                    powerUpImage = imageProvider.getFreezeGhostsImage();
                    break;
                case TELEPORT_PACMAN:
                    powerUpImage = imageProvider.getTeleportImage();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + powerUpType);
            }
            PowerUp powerUp = new PowerUp(powerUpType, position, powerUpImage);
            powerUps.add(powerUp);
            gridLabels[position.x][position.y].setIcon(new ImageIcon(powerUp.getImage().getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH)));

            // Удаление бонуса через 10 секунд
            new Thread(() -> {
                try {
                    Thread.sleep(15000);
                    if (powerUps.contains(powerUp)) {
                        powerUps.remove(powerUp);
                        SwingUtilities.invokeLater(() -> gridLabels[position.x][position.y].setIcon(new ImageIcon(imageProvider.getRoadImage().getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH))));
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }

    private void checkCollisionWithPowerUp() {
        Iterator<PowerUp> iterator = powerUps.iterator();
        while (iterator.hasNext()) {
            PowerUp powerUp = iterator.next();
            if (pacman.getRow() == powerUp.getPosition().x && pacman.getColumn() == powerUp.getPosition().y) {
                applyPowerUp(powerUp);
                iterator.remove();
                gridLabels[powerUp.getPosition().x][powerUp.getPosition().y].setIcon(new ImageIcon(imageProvider.getRoadImage().getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH)));
            }
        }
    }

    private void freezeGhosts() {
        for (Ghost ghost : ghosts) {
            ghost.freeze();
        }
        new Thread(() -> {
            try {
                Thread.sleep(10000);
                for (Ghost ghost : ghosts) {
                    ghost.unfreeze();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void applyPowerUp(PowerUp powerUp) {
        switch (powerUp.getType()) {
            case SPEED_INCREASE:
                pacman.increaseSpeed();
                break;
            case DOUBLE_POINTS:
                pacman.startDoublePoints();
                break;
            case EXTRA_LIFE:
                pacman.addLife();
                updateLivesLabel();
                break;
            case FREEZE_GHOSTS:
                freezeGhosts();
                break;
            case TELEPORT_PACMAN:
                Point newPosition = findFreePosition();
                pacman.teleport(newPosition);
                break;
            // If we need more we add more...
        }
    }

    private void initializeGrid() {
        removeAll();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                JLabel cellLabel = new JLabel();
                cellLabel.setHorizontalAlignment(JLabel.CENTER);
                cellLabel.setVerticalAlignment(JLabel.CENTER);
                if (grid[i][j] == 'X') {
                    cellLabel.setIcon(new ImageIcon(imageProvider.getWallImage().getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH)));
                } else {
                    cellLabel.setIcon(new ImageIcon(imageProvider.getRoadImage().getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH)));
                    if (food.contains(new Point(i, j))) {
                        cellLabel.setIcon(new ImageIcon(imageProvider.getFoodImage().getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH)));
                    }
                }
                gridLabels[i][j] = cellLabel;
                add(cellLabel);
            }
        }
        revalidate();
        repaint();
    }

    private void generateMaze() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                grid[i][j] = 'X';
            }
        }
        carvePassage(0, 0);
        placeFood();
    }

    private void carvePassage(int cx, int cy) {
        Direction[] dirs = Direction.values();
        Collections.shuffle(Arrays.asList(dirs));
        for (Direction dir : dirs) {
            int nx = cx + dir.dx;
            int ny = cy + dir.dy;
            int nx2 = cx + dir.dx * 2;
            int ny2 = cy + dir.dy * 2;
            if (inBounds(nx2, ny2) && grid[ny2][nx2] == 'X') {
                grid[ny][nx] = ' ';
                grid[ny2][nx2] = ' ';
                carvePassage(nx2, ny2);
            }
        }
    }

    private void addBoundaryPassages() {
        for (int i = 1; i < rows - 1; i++) {
            if (grid[i][1] == ' ') {
                grid[i][0] = ' ';
                grid[i][columns - 1] = ' ';
            }
        }
        for (int j = 1; j < columns - 1; j++) {
            if (grid[1][j] == ' ') {
                grid[0][j] = ' ';
                grid[rows - 1][j] = ' ';
            }
        }
    }

    private void placeFood() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (grid[i][j] == ' ') {
                    food.add(new Point(i, j));
                }
            }
        }
    }

    private boolean inBounds(int x, int y) {
        return x >= 0 && x < columns && y >= 0 && y < rows;
    }

    private Point findFreePosition() {
        Random random = new Random();
        int x, y;
        do {
            x = random.nextInt(columns);
            y = random.nextInt(rows);
        } while (grid[y][x] == 'X');
        return new Point(y, x);
    }

    private enum Direction {
        NORTH(0, -1), SOUTH(0, 1), EAST(1, 0), WEST(-1, 0);
        int dx, dy;

        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }

    private void initializeGhosts(String fieldSize) {
        int numGhosts;
        switch (fieldSize) {
            case "Small":
                numGhosts = 2;
                break;
            case "Medium":
                numGhosts = 3;
                break;
            case "Large":
                numGhosts = 4;
                break;
            case "Extra Large":
                numGhosts = 5;
                break;
            case "Gigantic":
                numGhosts = 5;
                break;
            default:
                numGhosts = 4;
        }

        ghosts = new Ghost[numGhosts];
        Point pacmanStartPosition = new Point(pacman.getRow(), pacman.getColumn());
        Set<Point> occupiedPositions = new HashSet<>();
        occupiedPositions.add(pacmanStartPosition);

        for (int i = 0; i < numGhosts; i++) {
            Point ghostPosition;
            do {
                ghostPosition = findFreePosition();
            } while (getDistance(pacmanStartPosition, ghostPosition) < 5 || occupiedPositions.contains(ghostPosition));
            ghosts[i] = new Ghost(ghostPosition);
            occupiedPositions.add(ghostPosition);
        }
    }

    private double getDistance(Point p1, Point p2) {
        int dx = p1.x - p2.x;
        int dy = p1.y - p2.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private void handleKeyPress(KeyEvent e) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastMoveTime < pacman.getSpeed()) {
            return;
        }

        int keyCode = e.getKeyCode();
        int newRow = pacman.getRow();
        int newColumn = pacman.getColumn();

        switch (keyCode) {
            case KeyEvent.VK_UP:
                newRow--;
                break;
            case KeyEvent.VK_DOWN:
                newRow++;
                break;
            case KeyEvent.VK_LEFT:
                newColumn = pacman.getColumn() - 1;
                break;
            case KeyEvent.VK_RIGHT:
                newColumn = pacman.getColumn() + 1;
                break;
            default:
                newColumn = pacman.getColumn();
        }

        if (isValidMove(newRow, newColumn)) {
            pacman.move(newRow, newColumn, keyCode);
            checkFoodCollision();
        } else {
            Point wrappedPosition = wrapAround(newRow, newColumn);
            newRow = wrappedPosition.x;
            newColumn = wrappedPosition.y;
            if (isValidMove(newRow, newColumn)) {
                pacman.move(newRow, newColumn, keyCode);
                checkFoodCollision();
            }
        }

        checkCollisionWithGhost();
        lastMoveTime = currentTime;
    }

    private void checkFoodCollision() {
        Point pacmanPos = new Point(pacman.getRow(), pacman.getColumn());
        if (food.contains(pacmanPos)) {
            food.remove(pacmanPos);
            pacman.addScore(10);
            updateScoreLabel();
            checkWinCondition();
        }
    }

    private void checkCollisionWithGhost() {
        if (pacman.isInvincible()) return;

        for (Ghost ghost : ghosts) {
            if (pacman.getRow() == ghost.getRow() && pacman.getColumn() == ghost.getColumn()) {
                pacman.loseLife();
                updateLivesLabel();
                pacman.startInvincibility();
                if (pacman.getLives() <= 0 && !gameEnded) {
                    gameEnded = true;
                    saveHighScore();
                    gameOver();
                }
                break;
            }
        }
    }

    private void updateLivesLabel() {
        livesLabel.setText("Lives: " + pacman.getLives());
    }

    private void updateScoreLabel() {
        scoreLabel.setText("Score: " + pacman.getScore());
    }

    private void startTimer() {
        startTime = System.currentTimeMillis();
        gameThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(1000);
                    SwingUtilities.invokeLater(this::updateTimer);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        gameThread.start();
    }

    private void updateTimer() {
        long elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000;
        timeLabel.setText("Time: " + elapsedSeconds);
    }

    private void saveHighScore() {
        HighScoresManager.addHighScore(new HighScore(playerName, pacman.getScore()));
    }

    private void gameOver() {
        stopAllThreads();
        JOptionPane.showMessageDialog(this, "Game Over! Your score: " + pacman.getScore());
        System.out.println("Game over. Score: " + pacman.getScore());
        SwingUtilities.invokeLater(() -> {
            System.out.println("Creating new MainMenu instance.");
            MainMenu mainMenu = new MainMenu();
            mainMenu.setVisible(true);
        });
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            System.out.println("Disposing current window.");
            window.dispose();
        }
    }

    public void exitToMainMenu() {
        stopAllThreads();
        SwingUtilities.invokeLater(() -> {
            MainMenu mainMenu = new MainMenu();
            mainMenu.setVisible(true);
        });
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }
        stopAllThreads();
    }

    private void checkWinCondition() {
        if (food.isEmpty() && !gameEnded) {
            gameEnded = true;
            saveHighScore();
            winGame();
        }
    }

    private void winGame() {
        stopAllThreads();
        int finalScore = pacman.getScore();
        System.out.println("Win game. Final score: " + finalScore);
        SwingUtilities.invokeLater(() -> {
            WinWindow winWindow = new WinWindow(finalScore);
            winWindow.setVisible(true);
        });
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }
    }

    private Point wrapAround(int row, int column) {
        if (row < 0) {
            row = rows - 1;
        } else if (row >= rows) {
            row = 0;
        }
        if (column < 0) {
            column = columns - 1;
        } else if (column >= columns) {
            column = 0;
        }
        return new Point(row, column);
    }

    private boolean isValidMove(int row, int column) {
        return row >= 0 && row < rows && column >= 0 && column < columns && grid[row][column] != 'X';
    }

    private void stopAllThreads() {
        if (gameThread != null && gameThread.isAlive()) {
//            System.out.println("Check");
            gameThread.interrupt();
        }
        for (Ghost ghost : ghosts) {
            ghost.stopAnimation();
        }
    }

    @Override
    public void removeNotify() {
//        System.out.println("Check2");
        stopAllThreads();
        super.removeNotify();
    }
}
