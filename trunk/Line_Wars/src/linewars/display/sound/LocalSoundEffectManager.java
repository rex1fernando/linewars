package linewars.display.sound;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

import linewars.display.DisplayConfiguration;
import linewars.display.layers.MapItemLayer.MapItemType;
import linewars.display.sound.SoundPlayer.Channel;
import linewars.gamestate.GameState;
import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.shapes.Rectangle;

public class LocalSoundEffectManager
{
	private HashMap<Integer, StateSoundPair> currentEffects;
	private double volume;
	
	public LocalSoundEffectManager()
	{
		currentEffects = new HashMap<Integer, StateSoundPair>();
		volume = 1.0;
	}
	
	public void play(GameState gamestate, Rectangle2D visibleScreen, double volume)
	{
		Position screenCenter = new Position(visibleScreen.getCenterX(), visibleScreen.getCenterY());
		Rectangle screenRect = new Rectangle(new Transformation(screenCenter, 0), visibleScreen.getWidth(), visibleScreen.getHeight());
		
		ArrayList<MapItem> items = new ArrayList<MapItem>();
		for(MapItemType type : MapItemType.values())
		{
			items.addAll(gamestate.getMapItemsOfType(type));
		}
		
		for(MapItem mapItem : items)
		{
			Position pos = mapItem.getPosition();
			int id = 0; //TODO get the mapitem's id
			StateSoundPair pair = currentEffects.get(id);
			
			double[] mapItemVol = new double[SoundPlayer.Channel.values().length];
			for(Channel c : Channel.values())
				mapItemVol[c.ordinal()] = mapItemVol(c, pos, screenRect);
			
			MapItemState state = mapItem.getState();
			if(pair.state != state)
			{
				//TODO do we want to cancel the current sound effect?
				pair.sound.setDone();
				
				String s = ((DisplayConfiguration)mapItem.getDefinition().getDisplayConfiguration()).getSound(state);
				if(s != null)
				{
					LocalSoundEffectInfo newEffect = new LocalSoundEffectInfo(s);
					for(Channel c : Channel.values())
						newEffect.setVolume(c, mapItemVol[c.ordinal()]);
					
					SoundPlayer.getInstance().playSound(newEffect);						
					currentEffects.put(id, new StateSoundPair(state, newEffect));
				}
			}
			else if(pair.sound.isDone())
			{
				LocalSoundEffectInfo newEffect = new LocalSoundEffectInfo(pair.sound.getURI());
				for(Channel c : Channel.values())
					newEffect.setVolume(c, mapItemVol[c.ordinal()]);
				
				SoundPlayer.getInstance().playSound(newEffect);
				currentEffects.put(id, new StateSoundPair(state, newEffect));
			}
			else
			{
				for(Channel c : Channel.values())
					pair.sound.setVolume(c, mapItemVol[c.ordinal()]);
			}
		}
	}
	
	private double mapItemVol(Channel c, Position mapItemPos, Rectangle screenRect)
	{
		//get the vector from the screen to the mapitem
		Position vectorFromScreenCenter = mapItemPos.subtract(screenRect.position().getPosition());
		
		// get the horizontal and vertical distances from the screen center
		double horizDistFromScreenCenter = vectorFromScreenCenter.scalarProjection(new Position(1, 0));
		double vertDistFromScreenCenter = vectorFromScreenCenter.scalarProjection(new Position(0, 1));
		
		//return 0 if the mapitem is further that half the screen size from the edge of the screen
		if(horizDistFromScreenCenter > screenRect.getWidth() || vertDistFromScreenCenter > screenRect.getHeight())
			return 0.0;
		
		/*
		 * TODO THIS CODE WILL HAVE TO CHANGE IF THE NUMBER OF CHANNELS CHANGES
		 */
		
		//the left and right channels are mirror images
		if(c.compareTo(Channel.LEFT) == 0)
			horizDistFromScreenCenter = -horizDistFromScreenCenter;
		
		//up and down are mirror images
		if(vertDistFromScreenCenter > 0)
			vertDistFromScreenCenter = -vertDistFromScreenCenter;
		
		double halfScreenWidth = screenRect.getWidth() / 2;
		double halfScreenHeight = screenRect.getHeight() / 2;
		
		//get the component of the volume from the vertical distance
		double vertComponent;
		if(vertDistFromScreenCenter <= halfScreenHeight)
			vertComponent = 1.0;
		else
			vertComponent = 1.0 - Math.pow(((vertDistFromScreenCenter - halfScreenHeight) / halfScreenHeight), 2);
		
		//get the component of the volume from the horizontal distance
		double horizComponent;
		if(horizDistFromScreenCenter < -halfScreenWidth)
			horizComponent = 0.0;
		else if(horizDistFromScreenCenter >= 0 && horizDistFromScreenCenter <= halfScreenWidth)
			horizComponent = 1.0;
		else if(horizDistFromScreenCenter < 0)
			horizComponent = -Math.pow((horizDistFromScreenCenter / halfScreenWidth), 2) + 1.0;
		else
			horizComponent = 1.0 - Math.pow(((horizDistFromScreenCenter - halfScreenWidth) / halfScreenWidth), 2);
		
		//multiply the two components together and return the result
		return vertComponent * horizComponent;
	}
	
	private class LocalSoundEffectInfo extends SoundInfo
	{
		private String uri;
		private double[] volume;
		
		public LocalSoundEffectInfo(String uri)
		{
			this.uri = uri;
			this.volume = new double[SoundPlayer.Channel.values().length];
			for(Channel c : Channel.values())
				volume[c.ordinal()] = 1.0;
		}
		
		public void setVolume(Channel c, double vol)
		{
			volume[c.ordinal()] = vol;
		}

		@Override
		public double getVolume(Channel c)
		{
			return volume[c.ordinal()] * LocalSoundEffectManager.this.volume;
		}

		@Override
		public String getURI()
		{
			return uri;
		}
	}
	
	private class StateSoundPair
	{
		public MapItemState state;
		public LocalSoundEffectInfo sound;

		public StateSoundPair(MapItemState state, LocalSoundEffectInfo sound)
		{
			this.state = state;
			this.sound = sound;
		}
	}
}
