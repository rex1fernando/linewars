package linewars.gamestate.mapItems.strategies.combat;

import utility.AugmentedMath;
import linewars.display.Animation;
import linewars.display.DisplayConfiguration;
import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemAggregate;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Projectile;
import linewars.gamestate.mapItems.ProjectileDefinition;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.abilities.Ability;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import configuration.Usage;
import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;

public strictfp class BomberCombatConfiguration extends CombatStrategyConfiguration {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5949619243777044207L;

	static {
		StrategyConfiguration.setStrategyConfigMapping("Bomber Combat",
				BomberCombatConfiguration.class, AbilityStrategyEditor.class);
	}
	
	public strictfp class BomberCombat implements CombatStrategy
	{
		private Unit bomber;
		private Unit target;
		private double lastBombTime;
		private double bombAnimationTime;
		private Ability mark;
		
		private BomberCombat(Unit u)
		{
			bomber = u;
			bombAnimationTime = 0;
			//TODO this is a hack
			bombAnimationTime = getBombTime(bomber);
		}
		
		private double getBombTime(MapItem m)
		{
			Animation bombing = ((DisplayConfiguration)m.getDefinition().getDisplayConfiguration()).getAnimation(MapItemState.Firing);
			if(bombing == null)
			{
				if(m instanceof MapItemAggregate)
				{
					for(MapItem contained : ((MapItemAggregate)m).getContainedItems())
					{
						double d = getBombTime(contained);
						if(d > 0)
							return d;
					}
				}
				return -1;
			}
			else
			{
				double d = 0;
				for(int i = 0; i < bombing.getNumImages(); i++)
					d += bombing.getImageTime(i);
				return d;
			}
		}

		@Override
		public String name() {
			return "Bomber combat strategy";
		}

		@Override
		public CombatStrategyConfiguration getConfig() {
			return BomberCombatConfiguration.this;
		}

		@Override
		public double getRange() {
			return getDropRadius();
		}
		
		private int getTargetMarks()
		{
			int marks = 0;
			for(Ability a : target.getActiveAbilities())
				if(a instanceof MarkTarget)
					marks++;
			
			return marks;
		}

		@Override
		public void fight(Unit[] availableEnemies, Unit[] availableAllies) {
			//is it time to bomb?
			if(bomber.getGameState().getTime() - lastBombTime > getCooldown())
			{
				//do i need a target?
				if(target == null || target.getState().equals(MapItemState.Dead) ||
						getTargetMarks() > 1)
				{
					if(mark != null && target != null)
						target.removeActiveAbility(mark);
					target = acquireTarget(availableEnemies);
					mark = new MarkTarget(target);
					target.addActiveAbility(mark);
				}
				
				if(target != null)
				{
					//am i in dropping bomb range of my target?
					if(target.getPosition().distanceSquared(bomber.getPosition()) < Math.pow(getDropRadius(), 2))
					{
						lastBombTime = bomber.getGameState().getTime();
						Projectile proj = getBomb().createMapItem(bomber.getTransformation(), bomber.getOwner(), bomber.getGameState());
						proj.getModifier().pushUnderStack(bomber.getModifier());
						bomber.getWave().getLane().addProjectile(proj);
						bomber.setStateIfInState(MapItemState.Idle, MapItemState.Firing);
						bomber.addActiveAbility(new Ability() {
							private double startTime = bomber.getGameState().getTime();
							private boolean finished = false;
							@Override
							public void update() {
								if(bomber.getGameState().getTime() - startTime > bombAnimationTime)
								{
									bomber.setStateIfInState(MapItemState.Firing, MapItemState.Idle);
									finished = true;
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
						});
						target = null;
					}
					else //move towards the target
					{
						Position pos = target.getPosition();
						double angle = target.getPosition().subtract(bomber.getPosition()).getAngle();
						bomber.getMovementStrategy().setTarget(new Transformation(pos, angle));
					}
				}
			}
			
			//this isn't an else so that if no target is found in the above if statement, the bomber can execute this
			if(!(bomber.getGameState().getTime() - lastBombTime > getCooldown()) || target == null) //no? okay
			{
				Transformation t = bomber.getWave().getLane().getPosition(bomber.getPositionAlongCurve());
				//first check to see if we've gone off the lane, since collision resolution isn't working for us
				if(t.getPosition().distanceSquared(bomber.getPosition()) > Math.pow(bomber.getWave().getLane().getWidth()/2, 2))
				{
					bomber.getMovementStrategy().setTarget(t);
				}
				else
				{
					double rot = t.getRotation();
					if(Math.abs(AugmentedMath.getAngleInPiToNegPi(rot - bomber.getRotation())) > Math.PI/2)
						rot += Math.PI;
					bomber.getMovementStrategy().setTarget(new Transformation(Position.getUnitVector(rot).scale(99999), rot));
				}
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
					dis = bomber.getPosition().distanceSquared(ret.getPosition());
				}
				else if(count == marks)
				{
					double d = bomber.getPosition().distanceSquared(u.getPosition());
					if(d < dis)
					{
						dis = d;
						ret = u;
					}
				}
			}
			return ret;
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
				BomberCombat.this.bomber.getState().equals(MapItemState.Dead);
			}
			
		}
		
	}
	
	
	
	public BomberCombatConfiguration()
	{
		super.setPropertyForName("cooldown", new EditorProperty(Usage.NUMERIC_FLOATING_POINT, 
				null, EditorUsage.PositiveReal, "The cooldown in seconds between dropping bombs"));
		super.setPropertyForName("dropRadius", new EditorProperty(Usage.NUMERIC_FLOATING_POINT, 
				null, EditorUsage.PositiveReal, "The radius the bomber must be within a unit to drop a bomb on it"));
		super.setPropertyForName("bomb", new EditorProperty(Usage.CONFIGURATION, 
				null, EditorUsage.ProjectileConfig, "The projectile to drop (the bomb)"));
	}
	
	public double getCooldown()
	{
		return (Double)super.getPropertyForName("cooldown").getValue();
	}
	
	public double getDropRadius()
	{
		return (Double)super.getPropertyForName("dropRadius").getValue();
	}
	
	public ProjectileDefinition getBomb()
	{
		return (ProjectileDefinition)super.getPropertyForName("bomb").getValue();
	}

	@Override
	public CombatStrategy createStrategy(MapItem m) {
		return new BomberCombat((Unit) m);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof BomberCombatConfiguration) &&
				((BomberCombatConfiguration) obj).getCooldown() == getCooldown() &&
				((BomberCombatConfiguration) obj).getDropRadius() == getDropRadius() &&
				((BomberCombatConfiguration) obj).getBomb().equals(getBomb());
	}

}
