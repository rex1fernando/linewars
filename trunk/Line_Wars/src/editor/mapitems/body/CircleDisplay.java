package editor.mapitems.body;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.shapes.configurations.CircleConfiguration;
import linewars.gamestate.shapes.configurations.ShapeConfiguration;
import editor.mapitems.body.BodyEditor.Inputs;

public class CircleDisplay implements ShapeDisplay {
	
	private Position center = new Position(0, 0);
	private double radius = 100;
	private Position moveCircleStart = null;
	private boolean adjustingRadius = false;
	
	public CircleDisplay()
	{
	}
	
	public CircleDisplay(CircleConfiguration c, double scale)
	{
		this.center = c.getPosition().getPosition().scale(1/scale);
		radius = c.getRadius()/scale;
	}

	@Override
	public void drawInactive(Graphics2D g, Position canvasCenter, Position canvasSize, double scale) {
		drawCircle(g, canvasCenter, Color.gray);
		moveCircleStart = null;
		adjustingRadius = false;
	}

	@Override
	public void drawActive(Graphics2D g, Position canvasCenter, Position mousePosition, List<Inputs> inputs, Position canvasSize, double scale) {
		drawCircle(g, canvasCenter, Color.red);
		if((isOnEdge(mousePosition, canvasCenter) || adjustingRadius) && moveCircleStart == null)
		{
			if(!adjustingRadius)
				adjustingRadius = true;
			g.setStroke(new BasicStroke(5));
			Position upperLeft = canvasCenter.add(center).subtract(new Position(radius, radius));
			Position lowerRight = new Position(radius, radius).scale(2);
			g.drawOval((int)upperLeft.getX(), (int)upperLeft.getY(),
						(int)lowerRight.getX(), (int)lowerRight.getY());
			if(inputs.contains(Inputs.leftMouse))
				radius = Math.sqrt(mousePosition.distanceSquared(canvasCenter.add(center)));
		}
		else if((mousePosition.distanceSquared(canvasCenter.add(center)) < radius*radius 
				&& inputs.contains(Inputs.leftMouse)) || moveCircleStart != null) 
		{
			if(moveCircleStart == null)
				moveCircleStart = mousePosition;
			else
			{
				center = center.subtract(moveCircleStart.subtract(mousePosition));
				moveCircleStart = mousePosition;
			}
		}
		
		if(!inputs.contains(Inputs.leftMouse))
			moveCircleStart = null;
		if(!isOnEdge(mousePosition, canvasCenter) && adjustingRadius)
			adjustingRadius = false;
	}
	
	private boolean isOnEdge(Position mousePos, Position canvasCenter)
	{
		return (Math.abs(Math.sqrt(mousePos.distanceSquared(canvasCenter.add(center))) - radius) <= 5);
	}

	/**
	 * @param g
	 * @param canvasCenter
	 */
	protected void drawCircle(Graphics2D g, Position canvasCenter, Color c) {
		g.setColor(new Color(c.getRed(), c.getGreen(),
				c.getBlue(), 128));
		Position upperLeft = canvasCenter.add(center).subtract(new Position(radius, radius));
		Position lowerRight = new Position(radius, radius).scale(2);
		g.fillOval((int)upperLeft.getX(), (int)upperLeft.getY(),
					(int)lowerRight.getX(), (int)lowerRight.getY());
		
		g.setColor(c);
		g.setStroke(new BasicStroke(3));
		g.drawOval((int)upperLeft.getX(), (int)upperLeft.getY(),
				(int)lowerRight.getX(), (int)lowerRight.getY());
	}

	@Override
	public ShapeConfiguration generateConfiguration(double scalingFactor) {
		return new CircleConfiguration(scalingFactor*radius, new Transformation(center.scale(scalingFactor), 0));
	}

	@Override
	public Transformation getTransformation() {
		return new Transformation(center, 0);
	}

}
