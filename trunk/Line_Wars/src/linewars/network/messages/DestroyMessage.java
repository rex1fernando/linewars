package linewars.network.messages;

import linewars.gamestate.GameState;
import linewars.gamestate.Node;
import linewars.gamestate.Player;
import linewars.gamestate.mapItems.BuildingDefinition;

public class DestroyMessage extends Message
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6815037516993206628L;
	
	private int nodeID;
	private int buildingID;

	public DestroyMessage(int pID, int nodeID, int buildingID)
	{
		super(pID);
		
		this.nodeID = nodeID;
		this.buildingID = buildingID;
	}

	@Override
	public void apply(GameState gameState)
	{
		Node n = gameState.getMap().getNodes()[nodeID];
		Player p = gameState.getPlayer(this.getPlayerId());
		if(buildingID < 0)
			return;
		BuildingDefinition bd = p.getRace().getAllBuildings().get(buildingID);
		for(int i = 0; i < n.getContainedBuildings().length; i++)
		{
			if(bd.equals(n.getContainedBuildings()[i].getDefinition()))
			{
				n.removeBuilding(i);
				break;
			}
		}
	}
}
