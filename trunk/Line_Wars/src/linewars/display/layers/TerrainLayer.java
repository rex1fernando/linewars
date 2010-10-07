package linewars.display.layers;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import linewars.gamestate.GameState;

public class TerrainLayer implements ILayer {

	@Override
	public void draw(Graphics g, GameState gamestate, Rectangle2D visibleScreen)
	{
		// String uri = gamestate.getMap();
		// MapItemDrawer.getInstance().draw(g, uri, pos(0,0));
	}
}
