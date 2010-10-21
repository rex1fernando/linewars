package linewars.display;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import linewars.gamestate.Lane;
import linewars.gamestate.Position;
import linewars.gamestate.mapItems.Node;
import linewars.gamestate.mapItems.Wave;

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

	/**
	 * Constructs a ColoredEdge.
	 * 
	 * @param l
	 *            The Lane that this ColoredEdge will color.
	 */
	public ColoredEdge(Lane l)
	{
		lane = l;
	}

	/**
	 * Draws this ColoredEdge according to the information in it's Lane.
	 * 
	 * @param g
	 *            The Graphics to draw to.
	 */
	public void draw(Graphics g)
	{
		ArrayList<Wave> frontlines = lane.getFrontLineWaves();
		ArrayList<Node> nodes = lane.getNodesList();
		Wave currentWave = frontlines.get(0);
		Node currentNode = nodes.get(0);
		double pos = 0.0;

		if(currentWave != null)
		{
			// draw portion that is "owned" by the first wave
			g.setColor(currentNode.getOwner().getPlayerColor());
			for(; pos < currentWave.getPosition(); pos += SEGMENT_STEP)
			{
				drawSegment(g, pos - SEGMENT_STEP, pos, pos + SEGMENT_STEP, pos
						+ 2 * SEGMENT_STEP);
			}
		}

		currentWave = frontlines.get(1);
		currentNode = nodes.get(1);

		g.setColor(Color.white);
		if(currentWave != null)
		{
			// draw the neutral area between the two waves
			for(; pos < currentWave.getPosition(); pos += SEGMENT_STEP)
			{
				drawSegment(g, pos - SEGMENT_STEP, pos, pos + SEGMENT_STEP, pos
						+ 2 * SEGMENT_STEP);
			}

			g.setColor(currentNode.getOwner().getPlayerColor());
		}

		// draw the portion that is "owned" by the second wave
		// if the second wave was null then this draws the neutral section
		// between the first wave and the second node
		for(; pos < 1 - SEGMENT_STEP; pos += SEGMENT_STEP)
		{
			drawSegment(g, pos - SEGMENT_STEP, pos, pos + SEGMENT_STEP, pos + 2
					* SEGMENT_STEP);
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
		Position beforePos = lane.getPosition(before);
		Position startPos = lane.getPosition(start);
		Position endPos = lane.getPosition(end);
		Position afterPos = lane.getPosition(after);

		// get the vectors that represents the line segments
		Position segBefore = beforePos.subtract(startPos);
		Position segment = startPos.subtract(endPos);
		Position segAfter = endPos.subtract(afterPos);

		// get the normalized vectors that are orthagonal to the lane
		// we will use these to get the bounding points on the segment
		Position normOrthStart = segBefore.orthagonal().add(segment.orthagonal()).normalize();
		Position normOrthEnd = segment.orthagonal().add(segAfter.orthagonal()).normalize();

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
