package linewars.display.sound;

import java.io.IOException;
import java.util.Arrays;

import javax.sound.sampled.AudioInputStream;

public class Sound
{
	private byte[] data;
	
	public Sound(AudioInputStream in) throws IOException
	{
		data = read(in);
	}
	
	public boolean isFinished(int progress)
	{
		return progress >= data.length;
	}
	
	public int getNextFrame(byte[] buffer, int progress, int size)
	{
		if(buffer.length < size)
			size = buffer.length;
		
		for(int i = 0; i < size; ++i)
		{
			buffer[i] = data[progress + i];
		}
		
		return progress + size;
	}

	private byte[] read(AudioInputStream in) throws IOException
	{
		byte[] allData = new byte[4096];
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
		return Arrays.copyOf(allData, offset);
	}
	
	private byte[] append(byte[] target, byte[] source, int offset)
	{
		byte[] ofTheJedi = null;
		if(target.length < offset + source.length)
		{
			ofTheJedi = new byte[2 * (source.length + offset)];
			for(int i = 0; i < target.length; i++)
			{
				ofTheJedi[i] = target[i];
			}
		}
		else
		{
			ofTheJedi = target;
		}
		
		for(int i = 0; i < source.length; i++)
		{
			ofTheJedi[i + offset] = source[i];
		}
		
		return ofTheJedi;
	}
}
