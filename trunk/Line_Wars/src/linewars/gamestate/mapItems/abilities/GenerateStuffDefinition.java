package linewars.gamestate.mapItems.abilities;

import utility.Observable;
import utility.Observer;

import linewars.gamestate.Player;
import linewars.gamestate.mapItems.Building;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemModifier.MapItemModifiers;
import configuration.*;
import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;

/**
 * 
 * @author Connor Schenck
 * 
 * This class is the definition for the generate stuff ability. It
 * crteates new generate stuff abilities.
 *
 */
public strictfp class GenerateStuffDefinition extends AbilityDefinition implements Observer {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5823141978350694783L;

	static {
		AbilityDefinition.setAbilityConfigMapping("Generate Stuff", GenerateStuffDefinition.class, AbilityStrategyEditor.class);
	}

	private double stuffIncome;
	
	public strictfp class GenerateStuff implements Ability {
		
		private Player owner;
		private double startTime;
		private Building building;
		
		private GenerateStuff(Player p, Building b)
		{
			owner = p;
			startTime = p.getGameState().getTime();
			building = b;
		}

		@Override
		public void update() {
			owner.addStuff((owner.getGameState().getTime() - startTime)*
					getStuffIncome()*building.getModifier().getModifier(MapItemModifiers.buildingProductionRate));
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
		if(o == this && arg.equals("stuffIncome") && super.getPropertyForName("stuffIncome").getValue() != null)
			stuffIncome = (Double)super.getPropertyForName("stuffIncome").getValue();
	}

	@Override
	public Ability createAbility(MapItem m) {
		return new GenerateStuff(m.getOwner(), (Building) m);
	}	

}
