package linewars.display.panels;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import linewars.configfilehandler.ConfigData;
import linewars.display.Animation;
import linewars.display.ImageDrawer;
import linewars.gameLogic.GameStateProvider;
import linewars.gamestate.Position;

@SuppressWarnings("serial")
public abstract class Panel extends JPanel
{	
	private enum ANIMATION { DEFAULT, ROLE_IN, ROLE_OUT }
	
	private int panelWidth;
	private int panelHeight;
	
	/**
	 * The factor that the image is scaled to in order to fill the panel
	 */
	protected double scaleFactor;
	
	protected GameStateProvider stateManager;
	protected Animation[] animations;
	protected Animation curAnimation;
	
	public Panel(GameStateProvider stateManager, int width, int height, ConfigData ... animations)
	{
		super(null);
		setOpaque(false);
		
		panelWidth = width;
		panelHeight = height;
		
		scaleFactor = 1.0;
		
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
			this.animations[i] = new Animation(animations[i], "", width, height);
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
		if(curAnimation != null)
		{
			ImageDrawer.getInstance().draw(g, curAnimation.getImage(stateManager.getCurrentGameState().getTime(), 0.0), new Position(0,0), 0.0, scaleFactor);
		}
		super.paint(g);
	}
}
