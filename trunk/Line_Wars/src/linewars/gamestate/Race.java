package linewars.gamestate;

import java.util.ArrayList;
import java.util.List;

import linewars.parser.Parser;
import linewars.parser.ParserKeys;

//TODO implement Race
public class Race {
	
	private String name;
	private String[] unitURIs;
	private String[] techURIs;
	private String[] buildingURIs;
	private String commandCenterURI;
	private String gateURI;
	
	public Race(Parser p)
	{
		name = p.getStringValue(ParserKeys.name);
		unitURIs = p.getList(ParserKeys.unitURI);
		techURIs = p.getList(ParserKeys.techURI);
		buildingURIs = p.getList(ParserKeys.buildingURI);
		commandCenterURI = p.getStringValue(ParserKeys.commandCenterURI);
		gateURI = p.getStringValue(ParserKeys.gateURI);
	}
	
	public List<String> getBuildingURIs()
	{
		ArrayList<String> ret = new ArrayList<String>();
		for(int i = 0; i < buildingURIs.length; i++)
		{
			ret.add(buildingURIs[i]);
		}
		return ret;
	}

	public List<String> getUnitURIs()
	{
		ArrayList<String> ret = new ArrayList<String>();
		for(int i = 0; i < unitURIs.length; i++)
		{
			ret.add(unitURIs[i]);
		}
		return ret;
	}
	
	public List<String> getTechURIs()
	{
		ArrayList<String> ret = new ArrayList<String>();
		for(int i = 0; i < techURIs.length; i++)
		{
			ret.add(techURIs[i]);
		}
		return ret;
	}
	
	//Is the name of the race sufficient for equals()? Will it be possible to name different races the same thing?
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
