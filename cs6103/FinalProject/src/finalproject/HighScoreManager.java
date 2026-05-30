package finalproject;

import java.io.*;
import java.util.*;

/**
 * Handles persistent storage for game metrics (moves, time, and score).
 */
public class HighScoreManager {
	private static final String FILE_NAME = "scores.txt";

	/**
	 * Appends a new score record to the local file (scores.txt).
	 */
	public static void saveScore(String name, int moves, int time, int score, String diff, String tags) {
		try (PrintWriter out = new PrintWriter(new FileWriter(FILE_NAME, true))) {
			// Add a comma prefix only if there are tags to display
			String tagPart = (tags == null || tags.isEmpty()) ? "" : ", " + tags;

			// Format: Name: Score (Moves: X, Time: Xs, Difficulty: Y)
			out.println(name + ": " + score + " (Moves: " + moves + ", Time: " + time + "s, Difficulty: " + diff
					+ tagPart + ")");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reads, validates, and sorts all saved scores descending.
	 */
	public static List<String> getHighScores() {
		List<String> scores = new ArrayList<>();
		File file = new File(FILE_NAME);

		if (!file.exists())
			return scores;

		try (Scanner scanner = new Scanner(file)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				// Skip empty lines to prevent NumberFormatException
				if (!line.trim().isEmpty() && line.contains(":")) {
					scores.add(line);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// Sort by parsing the numerical score value
		scores.sort((a, b) -> {
			try {
				int scoreA = Integer.parseInt(a.split(":")[1].trim().split(" ")[0]);
				int scoreB = Integer.parseInt(b.split(":")[1].trim().split(" ")[0]);

				// Sort descending (highest score first)
				return Integer.compare(scoreB, scoreA);
			} catch (Exception e) {
				return 0; // If a line is corrupted, don't crash, just don't move it
			}
		});

		return scores;
	}
}