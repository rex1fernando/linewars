package linewars.gamestate.mapItems.abilities;

import linewars.gamestate.mapItems.BuildingDefinition;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemDefinition;

public class ConstructBuildingDefinition extends AbilityDefinition {
	//TODO
	public ConstructBuildingDefinition(BuildingDefinition bd)
	{
		
	}

	@Override
	public boolean checkValidity(MapItemDefinition mid) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean startsActive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Ability createAbility(MapItem m) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int instancesOf() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean unlocked() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

}
