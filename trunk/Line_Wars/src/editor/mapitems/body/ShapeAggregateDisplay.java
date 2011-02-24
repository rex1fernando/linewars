package editor.mapitems.body;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import java.util.Scanner;

import javax.swing.JTextField;

import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.MapItemDefinition;
import linewars.gamestate.shapes.CircleConfiguration;
import linewars.gamestate.shapes.RectangleConfiguration;
import linewars.gamestate.shapes.ShapeAggregateConfiguration;
import linewars.gamestate.shapes.ShapeConfiguration;
import editor.mapitems.body.BodyEditor.Inputs;

public class ShapeAggregateDisplay implements ShapeDisplay {
	
	public interface CanvasDimensionsCallback {
		public double getWidth();
		public double getHeight();
	}
	
	private enum State { moving, rotating }
	
	private MapItemDefinition<?> definition;
	private JTextField scalingFactor;
	private CanvasDimensionsCallback dimCallback;
	
	private Position center;
	private double rotation;
	
	private double currentScale = 1;
	private State state;
	private Position moveStart;
	
	public ShapeAggregateDisplay(MapItemDefinition<?> mid, JTextField scale, Transformation trans, CanvasDimensionsCallback canvasDimensionsCallback)
	{
		this.definition = mid;
		this.scalingFactor = scale;
		this.center = trans.getPosition();
		this.rotation = trans.getRotation();
		dimCallback = canvasDimensionsCallback;
	}

	@Override
	public void drawInactive(Graphics2D g, Position canvasCenter) {
		drawShapes(g, canvasCenter, Color.gray);
	}

	@Override
	public void drawActive(Graphics2D g, Position canvasCenter,
			Position mousePosition, List<Inputs> inputs) {
		drawShapes(g, canvasCenter, Color.red);
		
		Position actualCenter = canvasCenter.add(center);
		Position rotationKnob = actualCenter.add(Position.getUnitVector(rotation).scale(60));
		
		g.setColor(Color.blue);
		g.setStroke(new BasicStroke(5));
		g.drawLine((int)actualCenter.getX(), (int)actualCenter.getY(), (int)rotationKnob.getX(), (int)rotationKnob.getY());
		
		drawDot(g, actualCenter, Color.red, 7);
		drawDot(g, rotationKnob, Color.green, 7);
		
		if((isOver(mousePosition, actualCenter) && state == null) || state == State.moving)
		{
			drawDot(g, actualCenter, Color.red, 10);
			if(inputs.contains(Inputs.leftMouse))
			{
				if(state == null)
				{
					state = State.moving;
					moveStart = mousePosition;
				}
				center = center.add(mousePosition.subtract(moveStart));
				moveStart = mousePosition;
			}
			else
				state = null;
		}
		if((isOver(mousePosition, rotationKnob) && state == null) || state == State.rotating)
		{
			drawDot(g, rotationKnob, Color.green, 10);
			if(inputs.contains(Inputs.leftMouse))
			{
				if(state == null)
					state = State.rotating;
				rotation = mousePosition.subtract(actualCenter).getAngle();
			}
			else
				state = null;
		}
		//TODO
	}
	
	private boolean isOver(Position pos, Position base)
	{
		return pos.distanceSquared(base) <= 25;
	}
	
	private void drawDot(Graphics2D g, Position pos, Color c, double radius)
	{
		g.setColor(c);
		g.fillOval((int)(pos.getX() - radius), (int)(pos.getY() - radius), (int)radius*2, (int)radius*2);
	}

	@Override
	public ShapeConfiguration generateConfiguration(double scalingFactor) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void drawShapes(Graphics2D g, Position canvasCenter, Color c)
	{
		Position actualCenter = canvasCenter.add(center);
		g.rotate(rotation, (int)actualCenter.getX(), (int)actualCenter.getY());
		
		updateScalingFactor(dimCallback.getWidth());
		ShapeConfiguration sc = definition.getBodyConfig();
		if(sc instanceof CircleConfiguration)
			drawCircle(g, canvasCenter, (CircleConfiguration) sc, c);
		else if(sc instanceof RectangleConfiguration)
			drawRect(g, canvasCenter, (RectangleConfiguration) sc, c);
		else if(sc instanceof ShapeAggregateConfiguration)
		{
			for(String name : ((ShapeAggregateConfiguration)sc).getDefinedShapeNames())
			{
				ShapeConfiguration subConfig = ((ShapeAggregateConfiguration)sc).getShapeConfigurationForName(name);
				if(subConfig instanceof CircleConfiguration)
					drawCircle(g, canvasCenter, (CircleConfiguration) subConfig, c);
				else if(subConfig instanceof RectangleConfiguration)
					drawRect(g, canvasCenter, (RectangleConfiguration) subConfig, c);
			}
		}
		
		g.rotate(-rotation, (int)actualCenter.getX(), (int)actualCenter.getY());
	}
	
	private void drawCircle(Graphics2D g, Position canvasCenter, CircleConfiguration cc, Color c)
	{
		g.setColor(new Color(c.getRed(), c.getGreen(),
				c.getBlue(), 128));
		Position upperLeft = canvasCenter.add(center)
				.add(cc.getPosition().getPosition().scale(currentScale))
				.subtract(new Position(cc.getRadius()*currentScale, cc.getRadius()*currentScale));
		Position lowerRight = new Position(cc.getRadius()*currentScale, cc.getRadius()*currentScale).scale(2);
		g.fillOval((int)upperLeft.getX(), (int)upperLeft.getY(),
					(int)lowerRight.getX(), (int)lowerRight.getY());
		
		g.setColor(c);
		g.setStroke(new BasicStroke(3));
		g.drawOval((int)upperLeft.getX(), (int)upperLeft.getY(),
				(int)lowerRight.getX(), (int)lowerRight.getY());
	}
	
	private void drawRect(Graphics2D g, Position canvasCenter, RectangleConfiguration rc, Color c)
	{
		Position upperLeft = canvasCenter.add(center)
				.add(rc.getPosition().getPosition().scale(currentScale))
				.subtract(rc.getWidth()*currentScale / 2, rc.getHeight()*currentScale / 2);
		Position rectCenter = upperLeft.add(rc.getWidth()*currentScale/2, rc.getHeight()*currentScale/2);
		g.rotate(rc.getPosition().getRotation(), (int)rectCenter.getX(), (int)rectCenter.getY());
		
		g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 128));
		g.fillRect((int)upperLeft.getX(), (int)upperLeft.getY(), 
				(int)(rc.getWidth()), (int)(rc.getHeight()));
		
		g.setColor(c);
		g.setStroke(new BasicStroke(3));
		g.drawRect((int)upperLeft.getX(), (int)upperLeft.getY(), 
				(int)(rc.getWidth()), (int)(rc.getHeight()));
		g.rotate(-rc.getPosition().getRotation(), (int)rectCenter.getX(), (int)rectCenter.getY());
	}
	
	private void updateScalingFactor(double canvasWidth)
	{
		Scanner s = new Scanner(scalingFactor.getText());
		if(s.hasNextDouble())
			currentScale = s.nextDouble()/canvasWidth;
	}

	@Override
	public Transformation getTransformation() {
		return new Transformation(center, rotation);
	}

}
