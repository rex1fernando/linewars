package editor.mapitems.body;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import linewars.gamestate.Position;

public class Rectangle implements ShapeDisplay {
	
	private double width = 100;
	private double height = 100;
	private Position upperLeft = new Position(0, 0);
	
	private boolean movingLeftSide = false;
	private boolean movingRightSide = false;
	private boolean movingUpperSide = false;
	private boolean movingLowerSide = false;

	@Override
	public void drawInactive(Graphics2D g, Position canvasCenter) {
		drawRect(g, canvasCenter, Color.gray);
	}

	@Override
	public void drawActive(Graphics2D g, Position canvasCenter,
			Point mousePosition, boolean mouseState) {
		drawRect(g, canvasCenter, Color.red);
		
		//TODO start here
	}
	
	private boolean isIn(Position pos, Position upperLeft, Position lowerRight)
	{
		return (pos.getX() <= lowerRight.getX()) && (pos.getY() <= lowerRight.getY())
				&& (pos.getX() >= upperLeft.getX()) && (pos.getY() >= upperLeft.getY());
	}
	
	private void drawRect(Graphics2D g, Position canvasCenter, Color c)
	{
		Position upperLeft = canvasCenter.subtract(this.upperLeft);
		g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 128));
		g.fillRect((int)upperLeft.getX(), (int)upperLeft.getY(), 
				(int)(width), (int)(height));
		
		g.setColor(c);
		g.setStroke(new BasicStroke(3));
		g.drawRect((int)upperLeft.getX(), (int)upperLeft.getY(), 
				(int)(width), (int)(height));
	}

}
