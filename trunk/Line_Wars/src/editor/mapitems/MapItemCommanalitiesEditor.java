package editor.mapitems;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import linewars.display.Animation;
import linewars.display.DisplayConfiguration;
import linewars.gamestate.mapItems.MapItem;
import linewars.gamestate.mapItems.MapItemDefinition;
import linewars.gamestate.mapItems.MapItemState;
import linewars.gamestate.mapItems.abilities.AbilityDefinition;
import linewars.gamestate.shapes.ShapeConfiguration;
import configuration.Configuration;
import editor.BigFrameworkGuy;
import editor.BigFrameworkGuy.ConfigType;
import editor.ConfigurationEditor;
import editor.ConfigurationSelector;
import editor.ListConfigurationSelector;
import editor.ListURISelector;
import editor.ListURISelector.ListSelectorOptions;
import editor.URISelector;
import editor.URISelector.SelectorOptions;

/**
 * 
 * @author Connor Schenck
 *
 * This editor represents the panel that allows users to
 * edit map items. 
 *
 */
public class MapItemCommanalitiesEditor extends JPanel implements ConfigurationEditor, ActionListener {
	
	//variable for the name
	private JTextField name;
	
	//variables for the states and corresponding animations
	private ListURISelector validStates;
	private URISelector animations;
	private HashMap<MapItemState, Animation> animationMap = new HashMap<MapItemState, Animation>();
	
	//variable for the abilities and collision strategies
	private ListConfigurationSelector abilities;
	private ConfigurationSelector collisionStrat;
	
	//variables for setting the body
	private JButton bodyButton;
	private JLabel bodyStatus;
	private ShapeConfiguration bodyConfig;
	
	private BigFrameworkGuy bfg;
	
