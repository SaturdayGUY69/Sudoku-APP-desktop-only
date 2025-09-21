package ninad.sudoku.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MainScreenController {

    private static final Logger LOGGER = Logger.getLogger(MainScreenController.class.getName());

    @FXML
    private Button playButton;

    @FXML
    private Button solveButton;

    @FXML
    private Button exitButton;

    @FXML
    public void initialize() {
        if (playButton != null)
            playButton.setOnAction(e -> openGameScreen());
        if (solveButton != null)
            solveButton.setOnAction(e -> openSolveScreen());
        if (exitButton != null)
            exitButton.setOnAction(e -> System.exit(0));
    }

    private void openGameScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ninad/sudoku/ui/GameScreen.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) playButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Sudoku Master - Play");
            stage.show();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to load GameScreen.fxml", ex);
        }
    }

    private void openSolveScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ninad/sudoku/ui/SolveScreen.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) solveButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Sudoku Master - Solve");
            stage.show();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to load SolveScreen.fxml", ex);
        }
    }
}
