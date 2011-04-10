package linewars.display.sound;

import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.UnsupportedAudioFileException;

import linewars.display.sound.SoundPlayer.Channel;
import linewars.display.sound.SoundPlayer.SoundType;
import linewars.gamestate.GameState;

public class MusicManager
{
	private ArrayList<String> songList;
	private MusicInfo playing;
	private double volume;
	private int songIndex;
	
	public MusicManager(String[] songs)
	{
		songList = new ArrayList<String>();
		playing = null;
		volume = 1.0;
		songIndex = 0;
		
		for(int i = 0; i < songs.length; ++i)
		{
			try
			{
				SoundPlayer.getInstance().addSound(songs[i]);
			}
			catch(UnsupportedAudioFileException e)
			{
				e.printStackTrace();
				continue;
			}
			catch(IOException e)
			{
				e.printStackTrace();
				continue;
			}
			
			songList.add(songs[i]);
		}
	}
	
	public void play(GameState gamestate, double volume)
	{
		this.volume = volume;
		
		//TODO implement a more intelligent song selector
		
		if(playing == null)
		{
			playing = new MusicInfo(songList.get(songIndex));
			SoundPlayer.getInstance().playSound(playing);
			
			songIndex = (songIndex + 1) % songList.size();
		}
		
		if(playing.isDone())
		{
			playing = new MusicInfo(songList.get(songIndex));
			SoundPlayer.getInstance().playSound(playing);
			
			songIndex = (songIndex + 1) % songList.size();
		}
	}
	
	private class MusicInfo extends SoundInfo
	{
		private String song;
		
		public MusicInfo(String song)
		{
			this.song = song;
		}
		
		@Override
		public double getVolume(Channel c)
		{
			return volume;
		}
		
		@Override
		public SoundType getType()
		{
			return SoundType.MUSIC;
		}

		@Override
		public String getURI()
		{
			return song;
		}
	}
}
