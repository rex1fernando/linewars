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
	
	public Race(Parser p)
	{
		name = p.getStringValue(ParserKeys.name);
		unitURIs = p.getList(ParserKeys.unitURI);
		techURIs = p.getList(ParserKeys.techURI);
		buildingURIs = p.getList(ParserKeys.buildingURI);
		commandCenterURI = p.getStringValue(ParserKeys.commandCenterURI);
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
	
	//TODO
	@Override
	public boolean equals(Object o)
	{
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

}
