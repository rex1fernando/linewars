package linewars.display;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
	/**
	 * 
	 */
	private static final long serialVersionUID = -9116359081134456270L;

	public Animation getAnimation(MapItemState state)
	{
		if(super.getPropertyForName(state.toString()) == null || 
				super.getPropertyForName(state.toString()).getValue() == null)
			return null;
		else
			return ((DisplayState)super.getPropertyForName(state.toString()).getValue()).getAnimation();
	}

	public void setAnimation(MapItemState state, Animation a)
	{
		if(!getDefinedStates().contains(state))
		{
			super.setPropertyForName(state.toString(), new Property(Usage.CONFIGURATION, new DisplayState()));
		}
		
		((DisplayState)super.getPropertyForName(state.toString()).getValue()).setAnimation(a);
	}

	public String getSound(MapItemState state)
	{
		if(super.getPropertyForName(state.toString()) == null || 
				super.getPropertyForName(state.toString()).getValue() == null)
			return null;
		else
			return ((DisplayState)super.getPropertyForName(state.toString()).getValue()).getSound();
	}

	public void setSound(MapItemState state, String s)
	{
		if(!getDefinedStates().contains(state))
		{
			super.setPropertyForName(state.toString(), new Property(Usage.CONFIGURATION, new DisplayState()));
		}
		
		((DisplayState)super.getPropertyForName(state.toString()).getValue()).setSound(s);
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
	
	private class DisplayState extends Configuration
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -2786012930654678416L;

		public DisplayState()
		{
			setAnimation(null);
			setSound(null);
		}

		public Animation getAnimation()
		{
			return (Animation)super.getPropertyForName("Animation").getValue();
		}

		public void setAnimation(Animation a)
		{
			super.setPropertyForName("Animation", new Property(Usage.ANIMATION, a));
		}

		public String getSound()
		{
			return (String)super.getPropertyForName("Sound").getValue();
		}

		public void setSound(String s)
		{
			super.setPropertyForName("Sound", new Property(Usage.STRING, s));
		}
	}
}
