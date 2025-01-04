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
    private static final char[][] foggyBoard = new char[GRID_SIZE][GRID_SIZE];
    private static final Scanner scanner = new Scanner(System.in);
    private static final Map<String, Integer> shipHealth = new HashMap<>();
    private static final Map<String, List<int[]>> shipCoordinates = new HashMap<>();

    public static void main(String[] args) {
        initializeGameField();
        initializeShipHealth();
        printGameField(board);

        placeAllShips();
        System.out.println("All ships have been placed!\nThe game starts!");
        printGameField(foggyBoard);

        playGame();
    }

    private static void initializeGameField() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                board[i][j] = WATER;
                foggyBoard[i][j] = WATER;
            }
        }
    }

    private static void initializeShipHealth() {
        for (int i = 0; i < SHIP_NAMES.length; i++) {
            shipHealth.put(SHIP_NAMES[i], SHIP_SIZES[i]);
        }
    }

    private static void printGameField(char[][] field) {
        System.out.print("  ");
        for (int i = 1; i <= GRID_SIZE; i++) {
            System.out.print(i + " ");
        }
        System.out.println();

        for (int i = 0; i < GRID_SIZE; i++) {
            System.out.print((char) ('A' + i) + " ");
            for (int j = 0; j < GRID_SIZE; j++) {
                System.out.print(field[i][j] + " ");
            }
            System.out.println();
        }
    }

    private static void placeAllShips() {
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

                if (placeShip(SHIP_NAMES[i], start[0], start[1], end[0], end[1], SHIP_SIZES[i])) {
                    placed = true;
                    printGameField(board);
                }
            }
        }
    }

    private static boolean placeShip(String shipName, int row1, int col1, int row2, int col2, int expectedLength) {
        if (row1 != row2 && col1 != col2) {
            System.out.println("Error! Wrong ship location! Try again:");
            return false;
        }

        int length = (row1 == row2) ? Math.abs(col2 - col1) + 1 : Math.abs(row2 - row1) + 1;

        if (length != expectedLength) {
            System.out.printf("Error! Wrong length of the %s! Try again:%n", shipName);
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

        List<int[]> coordinates = new ArrayList<>();
        for (int i = startRow; i <= endRow; i++) {
            for (int j = startCol; j <= endCol; j++) {
                coordinates.add(new int[]{i, j});
                board[i][j] = SHIP;
            }
        }
        shipCoordinates.put(shipName, coordinates);
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

    private static void playGame() {
        boolean gameRunning = true;
        while (gameRunning) {
            System.out.println("Take a shot!");
            String input = scanner.nextLine().toUpperCase();
            int[] shot = parseCoordinates(input);

            if (shot == null) {
                System.out.println("Error! You entered wrong coordinates! Try again:");
                continue;
            }

            int row = shot[0];
            int col = shot[1];

            if (board[row][col] == SHIP) {
                foggyBoard[row][col] = HIT;
                board[row][col] = HIT;
                System.out.println("You hit a ship!");
                if (checkAndHandleSunkShip(row, col)) {
                    System.out.println("You sank a ship! Specify a new target:");
                }
            } else if (board[row][col] == WATER) {
                foggyBoard[row][col] = MISS;
                board[row][col] = MISS;
                System.out.println("You missed!");
            } else {
                System.out.println("You already shot here. Try again:");
            }

            printGameField(foggyBoard);

            if (checkWinCondition()) {
                gameRunning = false;
            }
        }
    }

    private static boolean checkAndHandleSunkShip(int row, int col) {
        for (String ship : SHIP_NAMES) {
            List<int[]> coordinates = shipCoordinates.get(ship);
            boolean isSunk = true;

            for (int[] coordinate : coordinates) {
                if (board[coordinate[0]][coordinate[1]] == SHIP) {
                    isSunk = false;
                    break;
                }
            }

            if (isSunk) {
                shipHealth.put(ship, 0); // Mark the ship as sunk
                System.out.println("You sank the " + ship + "!");
                return true;
            }
        }
        return false;
    }

    private static boolean checkWinCondition() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (board[i][j] == SHIP) {
                    return false; // If there are still ships left, game isn't over.
                }
            }
        }
        System.out.println("You sank the last ship. You won. Congratulations!");
        return true; // All ships are sunk, player wins.
    }
}
