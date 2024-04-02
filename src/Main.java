import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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

        System.out.println("Checking Sudoku's syntactic validity...");

        /** Row Validity Checks **/

        int[] valueBank = new int[10];
        boolean validity = true;
        int i;
        int zero;
        for (i = 0; i < 9; i++)
        {
            for (int cell : board.getBoardRow(i))
            {
                if (cell != 0 && valueBank[cell] == 1)
                {
                    System.out.println("Duplicate detected in row " + i + " : " + cell);
                    validity = false;
                }
                else
                    valueBank[cell] = 1;
            }
            for (zero = 0; zero < 10; zero++) // the foreach style loop didn't work, so I replaced it with a regular one
                valueBank[zero] = 0;
        }

        System.out.println("Valid Rows?: " + validity);

        /** Column Validity Checks **/

        for (i = 0; i < 9; i++)
        {
            for (int cell : board.getBoardColumn(i))
            {
                if (valueBank[cell] == 1 && cell != 0)
                {
                    System.out.println("Duplicate detected in column " + i + " : " + cell);
                    validity = false;
                }
                else
                    valueBank[cell] = 1;
            }
            // zero out valueBank for future use
            for (zero = 0; zero < 10; zero++)
                valueBank[zero] = 0;
        }
        System.out.println("Valid Columns?: " + validity);

        /** Block Validity Checks **/

        int[] block = new int[9]; // this stores the 3x3 block to be checked
        int offsetX, offsetY, x, y;

        for (int blockIndex = 0; blockIndex < 9; blockIndex++) {
            // simplified and fixed
            // offsets are used instead of weird math as indices which fixed the problem
            block = board.getBoardBlock(blockIndex);
            for (int cell : block) {
                if (valueBank[cell] == 1 && cell != 0)
                {
                    validity = false;
                    System.out.println("Duplicate detected in block " + blockIndex + ": " + cell);
                }
                else
                    valueBank[cell] = 1;
            }
            for (zero = 0; zero < 10; zero++)
                valueBank[zero] = 0;

        }
        System.out.println("Valid Blocks?: " + validity);
        return validity;
    }

    private static void SolutionFinder(Sudoku sudoku)
    {
        System.out.println("Beginning solving algorithm...");
        boolean done = false;
        // todo: change the code below to assign a value when only one spot for a number exists
        /**
         *  Creative use of Sudoku's rules are the best way to solve Sudokus. For example,
         *  if 3 is found in 2 columns within a 3x9 column, 3 in the remaining 3x3 block
         *  must be in the final column. Beware: not all Sudokus are solvable, as a lack
         *  of information creates multiple possible solutions which is invalid as a puzzle.
        **/

        // sorry
        // blocks, then rows, then columns, then repeat
        // use idk how many arrays, zero them out between uses

        int[][] solution = sudoku.getBoard();
        int[] focus;
        int[] unusedValues = {1,1,1,1,1,1,1,1,1,1};
        int[][][] possible = new int[9][9][10];
        int x, y, z;
        while (!done)
        {
            done = true;
            for (x = 0; x < 9; x++) // Gets possible values per block
            {
                focus = sudoku.getBoardBlock(x); // Get the current working block
                for (y = 0; y < 9; y++) // Get and exclude the current known values
                {
                    if (focus[y] != 0)
                        unusedValues[focus[y]] = 0;
                }
                for (y = 0; y < 9; y++) // Remaining values labeled possible
                {
                    if (focus[y] == 0)
                        possible[x][y] = unusedValues; // Might not work if Java handles this as a pointer
                }
                for (int i : unusedValues) // Clear unused values
                    i = 1;

            }

            for (x = 0; x < 9; x++) // Gets possible values per row
            {
                focus = sudoku.getBoardRow(x); // Get the current working row
                for (y = 0; y < 9; y++) // Get and exclude the current known values
                {
                    if (focus[y] != 0)
                        unusedValues[focus[y]] = 0;
                }
                for (y = 0; y < 9; y++) // Excluded values removed from list of possible values
                {
                    if (focus[y] == 0)
                        for (z = 1; z < 10; z++)
                        {
                            possible[x][y][z] = possible[x][y][z] & unusedValues[z];
                        }
                }
                for (int i : unusedValues) // Clear unused values
                    i = 1;
            }

            for (x = 0; x < 9; x++) // Gets possible values per column
            {
                focus = sudoku.getBoardRow(x); // Get the current working column
                for (y = 0; y < 9; y++) // Get and exclude the current known values
                {
                    if (focus[y] != 0)
                        unusedValues[focus[y]] = 0;
                }
                for (y = 0; y < 9; y++) // Excluded values removed from list of possible values
                {
                    if (focus[y] == 0)
                        for (z = 1; z < 10; z++)
                        {
                            possible[x][y][z] = possible[x][y][z] & unusedValues[z];
                        }
                }
                for (int i : unusedValues) // Clear unused values
                    i = 1;
            }



            for (x = 0; x < 9; x++)
                for (y = 0; y < 9; y++)
                {
                    for (z = 1; z < 10; z++)
                    {
                        if (possible[x][y][z] == 1 && possible[x][y][0] == 0)
                            possible[x][y][0] = z;
                        else if (possible[x][y][z] == 1)
                            possible[x][y][0] = 0;
                    }
                    if (possible[x][y][0] != 0)
                        solution[x][y] = possible[x][y][0];

                }

            for (x = 0; x < 9; x++)
            {
                focus = sudoku.getBoardRow(x);
                for (y = 0; y < 9; y++)
                {
                    if (focus[y] == 0)
                        done = false;
                }

            }

        }


        sudoku.setSolution(solution);
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

        SolutionFinder(sudoku);
    }

    public static void main(String[] args)
    {
        Main program = new Main();
        program.sequence();
    }
}