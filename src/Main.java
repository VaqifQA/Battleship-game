import java.util.*;

public class Main {

    private static final int GRID_SIZE = 10;

    public static void main(String[] args) {
        char[][] field = initializeGameField();
        printGameField(field);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the coordinates of the ship:");
        String input = scanner.nextLine();

        String[] coordinates = input.split(" ");
        if (coordinates.length != 2) {
            System.out.println("Error!");
            return;
        }

        int[] start = parseCoordinates(coordinates[0]);
        int[] end = parseCoordinates(coordinates[1]);

        if (start == null || end == null || !validateCoordinates(start, end)) {
            System.out.println("Error!");
            return;
        }

        List<int[]> shipParts = getShipParts(start, end);
        for (int[] part : shipParts) {
            field[part[0]][part[1]] = 'O';
        }

        System.out.println("Length: " + shipParts.size());
        System.out.print("Parts: ");
        for (int[] part : shipParts) {
            System.out.print((char) ('A' + part[0]) + String.valueOf(part[1] + 1) + " ");
        }
        System.out.println();

        printGameField(field);
    }

    private static char[][] initializeGameField() {
        char[][] field = new char[GRID_SIZE][GRID_SIZE];
        for (int i = 0; i < GRID_SIZE; i++) {
            Arrays.fill(field[i], '~');
        }
        return field;
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

    private static int[] parseCoordinates(String coord) {
        if (coord.length() < 2 || coord.length() > 3) {
            return null;
        }

        char row = Character.toUpperCase(coord.charAt(0));
        String colPart = coord.substring(1);

        if (!Character.isLetter(row) || row < 'A' || row > 'J') {
            return null;
        }

        try {
            int col = Integer.parseInt(colPart);
            if (col < 1 || col > 10) {
                return null;
            }
            return new int[]{row - 'A', col - 1};
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static boolean validateCoordinates(int[] start, int[] end) {
        return (start[0] == end[0] || start[1] == end[1]) &&
                start[0] >= 0 && start[0] < GRID_SIZE &&
                start[1] >= 0 && start[1] < GRID_SIZE &&
                end[0] >= 0 && end[0] < GRID_SIZE &&
                end[1] >= 0 && end[1] < GRID_SIZE;
    }

    private static List<int[]> getShipParts(int[] start, int[] end) {
        List<int[]> parts = new ArrayList<>();

        if (start[0] == end[0]) { // Horizontal
            int row = start[0];
            int colStart = Math.min(start[1], end[1]);
            int colEnd = Math.max(start[1], end[1]);

            for (int col = colStart; col <= colEnd; col++) {
                parts.add(new int[]{row, col});
            }
        } else if (start[1] == end[1]) { // Vertical
            int col = start[1];
            int rowStart = Math.min(start[0], end[0]);
            int rowEnd = Math.max(start[0], end[0]);

            for (int row = rowStart; row <= rowEnd; row++) {
                parts.add(new int[]{row, col});
            }
        }

        return parts;
    }
}
