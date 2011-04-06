package linewars.gamestate.mapItems.strategies.combat;

import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Part;
import linewars.gamestate.mapItems.PartDefinition;
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
	
	public class HealFirstAlly extends GeneralDroneCombatStrategy
	{
		private Unit lastHealTarget;
		private Part healPart;
		
		private HealFirstAlly(Unit u)
		{
			super(u);
			healPart = getHealPart().createMapItem(u.getTransformation(), u.getOwner(), u.getGameState());
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
		public boolean isFinishedOnTarget() {
			return (this.getTarget() == null
					|| this.getTarget().getState().equals(MapItemState.Dead)
					|| this.getTarget().getHP()/((UnitDefinition)this.getTarget().getDefinition()).getMaxHP() >= 0.99);
		}

		@Override
		public Unit pickBestTarget(Unit[] targets) {
			Unit picked = null;
			for(Unit u : targets)
				if(picked != null && picked.getOwner().equals(this.getDrone().getOwner()) &&
						picked.getHP()/picked.getMaxHP() > u.getHP()/u.getMaxHP())
					picked = u;
			
			return picked;
		}

		@Override
		protected void applyEffect(Unit target) {
			target.setHP(target.getHP() + hps*this.getDrone().getGameState().getLastLoopTime());
			if(target != lastHealTarget)
			{
				if(lastHealTarget != null)
					lastHealTarget.removeMapItem(healPart);
				target.addMapItemToFront(healPart, Transformation.ORIGIN);
				lastHealTarget = target;
			}
		}

	}
	
	public HealFirstAllyConfiguration()
	{
		super.setPropertyForName("hps", new EditorProperty(
				Usage.NUMERIC_FLOATING_POINT, 0, EditorUsage.PositiveReal,
				"The amount of healing done per second"));
		super.setPropertyForName("healPart", new EditorProperty(Usage.CONFIGURATION, 
				null, EditorUsage.PartConfig, "The part to add to a unit being healed"));
		this.addObserver(this);
	}
	
	public PartDefinition getHealPart()
	{
		return (PartDefinition)super.getPropertyForName("healPart").getValue();
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
