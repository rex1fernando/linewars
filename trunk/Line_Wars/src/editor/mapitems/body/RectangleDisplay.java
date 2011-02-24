package editor.mapitems.body;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.shapes.RectangleConfiguration;
import linewars.gamestate.shapes.ShapeConfiguration;
import editor.mapitems.body.BodyEditor.Inputs;

public class RectangleDisplay implements ShapeDisplay {
	
	private enum RectangleState {
		rotating, moving, changingRight, changingUp, changingLeft, changingDown
	}
	
	private double width = 100;
	private double height = 100;
	private Position center = new Position(0, 0);
	private double rotation = 0;
	
	private RectangleState state = null;
	
	private double initialAngle;
	private Position startPosition;
	
	public RectangleDisplay()
	{
	}
	
	public RectangleDisplay(RectangleConfiguration rc)
	{
		this(rc, new Position(0, 0));
	}
	
	public RectangleDisplay(RectangleConfiguration rc, Position center)
	{
		width = rc.getWidth();
		height = rc.getHeight();
		this.center = center;
	}

	@Override
	public void drawInactive(Graphics2D g, Position canvasCenter) {
		drawRect(g, canvasCenter, Color.gray);
	}

	@Override
	public void drawActive(Graphics2D g, Position canvasCenter,
			Position mousePosition, List<Inputs> inputs) {
		drawRect(g, canvasCenter, Color.red);
		
		Position upperLeft = canvasCenter.add(center).subtract(width/2, height/2);
		Position mousePos = mousePosition.rotateAboutPosition(upperLeft.add(width/2, height/2), -rotation);
		
		//check for rotating
		boolean isOverCorner = isOver(g, RectangleState.rotating, canvasCenter, mousePos); 
		if((isOverCorner && state == null) || state == RectangleState.rotating)
		{
			if(inputs.contains(Inputs.leftMouse)) //if the mouse is down
			{
				if(state != RectangleState.rotating)
				{
					state = RectangleState.rotating;
					initialAngle = mousePos.subtract(upperLeft.add(width/2, height/2)).getAngle();
				}
				rotation += mousePos.subtract(upperLeft.add(width/2, height/2)).getAngle() - initialAngle;
			}
			else
				state = null;
		}
		
		boolean isOverSide = false;
		if(((isOverSide = isOver(g, RectangleState.changingRight, canvasCenter, mousePos)) && state == null) || state == RectangleState.changingRight)
		{
			if(inputs.contains(Inputs.leftMouse))
			{
				if(state != RectangleState.changingRight)
					state = RectangleState.changingRight;
				if(mousePos.getX() - upperLeft.getX() > 0)
				{
					double oldWidth = width;
					width = mousePos.getX() - upperLeft.getX();
					if(!inputs.contains(Inputs.shift))
						center = center.add(Position.getUnitVector(rotation).scale(0.5*(width - oldWidth)));
				}
			}
			else
				state = null;
		}
		if(((isOverSide = isOver(g, RectangleState.changingUp, canvasCenter, mousePos)) && state == null) || state == RectangleState.changingUp)
		{
			if(inputs.contains(Inputs.leftMouse))
			{
				if(state != RectangleState.changingUp)
					state = RectangleState.changingUp;
				if(upperLeft.getY() + height - mousePos.getY() > 0)
				{
					double oldHeight = height;
					height = upperLeft.getY() + height - mousePos.getY();
					if(!inputs.contains(Inputs.shift))
						center = center.subtract(Position.getUnitVector(rotation + Math.PI/2).scale(0.5*(height - oldHeight)));
				}
			}
			else
				state = null;
		}
		if(((isOverSide = isOver(g, RectangleState.changingLeft, canvasCenter, mousePos)) && state == null) || state == RectangleState.changingLeft)
		{
			if(inputs.contains(Inputs.leftMouse))
			{
				if(state != RectangleState.changingLeft)
					state = RectangleState.changingLeft;
				if(upperLeft.getX() + width - mousePos.getX() > 0)
				{
					double oldWidth = width;
					width = upperLeft.getX() + width - mousePos.getX();
					if(!inputs.contains(Inputs.shift))
						center = center.subtract(Position.getUnitVector(rotation).scale(0.5*(width - oldWidth)));
				}
			}
			else
				state = null;
		}
		if(((isOverSide = isOver(g, RectangleState.changingDown, canvasCenter, mousePos)) && state == null) || state == RectangleState.changingDown)
		{
			if(inputs.contains(Inputs.leftMouse))
			{
				if(state != RectangleState.changingDown)
					state = RectangleState.changingDown;
				if(mousePos.getY() - upperLeft.getY() > 0)
				{
					double oldHeight = height;
					height = mousePos.getY() - upperLeft.getY();
					if(!inputs.contains(Inputs.shift))
						center = center.add(Position.getUnitVector(rotation + Math.PI/2).scale(0.5*(height - oldHeight)));
				}
			}
			else
				state = null;
		}
		if((isIn(mousePos, upperLeft, upperLeft.add(width, height)) && state == null) || state == RectangleState.moving)
		{
			if(inputs.contains(Inputs.leftMouse))
			{
				Position mp = mousePosition;
				if(state != RectangleState.moving)
				{
					state = RectangleState.moving;
					startPosition = mp;
				}
				center = center.add(mp.subtract(startPosition));
				startPosition = mp;
			}
			else
				state = null;
		}
	}
	
