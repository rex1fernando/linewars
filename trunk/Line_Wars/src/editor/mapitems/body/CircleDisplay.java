package editor.mapitems.body;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

import editor.mapitems.body.BodyEditor.Inputs;

import linewars.gamestate.Position;
import linewars.gamestate.shapes.Circle;
import linewars.gamestate.shapes.CircleConfiguration;

public class CircleDisplay implements ShapeDisplay {
	
	private Position center = new Position(0, 0);
	private double radius = 100;
	private Position moveCircleStart = null;
	private boolean adjustingRadius = false;
	
	public CircleDisplay()
	{
	}
	
	public CircleDisplay(CircleConfiguration c)
	{
		this(c, new Position(0, 0));
	}
	
	public CircleDisplay(CircleConfiguration c, Position center)
	{
		this.center = center;
		radius = c.getRadius();
	}

	@Override
	public void drawInactive(Graphics2D g, Position canvasCenter) {
		drawCircle(g, canvasCenter, Color.gray);
		moveCircleStart = null;
		adjustingRadius = false;
	}

	@Override
	public void drawActive(Graphics2D g, Position canvasCenter, Point mousePosition, List<Inputs> inputs) {
		drawCircle(g, canvasCenter, Color.red);
		Position mousePos = new Position(mousePosition.x, mousePosition.y);
		if((isOnEdge(mousePos, canvasCenter) || adjustingRadius) && moveCircleStart == null)
		{
			if(!adjustingRadius)
				adjustingRadius = true;
			g.setStroke(new BasicStroke(5));
			Position upperLeft = canvasCenter.subtract(center).subtract(new Position(radius, radius));
			Position lowerRight = new Position(radius, radius).scale(2);
			g.drawOval((int)upperLeft.getX(), (int)upperLeft.getY(),
						(int)lowerRight.getX(), (int)lowerRight.getY());
			if(inputs.contains(Inputs.leftMouse))
				radius = Math.sqrt(mousePos.distanceSquared(canvasCenter.subtract(center)));
		}
		else if((mousePos.distanceSquared(canvasCenter.subtract(center)) < radius*radius 
				&& inputs.contains(Inputs.leftMouse)) || moveCircleStart != null) 
		{
			if(moveCircleStart == null)
				moveCircleStart = mousePos;
			else
			{
				center = center.add(moveCircleStart.subtract(mousePos));
				moveCircleStart = mousePos;
			}
		}
		
		if(!inputs.contains(Inputs.leftMouse))
			moveCircleStart = null;
		if(!isOnEdge(mousePos, canvasCenter) && adjustingRadius)
			adjustingRadius = false;
	}
	
	private boolean isOnEdge(Position mousePos, Position canvasCenter)
	{
		return (Math.abs(Math.sqrt(mousePos.distanceSquared(canvasCenter.subtract(center))) - radius) <= 5);
	}

	/**
	 * @param g
	 * @param canvasCenter
	 */
	protected void drawCircle(Graphics2D g, Position canvasCenter, Color c) {
		g.setColor(new Color(c.getRed(), c.getGreen(),
				c.getBlue(), 128));
		Position upperLeft = canvasCenter.subtract(center).subtract(new Position(radius, radius));
		Position lowerRight = new Position(radius, radius).scale(2);
		g.fillOval((int)upperLeft.getX(), (int)upperLeft.getY(),
					(int)lowerRight.getX(), (int)lowerRight.getY());
		
		g.setColor(c);
		g.setStroke(new BasicStroke(3));
		g.drawOval((int)upperLeft.getX(), (int)upperLeft.getY(),
				(int)lowerRight.getX(), (int)lowerRight.getY());
	}

}