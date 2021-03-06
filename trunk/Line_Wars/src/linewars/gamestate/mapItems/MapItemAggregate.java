package linewars.gamestate.mapItems;

import java.util.ArrayList;
import java.util.List;

import linewars.gamestate.GameState;
import linewars.gamestate.Player;
import linewars.gamestate.Position;
import linewars.gamestate.Transformation;
import linewars.gamestate.mapItems.strategies.collision.CollisionStrategyConfiguration;
import linewars.gamestate.shapes.Circle;
import linewars.gamestate.shapes.Shape;
import linewars.gamestate.shapes.ShapeAggregate;
import utility.Observable;

public abstract class MapItemAggregate extends MapItem {

	private List<MapItem> containedItems = new ArrayList<MapItem>();
	private List<Transformation> relativeTrans = new ArrayList<Transformation>();
	private List<Turret> turrets = new ArrayList<Turret>();
	private Transformation transform;
	
	private ShapeAggregate body = null;
	
	public MapItemAggregate(Transformation trans, MapItemDefinition<? extends MapItemAggregate> def, 
			GameState gameState, Player owner) {
		super(trans, def, owner, gameState);
		transform = trans;
	}
	
	public void pushModifierToAllItems(MapItemModifier mod)
	{
		super.pushModifier(mod);
		for(MapItem m : containedItems)
		{
			if(m instanceof MapItemAggregate)
				((MapItemAggregate) m).pushModifierToAllItems(mod);
			else
				m.pushModifier(mod);
		}
	}
	
	public void removeModifierFromAllItems(MapItemModifier mod)
	{
		super.removeModifier(mod);
		for(MapItem m : containedItems)
		{
			if(m instanceof MapItemAggregate)
				((MapItemAggregate) m).removeModifierFromAllItems(mod);
			else
				m.removeModifier(mod);
		}
	}
	
	public void addMapItemToFront(MapItem m, Transformation t)
	{
		m.setTransformation(new Transformation(this.getPosition().add(
				t.getPosition().rotateAboutPosition(new Position(0, 0), this.getRotation())), 
				t.getRotation() + this.getRotation()));
		containedItems.add(0, m);
		if(m.getDefinition().isValidState(this.getState()))
			m.setState(this.getState());
		body = null;
	}
	
	public void addMapItem(MapItem m, Transformation t)
	{
		m.setTransformation(new Transformation(this.getPosition().add(
				t.getPosition().rotateAboutPosition(new Position(0, 0), this.getRotation())), 
				t.getRotation() + this.getRotation()));
		containedItems.add(m);
		if(m.getDefinition().isValidState(this.getState()))
			m.setState(this.getState());
		body = null;
	}
	
	/**
	 * Checks to see if m is contained in this map item aggregate. If it is, returns
	 * true, if not recursively checks to see if any contained map item aggregates
	 * contain m. If any of them do, returns true, otherwise returns false.
	 * 
	 * @param m
	 * @return
	 */
	public boolean containsRecursively(MapItem m)
	{
		for(MapItem contained : containedItems)
		{
			if(contained.equals(m))
				return true;
			else if((contained instanceof MapItemAggregate) &&
					((MapItemAggregate)contained).containsRecursively(m))
				return true;
		}
		return false;
	}
	
	public void removeMapItem(MapItem m)
	{
		containedItems.remove(m);
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
			updateInternalVariables();
		List<Turret> ret = new ArrayList<Turret>();
		ret.addAll(turrets);
		return ret;
	}
	
	public List<MapItem> getContainedItems()
	{
		List<MapItem> ret = new ArrayList<MapItem>();
		ret.addAll(containedItems);
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
		if(body != null)
		{
			body = body.transform(new Transformation(newPos.subtract(oldPos), 0));
//			updateInternalVariables();
//			if(!tempBody.equals(body))
//				System.out.println("MapItemAggregate:setPosition: Body not translated correctly");
		}
		
	}
	
	@Override
	public void setRotation(double newRot)
	{
		double rot = newRot - transform.getRotation();
		for(MapItem m : containedItems)
		{
			m.setPosition(m.getPosition().rotateAboutPosition(getPosition(), rot));
			m.setRotation(rot + m.getRotation());
		}
		
		double oldRot = transform.getRotation();
		transform = new Transformation(transform.getPosition(), newRot);
		
		if(body != null)
		{
			body = body.transform(new Transformation(Position.ORIGIN, newRot - oldRot));
//			updateInternalVariables();
//			if(!tempBody.equals(body))
//				System.out.println("MapItemAggregate:setPosition: Body not rotated correctly");
		}
		
		
		
	}
	
