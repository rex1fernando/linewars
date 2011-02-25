package linewars.display;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import linewars.display.sound.Sound;
import linewars.gamestate.Position;
import linewars.gamestate.mapItems.MapItemState;
import configuration.Configuration;
import configuration.Property;
import configuration.Usage;

//TODO Ryan I changed how the sounds are saved, you were overwriting the animations
//also I added the dimensions of the map item in here, use these dimensions to draw
//the map item to the screen (this is the size of the box in game units the images
//should be drawn in)
public class DisplayConfiguration extends Configuration
{
	public Animation getAnimation(MapItemState state)
	{
		return (Animation)super.getPropertyForName(state.toString() + "Animation").getValue();
	}

	public void setAnimation(MapItemState state, Animation a)
	{
		super.setPropertyForName(state.toString() + "Animation", new Property(Usage.ANIMATION, a));
	}

	public Sound getSound(MapItemState state)
	{
		return (Sound)super.getPropertyForName(state.toString() + "Sound").getValue();
	}

	public void setSound(MapItemState state, Sound s)
	{
		super.setPropertyForName(state.toString() + "Sound", new Property(Usage.SOUND, s));
	}

	public List<MapItemState> getDefinedStates()
	{
		List<MapItemState> ret = new ArrayList<MapItemState>();
		Set<String> definedKeys = super.getPropertyNames();
		for(MapItemState mis : MapItemState.values())
			if(definedKeys.contains(mis.toString() + "Animation"))
				ret.add(mis);

		return ret;
	}
	
	public void setDimensions(Position dim)
	{
		super.setPropertyForName("dimensions", new Property(Usage.POSITION, dim));
	}
	
	public Position getDimensions()
	{
		return (Position)super.getPropertyForName("dimensions").getValue();
	}
}
