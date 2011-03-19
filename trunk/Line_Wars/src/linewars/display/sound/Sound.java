package linewars.display.sound;

import java.io.IOException;
import java.util.Arrays;

import javax.sound.sampled.AudioInputStream;

import linewars.display.sound.SoundPlayer.Channel;

public class Sound
{
	private byte[][] data;
	
	public Sound(AudioInputStream in) throws IOException
	{
		data = read(in);
	}
	
	public boolean isFinished(int progress)
	{
		return progress >= data.length;
	}
	
	public int getNextFrame(byte[][] buffer, int progress, int size)
	{
		if(progress + size > data[0].length)
			size = data[0].length - progress;
		if(buffer[0].length < size)
			size = buffer[0].length;
		
		for(int c = 0; c < data.length; ++c)
		{
			for(int i = 0; i < size; ++i)
			{
				buffer[c][i] = data[c][progress + i];
			}
		}
		
		return progress + size;
	}

	private byte[][] read(AudioInputStream in) throws IOException
	{
		byte[][] allData = new byte[Channel.values().length][4096];
		
		int offset = 0;
		
		int nBytesRead = 0;
		while(nBytesRead != -1)
		{
			byte[] buffer = new byte[4096];
			nBytesRead = in.read(buffer, 0, buffer.length);
			if(nBytesRead != -1)
			{
				allData = append(allData, buffer, offset);
				offset += nBytesRead;
			}			
		}
		
		in.close();
		
		for(Channel c : Channel.values())
			allData[c.ordinal()] = Arrays.copyOf(allData[c.ordinal()], offset / allData.length);
		
		return allData;
	}
	
	private byte[][] append(byte[][] target, byte[] source, int offset)
	{
		byte[][] ofTheJedi = null;
		if(target[0].length < offset + source.length / target.length)
		{
			ofTheJedi = new byte[target.length][2 * (source.length + offset)];
			for(int i = 0; i < target.length; ++i)
			{
				for(int j = 0; j < target[i].length; ++j)
				{
					ofTheJedi[i][j] = target[i][j];
				}
			}
		}
		else
		{
			ofTheJedi = target;
		}
		
		for(int i = 0; i < source.length; i++)
		{
			ofTheJedi[(i + offset) % ofTheJedi.length][(i + offset) / ofTheJedi.length] = source[i];
		}
		
		return ofTheJedi;
	}
}
