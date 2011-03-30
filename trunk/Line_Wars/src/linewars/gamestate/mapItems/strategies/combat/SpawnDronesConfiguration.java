package linewars.gamestate.mapItems.strategies.combat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

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

public class SpawnDronesConfiguration extends
		CombatStrategyConfiguration implements Observer {
	
	static {
		StrategyConfiguration.setStrategyConfigMapping("Spawn Drones",
				SpawnDronesConfiguration.class, AbilityStrategyEditor.class);
	}
	
	private double range;
	private int numDrones;
	private double launchTime;
	private UnitDefinition droneConfig;
	
	public class SpawnDrones implements CombatStrategy
	{
		
		private Unit droneCarrier;
		private HashMap<Unit, Unit> dronesToTargets = new HashMap<Unit, Unit>();
		private double lastLaunchTime = 0;
		private int lastTick = 0;
		
		private SpawnDrones(Unit u)
		{
			droneCarrier = u;
		}

		@Override
		public String name() {
			return droneCarrier.getName() + " spawns healing drones";
		}

		@Override
		public CombatStrategyConfiguration getConfig() {
			return SpawnDronesConfiguration.this;
		}

		@Override
		public double getRange() {
			return range;
		}

		@Override
		public void fight(Unit[] availableEnemies, Unit[] availableAllies) {
			//first check to see if this unit went out of combat since the last call to fight
			if(lastTick + 1 < droneCarrier.getGameState().getTimerTick())
			{
				//--if we did go out of combat, check to see if any of the currently active drones were docked
				for(Unit drone : dronesToTargets.keySet())
				{
					if(!droneCarrier.getWave().contains(drone))
						dronesToTargets.remove(drone);
				}
			}
			lastTick = droneCarrier.getGameState().getTimerTick();
			
			//next check to see if any drones are dead
			for(Unit drone : dronesToTargets.keySet())
			{
				if(drone.getState().equals(MapItemState.Dead))
					dronesToTargets.remove(drone);
			}
			
			//next check to see if another drone can be spawned
			if(dronesToTargets.size() < numDrones && 
					droneCarrier.getGameState().getTime() - lastLaunchTime > launchTime)
			{
				Unit drone = droneConfig.createMapItem(droneCarrier.getTransformation(), 
						droneCarrier.getOwner(), droneCarrier.getGameState());
				dronesToTargets.put(drone, null);
				droneCarrier.getWave().addUnit(drone);
				lastLaunchTime = droneCarrier.getGameState().getTime();
			}
			
			//next check to see if any of the current drones have finished their target
			for(Entry<Unit, Unit> e : dronesToTargets.entrySet())
			{
				DroneCombatStrategy dcs = (DroneCombatStrategy) e.getKey().getCombatStrategy();
				if(e.getKey() == null || dcs.isFinishedOnTarget())
				{
					List<Unit> targets = new ArrayList<Unit>();
					for(Unit u : availableAllies)
						targets.add(u);
					for(Unit u : availableEnemies)
						targets.add(u);
					for(Unit u : dronesToTargets.values())
						targets.remove(u);
					Unit newTarget = dcs.pickBestTarget(targets.toArray(new Unit[targets.size()]));
					dcs.setTarget(newTarget);
					dronesToTargets.put(e.getKey(), newTarget);
				}
			}
			
		}
		
	}
	
	public SpawnDronesConfiguration()
	{
		super.setPropertyForName("range", new EditorProperty(
				Usage.NUMERIC_FLOATING_POINT, 0, EditorUsage.PositiveReal,
				"The maximum range to launch drones at"));
		super.setPropertyForName("numDrones", new EditorProperty(
				Usage.NUMERIC_INTEGER, 0, EditorUsage.NaturalNumber,
				"The number of drones to spawn"));
		super.setPropertyForName("launchTime", new EditorProperty(
				Usage.NUMERIC_FLOATING_POINT, 0, EditorUsage.PositiveReal,
				"The time it takes to launch a drone in seconds"));
		super.setPropertyForName("droneConfig", new EditorProperty(
				Usage.CONFIGURATION, null, EditorUsage.UnitConfig,
				"The config for the drones to spawn"));
	}

	@Override
	public CombatStrategy createStrategy(MapItem m) {
		if(m instanceof Unit)
			return new SpawnDrones((Unit) m);
		else
			throw new IllegalArgumentException("Only units may have combat strategies");
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof SpawnDronesConfiguration)
		{
			SpawnDronesConfiguration shdc = (SpawnDronesConfiguration) obj;
			return shdc.droneConfig.equals(droneConfig) &&
					shdc.launchTime == launchTime &&
					shdc.numDrones == numDrones &&
					shdc.range == range;
		}
		else
			return false;
	}

	@Override
	public void update(Observable o, Object arg) {
		if(o == this)
		{ 
			if(arg.equals("range"))
				range = (Double)super.getPropertyForName("range").getValue();
			else if(arg.equals("numDrones"))
				numDrones = (Integer)super.getPropertyForName("numDrones").getValue();
			else if(arg.equals("launchTime"))
				launchTime = (Double)super.getPropertyForName("launchTime").getValue();
			else if(arg.equals("droneConfig"))
				droneConfig = (UnitDefinition)super.getPropertyForName("droneConfig").getValue();
				
		}
	}

}
