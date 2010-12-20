package linewars.gamestate.mapItems.abilities;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;
import linewars.gamestate.GameState;
import linewars.gamestate.mapItems.MapItem;

/**
 * 
 * @author Connor Schenck
 * 
 * This class is the definition for the generate stuff ability. It
 * crteates new generate stuff abilities.
 *
 */
public strictfp class GenerateStuffDefinition extends AbilityDefinition {

	private double stuffIncome;
	private ConfigData parser;
	private GameState gameState;
	
	public GenerateStuffDefinition(int id, ConfigData p, GameState gs) {
		super(id);
		parser = p;
		gameState = gs;
		this.forceReloadConfigData();
	}
	
	/**
	 * 
	 * @return	the amount of stuff generated per second
	 */
	public double getStuffIncome() {
		return stuffIncome;
	}
	
	public GameState getGameState() {
		return gameState;
	}

	@Override
	public ConfigData getParser() {
		return parser;
	}

	@Override
	public void forceReloadConfigData() {
		stuffIncome = parser.getNumber(ParserKeys.stuffIncome);		
	}

	@Override
	public boolean checkValidity() {
		return true;
	}

	@Override
	public boolean startsActive() {
		return true;
	}

	@Override
	public Ability createAbility(MapItem m) {
		return new GenerateStuff(this, m.getOwner());
	}

	@Override
	public boolean unlocked() {
		return true;
	}

	@Override
	public String getName() {
		return "Generate Stuff";
	}

	@Override
	public String getDescription() {
		return "Generates " + stuffIncome + " per second";
	}

	@Override
	public String getIconURI() {
		return null;
	}

	@Override
	public String getPressedIconURI() {
		return null;
	}

	@Override
	public String getRolloverIconURI() {
		return null;
	}

	@Override
	public String getSelectedIconURI() {
		return null;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof GenerateStuffDefinition)
		{
			GenerateStuffDefinition gsd = (GenerateStuffDefinition) o;
			return gsd.stuffIncome == stuffIncome;
		}
		return false;
	}

}
