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
import configuration.ListConfiguration;
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
	
	private static final long serialVersionUID = 8751120957207584161L;
	
	private double range;
	private double droneCreationTime;
	private double launchTime;
	private List<UnitDefinition> droneConfigs;
	
	public class SpawnDrones implements CombatStrategy
	{
		
		private Unit droneCarrier;
		private HashMap<Unit, Unit> dronesToTargets = new HashMap<Unit, Unit>();
		private HashMap<UnitDefinition, Unit> ownedDrones = new HashMap<UnitDefinition, Unit>();
		private double lastLaunchTime = 0;
		private double lastCreateTime = 0;
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
			
			//first check to see if there are any uncreated drones and attempt
			//to create them
			for(UnitDefinition ud : droneConfigs)
			{
				if(ownedDrones.get(ud) == null)
					ownedDrones.put(ud, createDrone(ud));
			}
			
			//next check to see if there are any unlaunched drones and attempt to
			//launch them
			for(Unit drone : ownedDrones.values())
			{
				if(!droneCarrier.getWave().contains(drone) && launchDrone()) //if not in the lane, it is not launched
				{
					droneCarrier.getWave().addUnit(drone);
					dronesToTargets.put(drone, null);
				}
				else if(!droneCarrier.getWave().contains(drone))
					dronesToTargets.remove(drone);
			}
			
			//next check to see if there are any dead drones, and remove them
			for(Unit drone : ownedDrones.values())
			{
				if(drone.getState().equals(MapItemState.Dead))
				{
					ownedDrones.put((UnitDefinition) drone.getDefinition(), null);
					dronesToTargets.remove(drone);
				}
			}
			
			//next check to see if any of the current drones have finished their target
			//and are in need of a new target
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
					//TODO implement isSuitableTarget and use it
					Unit newTarget = dcs.pickBestTarget(targets.toArray(new Unit[targets.size()]));
					dcs.setTarget(newTarget);
					dronesToTargets.put(e.getKey(), newTarget);
				}
			}
			
		}
		
		private Unit createDrone(UnitDefinition droneConfig)
		{
			if(droneCarrier.getGameState().getTime() - lastCreateTime > droneCreationTime)
			{
				lastCreateTime = droneCarrier.getGameState().getTime();
				return droneConfig.createMapItem(
						droneCarrier.getTransformation(),
						droneCarrier.getOwner(), droneCarrier.getGameState());
			}
			else
				return null;
		}
		
		private boolean launchDrone()
		{
			if(droneCarrier.getGameState().getTime() - lastLaunchTime > launchTime)
			{
				lastLaunchTime = droneCarrier.getGameState().getTime();
				return true;
			}
			else
				return false;
		}
		
	}
	
	public SpawnDronesConfiguration()
	{
		super.setPropertyForName("range", new EditorProperty(
				Usage.NUMERIC_FLOATING_POINT, 0, EditorUsage.PositiveReal,
				"The maximum range to launch drones at"));
		super.setPropertyForName("launchTime", new EditorProperty(
				Usage.NUMERIC_FLOATING_POINT, 0, EditorUsage.PositiveReal,
				"The time it takes to launch a drone in seconds"));
		super.setPropertyForName("droneCreationTime", new EditorProperty(
				Usage.NUMERIC_FLOATING_POINT, 0, EditorUsage.PositiveReal,
				"The time it takes to create a drone in seconds"));
		super.setPropertyForName("droneConfigs", new EditorProperty(
				Usage.CONFIGURATION, null, EditorUsage.ListUnitConfig,
				"The configs for the drones to spawn. Spawns one drone from each."));
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
			return shdc.droneConfigs.equals(droneConfigs) &&
					shdc.launchTime == launchTime &&
					shdc.droneCreationTime == droneCreationTime &&
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
			else if(arg.equals("droneCreationTime"))
				droneCreationTime = (Double)super.getPropertyForName("droneCreationTime").getValue();
			else if(arg.equals("launchTime"))
				launchTime = (Double)super.getPropertyForName("launchTime").getValue();
			else if(arg.equals("droneConfigs"))
			{
				ListConfiguration<?> lc = (ListConfiguration<?>)super.getPropertyForName("droneConfigs").getValue();
				droneConfigs.clear();
				for(Object o1 : lc.getEnabledSubList())
					droneConfigs.add((UnitDefinition) o1);
			}
				
		}
	}

}
