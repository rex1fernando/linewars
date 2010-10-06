package linewars.gamestate.mapItems;

import linewars.gamestate.Position;
import linewars.gamestate.mapItems.abilities.Ability;
import linewars.gamestate.mapItems.abilities.ResearchTechDefinition;

public class CommandCenter extends Building {

	private CommandCenterDefinition definition;
	
	public CommandCenter(Position p, double rot, CommandCenterDefinition def, Node n) {
		super(p, rot, def, n);
		definition = def;
	}
	
	@Override
	public void addActiveAbility(Ability a)
	{
		if(a instanceof ResearchTechDefinition)
			definition.removeTech((ResearchTechDefinition) a);
		super.addActiveAbility(a);
	}

}
