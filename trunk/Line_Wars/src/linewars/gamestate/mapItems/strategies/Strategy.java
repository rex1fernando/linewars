package linewars.gamestate.mapItems.strategies;


public interface Strategy<T extends StrategyConfiguration<?>> {
	
	/**
	 * Returns the name of this collision strategy
	 * 
	 * @return		the name of this collision strategy
	 */
	public String name();
	
	public T getConfig();

}
