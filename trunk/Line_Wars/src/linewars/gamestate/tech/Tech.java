package linewars.gamestate.tech;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map.Entry;

import linewars.gamestate.Function;
import linewars.gamestate.Player;
import linewars.configfilehandler.*;
import linewars.configfilehandler.ConfigData.NoSuchKeyException;
import linewars.configfilehandler.ConfigFileReader.InvalidConfigFileException;

/**
 * 
 * @author John George, Taylor Bergquist
 *
 */
public strictfp class Tech {
	
	private Player owner;
	
	private int currentResearch = 0;
	private int maxTimesResearchable;
	private Function costFunction;
	
	private String name;
	private String tooltip;
	private String iconURI;
	private String pressedIconURI;
	private String rolloverIconURI;
	private String selectedIconURI;

	private HashMap<String, HashMap<ParserKeys, Modifier>> modificationChain;
	
	public Tech(ConfigData configuration, Player owner)
	{	
		this.owner = owner;
		
		try
		{
			maxTimesResearchable = configuration.getNumber(ParserKeys.maxTimesResearchable).intValue();
		}
		catch(NoSuchKeyException e)
		{
			maxTimesResearchable = Integer.MAX_VALUE;
		}
		
		costFunction = new Function(configuration.getConfig(ParserKeys.costFunction));
		
		name = configuration.getString(ParserKeys.name);
		tooltip = configuration.getString(ParserKeys.tooltip);
		iconURI = configuration.getString(ParserKeys.icon);
		pressedIconURI = configuration.getString(ParserKeys.pressedIcon);
		rolloverIconURI = configuration.getString(ParserKeys.rolloverIcon);
		selectedIconURI = configuration.getString(ParserKeys.selectedIcon);
		
		modificationChain = new HashMap<String, HashMap<ParserKeys, Modifier>>();
		for(ConfigData modifiedURI : configuration.getConfigList(ParserKeys.modifiedURI)){
			HashMap<ParserKeys, Modifier> toPopulate = new HashMap<ParserKeys, Modifier>();
			modificationChain.put(modifiedURI.getString(ParserKeys.URI), toPopulate);
			for(ConfigData modifiedKey : modifiedURI.getConfigList(ParserKeys.modifiedKey)){
				String key = modifiedKey.getString(ParserKeys.key);
				ParserKeys realKey = null;
				for(ParserKeys k : ParserKeys.values())
					if(k.toString().equalsIgnoreCase(key))
					{
						realKey = k;
						break;
					}
				if(realKey == null)
					ParserKeys.valueOf(key);
				Modifier toPut = new NumericModifier(modifiedKey.getConfig(ParserKeys.modifier));
				toPopulate.put(realKey, toPut);
			}
		}
	}
	
	/**
	 * This method checks to see if the pre-req's for this
	 * tech have been researched.
	 * 
	 * @return	true if the pre-req's have been researched, false otherwise
	 */
	public boolean researchable()
	{
		return true;
	}
	
	/**
	 * This method returns the maximum number of times this tech
	 * is allowed to be researched.
	 * 
	 * @return	the number of times to allow research
	 */
	public int maxTimesResearchable()
	{
		return maxTimesResearchable;
	}
	
	/**
	 * researches the tech
	 * @throws InvalidConfigFileException 
	 * @throws FileNotFoundException 
	 */
	public void research() throws FileNotFoundException, InvalidConfigFileException
	{
		for(Entry<String, HashMap<ParserKeys, Modifier>> URI : modificationChain.entrySet()){
			Upgradable toModify = owner.getUpgradable(URI.getKey());
			for(Entry<ParserKeys, Modifier> modification : URI.getValue().entrySet()){
				modification.getValue().modify(toModify.getParser(), modification.getKey(), currentResearch);
				toModify.forceReloadConfigData();
			}
		}
		currentResearch++;
	}
	
	/**
	 * gets the name of this tech
	 * 
	 * @return	the name
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * returns the tooltip description of the tech
	 * 
	 * @return	the description
	 */
	public String getDescription()
	{
		return tooltip;
	}
	
	public Function getCostFunction()
	{
		return costFunction;
	}
		
	public String getIconURI()
	{
		return iconURI;
	}
	
	public String getPressedIconURI()
	{
		return pressedIconURI;
	}
	
	public String getRolloverIconURI()
	{
		return rolloverIconURI;
	}
	
	public String getSelectedIconURI()
	{
		return selectedIconURI;
	}

}
