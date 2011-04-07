package linewars.gamestate.mapItems.strategies.combat;

import java.util.List;

import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemModifier;
import linewars.gamestate.mapItems.MapItemModifier.MapItemModifiers;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Part;
import linewars.gamestate.mapItems.PartDefinition;
import linewars.gamestate.mapItems.Turret;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.abilities.Ability;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import linewars.gamestate.shapes.AABB;
import linewars.gamestate.shapes.Circle;
import configuration.Property;
import configuration.Usage;
import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;

public class GunshipHelpAlliesConfiguration extends CombatStrategyConfiguration {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7307604612393054537L;

	static {
		StrategyConfiguration.setStrategyConfigMapping("Gunship Help Allies",
				GunshipHelpAlliesConfiguration.class, AbilityStrategyEditor.class);
	}
	
	public class GunshipHelpAllies implements CombatStrategy
	{
		private Unit gunship;
		private Unit target;
		private double lastPulseTime;
		
		private GunshipHelpAllies(Unit u)
		{
			gunship = u;
		}

		@Override
		public String name() {
			return "Gunship Help Allies";
		}

		@Override
		public CombatStrategyConfiguration getConfig() {
			return GunshipHelpAlliesConfiguration.this;
		}

		@Override
		public double getRange() {
			return getDoubleValue("range");
		}

		@Override
		public void fight(Unit[] availableEnemies, Unit[] availableAllies) {
			//first check to see if we need a new target
			if(target == null || target.getState().equals(MapItemState.Dead))
			{
				target = acquireTarget(availableAllies);
				//this ability is just to tell other gunships that this one is targeting
				//target and they should look for someone else
				target.addActiveAbility(new GunshipTarget(target));
			}
			
			//next check to see if we're hovering over the target
			if(target.getPosition().distanceSquared(gunship.getPosition()) > Math.pow(getRange(), 2))
				gunship.getMovementStrategy().setTarget(target.getTransformation());
			
			//next check to see if we can pulse
			if(gunship.getGameState().getTime() - lastPulseTime > getDoubleValue("cooldown"))
			{
				double range = getDoubleValue("range");
				AABB box = new AABB(gunship.getPosition().getX() - range, gunship.getPosition().getY() - range, 
						gunship.getPosition().getX() + range, gunship.getPosition().getY() + range);
				List<Unit> potentialHits = gunship.getWave().getLane().getUnitsIn(box);
				for(Unit u : potentialHits)
				{
					if(u.getPosition().distanceSquared(gunship.getPosition()) <= range*range)
						u.addActiveAbility(new ShieldBuff(u));
				}
				lastPulseTime = gunship.getGameState().getTime();
			}
			
			//finally if we have any turrets, tell them to fight
			for(Turret t : gunship.getTurrets())
				t.getTurretStrategy().fight(availableEnemies, availableAllies);
		}
		
		private Unit acquireTarget(Unit[] availableAllies)
		{
			Unit ret = null;
			double dis = Double.POSITIVE_INFINITY;
			for(Unit u : availableAllies)
			{
				boolean found = false;
				for(Ability a : u.getActiveAbilities())
				{
					if(a instanceof GunshipTarget)
					{
						found = true;
						break;
					}
				}
				if(found)
					continue;
				
				double d = gunship.getPosition().distanceSquared(u.getPosition());
				if(d < dis)
				{
					dis = d;
					ret = u;
				}
			}
			
			return ret;
		}
		
		private class GunshipTarget implements Ability
		{
			private MapItem mapItem;
			
			private GunshipTarget(MapItem m)
			{
				mapItem = m;
			}

			@Override
			public void update() {}

			@Override
			public boolean killable() {
				return true;
			}

			@Override
			public boolean finished() {
				return target != mapItem;
			}
			
		}
		
		private class ShieldBuff implements Ability
		{
			private Unit unit;
			private MapItemModifier mod;
			private Part shieldPart;
			private double startTime;
			private boolean finished = false;
			
