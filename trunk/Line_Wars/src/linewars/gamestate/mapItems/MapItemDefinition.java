package linewars.gamestate.mapItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.abilities.AbilityDefinition;
import linewars.gamestate.mapItems.strategies.collision.AllEnemies;
import linewars.gamestate.mapItems.strategies.collision.AllEnemyUnits;
import linewars.gamestate.mapItems.strategies.collision.CollidesWithAll;
import linewars.gamestate.mapItems.strategies.collision.CollisionStrategy;
import linewars.gamestate.mapItems.strategies.collision.CollisionStrategyConfiguration;
import linewars.gamestate.mapItems.strategies.collision.Ground;
import linewars.gamestate.mapItems.strategies.collision.NoCollision;
import linewars.gamestate.shapes.Shape;
import linewars.gamestate.shapes.ShapeConfiguration;
import configuration.Configuration;
import configuration.ListConfiguration;
import configuration.Property;
import configuration.Usage;

/**
 * 
 * @author , Connor Schenck
 *
 * This class represents a definition for a map item. It is used
 * to create the map items it defines (similar to the way a class
 * is used to define how to create objects of its type). For the
 * map items it creates, it knows what states they are allowed to
 * be in, what their name is, what parser they use, what abilities
 * they are allowed to use, who owns them, and what collision strategy
 * they use.
 */
public strictfp abstract class MapItemDefinition<T extends MapItem> extends Configuration implements Observer {
	
	private ArrayList<MapItemState> validStates;
	private String name;
	protected ArrayList<AbilityDefinition> abilities;
	protected CollisionStrategyConfiguration cStrat;
	protected ShapeConfiguration body;
	
	/**
	 * Creates a map item definition.
	 */
	public MapItemDefinition()
	{
		super.setPropertyForName("validStates", 
				new Property(Usage.CONFIGURATION, 
						new ListConfiguration<MapItemState>(new ArrayList<MapItemState>(), 
								new ArrayList<String>(), new ArrayList<Usage>())));
		super.setPropertyForName("name", new Property(Usage.STRING, ""));
		super.setPropertyForName("abilities", new Property(Usage.CONFIGURATION,
				new ListConfiguration<AbilityDefinition>(new ArrayList<AbilityDefinition>(), 
						new ArrayList<String>(), new ArrayList<Usage>())));
		super.setPropertyForName("cStrat", new Property(Usage.CONFIGURATION, null));
		super.setPropertyForName("body", new Property(Usage.CONFIGURATION, null));
		this.forceReloadConfigData();
		this.addObserver(this);
	}

	/**
	 * Checks to see if the given state is valid for this type of map item
	 * 
	 * @param m	the state
	 * @return	true if its valid, false otherwise
	 */
	public boolean isValidState(MapItemState m)
	{
		return validStates.contains(m);
	}
	
	/**
	 * 
	 * @return	the name of the map items
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * 
	 * @return	the list of availabel ability definitions
	 */
	public AbilityDefinition[] getAbilityDefinitions()
	{
		return abilities.toArray(new AbilityDefinition[0]);
	}
	
	/**
	 * 
	 * @return	the collision strategy associated with this type of map item
	 */
	public CollisionStrategyConfiguration getCollisionStrategyConfig()
	{
		return cStrat;
	}
	
	/**
	 * 
	 * @return	the shape aggregate associated with this map item
	 */
	public ShapeConfiguration getBodyConfig()
	{
		return body;
	}
	
	public abstract T createMapItem(Transformation t);
	
	/**
	 * Forces this definition to reload itself from its config
	 */
	@SuppressWarnings("unchecked")
	public void forceReloadConfigData()
	{
		validStates = ((ListConfiguration<MapItemState>)super.getPropertyForName("validStates").getValue()).getEnabledSubList();
		name = (String)super.getPropertyForName("name").getValue();
		abilities = ((ListConfiguration<AbilityDefinition>)super.getPropertyForName("abilities").getValue()).getEnabledSubList();
		cStrat = (CollisionStrategyConfiguration)super.getPropertyForName("cStrat").getValue();
		body = (ShapeConfiguration)super.getPropertyForName("body").getValue();
		
		this.forceSubclassReloadConfigData();
	}
	
	/**
	 * Forces any subclass of MapItemDefinition to reload itself from its config
	 */
	protected abstract void forceSubclassReloadConfigData();
	
}
