package linewars.test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import linewars.display.sound.SoundInfo;
import linewars.display.sound.SoundPlayer;
import linewars.display.sound.SoundPlayer.Channel;

public class SoundTest {
	private static final String SOUND = "Guitar_test_riff.wav";
	private static final String SOUND_2 = "Guitar_test_riff.wav";
	
	public static void main(String[] args){
//		testPlay("resources\\sounds\\" + SOUND);
		soundPlayerTest();
	}
	
	public static void testPlay(String filename) {
		try {
			File file = new File(filename);
			// Get AudioInputStream from given file.
			AudioInputStream in = AudioSystem.getAudioInputStream(file);
			AudioInputStream din = null;
			if (in != null) {
				AudioFormat baseFormat = in.getFormat();
				AudioFormat decodedFormat = new AudioFormat(
						AudioFormat.Encoding.PCM_SIGNED,
						baseFormat.getSampleRate(), 16,
						baseFormat.getChannels(), baseFormat.getChannels() * 2,
						baseFormat.getFrameRate(), false);
				// Get AudioInputStream that will be decoded by underlying
				// VorbisSPI
				din = AudioSystem.getAudioInputStream(decodedFormat, in);
				// Play now !
				byte[] data = read(din);
				play(decodedFormat, data);
//				rawplay(decodedFormat, din);
				in.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void play(AudioFormat targetFormat, byte[] data) throws LineUnavailableException {
		SourceDataLine line = getLine(targetFormat);
		if(line == null){
			return;
		}
		line.start();
		line.write(data, 0, data.length);
		
		line.drain();
		line.stop();
		line.close();	
	}

	private static void rawplay(AudioFormat targetFormat, AudioInputStream din)
			throws IOException, LineUnavailableException {
		byte[] data = new byte[4096];
		SourceDataLine line = getLine(targetFormat);
		if (line != null) {
			// Start
			line.start();
			int nBytesRead = 0, nBytesWritten = 0;
			while (nBytesRead != -1) {
				nBytesRead = din.read(data, 0, data.length);
				if (nBytesRead != -1)
					nBytesWritten = line.write(data, 0, nBytesRead);
			}
			// Stop
			line.drain();
			line.stop();
			line.close();
			din.close();
		}
	}
	
	private static byte[] read(AudioInputStream din) throws IOException{
		byte[] allData = new byte[4096];
		int offset = 0;
		
		int nBytesRead = 0;
		while(nBytesRead != -1){
			byte[] buffer = new byte[4096];
			nBytesRead = din.read(buffer, 0, buffer.length);
			if(nBytesRead != -1){
				allData = append(allData, buffer, offset);
			}
			
			offset += nBytesRead;
		}
		
		din.close();
		System.out.println(offset);
		return Arrays.copyOf(allData, 4096 * (offset / 4096 + 1));
	}

	private static byte[] append(byte[] target, byte[] source, int offset) {
		byte[] ofTheJedi = null;
		if(target.length < offset + source.length){
			ofTheJedi = new byte[2 * (source.length + offset)];
			for(int i = 0; i < target.length; i++){
				ofTheJedi[i] = target[i];
			}
		}else{
			ofTheJedi = target;
		}
		for(int i = 0; i < source.length; i++){
			ofTheJedi[i + offset] = source[i];
		}
		return ofTheJedi;
	}

	private static SourceDataLine getLine(AudioFormat audioFormat)
			throws LineUnavailableException {
		SourceDataLine res = null;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class,
				audioFormat);
		res = (SourceDataLine) AudioSystem.getLine(info);
		res.open(audioFormat);
		return res;
	}
	
	private static void soundPlayerTest()
	{
		SoundPlayer sp = SoundPlayer.getInstance();
		Thread soundplayer = new Thread(sp);
//		soundplayer.setDaemon(true);

		try
		{
			sp.addSound(SOUND);
			sp.addSound(SOUND_2);
		}
		catch (UnsupportedAudioFileException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
//		try
//		{
//			Thread.sleep(1000);
//		}
//		catch(InterruptedException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		sp.playSound(new SoundInfo() {
			
			@Override
			public double getVolume(Channel c) {
				// TODO Auto-generated method stub
				return 0.5;
			}
			
			@Override
			public String getURI() {
				// TODO Auto-generated method stub
				return SOUND_2;
			}
		});
		
		soundplayer.start();

		try
		{
			Thread.sleep(10000);
		}
		catch(InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		sp.stop();
		
		try
		{
			Thread.sleep(10000);
		}
		catch(InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
