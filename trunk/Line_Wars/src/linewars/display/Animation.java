package linewars.display;

import java.io.IOException;
import java.util.List;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;

/**
 * Encapsulates animation information.
 * 
 * @author Titus Klinge
 * @author Ryan Tew
 */
public class Animation
{
	private String[] imageURIs;
	private double[] displayTimes;

	/**
	 * Creates this animation.
	 * 
	 * @param parser
	 *            The ConfigData for the animation.
	 * @param mapItemURI
	 *            The URI of the MapItem this animation is for.
	 * @param width
	 *            The width of the animation in game units.
	 * @param height
	 *            The height of the animation in game units.
	 */
	public Animation(ConfigData parser, String mapItemURI, int width, int height)
	{
		// get the file the animation is in
		String file = "/" + parser.getURI();
		file = file.substring(0, file.lastIndexOf('/') + 1);

		// get the animation images
		List<String> uris = parser.getStringList(ParserKeys.icon);
		imageURIs = new String[uris.size()];
		for(int i = 0; i < uris.size(); ++i)
		{
			imageURIs[i] = file + uris.get(i);
		}

		// get the display times from the config file
		List<String> times = parser.getStringList(ParserKeys.displayTime);
		displayTimes = new double[times.size()];
		for(int i = 0; i < times.size(); ++i)
		{
			displayTimes[i] = new Double(times.get(i)).doubleValue();
		}

		// load images
		for(String uri : imageURIs)
		{
			try
			{
				ImageDrawer.getInstance().addImage(uri, mapItemURI, width, height);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Returns the image to be displayed at the current game time.
	 * 
	 * @param gameTime
	 *            The time of the game in seconds.
	 * @param creationTime
	 *            The start time of the animation in milliseconds
	 * @return The string URI associated with the image to be used at the
	 *         current game time.
	 */
	public String getImage(double gameTime, double creationTime)
	{
		if(imageURIs.length == 1)
			return imageURIs[0];

		double curTime = (gameTime * 1000) - creationTime;

		double sum = 0;
		for(int i = 0; i < displayTimes.length; i++)
		{
			sum += displayTimes[i];
		}

		double time = curTime % sum;

		int i;
		for(i = 0; i < displayTimes.length; i++)
		{
			if(time > displayTimes[i])
			{
				time -= displayTimes[i];
			}
			else
			{
				return imageURIs[i];
			}
		}

		// this should never happen...but........
		return imageURIs[i];
	}
}
