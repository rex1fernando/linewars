package linewars.gamestate.mapItems.abilities;

import java.io.FileNotFoundException;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ConfigFileReader.InvalidConfigFileException;
import linewars.configfilehandler.ParserKeys;
import linewars.gamestate.Player;
import linewars.gamestate.mapItems.Building;
import linewars.gamestate.mapItems.BuildingDefinition;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemDefinition;
import linewars.gamestate.mapItems.UnitDefinition;

/**
 * 
 * @author , Connor Schenck
 *
 * This class represents the ability definition that creates units.
 * Knows which unit it creates and starts active. Must be given
 * what UnitDefinition to create from and the build time of that unit.
 */
public strictfp class ConstructUnitDefinition extends AbilityDefinition {
	
	private UnitDefinition unitDefinition = null;
	private long buildtime;
	private ConfigData parser;
	
	public ConstructUnitDefinition(ConfigData cd, Player owner, int ID)
	{
		super(ID);
		this.owner = owner;
		parser = cd;
		this.forceReloadConfigData();
	}

	@Override
	public boolean checkValidity() {
		return (unitDefinition != null);
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
		return unitDefinition.getParser().getString(ParserKeys.icon);
	}

	@Override
	public String getPressedIconURI() {
		return unitDefinition.getParser().getString(ParserKeys.pressedIcon);
	}

	@Override
	public String getRolloverIconURI() {
		return unitDefinition.getParser().getString(ParserKeys.rolloverIcon);
	}

	@Override
	public String getSelectedIconURI() {
		return unitDefinition.getParser().getString(ParserKeys.selectedIcon);
	}

	@Override
	public ConfigData getParser() {
		return parser;
	}

	@Override
	public void forceReloadConfigData() {
		buildtime = (long)(double)parser.getNumber(ParserKeys.buildTime);
		try {
			unitDefinition = owner.getUnitDefinition(parser.getString(ParserKeys.unitURI));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidConfigFileException e) {
			e.printStackTrace();
		}
	}

}
