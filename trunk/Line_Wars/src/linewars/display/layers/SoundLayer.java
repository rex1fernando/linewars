package linewars.display.layers;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import linewars.display.sound.LocalSoundEffectManager;
import linewars.display.sound.MusicManager;
import linewars.gamestate.GameState;

public class SoundLayer implements ILayer
{
	private MusicManager music;
	private LocalSoundEffectManager localEffects;
	
	private double volume;
	
	public SoundLayer(String[] songs)
	{
		music = new MusicManager(songs);
		localEffects = new LocalSoundEffectManager();
		
		volume = 1.0;
	}
	
	public void setVolume(double vol)
	{
		if(vol < 0.0)
			vol = 0.0;
		else if(vol > 1.0)
			vol = 1.0;
		
		volume = vol;
	}
	
	@Override
	public void draw(Graphics g, GameState gamestate, Rectangle2D visibleScreen, double scale)
	{
		music.play(gamestate, volume);
		localEffects.play(gamestate, visibleScreen, volume);
	}
}
