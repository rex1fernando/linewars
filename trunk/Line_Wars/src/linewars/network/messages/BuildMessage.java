package linewars.network.messages;

import linewars.gamestate.GameState;
import linewars.gamestate.mapItems.BuildingDefinition;
import linewars.gamestate.mapItems.abilities.ConstructBuildingDefinition;

public class BuildMessage extends Message
{

	private int nodeID;
	private int buildingID;
	
	public BuildMessage(int pID, int nodeID, int buildingID) {
		super(pID);
		
		this.nodeID = nodeID;
		this.buildingID = buildingID;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4630399012107084114L;

	@Override
	public void apply(GameState gameState) {
		BuildingDefinition bd = gameState.getPlayer(this.getPlayerId()).getRace().getAllBuildings().get(buildingID);
		ConstructBuildingDefinition cbd = new ConstructBuildingDefinition(bd);
		gameState.getMap().getNodes()[nodeID].getCommandCenter()
				.addActiveAbility(cbd.createAbility(gameState.getMap().getNodes()[nodeID].getCommandCenter()));
	}

}
