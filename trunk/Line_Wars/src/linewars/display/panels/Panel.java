package linewars.display.panels;

import java.awt.Graphics;

import javax.swing.JPanel;

import linewars.display.Animation;
import linewars.display.MapItemDrawer;
import linewars.gameLogic.GameStateProvider;
import linewars.gamestate.Position;
import linewars.parser.Parser;

@SuppressWarnings("serial")
public abstract class Panel extends JPanel
{	
	private enum ANIMATION { DEFAULT, ROLE_IN, ROLE_OUT }
	
	private int panelWidth;
	private int panelHeight;
	
	protected GameStateProvider stateManager;
	protected Animation[] animations;
	protected Animation curAnimation;
	
	public Panel(GameStateProvider stateManager, int width, int height, Parser ... animations)
	{
		super(null);
		setOpaque(false);
		
		panelWidth = width;
		panelHeight = height;
		
		setSize(panelWidth, panelHeight);
		
		// check for correct animations
//		if (animations == null || animations.length != ANIMATION.values().length)
//		{
//			throw new IllegalArgumentException("A Panel requires exactly " + ANIMATION.values().length + " animations!");
//		}
		
		this.stateManager = stateManager;
		
		
		this.animations = new Animation[animations.length];
		for(int i = 0; i < animations.length; ++i)
		{
			this.animations[i] = new Animation(animations[i], width, height);
		}
		
		if(this.animations.length != 0)
		{
			curAnimation = this.animations[ANIMATION.DEFAULT.ordinal()];
		}
		else
		{
			curAnimation = null;
		}
	}
	
	/**
	 * Updates the size and location of the panel relative to its parent.  This method
	 * is called when the containing panel is resized.
	 */
	public void updateLocation()
	{
		setSize(panelWidth, panelHeight);
	}
	
	@Override
	public void paint(Graphics g)
	{
//		g.setColor(Color.black);
//		g.fillRect(0, 0, getWidth(), getHeight());
		if(curAnimation != null)
		{
			MapItemDrawer.getInstance().draw(g, curAnimation.getImage(stateManager.getCurrentGameState().getTime(), 0.0), new Position(0,0), 0.0, 1, 1);
		}
		super.paint(g);
	}
}
