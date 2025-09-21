package ninad.sudoku.ui;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import ninad.sudoku.Main;
import ninad.sudoku.logic.SudokuGenerator;
import ninad.sudoku.logic.SudokuValidator;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.function.UnaryOperator;

public class GameScreenController {

    private static final Logger LOGGER = Logger.getLogger(GameScreenController.class.getName());

    @FXML private GridPane sudokuGrid;
    @FXML private Label timerLabel, pauseTimerLabel, pauseMovesLabel, scoreLabel;
    @FXML private Button pauseButton;
    @FXML private Button overlayResumeButton, overlayGiveUpButton;
    @FXML private VBox pauseOverlay;

    private static final int SIZE = 9;
    private static final int CELL_SIZE = 50;

    private int moves = 0;
    private int elapsedSeconds = 0;
    private int score = 0;
    private int streak = 0;

    private int basePoints, streakIncrement, timePenalty;

    private int[][] puzzleGrid;
    private int[][] solutionGrid;
    private int[][] userGrid;

    private int totalBlanks = 0;
    private int correctCount = 0;

    private Timeline timer;

    @FXML
    public void initialize() {
        final String difficulty = "easy"; // can be dynamic
        SudokuGenerator generator = new SudokuGenerator(difficulty);
        puzzleGrid = generator.getPuzzleGrid();
        solutionGrid = generator.getSolutionGrid();
        userGrid = new int[SIZE][SIZE];

        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                if (puzzleGrid[r][c] == 0) totalBlanks++;

        switch (difficulty.toLowerCase()) {
            case "easy" -> { basePoints = 5; streakIncrement = 1; timePenalty = 10; }
            case "medium" -> { basePoints = 7; streakIncrement = 3; timePenalty = 7; }
            case "hard" -> { basePoints = 10; streakIncrement = 5; timePenalty = 5; }
        }

        buildSudokuGrid();
        populateGrid();
        startTimer();

        pauseButton.setOnAction(e -> pauseGame());
        overlayResumeButton.setOnAction(e -> resumeGame());
        overlayGiveUpButton.setOnAction(e -> giveUp());
    }

