package linewars.gamestate.mapItems.strategies.combat;

import configuration.Usage;
import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;
import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemModifier;
import linewars.gamestate.mapItems.MapItemModifier.MapItemModifiers;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Turret;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.abilities.Ability;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;

public class MeleeChargeOnTargetConfiguration extends
		CombatStrategyConfiguration {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2446521532908824884L;

	static {
		StrategyConfiguration.setStrategyConfigMapping("Melee Charge on Target",
				MeleeChargeOnTargetConfiguration.class, AbilityStrategyEditor.class);
	}
	
	private static final double MIN_TARGET_SWITCH_TIME = 2.5;
	
	public strictfp class MeleeChargeOnTarget implements CombatStrategy 
	{
		
		private Unit unit = null;
		private Unit target = null;
		private double timeOnTargetSinceLastInRange;
		private double lastChargeTime;
		
		private double minRange;
		private double maxRange = -1;
		
		
		/**
		 * Constructs a ShootClosestTarget object. The UnitDefinition parameter
		 * allows this strategy to get the reference to the ShootDefinition in
		 * the UnitDefinition.
		 * 
		 * @param ud	the UnitDefinition that owns the unit that owns this strategy.
		 */
		private MeleeChargeOnTarget(Unit u) 
		{
			unit = u;
		}
	
		@Override
		public double getRange() {
			calcRanges();
			return maxRange;
		}
		
		private void calcRanges()
		{
			minRange = Double.MAX_VALUE;
			maxRange = -1;
			for(Turret t : unit.getTurrets())
			{
				if(t.getTurretStrategy().getRange() > maxRange)
					maxRange = t.getTurretStrategy().getRange();
				if(t.getTurretStrategy().getRange() < minRange)
					minRange = t.getTurretStrategy().getRange();
			}
		}
		
		private Unit acquireTarget(Unit[] availableEnemies)
		{
			Unit ret = null;
			double dis = Double.POSITIVE_INFINITY;
			int marks = Integer.MAX_VALUE;
			for(Unit u : availableEnemies)
			{
				int count = 0;
				for(Ability a : u.getActiveAbilities())
				{
					if(a instanceof MarkTarget)
						++count;
				}
				if(count < marks)
				{
					ret = u;
					marks = count;
					dis = unit.getPosition().distanceSquared(ret.getPosition());
				}
				else if(count == marks)
				{
					double d = unit.getPosition().distanceSquared(u.getPosition());
					if(d < dis)
					{
						dis = d;
						ret = u;
					}
				}
			}
			timeOnTargetSinceLastInRange = unit.getGameState().getTime();
			ret.addActiveAbility(new MarkTarget(ret));
			return ret;
		}

		@Override
		public String name() {
			return "Move to the closest target";
		}

		@Override
		public CombatStrategyConfiguration getConfig() {
			return MeleeChargeOnTargetConfiguration.this;
		}
		
		private class MarkTarget implements Ability
		{
			private Unit unit;
			
			private MarkTarget(Unit u)
			{
				unit = u;
			}

			@Override
			public void update() {}

			@Override
			public boolean killable() {
				return true;
			}

			@Override
			public boolean finished() {
				return unit != target || 
				MeleeChargeOnTarget.this.unit.getState().equals(MapItemState.Dead);
			}
			
		}

		@Override
		public void fight(Unit[] availableEnemies, Unit[] availableAllies) {
			//first tell my turrets to fight
			for(Turret t : unit.getTurrets())
				t.getTurretStrategy().fight(availableEnemies, availableAllies);
			
			//next update my ranges
			calcRanges();
			
			double timeSinceInRange = unit.getGameState().getTime() - timeOnTargetSinceLastInRange;
			//if i have no target, and i can't pick one yet, return
			if(target == null && timeSinceInRange < MIN_TARGET_SWITCH_TIME)
				return;
			
			//if i have no target, and i can pick one, do so
			if(target == null && timeSinceInRange >= MIN_TARGET_SWITCH_TIME)
				target = acquireTarget(availableEnemies);
			
			//if i have a dead target, ill need a new one
			if(target.getState().equals(MapItemState.Dead))
				target = acquireTarget(availableEnemies);
			
			double disSquared = unit.getPosition().distanceSquared(target.getPosition());
			//if i have a target, but i am not in range, move towards it
			if(disSquared > minRange*minRange)
			{
				//can I charge?
				if(unit.getGameState().getTime() - lastChargeTime > getChargeCooldown())
				{//AWWWWWWWWWWWWWWW YEAHHHHHHHHHHHHHHHHH
					unit.addActiveAbility(new Charge()); //haul ass
				}
				Position pos = target.getPosition();
				double angle = pos.subtract(unit.getPosition()).getAngle();
				unit.getMovementStrategy().setTarget(new Transformation(pos, angle));
			}
			//if i have a target and i am in range, stop
			else
				timeOnTargetSinceLastInRange = unit.getGameState().getTime();
			
			//if i have a target and i havent been in range of it in a while, get a new target (don't force it)
			if(timeSinceInRange >= MIN_TARGET_SWITCH_TIME)
				target = acquireTarget(availableEnemies);
		}
		
		private class Charge implements Ability
		{
			private double chargeStartTime;
			private MapItemModifier mod;
			private boolean finished = false;
			
			private Charge()
			{
				chargeStartTime = unit.getGameState().getTime();
				mod = new MapItemModifier();
				mod.setMapping(MapItemModifiers.moveSpeed, new MapItemModifier.Add(getMoveSpeedIncrease()));
				unit.pushModifier(mod);
			}

			@Override
			public void update() {
				if(!finished && unit.getGameState().getTime() - chargeStartTime > getChargeDuration())
				{
					finished = true;
					unit.removeModifier(mod);
				}
				
			}

			@Override
			public boolean killable() {
				return true;
			}

			@Override
			public boolean finished() {
				return finished;
			}
			
		}
	}
	
	public MeleeChargeOnTargetConfiguration()
	{
		super.setPropertyForName("chargeDuration", new EditorProperty(Usage.NUMERIC_FLOATING_POINT, 
				null, EditorUsage.PositiveReal, "The time that the charge lasts for"));
		super.setPropertyForName("chargeCooldown", new EditorProperty(Usage.NUMERIC_FLOATING_POINT,
				null, EditorUsage.PositiveReal, "The cooldown of the charge"));
		super.setPropertyForName("moveSpeedIncrease", new EditorProperty(Usage.NUMERIC_FLOATING_POINT, 
				null, EditorUsage.Real, "The ratio to incerase the move speed by (e.g. a 50% increase would be 0.5)"));
	}
	
	public double getChargeDuration()
	{
		return (Double)super.getPropertyForName("chargeDuration").getValue();
	}
	
	public double getChargeCooldown()
	{
		return (Double)super.getPropertyForName("chargeCooldown").getValue();
	}
	
	public double getMoveSpeedIncrease()
	{
		return (Double)super.getPropertyForName("moveSpeedIncrease").getValue();
	}

	@Override
	public CombatStrategy createStrategy(MapItem m) {
		if(!(m instanceof Unit))
			throw new IllegalArgumentException("Only units may have combat strategies");
		
		return new MeleeChargeOnTarget((Unit)m);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof MeleeChargeOnTargetConfiguration) &&
				((MeleeChargeOnTargetConfiguration)obj).getChargeDuration() == getChargeDuration() &&
				((MeleeChargeOnTargetConfiguration)obj).getChargeCooldown() == getChargeCooldown() &&
				((MeleeChargeOnTargetConfiguration)obj).getMoveSpeedIncrease() == getMoveSpeedIncrease();
	}

}
