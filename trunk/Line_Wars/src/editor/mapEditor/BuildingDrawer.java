package editor.mapEditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

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
		pos = panel.toScreenCoord(new Position(pos.getX() - (w / 2), pos.getY() - (h / 2)));

		double rotation = trans.getRotation();
		double x = pos.getX();
		double y = pos.getY();

		if(commandCenter)
		{
			g.setColor(Color.yellow);
			g.fillOval((int)x, (int)y, (int)(w * scale), (int)(h * scale));
		}
		else
		{
			g.setColor(Color.blue);
			((Graphics2D)g).rotate(rotation, x, y);
			g.fillRect((int)x, (int)y, (int)(w * scale), (int)(h * scale));
			((Graphics2D)g).rotate(-rotation, x, y);
		}
	}
}
