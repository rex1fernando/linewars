package linewars.gamestate.mapItems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;

import linewars.gamestate.GameState;
import linewars.gamestate.Player;
import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.shapes.Shape;
import linewars.gamestate.shapes.ShapeAggregate;

public abstract class MapItemAggregate extends MapItem {

	private List<MapItem> containedItems = new ArrayList<MapItem>();
	private List<Turret> turrets = new ArrayList<Turret>();
	private Transformation transform;
	
	private ShapeAggregate body = null;
	
	public MapItemAggregate(Transformation trans, MapItemDefinition<? extends MapItemAggregate> def, 
			GameState gameState, Player owner) {
		super(trans, def, owner, gameState);
		transform = trans;
	}
	
	public void addMapItem(MapItem m, Transformation t)
	{
		m.setTransformation(new Transformation(this.getPosition().add(t.getPosition()), t.getRotation()));
		containedItems.add(m);
		body = null;
	}
	
	public void removeAllMapItems()
	{
		containedItems.clear();
		body = null;
	}
	
	public List<Turret> getTurrets()
	{
		if(checkForContainedItemsChange(this))
		{
			for(MapItem m : containedItems)
			{
				if(m instanceof Turret)
					turrets.add((Turret)m);
				else if(m instanceof MapItemAggregate)
					turrets.addAll(((MapItemAggregate)m).getTurrets());
			}
		}
		List<Turret> ret = new ArrayList<Turret>();
		Collections.copy(ret, turrets);
		return ret;
	}
	
	public List<MapItem> getContainedItems()
	{
		List<MapItem> ret = new ArrayList<MapItem>();
		Collections.copy(ret, containedItems);
		return ret;
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
		if(checkForContainedItemsChange(this))
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
	
	@Override
	public void setState(MapItemState state)
	{
		for(MapItem m : containedItems)
			if(m.getDefinition().isValidState(state))
				m.setState(state);
		super.setState(state);
	}
	
	@Override
	public void update(Observable obs, Object o)
	{
		super.update(obs, o);
		if(obs == this.getDefinition())
		{
			if(o.equals("containedItems") || o.equals("relativeTrans"))
			{
				this.removeAllMapItems();
				List<MapItemDefinition<?>> newItems = ((MapItemAggregateDefinition<?>)this.getDefinition()).getContainedItems();
				List<Transformation> newTrans = ((MapItemAggregateDefinition<?>)this.getDefinition()).getRelativeTransformations();
				for(int i = 0; i < newItems.size(); i++)
					this.addMapItem(newItems.get(i).createMapItem(Transformation.ORIGIN, 
							this.getOwner(), this.getGameState()), newTrans.get(i));
			}
		}
	}
	
	public static boolean checkForContainedItemsChange(MapItemAggregate mia)
	{
		if(mia.body == null)
			return true;
		
		for(MapItem m : mia.getContainedItems())
			if(m instanceof MapItemAggregate && checkForContainedItemsChange((MapItemAggregate)m))
				return true;
		
		return false;
	}
	
	

}
