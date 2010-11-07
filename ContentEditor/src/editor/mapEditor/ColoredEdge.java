package editor.mapEditor;

import java.awt.Color;
import java.awt.Graphics;

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
	private MapPanel panel;

	/**
	 * Constructs a ColoredEdge.
	 * 
	 * @param l
	 *            The Lane that this ColoredEdge will color.
	 * @param numPlayers
	 *            The number of players in the game.
	 */
	public ColoredEdge(MapPanel panel)
	{
		this.panel = panel;
	}

	/**
	 * Draws this ColoredEdge according to the information in it's Lane.
	 * 
	 * @param g
	 *            The Graphics to draw to.
	 */
	public void draw(Graphics g, Lane lane, double scale)
	{
		Node[] nodes = lane.getNodes();

		Position laneStart = lane.getPosition(0).getPosition();		
		Position laneEnd = lane.getPosition(1.0).getPosition();				

		g.setColor(Color.blue);
		
		//initialize the draw positions
		Position beforePos = nodes[0].getPosition().getPosition();
		Position startPos = laneStart;
		Position endPos = lane.getPosition(SEGMENT_STEP).getPosition();
		Position afterPos = lane.getPosition(2 * SEGMENT_STEP).getPosition();

		//draw the first segment it needs to start at the node
		drawSegment(g, lane, beforePos, beforePos, laneStart, endPos, scale);
		
		//draw the first segment in the lane
		drawSegment(g, lane, beforePos, laneStart, endPos, afterPos, scale);
		
		//draw lane
		for(double pos = 3 * SEGMENT_STEP; pos < 1; pos += SEGMENT_STEP)
		{
			//increment the positions
			beforePos = startPos;
			startPos = endPos;
			endPos = afterPos;
			afterPos = lane.getPosition(pos).getPosition();
			
			drawSegment(g, lane, beforePos, startPos, endPos, afterPos, scale);			
		}
		
		//increment the positions
		beforePos = startPos;
		startPos = endPos;
		endPos = afterPos;
		afterPos = nodes[1].getPosition().getPosition();

		//draw the second to last segment
		drawSegment(g, lane, beforePos, startPos, endPos, laneEnd, scale);

		//draw the last segment in the lane
		drawSegment(g, lane, startPos, endPos, laneEnd, afterPos, scale);
		
		//draw the last segment it needs to end at the node
		drawSegment(g, lane, endPos, laneEnd, afterPos, afterPos, scale);
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
	private void drawSegment(Graphics g, Lane lane, Position before, Position start,
			Position end, Position after, double scale)
	{
		// get the vectors that represents the line segments
		Position segBefore = before.subtract(start);
		Position segment = start.subtract(end);
		Position segAfter = end.subtract(after);

		// get the normalized vectors that are orthagonal to the lane
		// we will use these to get the bounding points on the segment
		Position normOrthStart = segBefore.orthogonal().add(segment.orthogonal()).normalize();
		Position normOrthEnd = segment.orthogonal().add(segAfter.orthogonal()).normalize();

		// generate the points that bound the segment to be drawn
		Position p1 = panel.toScreenCoord(new Position(start.getX() + normOrthStart.getX() * lane.getWidth() / 2,
														start.getY() + normOrthStart.getY() * lane.getWidth() / 2));
		Position p2 = panel.toScreenCoord(new Position(start.getX() - normOrthStart.getX() * lane.getWidth() / 2,
														start.getY() - normOrthStart.getY() * lane.getWidth() / 2));
		Position p3 = panel.toScreenCoord(new Position(end.getX() - normOrthEnd.getX() * lane.getWidth() / 2,
														end.getY() - normOrthEnd.getY() * lane.getWidth() / 2));
		Position p4 = panel.toScreenCoord(new Position(end.getX() + normOrthEnd.getX() * lane.getWidth() / 2,
														end.getY() + normOrthEnd.getY() * lane.getWidth() / 2));
		
		int[] x = {(int)p1.getX(), (int)p2.getX(), (int)p3.getX(), (int)p4.getX()};
		int[] y = {(int)p1.getY(), (int)p2.getY(), (int)p3.getY(), (int)p4.getY()};

		g.fillPolygon(x, y, 4);
	}
}
