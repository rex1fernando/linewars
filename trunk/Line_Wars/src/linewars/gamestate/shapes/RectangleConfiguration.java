package linewars.gamestate.shapes;

import utility.Observable;
import utility.Observer;

import configuration.Property;
import configuration.Usage;
import linewars.gamestate.Transformation;

public class RectangleConfiguration extends ShapeConfiguration implements Observer {
	
	private static final long serialVersionUID = -2133103860808971483L;
	
	private double width;
	private String widthKey = "width";
	private Usage widthUsage = Usage.NUMERIC_FLOATING_POINT;

	private double height;
	private String heightKey = "height";
	private Usage heightUsage = Usage.NUMERIC_FLOATING_POINT;
	
	private Transformation position;
	private String positionKey = "position";
	private Usage positionUsage = Usage.TRANSFORMATION;
	
	public RectangleConfiguration(double width, double height, Transformation position){
		this.addObserver(this);
		this.setPropertyForName(widthKey, new Property(widthUsage, width));
		this.setPropertyForName(heightKey, new Property(heightUsage, height));
		this.setPropertyForName(positionKey, new Property(positionUsage, position));
	}
	
	@Override
	public Shape construct(Transformation location) {
		return new Rectangle(position, width, height);
	}
	
	public double getHeight(){
		return height;
	}
	
	public double getWidth(){
		return width;
	}
	
	public Transformation getPosition(){
		return position;
	}
	
	public void setHeight(double newHeight){
		this.setPropertyForName(heightKey, new Property(heightUsage, newHeight));
	}
	
	public void setWidth(double newWidth){
		this.setPropertyForName(widthKey, new Property(widthUsage, newWidth));
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
		if(widthKey.equals(propertyName)){
			width = (Double) this.getPropertyForName(widthKey).getValue();
		}else if(heightKey.equals(propertyName)){
			height = (Double) this.getPropertyForName(heightKey).getValue();
		}else if(positionKey.equals(propertyName)){
			position = (Transformation) this.getPropertyForName(positionKey).getValue();
		}
	}
}
