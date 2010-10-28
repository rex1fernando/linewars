package linewars.display.layers;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import linewars.display.Animation;
import linewars.display.MapItemDrawer;
import linewars.gamestate.GameState;
import linewars.gamestate.Position;
import linewars.gamestate.mapItems.CommandCenter;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemState;
import linewars.parser.ConfigFile;
import linewars.parser.Parser;
import linewars.parser.Parser.InvalidConfigFileException;
import linewars.parser.Parser.NoSuchKeyException;

public class MapItemLayer implements ILayer
{
	public enum MapItemType {UNIT, PROJECTILE, BUILDING}
	
	private MapItemType mapItemType;
	
	private Map<String, Map<MapItemState, Animation>> unitToStateMap;
	
	public MapItemLayer(MapItemType type)
	{
		mapItemType = type;
		
		unitToStateMap = new HashMap<String, Map<MapItemState, Animation>>();
	}
	
	@Override
	public void draw(Graphics g, GameState gamestate, Rectangle2D visibleScreen, double scaleX, double scaleY)
	{
		for (MapItem mapItem : gamestate.getMapItemsOfType(mapItemType))
		{
			Position pos = mapItem.getPosition();
			Rectangle2D rect = new Rectangle2D.Double(pos.getX(), pos.getY(), mapItem.getWidth(), mapItem.getHeight());
			double rotation = mapItem.getRotation();
			
			if (visibleScreen.intersects(rect))
			{
				//get the animation map for the unit
				String uri = mapItem.getURI();
				Map<MapItemState, Animation> stateToAnimationMap = unitToStateMap.get(uri);
				if(stateToAnimationMap == null)
				{
					//we have not drawn this unit before
					//add a mapping for this unit
					stateToAnimationMap = new HashMap<MapItemState, Animation>();
					unitToStateMap.put(uri, stateToAnimationMap);
				}
				
				//get the animation for the state
				MapItemState state = mapItem.getState();
				Animation anim = stateToAnimationMap.get(state);
				if(anim == null)
				{
					//we have not drawn this state before
					//get the animation for this state
					Parser parser = null;
					try
					{
						parser = new Parser(new ConfigFile(mapItem.getParser().getStringValue(state.toString())));
					}
					catch (FileNotFoundException e)
					{
						e.printStackTrace();
					}
					catch (NoSuchKeyException e)
					{
						e.printStackTrace();
					}
					catch (InvalidConfigFileException e)
					{
						e.printStackTrace();
					}
					
					//add an animation mapping for this state
					anim = new Animation(parser, mapItem.getURI(), (int)mapItem.getWidth(), (int)mapItem.getHeight());
					stateToAnimationMap.put(state, anim);
				}
				
				//get the items coordinates based on the visible screen
				pos = new Position(pos.getX() - visibleScreen.getX() - (mapItem.getWidth() / 2), pos.getY() - visibleScreen.getY() - (mapItem.getHeight() / 2));
				
				//draw the animation
				MapItemDrawer.getInstance().draw(g, anim.getImage(gamestate.getTime(), mapItem.getStateStartTime()) + mapItem.getURI(), pos, rotation, scaleX, scaleY);
			}
		}
	}
}
