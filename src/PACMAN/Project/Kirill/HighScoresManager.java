package PACMAN.Project.Kirill;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HighScoresManager {
    private static final String HIGH_SCORES_FILE = "highscores.ser";

    public static void addHighScore(HighScore highScore) {
        List<HighScore> highScores = loadHighScores();
        highScores.add(highScore);
        saveHighScores(highScores);
    }

    public static List<HighScore> loadHighScores() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(HIGH_SCORES_FILE))) {
            return (List<HighScore>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private static void saveHighScores(List<HighScore> highScores) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(HIGH_SCORES_FILE))) {
            oos.writeObject(highScores);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
