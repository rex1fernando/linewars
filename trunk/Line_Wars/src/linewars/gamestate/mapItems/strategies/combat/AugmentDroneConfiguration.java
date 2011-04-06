package linewars.gamestate.mapItems.strategies.combat;

import java.util.LinkedList;
import java.util.Queue;

import configuration.Usage;

import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;

import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemModifier;
import linewars.gamestate.mapItems.MapItemModifier.MapItemModifierType;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Part;
import linewars.gamestate.mapItems.PartDefinition;
import linewars.gamestate.mapItems.UnitDefinition;
import linewars.gamestate.mapItems.MapItemModifier.MapItemModifiers;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import linewars.gamestate.mapItems.Unit;

public class AugmentDroneConfiguration extends CombatStrategyConfiguration {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3455424595315157448L;
	
	static {
		StrategyConfiguration.setStrategyConfigMapping("Augmentation Drone",
				AugmentDroneConfiguration.class, AbilityStrategyEditor.class);
	}
	
	private static double MAX_NO_USE_TIME = 15;
	
	public class AugmentDrone extends GeneralDroneCombatStrategy
	{
		private double lastUsedTime = 0;
		private boolean timedOut = false;
		private Unit lastTarget;
		private Part augmentPart;
		private NotifyModifier mod;
		private Queue<UnitDefinition> bannedDefs = new LinkedList<UnitDefinition>();
		
		protected AugmentDrone(Unit u) {
			super(u);
			mod = new NotifyModifier();
			mod.setMapping(MapItemModifiers.fireRate, new MapItemModifierType() {
				@Override
				public double modify(double x) {
					return x + getFireRateIncrease();
				}
			});
			augmentPart = getAugmentPart().createMapItem(Transformation.ORIGIN, u.getOwner(), u.getGameState());
		}

		private class NotifyModifier extends MapItemModifier
		{
			@Override
			public double getModifier(MapItemModifiers m)
			{
				if(m.equals(MapItemModifiers.fireRate))
					lastUsedTime = AugmentDrone.this.getDrone().getGameState().getTime();
				return super.getModifier(m);
			}
		}
		
		@Override
		public void setTarget(Unit t)
		{
			if(lastTarget != null && t != lastTarget)
			{
				lastTarget.removeModifierFromAllItems(mod);
				lastTarget.removeMapItem(augmentPart);
			}
			if(t != null)
			{
				t.pushModifierToAllItems(mod);
				t.addMapItemToFront(augmentPart, Transformation.ORIGIN);
			}
			super.setTarget(t);
		}

		@Override
		public boolean isFinishedOnTarget() {
			//check to see if the target timed out
			if(this.getTarget() != null && 
			   !this.getTarget().getState().equals(MapItemState.Dead) &&
			   this.getDrone().getGameState().getTime() - lastUsedTime > MAX_NO_USE_TIME)
				timedOut = true;
			
			return this.getTarget() == null ||
			   this.getTarget().getState().equals(MapItemState.Dead) ||
			   timedOut;
		}

		@Override
		public Unit pickBestTarget(Unit[] targets) {
			if(timedOut)
			{
				timedOut = false;
				if(this.getTarget() != null && !bannedDefs.contains(this.getTarget().getDefinition()))
					bannedDefs.add((UnitDefinition) this.getTarget().getDefinition());
			}
			
			//first go through each target and see if it's definition isn't in the banned list
			for(Unit t : targets)
			{
				if(!bannedDefs.contains(t.getDefinition()))
					return t;
			}
			
			//if we got here, then all the potential targets are in the banned list
			//so now go through the queue, popping and checking to see if that lets us pick any units
			int maxPops = bannedDefs.size();
			for(int pops = 0; pops < maxPops; pops++)
			{
				UnitDefinition ud = bannedDefs.poll();
				//now go through each of the units again
				for(Unit t : targets)
				{
					if(!bannedDefs.contains(t.getDefinition()))
						return t;
				}
				//if we made it here, then this didn't unban any units, so readd the definition
				bannedDefs.add(ud);
			}
			
			//if we've made it here, there are no units to pick from
			return null;
				
		}

		@Override
		public String name() {
			return "Augment Drone Combat Strategy";
		}

		@Override
		public CombatStrategyConfiguration getConfig() {
			return AugmentDroneConfiguration.this;
		}

		@Override
		protected void applyEffect(Unit target) {}
	}
	
	public AugmentDroneConfiguration()
	{
		super.setPropertyForName("fireRateIncrease", new EditorProperty(Usage.NUMERIC_FLOATING_POINT, 
				null, EditorUsage.Real, "The ratio to increase the fire rate by"));
		super.setPropertyForName("augmentPart", new EditorProperty(Usage.CONFIGURATION, 
				null, EditorUsage.PartConfig, "The part to add to map items that are being augmented"));
	}
	
	public double getFireRateIncrease()
	{
		return (Double)super.getPropertyForName("fireRateIncrease").getValue();
	}
	
	public PartDefinition getAugmentPart()
	{
		return (PartDefinition)super.getPropertyForName("augmentPart").getValue();
	}

	@Override
	public CombatStrategy createStrategy(MapItem m) {
		return new AugmentDrone((Unit) m);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof AugmentDroneConfiguration) &&
				((AugmentDroneConfiguration) obj).getAugmentPart().equals(getAugmentPart()) &&
				((AugmentDroneConfiguration) obj).getFireRateIncrease() == getFireRateIncrease();
	}

}
