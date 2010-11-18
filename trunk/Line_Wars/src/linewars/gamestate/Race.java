package linewars.gamestate;

import java.util.ArrayList;
import java.util.List;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;


/**
 * 
 * @author John George
 *
 *	This class represents a race, of which each player has one. Races know what units and buildings are available to be 
 *  created by itself.
 */
public strictfp class Race {
	
	private String name;
	private List<String> unitURIs;
	private List<String> techURIs;
	private List<String> buildingURIs;
	private String commandCenterURI;
	private String gateURI;
	
	/**
	 * Creates a new Race object based on the information in the given ConfigData.
	 * @param p
	 * 		The ConfigData object to be used to create this Race.
	 */
	public Race(ConfigData p)
	{
		name = p.getString(ParserKeys.name);
		unitURIs = p.getStringList(ParserKeys.unitURI);
		techURIs = p.getStringList(ParserKeys.techURI);
		buildingURIs = p.getStringList(ParserKeys.buildingURI);
		commandCenterURI = p.getString(ParserKeys.commandCenterURI);
		gateURI = p.getString(ParserKeys.gateURI);
	}
	
	/**
	 * 
	 * @return
	 * 		A List containing all of the BuildingURIs associated with this Race.
	 */
	public List<String> getBuildingURIs()
	{
		ArrayList<String> ret = new ArrayList<String>();
		for(int i = 0; i < buildingURIs.size(); i++)
		{
			ret.add(buildingURIs.get(i));
		}
		return ret;
	}

	/**
	 * 
	 * @return
	 * 		A List containing all of the UnitURIs associated with this Race.
	 */
	public List<String> getUnitURIs()
	{
		ArrayList<String> ret = new ArrayList<String>();
		for(int i = 0; i < unitURIs.size(); i++)
		{
			ret.add(unitURIs.get(i));
		}
		return ret;
	}
	
	/**
	 * 
	 * @return
	 * 		A List containing all of the TechURIs associated with this Race.
	 */
	public List<String> getTechURIs()
	{
		ArrayList<String> ret = new ArrayList<String>();
		for(int i = 0; i < techURIs.size(); i++)
		{
			ret.add(techURIs.get(i));
		}
		return ret;
	}
	
	/**
	 * 
	 * @return
	 * 		The CommandCenterURI associated with this Race.
	 */
	public String getCommandCenterURI()
	{
		return commandCenterURI;
	}
	
	/**
	 * 
	 * @return
	 * 		The name of this Race.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * 
	 * @return
	 * 		The GateURI associated with this Race.
	 */
	public String getGateURI()
	{
		return gateURI;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o == null || o.getClass() != this.getClass()){
			return false;
		}
		
		if(((Race)o).getName() == this.name)
		{
			return true;
		}
		
		return false;
	}

}
