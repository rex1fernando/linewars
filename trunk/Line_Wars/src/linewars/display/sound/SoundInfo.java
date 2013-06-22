package linewars.display.sound;

import linewars.display.sound.SoundPlayer.Channel;
import linewars.display.sound.SoundPlayer.SoundType;

public abstract class SoundInfo {
	private boolean isDone;

	public SoundInfo() {
		isDone = false;
	}

	/**
	 * Tells if the sound is done playing.
	 * 
	 * @return true if the sound is finished, false otherwise.
	 */
	public void setDone() {
		isDone = true;
	}

	public boolean isDone() {
		return isDone;
	}

	/**
	 * Gets the volume of the sound.
	 * 
	 * @param c
	 *            The channel to get the volume for.
	 * 
	 * @return a value from 1.0 to 0.0
	 */
	public abstract double getVolume(Channel c);

	/**
	 * Gets the sound type of the sound.
	 * 
	 * @return the type of this sound.
	 */
	public abstract SoundType getType();

	/**
	 * Gets the uri of the sound.
	 * 
	 * @return the uri of the sound.
	 */
	public abstract String getURI();
}
