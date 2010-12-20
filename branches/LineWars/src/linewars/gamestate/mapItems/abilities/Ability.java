package linewars.gamestate.mapItems.abilities;

/**
 * 
 * @author cschenck
 *
 * This class represents an ability that is currently active
 * on its mapItem.
 */
public interface Ability {
	
	/**
	 * This method tells the ability to update itself for one tick of the
	 * game loop.
	 */
	public void update();
	
	/**
	 * If this ability can be "killed" right now (i.e. if it doesn't have
	 * any buiseness which it must finish to maintain logical consistency
	 * within the game state)
	 *
	 * @return	whether or not this ability can be killed right now.
	 */
	public boolean killable();
	
	/**
	 * If this ability is finished doing whatever it needs to do. This is
	 * different than killable because an ability may not be finished, but
	 * it may be able to stop without causing problems in the game state.
	 * 
	 * @return	whether or not this ability is finished
	 */
	public boolean finished();

}
