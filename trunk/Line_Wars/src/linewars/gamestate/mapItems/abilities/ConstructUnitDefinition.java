package linewars.gamestate.mapItems.abilities;

import java.util.Observable;
import java.util.Observer;

import linewars.gamestate.mapItems.Building;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.UnitDefinition;
import configuration.*;
import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;

/**
 * 
 * @author , Connor Schenck
 *
 * This class represents the ability definition that creates units.
 * Knows which unit it creates and starts active. Must be given
 * what UnitDefinition to create from and the build time of that unit.
 */
public strictfp class ConstructUnitDefinition extends AbilityDefinition implements Observer {
	
	static {
		AbilityDefinition.setAbilityConfigMapping("Construct Unit", ConstructUnitDefinition.class, AbilityStrategyEditor.class);
	}
	
	private UnitDefinition unitDefinition = null;
	private long buildtime;
	
	public strictfp class ConstructUnit implements Ability {
		
		private Building building;
		private long startTime;
		
		private ConstructUnit(Building b)
		{
			building = b;
			b.setState(MapItemState.Active);
			startTime = (long)(b.getGameState().getTime()*1000);
		}

		@Override
		public void update() {
			if((long)(building.getGameState().getTime()*1000) - startTime > getBuildTime())
			{
				if(building.getState() != MapItemState.Active)
					building.setState(MapItemState.Active);
				Unit u = getUnitDefinition().createMapItem(building.getTransformation(), 
						building.getOwner(), building.getGameState());
				building.getNode().addUnit(u);
				startTime = (long)(building.getGameState().getTime()*1000);
			}
		}

		@Override
		public boolean killable() {
			return true;
		}

		@Override
		public boolean finished() {
			return false;
		}

	}
	
	public ConstructUnitDefinition()
	{
		super.setPropertyForName("unitDefinition", new EditorProperty(Usage.CONFIGURATION, 
				null, EditorUsage.UnitConfig, "The unit to build"));
		super.setPropertyForName("buildtime", new EditorProperty(Usage.NUMERIC_FLOATING_POINT, 
				null, EditorUsage.NaturalNumber, "The time it takes to build the unit in ms"));
		super.addObserver(this);
	}

	@Override
	public boolean startsActive() {
		return true;
	}

	@Override
	public Ability createAbility(MapItem m) {
		if(m instanceof Building)
			return new ConstructUnit((Building)m);
		else
			throw new IllegalArgumentException("Only buildings may construct units");
	}

	@Override
	public String getName() {
		if(unitDefinition != null)
			return "Construct Unit: " + unitDefinition.getName();
		else
			return "Construct Unit: undefinded";
	}

	@Override
	public String getDescription() {
		return "Constructs the unit " + unitDefinition.getName() + ". Starts active and repeats.";
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof ConstructUnitDefinition)
			return unitDefinition.equals(((ConstructUnitDefinition)o).unitDefinition);
		else
			return false;
	}
	
	public long getBuildTime()
	{
		return buildtime;
	}
	
	public UnitDefinition getUnitDefinition()
	{
		return unitDefinition;
	}

	@Override
	public void update(Observable o, Object arg) {
		if(o == this)
		{
			if(arg.equals("unitDefinition"))
				unitDefinition = (UnitDefinition)super.getPropertyForName("unitDefinition").getValue();
			if(arg.equals("buildtime"))
				buildtime = (long)(int)(Integer)super.getPropertyForName("buildtime").getValue();
		}
	}

}
