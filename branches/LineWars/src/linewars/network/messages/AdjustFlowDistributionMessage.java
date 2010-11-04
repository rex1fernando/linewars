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
	
	public AdjustFlowDistributionMessage(int pID, int ts, int laneID, double flow) {
		super(pID, ts);
		
		this.laneID = laneID;
		this.flow = flow;
	}

	@Override
	public void apply(GameState gameState) {
		gameState.getPlayer(this.getPlayerId()).setFlowDist(gameState.getMap().getLanes()[laneID], flow);		
	}

}
