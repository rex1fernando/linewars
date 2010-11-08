package editor.mapEditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import linewars.gamestate.BuildingSpot;
import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.shapes.Circle;
import linewars.gamestate.shapes.Rectangle;

public class BuildingDrawer
{
	private MapPanel panel;
	
	public BuildingDrawer(MapPanel panel)
	{
		this.panel = panel;
	}

	public void draw(Graphics g, BuildingSpot buildingSpot, Position mouse, double scale)
	{
		draw(g, buildingSpot, mouse, scale, false);
	}

	public void draw(Graphics g, BuildingSpot buildingSpot, Position mouse, double scale, boolean commandCenter)
	{
		Dimension dim = buildingSpot.getDim();
		double w = dim.getWidth();
		double h = dim.getHeight();
		
		Transformation trans = buildingSpot.getTrans();
		Position gamePos = trans.getPosition();
		Position rotateAbout = panel.toScreenCoord(gamePos);
		Position pos = panel.toScreenCoord(new Position(gamePos.getX() - (w / 2), gamePos.getY() - (h / 2)));

		double rotation = trans.getRotation();
		double x = rotateAbout.getX();
		double y = rotateAbout.getY();

		//set the transparent color
		if(commandCenter)
			g.setColor(new Color(255, 255, 0, 80));
		else
			g.setColor(new Color(0, 0, 255, 80));

		//rotate the graphics
		((Graphics2D)g).rotate(rotation, x, y);
		
		//draw the rectangle
		g.fillRect((int)pos.getX(), (int)pos.getY(), (int)(w * scale), (int)(h * scale));
		
		//set the border color
		if(commandCenter)
			g.setColor(new Color(255, 255, 0));
		else
			g.setColor(new Color(0, 0, 255));
		
		//set the brush size
		Rectangle outer = new Rectangle(trans, w + 2.5 / scale, h + 2.5 / scale);
		Rectangle inner = new Rectangle(trans, w - 2.5 / scale, h - 2.5 / scale);
		if(outer.positionIsInShape(mouse) && !inner.positionIsInShape(mouse))
			((Graphics2D)g).setStroke(new BasicStroke(10));
		else
			((Graphics2D)g).setStroke(new BasicStroke(5));

		//draw the border
		g.drawRect((int)pos.getX(), (int)pos.getY(), (int)(w * scale), (int)(h * scale));
		
		//set the color for the rotation dot
		g.setColor(Color.pink);
		
		//set the dot position
		Position dotPos = new Position(gamePos.getX() + w / 2, gamePos.getY() + h / 2);
		Position drawDot = panel.toScreenCoord(dotPos);
		
		//draw the dot
		Circle dot = new Circle(new Transformation(dotPos, 0), 5 / scale);
		if(dot.positionIsInShape(mouse))
			g.fillOval((int)drawDot.getX() - 10, (int)drawDot.getY() - 10, 20, 20);
		else
			g.fillOval((int)drawDot.getX() - 5, (int)drawDot.getY() - 5, 10, 10);
		
		//rotate the graphics back to its original rotation
		((Graphics2D)g).rotate(-rotation, x, y);
	}
}
