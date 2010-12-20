package linewars.display.layers;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import linewars.gamestate.GameState;

/**
 * TODO add javadoc for class
 * 
 * @author Titus Klinge
 * @author Ryan Tew
 */
public interface ILayer
{
	/**
	 * Draws the layer's drawable objects to the graphics object provided.
	 * 
	 * @param g
	 *            The graphics object to be drawn to.
	 * @param gamestate
	 *            The current drawable gamestate of the game.
	 * @param visibleScreen
	 *            The amount of the map that is visible to the user.
	 * @param scale
	 *            The conversion factor from game units to screen units.
	 */
	public void draw(Graphics g, GameState gamestate, Rectangle2D visibleScreen, double scale);
}
