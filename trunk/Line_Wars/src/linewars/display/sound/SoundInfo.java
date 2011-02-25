package linewars.display.sound;

import linewars.display.sound.SoundPlayer.Channel;

public abstract class SoundInfo
{
	private boolean isDone;
	
	public SoundInfo()
	{
		isDone = false;
	}

	/**
	 * Tells if the sound is done playing.
	 * 
	 * @return true if the sound is finished, false otherwise.
	 */
	public void setDone()
	{
		isDone = true;
	}
	
	public boolean isDone()
	{
		return isDone;
	}
	
	/**
	 * Gets the volume of the sound.
	 * 
	 * @param c The channel to get the volume for.
	 * 
	 * @return a value from 1.0 to 0.0
	 */
	public abstract double getVolume(Channel c);

	/**
	 * Gets the uri of the sound.
	 * 
	 * @return the uri of the sound.
	 */
	public abstract String getURI();
}
