package linewars.display;

import java.awt.Graphics;

import linewars.gamestate.Position;

public class CircleDrawer
{
	private static final int STEPS = 16;
	public static void drawCircle(Graphics g, Position p, double r)
	{
		int[] x = new int[STEPS];
		int[] y = new int[STEPS];
		for(int i = 0; i < STEPS; ++i)
		{
			double theta = ((double)i / STEPS) * 2 * Math.PI;
			Position push = new Position(Math.cos(theta), Math.sin(theta));
			Position point = p.add(push.scale(r));
			x[i] = (int)point.getX();
			y[i] = (int)point.getY();
		}
		
		g.fillPolygon(x, y, STEPS);
	}
}
