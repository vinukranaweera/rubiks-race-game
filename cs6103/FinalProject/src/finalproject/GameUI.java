package finalproject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * The View component of the MVC architecture. Manages the "Interactive GUI" and
 * "Custom rendering" features. Handles event-driven input (mouse clicks, button
 * actions) to drive the game.
 */
public class GameUI extends JFrame {
	private GameController controller;
	private RubikTile[][] buttons;
	private JPanel targetPanel;
	private JLabel statsLabel;
	private JComboBox<Difficulty> diffSelector;
	private JLabel matchLabel; // The X / (9/16) label
	private GameTimer timer;
	private JPanel boardPanel; // To swap during difficulty changes
	private JButton startPauseBtn; // Start/Pause/Resume button
	private JCheckBox limitedMovesCheck; // Option for move limits
	private JCheckBox limitedTimeCheck;
	private final Color TRAY_COLOR = new Color(85, 85, 85);

	public GameUI() {
		controller = new GameController();
		setTitle("Rubik's Race - Single Player");
		setSize(700, 900);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout(10, 10));

		// --- TOP PANEL: Information and Controls ---
		// Uses a 1x2 Grid to separate instructions from game stats
		JPanel topContainer = new JPanel(new GridLayout(1, 2, 20, 0));
		topContainer.setBorder(new EmptyBorder(15, 20, 15, 20));

		// Top Left: Title and User Instructions
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
		
		JLabel title = new JLabel("RUBIK'S RACE");
		title.setFont(new Font("SansSerif", Font.BOLD, 22));

		String instructionText = "Match the center grid to the target pattern. "
	            + "Slide tiles using the empty space. Click far tiles to move multiple at once! "
	            + "Enable harder modes or difficulty for higher scores. Press START to begin!";
	    
	    JTextArea inst = new JTextArea(instructionText);
	    inst.setWrapStyleWord(true);
	    inst.setLineWrap(true);
	    inst.setOpaque(false);
	    inst.setEditable(false);
	    inst.setFont(new Font("SansSerif", Font.PLAIN, 12));
	    inst.setMaximumSize(new Dimension(300, 100));

	    title.setAlignmentX(Component.LEFT_ALIGNMENT);
	    inst.setAlignmentX(Component.LEFT_ALIGNMENT);

	    leftPanel.add(title);
	    leftPanel.add(Box.createVerticalStrut(5));
	    leftPanel.add(inst);

		// Top Right: Score Tracking and Settings
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

		statsLabel = new JLabel("Moves: 0 | Time: 0s", SwingConstants.RIGHT);
		matchLabel = new JLabel("Matches: 0 / 0", SwingConstants.RIGHT);
		statsLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		matchLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		// Difficulty Dropdown
		diffSelector = new JComboBox<>(Difficulty.values());
		diffSelector.addActionListener(e -> changeDifficulty());
		diffSelector.setAlignmentX(Component.RIGHT_ALIGNMENT);
		diffSelector.setMaximumSize(new Dimension(150, 25));

		// Layout for Move/Time limits
		JPanel checkboxRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		checkboxRow.setOpaque(false);

		limitedMovesCheck = new JCheckBox("Limited Moves");
		limitedMovesCheck.addActionListener(e -> refresh());

		limitedTimeCheck = new JCheckBox("Limited Time");
		limitedTimeCheck.addActionListener(e -> refresh());

		checkboxRow.add(limitedMovesCheck);
		checkboxRow.add(limitedTimeCheck);

		checkboxRow.setAlignmentX(Component.RIGHT_ALIGNMENT);

		rightPanel.add(diffSelector);
		rightPanel.add(Box.createVerticalStrut(5));
		rightPanel.add(checkboxRow);
		rightPanel.add(Box.createVerticalStrut(5));
		rightPanel.add(statsLabel);
		rightPanel.add(Box.createVerticalStrut(5));
		rightPanel.add(matchLabel);
		
		rightPanel.add(Box.createVerticalGlue());

		topContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
		topContainer.add(leftPanel);
		topContainer.add(rightPanel);
		add(topContainer, BorderLayout.NORTH);

