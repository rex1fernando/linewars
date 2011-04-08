package linewars.gamestate.mapItems.strategies.combat;

import utility.AugmentedMath;
import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Projectile;
import linewars.gamestate.mapItems.ProjectileDefinition;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import configuration.Usage;
import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;

public class BomberCombatConfiguration extends CombatStrategyConfiguration {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5949619243777044207L;

	static {
		StrategyConfiguration.setStrategyConfigMapping("Bomber Combat",
				BomberCombatConfiguration.class, AbilityStrategyEditor.class);
	}
	
	public class BomberCombat implements CombatStrategy
	{
		private Unit bomber;
		private Unit target;
		private double lastBombTime;
		
		private BomberCombat(Unit u)
		{
			bomber = u;
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

		@Override
		public void fight(Unit[] availableEnemies, Unit[] availableAllies) {
			//is it time to bomb?
			if(bomber.getGameState().getTime() - lastBombTime > getCooldown())
			{
				//do i need a target?
				if(target == null || target.getState().equals(MapItemState.Dead))
				{
					double dis = Double.POSITIVE_INFINITY;
					for(Unit t : availableEnemies)
					{
						double d = bomber.getPosition().distanceSquared(t.getPosition());
						if(d < dis && !t.getState().equals(MapItemState.Dead))
						{
							dis = d;
							target = t;
						}
					}
				}
				
				if(target != null)
				{
					//am i in dropping bomb range of my target?
					if(target.getPosition().distanceSquared(bomber.getPosition()) < Math.pow(getDropRadius(), 2))
					{
						lastBombTime = bomber.getGameState().getTime();
						Projectile proj = getBomb().createMapItem(bomber.getTransformation(), bomber.getOwner(), bomber.getGameState());
						bomber.getWave().getLane().addProjectile(proj);
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