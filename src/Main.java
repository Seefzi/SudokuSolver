import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;
import static java.lang.System.exit;

public class Main
{
    // Overarching Sudoku type
    // I fixed the issue with static and nonstatic references
    private class Sudoku
    {
        private int[][] board;     /** This is the puzzle as input by the user. **/
        private int[][] solution;  /** This is the puzzle solution. **/

    // Class constructor, setters and getters below.
    public Sudoku()
    {
        this.board = new int[9][9];
        this.solution = new int[9][9];
    }
        public void setBoard(int[][] input)
        {
            this.board = input;
        }
        public void setSolution(int[][] solved)
        {
            this.solution = solved;
        }
        public void setSolution(int row, int col, int num){
            this.solution[row][col] = num;
        }
        public int[][] getBoard() { return this.board; }
        public int[] getBoardRow(int rowIndex)
        {
            return this.board[rowIndex];
        }
        public int[] getBoardColumn(int colIndex)
        {
            int[] currentCol = new int[9];
            for (int i = 0; i < 9; i++)
                currentCol[i] = this.board[i][colIndex];
            return currentCol;
        }
        public int[] getBoardBlock(int blockIndex)
        {
            int[] block = new int[9];
            int offsetX = 3 * (blockIndex % 3);
            int offsetY = 3 * (blockIndex / 3);
            int i = 0, x, y;
            for (x = 0; x < 3; x++)
            {
                for (y = 0; y < 3; y++)
                {
                    block[i] = this.getBoardRow(x + offsetX)[y + offsetY];
                    i++;
                }
            }

            return block;
        }
        public int[][] getSolution()
        {
            return this.solution;
        }
        public int[] getSolutionRow(int rowIndex)
        {
            return this.solution[rowIndex];
        }
        public int[] getSolutionColumn(int colIndex)
        {
            int[] currentCol = new int[9];
            for (int i = 0; i < 9; i++)
                currentCol[i] = this.solution[i][colIndex];
            return currentCol;
        }
        public int[] getSolutionBlock(int blockIndex)
        {
            int[] block = new int[9];
            int offsetX = 3 * (blockIndex % 3);
            int offsetY = 3 * (blockIndex / 3);
            int i = 0, x, y;
            for (x = 0; x < 3; x++)
            {
                for (y = 0; y < 3; y++)
                {
                    block[i] = this.getSolutionRow(x + offsetX)[y + offsetY];
                    i++;
                }
            }

            return block;
        }

    }
    private static int[][] InitializeSudokuFromUserInput()
    {
        // stores input from user to be turned into a sudoku line
        int[][] newBoard = new int[9][9]; // [row][column]
        boolean validity;
        String newLine;
        Scanner input = new Scanner(System.in);
        System.out.println("Welcome to Sudoku Solver. Please start by entering the first line of the sudoku you would like me to solve. Fill in any blank spaces with 0.");

        // iteratorX == row. iteratorY == column.
        int iteratorX, iteratorY;

        // iterates by row
        for (iteratorX = 0; iteratorX < 9; iteratorX++)
        {
            do {
                // I thought it was Java screwing me, turns out it was secretly working fine! ugh. Added this println to demonstrate that it's working.
                System.out.println("Line " + (iteratorX + 1) + ":");
                newLine = input.nextLine();
                validity = true;

                // prevents out of bounds exceptions by checking length validity.
                if (newLine.length() != 9)
                {
                    System.out.println("Error, input was below expected length. Remember, Sudokus are 9x9. Please try again:");
                    validity = false;
                }

                // iterates column within upper row. since I need the extra dimension, foreach won't work here.
                for (iteratorY = 0; iteratorY < 9 && validity; iteratorY++)
                {
                    newBoard[iteratorX][iteratorY] = Character.getNumericValue(newLine.toCharArray()[iteratorY]); // can return -1 or -2 if invalid input is given
                }
            } while (!validity); // if validity is false, loop will repeat.
        }

        return newBoard;
    }

