package linewars.display.layers;

import java.awt.Graphics;
import java.awt.Rectangle;

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
	 * @param visibleScreen TODO
	 */
	public void draw(Graphics g, GameState gamestate, Rectangle visibleScreen);
}
