package linewars.gamestate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import linewars.gamestate.mapItems.Building;
import linewars.gamestate.mapItems.BuildingDefinition;
import linewars.gamestate.mapItems.GateDefinition;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.Projectile;
import linewars.gamestate.mapItems.Unit;
import linewars.gamestate.mapItems.UnitDefinition;
import linewars.gamestate.playerabilities.PlayerAbility;


/**
 * 
 * @author John George
 *
 * This class represents a player of the game and contains all of the state that a player needs to know.
 */
public strictfp class Player {

	private GameState gameState;
	private int playerID;
	private double stuffAmount;
	private ArrayList<Node> ownedNodes;
	private HashMap<Lane, Node> startPoints;
	private HashMap<Lane, Double> flowDist;
	private ArrayList<Unit> ownedUnits;
	private ArrayList<Building> ownedBuildings;
	private ArrayList<Projectile> ownedProjectiles;
	private String name;
	private Race race;
	private double energy = 0;
	
	public Player(GameState gameState, Node[] startingNodes, Race r, String name, int ID) {
		stuffAmount = gameState.getStartingStuffAmount();
		this.gameState = gameState;
		playerID = ID;
		this.name = name;
		
		ownedBuildings = new ArrayList<Building>();
		ownedNodes = new ArrayList<Node>();
		ownedProjectiles = new ArrayList<Projectile>();
		ownedUnits = new ArrayList<Unit>();
		
		for(int i = 0; i < startingNodes.length; i++)
		{
			ownedNodes.add(startingNodes[i]);
		}
		
		flowDist = new HashMap<Lane, Double>();
		startPoints = new HashMap<Lane, Node>();
		flowSetup();
		
		race = r;
		
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
		Lane[] lanes = gameState.getMap().getLanes();
		for(Lane l : lanes)
		{
			Node[] currentNodes = l.getNodes();
			boolean foundOwnedNode = false;
			for(Node n : currentNodes)
			{
				if(ownedNodes.contains(n) && !foundOwnedNode)
				{
					startPoints.put(l, n);
					foundOwnedNode = true;
				}
			}
			if(!foundOwnedNode)
			{
				startPoints.put(l, l.getNodes()[0]);
			}
			flowDist.put(l, new Double(50));
		}
	}
	
	public List<PlayerAbility> getUnlockedPlayerAbilities()
	{
		return race.getUnlockedPlayerAbilites();
	}
	
	public List<PlayerAbility> getAllPlayerAbilities()
	{
		return race.getAllPlayerAbilites();
	}
	
	public double getPlayerEnergy()
	{
		return energy;
	}
	
	public void setPlayerEnergy(double e)
	{
		energy = e;
	}
	
	/**
	 * Checks whether or not the Node n is a start point for flow distribution for this player for
	 * the specified Lane.
	 * @param l
	 * 		The Lane for which to check n.
	 * @param n
	 * 		The Node being checked.
	 * @return
	 * 		true if n is a start point for this Player for l, false otherwise.
	 */
	public boolean isStartPoint(Lane l, Node n)
	{
		return startPoints.get(l).equals(n);
	}
	
	/**
	 * 
	 * @return
	 * 		The amount of Stuff this Player owns.
	 */
	public double getStuff(){
		return stuffAmount;
	}
	
	/**
	 * 
	 * @return
	 * 		This Player's name.
	 */
	public String getPlayerName(){
		return name;
	}
	
	/**
	 * 
	 * @param l
	 * 		The lane for which to get this Player's flow distribution.
	 * @return
	 * 		This Player's flow distribution for the specified Lane.
	 */
	public double getFlowDist(Lane l){
		return flowDist.get(l);
	}
	
	/**
	 * 
	 * @return
	 * 		The HashMap representing the flow distribution of this Player.
	 */
	public HashMap<Lane, Double> getFlowDist()
	{
		return flowDist;
	}
	
	/**
	 * 
	 * @return
	 * 		An ArrayList containing all of the Nodes owned by this Player.
	 */
	public ArrayList<Node> getOwnedNodes(){
		return ownedNodes;
	}
	
	/**
	 * 
	 * @return
	 * 		An array containing all of the UnitDefinitions owned by this Player.
	 */
	public UnitDefinition[] getUnitDefinitions(){
		return race.getUnlockedUnits().toArray(new UnitDefinition[0]);
	}
	
	/**
	 * 
	 * @return
	 * 		An array containing all of the BuildingDefinitions owned by this Player.
	 */
	public BuildingDefinition[] getBuildingDefintions()
	{
		return race.getUnlockedBuildings().toArray(new BuildingDefinition[0]);
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
	public void setFlowDist(Lane l, Double val, int nodeID)
	{
		if(startPoints.get(l).getID() != nodeID)
		{
			swapStartPoints(l);
		}
		flowDist.put(l, val);
	}
	
	/**
	 * Swaps the start point for this player for the given Lane.
	 * @param l The lane to swap start points.
	 */
	private void swapStartPoints(Lane l)
	{
		Node[] nodes = l.getNodes();
		if(nodes[0].equals(startPoints.get(l)))
		{
			setStartPoint(l, nodes[1]);
		}else{
			setStartPoint(l, nodes[0]);
		}
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
	 * 
	 * @return
	 * 		This Player's CommandCenterDefinition.
	 */
	public BuildingDefinition getCommandCenterDefinition()
	{
		return race.getCommandCenter();
	}
	
	/**
	 * 
	 * @return
	 * 		This Player's gateDefinition.
	 */
	public GateDefinition getGateDefinition()
	{
		return race.getGate();
	}
	
	/**
	 * 
	 * @return
	 * 		This Player's playerID.
	 */
	public int getPlayerID()
	{
		return playerID;
	}
	
	@Override
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
	
	/**
	 * Removes u from this Player's list of owned Units.
	 * @param u
	 * 		The Unit to be removed from the list of owned Units.
	 */
	public void removeUnit(Unit u)
	{
		ownedUnits.remove(u);
	}
	
	/**
	 * Removes u from this Player's list of owned Buildings.
	 * @param u
	 * 		The Building to be removed from the list of owned Buildings.
	 */
	public void removeBuilding(Building u)
	{
		ownedBuildings.remove(u);
	}
	
	/**
	 * Removes u from this Player's list of owned Projectiles.
	 * @param u
	 * 		The Projectile to be removed from the list of owned Projectiles.
	 */
	public void removeProjectile(Projectile u)
	{
		ownedProjectiles.remove(u);
	}
	
	/**
	 * 
	 * @return
	 * 		The GameState object owned by this Player.
	 */
	public GameState getGameState()
	{
		return gameState;
	}
	
	/**
	 * 
	 * @param l
	 * 		The Lane of which to check the adjacent Nodes.
	 * @return
	 * 		The flow start node of this Player for the specified Lane.
	 */
	public Node getStartNode(Lane l) {
		return this.startPoints.get(l);
	}

	/**
	 * 
	 * @return
	 * 		The Race object which defines this Player's race.
	 */
	public Race getRace() {
		return race;
	}
}