	@Override
	public void setTransformation(Transformation t)
	{
		setPosition(t.getPosition());
		setRotation(t.getRotation());
	}
	
	@Override
	public boolean finished()
	{
		if(!super.finished())
			return false;
		
		for(MapItem m : containedItems)
			if(!m.finished())
				return false;
		
		return true;
	}
	
	@Override
	public boolean isCollidingWith(MapItem m)
	{
		if(!CollisionStrategyConfiguration.isAllowedToCollide(m, this))
			return false;
		for(MapItem c : containedItems)
//			if(c.getBody().isCollidingWith(m.getBody()))
			if(c.isCollidingWith(m))
				return true;
		return false;
	}
	
	@Override
	public Shape getBody()
	{
		if(!this.getGameState().isLocked() /*&& checkForContainedItemsChange(this)*/)
			updateInternalVariables();
			
		if(body == null && this.getGameState().isLocked())
			return new Circle(transform, 10);
		
		return body;
	}
	
	public void setStateIfInState(MapItemState condition, MapItemState toSet)
	{
		if(this.getState().equals(condition) && this.getDefinition().getValidStates().contains(toSet))
			super.setState(toSet);
		if(containedItems != null)
		{
			for(MapItem m : containedItems)
				if(m.getDefinition().isValidState(toSet) && m.getState().equals(condition))
				{
					if(m instanceof MapItemAggregate)
						((MapItemAggregate)m).setStateIfInState(condition, toSet);
					else
						m.setState(toSet);
				}
				else if(m instanceof MapItemAggregate)
					((MapItemAggregate)m).setStateIfInState(condition, toSet);
		}
	}
	
	@Override
	public void setState(MapItemState state)
	{
		if(containedItems != null)
		{
			for(MapItem m : containedItems)
				if(m.getDefinition().isValidState(state))
					m.setState(state);
		}
		super.setState(state);
	}
	
	@Override
	public void updateMapItem()
	{
		super.updateMapItem();
		for(MapItem contained : containedItems)
			contained.updateMapItem();
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
				for(int i = 0; i < newItems.size() && i < newTrans.size(); i++)
					this.addMapItem(newItems.get(i).createMapItem(Transformation.ORIGIN, 
							this.getOwner(), this.getGameState()), newTrans.get(i));
				body = null;
			}
		}
	}
	
	protected void updateInternalVariables()
	{
		ArrayList<Shape> shapes = new ArrayList<Shape>();
		ArrayList<Transformation> pos = new ArrayList<Transformation>();
		
		for(MapItem m : containedItems)
		{
			shapes.add(m.getBody());
			pos.add(new Transformation(m.getPosition().subtract(this.getPosition()), m.getRotation() - this.getRotation()));
		}
		
		body = new ShapeAggregate(transform, shapes, pos);
		relativeTrans = pos;
		
		turrets.clear();
		for(MapItem m : containedItems)
		{
			if(m instanceof Turret)
				turrets.add((Turret)m);
			else if(m instanceof MapItemAggregate)
				turrets.addAll(((MapItemAggregate)m).getTurrets());
		}
	}
	
	public static boolean checkForContainedItemsChange(MapItemAggregate mia)
	{
		if(mia.body == null || mia.containedItems.size() != mia.relativeTrans.size())
			return true;
		
		for(int i = 0; i < mia.containedItems.size(); i++)
		{
			MapItem m = mia.containedItems.get(i);
			Transformation relativeTrans = new Transformation(m.getPosition().subtract(
					mia.getPosition()), m.getRotation() - mia.getRotation());
			if(!relativeTrans.equals(mia.relativeTrans.get(i)))
				return true;
			if(m instanceof MapItemAggregate && checkForContainedItemsChange((MapItemAggregate)m))
				return true;
		}
		
		return false;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof MapItemAggregate)
		{
			MapItemAggregate mia = (MapItemAggregate) obj;
			return super.equals(mia) &&
					mia.containedItems.equals(containedItems) &&
					mia.relativeTrans.equals(relativeTrans) &&
					mia.transform.equals(transform) &&
					(mia.body != null && body != null && mia.body.equals(body));
		}
		else
			return false;
	}
	

}
