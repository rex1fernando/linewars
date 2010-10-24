package linewars.display.panels;

import java.awt.Graphics;

import javax.swing.JPanel;

import linewars.display.Animation;
import linewars.display.MapItemDrawer;
import linewars.gamestate.GameStateManager;
import linewars.gamestate.Position;

@SuppressWarnings("serial")
public abstract class Panel extends JPanel
{	
	private enum ANIMATION { DEFAULT, ROLE_IN, ROLE_OUT }
	
	private int width;
	private int height;
	
	protected GameStateManager stateManager;
	protected Animation[] animations;
	protected Animation curAnimation;
	
	public Panel(GameStateManager stateManager, int width, int height, Animation ... animations)
	{
		super(null);
		setOpaque(false);
		
		this.width = width;
		this.height = height;
		
		setSize(width, height);
		
		// check for correct animations
		if (animations == null || animations.length != ANIMATION.values().length)
		{
			throw new IllegalArgumentException("A Panel requires exactly " + ANIMATION.values().length + " animations!");
		}
		
		this.stateManager = stateManager;
		this.animations = animations;
		curAnimation = animations[ANIMATION.DEFAULT.ordinal()];
	}
	
	/**
	 * Updates the size and location of the panel relative to its parent.  This method
	 * is called when the containing panel is resized.
	 */
	public void updateLocation()
	{
		setSize(width, height);
	}
	
	@Override
	public void paint(Graphics g)
	{
//		g.setColor(Color.black);
//		g.fillRect(0, 0, getWidth(), getHeight());
		MapItemDrawer d = MapItemDrawer.getInstance();
		d.draw(g, curAnimation.getImage(stateManager.getDisplayGameState().getTime()), new Position(0,0), 0.0);
		super.paint(g);
	}
}
