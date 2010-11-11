package linewars.network.messages;

import linewars.gamestate.GameState;

public class AdjustFlowDistributionMessage extends Message
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6075489305070858963L;
	private int laneID;
	private double flow;
	private int nodeID;
	
	public AdjustFlowDistributionMessage(int pID, int laneID, double flow, int nodeID) {
		super(pID);
		
		this.nodeID = nodeID;
		this.laneID = laneID;
		this.flow = flow;
	}

	@Override
	public void apply(GameState gameState) {
		gameState.getPlayer(this.getPlayerId()).setFlowDist(gameState.getMap().getLanes()[laneID], flow, nodeID);		
	}

}