		// --- CENTER PANEL: Gameplay Area ---
		// Contains both the target goal and the interactive player board
		JPanel centerContainer = new JPanel(new BorderLayout());

		// Target Pattern Display: "Randomly generated target 3x3/4x4 pattern"
		JPanel targetWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
		targetPanel = new JPanel();
		targetWrapper.add(targetPanel);
		centerContainer.add(targetWrapper, BorderLayout.NORTH);

		boardPanel = new JPanel(new GridBagLayout());
		boardPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		centerContainer.add(boardPanel, BorderLayout.CENTER);
		add(centerContainer, BorderLayout.CENTER);

		// --- BOTTOM PANEL: Global Actions ---
		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		startPauseBtn = new JButton("START GAME");
		startPauseBtn.addActionListener(e -> toggleStartPause());
		JButton resetBtn = new JButton("Reset Board");
		resetBtn.addActionListener(e -> resetGame());
		JButton scoresBtn = new JButton("Leaderboard");
		scoresBtn.addActionListener(e -> showScores());
		JButton exitBtn = new JButton("Exit");
		exitBtn.addActionListener(e -> System.exit(0));

		bottomPanel.add(startPauseBtn);
		bottomPanel.add(resetBtn);
		bottomPanel.add(scoresBtn);
		bottomPanel.add(exitBtn);
		add(bottomPanel, BorderLayout.SOUTH);

