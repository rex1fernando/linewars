package linewars.gamestate.shapes;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import linewars.gamestate.Position;
import linewars.gamestate.Transformation;

import configuration.Configuration;
import configuration.ListConfiguration;
import configuration.Usage;

public class ShapeAggregateConfiguration extends ShapeConfiguration implements Observer{

	private ArrayList<ShapeConfiguration> allShapes;
	private ArrayList<ShapeConfiguration> initialShapes;
	
	private String shapesListKey = "shapes";
	private Usage shapesListUsage = Usage.CONFIGURATION;
	
	public ShapeAggregateConfiguration(ArrayList<Shape> allShapes, ArrayList<Shape> enabledSubList){
		this.addObserver(this);
		//TODO set up that subconfiguration
		//TODO observe that configuration?
	}
	
	public ArrayList<ShapeConfiguration> getAllShapes(){
		return (ArrayList<ShapeConfiguration>) allShapes.clone();
	}
	
	public ArrayList<ShapeConfiguration> getInitialShapes(){
		return (ArrayList<ShapeConfiguration>) initialShapes.clone();
	}
	
	public void setAllShapes(ArrayList<ShapeConfiguration> newFullList){
		ArrayList<ShapeConfiguration> newInitialList = (ArrayList<ShapeConfiguration>) initialShapes.clone();
		newInitialList.retainAll(newFullList);
		//TODO set up the configuration and set it
		//TODO don't forget to observe it!
	}
	
	public boolean setInitialShapes(ArrayList<ShapeConfiguration> newInitialList){
		if(!allShapes.containsAll(newInitialList)){//TODO add this functionality to ListConfiguration?
			return false;
		}
		//TODO set up the new configuration and set ti
		//TODO observe it too?
		return true;
	}

	@Override
	public Shape construct(Transformation location) {
		Transformation zeroMovement = new Transformation(new Position(0, 0), 0);
		ArrayList<Shape> shapes = new ArrayList<Shape>();
		ArrayList<Transformation> relativePositions = new ArrayList<Transformation>();
		for(int i = 0; i < initialShapes.size(); i++){
			shapes.add(initialShapes.get(i).construct(zeroMovement));
			relativePositions.add(zeroMovement);
		}
		return new ShapeAggregate(zeroMovement, shapes, relativePositions);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		//we can ignore these arguments and just update our state...
		ListConfiguration<ShapeConfiguration> subConfig = (ListConfiguration<ShapeConfiguration>) this.getPropertyForName(shapesListKey).getValue();
		allShapes = subConfig.getFullList();
		initialShapes = subConfig.getEnabledSubList();
	}
}
