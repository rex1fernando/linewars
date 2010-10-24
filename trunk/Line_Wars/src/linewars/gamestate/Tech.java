package linewars.gamestate;

import linewars.parser.Parser;

public class Tech {
	
	public Tech(String URI, Player owner)
	{
		//TODO
	}
	
	//TODO implement researchable
	/**
	 * This method checks to see if the pre-req's for this
	 * tech have been researched.
	 * 
	 * @return	true if the pre-req's have been researched, false otherwise
	 */
	public boolean researchable()
	{
		return false;
	}
	
	//TODO implement maxTimesResearchable
	/**
	 * This method returns the maximum number of times this tech
	 * is allowed to be researched.
	 * 
	 * @return	the number of times to allow research
	 */
	public int maxTimesResearchable()
	{
		return 0;
	}
	
	//TODO implement research (maybe make it abstract)
	/**
	 * researches the tech
	 */
	public void research()
	{
		
	}
	
	//TODO implement getName 
	/**
	 * gets the name of this tech
	 * 
	 * @return	the name
	 */
	public String getName()
	{
		return null;
	}
	
	//TODO implement getDescription
	/**
	 * returns the tooltip description of the tech
	 * 
	 * @return	the description
	 */
	public String getDescription()
	{
		return null;
	}
	
	//TODO implement getParser
	/**
	 * gets the parser associated with this tech
	 * 
	 * @return	the parser
	 */
	public Parser getParser()
	{
		return null;
	}

}
