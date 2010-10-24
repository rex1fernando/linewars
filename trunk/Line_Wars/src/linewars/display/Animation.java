package linewars.display;

import linewars.parser.Parser;
import linewars.parser.ParserKeys;

/**
 * Encapsulates animation information.
 * 
 * @author Titus Klinge
 * @author Ryan Tew
 */
public class Animation
{
	private final String[] imageURIs;
	private final double[] displayTimes;
	private final double creationTime;

	/**
	 * Initializes the animation with the specified image file URIs,
	 * corresponding display times, and its creation time.
	 * 
	 * @param imageURIs
	 *            The image URIs that make up the animation.
	 * @param displayTimes
	 *            The time each image is displayed.
	 * @param creationTime
	 *            The time the animation object is created.
	 */
	public Animation(String[] imageURIs, double[] displayTimes, double creationTime)
	{
		if (imageURIs == null || displayTimes == null || imageURIs.length != displayTimes.length)
		{
			throw new IllegalArgumentException(
					"The number of URIs and display times must be the same!");
		}
		
		this.imageURIs = imageURIs;
		this.displayTimes = displayTimes;
		this.creationTime = creationTime;
	}
	
	public Animation(Parser parser, double creationTime)
	{
		String[] times = parser.getList(ParserKeys.displayTime);
		displayTimes = new double[times.length];
		for(int i = 0; i < times.length; ++i)
		{
			displayTimes[i] = new Double(times[i]).doubleValue();
		}
		
		imageURIs = parser.getList(ParserKeys.icon);
		this.creationTime = creationTime;
	}

	/**
	 * Returns the image to be displayed at the current game time.
	 * 
	 * @param gameTime
	 *            The time of the game.
	 * 
	 * @return The string URI associated with the image to be used at the
	 *         current game time.
	 */
	public String getImage(double gameTime)
	{
		if (imageURIs.length == 1) return imageURIs[0];
		
		double curTime = gameTime - creationTime;

		double sum = 0;
		for (int i = 0; i < displayTimes.length; i++)
		{
			sum += displayTimes[i];
		}

		double time = curTime % sum;

		int i;
		for (i = 0; i < displayTimes.length; i++)
		{
			if (time > displayTimes[i])
			{
				time -= displayTimes[i];
			} else
			{
				return imageURIs[i];
			}
		}

		// this should never happen...but........
		return imageURIs[i];
	}
}
