package linewars.gamestate.mapItems;

import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.abilities.Ability;
import linewars.gamestate.mapItems.abilities.ResearchTechDefinition;

public class CommandCenter extends Building {

	private CommandCenterDefinition definition;
	
	public CommandCenter(Transformation t, CommandCenterDefinition def, Node n) {
		super(t, def, n);
		definition = def;
	}

}
