package linewars.gamestate.playerabilities;

import linewars.gamestate.Lane;
import linewars.gamestate.Player;
import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.Wave;
import linewars.gamestate.mapItems.MapItemAggregate;
import linewars.gamestate.mapItems.MapItemModifier;
import linewars.gamestate.mapItems.MapItemModifier.MapItemModifiers;
import linewars.gamestate.mapItems.Part;
import linewars.gamestate.mapItems.PartDefinition;
import linewars.gamestate.mapItems.Projectile;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.abilities.Ability;
import configuration.Usage;
import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;

public class Overdrive extends PlayerAbility {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6383786619155188197L;

	static {
		PlayerAbility.setAbilityConfigMapping("Overdrive", Overdrive.class, AbilityStrategyEditor.class);
	}
	
	public Overdrive()
	{
		super.setPropertyForName("damageDealtIncrease", new EditorProperty(Usage.NUMERIC_FLOATING_POINT, 
				null, EditorUsage.Real, "The ratio to increase damage dealt by"));
		super.setPropertyForName("moveSpeedIncrease", new EditorProperty(Usage.NUMERIC_FLOATING_POINT, 
				null, EditorUsage.Real, "The ratio to increase move speed by"));
		super.setPropertyForName("firingRateIncrease", new EditorProperty(Usage.NUMERIC_FLOATING_POINT, 
				null, EditorUsage.Real, "The ratio to increase firing rate by"));
		super.setPropertyForName("damageRecievedIncrease", new EditorProperty(Usage.NUMERIC_FLOATING_POINT, 
				null, EditorUsage.Real, "The ratio to increase damaged received by"));
		super.setPropertyForName("duration", new EditorProperty(Usage.NUMERIC_FLOATING_POINT, 
				null, EditorUsage.PositiveReal, "The duration of the effect in seconds"));
		super.setPropertyForName("overdrivePart", new EditorProperty(Usage.CONFIGURATION, 
				null, EditorUsage.PartConfig, "The part to add underneath the overdrived mapitems"));
	}
	
	public double getDamageDealtIncrease()
	{
		return (Double)super.getPropertyForName("damageDealtIncrease").getValue();
	}
	
	public double getMoveSpeedIncrease()
	{
		return (Double)super.getPropertyForName("moveSpeedIncrease").getValue();
	}
	
	public double getFiringRateIncrease()
	{
		return (Double)super.getPropertyForName("firingRateIncrease").getValue();
	}
	
	public double getDamageReceivedIncrease()
	{
		return (Double)super.getPropertyForName("damageRecievedIncrease").getValue();
	}
	
	public double getDuration()
	{
		return (Double)super.getPropertyForName("duration").getValue();
	}
	
	public PartDefinition getOverdrivePart()
	{
		return (PartDefinition)super.getPropertyForName("overdrivePart").getValue();
	}

	@Override
	public boolean requiresPosition() {
		return true;
	}

	@Override
	public void apply(Position p, Player player) {
		//first check to make sure the player has enough energy and charge the player if they do
		if(player.getPlayerEnergy() < this.getEnergyCost())
			return;
		player.setPlayerEnergy(player.getPlayerEnergy() - this.getEnergyCost());
		
		//first find the lane that is closest
		Lane lane = null;
		double dis = Double.POSITIVE_INFINITY;
		for(Lane l : player.getGameState().getMap().getLanes())
		{
			double d = p.distanceSquared(l.getPosition(l.getClosestPointRatio(p)).getPosition())/Math.pow(l.getWidth()/2, 2);
			if(d < 1.0 && dis < 1.0)
				return;
			if(d < dis)
			{
				dis = d;
				lane = l;
			}
		}
		
		if(dis > 1.0)
			return;
		
		//now go through every unit and projectile in the lane and berserk them
		for(Wave w : lane.getWaves())
		{
			for(Unit u : w.getUnits())
				if(player.equals(u.getOwner()))
					u.addActiveAbility(new Berserk(u));
		}
		
		for(Projectile p1 : lane.getProjectiles())
			if(player.equals(p1.getOwner()))
				p1.addActiveAbility(new Berserk(p1));
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Overdrive) &&
				((Overdrive) obj).getDamageDealtIncrease() == getDamageDealtIncrease() &&
				((Overdrive) obj).getDamageReceivedIncrease() == getDamageReceivedIncrease() &&
				((Overdrive) obj).getFiringRateIncrease() == getFiringRateIncrease() &&
				((Overdrive) obj).getMoveSpeedIncrease() == getMoveSpeedIncrease() &&
				((Overdrive) obj).getDuration() == getDuration() &&
				((Overdrive) obj).getOverdrivePart().equals(getOverdrivePart());
	}
	
	private class Berserk implements Ability
	{
		private MapItemAggregate b;
		private MapItemModifier mod;
		private double startTime;
		private boolean finished = false;
		private Part boostPart;
		
		private Berserk(MapItemAggregate b)
		{
			this.b = b;
			mod = new MapItemModifier();
			mod.setMapping(MapItemModifiers.damageDealt, new MapItemModifier.Add(getDamageDealtIncrease()));
			mod.setMapping(MapItemModifiers.damageReceived, new MapItemModifier.Add(getDamageReceivedIncrease()));
			mod.setMapping(MapItemModifiers.fireRate, new MapItemModifier.Add(getFiringRateIncrease()));
			mod.setMapping(MapItemModifiers.moveSpeed, new MapItemModifier.Add(getMoveSpeedIncrease()));
			b.pushModifierToAllItems(mod);
			startTime = b.getGameState().getTime();
			boostPart = getOverdrivePart().createMapItem(Transformation.ORIGIN, b.getOwner(), b.getGameState());
			b.addMapItemToFront(boostPart, Transformation.ORIGIN);
		}

		@Override
		public void update() {
			if(!finished && b.getGameState().getTime() - startTime > getDuration())
			{
				b.removeModifierFromAllItems(mod);
				b.removeMapItem(boostPart);
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
		
	}

}
