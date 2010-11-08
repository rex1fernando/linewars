package editor.mapEditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import linewars.gamestate.BuildingSpot;
import linewars.gamestate.Position;
import linewars.gamestate.Transformation;

public class BuildingDrawer
{
	private MapPanel panel;
	
	public BuildingDrawer(MapPanel panel)
	{
		this.panel = panel;
	}

	public void draw(Graphics g, BuildingSpot buildingSpot, double scale)
	{
		draw(g, buildingSpot, scale, false);
	}

	public void draw(Graphics g, BuildingSpot buildingSpot, double scale, boolean commandCenter)
	{
		Dimension dim = buildingSpot.getDim();
		double w = dim.getWidth();
		double h = dim.getHeight();
		
		Transformation trans = buildingSpot.getTrans();
		Position pos = trans.getPosition();
		Position rotateAbout = panel.toScreenCoord(pos);
		pos = panel.toScreenCoord(new Position(pos.getX() - (w / 2), pos.getY() - (h / 2)));

		double rotation = trans.getRotation();
		double x = rotateAbout.getX();
		double y = rotateAbout.getY();

		if(commandCenter)
		{
			g.setColor(Color.yellow);
		}
		else
		{
			g.setColor(Color.blue);
		}

		((Graphics2D)g).rotate(rotation, x, y);
		g.fillRect((int)pos.getX(), (int)pos.getY(), (int)(w * scale), (int)(h * scale));
		g.setColor(Color.pink);
		g.fillOval((int)(x + ((w / 2) - 25) * scale), (int)(y - 25 * scale), (int)(50 * scale), (int)(50 * scale));
		((Graphics2D)g).rotate(-rotation, x, y);
	}
}
