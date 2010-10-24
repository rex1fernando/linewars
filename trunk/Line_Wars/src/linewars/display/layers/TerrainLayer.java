package linewars.display.layers;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import linewars.display.MapItemDrawer;
import linewars.gamestate.GameState;
import linewars.gamestate.Position;

public class TerrainLayer implements ILayer {

	@Override
	public void draw(Graphics g, GameState gamestate, Rectangle2D visibleScreen, double scaleX, double scaleY)
	{
		String uri = gamestate.getMap();
		MapItemDrawer.getInstance().draw(g, uri, new Position(-visibleScreen.getX(), -visibleScreen.getY()), 0.0, scaleX, scaleY);
	}
}
