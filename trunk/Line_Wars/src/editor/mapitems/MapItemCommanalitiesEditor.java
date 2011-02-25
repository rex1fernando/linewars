package editor.mapitems;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import linewars.display.Animation;
import linewars.display.DisplayConfiguration;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemDefinition;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.abilities.AbilityDefinition;
import linewars.gamestate.mapItems.strategies.collision.CollisionStrategyConfiguration;
import configuration.Configuration;
import editor.BigFrameworkGuy;
import editor.BigFrameworkGuy.ConfigType;
import editor.ConfigurationEditor;
import editor.GenericSelector;
import editor.GenericSelector.GenericListCallback;
import editor.GenericSelector.SelectConfigurations;
import editor.GenericSelector.SelectionChangeListener;
import editor.GenericSelector.ShowBFGName;
import editor.ListGenericSelector;
import editor.ListGenericSelector.ListChangeListener;
import editor.mapitems.MapItemEditor.Wrapper;
import editor.mapitems.body.BodyEditor.DisplayConfigurationCallback;

/**
 * 
 * @author Connor Schenck
 *
 * This editor represents the panel that allows users to
 * edit map items. 
 *
 */
public class MapItemCommanalitiesEditor extends JPanel implements ConfigurationEditor {
	
	//variable for the name
	private JTextField name;
	
	//variables for the states and corresponding animations
	private ListGenericSelector<MapItemState> validStates;
	private GenericSelector<Animation> animations;
	private HashMap<MapItemState, Animation> animationMap = new HashMap<MapItemState, Animation>();
	
	//variable for the abilities and collision strategies
	private ListGenericSelector<AbilityDefinition> abilities;
	private GenericSelector<CollisionStrategyConfiguration> collisionStrat;
	
	private BigFrameworkGuy bfg;
	
	/**
	 * Constructs this map item editor. Takes in a reference to a
	 * BigFrameworkGuy so that it can know about URIs for abilities,
	 * etc.
	 * 
	 * @param guy	the big framework guy with a list of all relevant URIs
	 */
	public MapItemCommanalitiesEditor(BigFrameworkGuy guy, Wrapper<DisplayConfigurationCallback> callback)
	{
		bfg = guy;
		
		//set up the name panel
		JPanel namePanel = new JPanel();
		namePanel.add(new JLabel("Name"));
		name = new JTextField();
		name.setColumns(20);
		namePanel.add(name);
		
		//set up the states panel
		validStates = new ListGenericSelector<MapItemState>("Valid States", new MapItemStateSelector());
		validStates.addListChangeListener(new ListChangeListener<MapItemState>() {
			@Override
			public void objectsRemoved(List<MapItemState> removed) {
				for(MapItemState mis : removed)
					animationMap.remove(mis);
			}
			@Override
			public void objectAdded(MapItemState added) {}
			@Override
			public void HighlightChange(List<MapItemState> highlighted) {
				if(highlighted.size() == 1)
					animations.setSelectedObject(animationMap.get(highlighted.get(0)));
				else
					animations.setSelectedObject(null);
			}
		});
		
		//set up the animations
		animations = new GenericSelector<Animation>("Animation", 
				new SelectConfigurations<Animation>(bfg, ConfigType.animation), new ShowBFGName<Animation>());
		animations.addSelectionChangeListener(new SelectionChangeListener<Animation>() {
			@Override
			public void selectionChanged(Animation newSelection) {
				if(validStates.getHighlightedObjects().size() == 1)
					animationMap.put(validStates.getHighlightedObjects().get(0), newSelection);
				else
					animations.setSelectedObject(null);
			}
		});
		
		//add the panel for states/animations
		JPanel statesPanel = new JPanel();
		statesPanel.add(validStates);
		statesPanel.add(animations);
		
		//set up the abilities and collision strat panel
		abilities = new ListGenericSelector<AbilityDefinition>("Abilities", 
				new SelectConfigurations<AbilityDefinition>(bfg, ConfigType.ability), new ShowBFGName<AbilityDefinition>());
		JPanel aPanel = new JPanel();
		aPanel.add(abilities);
		collisionStrat = new GenericSelector<CollisionStrategyConfiguration>("Collision Strategy", 
				new SelectConfigurations<CollisionStrategyConfiguration>(bfg, ConfigType.collisionStrategy), 
				new ShowBFGName<CollisionStrategyConfiguration>());
		JPanel cPanel = new JPanel();
		cPanel.add(collisionStrat);
		
		//set up the main panel
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(namePanel);
		this.add(statesPanel);
		this.add(aPanel);
		this.add(cPanel);
		
		this.instantiateNewConfiguration();
		
		this.setPreferredSize(new Dimension(800, 600));
		
		callback.setData(new DisplayConfigurationCallback() {
			@Override
			public DisplayConfiguration getDisplayConfiguration() {
				return constructDisplayConfiguration(null);
			}
		});
	}
	
