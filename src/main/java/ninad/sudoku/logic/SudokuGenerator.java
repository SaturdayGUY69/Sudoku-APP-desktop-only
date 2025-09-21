package ninad.sudoku.logic;

import java.util.*;

public class SudokuGenerator {

    private final int[][] solutionGrid = new int[9][9];
    private final int[][] puzzleGrid = new int[9][9];
    private final Random random = new Random();

    public SudokuGenerator(String difficulty) {
        generateFullGrid();
        removeCells(difficulty);
    }

    /** Generate a full valid Sudoku grid using backtracking */
    private boolean generateFullGrid() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (solutionGrid[row][col] == 0) {
                    List<Integer> numbers = getShuffledNumbers();
                    for (int num : numbers) {
                        if (isSafe(solutionGrid, row, col, num)) {
                            solutionGrid[row][col] = num;
                            if (generateFullGrid()) return true;
                            solutionGrid[row][col] = 0; // backtrack
                        }
                    }
                    return false; // no valid number found
                }
            }
        }
        return true; // grid filled
    }

    /** Shuffle numbers 1-9 for random placement */
    private List<Integer> getShuffledNumbers() {
        List<Integer> numbers = new ArrayList<>(9);
        for (int i = 1; i <= 9; i++) numbers.add(i);
        Collections.shuffle(numbers, random);
        return numbers;
    }

    /** Check if the number can be placed at row,col */
    private boolean isSafe(int[][] grid, int row, int col, int num) {
        for (int i = 0; i < 9; i++) {
            if (grid[row][i] == num || grid[i][col] == num) return false;
        }
        int boxRowStart = row - row % 3;
        int boxColStart = col - col % 3;
        for (int r = boxRowStart; r < boxRowStart + 3; r++) {
            for (int c = boxColStart; c < boxColStart + 3; c++) {
                if (grid[r][c] == num) return false;
            }
        }
        return true;
    }

    /** Remove cells based on difficulty */
    private void removeCells(String difficulty) {
        // Copy solution to puzzle grid
        for (int i = 0; i < 9; i++) {
            System.arraycopy(solutionGrid[i], 0, puzzleGrid[i], 0, 9);
        }

        int[] removalRange = switch (difficulty.toLowerCase()) {
            case "easy" -> new int[]{33, 34, 35, 36, 37};
            case "medium" -> new int[]{40, 41, 42, 43, 44, 45};
            case "hard" -> new int[]{50, 51, 52, 53, 54, 55};
            default -> new int[]{33, 34, 35, 36, 37, 38};
        };

        int totalToRemove = removalRange[random.nextInt(removalRange.length)];
        int removed = 0;

        // ✅ Improvement: use a Set to avoid removing the same cell twice
        Set<String> removedCells = new HashSet<>();

        while (removed < totalToRemove) {
            int row = random.nextInt(9);
            int col = random.nextInt(9);
            String key = row + "-" + col;

            if (puzzleGrid[row][col] != 0 && !removedCells.contains(key)) {
                puzzleGrid[row][col] = 0;
                removedCells.add(key);
                removed++;
            }
        }
    }

    /** Check if a user move is correct */
    public boolean isCorrectMove(int row, int col, int value) {
        return solutionGrid[row][col] == value;
    }

    /** Get the puzzle grid */
    public int[][] getPuzzleGrid() {
        return puzzleGrid;
    }

    /** Get the solution grid */
    public int[][] getSolutionGrid() {
        return solutionGrid; // ✅ Fixed (was empty before)
    }
}
