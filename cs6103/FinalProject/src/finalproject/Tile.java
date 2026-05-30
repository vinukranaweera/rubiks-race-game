package finalproject;

import java.awt.Color;

/**
 * Model representing a single puzzle tile and its color.
 */
public class Tile {
    private Color color;

    public static final Color RUBIK_WHITE = Color.WHITE;
    public static final Color RUBIK_YELLOW = new Color(255, 215, 0); // Golden Yellow
    public static final Color RUBIK_RED = new Color(220, 20, 60);    // Crimson
    public static final Color RUBIK_ORANGE = new Color(255, 140, 0); // Dark Orange
    public static final Color RUBIK_BLUE = new Color(30, 144, 255);  // Dodger Blue
    public static final Color RUBIK_GREEN = new Color(50, 205, 50);  // Lime Green

    public Tile(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}