	private Position offset(Position pos, int scaler)
	{
		return pos.add(new Position(5, 5).scale(scaler));
	}
	
	private boolean isOver(Graphics2D g, RectangleState s, Position canvasCenter, Position mousePosition)
	{
		boolean ret = false;
		Position[] corners = new Position[4];
		Position upperLeft = canvasCenter.add(center).subtract(width/2, height/2);
		Position upperRight = upperLeft.add(width, 0);
		Position lowerLeft = upperLeft.add(0, height);
		Position lowerRight = upperLeft.add(width, height);
		corners[0] = upperLeft;
		corners[1] = upperRight;
		corners[2] = lowerLeft;
		corners[3] = lowerRight;
		if(s == RectangleState.rotating)
		{
			//check corners
			for(Position corner : corners)
			{
				if(isIn(mousePosition, offset(corner, -1), offset(corner, 1)))
				{
					ret = true;
					rotateGraphics(g, canvasCenter);
					g.setStroke(new BasicStroke(10));
					g.setColor(Color.red);
					g.drawLine((int)corner.getX(), (int)corner.getY(), (int)corner.getX(), (int)corner.getY());
					unrotateGraphics(g, canvasCenter);
				}
			}
		}
		else if(s == RectangleState.changingRight || s == RectangleState.changingUp ||
				s == RectangleState.changingLeft || s == RectangleState.changingDown)
		{
			Position a = null, b = null;
			switch (s)
			{
				case changingRight:
					a = upperRight;
					b = lowerRight;
					break;
				case changingUp:
					a = upperLeft;
					b = upperRight;
					break;
				case changingLeft:
					a = upperLeft;
					b = lowerLeft;
					break;
				case changingDown:
					a = lowerLeft;
					b = lowerRight;
					break;
			}
			if(isIn(mousePosition, offset(a, -1), offset(b, 1)))
			{
				ret = true;
				rotateGraphics(g, canvasCenter);
				g.setStroke(new BasicStroke(5));
				g.setColor(Color.red);
				g.drawLine((int)a.getX(), (int)a.getY(), (int)b.getX(), (int)b.getY());
				unrotateGraphics(g, canvasCenter);
			}
		}
		return ret;
	}
	
	private boolean isIn(Position pos, Position upperLeft, Position lowerRight)
	{
		return (pos.getX() <= lowerRight.getX()) && (pos.getY() <= lowerRight.getY())
				&& (pos.getX() >= upperLeft.getX()) && (pos.getY() >= upperLeft.getY());
	}
	
	private void drawRect(Graphics2D g, Position canvasCenter, Color c)
	{
		Position upperLeft = canvasCenter.add(center).subtract(width/2, height/2);
		rotateGraphics(g, canvasCenter);
		g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 128));
		g.fillRect((int)upperLeft.getX(), (int)upperLeft.getY(), 
				(int)(width), (int)(height));
		
		g.setColor(c);
		g.setStroke(new BasicStroke(3));
		g.drawRect((int)upperLeft.getX(), (int)upperLeft.getY(), 
				(int)(width), (int)(height));
		unrotateGraphics(g, canvasCenter);
	}
	
	private void rotateGraphics(Graphics2D g, Position canvasCenter)
	{
		Position center = canvasCenter.add(this.center);
		g.rotate(rotation, (int)(center.getX()), (int)(center.getY()));
	}
	private void unrotateGraphics(Graphics2D g, Position canvasCenter)
	{
		Position center = canvasCenter.add(this.center);
		g.rotate(-rotation, (int)(center.getX()), (int)(center.getY()));
	}

	@Override
	public ShapeConfiguration generateConfiguration(double scalingFactor) {
		return new RectangleConfiguration(scalingFactor*width, scalingFactor*height, new Transformation(center.scale(scalingFactor), rotation));
	}

	@Override
	public Transformation getTransformation() {
		return new Transformation(center, rotation);
	}

}
