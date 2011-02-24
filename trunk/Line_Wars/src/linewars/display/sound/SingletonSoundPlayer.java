package linewars.display.sound;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SingletonSoundPlayer implements Runnable
{
	private static final int SAMPLE_RATE = 44000;
	private static final int SAMPLE_SIZE = 16;
	private static final int CHANELS = 2;

	private static final Object instanceLock = new Object();
	private static final Object runningLock = new Object();
	private static SingletonSoundPlayer instance;

	private boolean running;

	private HashMap<String, Sound> sounds;
	private LinkedList<Pair> playing;
	private AudioFormat format;
	private SourceDataLine line;
	private long lastTime;
	private float loopTime;

	private SingletonSoundPlayer()
	{
		running = false;
		sounds = new HashMap<String, Sound>();
		playing = new LinkedList<Pair>();
		format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE, CHANELS, true, false);
		loopTime = 0.0f;
	}

	public static SingletonSoundPlayer getInstance()
	{
		if(instance == null)
		{
			synchronized(instanceLock)
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
		AudioInputStream din = AudioSystem.getAudioInputStream(format, in);
		sounds.put(uri, new Sound(din));
	}

	public void playSound(SoundPlayer playMe)
	{
		if(playMe == null)
			return;

		playing.add(new Pair(0, playMe));
	}

	@Override
	public void run()
	{
		// We only want this to run once
		if(!running)
		{
			synchronized(runningLock)
			{
				if(running)
				{
					return;
				}
			}
		}
		else
		{
			return;
		}

		try
		{
			line = getLine(format);
		}
		catch(LineUnavailableException e)
		{
			running = false;
			e.printStackTrace();
			return;
		}
		
		while(true)
		{
			lastTime = System.currentTimeMillis();

			play();

			long curTime = System.currentTimeMillis();
			long elapsedTime = curTime - lastTime;
			loopTime = (loopTime * 0.875f) + (elapsedTime * 0.125f);
		}
	}

	private void play()
	{
		int samples = (int)(SAMPLE_RATE / (loopTime / 1000));
		int bytes = samples * SAMPLE_SIZE;
		byte[] dataForStream = new byte[bytes];
		
		int index = 0;
		while(index < playing.size())
		{
			Pair p = playing.get(index);
			Sound current = sounds.get(p.sound.getURI());
			if(current == null || p.sound.finished() || current.isFinished(p.progress))
			{
				playing.remove(index);
				continue;
			}
			
			byte[] dataFromSource = new byte[bytes];
			p.progress = current.getNextFrame(dataFromSource, p.progress, bytes);

			for(int i = 0; i < bytes; ++i)
			{
				dataForStream[i] = (byte)((((int)dataForStream[i]) * index / (index + 1)) +
								   ((((int)dataFromSource[i])* p.sound.getVolume()) * 1 / (index + 1)));
			}
			
			++index;
		}
		
		line.write(dataForStream, 0, bytes);
	}

	private static SourceDataLine getLine(AudioFormat audioFormat) throws LineUnavailableException
	{
		SourceDataLine res = null;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		res = (SourceDataLine)AudioSystem.getLine(info);
		res.open(audioFormat);
		return res;
	}

	public interface SoundPlayer
	{
		/**
		 * Gets the volume of the sound.
		 * 
		 * @return a value from 1.0 to 0.0
		 */
		public double getVolume();

		/**
		 * Gets the uri of the sound.
		 * 
		 * @return the uri of the sound.
		 */
		public String getURI();

		/**
		 * Tells if the sound is done playing.
		 * 
		 * @return true if the sound is finished, false otherwise.
		 */
		public boolean finished();
	}

	private class Pair
	{
		public int progress;
		public SoundPlayer sound;

		public Pair(int progress, SoundPlayer sound)
		{
			this.progress = progress;
			this.sound = sound;
		}
	}
}
