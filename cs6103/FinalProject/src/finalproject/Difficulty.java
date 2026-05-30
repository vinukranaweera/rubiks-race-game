package finalproject;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Defines game constraints and balancing for different challenge levels.
 */
public enum Difficulty {
	NORMAL(5, 3, 10000, 80, 120), // 5x5 board, 3x3 target, 80 moves, 120 seconds
	HARD(6, 4, 20000, 120, 180); // 6x6 board, 4x4 target, 120 moves, 180 seconds

	public final int size, targetSize, baseScorePotential, maxMoves, maxTime;

	Difficulty(int s, int ts, int b, int mm, int mt) {
		this.size = s;
		this.targetSize = ts;
		this.baseScorePotential = b;
		this.maxMoves = mm;
		this.maxTime = mt;
	}

	/**
	 * Generates a balanced set of colors to ensure the board has a playable
	 * distribution of all Rubik colors.
	 */
	public List<Color> getColorPool() {
		Color[] colors = { Tile.RUBIK_WHITE, Tile.RUBIK_YELLOW, Tile.RUBIK_RED, Tile.RUBIK_ORANGE, Tile.RUBIK_BLUE,
				Tile.RUBIK_GREEN };

		List<Color> pool = new ArrayList<>();
		int tilesNeeded = (size * size) - 1; // Total slots minus the empty one

		// Populate pool with an even number of each color first
		for (int i = 0; i < tilesNeeded; i++) {
			pool.add(colors[i % colors.length]);
		}

		// Inject extra random tiles to break the even pattern before shuffling
		while (pool.size() < tilesNeeded + 5) {
			pool.add(colors[new java.util.Random().nextInt(colors.length)]);
		}

		Collections.shuffle(pool);

		// Only return the exact amount needed to maintain valid state
		return pool.subList(0, tilesNeeded);
	}
}