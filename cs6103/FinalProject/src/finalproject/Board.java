package finalproject;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Maintains the internal state of the board and target pattern, and handles
 * core game logic such as tile movement and win condition detection.
 */
public class Board {
	private Tile[][] grid;
	private Tile[][] target;
	private final Difficulty difficulty;
	private int size;

	public Board(Difficulty difficulty) {
		this.difficulty = difficulty;
		this.size = difficulty.size;
		this.grid = new Tile[size][size];
		this.target = new Tile[difficulty.targetSize][difficulty.targetSize];

		// Generate the target pattern first
		generateTarget();

		// Initialize the board based on target colors
		initializeBoard();
	}

	/**
	 * Populates board with necessary target colors and distractor "junk" colors.
	 */
	private void initializeBoard() {
		int totalSlots = size * size - 1;
		List<Color> pool = new ArrayList<>();

		// Count required colors from target
		Map<Color, Integer> targetCounts = new HashMap<>();
		for (int i = 0; i < difficulty.targetSize; i++) {
			for (int j = 0; j < difficulty.targetSize; j++) {
				Color c = target[i][j].getColor();
				targetCounts.put(c, targetCounts.getOrDefault(c, 0) + 1);
			}
		}

		// Identify colors not present in the target to act as "blocker" tiles
		Color[] rubikColors = { Tile.RUBIK_WHITE, Tile.RUBIK_YELLOW, Tile.RUBIK_RED, Tile.RUBIK_ORANGE, Tile.RUBIK_BLUE,
				Tile.RUBIK_GREEN };
		List<Color> junkColors = new ArrayList<>();
		for (Color c : rubikColors) {
			if (!targetCounts.containsKey(c)) {
				junkColors.add(c);
			}
		}

		Random rand = new Random();

		// Add mandatory target colors plus a small random buffer for variety
		for (Map.Entry<Color, Integer> entry : targetCounts.entrySet()) {
			int required = entry.getValue();
			int buffer = rand.nextInt(3) + 1; // Adds 1, 2, or 3 extra tiles of this color
			for (int i = 0; i < (required + buffer); i++) {
				if (pool.size() < totalSlots) {
					pool.add(entry.getKey());
				}
			}
		}

		// Fill remaining slots with junk colors to increase puzzle complexity
		while (pool.size() < totalSlots) {
			if (junkColors.isEmpty()) {
				// Fallback if the target uses all 6 colors
				pool.add(rubikColors[rand.nextInt(rubikColors.length)]);
			} else {
				pool.add(junkColors.get(rand.nextInt(junkColors.size())));
			}
		}

		// Map the shuffled collection to the 2D grid array
		Collections.shuffle(pool);
		int count = 0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (i == size - 1 && j == size - 1) {
					grid[i][j] = null; // Designate the empty space for sliding
				} else {
					grid[i][j] = new Tile(pool.get(count++));
				}
			}
		}
	}

	/**
	 * Handles sliding mechanics for rows and columns.
	 */
	public boolean slideTile(int row, int col) {
		int emptyRow = -1;
		int emptyCol = -1;

		// Locate current position of the empty space (null)
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (grid[i][j] == null) {
					emptyRow = i;
					emptyCol = j;
					break;
				}
			}
		}

		// Handle horizontal movement in a row
		if (row == emptyRow) {
			int dir = (col < emptyCol) ? 1 : -1;
			for (int j = emptyCol; j != col; j -= dir) {
				grid[row][j] = grid[row][j - dir];
			}
			grid[row][col] = null;
			return true;
		}
		// Handle vertical movement in a column
		else if (col == emptyCol) {
			int dir = (row < emptyRow) ? 1 : -1; // Corrected logic flow for multi-tile
			for (int i = emptyRow; i != row; i -= dir) {
				grid[i][col] = grid[i - dir][col];
			}
			grid[row][col] = null;
			return true;
		}
		return false;
	}

	/**
	 * Randomly generates the 3x3 or 4x4 goal pattern.
	 */
	private void generateTarget() {
		Random rand = new Random();
		Color[] colors = { Tile.RUBIK_WHITE, Tile.RUBIK_YELLOW, Tile.RUBIK_RED, Tile.RUBIK_ORANGE, Tile.RUBIK_BLUE,
				Tile.RUBIK_GREEN };

		int tSize = difficulty.targetSize;
		target = new Tile[tSize][tSize];

		for (int i = 0; i < tSize; i++) {
			for (int j = 0; j < tSize; j++) {
				target[i][j] = new Tile(colors[rand.nextInt(colors.length)]);
			}
		}
	}

	/**
	 * Checks if a specific grid tile matches the centered target pattern.
	 */
	public boolean isMatchingTarget(int gridRow, int gridCol) {
		int tSize = difficulty.targetSize;
		int offset = (size - tSize) / 2; // Offset to center the target check
		int targetR = gridRow - offset;
		int targetC = gridCol - offset;

		if (targetR >= 0 && targetR < tSize && targetC >= 0 && targetC < tSize) {
			if (grid[gridRow][gridCol] == null)
				return false;
			return grid[gridRow][gridCol].getColor().equals(target[targetR][targetC].getColor());
		}
		return false;
	}

	/**
	 * Win detection: Validates if the entire center grid aligns with the target.
	 */
	public boolean checkWin() {
		return getMatchCount() == (difficulty.targetSize * difficulty.targetSize);
	}

	/**
	 * Checks how many squares match according to the target.
	 */
	public int getMatchCount() {
		int matches = 0;
		int tSize = difficulty.targetSize;
		int offset = (size - tSize) / 2;
		for (int i = 0; i < tSize; i++) {
			for (int j = 0; j < tSize; j++) {
				if (isMatchingTarget(i + offset, j + offset))
					matches++;
			}
		}
		return matches;
	}

	/**
	 * Refresh match label with dynamic denominator
	 */
	public String getMatchStatus() {
		int current = getMatchCount();
		int total = difficulty.targetSize * difficulty.targetSize;
		return "Matches: " + current + " / " + total;
	}

	public Tile[][] getGrid() {
		return grid;
	}

	public Tile[][] getTarget() {
		return target;
	}

	public Difficulty getDifficulty() {
		return difficulty;
	}
}