package linewars.display;

import java.io.FileNotFoundException;
import java.io.IOException;

import linewars.gamestate.mapItems.MapItemDefinition;
import linewars.gamestate.shapes.ShapeAggregate;
import linewars.parser.ConfigFile;
import linewars.parser.Parser;
import linewars.parser.Parser.InvalidConfigFileException;
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

	public Animation(Parser parser, int width, int height)
	{
		//get the animation images
		imageURIs = parser.getList(ParserKeys.icon);

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
				MapItemDrawer.getInstance().addImage(uri, width, height);
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
