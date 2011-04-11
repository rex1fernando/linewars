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
	
	public SoundLayer(String[] songs)
	{
		music = new MusicManager(songs);
		localEffects = new LocalSoundEffectManager();
	}
	
	@Override
	public void draw(Graphics g, GameState gamestate, Rectangle2D visibleScreen, double scale)
	{
		music.play(gamestate);
		localEffects.play(gamestate, visibleScreen);
	}
}
