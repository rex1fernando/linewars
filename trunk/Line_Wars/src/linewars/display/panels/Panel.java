package linewars.display.panels;

import java.awt.Graphics;

import javax.swing.JPanel;

import linewars.display.Animation;
import linewars.display.ImageDrawer;
import linewars.gameLogic.GameStateProvider;
import linewars.gamestate.Position;

/**
 * Encapsulates information for displaying panels on the display.
 * 
 * @author Titus Klinge
 * @author Ryan Tew
 * 
 */
@SuppressWarnings("serial")
public abstract class Panel extends JPanel
{
	private enum ANIMATION
	{
		DEFAULT, ROLE_IN, ROLE_OUT
	}

	private int panelWidth;
	private int panelHeight;

	/**
	 * The factor that the image is scaled to in order to fill the panel
	 */
	protected double scaleFactor;

	protected GameStateProvider stateManager;
	protected Animation[] animations;
	protected Animation curAnimation;

	/**
	 * Constructs this Panel
	 * 
	 * @param stateManager
	 *            The gamestate manager for this instance of the game.
	 * @param width
	 *            The width of the panel.
	 * @param height
	 *            The height of the panel.
	 * @param animations
	 *            The list of animations for the panel.
	 */
	public Panel(GameStateProvider stateManager, int width, int height, Animation... animations)
	{
		super(null);
		setOpaque(false);

		panelWidth = width;
		panelHeight = height;

		scaleFactor = 1.0;

		setSize(panelWidth, panelHeight);

		// check for correct animations
		// if (animations == null || animations.length != ANIMATION.values().length)
		// {
		// 		throw new IllegalArgumentException("A Panel requires exactly " +
		// 						ANIMATION.values().length + " animations!");
		// }

		this.stateManager = stateManager;

		Position size = new Position(panelWidth, panelHeight);
		this.animations = new Animation[animations.length];
		for(int i = 0; i < animations.length; ++i)
		{
			animations[i].loadAnimationResources(size);
			this.animations[i] = animations[i];
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
	 * Updates the size and location of the panel relative to its parent. This
	 * method is called when the containing panel is resized.
	 */
	public void updateLocation()
	{
		setSize((int)(scaleFactor * panelWidth), (int)(scaleFactor * panelHeight));
	}

	@Override
	public void paint(Graphics g)
	{
		if(curAnimation != null)
		{
			ImageDrawer.getInstance().draw(g, curAnimation.getImage(stateManager.getCurrentGameState().getTime(), 0.0),
					panelWidth, panelHeight,
					new Position(0, 0), scaleFactor);
		}
		
		super.paint(g);
	}
}
