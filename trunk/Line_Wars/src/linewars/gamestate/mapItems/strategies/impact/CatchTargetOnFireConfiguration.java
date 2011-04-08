package linewars.gamestate.mapItems.strategies.impact;

import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemModifier.MapItemModifiers;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Part;
import linewars.gamestate.mapItems.PartDefinition;
import linewars.gamestate.mapItems.Projectile;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.abilities.Ability;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import configuration.Usage;
import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;

public class CatchTargetOnFireConfiguration extends ImpactStrategyConfiguration {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8183470932347599355L;
	
	static {
		StrategyConfiguration.setStrategyConfigMapping("Catch Target on Fire",
				CatchTargetOnFireConfiguration.class, AbilityStrategyEditor.class);
	}

	public class CatchTargetOnFire implements ImpactStrategy
	{
		private Projectile proj;
		private double startTime;
		
		private CatchTargetOnFire(Projectile p)
		{
			proj = p;
			startTime = proj.getGameState().getTime();
			proj.addActiveAbility(new Ability() {
				private boolean finished = false;
				@Override
				public void update() {
					if(proj.getGameState().getTime() - startTime > getDuration())
					{
						finished = true;
						proj.setState(MapItemState.Dead);
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
		}

		@Override
		public String name() {
			return "Catch target on fire";
		}

		@Override
		public ImpactStrategyConfiguration getConfig() {
			return CatchTargetOnFireConfiguration.this;
		}
		
		private boolean isOnFire(MapItem m)
		{
			for(Ability a : m.getActiveAbilities())
				if(a instanceof OnFire)
					return true;
			
			return false;
		}

		@Override
		public void handleImpact(MapItem m) {
			if(m instanceof Unit && !isOnFire(m))
			{
				final Unit u = (Unit) m;
				final Part burning = getBurningPart().createMapItem(u.getTransformation(), u.getOwner(), u.getGameState());
				u.addMapItemToFront(burning, Transformation.ORIGIN);
				u.addActiveAbility(new OnFire(u, burning));
			}
		}
		
		private class OnFire implements Ability
		{
			private double start;
			private boolean finished = false;
			private Unit unit;
			private Part burn;
			
			private OnFire(Unit u, Part burning)
			{
				this.unit = u;
				start = u.getGameState().getTime();
				burn = burning;
			}
			
			@Override
			public void update() {
				unit.setHP(unit.getHP() - getDamagePerSecond()*unit.getGameState().getLastLoopTime()*
						proj.getModifier().getModifier(MapItemModifiers.damageDealt));
				if(unit.getGameState().getTime() - start > getDuration() && !finished)
				{
					finished = true;
					unit.removeMapItem(burn);
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

		@Override
		public void handleImpact(Position p) {}
		
	}
	
	public CatchTargetOnFireConfiguration()
	{
		super.setPropertyForName("damagePerSecond", new EditorProperty(Usage.NUMERIC_FLOATING_POINT,
				null, EditorUsage.Real, "The damage dealt per second while the unit is on fire"));
		super.setPropertyForName("duration", new EditorProperty(Usage.NUMERIC_FLOATING_POINT,
				null, EditorUsage.Real, "The duration of the burning effect"));
		super.setPropertyForName("buringPart", new EditorProperty(Usage.CONFIGURATION,
				null, EditorUsage.PartConfig, "The part to add to the unit that is burning (the burning animation)"));
	}
	
	private double getDamagePerSecond()
	{
		return (Double)super.getPropertyForName("damagePerSecond").getValue();
	}
	
	private double getDuration()
	{
		return (Double)super.getPropertyForName("duration").getValue();
	}
	
	private PartDefinition getBurningPart()
	{
		return (PartDefinition)super.getPropertyForName("buringPart").getValue();
	}

	@Override
	public ImpactStrategy createStrategy(MapItem m) {
		return new CatchTargetOnFire((Projectile) m);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof CatchTargetOnFireConfiguration) &&
				((CatchTargetOnFireConfiguration) obj).getDamagePerSecond() == getDamagePerSecond() &&
				((CatchTargetOnFireConfiguration) obj).getDuration() == getDuration() &&
				((CatchTargetOnFireConfiguration) obj).getBurningPart().equals(getBurningPart());
	}

}
