package editor.mapitems;

import javax.swing.JPanel;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;
import editor.BigFrameworkGuy;
import editor.ConfigurationEditor;
import editor.ListURISelector;
import editor.URISelector;
import editor.URISelector.SelectorOptions;

public class MapItemEditor extends JPanel implements ConfigurationEditor {
	
	private ListURISelector validStates;
	private URISelector animations;
	
	private BigFrameworkGuy bfg;
	
	public MapItemEditor(BigFrameworkGuy guy)
	{
		bfg = guy;
		validStates = new ListURISelector("Valid States", new MapItemStateSelector());
		animations = new URISelector("Animation", new AnimationSelector());
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
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub
		return null;
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
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public strictfp enum MapItemState {
		Idle, Dead, Constructing, Active, Moving
	}
	
	private class AnimationSelector implements SelectorOptions {

		@Override
		public String[] getOptions() {
			return bfg.getAnimationURIs();
		}

		@Override
		public void uriSelected(String uri) {
			// TODO Auto-generated method stub
			
		}
		
	}

}
