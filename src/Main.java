import java.util.Scanner;
import static java.lang.System.exit;
public class Main
{
    // Overarching Sudoku type
    // all of my experience is with C, C++ and C# so hopefully this works
    // it wasn't working unless I made it static for some reason. Good thing I only need 1 I guess??
    private static class Sudoku
    {
        private int[][] board;     /** This is the puzzle as input by the user. **/
        private int[][] solution;  /** This is the puzzle solution. **/

        // Class constructor, setters and getters below.
        public Sudoku()
        {
            board = new int[9][9];
            solution = new int[9][9];
        }
        public void setBoard(int[][] input)
        {
            board = input;
        }
        public void setSolution(int[][] solved)
        {
            solution = solved;
        }
        public int[] getBoardRow(int rowIndex)
        {
            return board[rowIndex];
        }
        public int[] getBoardColumn(int colIndex)
        {
            int[] currentCol = new int[9];
            for (int i = 0; i < 9; i++)
                currentCol[i] = board[i][colIndex];
            return currentCol;
        }
        public int[][] getSolution()
        {
            return solution;
        }

    }
    private static int[][] InitializeSudoku()
    {
         // stores input from user to be turned into a sudoku line
        int[][] newBoard = new int[9][9]; // [row][column]
        boolean validity = true;
        String newLine;
        System.out.println("Welcome to Sudoku Solver. Please start by entering the first line of the sudoku you would like me to solve. Fill in any blank spaces with 0.");

        // iteratorX == row. iteratorY == column.
        int iteratorX = 0, iteratorY = 0;

        // iterates by row
        for (iteratorX = 0; iteratorX < 9; iteratorX++)
        {
            do {
                // Due to reasons, this doesn't work. lol
                Scanner input = new Scanner(System.in);
                int middleMan = input.nextInt();
                newLine = String.valueOf(middleMan);
                validity = true;

                // prevents out of bounds exceptions by checking length validity.
                if (newLine.length() < 9)
                {
                    System.out.println("Error, input was below expected length. Remember, Sudokus are 9x9. Please try again:");
                    validity = false;
                }

                // iterates column within upper row. since I need the extra dimension, foreach won't work here.
                for (iteratorY = 0; iteratorY < 9; iteratorY++)
                {
                    newBoard[iteratorX][iteratorY] = Character.getNumericValue(newLine.toCharArray()[iteratorY]); // can return -1 or -2 if invalid input is given
                }
            } while (!validity); // if validity is false, loop will repeat.
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
        for (i = 0; i < 9; i++)
        {
            for (int cell : board.getBoardRow(i))
            {
                if (valueBank[cell] == 1 && cell != 0)
                    validity = false;
                else
                    valueBank[cell] = 1;
            }
        }
        // zero out valueBank for future use
        for (int value : valueBank)
            valueBank[value] = 0;

        /** Column Validity Checks **/

        for (i = 0; i < 9; i++)
        {
            for (int cell : board.getBoardColumn(i))
            {
                if (valueBank[cell] == 1 && cell != 0)
                    validity = false;
                else
                    valueBank[cell] = 1;
            }
        }
        // zero out valueBank for future use
        for (int value : valueBank)
            valueBank[value] = 0;

        /** Cell Validity Checks **/

        /**
         * this one was trickier
         * first for loop determines which block out of 9 total,
         * second for loop gets the values in the specified 3x3 block (I hope...),
         * third for loop uses valueBank to check for duplicates that aren't 0,
         * final for loop resets valueBank for the next pass
        **/

        int[] block = new int[9]; // this stores the 3x3 block to be checked
        for (int blockIndex = 0; blockIndex < 9; blockIndex++) {
            for (i = 0; i < i + 9; i++) {
                block[i] = board.getBoardRow((i / 3))[(i % 3) + (blockIndex / 3)];
            }
            for (int cell : block) {
                if (valueBank[cell] == 1 && cell != 0)
                    validity = false;
                else
                    valueBank[cell] = 1;
            }
            for (int value : valueBank)
                valueBank[value] = 0;
        }

        return validity;
    }

    private static int[][] SolutionFinder(Sudoku sudoku)
    {
        System.out.println("Beginning solving algorithm...");
        // todo: use iterative steps to find solution. More methods may be needed.
        /**
         *  Creative use of Sudoku's rules are the best way to solve Sudokus. For example,
         *  if 3 is found in 2 columns within a 3x9 column, 3 in the remaining 3x3 block
         *  must be in the final column. Beware: not all Sudokus are solvable, as a lack
         *  of information creates multiple possible solutions which is invalid as a puzzle.
        **/
        return new int[9][9];
    }

    public static void main(String[] args)
    {
        Sudoku sudoku = new Sudoku();
        sudoku.setBoard(InitializeSudoku());

        if (!Validator(sudoku))
        {
            System.out.println("The Sudoku provided wasn't valid. Exiting...");
            exit(1);
        }
        else
            System.out.println("The Sudoku is syntactically sound.");

        sudoku.setSolution(SolutionFinder(sudoku));


    }
}