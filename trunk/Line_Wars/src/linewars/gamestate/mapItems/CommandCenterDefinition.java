package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;

import linewars.gamestate.Player;
import linewars.gamestate.Tech;
import linewars.gamestate.mapItems.abilities.ConstructBuildingDefinition;
import linewars.gamestate.mapItems.abilities.ResearchTechDefinition;
import linewars.parser.Parser.InvalidConfigFileException;

public class CommandCenterDefinition extends BuildingDefinition {

	public CommandCenterDefinition(String URI, Player owner)
			throws FileNotFoundException, InvalidConfigFileException {
		super(URI, owner);
		abilities.clear();
		
		BuildingDefinition[] bds = owner.getBuildingDefintions();
		for(BuildingDefinition b : bds)
			abilities.add(new ConstructBuildingDefinition(b, this));
		
		Tech[] techs = owner.getTech();
		for(Tech t : techs)
			abilities.add(new ResearchTechDefinition(t, this));
	}
	
	void removeTech(ResearchTechDefinition rtd)
	{
		abilities.remove(rtd);
	}

}
