package linewars.network.messages;

import linewars.gamestate.GameState;
import linewars.gamestate.mapItems.abilities.AbilityDefinition;

public class BuildMessage extends Message
{

	private int nodeID;
	private int abilityID;
	
	public BuildMessage(int pID, int nodeID, int abilityID) {
		super(pID);
		
		this.nodeID = nodeID;
		this.abilityID = abilityID;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4630399012107084114L;

	@Override
	public void apply(GameState gameState) {
		AbilityDefinition ad = gameState.getMap().getNodes()[nodeID].getCommandCenter().getAvailableAbilities()[abilityID];	
		gameState.getMap().getNodes()[nodeID].getCommandCenter()
				.addActiveAbility(
						ad.createAbility(gameState.getMap().getNodes()[nodeID]
								.getCommandCenter()));
	}

}
