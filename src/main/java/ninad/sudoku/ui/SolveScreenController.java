package ninad.sudoku.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import ninad.sudoku.Main;
import ninad.sudoku.logic.SudokuSolver;
import ninad.sudoku.logic.SudokuValidator;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SolveScreenController {

    private static final Logger LOGGER = Logger.getLogger(SolveScreenController.class.getName());

    @FXML private GridPane sudokuGrid;
    @FXML private Label statusLabel;
    @FXML private Button autoSolveButton, backButton;

    private static final int SIZE = 9;
    private static final int CELL_SIZE = 50;
    private static final String FONT_FAMILY = "System";
    private static final int FONT_SIZE = 18;

    private int[][] solutionGrid;
    private Timeline fillTimeline;

    @FXML
    public void initialize() {
        buildSudokuGrid();
        setupButtons();
    }

    private void buildSudokuGrid() {
        sudokuGrid.getChildren().clear();

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                TextField cell = new TextField();
                cell.setPrefSize(CELL_SIZE, CELL_SIZE);
                cell.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, FONT_SIZE));
                cell.setAlignment(Pos.CENTER);

                String baseStyle = getCellBorderStyle(row, col)
                        + String.format(" -fx-font-weight: bold; -fx-font-size: %dpx; -fx-text-fill: #222222; -fx-alignment: center;", FONT_SIZE);
                cell.setStyle(baseStyle);

                final int r = row, c = col;
                UnaryOperator<TextFormatter.Change> filter = change -> {
                    String newText = change.getControlNewText();
                    return newText.matches("[1-9]?") ? change : null;
                };
                cell.setTextFormatter(new TextFormatter<>(filter));

                cell.textProperty().addListener((obs, oldVal, newVal) -> handleCellInput(cell, r, c, baseStyle, newVal));
                cell.focusedProperty().addListener((obs, oldVal, newVal) -> handleCellFocusLost(cell, r, c, baseStyle));

                sudokuGrid.add(cell, col, row);
            }
        }
    }

    private void handleCellInput(TextField cell, int row, int col, String baseStyle, String newVal) {
        if (newVal == null || newVal.isEmpty()) {
            cell.setStyle(baseStyle);
            return;
        }

        int val;
        try { val = Integer.parseInt(newVal); }
        catch (NumberFormatException ex) {
            cell.setText("");
            cell.setStyle(baseStyle);
            return;
        }

        if (isValidMove(row, col, val)) {
            cell.setStyle(getCellBorderStyle(row, col)
                    + "-fx-background-color: #c8e6c9; -fx-font-weight: bold; -fx-font-size: "
                    + FONT_SIZE + "px; -fx-text-fill: #222222;");
        } else {
            flashRed(cell, baseStyle);
        }
    }

    private void handleCellFocusLost(TextField cell, int row, int col, String baseStyle) {
        String txt = cell.getText();
        if (txt == null || txt.isEmpty()) {
            cell.setStyle(baseStyle);
        } else {
            int val = Integer.parseInt(txt);
            if (!isValidMove(row, col, val)) {
                flashRed(cell, baseStyle);
            } else {
                cell.setStyle(getCellBorderStyle(row, col)
                        + "-fx-background-color: #c8e6c9; -fx-font-weight: bold; -fx-font-size: "
                        + FONT_SIZE + "px; -fx-text-fill: #222222;");
            }
        }
    }

    private void flashRed(TextField cell, String baseStyle) {
        cell.setStyle(getCellBorderStyle(GridPane.getRowIndex(cell), GridPane.getColumnIndex(cell))
                + "-fx-background-color: #ffcdd2; -fx-font-weight: bold; -fx-font-size: "
                + FONT_SIZE + "px; -fx-text-fill: #222222;");
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> cell.setText("")));
        timeline.setOnFinished(e -> cell.setStyle(baseStyle));
        timeline.play();
    }

    private boolean isValidMove(int row, int col, int val) {
        int[][] currentGrid = getCurrentGrid();
        currentGrid[row][col] = val;
        return SudokuValidator.validateSingle(currentGrid, row, col);
    }

    private int[][] getCurrentGrid() {
        int[][] grid = new int[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                TextField cell = (TextField) getNodeByRowColumnIndex(row, col, sudokuGrid);
                String text = (cell != null) ? cell.getText().trim() : "";
                grid[row][col] = text.isEmpty() ? 0 : Integer.parseInt(text);
            }
        }
        return grid;
    }

    private String getCellBorderStyle(int row, int col) {
        int top = 1, right = 1, bottom = 1, left = 1;
        if (row == 2 || row == 5) bottom = 2;
        if (col == 2 || col == 5) right = 2;
        return String.format("-fx-border-color: black; -fx-border-width: %d %d %d %d;", top, right, bottom, left);
    }

    private void setupButtons() {
        autoSolveButton.setOnAction(e -> startAutoSolve());
        backButton.setOnAction(e -> goBack());
    }

    private void startAutoSolve() {
        autoSolveButton.setDisable(true);
        backButton.setDisable(true);
        statusLabel.setText("Solving...");

        int[][] userGrid = getCurrentGrid();
        if (!SudokuValidator.isValid(userGrid)) {
            statusLabel.setText("Puzzle has conflicts!");
            autoSolveButton.setDisable(false);
            backButton.setDisable(false);
            return;
        }

        solutionGrid = SudokuSolver.solveCopy(userGrid);
        if (solutionGrid == null) {
            statusLabel.setText("No solution found!");
            autoSolveButton.setDisable(false);
            backButton.setDisable(false);
            return;
        }

        boolean[][] originallyFilled = new boolean[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                originallyFilled[r][c] = userGrid[r][c] != 0;
                TextField cell = (TextField) getNodeByRowColumnIndex(r, c, sudokuGrid);
                if (cell == null) continue;
                if (originallyFilled[r][c]) {
                    cell.setStyle(getCellBorderStyle(r, c)
                            + "-fx-background-color: #e0e0e0; -fx-font-weight: bold; -fx-font-size: "
                            + FONT_SIZE + "px; -fx-text-fill: #222222;");
                }
                cell.setDisable(true);
            }
        }

        List<Point> positions = new ArrayList<>();
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                positions.add(new Point(r, c));

        fillTimeline = new Timeline();
        for (int i = 0; i < positions.size(); i++)
            fillTimeline.getKeyFrames().add(createKeyFrame(i, positions, originallyFilled));

        fillTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(150 * (positions.size() + 1)), ev -> {
            statusLabel.setText("Solved!");
            for (int r = 0; r < SIZE; r++) {
                for (int c = 0; c < SIZE; c++) {
                    TextField cell = (TextField) getNodeByRowColumnIndex(r, c, sudokuGrid);
                    if (cell == null) continue;
                    if (originallyFilled[r][c]) {
                        cell.setStyle(getCellBorderStyle(r, c)
                                + "-fx-background-color: #e0e0e0; -fx-font-weight: bold; -fx-font-size: "
                                + FONT_SIZE + "px; -fx-text-fill: #222222;");
                    } else {
                        cell.setEditable(false);
                        cell.setDisable(false);
                    }
                }
            }
            backButton.setDisable(false);
        }));

        fillTimeline.play();
    }

    private KeyFrame createKeyFrame(int idx, List<Point> positions, boolean[][] originallyFilled) {
        return new KeyFrame(Duration.millis(150 * (idx + 1)), ev -> {
            Point p = positions.get(idx);
            int r = p.x, c = p.y;
            if (!originallyFilled[r][c]) {
                TextField cell = (TextField) getNodeByRowColumnIndex(r, c, sudokuGrid);
                if (cell != null) {
                    cell.setText(String.valueOf(solutionGrid[r][c]));
                    cell.setStyle(getCellBorderStyle(r, c)
                            + "-fx-background-color: #c8e6c9; -fx-font-weight: bold; -fx-font-size: "
                            + FONT_SIZE + "px; -fx-text-fill: #222222;");
                }
            }
        });
    }

    private javafx.scene.Node getNodeByRowColumnIndex(int row, int column, GridPane gridPane) {
        for (javafx.scene.Node node : gridPane.getChildren()) {
            Integer r = GridPane.getRowIndex(node);
            Integer c = GridPane.getColumnIndex(node);
            if (r != null && c != null && r == row && c == column) return node;
        }
        return null;
    }

    private void goBack() {
        if (fillTimeline != null) fillTimeline.stop();
        try {
            Main.showMainMenu();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to go back to main menu", ex);
        }
    }
}
