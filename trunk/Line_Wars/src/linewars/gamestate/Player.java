package linewars.gamestate;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Map.Entry;

import linewars.gamestate.mapItems.*;
import linewars.parser.Parser.InvalidConfigFileException;

public class Player {

	private GameState gameState;
	private int playerID;
	private double stuffAmount;
	private ArrayList<Node> ownedNodes;
	private HashMap<Lane, Node> startPoints;
	private HashMap<Lane, Double> flowDist;
	private ArrayList<Unit> ownedUnits;
	private ArrayList<Building> ownedBuildings;
	private ArrayList<Projectile> ownedProjectiles;
	private HashMap<String, BuildingDefinition> buildingDefs;
	private HashMap<String, UnitDefinition> unitDefs;
	private HashMap<String, ProjectileDefinition> projDefs;
	private HashMap<String, Tech> techLevels;
	private CommandCenterDefinition ccd;
	private GateDefinition gateDefinition;
	private String name;
	
	public Player(GameState gameState, Node[] startingNodes, Race r, String name, int ID) throws FileNotFoundException, InvalidConfigFileException{
		stuffAmount = gameState.getStartingStuffAmount();
		this.gameState = gameState;
		playerID = ID;
		this.name = name;
		
		ownedNodes = new ArrayList<Node>();
		
		for(int i = 0; i < startingNodes.length; i++)
		{
			ownedNodes.add(startingNodes[i]);
		}
		
		flowDist = new HashMap<Lane, Double>();
		startPoints = new HashMap<Lane, Node>();
		flowSetup();
		
		projDefs = new HashMap<String, ProjectileDefinition>();
		
		buildingDefs = new HashMap<String, BuildingDefinition>();
		List<String> URIs = r.getBuildingURIs();
		for(String uri : URIs)
		{
			BuildingDefinition bd = new BuildingDefinition(uri, this, gameState);
			buildingDefs.put(uri, bd);
		}
		
		unitDefs = new HashMap<String, UnitDefinition>();
		URIs = r.getUnitURIs();
		for(String uri : URIs)
		{
			UnitDefinition ud = new UnitDefinition(uri, this, gameState);
			unitDefs.put(uri, ud);
		}
		
		techLevels = new HashMap<String, Tech>();
		URIs = r.getTechURIs();
		for(String uri : URIs)
		{
			Tech t = new Tech(uri, this);
			techLevels.put(uri, t);
		}
		
		ccd = new CommandCenterDefinition(r.getCommandCenterURI(), this, gameState);
		gateDefinition = new GateDefinition(r.getGateURI(), this, gameState);
		
		for(Node n : startingNodes)
			n.setOwner(this);
	}
	
	/**
	 * Sets up the initial flow distribution for this player. For each lane,
	 * set that lane's flow distribution to 50 and, if available, set an owned
	 * node attached to that lane as the start point for flow. If no owned node
	 * is attached to that lane, set a random one as the start point.
	 */
	private void flowSetup()
	{
		boolean foundOwnedNode = false;
		Lane[] lanes = gameState.getMap().getLanes();
		for(int i = 0; i < lanes.length; i++)
		{
			Node[] currentNodes = lanes[i].getNodes();
			for(int j = 0; j < currentNodes.length; j++)
			{
				if(ownedNodes.contains(currentNodes[j]) && !foundOwnedNode)
				{
					startPoints.put(lanes[i], currentNodes[j]);
					foundOwnedNode = true;
				}
			}
			if(!foundOwnedNode)
			{
				startPoints.put(lanes[i], lanes[i].getNodes()[0]);
			}
			flowDist.put(lanes[i], new Double(50));
		}
	}
	
	public boolean isStartPoint(Lane l, Node n)
	{
		return startPoints.get(l).equals(n);
	}
	
	public double getStuff(){
		return stuffAmount;
	}
	
	public String getPlayerName(){
		return name;
	}
	
	public double getFlowDist(Lane l){
		return flowDist.get(l);
	}
	
	public HashMap<Lane, Double> getFlowDist()
	{
		return flowDist;
	}
	
	public ArrayList<Node> getOwnedNodes(){
		return ownedNodes;
	}
	
	public UnitDefinition[] getUnitDefinitions(){
		Set<Entry<String, UnitDefinition>> set = unitDefs.entrySet();
		Iterator<Entry<String, UnitDefinition>> it = set.iterator();
		ArrayList<UnitDefinition> ret = new ArrayList<UnitDefinition>();
		while(it.hasNext())
			ret.add(it.next().getValue());
		return ret.toArray(new UnitDefinition[0]);
	}
	
	public BuildingDefinition[] getBuildingDefintions()
	{
		Set<Entry<String, BuildingDefinition>> set = buildingDefs.entrySet();
		Iterator<Entry<String, BuildingDefinition>> it = set.iterator();
		ArrayList<BuildingDefinition> ret = new ArrayList<BuildingDefinition>();
		while(it.hasNext())
			ret.add(it.next().getValue());
		return ret.toArray(new BuildingDefinition[0]);
	}
	
	public Tech[] getTech()
	{
		Set<Entry<String, Tech>> set = techLevels.entrySet();
		Iterator<Entry<String, Tech>> it = set.iterator();
		ArrayList<Tech> ret = new ArrayList<Tech>();
		while(it.hasNext())
			ret.add(it.next().getValue());
		return ret.toArray(new Tech[0]);
	}
	
	/**
	 * adds the given mapItem to the player's master list of owned items
	 * 
	 * @param m	the mapItem to add
	 */
	public void addMapItem(MapItem m)
	{
		if(m instanceof Building)
			ownedBuildings.add((Building) m);
		else if(m instanceof Unit)
			ownedUnits.add((Unit) m);
		else if(m instanceof Projectile)
			ownedProjectiles.add((Projectile) m);
	}
	
