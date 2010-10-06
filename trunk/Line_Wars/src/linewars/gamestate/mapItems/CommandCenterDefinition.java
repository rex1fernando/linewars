package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;

import linewars.gamestate.ConfigFileParser.InvalidConfigFileException;
import linewars.gamestate.mapItems.abilities.ConstructBuildingDefinition;
import linewars.gamestate.mapItems.abilities.ResearchTechDefinition;

public class CommandCenterDefinition extends BuildingDefinition {

	public CommandCenterDefinition(String URI, Player owner)
			throws FileNotFoundException, InvalidConfigFileException {
		super(URI, owner);
		abilities.clear();
		
		BuildingDefinition[] bds = owner.getBuildingDefintions();
		for(BuildingDefinition b : bds)
			abilities.add(new ConstructBuildingDefinition(b));
		
		Tech[] techs = owner.getTech();
		for(Tech t : techs)
			abilities.add(new ResearchTechDefinition(t));
	}
	
	void removeTech(ResearchTechDefinition rtd)
	{
		abilities.remove(rtd);
	}

}
