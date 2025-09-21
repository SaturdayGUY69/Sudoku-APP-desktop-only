package ninad.sudoku.logic;

/**
 * Extra checks for puzzle generation.
 * Not required for solving!
 */
public class PuzzleValidator {

    public static boolean hasMinimumClues(int[][] grid) {
        int count = 0;
        for (int[] row : grid)
            for (int cell : row)
                if (cell != 0) count++;
        return count >= 17;
    }

    public static boolean hasBlockDistribution(int[][] grid) {
        for (int boxRow = 0; boxRow < 3; boxRow++) {
            for (int boxCol = 0; boxCol < 3; boxCol++) {
                int count = 0;
                for (int r = 0; r < 3; r++)
                    for (int c = 0; c < 3; c++)
                        if (grid[boxRow*3 + r][boxCol*3 + c] != 0) count++;
                if (count < 1) return false;
            }
        }
        return true;
    }

    public static boolean hasRowDistribution(int[][] grid) {
        for (int row = 0; row < 9; row++) {
            boolean hasClue = false;
            for (int col = 0; col < 9; col++) {
                if (grid[row][col] != 0) { hasClue = true; break; }
            }
            if (!hasClue) return false;
        }
        return true;
    }

    public static boolean hasColDistribution(int[][] grid) {
        for (int col = 0; col < 9; col++) {
            boolean hasClue = false;
            for (int row = 0; row < 9; row++) {
                if (grid[row][col] != 0) { hasClue = true; break; }
            }
            if (!hasClue) return false;
        }
        return true;
    }

    /** Run all puzzle-quality checks. */
    public static String validatePuzzle(int[][] grid) {
        if (!hasMinimumClues(grid)) return "At least 17 clues are required!";
        if (!hasBlockDistribution(grid)) return "Each 3x3 box must contain at least 1 clue!";
        if (!hasRowDistribution(grid)) return "Each row must contain at least 1 clue!";
        if (!hasColDistribution(grid)) return "Each column must contain at least 1 clue!";
        return null; // valid
    }
}
