package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;

import linewars.gamestate.GameState;
import linewars.gamestate.Player;
import linewars.gamestate.Tech;
import linewars.gamestate.mapItems.abilities.ConstructBuildingDefinition;
import linewars.gamestate.mapItems.abilities.ResearchTechDefinition;
import linewars.parser.Parser.InvalidConfigFileException;

/**
 * 
 * @author cschenck
 *
 * This class is the definition of the command center building.
 * It is responsible for creating command centers. It also
 * takes all the buildings and tech's and creates abilitydefinitions
 * for creating/researching them that the command center gets
 * as part of its list of abilities.
 */
public class CommandCenterDefinition extends BuildingDefinition {

	public CommandCenterDefinition(String URI, Player owner, GameState gameState)
			throws FileNotFoundException, InvalidConfigFileException {
		super(URI, owner, gameState);
		
		BuildingDefinition[] bds = owner.getBuildingDefintions();
		for(BuildingDefinition b : bds)
			abilities.add(new ConstructBuildingDefinition(b, this));
		
		Tech[] techs = owner.getTech();
		for(Tech t : techs)
			abilities.add(new ResearchTechDefinition(t, this));
	}
	
	/**
	 * Removes the given tech from this command center's list
	 * of researchable techs.
	 * 
	 * @param rtd	the tech ability to remove
	 */
	void removeTech(ResearchTechDefinition rtd)
	{
		abilities.remove(rtd);
	}

}
