package linewars.gamestate.mapItems;

import java.util.ArrayList;
import java.util.List;

import configuration.ListConfiguration;
import configuration.Property;
import configuration.Usage;

import linewars.gamestate.GameState;
import linewars.gamestate.Player;
import linewars.gamestate.Position;
import linewars.gamestate.Transformation;

public abstract class MapItemAggregateDefinition<T extends MapItemAggregate> extends MapItemDefinition<MapItemAggregate> {
	
	private List<MapItemDefinition<? extends MapItem>> containedItems = new ArrayList<MapItemDefinition<? extends MapItem>>();
	private List<Transformation> relativeTrans = new ArrayList<Transformation>();

	public MapItemAggregateDefinition() {
		super();
		super.setPropertyForName("containedItems", new Property(Usage.CONFIGURATION, 
				new ListConfiguration<MapItemDefinition<? extends MapItem>>(new ArrayList<MapItemDefinition<? extends MapItem>>(), 
						new ArrayList<String>(), new ArrayList<Usage>())));
		super.setPropertyForName("relativeTrans", new Property(Usage.CONFIGURATION, 
				new ListConfiguration<Transformation>(new ArrayList<Transformation>(), 
						new ArrayList<String>(), new ArrayList<Usage>())));
	}
	
	@Override
	public MapItemAggregate createMapItem(Transformation t, Player owner, GameState gameState)
	{
		MapItemAggregate mia = createMapItemAggregate(t, owner, gameState);
		for(int i = 0; i < containedItems.size(); i++)
			mia.addMapItem(containedItems.get(i).createMapItem(new Transformation(new Position(0, 0), 0), owner, gameState), relativeTrans.get(i));
		return mia;
	}
	
	protected abstract T createMapItemAggregate(Transformation t, Player owner, GameState gameState);
	
	@SuppressWarnings("unchecked")
	@Override
	protected final void forceSubclassReloadConfigData()
	{
		containedItems = ((ListConfiguration<MapItemDefinition<? extends MapItem>>)super.getPropertyForName("containedItems").getValue()).getEnabledSubList();
		relativeTrans = ((ListConfiguration<Transformation>)super.getPropertyForName("relativeTrans").getValue()).getEnabledSubList();
		this.forceAggregateSubReloadConfigData();
	}
	
	protected abstract void forceAggregateSubReloadConfigData();
	
	public List<MapItemDefinition<? extends MapItem>> getContainedItems()
	{
		return containedItems;
	}
	
	public List<Transformation> getRelativeTransformations()
	{
		return relativeTrans;
	}

}
