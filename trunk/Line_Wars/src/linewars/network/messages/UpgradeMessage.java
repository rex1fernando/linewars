package linewars.network.messages;

import linewars.gamestate.GameState;
import linewars.gamestate.mapItems.abilities.AbilityDefinition;

public class UpgradeMessage extends Message
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4351357585865137619L;
	private int nodeID;
	private int abilityID;
	
	public UpgradeMessage(int pID, int nodeID, int abilityID) {
		super(pID);
		
		this.nodeID = nodeID;
		this.abilityID = abilityID;
	}
	
	public int getNodeID()
	{
		return nodeID;
	}
	
	public int getAbilityID()
	{
		return abilityID;
	}

	@Override
	public void apply(GameState gameState) {
		AbilityDefinition ad = gameState.getMap().getNodes()[nodeID].getCommandCenter().getAvailableAbilities()[abilityID];	
		gameState.getMap().getNodes()[nodeID].getCommandCenter()
				.addActiveAbility(
						ad.createAbility(gameState.getMap().getNodes()[nodeID]
								.getCommandCenter()));
	}

}
