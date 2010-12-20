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
 * This class handles drawing a Lane between two Nodes.
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
	 * @param panel
	 *            The map panel this will draw to.
	 */
	public LaneDrawer(MapPanel panel)
	{
		this.panel = panel;
	}
	
	/**
	 * Draws the lanes for a map image.
	 * @param g The Graphics object for the image.
	 * @param lane The lane to be drawn.
	 */
	public void createMap(Graphics g, Lane lane)
	{
		Node[] nodes = lane.getNodes();

		Position laneStart = lane.getPosition(0).getPosition();
		Position laneEnd = lane.getPosition(1.0).getPosition();

		g.setColor(Color.white);

		// initialize the draw positions
		Position beforePos = nodes[0].getTransformation().getPosition();
		Position startPos = laneStart;
		Position endPos = lane.getPosition(SEGMENT_STEP).getPosition();
		Position afterPos = lane.getPosition(2 * SEGMENT_STEP).getPosition();

		// draw the first segment it needs to start at the node
		drawSegment(g, lane, beforePos, beforePos, laneStart, endPos, true);

		// draw the first segment in the lane
		drawSegment(g, lane, beforePos, laneStart, endPos, afterPos, true);

		// draw lane
		for(double pos = 3 * SEGMENT_STEP; pos < 1; pos += SEGMENT_STEP)
		{
			// increment the positions
			beforePos = startPos;
			startPos = endPos;
			endPos = afterPos;
			afterPos = lane.getPosition(pos).getPosition();

			drawSegment(g, lane, beforePos, startPos, endPos, afterPos, true);
		}

		// increment the positions
		beforePos = startPos;
		startPos = endPos;
		endPos = afterPos;
		afterPos = nodes[1].getTransformation().getPosition();

		// draw the second to last segment
		drawSegment(g, lane, beforePos, startPos, endPos, laneEnd, true);

		// draw the last segment in the lane
		drawSegment(g, lane, startPos, endPos, laneEnd, afterPos, true);

		// draw the last segment it needs to end at the node
		drawSegment(g, lane, endPos, laneEnd, afterPos, afterPos, true);
	}

	/**
	 * Draws the given lane and its corresponding control points.
	 * 
	 * @param g
	 *            The Graphics to draw to.
	 * @param lane
	 *            The Lane to draw.
	 * @param selected
	 *            Is this lane selected?
	 * @param mouse
	 *            The position of the mouse in game coordinates.
	 * @param scale
	 *            The conversion factor from map size to screen size.
	 */
	public void draw(Graphics g, Lane lane, boolean selected, Position mouse, double scale)
	{
		Node[] nodes = lane.getNodes();

		Position laneStart = lane.getPosition(0).getPosition();
		Position laneEnd = lane.getPosition(1.0).getPosition();

		g.setColor(new Color(255, 0, 0, selected ? 90 : 60));

		// initialize the draw positions
		Position beforePos = nodes[0].getTransformation().getPosition();
		Position startPos = laneStart;
		Position endPos = lane.getPosition(SEGMENT_STEP).getPosition();
		Position afterPos = lane.getPosition(2 * SEGMENT_STEP).getPosition();

		// draw the first segment it needs to start at the node
		drawSegment(g, lane, beforePos, beforePos, laneStart, endPos, false);

		// draw the first segment in the lane
		drawSegment(g, lane, beforePos, laneStart, endPos, afterPos, false);

		// draw lane
		for(double pos = 3 * SEGMENT_STEP; pos < 1; pos += SEGMENT_STEP)
		{
			// increment the positions
			beforePos = startPos;
			startPos = endPos;
			endPos = afterPos;
			afterPos = lane.getPosition(pos).getPosition();

			drawSegment(g, lane, beforePos, startPos, endPos, afterPos, false);
		}

		// increment the positions
		beforePos = startPos;
		startPos = endPos;
		endPos = afterPos;
		afterPos = nodes[1].getTransformation().getPosition();

		// draw the second to last segment
		drawSegment(g, lane, beforePos, startPos, endPos, laneEnd, false);

		// draw the last segment in the lane
		drawSegment(g, lane, startPos, endPos, laneEnd, afterPos, false);

		// draw the last segment it needs to end at the node
		drawSegment(g, lane, endPos, laneEnd, afterPos, afterPos, false);

		// get the control points
		BezierCurve curve = lane.getCurve();
		Position gameP1 = curve.getP1();
		Position gameP2 = curve.getP2();
		Position screenP0 = panel.toScreenCoord(curve.getP0());
		Position screenP1 = panel.toScreenCoord(gameP1);
		Position screenP2 = panel.toScreenCoord(gameP2);
		Position screenP3 = panel.toScreenCoord(curve.getP3());
		Circle circP1 = new Circle(new Transformation(gameP1, 0), 5 / scale);
		Circle circP2 = new Circle(new Transformation(gameP2, 0), 5 / scale);

		// draw lines connecting p0 to p1 and p2 to p3
		g.setColor(Color.yellow);
		((Graphics2D)g).setStroke(new BasicStroke(5));
		g.drawLine((int)screenP0.getX(), (int)screenP0.getY(), (int)screenP1.getX(), (int)screenP1.getY());
		g.drawLine((int)screenP2.getX(), (int)screenP2.getY(), (int)screenP3.getX(), (int)screenP3.getY());

		// set the color for the control points
		g.setColor(Color.pink);

		// draw control point 1
		if(circP1.positionIsInShape(mouse))
			g.fillOval((int)screenP1.getX() - 10, (int)screenP1.getY() - 10, 20, 20);
		else
			g.fillOval((int)screenP1.getX() - 5, (int)screenP1.getY() - 5, 10, 10);

		// draw control point 2
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
	 *            The Graphics object to draw the line segment to.\
	 * @param lane
	 *            The lane to draw.
	 * @param before
	 *            A position that is close to and comes before start.
	 * @param start
	 *            The position along the bezier curve to start drawing.
	 * @param end
	 *            The position along the bezier curve to stop drawing.
	 * @param after
	 *            A position that is close to and comes after end.
	 * @param drawMap TODO
	 */
	private void drawSegment(Graphics g, Lane lane, Position before, Position start,
			Position end, Position after, boolean drawMap)
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
		Position p1 = new Position(start.getX() + normOrthStart.getX() * lane.getWidth() / 2,
												start.getY() + normOrthStart.getY() * lane.getWidth() / 2);
		Position p2 = new Position(start.getX() - normOrthStart.getX() * lane.getWidth() / 2,
												start.getY() - normOrthStart.getY() * lane.getWidth() / 2);
		Position p3 = new Position(end.getX() - normOrthEnd.getX() * lane.getWidth() / 2,
												end.getY() - normOrthEnd.getY() * lane.getWidth() / 2);
		Position p4 = new Position(end.getX() + normOrthEnd.getX() * lane.getWidth() / 2,
												end.getY() + normOrthEnd.getY() * lane.getWidth() / 2);
		
		//if we are drawing to the screen we want to convert these positions to screen coordinates
		if(!drawMap)
		{
			p1 = panel.toScreenCoord(p1);
			p2 = panel.toScreenCoord(p2);
			p3 = panel.toScreenCoord(p3);
			p4 = panel.toScreenCoord(p4);
		}

		int[] x = {(int)p1.getX(), (int)p2.getX(), (int)p3.getX(), (int)p4.getX()};
		int[] y = {(int)p1.getY(), (int)p2.getY(), (int)p3.getY(), (int)p4.getY()};

		g.fillPolygon(x, y, 4);
	}
}
