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
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2829972203266043058L;

	static {
		StrategyConfiguration.setStrategyConfigMapping("Heal First Ally",
				HealFirstAllyConfiguration.class, AbilityStrategyEditor.class);
	}
	
	private double hps;
	
	public class HealFirstAlly implements CombatStrategy, DroneCombatStrategy
	{
		
		private Unit unit;
		private Unit beingHealed = null;
		private Unit carrier;
		private int lastFightTick = 0;
		
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
			return carrier.getCombatStrategy().getRange();
		}

		@Override
		public void fight(Unit[] availableEnemies, Unit[] availableAllies) {
			lastFightTick = unit.getGameState().getTimerTick();
			if(beingHealed == null)
			{
				unit.setState(MapItemState.Idle);
				return;
			}
			
			//move the drone
			unit.getMovementStrategy().setTarget(beingHealed.getTransformation());
			//heal the target
			beingHealed.setHP(beingHealed.getHP() + hps*unit.getGameState().getLastLoopTime());
			
			unit.setState(MapItemState.Firing);
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
			beingHealed = target;
		}

		@Override
		public boolean isFinishedOnTarget() {
			return (beingHealed == null
					|| beingHealed.getState().equals(MapItemState.Dead)
					|| beingHealed.getHP()/((UnitDefinition)beingHealed.getDefinition()).getMaxHP() >= 0.99);
		}

		@Override
		public Unit pickBestTarget(Unit[] targets) {
			Unit picked = null;
			for(Unit u : targets)
				if(picked != null && picked.getOwner().equals(unit.getOwner()) &&
						picked.getHP()/picked.getMaxHP() > u.getHP()/u.getMaxHP())
					picked = u;
			
			return picked;
		}

		@Override
		public void setDroneCarrier(Unit carrier) {
			this.carrier = carrier;
		}

		@Override
		public Unit getDroneCarrier() {
			return carrier;
		}

		@Override
		public int getLastFightTick() {
			return lastFightTick;
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
