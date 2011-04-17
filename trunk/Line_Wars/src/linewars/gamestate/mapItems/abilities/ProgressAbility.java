package linewars.gamestate.mapItems.abilities;

public interface ProgressAbility {
	
	/**
	 * Returns the progress of the ability towards completion.
	 * 
	 * @return a value in the range 0-1
	 */
	public double getProgress();

}
