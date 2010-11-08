package editor.mapitems;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import javax.swing.*;
import javax.swing.event.*;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ConfigFileWriter;
import linewars.configfilehandler.ParserKeys;
import linewars.gamestate.mapItems.MapItemState;
import editor.BigFrameworkGuy;
import editor.ConfigurationEditor;
import editor.ListURISelector;
import editor.URISelector;
import editor.URISelector.SelectorOptions;
import editor.mapitems.StrategySelector.StrategySelectorCallback;

public class MapItemEditor extends JPanel implements ConfigurationEditor, ListSelectionListener, 
												ActionListener {
	
	//variable for the name
	private JTextField name;
	
	//variables for the states and corresponding animations
	private ListURISelector validStates;
	private URISelector animations;
	private HashMap<MapItemState, String> animationMap = new HashMap<MapItemState, String>();
	
	//variable for the abilities and collision strategies
	private ListURISelector abilities;
	private URISelector collisionStrat;
	
	//variables for setting the body
	private JButton bodyButton;
	private JLabel bodyStatus;
	private ConfigData bodyConfig;
	
	private BigFrameworkGuy bfg;
	
	public MapItemEditor(BigFrameworkGuy guy)
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
		validStates.addListSelectionListener(this);
		animations = new URISelector("Animation", new AnimationSelector());
		JPanel statesPanel = new JPanel();
		statesPanel.add(validStates);
		statesPanel.add(animations);
		
		//set up the abilities and collision strat panel
		abilities = new ListURISelector("Abilities", new AbilitySelector());
		collisionStrat = new URISelector("Collision Strategy", new CollisionStrategySelector());
		JPanel abilitiesColStratPanel = new JPanel();
		abilitiesColStratPanel.add(abilities);
		abilitiesColStratPanel.add(collisionStrat);
		
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
		this.add(abilitiesColStratPanel);
		this.add(bodyPanel);
		
		this.reset();
	}
	
	public String getAnimation(MapItemState key)
	{
		return animationMap.get(key);
	}
	
	public void setBody(ConfigData cd)
	{
		bodyConfig = cd;
		bodyStatus.setText("Set");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -2434216613579213750L;

	@Override
	public void setData(ConfigData cd) {
		// TODO Auto-generated method stub

	}

	@Override
	public void forceSetData(ConfigData cd) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reset() {
		//reset the name
		name.setText("");
		
		//reset the states and animations
		validStates.setSelectedURIs(new String[]{MapItemState.Idle.toString()});
		animationMap.clear();
		animations.setSelectedURI("");
		
		//reset the abilities and collision strat
		abilities.setSelectedURIs(new String[0]);
		collisionStrat.setSelectedURI("");
		
		//reset the body config
		bodyConfig = null;
		bodyStatus.setText("Not Set");
	}

	@Override
	public ConfigData getData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ParserKeys getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JPanel getPanel() {
		return this;
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		String[] highlighted = validStates.getHighlightedURIs();
		MapItemState key = null;
		if(highlighted.length > 0)
			key = MapItemState.valueOf(highlighted[0]);
		if(highlighted.length != 1 || animationMap.get(key) == null)
			animations.setSelectedURI("");
		else
			animations.setSelectedURI(animationMap.get(key));
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource().equals(bodyButton))
		{
			BodyEditor be = new BodyEditor(this);
			if(bodyConfig != null)
				be.setData(bodyConfig);
		}
	}
	
	private class MapItemStateSelector implements SelectorOptions {

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
	
	private class AbilitySelector implements SelectorOptions {

		@Override
		public String[] getOptions() {
			return bfg.getAbilityURIs();
		}

		@Override
		public void uriSelected(String uri) {
			getPanel().validate();
			getPanel().updateUI();
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
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.add(new MapItemEditor(new MapItemEditorTester()));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	private static class MapItemEditorTester extends BigFrameworkGuy {
		@Override
		public String[] getAnimationURIs() {
			return new String[]{"resources/animations/commandCenterIdle.cfg", "a1", "a2"};
		}
		@Override
		public String[] getAbilityURIs() {
			return new String[]{"ability1", "ability2", "ability3"};
		}
	}

}
