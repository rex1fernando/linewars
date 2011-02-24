package linewars.display.sound;

import java.io.IOException;
import java.util.Arrays;

import javax.naming.InsufficientResourcesException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

public class Sound
{
	private AudioFormat format;
	private byte[] data;
	
	public Sound(AudioInputStream in) throws IOException
	{
		format = in.getFormat();
		data = read(in);
	}
	
	public boolean isFinished(int progress)
	{
		return progress >= data.length;
	}
	
	public int getFrameSize()
	{
		return format.getFrameSize();
	}
	
	public int getNextFrame(byte[] buffer, int progress) throws InsufficientResourcesException
	{
		int frameSize = getFrameSize();
		if(buffer.length < frameSize)
			throw new InsufficientResourcesException("The buffer cannot handle a frame of length " + frameSize);
		
		for(int i = 0; i < frameSize; ++i)
		{
			buffer[i] = data[progress + i];
		}
		
		return progress + frameSize;
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
		System.out.println(offset);
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
