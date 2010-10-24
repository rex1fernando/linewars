package linewars.display.layers;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import linewars.display.Animation;
import linewars.display.MapItemDrawer;
import linewars.gamestate.GameState;
import linewars.gamestate.Position;
import linewars.gamestate.mapItems.MapItem;
import linewars.parser.ParserKeys;

public class MapItemLayer implements ILayer
{
	public enum MapItemType {UNIT, PROJECTILE, BUILDING}
	
	private MapItemType mapItemType;
	
	public MapItemLayer(MapItemType type)
	{
		mapItemType = type;
	}
	
	@Override
	public void draw(Graphics g, GameState gamestate, Rectangle2D visibleScreen)
	{
		for (MapItem mapItem : getMapItems(gamestate))
		{
			Position pos = mapItem.getPosition();
			Rectangle2D rect = new Rectangle2D.Double(pos.getX(), pos.getY(), mapItem.getWidth(), mapItem.getHeight());
			double rotation = mapItem.getRotation();
			
			if (visibleScreen.intersects(rect))
			{
				//TODO I fixed this line when I updated how the parser works -Connor
				Animation anim = mapItem.getAnimation();
				MapItemDrawer.getInstance().draw(g, anim.getImage(gamestate.getTime()), pos, rotation);
			}
		}
	}
	
	private List<MapItem> getMapItems(GameState gamestate)
	{
		switch (mapItemType)
		{
		case UNIT:
			return gamestate.getUnits();
		case PROJECTILE:
			return gamestate.getProjectiles();
		case BUILDING:
			return gamestate.getBuildings();
		default:
			return new ArrayList<MapItem>(0);	
		}
	}

}
