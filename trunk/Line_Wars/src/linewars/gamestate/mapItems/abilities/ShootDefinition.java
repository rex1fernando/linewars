package linewars.gamestate.mapItems.abilities;

import java.io.FileNotFoundException;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ConfigFileReader.InvalidConfigFileException;
import linewars.configfilehandler.ParserKeys;
import linewars.gamestate.Player;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemDefinition;
import linewars.gamestate.mapItems.ProjectileDefinition;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.UnitDefinition;

/**
 * 
 * @author , Connor Schenck
 *
 * this class represents the definition of the shoot ability.
 * It knows what projectile definition to use and the maximum
 * range away this ability can be used.
 */
public strictfp class ShootDefinition extends AbilityDefinition {
	
	private ProjectileDefinition ammo = null;
	private double range;
	private ConfigData parser;
	
	public ShootDefinition(ConfigData cd, Player owner, int ID)
	{
		super(ID);
		this.owner = owner;
		parser = cd;
		this.forceReloadConfigData();
	}

	@Override
	public boolean checkValidity() {
		return (ammo != null);
	}

	@Override
	public boolean startsActive() {
		return false;
	}

	@Override
	public Ability createAbility(MapItem m) {
		if(m instanceof Unit)
			return new Shoot(ammo, (Unit)m);
		else
			throw new IllegalArgumentException(m.getName() + " cannot shoot.");
	}

	@Override
	public boolean unlocked() {
		return true;
	}

	@Override
	public String getName() {
		return "Shoot: " + ammo.getName();
	}

	@Override
	public String getDescription() {
		return "Shoots a " + ammo.getName();
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof ShootDefinition)
			return ammo.equals(((ShootDefinition)o).ammo);
		else
			return false;
	}
	
	public double getRange()
	{
		return range;
	}

	@Override
	public String getIconURI() {
		return ammo.getParser().getString(ParserKeys.icon);
	}

	@Override
	public String getPressedIconURI() {
		return ammo.getParser().getString(ParserKeys.pressedIcon);
	}

	@Override
	public String getRolloverIconURI() {
		return ammo.getParser().getString(ParserKeys.rolloverIcon);
	}

	@Override
	public String getSelectedIconURI() {
		return ammo.getParser().getString(ParserKeys.selectedIcon);
	}

	@Override
	public ConfigData getParser() {
		return parser;
	}

	@Override
	public void forceReloadConfigData() {
		try {
			ammo = owner.getProjectileDefinition(parser.getString(ParserKeys.projectileURI));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidConfigFileException e) {
			e.printStackTrace();
		}
		range = parser.getNumber(ParserKeys.range);
	}

}
