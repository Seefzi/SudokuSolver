import java.util.Scanner;
import static java.lang.System.exit;
public class Main
{
    private static int[][] InitializeSudoku()
    {
        String newLine = ""; // stores input from user to be turned into a sudoku line
        int[][] sudoku = new int[9][9]; // [row][column]
        boolean validity = true;
        System.out.println("Welcome to Sudoku Solver. Please start by entering the first line of the sudoku you would like me to solve. Fill in any blank spaces with 0.");

        // iteratorX == row. iteratorY == column.
        int iteratorX = 0, iteratorY = 0;

        // iterates by row
        for (iteratorX = 0; iteratorX < 9; iteratorX++)
        {
            do {
                Scanner input = new Scanner(System.in);
                newLine = input.nextLine();
                input.close();
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
                    sudoku[iteratorX][iteratorY] = Character.getNumericValue(newLine.toCharArray()[iteratorY]); // can return -1 or -2 if invalid input is given
                }
            } while (!validity); // if validity is false, loop will repeat.
        }

/**------------------------------------------------------------------------------------------------------------
*   Sudoku rules are as follows:                                                                              *
*     * A row cannot contain the same value more than once and must contain every value from 1-9              *
*     * A column cannot contain the same value more than once and must contain every value from 1-9           *
*     * Each 3x3 subgrid cannot contain the same value more than once and must contain every value from 1-9   *
*     * A Sudoku must contain enough information for the Sudoku to be solvable                                *
------------------------------------------------------------------------------------------------------------**/

        System.out.println("Checking Sudoku's syntactic validity...");

        /** ROW VALIDITY CHECK **/

        int[] valueBank = new int[9];
        validity = true;
        for (int[] row : sudoku) {
            for (int cell : row) {
                // checks to make sure the values are within acceptable range, i.e. not -1 or -2. This will run through every value, so it's not needed in future checks.
                if (cell < 0 || cell > 9) {
                    System.out.println("Error, input contained letters or numeric values outside of expected bounds. Please try again:");
                    validity = false;
                }
                // uses the valueBank array to ensure the rule of values in a row is followed
                if (valueBank[cell] == 1) {
                    System.out.println("Error, input contains " + cell + " multiple times, which is invalid for a Sudoku. Please try again:");
                    validity = false;
                }
                // sets the valueBank index for the number in the cell to 1 so that it can be checked against the next iteration.
                valueBank[cell] = 1;
            }
        }
        // zero out valueBank for future use
        for (int value : valueBank)
            value = 0;

        /** COLUMN VALIDITY CHECK **/

        // iterate by column instead of row
        for (iteratorY = 0; iteratorY < 9; iteratorY++)
        {
            for (iteratorX = 0; iteratorX < 9; iteratorX++)
            {
                // uses the valueBank array to ensure the rule of values in a column is followed
                if (valueBank[sudoku[iteratorX][iteratorY]] == 1) {
                    System.out.println("Error, column " + iteratorY + " contains " + sudoku[iteratorX][iteratorY] + " multiple times, which is invalid for a Sudoku. Please try again:");
                    validity = false;
                }
                valueBank[sudoku[iteratorX][iteratorY]] = 1;
            }
        }
        // zero out valueBank for future use
        for (int value : valueBank)
            value = 0;

        

        if (!validity)
        {
            System.out.println("There's a problem with your Sudoku. Please review any error messages and try again.");
            exit(1);
        }

        return sudoku;
    }
    public static void main(String[] args)
    {
        int[][] sudoku = InitializeSudoku();


    }
}