	/**
	 * Returns the URI of the animation associated with the
	 * given MapItemState. If there is no animation defined,
	 * then it returns null.
	 * 
	 * @param key	the MapItemState of the animation to get
	 * @return		the animation associated with key.
	 */
	public Animation getAnimation(MapItemState key)
	{
		return animationMap.get(key);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -2434216613579213750L;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void setData(Configuration cd) {
		MapItemDefinition mic = (MapItemDefinition)cd;
		
		//set the name
		name.setText(mic.getName());
		
		//set the states
		validStates.setSelectedObjects(mic.getValidStates());
		
		//set up the animations
		animationMap.clear();
		DisplayConfiguration dc = (DisplayConfiguration)mic.getDisplayConfiguration();
		List<MapItemState> states = mic.getValidStates();
		for(MapItemState mis : states)
			animationMap.put(mis, dc.getAnimation(mis));
		
		//set up the abilities
		abilities.setSelectedObjects(mic.getAbilityDefinitions());
		
		//set up the collision strat
		collisionStrat.setSelectedObject(mic.getCollisionStrategyConfig());
		
		this.validate();
		this.updateUI();
	}

	@Override
	public Configuration instantiateNewConfiguration() {
		//reset the name
		name.setText("");
		
		//reset the states and animations
		List<MapItemState> initialStates = new ArrayList<MapItemState>();
		initialStates.add(MapItemState.Idle);
		validStates.setSelectedObjects(initialStates);
		animationMap.clear();
		animations.setSelectedObject(null);
		
		//reset the abilities and collision strat
		abilities.setSelectedObjects(new ArrayList<AbilityDefinition>());
		collisionStrat.setSelectedObject(null);
		
		this.validate();
		this.updateUI();
		
		return null;
	}
	
	private DisplayConfiguration constructDisplayConfiguration(DisplayConfiguration dc)
	{
		//add the valid states and their animations
		if(dc == null)
			dc = new DisplayConfiguration();
		for(MapItemState mis : validStates.getSelectedObjects())
			if(animationMap.containsKey(mis))
				dc.setAnimation(mis, animationMap.get(mis));
		return dc;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ConfigType getData(Configuration toSet) {
		MapItemDefinition<? extends MapItem> mid = (MapItemDefinition<? extends MapItem>)toSet;
		
		//add the name
		mid.setName(name.getText());
		
		//add the valid states
		mid.setValidStates(validStates.getSelectedObjects());
		
		//set the display config
		mid.setDisplayConfiguration((DisplayConfiguration) mid.getDisplayConfiguration());
		
		//get the abilities
		mid.setAbilities(abilities.getSelectedObjects());
		
		//add the collision strat
		mid.setCollisionStrategy(collisionStrat.getSelectedObject());
		
		return null;
	}

	@Override
	public List<ConfigType> getAllLoadableTypes() {
		List<ConfigType> ret = new ArrayList<ConfigType>();
		ret.add(ConfigType.building);
		ret.add(ConfigType.gate);
		ret.add(ConfigType.part);
		ret.add(ConfigType.projectile);
		ret.add(ConfigType.turret);
		ret.add(ConfigType.unit);
		return ret;
	}
	
	@Override
	public JPanel getPanel() {
		return this;
	}
	
	private class MapItemStateSelector implements GenericListCallback<MapItemState> {

		@Override
		public List<MapItemState> getSelectionList() {
			List<MapItemState> options = new ArrayList<MapItemState>();
			for(int i = 0; i < MapItemState.values().length; i++)
				options.add(MapItemState.values()[i]);
			return options;
		}
		
	}

}
