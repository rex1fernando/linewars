package linewars.display.sound;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
	private static final int SAMPLE_RATE = 44100;

	/*
	 * TODO the code in this class only supports 8 bit encoding, it will have to
	 * be modified to handle larger sizes if this number is increased.
	 */
	private static final int SAMPLE_SIZE_IN_BYTES = 2;
	
	private static final int MIN_LOOP_TIME_MS = 1;
	
	private static final double LAG_SPIKE_BUFFER_PERCENTAGE = 1.85;
	
	private static final double MIN_BUFFER_WRITE_SIZE = 1.00;

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
		loopTime = MIN_LOOP_TIME_MS;
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
		String absURI = System.getProperty("user.dir") + "/resources/sounds/" + uri;
		absURI = absURI.replace("/", File.separator);

		File file = new File(absURI);
		
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
	
	public void stop()
	{
		running = false;

		line.drain();
		line.stop();
		line.close();
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
				else
				{
					running = true;
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
		
		line.start();

		while(running)
		{
			lastTime = System.currentTimeMillis();
			
			play();

			long curTime = System.currentTimeMillis();
			long elapsedTime = curTime - lastTime;
			
			if(elapsedTime < MIN_LOOP_TIME_MS)
			{
				try
				{
					Thread.sleep(MIN_LOOP_TIME_MS);
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
				
				curTime = System.currentTimeMillis();
				elapsedTime = curTime - lastTime;
			}
			
			loopTime = (loopTime * 0.875f) + (elapsedTime * 0.125f);
		}
	}
	
	private void play()
	{
		int samples = (int)(SAMPLE_RATE * (loopTime / 1000.0f)) * Channel.values().length;
		int bytes = samples * SAMPLE_SIZE_IN_BYTES;
		
		int calculatedBytes = bytes;
		int bufferUsed = line.getBufferSize() - line.available();
		bytes = (int)(calculatedBytes * LAG_SPIKE_BUFFER_PERCENTAGE) - bufferUsed;
		
		if(bytes < calculatedBytes * MIN_BUFFER_WRITE_SIZE)
			bytes = (int)(calculatedBytes * MIN_BUFFER_WRITE_SIZE);
			
		bytes = (bytes / (SAMPLE_SIZE_IN_BYTES * Channel.values().length)) * (SAMPLE_SIZE_IN_BYTES * Channel.values().length);
		
//		System.out.println(loopTime + "\t\t" + calculatedBytes + "\t\t" + bytes + "\t\t" + bufferUsed);
		
		if(bytes <= 0)
			return;
		
		byte[] channelData = new byte[bytes];

		int index = 0;
		while(index < playing.size())
		{
			SoundPair p = playing.get(index);
			if(p == null)
				continue;
			
			Sound current = sounds.get(p.sound.getURI());
			if(current == null || p.sound.isDone() || current.isFinished(p.progress))
			{
				p.sound.setDone();
				playing.remove(index);
				continue;
			}

			byte[] dataFromSource = new byte[bytes];
			p.progress = current.getNextFrame(dataFromSource, p.progress, bytes);

			for(int i = 0; i < bytes / Channel.values().length / SAMPLE_SIZE_IN_BYTES; ++i)
			{
				for(Channel channel : Channel.values())
				{
					int c = channel.ordinal();
					int sampleNum = (i * Channel.values().length) + c;
					long channelSample = 0;
					long dataSample = 0;
					
					channelSample = channelData[((sampleNum + 1) * SAMPLE_SIZE_IN_BYTES) - 1];
					dataSample = dataFromSource[((sampleNum + 1) * SAMPLE_SIZE_IN_BYTES) - 1];
					for(int j = SAMPLE_SIZE_IN_BYTES - 2; j >= 0; --j)
					{
						int byteNum = (sampleNum * SAMPLE_SIZE_IN_BYTES) + j;
						
						channelSample = (channelSample << 8) | (channelData[byteNum] & 255L);
						dataSample = (dataSample << 8) | (dataFromSource[byteNum] & 255L);
					}
					
					channelSample = (long)((channelSample * (double)(index / (index + 1))) +
							((dataSample * p.sound.getVolume(channel)) * (double)(1 / (index + 1))));
					
					for(int j = 0; j < SAMPLE_SIZE_IN_BYTES; ++j)
					{
						int byteNum = (sampleNum * SAMPLE_SIZE_IN_BYTES) + j;
						byte toSave = (byte)(channelSample & 255L);
						channelData[byteNum] = toSave;
						channelSample = channelSample >> 8;
					}
				}
			}

			++index;
		}

		line.write(channelData, 0, channelData.length);
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