    private void buildSudokuGrid() {
        sudokuGrid.getChildren().clear();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                TextField cell = new TextField();
                cell.setPrefSize(CELL_SIZE, CELL_SIZE);
                cell.setAlignment(Pos.CENTER);
                cell.setStyle(getCellBorderStyle(row, col) + "-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #222222;");

                // Single-digit filter
                UnaryOperator<TextFormatter.Change> filter = change -> {
                    String text = change.getText();
                    if (!text.matches("[1-9]?")) return null;
                    String newText = change.getControlNewText();
                    return newText.length() > 1 ? null : change;
                };
                cell.setTextFormatter(new TextFormatter<>(filter));

                sudokuGrid.add(cell, col, row);
            }
        }
    }

    private void populateGrid() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                TextField cell = (TextField) getNodeByRowColumnIndex(row, col, sudokuGrid);
                if (cell == null) continue;

                int value = puzzleGrid[row][col];
                if (value != 0) {
                    cell.setText(String.valueOf(value));
                    cell.setDisable(true);
                    cell.setStyle(getCellBorderStyle(row, col) + "-fx-background-color: #e0e0e0; -fx-font-weight: bold; -fx-font-size: 18px;");
                    userGrid[row][col] = value;
                } else {
                    int r = row, c = col;
                    cell.textProperty().addListener((obs, oldVal, newVal) -> {
                        if (newVal.isEmpty()) {
                            if (userGrid[r][c] != 0) correctCount--;
                            userGrid[r][c] = 0;
                            streak = 0;
                            updateScore();
                            cell.setStyle(getCellBorderStyle(r, c));
                            return;
                        }
                        try {
                            int val = Integer.parseInt(newVal);
                            moves++;
                            if (isValidMove(r, c, val)) {
                                if (userGrid[r][c] == 0) correctCount++;
                                streak++;
                                int points = basePoints + streakIncrement * (streak - 1);
                                score += points;
                                userGrid[r][c] = val;
                                cell.setStyle(getCellBorderStyle(r, c) + "-fx-background-color: #c8e6c9;");
                            } else {
                                streak = 0;
                                userGrid[r][c] = 0;
                                cell.setStyle(getCellBorderStyle(r, c) + "-fx-background-color: #ffcdd2;");
                                PauseTransition pt = new PauseTransition(Duration.seconds(1));
                                pt.setOnFinished(event -> cell.setText(""));
                                pt.play();
                            }
                            updateScore();
                            checkAutoWin();
                        } catch (NumberFormatException ex) {
                            cell.setText("");
                        }
                    });

                    cell.focusedProperty().addListener((obs, oldVal, newVal) -> {
                        if (!newVal && !cell.getText().isEmpty()) {
                            int val = Integer.parseInt(cell.getText());
                            if (!isValidMove(r, c, val)) {
                                cell.setText("");
                                cell.setStyle(getCellBorderStyle(r, c));
                                userGrid[r][c] = 0;
                            }
                        }
                    });
                }
            }
        }
    }

    private boolean isValidMove(int row, int col, int val) {
        int[][] tempGrid = new int[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++)
            System.arraycopy(userGrid[r], 0, tempGrid[r], 0, SIZE);
        tempGrid[row][col] = val;
        return SudokuValidator.validateSingle(tempGrid, row, col);
    }

    private javafx.scene.Node getNodeByRowColumnIndex(int row, int column, GridPane gridPane) {
        for (javafx.scene.Node node : gridPane.getChildren()) {
            Integer r = GridPane.getRowIndex(node);
            Integer c = GridPane.getColumnIndex(node);
            if (r != null && c != null && r == row && c == column) return node;
        }
        return null;
    }

    private String getCellBorderStyle(int row, int col) {
        int top = 1, right = 1, bottom = 1, left = 1;
        if (row % 3 == 2) bottom = 2;
        if (col % 3 == 2) right = 2;
        return String.format("-fx-border-color: black; -fx-border-width: %d %d %d %d;", top, right, bottom, left);
    }

    private void pauseGame() {
        if (pauseTimerLabel != null) pauseTimerLabel.setText("Time: " + formatTime(elapsedSeconds));
        if (pauseMovesLabel != null) pauseMovesLabel.setText("Moves: " + moves);
        if (pauseOverlay != null) pauseOverlay.setVisible(true);
        if (timer != null) timer.pause();
    }

    private void resumeGame() {
        if (pauseOverlay != null) pauseOverlay.setVisible(false);
        if (timer != null) timer.play();
    }

    private void giveUp() {
        if (timer != null) timer.stop();
        if (pauseOverlay != null) pauseOverlay.setVisible(false);
        try {
            Main.showGiveUpScreen(puzzleGrid, solutionGrid);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Failed to show GiveUpScreen", ex);
        }
    }

    private void startTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            elapsedSeconds++;
            if (timerLabel != null) timerLabel.setText(formatTime(elapsedSeconds));
            updateScore();
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private String formatTime(int totalSeconds) {
        int mins = totalSeconds / 60;
        int secs = totalSeconds % 60;
        return String.format("%02d:%02d", mins, secs);
    }

    private void updateScore() {
        int timeDeduction = elapsedSeconds / timePenalty;
        int displayScore = score - timeDeduction;
        if (displayScore < 0) displayScore = 0;
        if (scoreLabel != null) scoreLabel.setText("Score: " + displayScore);
    }

    private void checkAutoWin() {
        if (correctCount >= totalBlanks) {
            if (timer != null) timer.stop();
            int finalScore = 0;
            if (scoreLabel != null) {
                try { finalScore = Integer.parseInt(scoreLabel.getText().replace("Score: ", "")); }
                catch (NumberFormatException ignored) {}
            }
            try {
                Main.showWinScreen(finalScore, moves, elapsedSeconds);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to show WinScreen", e);
            }
        }
    }
}