    private static int[][] InitializeSudokuFromFile(File f)
    {
        int[][] newBoard = new int[9][9]; // [row][column]
        boolean validity;
        String newLine;
        Scanner input;
        try {
            input = new Scanner(f);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Test file found. Reading...");

        // iteratorX == row. iteratorY == column.
        int iteratorX, iteratorY;

        // iterates by row
        for (iteratorX = 0; iteratorX < 9; iteratorX++)
        {
            newLine = input.nextLine();
            validity = true;
            System.out.println("Line " + (iteratorX + 1) + ": " + newLine);

            // prevents out of bounds exceptions by checking length validity.
            if (newLine.length() != 9)
            {
                System.out.println("Error, input was different from expected length.");
                validity = false;
            }

            if (!validity)
            {
                System.out.println("Check your test file, there is a problem.");
                exit(2);
            }

            // iterates column within upper row. since I need the extra dimension, foreach won't work here.
            for (iteratorY = 0; iteratorY < 9; iteratorY++)
            {
                newBoard[iteratorX][iteratorY] = Character.getNumericValue(newLine.toCharArray()[iteratorY]); // can return -1 or -2 if invalid input is given
            }


        }

        return newBoard;
    }

    private static boolean Validator(Sudoku board) {
        /**------------------------------------------------------------------------------------------------------------
         *   Sudoku rules are as follows:                                                                              *
         *     * A row cannot contain the same value more than once and must contain every value from 1-9              *
         *     * A column cannot contain the same value more than once and must contain every value from 1-9           *
         *     * Each 3x3 subgrid cannot contain the same value more than once and must contain every value from 1-9   *
         *     * A Sudoku must contain enough information for the Sudoku to be solvable                                *
         ------------------------------------------------------------------------------------------------------------**/

        // Use valueBank to store previous values and check against valueBank to determine validity.
        // valueBank's index of 0 can equal 1 and still be valid!

        System.out.println("Checking Sudoku's validity...");

        /** Row Validity Checks **/

        int[] valueBank = new int[10];
        boolean validity = true;
        int i;
        for (i = 0; i < 9; i++)
        {
            for (int cell : board.getBoardRow(i))
            {
                if (cell != 0 && valueBank[cell] == 1)
                {
                    System.out.println("ROW CHECK FAILED: Duplicate detected in row " + i + " : " + cell);
                    validity = false;
                }
                else
                    valueBank[cell] = 1;
            }
            Arrays.fill(valueBank, 0);
        }

        /** Column Validity Checks **/

        for (i = 0; i < 9; i++)
        {
            for (int cell : board.getBoardColumn(i))
            {
                if (valueBank[cell] == 1 && cell != 0)
                {
                    System.out.println("COLUMN CHECK FAILED: Duplicate detected in column " + i + " : " + cell);
                    validity = false;
                }
                else
                    valueBank[cell] = 1;
            }
            Arrays.fill(valueBank, 0);
        }

        /** Block Validity Checks **/

        int[] block; // this stores the 3x3 block to be checked

        for (int blockIndex = 0; blockIndex < 9; blockIndex++) {
            // simplified and fixed
            // offsets are used instead of weird math as indices which fixed the problem
            block = board.getBoardBlock(blockIndex);
            for (int cell : block) {
                if (valueBank[cell] == 1 && cell != 0)
                {
                    validity = false;
                    System.out.println("BLOCK CHECK FAILED: Duplicate detected in block " + blockIndex + ": " + cell);
                }
                else
                    valueBank[cell] = 1;
            }
            Arrays.fill(valueBank, 0);

        }
        return validity;
    }

    private static boolean SolutionFinder(Sudoku sudoku)
    {
        System.out.println("Beginning solving algorithm...");

        // Check if initial board is valid
        if(!Validator(sudoku)) {
            return false;
        }

        // Traverse board and find empty cells
        int[] emptyCell = findEmptyCell(sudoku); // finds a single cell per call
        if (emptyCell == null) {    // No empty cells
            return true;            // Puzzle solved
        }

        // Fill the empty cell 1-9
        int row = emptyCell[0];
        int col = emptyCell[1];

        for (int number = 1; number <= 9; number++) {
            // Validate each possibility
            if (isPlacementValid(sudoku, number, row, col)) {
                // If number is valid, assign it to cell
                sudoku.setSolution(row, col, number);

                // Repeat process using recursion
                if (SolutionFinder(sudoku)) {
                    return true; // Puzzle solved
                }
                else {
                    // Backtrack if necessary and clear out placement
                    sudoku.getBoard()[row][col] = 0;
                }
            }
        }

        // Backtrack
        return false;
    }

    // Find an empty cell in the grid
    private static int[] findEmptyCell(Sudoku sudoku) {
        int[][] board = sudoku.getSolution();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if(board[i][j] == 0) {          // If cell is empty
                    return new int[]{i, j};     // Return row and column of cell
                }
            }
        }
        return null;
    }

    // Check if placement is valid
    private static boolean isPlacementValid(Sudoku sudoku, int number, int row, int col) {
        return !isNumberInRow(sudoku, number, row) &&
                !isNumberInColumn(sudoku, number, col) &&
                !isNumberInBlock(sudoku, number, row, col);
    }

    // Check if number is in row
    private static boolean isNumberInRow(Sudoku sudoku, int number, int row) {
        int[] boardRow = sudoku.getSolutionRow(row);
        for (int cell: boardRow) {
            if (cell == number) {
                return true;
            }
        }
        return false;
    }

    // Check if number is in column
    private static boolean isNumberInColumn(Sudoku sudoku, int number, int col) {
        int[] boardCol = sudoku.getSolutionColumn(col);
        for (int cell: boardCol) {
            if (cell == number) {
                return true;
            }
        }
        return false;
    }

    // Check if number is in block
    private static boolean isNumberInBlock(Sudoku sudoku, int number, int row, int col) {
        int[] boardBlock = sudoku.getSolutionBlock((row / 3) * 3 + (col / 3));
        for (int cell: boardBlock) {
            if (cell == number) {
                return true;
            }
        }
        return false;
    }

    private void sequence()
    {
        Sudoku sudoku = new Sudoku();
        File f = new File("test_sudoku.txt");
        if(f.exists() && !f.isDirectory())
            sudoku.setBoard(InitializeSudokuFromFile(f));
        else
            sudoku.setBoard(InitializeSudokuFromUserInput());

        if (!Validator(sudoku))
        {
            System.out.println("The Sudoku provided wasn't valid. Exiting...");
            exit(1);
        }
        else
            System.out.println("The Sudoku is syntactically sound.");

        sudoku.setSolution(sudoku.getBoard());

        if (SolutionFinder(sudoku)) {
            System.out.println("The Sudoku has been solved successfully!");
        }
        else {
            System.out.println("The Sudoku is unsolvable. Exiting...");
            exit(1);
        }

        printSudoku(sudoku);
    }

    private static void printSudoku(Sudoku sudoku) {
        for (int row = 0; row < 9; row++) {
            if (row % 3 == 0 && row != 0) {
                System.out.print("-----------");
            }
            for (int col = 0; col < 9; col++) {
                if (col % 3 == 0 && col != 0) {
                    System.out.print("|");
                }
                System.out.print(sudoku.getBoard()[row][col]);
            }
            System.out.println();
        }
    }

    public static void main(String[] args)
    {
        Main program = new Main();
        program.sequence();
    }
}