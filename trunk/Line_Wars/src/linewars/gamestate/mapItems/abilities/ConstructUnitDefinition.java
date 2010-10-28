package linewars.gamestate.mapItems.abilities;

import linewars.gamestate.mapItems.Building;
import linewars.gamestate.mapItems.BuildingDefinition;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemDefinition;
import linewars.gamestate.mapItems.UnitDefinition;
import linewars.parser.ParserKeys;

/**
 * 
 * @author cschenck
 *
 * This class represents the ability definition that creates units.
 * Knows which unit it creates and starts active. Must be given
 * what UnitDefinition to create from and the build time of that unit.
 */
public class ConstructUnitDefinition extends AbilityDefinition {
	
	private UnitDefinition unitDefinition = null;
	private long buildtime;
	
	public ConstructUnitDefinition(UnitDefinition ud, MapItemDefinition owner, long buildTime, int ID)
	{
		super(ID);
		unitDefinition = ud;
		this.owner = owner;
		this.buildtime = buildTime;
	}

	@Override
	public boolean checkValidity() {
		return (unitDefinition != null) && (this.owner instanceof BuildingDefinition);
	}

	@Override
	public boolean startsActive() {
		return true;
	}

	@Override
	public Ability createAbility(MapItem m) {
		if(m instanceof Building)
			return new ConstructUnit(unitDefinition, (Building)m, buildtime);
		else
			throw new IllegalArgumentException("Only buildings may construct units");
	}

	@Override
	public boolean unlocked() {
		return true;
	}

	@Override
	public String getName() {
		return "Construct Unit: " + unitDefinition.getName();
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

	@Override
	public String getIconURI() {
		return unitDefinition.getParser().getStringValue(ParserKeys.icon);
	}

	@Override
	public String getPressedIconURI() {
		return unitDefinition.getParser().getStringValue(ParserKeys.pressedIcon);
	}

	@Override
	public String getRolloverIconURI() {
		return unitDefinition.getParser().getStringValue(ParserKeys.rolloverIcon);
	}

	@Override
	public String getSelectedIconURI() {
		return unitDefinition.getParser().getStringValue(ParserKeys.selectedIcon);
	}

}
