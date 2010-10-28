package linewars.display;

import java.io.IOException;

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
	private String[] imageURIs;
	private double[] displayTimes;

	public Animation(Parser parser, String unitURI, int width, int height)
	{
		//get the file the animation is in
		String file =  "/" + parser.getConfigFile().getURI();
		file = file.substring(0, file.lastIndexOf('/') + 1);
		
		//get the animation images
		String[] uris = parser.getList(ParserKeys.icon);
		imageURIs = new String[uris.length];
		for(int i = 0; i < uris.length; ++i)
		{
			imageURIs[i] = file + uris[i];
		}

		//get the display times from the config file
		String[] times = parser.getList(ParserKeys.displayTime);
		displayTimes = new double[times.length];
		for(int i = 0; i < times.length; ++i)
		{
			displayTimes[i] = new Double(times[i]).doubleValue();
		}
		
		//load images
		for(String uri : imageURIs)
		{			
			try
			{
				MapItemDrawer.getInstance().addImage(uri, unitURI, width, height);
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
	 *            The time of the game.
	 * @param creationTime TODO
	 * 
	 * @return The string URI associated with the image to be used at the
	 *         current game time.
	 */
	public String getImage(double gameTime, double creationTime)
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
