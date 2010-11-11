package linewars.display.layers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ConfigData.NoSuchKeyException;
import linewars.configfilehandler.ConfigFileReader;
import linewars.configfilehandler.ConfigFileReader.InvalidConfigFileException;
import linewars.configfilehandler.ParserKeys;
import linewars.display.Animation;
import linewars.display.CircleDrawer;
import linewars.display.Display;
import linewars.display.ImageDrawer;
import linewars.gamestate.GameState;
import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.shapes.Rectangle;
import linewars.gamestate.shapes.Shape;

/**
 * Handles the drawing of all the map items. (Units, Projectiles, and
 * Buildings).
 * 
 * @author Ryan Tew
 * 
 */
public class MapItemLayer implements ILayer
{
	public enum MapItemType
	{
		UNIT, PROJECTILE, BUILDING, LANEBORDER
	}

	private Display display;

	private MapItemType mapItemType;

	private Map<String, Map<MapItemState, Animation>> unitToStateMap;

	/**
	 * Constructs this map item layer.
	 * 
	 * @param type
	 *            The type of map item this layer will display.
	 * @param d
	 *            The display that this layer will be drawing for.
	 */
	public MapItemLayer(MapItemType type, Display d)
	{
		display = d;

		mapItemType = type;

		unitToStateMap = new HashMap<String, Map<MapItemState, Animation>>();
	}

	@Override
	public void draw(Graphics g, GameState gamestate, Rectangle2D visibleScreen, double scale)
	{
		Rectangle screenRect = new Rectangle(new Transformation(new Position(visibleScreen.getCenterX(),
				visibleScreen.getCenterY()), 0), visibleScreen.getWidth(), visibleScreen.getHeight());
		for(MapItem mapItem : gamestate.getMapItemsOfType(mapItemType))
		{
			Position pos = mapItem.getPosition();
			double rotation = mapItem.getRotation();

			Shape itemShape = mapItem.getBody();// new Rectangle(pos.getX() -
												// width / 2, pos.getY() -
												// height / 2, width, height);

			if(screenRect.isCollidingWith(itemShape))
			{
				if(mapItemType == MapItemType.LANEBORDER)
				{
					pos = display.toScreenCoord(pos);
					g.setColor(Color.red);
					CircleDrawer.drawCircle(g, pos, mapItem.getRadius() * scale);
					continue;
				}
				// get the animation map for the unit
				String uri = mapItem.getURI();
				Map<MapItemState, Animation> stateToAnimationMap = unitToStateMap.get(uri);
				if(stateToAnimationMap == null)
				{
					// we have not drawn this unit before
					// add a mapping for this unit
					stateToAnimationMap = new HashMap<MapItemState, Animation>();
					unitToStateMap.put(uri, stateToAnimationMap);
				}

				// get the animation for the state
				MapItemState state = mapItem.getState();
				Animation anim = stateToAnimationMap.get(state);
				if(anim == null)
				{
					// we have not drawn this state before
					// get the animation for this state
					ConfigData parser = null;
					try
					{
						parser = new ConfigFileReader(mapItem.getParser().getString(
								ParserKeys.valueOf(state.toString()))).read();
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

					// add an animation mapping for this state
					anim = new Animation(parser, mapItem.getURI(), (int)mapItem.getWidth(), (int)mapItem.getHeight());
					stateToAnimationMap.put(state, anim);
				}

				// get the items coordinates based on the visible screen
				Position upperLeftPos = new Position(pos.getX() - visibleScreen.getX() - (mapItem.getWidth() / 2),
						pos.getY() - visibleScreen.getY() - (mapItem.getHeight() / 2));
				pos = display.toScreenCoord(pos);

				// rotate the image
				((Graphics2D)g).rotate(rotation, pos.getX(), pos.getY());

				// draw the animation
				ImageDrawer.getInstance().draw(g,
						anim.getImage(gamestate.getTime(), mapItem.getStateStartTime()) + mapItem.getURI(),
						upperLeftPos, rotation, scale);

				// set the graphics to not be rotated anymore
				((Graphics2D)g).rotate(-rotation, pos.getX(), pos.getY());
			}
		}
	}
}
