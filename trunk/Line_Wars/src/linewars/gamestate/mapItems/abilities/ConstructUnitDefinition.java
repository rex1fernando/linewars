package linewars.gamestate.mapItems.abilities;

import linewars.gamestate.mapItems.BuildingDefinition;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemDefinition;
import linewars.gamestate.mapItems.UnitDefinition;

public class ConstructUnitDefinition extends AbilityDefinition {
	
	private UnitDefinition unitDefinition = null;
	
	public ConstructUnitDefinition(UnitDefinition ud, MapItemDefinition owner)
	{
		unitDefinition = ud;
		this.owner = owner;
	}

	@Override
	public boolean checkValidity() {
		return (unitDefinition != null) && (this.owner instanceof BuildingDefinition);
	}

	@Override
	public boolean startsActive() {
		return true;
	}

	@Override
	public Ability createAbility(MapItem m) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean unlocked() {
		return true;
	}

	@Override
	public String getName() {
		return "Construct Unit: " + unitDefinition.getName();
	}

	@Override
	public String getDescription() {
		return "Constructs the unit " + unitDefinition.getName() + ". Starts active and repeats.";
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof ConstructUnitDefinition)
			return unitDefinition.equals(((ConstructUnitDefinition)o).unitDefinition);
		else
			return false;
	}

}
