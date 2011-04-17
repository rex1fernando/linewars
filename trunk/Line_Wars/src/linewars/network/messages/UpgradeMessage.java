package linewars.network.messages;

import linewars.gamestate.GameState;
import linewars.gamestate.tech.TechConfiguration;
import linewars.gamestate.tech.TechGraph.TechNode;

public class UpgradeMessage extends Message
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4351357585865137619L;
	private int techGraphID;
	private int techID;
	
	public UpgradeMessage(int pID, int TechGraphID, int techID) {
		super(pID);
		
		this.techGraphID = TechGraphID;
		this.techID = techID;
	}
	
	public int getTechGraphID()
	{
		return techGraphID;
	}
	
	public int getTechID()
	{
		return techID;
	}

	@Override
	public void apply(GameState gameState) {
		TechNode tech = gameState.getPlayer(getPlayerId()).getRace().getAllTechGraphs().get(techGraphID).getOrderedList().get(techID);
		tech.research(gameState.getPlayer(getPlayerId()));
	}

}
