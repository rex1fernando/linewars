package linewars.gamestate.mapItems.abilities;

import configuration.Usage;
import linewars.gamestate.Lane;
import linewars.gamestate.Wave;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemModifier;
import linewars.gamestate.mapItems.MapItemModifier.*;
import linewars.gamestate.mapItems.Unit;
import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;

public class ArmorPlatingConfiguration extends AbilityDefinition {
	
	static {
		AbilityDefinition.setAbilityConfigMapping("Armor Plating",
				ArmorPlatingConfiguration.class, AbilityStrategyEditor.class);
	}
	
	public class ArmorPlating implements Ability
	{
		private MapItem armor;
		private Unit unit;
		private MapItemModifier mod;
		
		private ArmorPlating(MapItem m)
		{
			//what unit is m a part of?
			for(Lane l : m.getGameState().getMap().getLanes())
			{
				for(Wave w : l.getWaves())
				{
					for(Unit u : w.getUnits())
					{
						if(u.getContainedItems().contains(m))
						{
							unit = u;
							break;
						}
					}
					if(unit != null)
						break;
				}
				if(unit != null)
					break;
			}
			
			if(unit != null)
			{
				mod = new MapItemModifier();
				mod.setMapping(MapItemModifiers.damageReceived, new Add(getDamageReduction()));
				unit.pushModifier(mod);
			}
		}

		@Override
		public void update() {
			//if the armor got removed from the unit
			if(unit != null && !unit.getContainedItems().contains(armor))
			{
				//well FINE! you don't get MY modifier anymore!
				unit.removeModifier(mod);
				//and I'm just going to FORGET about you!
				unit = null;
			}
		}

		@Override
		public boolean killable() {
			return true;
		}

		@Override
		public boolean finished() {
			return unit == null;
		}
		
	}
	
	public ArmorPlatingConfiguration()
	{
		super.setPropertyForName("damageReduction", new EditorProperty(Usage.NUMERIC_FLOATING_POINT, 
				null, EditorUsage.Real, "The percent damage is reduced by when being applied to the unit"));
	}
	
	private double getDamageReduction()
	{
		return (Double)super.getPropertyForName("damageReduction").getValue();
	}

	@Override
	public boolean startsActive() {
		return true;
	}

	@Override
	public Ability createAbility(MapItem m) {
		return new ArmorPlating(m);
	}

	@Override
	public String getName() {
		return "Armor Plating";
	}

	@Override
	public String getDescription() {
		return "Adds armor plating to the map item";		
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof ArmorPlatingConfiguration) &&
				((ArmorPlatingConfiguration) o).getDamageReduction() == getDamageReduction();
	}

}
