package linewars.display.layers;

import java.awt.BasicStroke;
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
import linewars.gamestate.mapItems.Building;
import linewars.gamestate.mapItems.Gate;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.abilities.Ability;
import linewars.gamestate.mapItems.abilities.ProgressAbility;
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
	
	private static final double RADIUS_SCALE = 0.4;
	private static final int NUM_SIDES = 6;
	private static final double HEALTH_BAR_THICKNESS = 0.4;

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
				
				if(display.showUnderlays() && mapItem instanceof Unit && mapItem.getState() != MapItemState.Dead)
				{
					drawHealth((Graphics2D)g, pos, width*scale, height*scale, scale, (Unit) mapItem);
					drawPlayerColor((Graphics2D) g, pos, width*scale, height*scale, scale, (Unit) mapItem);
				}
				else if(display.showUnderlays() && mapItem instanceof Building && mapItem.getState() != MapItemState.Dead)
					drawProgress((Graphics2D)g, pos, width*scale, height*scale, scale, (Building) mapItem);
				else if(mapItem instanceof Gate && mapItem.getState() != MapItemState.Dead)
					drawHealth((Graphics2D)g, pos, width*scale, height*scale, scale, (Unit) mapItem);
					
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
	
	private void drawHealth(Graphics2D g, Position pos, double width, double height, double scale, Unit u)
	{
		double healthRatio = u.getHP()/u.getMaxHP();
		drawRatioCircle(g, pos, width, height, scale, healthRatio, Color.green, Color.red, u.getRadius());
	}
	
	private void drawProgress(Graphics2D g, Position pos, double width, double height, double scale, Building b)
	{
		double progress = 0;
		int num = 0;
		for(Ability a : b.getActiveAbilities())
		{
			if(a instanceof ProgressAbility)
			{
				progress = (progress*num + ((ProgressAbility)a).getProgress())/(num + 1);
				++num;
			}
		}
		if(num > 0)
		{
			DisplayConfiguration dc = (DisplayConfiguration) b.getDefinition().getDisplayConfiguration();
			double radius = 10*Math.max(dc.getDimensions().getX(), dc.getDimensions().getY());
			drawRatioCircle(g, pos, 1.5*width, 1.5*height, scale, progress, Color.DARK_GRAY, Color.gray, radius);
		}
	}
	
	private void drawRatioCircle(Graphics2D g, Position pos, double width, double height, 
			double scale, double ratio, Color initialColor, Color finalColor, double mapItemRadius)
	{
		double radius = Math.min(width, height)*RADIUS_SCALE;
		double degreesPerSide = 2*Math.PI/(NUM_SIDES);
		
		g.setStroke(new BasicStroke((float) (HEALTH_BAR_THICKNESS*scale*Math.log(mapItemRadius))));
		g.setColor(initialColor);
		
		Position lastPos = Position.getUnitVector(0).scale(radius).add(pos);
		for(double angle = 0; angle + degreesPerSide <= 2*Math.PI; angle += degreesPerSide)
		{
			double angleRatio = angle/(2*Math.PI);
			double nextAngleRatio = (angle + degreesPerSide)/(2*Math.PI);
			Position newPos = Position.getUnitVector(angle + degreesPerSide).scale(radius).add(pos);
			if(angleRatio < ratio && nextAngleRatio >= ratio && ratio < 1.0)
			{
				double r = (ratio - angleRatio)/(degreesPerSide/(2*Math.PI));
				Position intermediatePos = newPos.subtract(lastPos).scale(r).add(lastPos);
				g.drawLine((int)lastPos.getX(), (int)lastPos.getY(), (int)intermediatePos.getX(), (int)intermediatePos.getY());
				lastPos = intermediatePos;
				g.setColor(finalColor);
			}
			
			g.drawLine((int)lastPos.getX(), (int)lastPos.getY(), (int)newPos.getX(), (int)newPos.getY());
			lastPos = newPos;
		}
		
		g.setStroke(new BasicStroke());
	}
	
	private void drawPlayerColor(Graphics2D g, Position pos, double width, double height, double scale, Unit u)
	{
		int numSides = 3;
		double radius = Math.min(width, height)*0.4;
		double degreesPerSide = 2*Math.PI/(numSides);
		
		g.setStroke(new BasicStroke((float) (0.01*scale*u.getRadius())));
		Color c = ImageDrawer.getInstance().getPlayerColor(u.getOwner().getPlayerID(), u.getGameState().getNumPlayers());
		if(c == Color.white)
			return;
		g.setColor(c);
		
		int[] xPos = new int[numSides];
		int[] yPos = new int[numSides];
		
		int i = 0;
		for(double angle = 0; angle < 2*Math.PI; angle += degreesPerSide)
		{
			Position p = Position.getUnitVector(angle).scale(radius).add(pos);
			xPos[i] = (int) p.getX();
			yPos[i] = (int) p.getY();
			++i;
		}
		
		g.fillPolygon(xPos, yPos, xPos.length);
		
		g.setStroke(new BasicStroke());
	}
}
