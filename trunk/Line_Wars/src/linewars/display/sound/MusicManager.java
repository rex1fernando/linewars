package linewars.display.sound;

import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.UnsupportedAudioFileException;

import linewars.display.sound.SoundPlayer.Channel;
import linewars.display.sound.SoundPlayer.SoundType;
import linewars.gamestate.GameState;

public class MusicManager
{
	private static final long SONG_PAUSE_TIME = 120000;
	
	private ArrayList<String> songList;
	private MusicInfo playing;
	private int songIndex;
	private long songPauseStartTime = -1;
	
	public MusicManager(String[] songs)
	{
		songList = new ArrayList<String>();
		songList.add("Guitar_Reduced_Volume1.wav");
		songList.add("GameSong2.wav");
		playing = null;
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
	
	public void play(GameState gamestate)
	{
		//TODO implement a more intelligent song selector
		
		if(playing == null)
		{
			playing = new MusicInfo(songList.get(songIndex));
			SoundPlayer.getInstance().playSound(playing);
			
			songIndex = (songIndex + 1) % songList.size();
		}
		
		if(playing.isDone() && System.currentTimeMillis() - songPauseStartTime > SONG_PAUSE_TIME)
		{
			playing = new MusicInfo(songList.get(songIndex));
			SoundPlayer.getInstance().playSound(playing);
			
			songIndex = (songIndex + 1) % songList.size();
			songPauseStartTime = -1;
		}
		else if(playing.isDone() && songPauseStartTime < 0)
			songPauseStartTime = System.currentTimeMillis();
			
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
			return 1.0;
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
