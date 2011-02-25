package linewars.display.sound;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundPlayer implements Runnable
{
	private static final int SAMPLE_RATE = 44000;

	/*
	 * TODO the code in this class only supports 8 bit encoding, it will have to
	 * be modified to handle larger sizes if this number is increased.
	 */
	private static final int SAMPLE_SIZE_IN_BYTES = 1;

	/*
	 * TODO if the channels are changed then the sound managers may need to
	 * change the way that they calculate their volume.
	 */
	public enum Channel
	{
		LEFT, RIGHT
	}

	private static final Object instanceLock = new Object();
	private static final Object runningLock = new Object();
	private static SoundPlayer instance;

	private boolean running;

	private HashMap<String, Sound> sounds;
	private LinkedList<SoundPair> playing;
	private AudioFormat format;
	private SourceDataLine line;
	private long lastTime;
	private float loopTime;

	private SoundPlayer()
	{
		running = false;
		sounds = new HashMap<String, Sound>();
		playing = new LinkedList<SoundPair>();
		format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_IN_BYTES * 8, Channel.values().length, true, false);
		loopTime = 0.0f;
	}

	public static SoundPlayer getInstance()
	{
		if(instance == null)
		{
			synchronized(instanceLock)
			{
				if(instance == null)
				{
					instance = new SoundPlayer();
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

	public void playSound(SoundInfo playMe)
	{
		if(playMe == null)
			return;

		playing.add(new SoundPair(0, playMe));
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
		// TODO add in channel info

		int samples = (int)(SAMPLE_RATE / (loopTime / 1000));
		int bytes = samples * SAMPLE_SIZE_IN_BYTES;
		byte[][] channelData = new byte[Channel.values().length][bytes];

		int index = 0;
		while(index < playing.size())
		{
			SoundPair p = playing.get(index);
			Sound current = sounds.get(p.sound.getURI());
			if(current == null || p.sound.isDone() || current.isFinished(p.progress))
			{
				p.sound.setDone();
				playing.remove(index);
				continue;
			}

			byte[][] dataFromSource = new byte[Channel.values().length][bytes];
			p.progress = current.getNextFrame(dataFromSource, p.progress, bytes);

			// TODO this loop will have to be modified if we choose to use an
			// encoding larger than 8 bits.
			for(Channel channel : Channel.values())
			{
				int c = channel.ordinal();
				for(int i = 0; i < bytes; ++i)
				{
					channelData[c][i] = (byte)((((int)channelData[c][i]) * index / (index + 1)) +
										((((int)dataFromSource[c][i]) * p.sound.getVolume(channel)) * 1 / (index + 1)));
				}
			}

			++index;
		}

		byte[] dataForStream = new byte[channelData.length * bytes];
		for(int i = 0; i < dataForStream.length; ++i)
			dataForStream[i] = channelData[i % channelData.length][i / channelData.length];
		
		line.write(dataForStream, 0, channelData.length * bytes);
	}

	private static SourceDataLine getLine(AudioFormat audioFormat) throws LineUnavailableException
	{
		SourceDataLine res = null;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		res = (SourceDataLine)AudioSystem.getLine(info);
		res.open(audioFormat);
		return res;
	}

	private class SoundPair
	{
		public int progress;
		public SoundInfo sound;

		public SoundPair(int progress, SoundInfo sound)
		{
			this.progress = progress;
			this.sound = sound;
		}
	}
}
