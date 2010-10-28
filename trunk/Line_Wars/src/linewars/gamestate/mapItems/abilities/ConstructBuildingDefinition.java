package linewars.gamestate.mapItems.abilities;

import linewars.gamestate.mapItems.BuildingDefinition;
import linewars.gamestate.mapItems.CommandCenter;
import linewars.gamestate.mapItems.CommandCenterDefinition;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemDefinition;
import linewars.parser.ParserKeys;

/**
 * 
 * @author cschenck
 *
 * This class represents the definition for an ability that constructs
 * a building. Knows what buiding it contructs.
 */
public class ConstructBuildingDefinition extends AbilityDefinition {
	
	private BuildingDefinition buildingDefinition = null;
	
	public ConstructBuildingDefinition(BuildingDefinition bd, MapItemDefinition owner, int ID)
	{
		super(ID);
		buildingDefinition = bd;
		this.owner = owner;
	}

	@Override
	public boolean startsActive() {
		return false;
	}

	@Override
	public Ability createAbility(MapItem m) {
		if(!(m instanceof CommandCenter))
			throw new IllegalArgumentException("The input argument m must be a CommandCenter.");
		
		return new ConstructBuilding(((CommandCenter)m).getNode(), buildingDefinition);
	}

	@Override
	public boolean unlocked() {
		return true;
	}

	@Override
	public String getName() {
		return "Construct Building: " + buildingDefinition.getName();
	}

	@Override
	public String getDescription() {
		return "Constructs the building " + buildingDefinition.getName() + 
		". Costs " + buildingDefinition.getCost() + ". Takes " +
		(buildingDefinition.getBuildTime()/1000.0) + " seconds to build."; 
	}

	@Override
	public boolean equals(Object o) {
		if(!(o instanceof ConstructBuildingDefinition))
			return false;
		else
			return buildingDefinition.equals(((ConstructBuildingDefinition)o).buildingDefinition);
	}

	@Override
	public boolean checkValidity() {
		return (buildingDefinition != null) && (owner instanceof CommandCenterDefinition);
	}

	@Override
	public String getIconURI() {
		return buildingDefinition.getParser().getStringValue(ParserKeys.icon);
	}

}
