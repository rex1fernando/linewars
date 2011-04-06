package linewars.gamestate.shapes.configurations;

import java.util.ArrayList;
import java.util.List;

import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.shapes.Shape;
import linewars.gamestate.shapes.ShapeAggregate;
import configuration.ListConfiguration;
import configuration.Property;
import configuration.Usage;

public class ShapeAggregateConfiguration extends ShapeConfiguration{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5604264713251858760L;

	private ListConfiguration<ShapeConfiguration> shapesList;
	
	private String shapesListKey = "shapes";
	private Usage shapesListUsage = Usage.CONFIGURATION;
	
	public ShapeAggregateConfiguration(ArrayList<ShapeConfiguration> allShapes, ArrayList<ShapeConfiguration> enabledSubList, ArrayList<String> names){
		this();
		setAllShapes(allShapes, enabledSubList, names);
	}
	
	public ShapeAggregateConfiguration()
	{
		shapesList = new ListConfiguration<ShapeConfiguration>(new ArrayList<ShapeConfiguration>(), new ArrayList<String>(), new ArrayList<Usage>());
		
		this.setPropertyForName(shapesListKey, new Property(shapesListUsage, shapesList));
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<ShapeConfiguration> getAllShapes(){
		return (ArrayList<ShapeConfiguration>) shapesList.getFullList().clone();
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<ShapeConfiguration> getInitialShapes(){
		return (ArrayList<ShapeConfiguration>) shapesList.getEnabledSubList().clone();
	}
	
	public void setAllShapes(ArrayList<ShapeConfiguration> newFullList, ArrayList<ShapeConfiguration> enabledSubList, ArrayList<String> names){
		ArrayList<Usage> usages = new ArrayList<Usage>();
		for(int i = 0; i < newFullList.size(); i++){
			usages.add(Usage.CONFIGURATION);
		}
		
		shapesList = new ListConfiguration<ShapeConfiguration>(newFullList, names, usages);
		
		this.setPropertyForName(shapesListKey, new Property(shapesListUsage, shapesList));
		
		//Set the list of ShapeConfigurations that start enabled
		//This updates our internal state as a side effect
		shapesList.setEnabledSubList(enabledSubList);
	}
	
	public boolean setInitialShapes(ArrayList<ShapeConfiguration> newInitialList){
		if(!shapesList.getFullList().containsAll(newInitialList)){
			return false;
		}
		shapesList.setEnabledSubList(newInitialList);
		return true;
	}
	
	/**
	 * This method sets the name name to refer to shapeConfig.
	 * 
	 * @param name
	 * @param shapeConfig
	 * @param enabled
	 */
	public void setShapeConfigurationForName(String name, ShapeConfiguration shapeConfig, boolean enabled)
	{
		ArrayList<ShapeConfiguration> currentShapes = shapesList.getFullList();
		ArrayList<ShapeConfiguration> currentlyEnabledShapes = shapesList.getEnabledSubList();
		ArrayList<String> currentNames = shapesList.getNames();
		
		int index = currentShapes.indexOf(shapeConfig);
		currentNames.set(index, name);
		
		if(enabled){
			if(!currentlyEnabledShapes.contains(shapeConfig)){
				currentlyEnabledShapes.add(shapeConfig);
			}
		}else if(currentlyEnabledShapes.contains(shapeConfig)){
			currentlyEnabledShapes.remove(shapeConfig);
		}
		
		setAllShapes(currentShapes, currentlyEnabledShapes, currentNames);
	}
	
	/**
	 * Gets the shape configuration that is associated with name, as set by
	 * setShapeConfigurationForName.
	 * 
	 * @param name
	 * @return
	 */
	public ShapeConfiguration getShapeConfigurationForName(String name)
	{
		ArrayList<String> names = shapesList.getNames();
		int index = names.indexOf(name);
		return shapesList.getFullList().get(index);
	}
	
	/**
	 * Takes in the name of a shape as set by setShapeConfigurationForName and returns
	 * the value of the enabled flag for that shape.
	 * 
	 * @param shapeName
	 * @return
	 */
	public boolean isInitiallyEnabled(String shapeName)
	{
		ArrayList<String> names = shapesList.getNames();
		int index = names.indexOf(shapeName);
		ShapeConfiguration shapeInQuestion = shapesList.getFullList().get(index);
		return shapesList.getEnabledSubList().contains(shapeInQuestion);
	}
	
	/**
	 * Gets the list of all shape configuration names set by setShapeConfigurationForName
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<String> getDefinedShapeNames()
	{
		return (List<String>) shapesList.getNames().clone();
	}

	@Override
	public Shape construct(Transformation location) {
		Transformation zeroMovement = new Transformation(new Position(0, 0), 0);
		ArrayList<Shape> shapes = new ArrayList<Shape>();
		ArrayList<Transformation> relativePositions = new ArrayList<Transformation>();
		for(int i = 0; i < shapesList.getEnabledSubList().size(); i++){
			shapes.add(shapesList.getEnabledSubList().get(i).construct(zeroMovement));
			relativePositions.add(zeroMovement);
		}
		return new ShapeAggregate(zeroMovement, shapes, relativePositions);
	}
}
