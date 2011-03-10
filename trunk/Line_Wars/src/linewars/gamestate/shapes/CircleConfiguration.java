package linewars.gamestate.shapes;

import utility.Observable;
import utility.Observer;

import configuration.Property;
import configuration.Usage;
import linewars.gamestate.Transformation;

public class CircleConfiguration extends ShapeConfiguration implements Observer {

	private static final long serialVersionUID = 5598610257920078118L;
	
	private double radius;
	private String radiusKey = "radius";
	private Usage radiusUsage = Usage.NUMERIC_FLOATING_POINT;
	
	private Transformation position;
	private String positionKey = "position";
	private Usage positionUsage = Usage.TRANSFORMATION;
	
	public CircleConfiguration(double radius, Transformation position){
		this.addObserver(this);
		this.setPropertyForName(radiusKey, new Property(radiusUsage, radius));
		this.setPropertyForName(positionKey, new Property(positionUsage, position));
	}
	
	@Override
	public Shape construct(Transformation location) {
		return new Circle(location.add(position), radius);
	}
	
	public double getRadius(){
		return radius;
	}
	
	public Transformation getPosition(){
		return position;
	}
	
	public void setRadius(double newRadius){
		this.setPropertyForName(radiusKey, new Property(radiusUsage, newRadius));
	}
	
	public void setPosition(Transformation newPosition){
		this.setPropertyForName(positionKey, new Property(positionUsage, position));
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if(arg0 != this){
			return;
		}
		if(!(arg1 instanceof String)){
			return;
		}
		String propertyName = (String) arg1;
		if(radiusKey.equals(propertyName)){
			radius = (Double) this.getPropertyForName(radiusKey).getValue();
		}else if(positionKey.equals(propertyName)){
			position = (Transformation) this.getPropertyForName(positionKey).getValue();
		}
	}
}
