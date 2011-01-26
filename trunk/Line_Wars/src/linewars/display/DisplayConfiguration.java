package linewars.display;

import linewars.gamestate.mapItems.MapItemState;
import configuration.Configuration;
import configuration.Property;
import configuration.Usage;

public class DisplayConfiguration extends Configuration {
	
	public Animation getAnimation(MapItemState state)
	{
		return (Animation)super.getPropertyForName(state.toString()).getValue();
	}
	
	public void setAnimation(MapItemState state, Animation a)
	{
		super.setPropertyForName(state.toString(), new Property(Usage.ANIMATION, a));
	}

}
