package finalproject;

/**
 * Controller managing game state and logic flow between Model and View.
 */
public class GameController {
	// Defines the lifecycle of a game session to prevent invalid actions (e.g.,
	// moving tiles when finished)
	public enum GameState {
		NOT_STARTED, ACTIVE, PAUSED, FINISHED
	}

	private Board board;
	private int moves;
	private GameState state;

	public GameController() {
		this.board = new Board(Difficulty.NORMAL); // Default difficulty is Normal
		this.moves = 0;
		this.state = GameState.NOT_STARTED;
	}

	/**
	 * Metrics tracking number of moves
	 */
	public void incrementMoves() {
		this.moves++;
	}

	public int getMoves() {
		return moves;
	}

	public Board getBoard() {
		return board;
	}

	public GameState getState() {
		return state;
	}

	public void setState(GameState newState) {
		this.state = newState;
	}

	public void start() {
		this.state = GameState.ACTIVE;
	}

	public void pause() {
		this.state = GameState.PAUSED;
	}

	public void resume() {
		this.state = GameState.ACTIVE;
	}

	/**
	 * Checks for win and updates state to lock interactions.
	 */
	public boolean isWin() {
		if (state == GameState.FINISHED)
			return false;
		if (board.checkWin()) {
			state = GameState.FINISHED;
			return true;
		}
		return false;
	}

	/**
	 * Resets moves and board while keeping current difficulty.
	 */
	public void reset() {
		this.board = new Board(board.getDifficulty());
		this.moves = 0;
		this.state = GameState.NOT_STARTED;
	}

	/**
	 * Updates difficulty and resets the session.
	 */
	public void changeDifficulty(Difficulty diff) {
		this.board = new Board(diff);
		this.moves = 0;
		this.state = GameState.NOT_STARTED;
	}
}