			private ShieldBuff(Unit u)
			{
				unit = u;
				mod = new MapItemModifier();
				mod.setMapping(MapItemModifiers.damageReceived, new MapItemModifier.Add(-getDoubleValue("damageReduction")));
				if(getBooleanValue("augmentationFieldEnabled"))
				{
					mod.setMapping(MapItemModifiers.moveSpeed, new MapItemModifier.Add(getDoubleValue("moveSpeedIncrease")));
					mod.setMapping(MapItemModifiers.fireRate, new MapItemModifier.Add(getDoubleValue("fireRateIncrease")));
				}
				shieldPart = getShieldPart().createMapItem(Transformation.ORIGIN, u.getOwner(), u.getGameState());
				
				u.pushModifierToAllItems(mod);
				u.addMapItemToFront(shieldPart, Transformation.ORIGIN);
				startTime = u.getGameState().getTime();
			}

			@Override
			public void update() {
				if(!finished && getBooleanValue("repairFieldEnabled"))
				{
					double heals = getDoubleValue("healsPerSecond")*unit.getGameState().getLastLoopTime();
					unit.setHP(unit.getHP() + heals);
				}
				
				if(!finished && unit.getGameState().getTime() - startTime > getDoubleValue("duration"))
				{
					finished = true;
					unit.removeMapItem(shieldPart);
					unit.removeModifierFromAllItems(mod);
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
	
	public GunshipHelpAlliesConfiguration()
	{
		//these properties are not configurable in the editor, they are here for the techs
		super.setPropertyForName("repairFieldEnabled", new Property(Usage.BOOLEAN, false));
		super.setPropertyForName("augmentationFieldEnabled", new Property(Usage.BOOLEAN, false));
		
		super.setPropertyForName("healsPerSecond", new EditorProperty(Usage.NUMERIC_FLOATING_POINT, 
				null, EditorUsage.Real, "The amount of healing done per second by the healing field"));
		super.setPropertyForName("damageReduction", new EditorProperty(Usage.NUMERIC_FLOATING_POINT, 
				null, EditorUsage.Real, "The ratio to reduce the damage taken by (eg 5% reduction would be a value of 0.05)"));
		super.setPropertyForName("moveSpeedIncrease", new EditorProperty(Usage.NUMERIC_FLOATING_POINT,
				null, EditorUsage.Real, "The ratio to increase the move speed by (eg 5% increase would be a value of 0.05)"));
		super.setPropertyForName("fireRateIncrease", new EditorProperty(Usage.NUMERIC_FLOATING_POINT,
				null, EditorUsage.Real, "The ratio to increase the fire rate by (eg 5% increase would be a value of 0.05)"));
		super.setPropertyForName("cooldown", new EditorProperty(Usage.NUMERIC_FLOATING_POINT,
				null, EditorUsage.PositiveReal, "The cooldown of the pulse of the shield generator in seconds"));
		super.setPropertyForName("duration", new EditorProperty(Usage.NUMERIC_FLOATING_POINT,
				null, EditorUsage.PositiveReal, "The duration in seconds of the pulse of the shield generator"));
		super.setPropertyForName("range", new EditorProperty(Usage.NUMERIC_FLOATING_POINT,
				null, EditorUsage.PositiveReal, "The range of the shield generator"));
		super.setPropertyForName("shieldPart", new EditorProperty(Usage.CONFIGURATION, 
				null, EditorUsage.PartConfig, "The part to add to units affected by the shield"));
	}
	
	public <T> T getValue(String val, T obj)
	{
		return (T)super.getPropertyForName(val).getValue();
	}
	
	public Double getDoubleValue(String name)
	{
		return getValue(name, Double.MAX_VALUE);
	}
	
	public Boolean getBooleanValue(String name)
	{
		return getValue(name, Boolean.TRUE);
	}
	
	public PartDefinition getShieldPart()
	{
		return (PartDefinition)super.getPropertyForName("shieldPart").getValue();
	}

	@Override
	public CombatStrategy createStrategy(MapItem m) {
		return new GunshipHelpAllies((Unit) m);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof GunshipHelpAlliesConfiguration)
		{
			GunshipHelpAlliesConfiguration ghac = (GunshipHelpAlliesConfiguration) obj;
			String[] props = {"repairFieldEnabled",
							  "augmentationFieldEnabled",
							  "healsPerSecond",
							  "damageReduction",
							  "moveSpeedIncrease",
							  "fireRateIncrease",
							  "cooldown",
							  "duration",
							  "range",
							  "shieldPart"};
			for(String prop : props)
				if(!ghac.getPropertyForName(prop).getValue().equals(getPropertyForName(prop).getValue()))
					return false;
			
			return true;
		}
		else
			return false;
	}
}

