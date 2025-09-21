package ninad.sudoku.logic;

/**
 * Strict validator for Sudoku solving.
 * Only checks for conflicts in rows, cols, and boxes.
 */
public class SudokuValidator {

    /** Check if the whole grid is valid (no duplicates). */
    public static boolean isValid(int[][] grid) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int num = grid[row][col];
                if (num != 0) {
                    grid[row][col] = 0;
                    if (!isSafe(grid, row, col, num)) {
                        grid[row][col] = num;
                        return false;
                    }
                    grid[row][col] = num;
                }
            }
        }
        return true;
    }

    /** Check if placing num at (row, col) is valid. */
    public static boolean isSafe(int[][] grid, int row, int col, int num) {
        for (int i = 0; i < 9; i++) {
            if (grid[row][i] == num || grid[i][col] == num) return false;
        }
        int startRow = (row / 3) * 3;
        int startCol = (col / 3) * 3;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (grid[startRow + r][startCol + c] == num) return false;
            }
        }
        return true;
    }

    /** Validate a single cell’s placement. */
    public static boolean validateSingle(int[][] grid, int row, int col) {
        int val = grid[row][col];
        if (val == 0) return true;

        // Row
        for (int c = 0; c < 9; c++) {
            if (c != col && grid[row][c] == val) return false;
        }
        // Column
        for (int r = 0; r < 9; r++) {
            if (r != row && grid[r][col] == val) return false;
        }
        // Box
        int startRow = (row / 3) * 3;
        int startCol = (col / 3) * 3;
        for (int r = startRow; r < startRow + 3; r++) {
            for (int c = startCol; c < startCol + 3; c++) {
                if ((r != row || c != col) && grid[r][c] == val) return false;
            }
        }
        return true;
    }
}
