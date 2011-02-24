package linewars.display.sound;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SingletonSoundPlayer
{
	private HashMap<String, Sound> sounds;
	private ArrayList<Pair> playing;
	private static SingletonSoundPlayer instance;
	private static final Object lock = new Object();
	
	private SingletonSoundPlayer()
	{
		sounds = new HashMap<String, Sound>();
		playing = new ArrayList<Pair>();
	}
	
	public static SingletonSoundPlayer getInstance()
	{
		if(instance == null)
		{
			synchronized(lock)
			{
				if(instance == null)
				{
					instance = new SingletonSoundPlayer();
				}
			}
		}
		
		return instance;
	}
	
	public void addSound(String uri) throws UnsupportedAudioFileException, IOException
	{
		File file = new File(uri);
		AudioInputStream in = AudioSystem.getAudioInputStream(file);
		sounds.put(uri, new Sound(in));
	}
	
	public void playSound(String uri)
	{
		Sound playMe = sounds.get(uri);
		if(playMe == null)
			return;
		
		playing.add(new Pair(0, playMe));
	}
	
	public void play()
	{
		for(Pair p : playing)
		{
			
		}
	}
	
	public interface SoundPlayer
	{
		/**
		 * Gets the volume of the sound.
		 * @return a value from 1.0 to 0.0
		 */
		public double getVolume();
		
		/**
		 * Gets the uri of the sound.
		 * @return the uri of the sound.
		 */
		public String getURI();
		
		/**
		 * Tells if the sound is done playing.
		 * @return true if the sound is finished, false otherwise.
		 */
		public boolean finished();
	}
	
	private class Pair
	{
		public int progress;
		public Sound sound;
		
		public Pair(int progress, Sound sound)
		{
			this.progress = progress;
			this.sound = sound;
		}
	}
}
