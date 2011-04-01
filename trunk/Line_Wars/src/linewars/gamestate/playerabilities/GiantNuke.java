package linewars.gamestate.playerabilities;

import editor.abilitiesstrategies.AbilityStrategyEditor;
import linewars.gamestate.Player;
import linewars.gamestate.Position;
import linewars.gamestate.mapItems.abilities.AbilityDefinition;
import linewars.gamestate.mapItems.abilities.ConstructUnitDefinition;

public class GiantNuke extends PlayerAbility {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1823741141151561884L;

	static {
		PlayerAbility.setAbilityConfigMapping("Giant Nuke", GiantNuke.class, AbilityStrategyEditor.class);
	}

	@Override
	public boolean requiresPosition() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void apply(Position p, Player player) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return obj instanceof GiantNuke;
	}

}
