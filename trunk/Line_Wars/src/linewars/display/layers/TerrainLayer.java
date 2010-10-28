package linewars.display.layers;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import linewars.display.MapItemDrawer;
import linewars.gamestate.GameState;
import linewars.gamestate.Position;
import linewars.parser.ParserKeys;

public class TerrainLayer implements ILayer
{
	@Override
	public void draw(Graphics g, GameState gamestate, Rectangle2D visibleScreen, double scaleX, double scaleY)
	{
		//TODO remove this comment
		String uri = gamestate.getMap().getParser().getStringValue(ParserKeys.icon);
		MapItemDrawer.getInstance().draw(g, uri, new Position(-visibleScreen.getX(), -visibleScreen.getY()), 0.0, scaleX, scaleY);
	}
}
