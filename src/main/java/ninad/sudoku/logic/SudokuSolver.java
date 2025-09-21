package ninad.sudoku.logic;

public class SudokuSolver {

    private static final int SIZE = 9;

    public static boolean solve(int[][] grid) {
        // find first empty cell
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (grid[row][col] == 0) {
                    // try all numbers 1–9
                    for (int num = 1; num <= SIZE; num++) {
                        if (isSafe(grid, row, col, num)) {
                            grid[row][col] = num;
                            if (solve(grid)) {
                                return true; // solved further down
                            }
                            grid[row][col] = 0; // backtrack
                        }
                    }
                    // if no number works, trigger backtrack
                    return false;
                }
            }
        }
        // no empty cells left → solved
        return true;
    }

    public static int[][] solveCopy(int[][] grid) {
        int[][] copy = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(grid[i], 0, copy[i], 0, SIZE);
        }
        return solve(copy) ? copy : null;
    }

    private static boolean isSafe(int[][] grid, int row, int col, int num) {
        // row + col check
        for (int i = 0; i < SIZE; i++) {
            if (grid[row][i] == num || grid[i][col] == num) return false;
        }
        // 3x3 subgrid check
        int startRow = (row / 3) * 3;
        int startCol = (col / 3) * 3;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (grid[startRow + r][startCol + c] == num) return false;
            }
        }
        return true;
    }
}
