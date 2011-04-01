package linewars.network.messages;

import linewars.gamestate.GameState;
import linewars.gamestate.Player;
import linewars.gamestate.Position;

public class PlayerAbilityMessage extends Message {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4758021008848918453L;
	private int abilityID;
	private Position pos;
	
	/**
	 * Takes in a player ID and an ability ID (the position of the ability
	 * to apply in the list of all abilites for the player).
	 * 
	 * NOTE: show the list of unlocked player abilities in the display,
	 * and then when getting the abilityID, use the equals method to
	 * find that ability in the full list of player abilities, and send
	 * that index.
	 * 
	 * @param pID
	 * @param abilityID
	 */
	public PlayerAbilityMessage(int pID, int abilityID) {
		this(pID, abilityID, null);
	}

	/**
	 * Takes in a player ID and an ability ID (the position of the ability
	 * to apply in the list of abilites for the player), and a position
	 * to apply the ability at.
	 * 
	 * @param pID
	 * @param abilityID
	 * @param p
	 */
	public PlayerAbilityMessage(int pID, int abilityID, Position p) {
		super(pID);
		this.abilityID = abilityID;
		pos = p;
	}

	@Override
	public void apply(GameState gameState) {
		Player p = gameState.getPlayer(this.getPlayerId());
		p.getAllPlayerAbilities().get(abilityID).apply(pos, p);
	}

}
