package linewars.gamestate.mapItems;

import linewars.gamestate.Node;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.abilities.Ability;
import linewars.gamestate.mapItems.abilities.ResearchTechDefinition;

/**
 * 
 * @author , Connor Schenck
 *
 * This class represents the command center, or the central
 * building in a node. It is a type of building.
 */
public strictfp class CommandCenter extends Building {

	public CommandCenter(Transformation t, CommandCenterDefinition def, Node n) {
		super(t, def, n);
	}

}
