import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static java.lang.System.exit;

public class Main
{
    // Overarching Sudoku type
    // I fixed the issue with static and nonstatic references
    private class Sudoku
    {
        private int[][] board;     /** This is the puzzle as input by the user. **/
        private int[][] solution;  /** This is the puzzle solution. **/
        public boolean[][] visited;

    // Class constructor, setters and getters below.
    public Sudoku(int[][] input)
    {
        this.board = input;
        this.solution = new int[9][9];
        this.visited = new boolean[9][9];
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
            int offsetX = 3 * (blockIndex / 3);
            int offsetY = 3 * (blockIndex % 3);
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
            int offsetX = 3 * (blockIndex / 3);
            int offsetY = 3 * (blockIndex % 3);
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
            newLine = input.nextLine();
            validity = true;
            System.out.println("Line " + (iteratorX + 1) + ": " + newLine);

            // prevents out of bounds exceptions by checking length validity.
            if (newLine.length() != 9)
            {
                System.out.println("Error, input was different from expected length. Try again.");
                validity = false;
            }

            if (!validity)
            {
                iteratorX--;
            }

            // iterates column within upper row. since I need the extra dimension, foreach won't work here.
            for (iteratorY = 0; iteratorY < 9 && validity; iteratorY++)
            {
                newBoard[iteratorX][iteratorY] = Character.getNumericValue(newLine.toCharArray()[iteratorY]); // can return -1 or -2 if invalid input is given
            }


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
        System.out.println("Reading into array...");

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
                System.out.println("Check your file, there is a problem.");
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
        // System.out.println("Generative Recursion");

        int iteration = 0;
        int sinceLastPlacement = 0;
        while (true) {
            if (sinceLastPlacement > 162) {
                printSudoku(sudoku);
                System.out.println("Timeout: No placement in 162 attempts");
                return false;
            }

            iteration++;

            // Traverse board and find empty cells
            int[] emptyCell = findEmptyCell(sudoku); // finds a single cell per call
            if (emptyCell == null) {    // No empty cells left
                break;// Puzzle solved
            }

            // Fill the empty cell with the only possible number
            int row = emptyCell[0];
            int col = emptyCell[1];
            int[] possibleNumbers = getPossibleNumbers(sudoku, row, col);
            if (possibleNumbers.length == 0)
            {
                System.out.println("Error: No possible values for " + row + ", " + col + " on iteration " + iteration);
                return false;
            }
            int[] uniqueValuesInBlock = getUniqueValuesInBlock(sudoku, row, col);
            int[] uniqueValuesInRow = getUniqueValuesInRow(sudoku, row, col);
            int[] uniqueValuesInCol = getUniqueValuesInCol(sudoku, row, col);


            if (possibleNumbers.length == 1) {// If there is one possibility for the cell // for (int number : possibleNumbers) {
                // Validate each possibility
                if (isPlacementValid(sudoku, possibleNumbers[0], row, col)) {
                    // If number is valid, assign it to cell
                    sudoku.setSolution(row, col, possibleNumbers[0]);


                }
            } else { // these check the case that there is no other cell for a given value
                for (int j : uniqueValuesInBlock) {
                    if (isPlacementValid(sudoku, j, row, col) && Arrays.binarySearch(possibleNumbers, j) > 0) {
                        sudoku.setSolution(row, col, j);
                    }
                }
                for (int j : uniqueValuesInRow) {
                    if (isPlacementValid(sudoku, j, row, col) && Arrays.binarySearch(possibleNumbers, j) > 0) {
                        sudoku.setSolution(row, col, j);
                    }
                }
                for (int j : uniqueValuesInCol) {
                    if (isPlacementValid(sudoku, j, row, col) && Arrays.binarySearch(possibleNumbers, j) > 0) {
                        sudoku.setSolution(row, col, j);
                    }
                }

            }

            if (sudoku.getSolution()[row][col] == 0)
                sinceLastPlacement++;
            else
                sinceLastPlacement = 0;
        }
        // DO NOT repeat process using recursion
        printSudoku(sudoku);
        System.out.println("SOLUTION ALERT!");
        return true; // Puzzle solved

        // Backtrack

    }

    private static int[] getPossibleNumbers(Sudoku sudoku, int row, int col) {
        boolean[] usedNumbers = new boolean[10]; // Array to keep track of used numbers
        Arrays.fill(usedNumbers, false);

        // Check row
        for (int num : sudoku.getSolutionRow(row)) {
            usedNumbers[num] = true;
        }

        // Check column
        for (int num : sudoku.getSolutionColumn(col)) {
            usedNumbers[num] = true;
        }

        // Check block
        int blockIndex = (row / 3) * 3 + (col / 3);
        for (int num : sudoku.getSolutionBlock(blockIndex)) {
            usedNumbers[num] = true;
        }

        // Find unused numbers
        List<Integer> possibleNumbers = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            if (!usedNumbers[i]) {
                possibleNumbers.add(i);
            }
        }

        // Convert list to array
        int[] possibleNumbersArray = new int[possibleNumbers.size()];
        for (int i = 0; i < possibleNumbers.size(); i++) {
            possibleNumbersArray[i] = possibleNumbers.get(i);
        }

