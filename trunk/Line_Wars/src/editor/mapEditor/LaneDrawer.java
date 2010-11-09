package editor.mapEditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import linewars.gamestate.BezierCurve;
import linewars.gamestate.Lane;
import linewars.gamestate.Node;
import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.shapes.Circle;

/**
 * This class handles drawing a Lane between two Nodes. It will draw the Lane in
 * the color of the two Players on the Lane, marking their progress along it.
 * Any "neutral" area in the Lane is drawn white.
 * 
 * @author Ryan Tew
 * 
 */
public class LaneDrawer
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
	public LaneDrawer(MapPanel panel)
	{
		this.panel = panel;
	}

	/**
	 * Draws this ColoredEdge according to the information in it's Lane.
	 * 
	 * @param g
	 *            The Graphics to draw to.
	 */
	public void draw(Graphics g, Lane lane, Position mouse, double scale)
	{
		Node[] nodes = lane.getNodes();

		Position laneStart = lane.getPosition(0).getPosition();		
		Position laneEnd = lane.getPosition(1.0).getPosition();				

		g.setColor(new Color(255, 0, 0, 80));
		
		//initialize the draw positions
		Position beforePos = nodes[0].getTransformation().getPosition();
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
		afterPos = nodes[1].getTransformation().getPosition();

		//draw the second to last segment
		drawSegment(g, lane, beforePos, startPos, endPos, laneEnd, scale);

		//draw the last segment in the lane
		drawSegment(g, lane, startPos, endPos, laneEnd, afterPos, scale);
		
		//draw the last segment it needs to end at the node
		drawSegment(g, lane, endPos, laneEnd, afterPos, afterPos, scale);
		
		//get the control points
		BezierCurve curve = lane.getCurve();
		Position gameP1 = curve.getP1();
		Position gameP2 = curve.getP2();
		Position screenP0 = panel.toScreenCoord(curve.getP0());
		Position screenP1 = panel.toScreenCoord(gameP1);
		Position screenP2 = panel.toScreenCoord(gameP2);
		Position screenP3 = panel.toScreenCoord(curve.getP3());
		Circle circP1 = new Circle(new Transformation(gameP1, 0), 5 / scale);
		Circle circP2 = new Circle(new Transformation(gameP2, 0), 5 / scale);
		
		//draw lines connecting p0 to p1 and p2 to p3
		g.setColor(Color.orange);
		((Graphics2D)g).setStroke(new BasicStroke(5));
		g.drawLine((int)screenP0.getX(), (int)screenP0.getY(), (int)screenP1.getX(), (int)screenP1.getY());
		g.drawLine((int)screenP2.getX(), (int)screenP2.getY(), (int)screenP3.getX(), (int)screenP3.getY());
		
		//set the color for the control points
		g.setColor(Color.pink);
		
		//draw control point 1
		if(circP1.positionIsInShape(mouse))
			g.fillOval((int)screenP1.getX() - 10, (int)screenP1.getY() - 10, 20, 20);
		else
			g.fillOval((int)screenP1.getX() - 5, (int)screenP1.getY() - 5, 10, 10);

		//draw control point 2
		if(circP2.positionIsInShape(mouse))
			g.fillOval((int)screenP2.getX() - 10, (int)screenP2.getY() - 10, 20, 20);
		else
			g.fillOval((int)screenP2.getX() - 5, (int)screenP2.getY() - 5, 10, 10);
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
