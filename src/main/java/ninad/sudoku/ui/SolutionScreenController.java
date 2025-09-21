package ninad.sudoku.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import ninad.sudoku.Main;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SolutionScreenController {

    private static final Logger LOGGER = Logger.getLogger(SolutionScreenController.class.getName());

    @FXML
    private GridPane solutionGridPane;

    private int[][] puzzle;
    private int[][] solution;

    /** Called from Main to pass puzzle and solution */
    public void setGrids(int[][] puzzle, int[][] solution) {
        this.puzzle = puzzle;
        this.solution = solution;

        drawAllWhiteGrid();
        animateSolution();
    }

    /** Draw all boxes white from the start */
    private void drawAllWhiteGrid() {
        solutionGridPane.getChildren().clear();

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int value = puzzle[row][col];
                Label cell = new Label(value == 0 ? "" : String.valueOf(value));
                cell.setPrefSize(50, 50);
                cell.setStyle("-fx-border-color: black; -fx-font-size: 18px; -fx-alignment: center;"
                        + "-fx-background-color: white;" // all boxes white
                        + (value != 0 ? "-fx-text-fill: black;" : "")); // given numbers black
                solutionGridPane.add(cell, col, row);
            }
        }
    }

    /** Animate filling solution numbers in green */
    private void animateSolution() {
        Timeline timeline = new Timeline();
        int delay = 0;

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (puzzle[row][col] == 0) { // only animate blanks
                    final int r = row, c = col;
                    timeline.getKeyFrames().add(new KeyFrame(Duration.millis(delay), e -> {
                        Label cell = (Label) getNodeByRowColumnIndex(r, c);
                        if (cell != null) {
                            cell.setText(String.valueOf(solution[r][c]));
                            cell.setStyle("-fx-border-color: black; -fx-font-size: 18px; -fx-alignment: center;"
                                    + "-fx-background-color: #a5d6a7; -fx-text-fill: black;"); // solution green
                        }
                    }));
                    delay += 80; // animation speed
                }
            }
        }

        timeline.play();
    }

    /** Utility to get cell at (row, col) */
    private javafx.scene.Node getNodeByRowColumnIndex(int row, int col) {
        for (javafx.scene.Node node : solutionGridPane.getChildren()) {
            Integer r = GridPane.getRowIndex(node);
            Integer c = GridPane.getColumnIndex(node);
            if (r != null && c != null && r == row && c == col) {
                return node;
            }
        }
        return null;
    }

    /** Called from FXML button to go back to the Main Menu */
    @FXML
    private void goToMainMenu() {
        try {
            LOGGER.info("Main Menu button clicked from SolutionScreen.");
            Main.showMainMenu();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to go to Main Menu from SolutionScreen", ex);
        }
    }
}
