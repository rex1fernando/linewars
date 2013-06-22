package editor.mapitems.body;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.shapes.configurations.ShapeConfiguration;
import editor.mapitems.body.BodyEditor.Inputs;

public class AlignmentStickDisplay implements ShapeDisplay {
	
	private enum State { moving, rotating }
	
	private Position center;
	private double rotation;
	
	private State state;
	private Position moveStart;
	
	public AlignmentStickDisplay(Transformation t)
	{
		center = t.getPosition();
		rotation = t.getRotation();
	}

	@Override
	public void drawInactive(Graphics2D g, Position canvasCenter, Position canvasSize, double scale) {
		Position actualCenter = canvasCenter.add(center);
		drawDot(g, actualCenter, Color.gray, 7);
	}

	@Override
	public void drawActive(Graphics2D g, Position canvasCenter,
			Position mousePosition, List<Inputs> inputs, Position canvasSize, double scale) {
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
		throw new UnsupportedOperationException();
	}

	@Override
	public Transformation getTransformation() {
		return new Transformation(center, rotation);
	}

}
