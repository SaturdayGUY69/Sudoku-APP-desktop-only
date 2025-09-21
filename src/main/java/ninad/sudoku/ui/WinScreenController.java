package ninad.sudoku.ui;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import ninad.sudoku.Main;

import java.util.logging.Level;
import java.util.logging.Logger;

public class WinScreenController {

    private static final Logger LOGGER = Logger.getLogger(WinScreenController.class.getName());

    @FXML
    private Canvas confettiCanvas;

    @FXML
    private Label winLabel;

    @FXML
    private Label scoreLabel;

    @FXML
    private Label movesLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private Button mainMenuButton;

    @FXML
    public void initialize() {
        mainMenuButton.setOnAction(e -> {
            try {
                Main.showMainMenu();
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Failed to go back to main menu", ex);
            }
        });

        if (confettiCanvas != null) {
            LOGGER.info("Confetti canvas initialized with size: " +
                    confettiCanvas.getWidth() + "x" + confettiCanvas.getHeight());
        }
    }

    /** Set stats: score, moves, total time in seconds */
    public void setStats(int finalScore, int totalMoves, int totalTimeSeconds) {
        if (scoreLabel != null) scoreLabel.setText("Score: " + finalScore);
        if (movesLabel != null) movesLabel.setText("Moves: " + totalMoves);
        if (timeLabel != null) {
            int mins = totalTimeSeconds / 60;
            int secs = totalTimeSeconds % 60;
            timeLabel.setText(String.format("Time: %02d:%02d", mins, secs));
        }
        if (winLabel != null) {
            winLabel.setText("🎉 Congratulations! You Won in " + totalMoves + " moves! 🎉");
        }
    }
}
