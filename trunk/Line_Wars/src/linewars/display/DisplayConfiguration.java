package linewars.display;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
	
	public List<MapItemState> getDefinedStates()
	{
		List<MapItemState> ret = new ArrayList<MapItemState>();
		Set<String> definedKeys = super.getPropertyNames();
		for(MapItemState mis : MapItemState.values())
			if(definedKeys.contains(mis.toString()))
				ret.add(mis);
		
		return ret;
	}

}
