package linewars.network.messages;

import linewars.gamestate.GameState;

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
		// TODO Auto-generated method stub
	}
}
