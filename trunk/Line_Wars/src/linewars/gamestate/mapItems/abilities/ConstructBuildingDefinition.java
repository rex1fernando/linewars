package linewars.gamestate.mapItems.abilities;

import utility.Observable;
import utility.Observer;

import linewars.gamestate.Node;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.Building;
import linewars.gamestate.mapItems.BuildingDefinition;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemState;
import configuration.Usage;
import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;

/**
 * 
 * @author , Connor Schenck
 *
 * This class represents the definition for an ability that constructs
 * a building. Knows what buiding it contructs.
 */
public strictfp class ConstructBuildingDefinition extends AbilityDefinition implements Observer {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2267724322796418793L;

	static {
		AbilityDefinition.setAbilityConfigMapping("Construct Building", ConstructBuildingDefinition.class, AbilityStrategyEditor.class);
	}
	
	private BuildingDefinition buildingDefinition;
	
	public strictfp class ConstructBuilding implements Ability, ProgressAbility {

		private long startTime;
		private boolean built = false;
		private Building building = null;
		
		/**
		 * Creates the ability that constructs a building in node n
		 * using definition bd.
		 * 
		 * @param n
		 * @param bd
		 */
		private ConstructBuilding(Building sponsor)
		{
			Node n = sponsor.getNode();
			startTime = (long) (sponsor.getGameState().getTime()*1000);
			
			Transformation t = n.getNextAvailableBuildingSpot();
			//can't construct the building
			if(t == null || sponsor.getOwner().getStuff() < buildingDefinition.getCost())
			{
				built = true;
				return;
			}
			
			sponsor.getOwner().spendStuff(buildingDefinition.getCost());
			
			building = buildingDefinition.createMapItem(t, sponsor.getOwner(), sponsor.getGameState());
			building.setState(MapItemState.Constructing);
			if(!n.addBuilding(building))
				throw new RuntimeException("This should never happen: the building was not placed correctly.");
			building.getOwner().addMapItem(building);
		}
		
		@Override
		public void update() {
			if(!built && (long) (building.getGameState().getTime()*1000) - startTime >= buildingDefinition.getBuildTime())
			{
				built = true;
				building.setState(MapItemState.Idle);
			}
		}

		@Override
		public boolean killable() {
			return true;
		}

		@Override
		public boolean finished() {
			return built;
		}

		@Override
		public double getProgress() {
			return (building.getGameState().getTime()*1000 - startTime)/buildingDefinition.getBuildTime();
		}

	}
	
	public ConstructBuildingDefinition()
	{
		super.setPropertyForName("buildingDefinition", new EditorProperty(Usage.CONFIGURATION, 
				null, EditorUsage.BuildingConfig, "The building to build"));
		this.addObserver(this);
	}
	
	public ConstructBuildingDefinition(BuildingDefinition bd)
	{
		this.addObserver(this);
		super.setPropertyForName("buildingDefinition", new EditorProperty(Usage.CONFIGURATION, 
				null, EditorUsage.BuildingConfig, "The building to build").makeCopy(bd));
	}

	@Override
	public boolean startsActive() {
		return false;
	}

	@Override
	public Ability createAbility(MapItem m) {
		if(!(m instanceof Building))
			throw new IllegalArgumentException("Only building can build buildings.");
		
		return new ConstructBuilding((Building)m);
	}

	@Override
	public String getName() {
		return "Construct Building: " + buildingDefinition.getName();
	}

	@Override
	public String getDescription() {
		return "Constructs the building " + buildingDefinition.getName() + 
		". Costs " + buildingDefinition.getCost() + ". Takes " +
		(buildingDefinition.getBuildTime()/1000.0) + " seconds to build."; 
	}

	@Override
	public boolean equals(Object o) {
		if(!(o instanceof ConstructBuildingDefinition))
			return false;
		else
			return buildingDefinition.equals(((ConstructBuildingDefinition)o).buildingDefinition);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if(arg0 == this && arg1.equals("buildingDefinition"))
			buildingDefinition = (BuildingDefinition)super.getPropertyForName("buildingDefinition").getValue();
	}

}
