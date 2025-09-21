package ninad.sudoku.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import ninad.sudoku.Main;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GiveUpScreenController {

    private static final Logger LOGGER = Logger.getLogger(GiveUpScreenController.class.getName());

    @FXML private Button showSolutionButton;
    @FXML private Button mainMenuButton;

    private int[][] puzzle;
    private int[][] solution;

    @FXML
    public void initialize() {
        showSolutionButton.setOnAction(e -> {
            try {
                if (puzzle != null && solution != null) {
                    LOGGER.info("Navigating to Solution Screen.");
                    Main.showSolutionScreen(puzzle, solution);
                } else {
                    LOGGER.warning("No puzzle/solution available. Returning to Main Menu.");
                    Main.showMainMenu();
                }
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Failed to navigate to Solution Screen", ex);
            }
        });

        mainMenuButton.setOnAction(e -> {
            try {
                LOGGER.info("Returning to Main Menu.");
                Main.showMainMenu();
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Failed to navigate to Main Menu", ex);
            }
        });
    }

    /** Store puzzle + solution (called from Main) */
    public void setPuzzleAndSolution(int[][] puzzle, int[][] solution) {
        this.puzzle = puzzle;
        this.solution = solution;
        LOGGER.info("Puzzle and solution set in GiveUpScreenController.");
    }
}
