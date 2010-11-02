package linewars.display;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import linewars.gamestate.Lane;
import linewars.gamestate.Node;
import linewars.gamestate.Position;
import linewars.gamestate.Wave;

/**
 * This class handles drawing a Lane between two Nodes. It will draw the Lane in
 * the color of the two Players on the Lane, marking their progress along it.
 * Any "neutral" area in the Lane is drawn white.
 * 
 * @author Ryan Tew
 * 
 */
public class ColoredEdge
{
	private static final double SEGMENT_STEP = 0.01;
	private Lane lane;
	private int numPlayers;

	/**
	 * Constructs a ColoredEdge.
	 * 
	 * @param l
	 *            The Lane that this ColoredEdge will color.
	 * @param numPlayers
	 *            The number of players in the game.
	 */
	public ColoredEdge(Lane l, int numPlayers)
	{
		lane = l;
		this.numPlayers = numPlayers;
	}

	/**
	 * Draws this ColoredEdge according to the information in it's Lane.
	 * 
	 * @param g
	 *            The Graphics to draw to.
	 */
	public void draw(Graphics g)
	{
		Wave[] waves = lane.getWaves();

		Color curColor;
		int curIndex;
		double pos = 0.0;
		
		// get the playerID and Color for the first wave
		int prevIndex = waves[0].getUnits()[0].getOwner().getPlayerID();
		for(Wave wave : waves)
		{
			//set the current color
			curColor = ImageDrawer.getInstance().getPlayerColor(prevIndex, numPlayers);
			
			// if this wave belongs to a different player than the previous one
			// set the current color to white
			curIndex = wave.getUnits()[0].getOwner().getPlayerID();
			if(curIndex != prevIndex)
			{
				prevIndex = curIndex;
				curColor = Color.white;
			}

			//set the graphics color
			g.setColor(curColor);
			
			//draw the edge segment between the previous wave and the current wave
			for(; pos < wave.getPosition(); pos += SEGMENT_STEP)
			{
				drawSegment(g, pos - SEGMENT_STEP, pos, pos + SEGMENT_STEP, pos + 2 * SEGMENT_STEP);
			}
		}
	}

	/**
	 * Draws a line segment from start to end that to approximate a bezier
	 * curve.
	 * 
	 * @param g
	 *            The Graphics object to draw the line segment to.
	 * @param start
	 *            The percentage along the bezier curve to start drawing (from
	 *            0.0 to 1.0).
	 * @param end
	 *            The percentage along the bezier curve to stop drawing (from
	 *            0.0 to 1.0).
	 */
	private void drawSegment(Graphics g, double before, double start,
			double end, double after)
	{
		// get the start and end positions
		Position beforePos = lane.getPosition(before).getPosition();
		Position startPos = lane.getPosition(start).getPosition();
		Position endPos = lane.getPosition(end).getPosition();
		Position afterPos = lane.getPosition(after).getPosition();

		// get the vectors that represents the line segments
		Position segBefore = beforePos.subtract(startPos);
		Position segment = startPos.subtract(endPos);
		Position segAfter = endPos.subtract(afterPos);

		// get the normalized vectors that are orthagonal to the lane
		// we will use these to get the bounding points on the segment
		Position normOrthStart = segBefore.orthogonal().add(segment.orthogonal()).normalize();
		Position normOrthEnd = segment.orthogonal().add(segAfter.orthogonal()).normalize();

		// generate the points that bound the segment to be drawn
		int[] x = {(int)(startPos.getX() + normOrthStart.getX() * lane.getWidth() / 2),
				(int)(startPos.getX() - normOrthStart.getX() * lane.getWidth() / 2),
				(int)(endPos.getX() - normOrthEnd.getX() * lane.getWidth() / 2),
				(int)(endPos.getX() + normOrthEnd.getX() * lane.getWidth() / 2)};
		int[] y = {(int)(startPos.getY() + normOrthStart.getY() * lane.getWidth() / 2),
				(int)(startPos.getY() - normOrthStart.getY() * lane.getWidth() / 2),
				(int)(endPos.getY() - normOrthEnd.getY() * lane.getWidth() / 2),
				(int)(endPos.getY() + normOrthEnd.getY() * lane.getWidth() / 2)};

		g.fillPolygon(x, y, 4);
	}
}
