package linewars.gamestate.mapItems.abilities;

import java.util.Observable;
import java.util.Observer;

import linewars.gamestate.GameState;
import linewars.gamestate.Player;
import linewars.gamestate.mapItems.MapItem;
import configuration.*;
import editor.abilities.EditorProperty;
import editor.abilities.EditorUsage;

/**
 * 
 * @author Connor Schenck
 * 
 * This class is the definition for the generate stuff ability. It
 * crteates new generate stuff abilities.
 *
 */
public strictfp class GenerateStuffDefinition extends AbilityDefinition implements Observer {

	private double stuffIncome;
	
	public class GenerateStuff implements Ability {
		
		private Player owner;
		private double startTime;
		
		private GenerateStuff(Player p)
		{
			owner = p;
			startTime = p.getGameState().getTime();
		}

		@Override
		public void update() {
			owner.addStuff((owner.getGameState().getTime() - startTime)*getStuffIncome());
			startTime = owner.getGameState().getTime();
		}

		@Override
		public boolean killable() {
			return true;
		}

		@Override
		public boolean finished() {
			return false;
		}

	}
	
	public GenerateStuffDefinition() {
		super.setPropertyForName("stuffIncome", new EditorProperty(Usage.NUMERIC_FLOATING_POINT, 
				null, EditorUsage.PositiveReal, "The amount of stuff generated per second"));
		super.addObserver(this);
	}
	
	/**
	 * 
	 * @return	the amount of stuff generated per second
	 */
	public double getStuffIncome() {
		return stuffIncome;
	}

	@Override
	public boolean startsActive() {
		return true;
	}

	@Override
	public String getName() {
		return "Generate Stuff";
	}

	@Override
	public String getDescription() {
		return "Generates " + stuffIncome + " per second";
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof GenerateStuffDefinition)
		{
			GenerateStuffDefinition gsd = (GenerateStuffDefinition) o;
			return gsd.stuffIncome == stuffIncome;
		}
		return false;
	}

	@Override
	public void update(Observable o, Object arg) {
		if(o == this && arg.equals("stuffIncome"))
			stuffIncome = (Double)super.getPropertyForName("stuffIncome").getValue();
	}

	@Override
	public Ability createAbility(MapItem m) {
		return new GenerateStuff(m.getOwner());
	}	

}