		resetGame();
		setVisible(true);
	}

	/**
	 * Processes tile clicks and checks win/loss conditions.
	 */
	private void handleMove(int r, int c) {
		// Only allow moves if the GameState is ACTIVE (game has started and not paused)
		if (controller.getState() != GameController.GameState.ACTIVE)
			return;

		// Perform the logical shift in the Board model
		if (controller.getBoard().slideTile(r, c)) {
			controller.incrementMoves();

			// Instant visual update to reflect the new tile positions and match counts
			refresh();

			// Check for Move Limit Loss
			if (limitedMovesCheck.isSelected()
					&& controller.getMoves() >= controller.getBoard().getDifficulty().maxMoves) {
				gameOverLoss();
				return;
			}

			// Check for win condition
			if (controller.isWin())
				handleWin();
		}
	}

	/**
	 * Rebuilds the grid based on current difficulty size.
	 */
	private void initGrid() {
		boardPanel.removeAll(); // Removes current grid
		boardPanel.setOpaque(false);
		boardPanel.setLayout(new GridBagLayout()); // Ensures the board stays centered

		Difficulty diff = controller.getBoard().getDifficulty();
		int size = diff.size;
		buttons = new RubikTile[size][size];

		// Use the custom RoundedPanel to serve as the "Player Tray"
		RoundedPanel playerTray = new RoundedPanel(25, TRAY_COLOR);
		playerTray.setLayout(new GridLayout(size, size, 2, 2));
		playerTray.setBorder(new EmptyBorder(10, 10, 10, 10));

		// Calculate size to fit the grid
		int tileSize = (size == 5) ? 80 : 65;
		int totalSide = (size * tileSize) + (size * 2) + 20;

		Dimension boardDim = new Dimension(totalSide, totalSide);
		playerTray.setPreferredSize(boardDim);
		playerTray.setMinimumSize(boardDim);
		playerTray.setMaximumSize(boardDim);

		// Populate the grid with custom RubikTile components
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				RubikTile btn = new RubikTile(null);
				btn.setPreferredSize(new Dimension(tileSize, tileSize));
				buttons[i][j] = btn;
				final int r = i, c = j;
				btn.addActionListener(e -> handleMove(r, c));
				playerTray.add(btn);
			}
		}

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.CENTER;

		boardPanel.add(playerTray, gbc);

		// Re-calculate the component tree to reflect the new grid size
		boardPanel.revalidate();
		boardPanel.repaint();
	}

	/**
	 * Handles score calculation, multi-threaded timer termination, and persistent
	 * storage.
	 */
	private void handleWin() {
		// Stop the GameTimer immediately
		timer.stopTimer();
		int score = calculateScore();
		Difficulty diff = controller.getBoard().getDifficulty();

		// Compile active gameplay modifiers (Tags) for the leaderboard record
		StringBuilder tags = new StringBuilder();
		if (limitedMovesCheck.isSelected())
			tags.append("Limited Moves");

		if (limitedTimeCheck.isSelected()) {
			if (tags.length() > 0)
				tags.append(", ");
			tags.append("Limited Time");
		}
		String tagsStr = tags.toString();

		// Display performance results to the user
		String winMessage = String.format("Winner!\nMoves: %d\nTime: %ds\nDifficulty: %s\n%s\nScore: %d",
				controller.getMoves(), timer.getTime(), diff.name(), tagsStr, score);

		JOptionPane.showMessageDialog(this, winMessage);

		// Prompt for name to save to scores.txt
		String name = JOptionPane.showInputDialog(this, "Enter Name:");
		if (name != null) {
			// Ensure saveScore also matches leaderboard format
			HighScoreManager.saveScore(name, controller.getMoves(), timer.getTime(), score, diff.name(), tagsStr);
		}
		resetGame();
	}

	/**
	 * Syncs visual tiles and HUD with the board state.
	 */
	private void refresh() {
		Board board = controller.getBoard();
		Difficulty diff = board.getDifficulty();
		int size = diff.size;

		// HUD UPDATE: Stats and HUD Feedback
		int currentMoves = controller.getMoves();
		String moveString = "Moves: " + currentMoves;

		if (limitedMovesCheck.isSelected()) {
			moveString += " / " + diff.maxMoves;
		}

		// Time Logic: Support both standard elapsed time and limited time countdown
		int elapsed = (timer != null) ? timer.getTime() : 0;
		String timeString;

		if (limitedTimeCheck.isSelected()) {
			int remaining = diff.maxTime - elapsed;
			timeString = "Time: " + Math.max(0, remaining) + "s";
		} else {
			timeString = "Time: " + elapsed + "s";
		}
		statsLabel.setText(moveString + " | " + timeString);

		int totalTarget = diff.targetSize * diff.targetSize;
		matchLabel.setText("Matches: " + board.getMatchCount() + " / " + totalTarget);

		// BOARD UPDATE: Update individual tile colors and neon borders
		Tile[][] grid = board.getGrid();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				RubikTile btn = (RubikTile) buttons[i][j];

				// Retrieve color
				Color c = (grid[i][j] != null) ? grid[i][j].getColor() : null;
				btn.setTileColor(c);

				// Set glow status if this tile aligns with the target goal
				boolean matching = board.isMatchingTarget(i, j);
				btn.setMatch(matching);
			}
		}
	}

	/**
	 * Switches between active, paused, and resumed states. Disables settings
	 * (difficulty/modes) once the game starts.
	 */
	private void toggleStartPause() {
		if (controller.getState() == GameController.GameState.NOT_STARTED) {
			controller.start();
			timer.setPaused(false);
			startPauseBtn.setText("PAUSE");
			limitedMovesCheck.setEnabled(false);
			limitedTimeCheck.setEnabled(false);
			diffSelector.setEnabled(false);
		} else if (controller.getState() == GameController.GameState.ACTIVE) {
			controller.pause();
			timer.setPaused(true);
			startPauseBtn.setText("RESUME");
		} else {
			controller.resume();
			timer.setPaused(false);
			startPauseBtn.setText("PAUSE");
		}
	}

	/**
	 * Logic for handling failure state when limited moves is active.
	 */
	private void gameOverLoss() {
		timer.stopTimer();
		controller.setState(GameController.GameState.FINISHED);
		JOptionPane.showMessageDialog(this, "Game Over! You ran out of moves.");
		resetGame();
	}

	/**
	 * Logic for handling failure state when limited time is active.
	 */
	private void gameOverTimeLoss() {
		timer.stopTimer();
		controller.setState(GameController.GameState.FINISHED);
		JOptionPane.showMessageDialog(this, "Game Over! You ran out of time.");
		resetGame();
	}

	/**
	 * Resets board, target, and the background timer thread.
	 */
	private void resetGame() {
		controller.reset();
		initGrid();
		updateTargetDisplay();
		if (timer != null)
			timer.stopTimer();

		// Timer thread with callback for HUD refresh and time limits
		timer = new GameTimer(s -> {
			// Check for Time Limit Loss
			if (limitedTimeCheck.isSelected()) {
				int limit = controller.getBoard().getDifficulty().maxTime;
				if (s >= limit) {
					gameOverTimeLoss();
					return;
				}
			}
			refresh(); // Standard HUD refresh every second
		});

		timer.start();
		timer.setPaused(true); // Wait for the user to press START

		// Reset UI control availability
		startPauseBtn.setText("START GAME");
		limitedMovesCheck.setEnabled(true);
		limitedTimeCheck.setEnabled(true);
		diffSelector.setEnabled(true);
		refresh();
	}

	/**
	 * Scoring Algorithm (Score = max(100, Base - Penalty)): Balanced based on difficulty and performance metrics.
	 * Includes point multipliers for move and time limits.
	 */
	private int calculateScore() {
		int base = controller.getBoard().getDifficulty().baseScorePotential;
		// Penalty logic: Efficiency (Moves) and Speed (Time) reduce the potential score
		int penalty = (timer.getTime() * 10) + (controller.getMoves() * 20);

		// 1.5x Multipliers for completing the game under specific constraints
		if (limitedMovesCheck.isSelected())
			base *= 1.5;
		if (limitedTimeCheck.isSelected())
			base *= 1.5;
		return Math.max(100, base - penalty); // Guaranteed minimum for completion
	}

	/**
	 * Updates the system state when a new difficulty is selected from the
	 * JComboBox.
	 */
	private void changeDifficulty() {
		Difficulty d = (Difficulty) diffSelector.getSelectedItem();
		controller.changeDifficulty(d);
		resetGame();
	}

	/**
	 * Leaderboard View: Fetches data from persistent storage.
	 */
	private void showScores() {
	    java.util.List<String> scores = HighScoreManager.getHighScores();
	    StringBuilder sb = new StringBuilder("--- TOP 10 LEADERBOARD ---\n\n");
	    
	    // Limit the display to the top 10 entries
	    int limit = Math.min(scores.size(), 10);
	    
	    for (int i = 0; i < limit; i++) {
	        // i + 1 creates the ranking (1, 2, 3...)
	        sb.append((i + 1)).append(". ").append(scores.get(i)).append("\n");
	    }
	    
	    if (scores.isEmpty()) {
	        sb.append("No scores recorded yet. Be the first!");
	    }

	    // Wrap in a JScrollPane if you expect a very long list later
	    JOptionPane.showMessageDialog(this, sb.toString(), "High Scores", JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Renders the goal pattern in the target panel.
	 */
	private void updateTargetDisplay() {
		targetPanel.removeAll();
		targetPanel.setOpaque(false);
		targetPanel.setLayout(new BorderLayout(0, 5));

		// Label stays on top, outside the tray
		JLabel targetLabel = new JLabel("TARGET PATTERN", SwingConstants.CENTER);
		targetLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
		targetPanel.add(targetLabel, BorderLayout.NORTH);

		int tSize = controller.getBoard().getDifficulty().targetSize;

		// The target tray uses the same aesthetic as the main board tray
		RoundedPanel targetTray = new RoundedPanel(20, TRAY_COLOR);
		targetTray.setLayout(new GridLayout(tSize, tSize, 2, 2));
		targetTray.setBorder(new EmptyBorder(8, 8, 8, 8)); // Padding inside the tray

		Tile[][] t = controller.getBoard().getTarget();
		for (int i = 0; i < tSize; i++) {
			for (int j = 0; j < tSize; j++) {
				// Static tiles (no ActionListeners) for the target guide
				RubikTile tile = new RubikTile(t[i][j].getColor());
				tile.setPreferredSize(new Dimension(35, 35));
				targetTray.add(tile);
			}
		}

		// Aligner keeps the tray from stretching
		JPanel aligner = new JPanel(new FlowLayout(FlowLayout.CENTER));
		aligner.setOpaque(false);
		aligner.add(targetTray);

		targetPanel.add(aligner, BorderLayout.CENTER);
		targetPanel.revalidate();
		targetPanel.repaint();
	}

}