	/**
	 * Constructs this map item editor. Takes in a reference to a
	 * BigFrameworkGuy so that it can know about URIs for abilities,
	 * etc.
	 * 
	 * @param guy	the big framework guy with a list of all relevant URIs
	 */
	public MapItemCommanalitiesEditor(BigFrameworkGuy guy)
	{
		bfg = guy;
		
		//set up the name panel
		JPanel namePanel = new JPanel();
		namePanel.add(new JLabel("Name"));
		name = new JTextField();
		name.setColumns(20);
		namePanel.add(name);
		
		//set up the states panel
		validStates = new ListURISelector("Valid States", new MapItemStateSelector());
		animations = new URISelector("Animation", new AnimationSelector());
		JPanel statesPanel = new JPanel();
		statesPanel.add(validStates);
		statesPanel.add(animations);
		
		//set up the abilities and collision strat panel
		abilities = new ListConfigurationSelector("Abilities", bfg, ConfigType.ability);
		JPanel aPanel = new JPanel();
		aPanel.add(abilities);
		collisionStrat = new ConfigurationSelector("Collision Strategy", bfg, ConfigType.collisionStrategy);
		JPanel cPanel = new JPanel();
		cPanel.add(collisionStrat);
		
		//set up the body panel
		bodyButton = new JButton("Set the body...");
		bodyButton.addActionListener(this);
		bodyStatus = new JLabel("Not Set");
		JPanel bodyPanel = new JPanel();
		bodyPanel.add(bodyButton);
		bodyPanel.add(bodyStatus);
		
		//set up the main panel
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(namePanel);
		this.add(statesPanel);
		this.add(aPanel);
		this.add(cPanel);
		this.add(bodyPanel);
		
		this.instantiateNewConfiguration();
		
		this.setPreferredSize(new Dimension(800, 600));
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
	 * Sets the body config to the given config
	 * 
	 * @param cd	the body config
	 */
	public void setBody(ShapeConfiguration cd)
	{
		bodyConfig = cd;
		bodyStatus.setText("Set");
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
		List<MapItemState> states = mic.getValidStates();
		List<String> stateStrings = new ArrayList<String>();
		for(MapItemState mis : states)
			stateStrings.add(mis.toString());
		validStates.setSelectedURIs(stateStrings.toArray(new String[0]));
		
		//set up the animations
		animationMap.clear();
		DisplayConfiguration dc = (DisplayConfiguration)mic.getDisplayConfiguration();
		for(MapItemState mis : states)
			animationMap.put(mis, dc.getAnimation(mis));
		
		//set up the abilities
		abilities.setSelectedConfigurations(mic.getAbilityDefinitions());
		
		//set up the collision strat
		collisionStrat.setSelectedConfiguration(mic.getCollisionStrategyConfig());
		
		//set up the body
		if(mic.getBodyConfig() != null)
		{
			bodyConfig = mic.getBodyConfig();
			bodyStatus.setText("set");
		}
		else
			bodyStatus.setText("not set");
		
		this.validate();
		this.updateUI();
	}

	@Override
	public Configuration instantiateNewConfiguration() {
		//reset the name
		name.setText("");
		
		//reset the states and animations
		validStates.setSelectedURIs(new String[]{MapItemState.Idle.toString()});
		animationMap.clear();
		animations.setSelectedURI("");
		
		//reset the abilities and collision strat
		abilities.setSelectedConfigurations(new ArrayList<AbilityDefinition>());
		collisionStrat.setSelectedConfiguration(null);
		
		//reset the body config
		bodyConfig = null;
		bodyStatus.setText("Not Set");
		
		this.validate();
		this.updateUI();
		
		return null;
	}

	@Override
	public ConfigType getData(Configuration toSet) {
		MapItemDefinition<? extends MapItem> mid = (MapItemDefinition<? extends MapItem>)toSet;
		
		//add the name
		mid.setName(name.getText());
		
		//add the valid states
		
		
		//add the valid states and their animations
		for(String s : validStates.getSelectedURIs())
		{
			cd.add(ParserKeys.ValidStates, s);
			MapItemState state = MapItemState.valueOf(s);
			if(animationMap.containsKey(state))
				cd.set(ParserKeys.valueOf(state.toString()), animationMap.get(state));
		}
		
		//get the abilities
		if(abilities.getSelectedURIs().length > 0)
			cd.set(ParserKeys.abilities, abilities.getSelectedURIs());
		
		//add the collision strat
		if(!collisionStrat.getSelectedURI().equals(""))
		{
			ConfigData col = new ConfigData();
			col.set(ParserKeys.type, collisionStrat.getSelectedURI());
			cd.set(ParserKeys.collisionStrategy, col);
		}
		
		//set the body
		if(bodyConfig != null)
			cd.set(ParserKeys.body, bodyConfig);
		
		return cd;
	}

	@Override
	public List<ConfigType> getAllLoadableTypes() {
		if(mapItemTypeInfo != null)
			return mapItemTypeInfo.getAllLoadableTypes();
		else //use unit as default type
			return ParserKeys.unitURI;
	}

	@Override
	public boolean isValidConfig() {
		if(name == null)
			return false;
		
		//if the type is specified, then use the parser from that
		if(mapItemTypeInfo != null)
		{
			if(!mapItemTypeInfo.isValidConfig())
				return false;
		}
		else
			return false;
		
		//check the name
		if(name.getText().equals(""))
			return false;
		
		List<String> vs = new ArrayList<String>();
		for(String s : validStates.getSelectedURIs())
			vs.add(s);
		
		//make sure idle is defined
		if(!vs.contains("Idle"))
			return false;
		
		//units and projectiles need dead
		if((mapItemType.getSelectedURI().equalsIgnoreCase("Unit") 
				|| mapItemType.getSelectedURI().equalsIgnoreCase("Projectile")
				|| mapItemType.getSelectedURI().equalsIgnoreCase("Gate"))
				&& !vs.contains("Dead"))
				return false;
		
		//check to make sure the animations are defined
		for(String s : validStates.getSelectedURIs())
		{
			MapItemState state = MapItemState.valueOf(s);
			if(!animationMap.containsKey(state))
				return false;
		}
		
		//add the collision strat
		if(collisionStrat.getSelectedURI().equals(""))
			return false;
		
		//set the body
		if(bodyConfig == null)
			return false;
		
		return true;
	}
	
	@Override
	public JPanel getPanel() {
		return this;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource().equals(bodyButton))
		{
			if(animationMap.get(MapItemState.Idle) == null)
			{
				JOptionPane.showMessageDialog(this,
					    "The Idle animation must be set before setting the body.",
					    "Error",
					    JOptionPane.ERROR_MESSAGE);
				return;
			}
			BodyEditor be = new BodyEditor(this);
			if(bodyConfig != null)
				be.setData(bodyConfig);
		}
	}
	
