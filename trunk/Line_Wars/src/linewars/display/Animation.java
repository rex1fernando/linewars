package linewars.display;

import java.io.Serializable;
import java.util.ArrayList;

import configuration.Configuration;

/**
 * Encapsulates animation information.
 * 
 * @author Titus Klinge
 * @author Ryan Tew
 */
public class Animation extends Configuration
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5225248279157492602L;
	
	private ArrayList<String> imageURIs;
	private ArrayList<Double> displayTimes;
	private int size;
	private Animation next;
	
	public Animation()
	{
		imageURIs = new ArrayList<String>();
		displayTimes = new ArrayList<Double>();
		size = 0;
		next = null;
	}
	
	public void addFrame(String uri, double time)
	{
		addFrame(uri, time, size);
	}
	
	public void addFrame(String uri, double time, int index)
	{
		imageURIs.add(index, uri);
		displayTimes.add(index, time);
		++size;
	}
	
	public void moveFrame(int from, int to)
	{
		String uri = imageURIs.remove(from);
		double time = displayTimes.remove(from);
		
		imageURIs.add(to, uri);
		displayTimes.add(to, time);
	}
	
	public void setNextAnimation(Animation next)
	{
		this.next = next;
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
		//get the time the animation has been alive
		double curTime = (gameTime * 1000) - creationTime;

		//get the time length of the animation
		double sum = 0;
		for(int i = 0; i < displayTimes.size(); i++)
		{
			sum += displayTimes.get(i);
		}
		
		if(sum < curTime && next != null)
			return next.getImage(gameTime, creationTime + sum);

		//if there is only one image return that
		if(size == 1)
			return imageURIs.get(0);

		//get the time within the current animation loop
		double time = curTime % sum;

		//find and return the correct image
		int i;
		for(i = 0; i < displayTimes.size(); i++)
		{
			if(time > displayTimes.get(i))
			{
				time -= displayTimes.get(i);
			}
			else
			{
				return imageURIs.get(i);
			}
		}

		// this should never happen...but........
		return imageURIs.get(i);
	}
	
	public String getImage(int index)
	{
		return imageURIs.get(index);
	}
	
	public double getImageTime(int index)
	{
		return displayTimes.get(index);
	}
	
	public int getNumImages()
	{
		return imageURIs.size();
	}
}