        return possibleNumbersArray;
    }
    private static int[] getUniqueValuesInBlock(Sudoku sudoku, int row, int col) {
        int blockStartRow = (row / 3) * 3;
        int blockStartCol = (col / 3) * 3;

        int[] count = new int[10]; // Index 0 is not used

        // Iterate over the 3x3 block and count instances of each number
        for (int i = blockStartRow; i < blockStartRow + 3; i++) {
            for (int j = blockStartCol; j < blockStartCol + 3; j++) {
                int[] possibleNumbers = getPossibleNumbers(sudoku, i, j);
                for (int num : possibleNumbers) {
                    count[num]++;
                }
            }
        }

        // gets numbers that only appear once in the block
        int[] uniqueValues = new int[9];
        int index = 0;
        for (int num = 1; num <= 9; num++) {
            if (count[num] == 1) {
                uniqueValues[index++] = num;
            }
        }
        // returns without extra 0s
        return Arrays.copyOf(uniqueValues, index);
    }

    private static int[] getUniqueValuesInRow(Sudoku sudoku, int row, int col) {

        int[] count = new int[10]; // Index 0 is not used

        // Iterate over the row
        for (int i = 0; i < 9; i++) {
                int[] possibleNumbers = getPossibleNumbers(sudoku, row, i);
                for (int num : possibleNumbers) {
                    count[num]++;
                }
        }

        // gets numbers that only appear once in the block
        int[] uniqueValues = new int[9];
        int index = 0;
        for (int num = 1; num <= 9; num++) {
            if (count[num] == 1) {
                uniqueValues[index++] = num;
            }
        }
        // returns without extra 0s
        return Arrays.copyOf(uniqueValues, index);
    }

    private static int[] getUniqueValuesInCol(Sudoku sudoku, int row, int col) {

        int[] count = new int[10]; // Index 0 is not used

        // Iterate over the row
        for (int i = 0; i < 9; i++) {
            int[] possibleNumbers = getPossibleNumbers(sudoku, col, i);
            for (int num : possibleNumbers) {
                count[num]++;
            }
        }

        // gets numbers that only appear once in the block
        int[] uniqueValues = new int[9];
        int index = 0;
        for (int num = 1; num <= 9; num++) {
            if (count[num] == 1) {
                uniqueValues[index++] = num;
            }
        }
        // returns without extra 0s
        return Arrays.copyOf(uniqueValues, index);
    }

    // Find an empty cell in the grid
    private static int[] findEmptyCell(Sudoku sudoku) {
        int[][] board = sudoku.getSolution();
        boolean reset = true;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == 0 && !sudoku.visited[i][j]) {
                    reset = false;
                    break;
                }
            }
            if (!reset) {
                break;
            }
        }
        if (reset) {
            for (boolean[] row : sudoku.visited)
                Arrays.fill(row, false);
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if(board[i][j] == 0 && !sudoku.visited[i][j]) {// If cell is empty and has yet to be checked
                    sudoku.visited[i][j] = true;
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

    private void sequence() {
        File f = new File("test_sudoku.txt");
        Sudoku sudoku = null;
        Scanner s = new Scanner(System.in);

        System.out.println("Welcome to Sudoku Solver. How would you like to input your puzzle?");
        System.out.println("1: Manual input");
        if (f.exists() && !f.isDirectory())
            System.out.println("2: File input (test_sudoku.txt)");

        String input = s.nextLine();

        switch (input.charAt(0)){
            case '1':
                sudoku = new Sudoku(InitializeSudokuFromUserInput());
                break;
            case '2':
                // Check if file exits
                if(f.exists() && !f.isDirectory()) {
                    sudoku = new Sudoku(InitializeSudokuFromFile(f));
                } else {
                    // Otherwise take user input
                    System.out.println("No file found. Defaulting to manual input...");
                    sudoku = new Sudoku(InitializeSudokuFromUserInput());
                }
                break;
            case 'q':
            case 'Q':
                System.out.println("Exiting...");
                exit(0);
        }

        if (sudoku == null)
        {
            System.out.println("Sudoku object is null. Exiting...");
            exit(1);
        }

        // Check if board is valid
        if (!Validator(sudoku))
        {
            System.out.println("The Sudoku provided wasn't valid. Exiting...");
            exit(1);
        }
        else {
            System.out.println("The Sudoku is syntactically sound.");
            printSudoku(sudoku);
        }

        for (int i = 0; i < sudoku.getBoard().length; i++) {
            for (int j = 0; j < sudoku.getBoard()[i].length; j++) {
                sudoku.setSolution(i, j, sudoku.getBoard()[i][j]);
            }
        }

        // Try to solve the puzzle
        System.out.println("Beginning solving algorithm...");
        if (SolutionFinder(sudoku)) {
            System.out.println("The Sudoku has been solved successfully!");
        }
        else {
            System.out.println("The Sudoku is unsolvable. Womp womp.");
        }

        System.out.println("Would you like to input another puzzle? Y/N");
        input = s.nextLine();
        switch (input.charAt(0))
        {
            case 'y':
            case 'Y':
               sequence();
               break;
            case 'n':
            case 'N':
                System.out.println("Exiting...");
                exit(0);
        }
    }

    private static void printSudoku(Sudoku sudoku) {
        int[][] solution = sudoku.getSolution();
        for (int row = 0; row < 9; row++) {
            if (row % 3 == 0 && row != 0) {
                System.out.println("----------------------------");
            }
            for (int col = 0; col < 9; col++) {
                if (col % 3 == 0 && col != 0) {
                    System.out.print("|");
                }
                System.out.print(" " + solution[row][col] + " ");
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
