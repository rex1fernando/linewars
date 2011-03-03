package editor.mapEditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import linewars.gamestate.BuildingSpot;
import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.shapes.Circle;
import linewars.gamestate.shapes.Rectangle;

/**
 * Handles drawing a building spot.
 * 
 * @author Ryan Tew
 * 
 */
public class BuildingDrawer
{
	private MapPanel panel;

	/**
	 * Constructs this BuildingDrawer.
	 * 
	 * @param panel
	 *            The map panel this will be drawing to
	 */
	public BuildingDrawer(MapPanel panel)
	{
		this.panel = panel;
	}

	/**
	 * Draws the building spots for a map image.
	 * 
	 * @param g
	 *            The Graphics object for the image.
	 * @param b
	 *            The building spot to draw.
	 * @param commandCenter
	 *            Is this building spot a command center?
	 */
	public void createMap(Graphics g, BuildingSpot b, boolean commandCenter)
	{
		Position[] corners = b.getRect().getVertexPositions();

		int[] x = new int[] {(int)corners[0].getX(), (int)corners[1].getX(), (int)corners[2].getX(),
				(int)corners[3].getX()};
		int[] y = new int[] {(int)corners[0].getY(), (int)corners[1].getY(), (int)corners[2].getY(),
				(int)corners[3].getY()};

		// set the transparent color
		if(commandCenter)
			g.setColor(Color.yellow);
		else
			g.setColor(Color.blue);

		// fill the rectangle
		g.fillPolygon(x, y, 4);
	}

	/**
	 * Draws a building spot in blue.
	 * 
	 * @param g
	 *            The graphics object to draw on.
	 * @param buildingSpot
	 *            The building spot to draw.
	 * @param selected
	 *            Is this building spot selected?
	 * @param mouse
	 *            The position of the mouse in game coordinates.
	 * @param scale
	 *            The conversion factor from map size to screen size.
	 */
	public void draw(Graphics g, BuildingSpot buildingSpot, boolean selected, Position mouse, double scale)
	{
		draw(g, buildingSpot, selected, mouse, scale, false);
	}

	/**
	 * Draws a building spot. Blue if this building spot is a building. Orange
	 * if this building spot is a command center.
	 * 
	 * @param g
	 *            The graphics object to draw on.
	 * @param buildingSpot
	 *            The building spot to draw.
	 * @param selected
	 *            Is this building spot selected?
	 * @param mouse
	 *            The position of the mouse in game coordinates.
	 * @param scale
	 *            The conversion factor from map size to screen size.
	 * @param commandCenter
	 *            Is this building spot a command center?
	 */
	public void draw(Graphics g, BuildingSpot buildingSpot, boolean selected, Position mouse, double scale,
			boolean commandCenter)
	{
		Position[] corners = buildingSpot.getRect().getVertexPositions();
		for(int i = 0; i < corners.length; ++i)
			corners[i] = panel.toScreenCoord(corners[i]);

		int[] x = new int[] {(int)corners[0].getX(), (int)corners[1].getX(), (int)corners[2].getX(),
				(int)corners[3].getX()};
		int[] y = new int[] {(int)corners[0].getY(), (int)corners[1].getY(), (int)corners[2].getY(),
				(int)corners[3].getY()};

		// set the transparent color
		if(commandCenter)
			g.setColor(new Color(255, 140, 0, selected ? 90 : 30));
		else
			g.setColor(new Color(0, 0, 255, selected ? 90 : 30));

		// fill the rectangle
		g.fillPolygon(x, y, 4);

		// set the border color
		if(commandCenter)
			g.setColor(new Color(255, 140, 0));
		else
			g.setColor(new Color(0, 0, 255));

		// set the brush size
		Transformation trans = buildingSpot.getTrans();
		Dimension dim = buildingSpot.getDim();
		double w = dim.getWidth();
		double h = dim.getHeight();
		Rectangle outer = new Rectangle(trans, w + 2.5 / scale, h + 2.5 / scale);
		Rectangle inner = new Rectangle(trans, w - 2.5 / scale, h - 2.5 / scale);
		if(outer.positionIsInShape(mouse) && !inner.positionIsInShape(mouse))
			((Graphics2D)g).setStroke(new BasicStroke(10));
		else
			((Graphics2D)g).setStroke(new BasicStroke(5));

		// draw the border
		g.drawPolygon(x, y, 4);

		// set the color for the rotation dot
		g.setColor(Color.pink);

		// set the dot position
		Position dotPos = buildingSpot.getRect().getVertexPositions()[0];// new
																			// Position(gamePos.getX()
																			// +
																			// w
																			// /
																			// 2,
																			// gamePos.getY()
																			// +
																			// h
																			// /
																			// 2);
		Position drawDot = panel.toScreenCoord(dotPos);

		// draw the dot
		Circle dot = new Circle(new Transformation(dotPos, 0), 5 / scale);
		if(dot.positionIsInShape(mouse))
			g.fillOval((int)drawDot.getX() - 10, (int)drawDot.getY() - 10, 20, 20);
		else
			g.fillOval((int)drawDot.getX() - 5, (int)drawDot.getY() - 5, 10, 10);
	}
}
