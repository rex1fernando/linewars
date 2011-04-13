package linewars.gamestate.mapItems.strategies.impact;

import java.util.ArrayList;
import java.util.List;

import linewars.gamestate.Position;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.strategies.StrategyConfiguration;
import utility.Observable;
import utility.Observer;
import configuration.ListConfiguration;
import configuration.Usage;
import editor.abilitiesstrategies.AbilityStrategyEditor;
import editor.abilitiesstrategies.EditorProperty;
import editor.abilitiesstrategies.EditorUsage;

public class DoMultipleThingsConfiguration extends ImpactStrategyConfiguration{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6282682849880288344L;

	private static final String name = "Do Multiple Things";
	
	static{
		StrategyConfiguration.setStrategyConfigMapping(name, DoMultipleThingsConfiguration.class, AbilityStrategyEditor.class);
	}
	
	private static final String actionsName = "actions";
	private static final Usage actionsUsage = Usage.CONFIGURATION;
	private static final EditorUsage actionsEditorUsage = EditorUsage.ListImpactConfiguration;
	private static final String actionsDescription = "The ordered list of things that should happen on impact with another entity.";
	private EditorProperty actionsProperty = new EditorProperty(actionsUsage, null, actionsEditorUsage, actionsDescription);
	
	public DoMultipleThingsConfiguration() {
		this.setPropertyForName(actionsName, actionsProperty);
	}

	@Override
	public ImpactStrategy createStrategy(MapItem m) {
		return new DoMultipleThings(m);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof DoMultipleThingsConfiguration)) return false;
		DoMultipleThingsConfiguration other = (DoMultipleThingsConfiguration) obj;
		
		@SuppressWarnings("unchecked")
		ListConfiguration<ImpactStrategyConfiguration> mySubStrategies = (ListConfiguration<ImpactStrategyConfiguration>) this.getPropertyForName(actionsName).getValue();
		List<ImpactStrategyConfiguration> mine = mySubStrategies.getFullList();
		
		@SuppressWarnings("unchecked")
		ListConfiguration<ImpactStrategyConfiguration> theirSubStrategies = (ListConfiguration<ImpactStrategyConfiguration>) other.getPropertyForName(actionsName).getValue(); 
		List<ImpactStrategyConfiguration> theirs = theirSubStrategies.getFullList();
		
		if(mine.size() != theirs.size()) return false;
		
		for(int i = 0; i < mine.size(); i++){
			ImpactStrategyConfiguration m = mine.get(i);
			ImpactStrategyConfiguration t = theirs.get(i);
			if(!m.equals(t)){
				return false;
			}
		}

		return true;
	}
	
	public class DoMultipleThings implements ImpactStrategy, Observer{

		MapItem owner;
		List<ImpactStrategy> subStrategies;
		
		public DoMultipleThings(MapItem m){
			owner = m;
			subStrategies = new ArrayList<ImpactStrategy>();
			
			@SuppressWarnings("unchecked")
			ListConfiguration<ImpactStrategyConfiguration> subStrategyListConfiguration = (ListConfiguration<ImpactStrategyConfiguration>) DoMultipleThingsConfiguration.this.getPropertyForName(actionsName).getValue();
			List<ImpactStrategyConfiguration> enabledSubList = subStrategyListConfiguration.getEnabledSubList();
			
			for(ImpactStrategyConfiguration toAdd : enabledSubList){
				subStrategies.add(toAdd.createStrategy(m));
			}
			
			subStrategyListConfiguration.addObserver(this);
		}
		
		@Override
		public String name() {
			return name;
		}

		@Override
		public ImpactStrategyConfiguration getConfig() {
			return DoMultipleThingsConfiguration.this;
		}

		@Override
		public void handleImpact(MapItem m) {
			for(ImpactStrategy handler : subStrategies){
				handler.handleImpact(m);
			}
		}

		@Override
		public void handleImpact(Position p) {
			for(ImpactStrategy handler : subStrategies){
				handler.handleImpact(p);
			}
		}

		@Override
		public void update(Observable o, Object arg) {
			@SuppressWarnings("unchecked")
			ListConfiguration<ImpactStrategy> newSubStrategies = (ListConfiguration<ImpactStrategy>) DoMultipleThingsConfiguration.this.getPropertyForName(actionsName).getValue();
			subStrategies = newSubStrategies.getEnabledSubList();
		}
		
	}
}
