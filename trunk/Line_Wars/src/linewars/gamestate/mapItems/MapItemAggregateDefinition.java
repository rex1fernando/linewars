package linewars.gamestate.mapItems;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import linewars.configfilehandler.ConfigFileReader.InvalidConfigFileException;
import linewars.gamestate.GameState;
import linewars.gamestate.Player;
import linewars.gamestate.Position;
import linewars.gamestate.Transformation;

public abstract class MapItemAggregateDefinition<T extends MapItemAggregate> extends MapItemDefinition<MapItemAggregate> {
	
	private List<MapItemDefinition<? extends MapItem>> containedItems = new ArrayList<MapItemDefinition<? extends MapItem>>();
	private List<Transformation> relativeTrans = new ArrayList<Transformation>();

	public MapItemAggregateDefinition(String URI, Player owner,
			GameState gameState) throws FileNotFoundException,
			InvalidConfigFileException {
		super(URI, owner, gameState);
		//TODO fill up contained items list and relative transforms
	}
	
	@Override
	public MapItemAggregate createMapItem(Transformation t)
	{
		MapItemAggregate mia = createMapItemAggregate(t);
		for(int i = 0; i < containedItems.size(); i++)
			mia.addMapItem(containedItems.get(i).createMapItem(new Transformation(new Position(0, 0), 0)), relativeTrans.get(i));
		return mia;
	}
	
	protected abstract T createMapItemAggregate(Transformation t);

}
