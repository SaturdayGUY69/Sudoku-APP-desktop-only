package ninad.sudoku;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ninad.sudoku.ui.GiveUpScreenController;
import ninad.sudoku.ui.SolutionScreenController;
import ninad.sudoku.ui.WinScreenController;

import java.io.IOException;

public class Main extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        showMainMenu();
        stage.setTitle("Sudoku Master");
        stage.show();
    }

    /** Show the main menu screen */
    public static void showMainMenu() throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/ninad/sudoku/ui/MainScreen.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
    }

    /** Show game screen */
    public static void showGameScreen() throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/ninad/sudoku/ui/GameScreen.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
    }

    /** Show win screen with final stats (score, moves, time) */
    public static void showWinScreen(int finalScore, int totalMoves, int elapsedSeconds) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/ninad/sudoku/ui/WinScreen.fxml"));
        Scene scene = new Scene(loader.load());

        WinScreenController controller = loader.getController();
        controller.setStats(finalScore, totalMoves, elapsedSeconds);

        primaryStage.setScene(scene);
    }

    /** Show solution screen */
    public static void showSolutionScreen(int[][] puzzle, int[][] solution) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/ninad/sudoku/ui/SolutionScreen.fxml"));
        Scene scene = new Scene(loader.load());

        SolutionScreenController controller = loader.getController();
        controller.setGrids(puzzle, solution);

        primaryStage.setScene(scene);
    }

    /** Show give up screen */
    public static void showGiveUpScreen(int[][] puzzle, int[][] solution) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/ninad/sudoku/ui/GiveUpScreen.fxml"));
        Scene scene = new Scene(loader.load());

        GiveUpScreenController controller = loader.getController();
        controller.setPuzzleAndSolution(puzzle, solution);

        primaryStage.setScene(scene);
    }

    /** Get primary stage */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch();
    }
}
