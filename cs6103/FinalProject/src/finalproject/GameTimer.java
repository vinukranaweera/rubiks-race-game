package finalproject;

import javax.swing.SwingUtilities;

/**
 * Background thread for tracking gameplay time.
 */
public class GameTimer extends Thread {
	private int seconds = 0;
	private boolean running = true;
	private boolean paused = false;
	private TimerCallback callback;

	/**
	 * Allows the GameUI to listen for "ticks" without the timer needing to know the
	 * specifics of the display components.
	 */
	public interface TimerCallback {
		void onTick(int seconds);
	}

	public GameTimer(TimerCallback callback) {
		this.callback = callback;
		this.setDaemon(true); // Ensures thread closes on app exit
	}

	/**
	 * The main execution loop for the background thread.
	 */
	@Override
	public void run() {
		try {
			while (running) {
				// Pause execution for 1 second to create a standard clock tick
				Thread.sleep(1000);

				if (!paused) {
					seconds++;
					// Update UI on the Event Dispatch Thread
					SwingUtilities.invokeLater(() -> callback.onTick(seconds));
				}
			}
		} catch (InterruptedException e) {
			// Thread interrupted (normal shutdown)
		}
	}

	public int getTime() {
		return seconds;
	}

	/**
	 * Toggles the active state of the timer. Used for game pauses or when the board
	 * is being reset.
	 */
	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	/**
	 * Safely terminates the thread loop and wakes it from sleep to ensure immediate
	 * resource cleanup.
	 */
	public void stopTimer() {
		running = false;
		this.interrupt();
	}
}