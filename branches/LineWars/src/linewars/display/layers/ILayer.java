package linewars.display.layers;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import linewars.display.Display;
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
	 * @param display TODO
	 * @param gamestate
	 *            The current drawable gamestate of the game.
	 * @param visibleScreen TODO
	 * @param scale TODO
	 */
	public void draw(Graphics g, Display display, GameState gamestate, Rectangle2D visibleScreen, double scale);
}
