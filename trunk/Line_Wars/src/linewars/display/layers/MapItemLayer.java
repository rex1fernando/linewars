package linewars.display.layers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import linewars.display.Animation;
import linewars.display.CircleDrawer;
import linewars.display.Display;
import linewars.display.DisplayConfiguration;
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
	}

	@Override
	public void draw(Graphics g, GameState gamestate, Rectangle2D visibleScreen, double scale)
	{
		Rectangle screenRect = new Rectangle(new Transformation(new Position(visibleScreen.getCenterX(),
				visibleScreen.getCenterY()), 0), visibleScreen.getWidth(), visibleScreen.getHeight());
		for(MapItem mapItem : gamestate.getMapItemsOfType(mapItemType))
		{
			Position pos = mapItem.getPosition();
			Position size = ((DisplayConfiguration)mapItem.getDefinition().getDisplayConfiguration()).getDimensions();
			double rotation = mapItem.getRotation();
			int width = (int)size.getX();
			int height = (int)size.getY();

			Shape itemShape = new Rectangle(mapItem.getTransformation(), width, height);

			if(screenRect.isCollidingWith(itemShape))
			{
				if(mapItemType == MapItemType.LANEBORDER)
				{
					pos = display.toScreenCoord(pos);
					g.setColor(Color.red);
					CircleDrawer.drawCircle(g, pos, mapItem.getRadius() * scale);
					continue;
				}
				
				Animation anim = ((DisplayConfiguration)(mapItem.getDefinition().getDisplayConfiguration())).getAnimation(mapItem.getState());
				if(anim == null)
				{
					anim = ((DisplayConfiguration)(mapItem.getDefinition().getDisplayConfiguration())).getAnimation(MapItemState.Idle);
				}

				// get the items coordinates based on the visible screen
				pos = display.toScreenCoord(pos);

				// rotate the image
				((Graphics2D)g).rotate(rotation, pos.getX(), pos.getY());
				
				// draw the animation
				ImageDrawer.getInstance().drawAtCenter(g,
						anim.getImage(gamestate.getTime(), mapItem.getStateStartTime()),
						width, height,
						pos, scale);

				// set the graphics to not be rotated anymore
				((Graphics2D)g).rotate(-rotation, pos.getX(), pos.getY());
			}
		}
	}
}