	private class MapItemStateSelector implements ListSelectorOptions {

		@Override
		public String[] getOptions() {
			String[] options = new String[MapItemState.values().length];
			for(int i = 0; i < options.length; i++)
				options[i] = MapItemState.values()[i].toString();
			return options;
		}

		@Override
		public void uriSelected(String uri) {
			getPanel().validate();	
			getPanel().updateUI();
		}

		@Override
		public void uriRemoved(String uri) {
		}

		@Override
		public void uriHighlightChange(String[] uris) {
			MapItemState key = null;
			if(uris.length > 0)
				key = MapItemState.valueOf(uris[0]);
			if(uris.length != 1 || animationMap.get(key) == null)
				animations.setSelectedURI("");
			else
				animations.setSelectedURI(animationMap.get(key));
		}
		
	}
	
	private class AnimationSelector implements SelectorOptions {

		@Override
		public String[] getOptions() {
			return bfg.getAnimationURIs();
		}

		@Override
		public void uriSelected(String uri) {
			String[] highlighted = validStates.getHighlightedURIs();
			if(highlighted.length != 1)
				animations.setSelectedURI("");
			else
				animationMap.put(MapItemState.valueOf(highlighted[0]), animations.getSelectedURI());
		}
		
	}
	
	private class AbilitySelector implements ListSelectorOptions {

		@Override
		public String[] getOptions() {
			return bfg.getAbilityURIs();
		}

		@Override
		public void uriSelected(String uri) {
			getPanel().validate();
			getPanel().updateUI();
		}

		@Override
		public void uriRemoved(String uri) {
		}

		@Override
		public void uriHighlightChange(String[] uris) {
		}
		
	}
	
	private class CollisionStrategySelector implements SelectorOptions {

		@Override
		public String[] getOptions() {
			return new String[]{"AllEnemies", "CollidesWithAll", "Ground", "NoCollision", "AllEnemyUnits"};
		}

		@Override
		public void uriSelected(String uri) {
						
		}
		
	}
	
	private class MapItemTypeSelector implements SelectorOptions {

		@Override
		public String[] getOptions() {
			return new String[]{"Unit", "Building", "Projectile", "Gate"};
		}

		@Override
		public void uriSelected(String uri) {
			if(mapItemTypeInfo != null)
				MapItemCommanalitiesEditor.this.remove(mapItemTypeInfo.getPanel());
			
			if(uri.equalsIgnoreCase("Unit"))
				mapItemTypeInfo = new UnitEditor();
			else if(uri.equalsIgnoreCase("Building"))
				mapItemTypeInfo = new BuildingEditor();
			else if(uri.equalsIgnoreCase("Projectile"))
				mapItemTypeInfo = new ProjectileEditor();
			else if(uri.equalsIgnoreCase("Gate"))
				mapItemTypeInfo = new GateEditor();
			
			//make sure the dead state is on the list
			if(uri.equalsIgnoreCase("Unit") ||
					uri.equalsIgnoreCase("Projectile") || 
					uri.equalsIgnoreCase("Gate"))
			{
				String[] states = validStates.getSelectedURIs();
				boolean found = false;
				for(String s : states)
					if(s.equals("Dead"))
						found = true;
				if(!found)
				{
					states = Arrays.copyOf(states, states.length + 1);
					states[states.length - 1] = "Dead";
					validStates.setSelectedURIs(states);
				}
			}
			
			mapItemTypeInfo.getPanel().setBorder(BorderFactory.createBevelBorder(1));
			MapItemCommanalitiesEditor.this.add(mapItemTypeInfo.getPanel());
			MapItemCommanalitiesEditor.this.validate();
			MapItemCommanalitiesEditor.this.updateUI();
		}
		
	}

}
