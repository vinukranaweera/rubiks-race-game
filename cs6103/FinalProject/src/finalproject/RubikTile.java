package finalproject;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JButton;

/**
 * A custom Swing component that renders the visual state of a Tile.
 */
class RubikTile extends JButton {
	private Color tileColor;
	private static final int ROUNDNESS = 15;
	private boolean isMatch = false;

	/**
	 * Updates the matching state to trigger visual feedback (glow effect) when a
	 * tile aligns with the target pattern.
	 */
	public void setMatch(boolean isMatch) {
		this.isMatch = isMatch;
		repaint(); // Ensure the "glow" is drawn immediately
	}

	public RubikTile(Color color) {
		this.tileColor = color;

		// Disable default styling for custom painting
		setOpaque(false);
		setContentAreaFilled(false);
		setBorderPainted(false);
		setFocusPainted(false);
	}

	/**
	 * Dynamically updates the color of the button during board shuffles or moves.
	 */
	public void setTileColor(Color color) {
		this.tileColor = color;
		this.repaint();
	}

	/**
	 * Overrides the paint cycle to draw the 3D-effect tile and matching indicators.
	 */
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Render empty space socket
		if (tileColor == null) {
			g2.setColor(new Color(70, 70, 70)); // Dark empty slot
			g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, ROUNDNESS, ROUNDNESS);
		}
		// Render the Active Colored Tile
		else {
			// Draw green border if matched
			if (isMatch) {
				g2.setColor(new Color(50, 255, 50));
				g2.setStroke(new BasicStroke(4));

				g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, ROUNDNESS, ROUNDNESS);
			}

			// Draw tile body and 3D shine effect
			g2.setColor(tileColor);
			g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, ROUNDNESS - 5, ROUNDNESS - 5);
			g2.setColor(new Color(255, 255, 255, 50));
			g2.drawRoundRect(4, 4, getWidth() - 8, getHeight() - 8, ROUNDNESS, ROUNDNESS);
		}

		g2.dispose(); // Free graphics resources
	}
}