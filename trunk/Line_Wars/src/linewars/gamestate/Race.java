package linewars.gamestate;

import java.util.ArrayList;
import java.util.List;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;


/**
 * 
 * @author John George
 *
 */
public strictfp class Race {
	
	private String name;
	private List<String> unitURIs;
	private List<String> techURIs;
	private List<String> buildingURIs;
	private String commandCenterURI;
	private String gateURI;
	
	public Race(ConfigData p)
	{
		name = p.getString(ParserKeys.name);
		unitURIs = p.getStringList(ParserKeys.unitURI);
		techURIs = p.getStringList(ParserKeys.techURI);
		buildingURIs = p.getStringList(ParserKeys.buildingURI);
		commandCenterURI = p.getString(ParserKeys.commandCenterURI);
		gateURI = p.getString(ParserKeys.gateURI);
	}
	
	public List<String> getBuildingURIs()
	{
		ArrayList<String> ret = new ArrayList<String>();
		for(int i = 0; i < buildingURIs.size(); i++)
		{
			ret.add(buildingURIs.get(i));
		}
		return ret;
	}

	public List<String> getUnitURIs()
	{
		ArrayList<String> ret = new ArrayList<String>();
		for(int i = 0; i < unitURIs.size(); i++)
		{
			ret.add(unitURIs.get(i));
		}
		return ret;
	}
	
	public List<String> getTechURIs()
	{
		ArrayList<String> ret = new ArrayList<String>();
		for(int i = 0; i < techURIs.size(); i++)
		{
			ret.add(techURIs.get(i));
		}
		return ret;
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

	public String getCommandCenterURI()
	{
		return commandCenterURI;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getGateURI()
	{
		return gateURI;
	}

}
