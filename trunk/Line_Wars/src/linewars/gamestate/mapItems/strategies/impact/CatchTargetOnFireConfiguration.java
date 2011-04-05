package linewars.gamestate.mapItems.strategies.impact;

import configuration.Usage;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;
import linewars.gamestate.mapItems.MapItem;

public class CatchTargetOnFireConfiguration extends ImpactStrategyConfiguration {
	
	public CatchTargetOnFireConfiguration()
	{
		super.setPropertyForName("damagePerSecond", new EditorProperty(Usage.NUMERIC_FLOATING_POINT,
				null, EditorUsage.Real, "The damage dealt per second while the unit is on fire"));
		super.setPropertyForName("duration", new EditorProperty(Usage.NUMERIC_FLOATING_POINT,
				null, EditorUsage.Real, "The duration of the burning effect"));
		super.setPropertyForName("buringPart", new EditorProperty(Usage.CONFIGURATION,
				null, EditorUsage.Real, "The part to add to the unit that is burning (the burning animation)"));
	}
	
	private double getDamagePerSecond()
	{
		return (Double)super.getPropertyForName("damagePerSecond").getValue();
	}
	
	private double getDuration()
	{
		return (Double)super.getPropertyForName("duration").getValue();
	}
	
	private double getBurningPart()
	{
		return (Double)super.getPropertyForName("buringPart").getValue();
	}

	@Override
	public ImpactStrategy createStrategy(MapItem m) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

}
