package linewars.gamestate.mapItems;

import java.util.ArrayList;
import java.util.List;

import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.shapes.Shape;
import linewars.gamestate.shapes.ShapeAggregate;

public abstract class MapItemAggregate extends MapItem {

	private List<MapItem> containedItems = new ArrayList<MapItem>();
	private Transformation transform;
	
	private ShapeAggregate body = null;
	
	public MapItemAggregate(Transformation trans, MapItemDefinition<? extends MapItemAggregate> def) {
		super(trans, def);
		transform = trans;
	}
	
	public void addMapItem(MapItem m, Transformation t)
	{
		m.setTransformation(new Transformation(this.getPosition().add(t.getPosition()), t.getRotation()));
		containedItems.add(m);
		body = null;
	}
	
	public MapItem[] getContainedItems()
	{
		return containedItems.toArray(new MapItem[0]);
	}
	
	@Override
	public Position getPosition()
	{
		return transform.getPosition();
	}
	
	@Override
	public double getRotation()
	{
		return transform.getRotation();
	}
	
	@Override
	public Transformation getTransformation()
	{
		return transform;
	}
	
	@Override
	public void setPosition(Position newPos)
	{
		Position oldPos = transform.getPosition();
		for(MapItem m : containedItems)
		{
			Position diff = m.getPosition().subtract(oldPos);
			m.setPosition(newPos.add(diff));
		}
		transform = new Transformation(newPos, getRotation());
		body = null;
	}
	
	@Override
	public void setRotation(double newRot)
	{
		double rot = newRot - transform.getRotation();
		for(MapItem m : containedItems)
		{
			m.setPosition(m.getPosition().rotateAboutPosition(getPosition(), rot));
			m.setRotation(rot);
		}
		transform = new Transformation(transform.getPosition(), newRot);
		body = null;
	}
	
	@Override
	public void setTransformation(Transformation t)
	{
		setPosition(t.getPosition());
		setRotation(t.getRotation());
		body = null;
	}
	
	@Override
	public boolean isCollidingWith(MapItem m)
	{
		if(!this.getCollisionStrategy().canCollideWith(m))
			return false;
		for(MapItem c : containedItems)
			if(c.getBody().isCollidingWith(m.getBody()))
				return true;
		
		return false;
	}
	
	@Override
	public Shape getBody()
	{
		if(body == null)
		{
			ArrayList<Shape> shapes = new ArrayList<Shape>();
			ArrayList<Transformation> pos = new ArrayList<Transformation>();
			
			for(MapItem m : containedItems)
			{
				shapes.add(m.getBody());
				pos.add(new Transformation(m.getPosition().subtract(this.getPosition()), m.getRotation() - this.getRotation()));
			}
			
			body = new ShapeAggregate(transform, shapes, pos);
		}
		return body;
	}
	
	

}
