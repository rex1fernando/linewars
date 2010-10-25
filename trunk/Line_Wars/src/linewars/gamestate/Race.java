package linewars.gamestate;

import java.util.List;

import linewars.parser.Parser;
import linewars.parser.ParserKeys;

//TODO implement Race
public class Race {
	
	private String name;
	private String[] unitURIs;
	private List<String> techURIs;
	private List<String> buildingURIs;
	private String commandCenterURI;
	
	public Race(Parser p)
	{
		name = p.getStringValue(ParserKeys.name);
		unitURIs = p.getList(ParserKeys.unitURI);
	}
	
	//TODO
	public List<String> getBuildingURIs()
	{
		return null;
	}
	
	//TODO
	public List<String> getUnitURIs()
	{
		return null;
	}
	
	//TODO
	public List<String> getTechURIs()
	{
		return null;
	}
	
	//TODO
	@Override
	public boolean equals(Object o)
	{
		return false;
	}
	
	//TODO
	public String getCommandCenterURI()
	{
		return null;
	}

}
