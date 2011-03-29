package linewars.gamestate.mapItems.strategies.combat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.UnitDefinition;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import utility.Observable;
import utility.Observer;
import configuration.Usage;
import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;

public class HealFirstAllyConfiguration extends CombatStrategyConfiguration implements Observer {
	
	static {
		StrategyConfiguration.setStrategyConfigMapping("Heal First Ally",
				HealFirstAllyConfiguration.class, AbilityStrategyEditor.class);
	}
	
	private double hps;
	
	public class HealFirstAlly implements CombatStrategy, DroneCombatStrategy
	{
		
		private Unit unit;
		private Unit beingHealed = null;
		
		private HealFirstAlly(Unit u)
		{
			unit = u;
		}

		@Override
		public String name() {
			return "Heal first ally for " + hps + "/s";
		}

		@Override
		public CombatStrategyConfiguration getConfig() {
			return HealFirstAllyConfiguration.this;
		}

		@Override
		public double getRange() {
			return Double.MAX_VALUE;
		}

		@Override
		public void fight(Unit[] availableEnemies, Unit[] availableAllies) {
			//check to see if unit needs a new healing target
			if (beingHealed == null
					|| beingHealed.getState().equals(MapItemState.Dead)
					|| !contains(availableAllies, beingHealed)
					|| beingHealed.getHP()/((UnitDefinition)beingHealed.getDefinition()).getMaxHP() >= 0.99)
			{
				if(beingHealed != null)
					unitsBeingHealed.remove(beingHealed);
					
				Arrays.sort(availableAllies, new Comparator<Unit>() {
					@Override
					public int compare(Unit o1, Unit o2) {
						double ret = o1.getHP()/((UnitDefinition)o1.getDefinition()).getMaxHP() -
						o2.getHP()/((UnitDefinition)o2.getDefinition()).getMaxHP();
						if(ret < 0)
							return -1;
						else if(ret > 0)
							return 1;
						else
							return 0;
					}
				});
				
				for(Unit u : availableAllies)
				{
					if(!contains(unitsBeingHealed, u))
					{
						beingHealed = u;
						break;
					}
				}
				
				if(beingHealed == null)
					return;
				
				unitsBeingHealed.add(beingHealed);
			}
			
			//TODO move the unit and check range
			beingHealed.setHP(beingHealed.getHP() + hps*unit.getGameState().getLastLoopTime());
		}
		
		private boolean contains(Unit[] list, Unit u)
		{
			for(Unit unit : list)
				if(unit == u)
					return true;
			
			return false;
		}
		
		private boolean contains(List<Unit> list, Unit u)
		{
			for(Unit unit : list)
				if(unit == u)
					return true;
			
			return false;
		}

		@Override
		public void setTarget(Unit target) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isFinishedOnTarget() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Unit pickBestTarget(Unit[] targets) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	public HealFirstAllyConfiguration()
	{
		super.setPropertyForName("hps", new EditorProperty(
				Usage.NUMERIC_FLOATING_POINT, 0, EditorUsage.PositiveReal,
				"The amount of healing done per second"));
		this.addObserver(this);
	}

	@Override
	public CombatStrategy createStrategy(MapItem m) {
		if(m instanceof Unit)
			return new HealFirstAlly((Unit) m);
		else
			throw new IllegalArgumentException("Only Units may have a combat strategy");
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof HealFirstAllyConfiguration) &&
				(((HealFirstAllyConfiguration)obj).hps == hps);
	}

	@Override
	public void update(Observable o, Object arg) {
		if(o == this && arg.equals("hps"))
			hps = (double)(Double)super.getPropertyForName("hps").getValue();
	}

}
