package linewars.gamestate.mapItems;

public interface CombatStrategy {
	
	public void setUnit(Unit u);
	
	public CombatStrategy copy();

}
