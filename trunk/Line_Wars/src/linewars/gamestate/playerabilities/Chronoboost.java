package linewars.gamestate.playerabilities;

import linewars.display.DisplayConfiguration;
import linewars.gamestate.Node;
import linewars.gamestate.Player;
import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.Building;
import linewars.gamestate.mapItems.MapItemModifier;
import linewars.gamestate.mapItems.Part;
import linewars.gamestate.mapItems.PartDefinition;
import linewars.gamestate.mapItems.MapItemModifier.MapItemModifiers;
import linewars.gamestate.mapItems.abilities.Ability;
import configuration.Usage;
import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;

public class Chronoboost extends PlayerAbility {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6104128995214994793L;

	static {
		PlayerAbility.setAbilityConfigMapping("Chronoboost", Chronoboost.class, AbilityStrategyEditor.class);
	}
	
	public Chronoboost()
	{
		super.setPropertyForName("buildRateIncrease", new EditorProperty(Usage.NUMERIC_FLOATING_POINT, 
				null, EditorUsage.Real, "The ratio to increase the buildrate by"));
		super.setPropertyForName("duration", new EditorProperty(Usage.NUMERIC_FLOATING_POINT, 
				null, EditorUsage.PositiveReal, "The duration in seconds of the chronoboost"));
		super.setPropertyForName("boostPart", new EditorProperty(Usage.CONFIGURATION, 
				null, EditorUsage.PartConfig, "The part to add underneath the chronoboosted building"));
	}
	
	public double getBuildRateIncrease()
	{
		return (Double)super.getPropertyForName("buildRateIncrease").getValue();
	}
	
	public double getDuration()
	{
		return (Double)super.getPropertyForName("duration").getValue();
	}
	
	public PartDefinition getBoostPart()
	{
		return (PartDefinition)super.getPropertyForName("boostPart").getValue();
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
		
		double dis = Double.POSITIVE_INFINITY;
		Building closest = null;
		for(Node n : player.getGameState().getMap().getNodes())
		{
			for(Building b : n.getContainedBuildings())
			{
				double d = p.distanceSquared(b.getPosition());
				if(d < dis)
				{
					dis = d;
					closest = b;
				}
			}
		}
		
		//if they didn't actually click in a building
		//TODO this is a hack
		DisplayConfiguration dc = (DisplayConfiguration) closest.getDefinition().getDisplayConfiguration();
		if(dis > Math.pow(dc.getDimensions().getX(), 2) + Math.pow(dc.getDimensions().getY(), 2))
			return;
		
		closest.addActiveAbility(new Boost(closest));
	}
	
	private class Boost implements Ability
	{
		private Building b;
		private MapItemModifier mod;
		private double startTime;
		private boolean finished = false;
		private Part boostPart;
		
		private Boost(Building b)
		{
			this.b = b;
			mod = new MapItemModifier();
			mod.setMapping(MapItemModifiers.buildingProductionRate, new MapItemModifier.Add(getBuildRateIncrease()));
			b.pushModifier(mod);
			startTime = b.getGameState().getTime();
			boostPart = getBoostPart().createMapItem(Transformation.ORIGIN, b.getOwner(), b.getGameState());
			b.addMapItemToFront(boostPart, Transformation.ORIGIN);
		}

		@Override
		public void update() {
			if(!finished && b.getGameState().getTime() - startTime > getDuration())
			{
				b.removeModifier(mod);
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

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Chronoboost) &&
				((Chronoboost) obj).getBuildRateIncrease() == getBuildRateIncrease() &&
				((Chronoboost) obj).getDuration() == getDuration() &&
				((Chronoboost) obj).getBoostPart().equals(getBoostPart());
	}

}
