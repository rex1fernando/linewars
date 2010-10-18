package linewars.display;

import java.awt.Color;
import java.awt.Graphics;

import linewars.gamestate.Lane;
import linewars.gamestate.Position;

public class ColoredEdge
{
	private static final double SEGMENT_STEP = 0.1;
	private Lane lane;
	private Color color;

	public ColoredEdge(Lane l, Color c)
	{
		lane = l;
		color = c;
	}

	public void draw(Graphics g)
	{
		g.setColor(color);
		
		for(double i = 0.0; i < 1; i += SEGMENT_STEP)
		{
			drawSegment(g, i, i + SEGMENT_STEP);
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
	private void drawSegment(Graphics g, double start, double end)
	{
		//get the start and end positions
		Position startPos = getPosition(start);
		Position endPos = getPosition(end);
		
		//get the vector that represents the line segment
		Position segment = startPos.subtract(endPos);
		
		//get the normalized vector that is orthagonal to the segment
		//we will use this to get the bounding points on the segment
		Position normalizedOrthagonal = new Position(segment.getY(), -segment.getX()).normalize();

		//generate the points that bound the segment to be drawn
		Position first = new Position(startPos.getX() + normalizedOrthagonal.getX() * lane.getWidth() / 2, startPos.getY() + normalizedOrthagonal.getY() * lane.getWidth() / 2);
		int[] x = {(int)first.getX(),
				(int)(startPos.getX() - normalizedOrthagonal.getX() * lane.getWidth() / 2),
				(int)(endPos.getX() - normalizedOrthagonal.getX() * lane.getWidth() / 2),
				(int)(endPos.getX() + normalizedOrthagonal.getX() * lane.getWidth() / 2)};
		int[] y = {(int)first.getY(),
				(int)(startPos.getY() - normalizedOrthagonal.getY() * lane.getWidth() / 2),
				(int)(endPos.getY() - normalizedOrthagonal.getY() * lane.getWidth() / 2),
				(int)(endPos.getY() + normalizedOrthagonal.getY() * lane.getWidth() / 2)};
		
		g.fillPolygon(x, y, 4);
	}

	/**
	 * Gets the position along the bezier curve represented by the percentage
	 * pos. This follows the equation found at
	 * 		<a href="http://en.wikipedia.org/wiki/Bezier_curve#Cubic_B.C3.A9zier_curves">http://en.wikipedia.org/wiki/Bezier_curve</a>
	 * B(t)= (1-t)^3 * P0 + 3(1-t)^2 * t * P1 + 3(1-t) * t^2 * P 2 + t^3 * P3 where t = [0,1].
	 * 
	 * @param pos
	 *            The percentage along the bezier curve to get a position.
	 * 
	 * @return The position along the bezier curve represented by the percentage
	 *         pos.
	 */
	private Position getPosition(double pos)
	{
		double term0 = Math.pow((1 - pos), 3);
		double term1 = 3 * Math.pow(1 - pos, 2) * pos;
		double term2 = 3 * (1 - pos) * Math.pow(pos, 2);
		double term3 = Math.pow(pos, 3);

		double posX = term0 * lane.getP0().getX() + term1 * lane.getP1().getX()
				+ term2 * lane.getP2().getX() + term3 * lane.getP3().getX();
		double posY = term0 * lane.getP0().getY() + term1 * lane.getP1().getY()
				+ term2 * lane.getP2().getY() + term3 * lane.getP3().getY();

		return new Position(posX, posY);
	}
}