	/**
	 * Adds the specified amount of Stuff to the Player's account.
	 * @param amount The amount of Stuff to be added. Note that negative values
	 * 					are simply ignored.
	 */
	public void addStuff(double amount)
	{
		if(amount > 0)
		{
			stuffAmount = stuffAmount + amount;
		}
	}
	
	/**
	 * Sets the magnitude of the flow distribution for the lane l to the specified
	 * value.
	 * @param l The lane which is having its distribution modified.
	 * @param val The new magnitude for the flow distribution.
	 */
	public void setFlowDist(Lane l, Double val)
	{
		flowDist.put(l, val);
	}
	
	/**
	 * Sets the specified node as the start point for flow, meaning units will move
	 * from node n to the opposite end of the lane.
	 * @param l The lane in question.
	 * @param n The desired starting node for the lane l.
	 */
	public void setStartPoint(Lane l, Node n)
	{
		startPoints.put(l, n);
	}
		
	/**
	 * Spends the given amount of stuff, throws an exception if there
	 * isn't enough stuff left to spend
	 * 
	 * @param amount	the amount of stuff to spend
	 */
	public void spendStuff(double amount) throws IllegalArgumentException
	{
		if(amount > stuffAmount){
			throw new IllegalArgumentException("Not enough Stuff. " +name
					+" Tried spending " +amount +" Stuff but only has " +stuffAmount);
		}
		stuffAmount = stuffAmount - amount;
	}
	
	/**
	 * This method takes in a URI and returns the associated unitDefinition. If
	 * that unitDefinition is not yet loaded, it loads it and then returns it.
	 * Throws an exception if the definition can't be loaded
	 * 
	 * @param URI	the URI of the unit definition
	 * @return		the unit definition
	 * @throws InvalidConfigFileException 
	 * @throws FileNotFoundException 
	 */
	public UnitDefinition getUnitDefinition(String URI) throws FileNotFoundException, InvalidConfigFileException
	{
		UnitDefinition ud = unitDefs.get(URI);
		if(ud == null)
		{
			ud = new UnitDefinition(URI, this, gameState);
			unitDefs.put(URI, ud);
		}
		return ud;
	}
	
	/**
	 * This method takes in a URI and returns the associated tech. If
	 * that tech is not yet loaded, it loads it and then returns it.
	 * Throws an exception if the tech can't be loaded
	 * 
	 * @param URI	the URI of the unit definition
	 * @return		the unit definition
	 * @throws InvalidConfigFileException 
	 * @throws FileNotFoundException 
	 */
	public Tech getTech(String URI) throws FileNotFoundException, InvalidConfigFileException
	{
		Tech td = techLevels.get(URI);
		if(td == null)
		{
			td = new Tech(URI, this);
			techLevels.put(URI, td);
		}
		return td;
	}
	
	/**
	 * This method takes in a URI and returns the associated projectileDefinition. If
	 * that projectileDefinition is not yet loaded, it loads it and then returns it.
	 * Throws an exception if the definition can't be loaded
	 * 
	 * @param URI	the URI of the projectile definition
	 * @return		the projectile definition
	 * @throws InvalidConfigFileException 
	 * @throws FileNotFoundException 
	 */
	public ProjectileDefinition getProjectileDefinition(String URI) throws FileNotFoundException, InvalidConfigFileException
	{
		ProjectileDefinition pd = projDefs.get(URI);
		if(pd == null)
		{
			pd = new ProjectileDefinition(URI, this, gameState);
			projDefs.put(URI, pd);
		}
		return pd;
	}
	
	/**
	 * This method takes in a URI and returns the associated BuildingDefinition. If
	 * that BuildingDefinition is not yet loaded, it loads it and then returns it.
	 * Throws an exception if the definition can't be loaded
	 * 
	 * @param URI	the URI of the building definition
	 * @return		the building definition
	 * @throws InvalidConfigFileException 
	 * @throws FileNotFoundException 
	 */
	public BuildingDefinition getBuildingDefinition(String URI) throws FileNotFoundException, InvalidConfigFileException
	{
		BuildingDefinition bd = buildingDefs.get(URI);
		if(bd == null)
		{
			bd = new BuildingDefinition(URI, this, gameState);
			buildingDefs.put(URI, bd);
		}
		return bd;
	}
	
	/**
	 * Gets the MapItemDefinition associated with the URI. Attempts to
	 * load it if it needs to, returns null if unsuccessfull.
	 * 
	 * @param URI	the URI associated with the MapItemDefinition	
	 * @return		the MapItemDefinition associated with the URI
	 */
	public MapItemDefinition getMapItemDefinition(String URI)
	{
		MapItemDefinition mid = null;
		
		mid = projDefs.get(URI);
		if(mid != null)
			return mid;
		
		mid = unitDefs.get(URI);
		if(mid != null)
			return mid;
		
		mid = buildingDefs.get(URI);
		return mid;
	}
	
	public CommandCenterDefinition getCommandCenterDefinition()
	{
		return ccd;
	}
	
	public GateDefinition getGateDefinition()
	{
		return gateDefinition;
	}
	
	public int getPlayerID()
	{
		return playerID;
	}
	
	public boolean equals(Object obj)
	{
		if(obj == null){
			return false;
		}
		
		if(!this.getClass().equals(obj.getClass()))
		{
			return false;
		}
		
		Player other = (Player)obj;
		
		if(this.playerID == other.getPlayerID())
		{
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return playerID;
	}
}
