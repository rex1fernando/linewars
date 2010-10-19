package linewars.display.panels;

import java.awt.Graphics;

import javax.swing.JPanel;

import linewars.display.Animation;
import linewars.display.MapItemDrawer;
import linewars.gamestate.GameStateManager;
import linewars.gamestate.Position;

public abstract class Panel extends JPanel
{
	private final double width, height;
	private final double x_pos, y_pos;
	
	private enum ANIMATION { DEFAULT, ROLE_IN, ROLE_OUT }
	
	protected GameStateManager stateManager;
	private Animation[] animations;
	protected Animation curAnimation;
	
	public Panel(GameStateManager stateManager, , Animation ... animations)
	{
		super(null);
		setOpaque(false);
		
		this.width = width;
		this.height = height;
		x_pos = x;
		y_pos = y;
		
		// check for correct animations
		if (animations == null || animations.length != ANIMATION.values().length)
		{
			throw new IllegalArgumentException("The CommandCardPanel requires exactly " + ANIMATION.values().length + " animations!");
		}
		
		this.stateManager = stateManager;
		this.animations = animations;
		curAnimation = animations[ANIMATION.DEFAULT.ordinal()];
	}
	
	/**
	 * Updates the size and location of the panel relative to its parent.  This method
	 * is called when the containing panel is resized.
	 */
	public abstract void updateLocation();
	
	@Override
	public void paint(Graphics g)
	{
		MapItemDrawer d = MapItemDrawer.getInstance();
		//g.setColor(Color.black);
		//g.fillRect(getX(), getY(), getWidth(), getHeight());
		d.draw(g, curAnimation.getImage(stateManager.getDisplayGameState().getTime()), new Position(0,0));
		super.paint(g);
	}
}
