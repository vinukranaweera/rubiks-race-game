package finalproject;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * Custom JPanel with rounded corners for the puzzle grid and tiles.
 */
class RoundedPanel extends JPanel {
	private int radius;
	private Color bgColor;

	public RoundedPanel(int radius, Color bgColor) {
		this.radius = radius;
		this.bgColor = bgColor;
		setOpaque(false); // Make transparent so rounded corners are visible
	}

	/**
	 * Overrides the standard paint mechanism to render custom geometry.
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g.create();

		// Smooth edges and fill rounded rectangle
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(bgColor);
		g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
		g2.dispose();
	}
}