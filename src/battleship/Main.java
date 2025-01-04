package battleship;

import java.util.*;

public class Main {

    private static final int GRID_SIZE = 10;
    private static final char WATER = '~';
    private static final char SHIP = 'O';
    private static final char HIT = 'X';
    private static final char MISS = 'M';
    private static final String[] SHIP_NAMES = {
            "Aircraft Carrier",
            "Battleship",
            "Submarine",
            "Cruiser",
            "Destroyer"
    };
    private static final int[] SHIP_SIZES = {5, 4, 3, 3, 2};
    private static final char[][] board = new char[GRID_SIZE][GRID_SIZE];
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        initializeGameField();
        printGameField();

        for (int i = 0; i < SHIP_NAMES.length; i++) {
            boolean placed = false;
            while (!placed) {
                System.out.printf("Enter the coordinates of the %s (%d cells):%n", SHIP_NAMES[i], SHIP_SIZES[i]);
                String[] input = scanner.nextLine().toUpperCase().split(" ");
                if (input.length != 2) {
                    System.out.println("Incorrect input. Try again:");
                    continue;
                }
                int[] start = parseCoordinates(input[0]);
                int[] end = parseCoordinates(input[1]);
                if (start == null || end == null) {
                    System.out.println("Error! Incorrect coordinates. Try again:");
                    continue;
                }

                if (placeShip(start[0], start[1], end[0], end[1], SHIP_SIZES[i])) {
                    placed = true;
                    printGameField();
                }
            }
        }

        System.out.println("All ships have been placed!\n The game starts!");

        boolean gameRunning = true;
        while(gameRunning) {
            System.out.println("Take a shot!");
            String input = scanner.nextLine().toUpperCase();
            int[] shot = parseCoordinates(input);
            if (shot == null) {
                System.out.println("Error! You entered the wrong coordinates! Try again:");
                continue;
            }
            gameRunning = takeShot(shot[0], shot[1]);
            printGameField();
        }
    }

    private static void initializeGameField() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                board[i][j] = WATER;
            }
        }
    }

    private static void printGameField() {
        System.out.print("  ");
        for (int i = 1; i <= GRID_SIZE; i++) {
            System.out.print(i + " ");
        }
        System.out.println();

        for (int i = 0; i < GRID_SIZE; i++) {
            System.out.print((char) ('A' + i) + " ");
            for (int j = 0; j < GRID_SIZE; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    private static boolean placeShip(int row1, int col1, int row2, int col2, int expectedLength) {

        if (row1 != row2 && col1 != col2) {
            System.out.println("Error! Wrong ship location! Try again:");
            return false;
        }

        int length = (row1 == row2) ? Math.abs(col2 - col1) + 1 : Math.abs(row2 - row1) + 1;

        if (length != expectedLength) {
            System.out.printf("Error! Wrong length of the %s! Try again:%n", SHIP_NAMES[SHIP_SIZES.length - expectedLength]);
            return false;
        }

        int startRow = Math.min(row1, row2);
        int endRow = Math.max(row1, row2);
        int startCol = Math.min(col1, col2);
        int endCol = Math.max(col1, col2);

        if (!isValidPlacement(startRow, startCol, endRow, endCol)) {
            System.out.println("Error! You placed it too close to another one. Try again:");
            return false;
        }

        for (int i = startRow; i <= endRow; i++) {
            for (int j = startCol; j <= endCol; j++) {
                board[i][j] = SHIP;
            }
        }
        return true;
    }

    private static int[] parseCoordinates(String coord) {
        if (coord.length() < 2 || coord.length() > 3) {
            return null;
        }

        int row = coord.charAt(0) - 'A';
        int col;
        try {
            col = Integer.parseInt(coord.substring(1)) - 1;
        } catch (NumberFormatException e) {
            return null;
        }
        if (row < 0 || row >= GRID_SIZE || col < 0 || col >= GRID_SIZE) {
            return null;
        }
        return new int[]{row, col};
    }

    private static boolean isValidPlacement(int row1, int col1, int row2, int col2) {
        for (int i = Math.max(0, row1 - 1); i <= Math.min(GRID_SIZE - 1, row2 + 1); i++) {
            for (int j = Math.max(0, col1 - 1); j <= Math.min(GRID_SIZE - 1, col2 + 1); j++) {
                if (board[i][j] == SHIP) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean takeShot(int row, int col) {

        if (board[row][col] == SHIP) {
            board[row][col] = HIT;
            System.out.println("You hit a ship!");
        } else if (board[row][col] == WATER) {
            board[row][col] = MISS;
            System.out.println("You missed!");
        } else {
            System.out.println("You already shot here. Try again:");
            return true;
        }

        for (char[] rowData : board) {
            for (char cell : rowData) {
                if (cell == SHIP) {
                    return true;
                }
            }
        }

        System.out.println("Congratulations! You sank all the ships!");
        return false;
    }

}
