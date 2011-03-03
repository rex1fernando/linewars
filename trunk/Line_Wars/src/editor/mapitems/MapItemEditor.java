package editor.mapitems;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import linewars.gamestate.mapItems.BuildingDefinition;
import linewars.gamestate.mapItems.GateDefinition;
import linewars.gamestate.mapItems.MapItemDefinition;
import linewars.gamestate.mapItems.PartDefinition;
import linewars.gamestate.mapItems.ProjectileDefinition;
import linewars.gamestate.mapItems.TurretDefinition;
import linewars.gamestate.mapItems.UnitDefinition;
import configuration.Configuration;
import editor.BigFrameworkGuy;
import editor.BigFrameworkGuy.ConfigType;
import editor.ConfigurationEditor;
import editor.GenericSelector.GenericListCallback;
import editor.mapitems.body.BodyEditor;
import editor.mapitems.body.BodyEditor.DisplayConfigurationCallback;

public class MapItemEditor extends JPanel implements ConfigurationEditor {
	
	public class Wrapper<T> {
		private T data;
		public Wrapper(T d) {
			data = d;
		}
		public T getData() {
			return data;
		}
		public void setData(T d) {
			data = d;
		}
	}
	
	private ConfigurationEditor commanalities;
	private ConfigurationEditor variabilities;
	private ConfigurationEditor bodyEditor;
	
	private JButton showBodyEditor;
	private JFrame bodyEditorFrame;
	
	private BigFrameworkGuy bfg;
	private String imagePath;
	
	public MapItemEditor(BigFrameworkGuy guy, String imagePath)
	{
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		bfg = guy;
		this.imagePath = imagePath;
	}

	@Override
	public void setData(Configuration cd) {
		setUpPanels(getConfigEditor(getType(cd)));
		commanalities.setData(cd);
		variabilities.setData(cd);
		bodyEditor.setData(cd);
	}
	
	private void setUpPanels(ConfigurationEditor variabilitiesEditor)
	{
		Wrapper<DisplayConfigurationCallback> callback = new Wrapper<DisplayConfigurationCallback>(null);
		commanalities = new MapItemCommanalitiesEditor(bfg, callback);
		variabilities = variabilitiesEditor;
		bodyEditor = new BodyEditor(callback.getData(), imagePath, new GenericListCallback<MapItemDefinition<?>>() {
			@Override
			public List<MapItemDefinition<?>> getSelectionList() {
				List<MapItemDefinition<?>> ret = new ArrayList<MapItemDefinition<?>>();
				for(Configuration c : bfg.getConfigurationsByType(ConfigType.part))
					ret.add((MapItemDefinition<?>) c);
				for(Configuration c : bfg.getConfigurationsByType(ConfigType.turret))
					ret.add((MapItemDefinition<?>) c);
				return ret;
			}
		});
		bodyEditorFrame = new JFrame("Body Editor");
		bodyEditorFrame.setContentPane(bodyEditor.getPanel());
		bodyEditorFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		bodyEditorFrame.addWindowListener(new BodyEditorFrameListener());
		
		showBodyEditor = new JButton("Show Body Editor");
		showBodyEditor.addActionListener(new ShowBodyEditorListener());

		this.removeAll();
		this.add(showBodyEditor);
		this.add(commanalities.getPanel());
		this.add(variabilities.getPanel());
		this.validate();
		this.updateUI();
	}

	@Override
	public Configuration instantiateNewConfiguration() {
		Object[] possibilities = {"Unit", "Building", "Projectile", "Gate", "Turret", "Part"};
		String type = (String) JOptionPane.showInputDialog(this,
				"Which type of map item would you look to create?",
				"Select type", JOptionPane.PLAIN_MESSAGE, null, possibilities,
				"Unit");
		
		setUpPanels(getConfigEditor(getType(type)));
		commanalities.instantiateNewConfiguration();
		Configuration ret = variabilities.instantiateNewConfiguration();
		bodyEditor.instantiateNewConfiguration();
		bodyEditor.setData(ret);
		
		return ret;
	}

	@Override
	public ConfigType getData(Configuration toSet) {
		commanalities.getData(toSet);
		bodyEditor.getData(toSet);
		return variabilities.getData(toSet);
	}

	@Override
	public List<ConfigType> getAllLoadableTypes() {
		List<ConfigType> ret = new ArrayList<ConfigType>();
		ret.add(ConfigType.unit);
		ret.add(ConfigType.building);
		ret.add(ConfigType.gate);
		ret.add(ConfigType.projectile);
		ret.add(ConfigType.part);
		ret.add(ConfigType.turret);
		return ret;
	}

	@Override
	public JPanel getPanel() {
		return this;
	}
	
	private ConfigType getType(String s)
	{
		return ConfigType.valueOf(s.toLowerCase());
	}
	
	private ConfigType getType(Configuration c)
	{
		if(c instanceof GateDefinition)
			return ConfigType.gate;
		else if(c instanceof UnitDefinition)
			return ConfigType.unit;
		else if(c instanceof BuildingDefinition)
			return ConfigType.building;
		else if(c instanceof ProjectileDefinition)
			return ConfigType.projectile;
		else if(c instanceof TurretDefinition)
			return ConfigType.turret;
		else if(c instanceof PartDefinition)
			return ConfigType.part;
		else
			throw new IllegalArgumentException("Configuration type not identified");
	}
	
	private ConfigurationEditor getConfigEditor(ConfigType type)
	{
		if(type.equals(ConfigType.unit))
			return new UnitEditor(bfg);
		else if(type.equals(ConfigType.building))
			return new BuildingEditor();
		else if(type.equals(ConfigType.projectile))
			return new ProjectileEditor(bfg);
		else if(type.equals(ConfigType.gate))
			return new GateEditor();
		else if(type.equals(ConfigType.turret))
			return new TurretEditor(bfg);
		else if(type.equals(ConfigType.part))
			return new PartEditor();
		else
			throw new IllegalArgumentException(type.toString() + " not recognized");
	}
	
	private void setBodyEditorVisible(boolean visible)
	{
		if(visible)
		{
			showBodyEditor.setText("Hide Body Editor");
			bodyEditorFrame.setVisible(true);
			bodyEditorFrame.pack();
			bodyEditor.getPanel().setVisible(true);
		}
		else
		{
			showBodyEditor.setText("Show Body Editor");
			bodyEditorFrame.setVisible(false);
			bodyEditor.getPanel().setVisible(false);
		}
	}
	
	private class ShowBodyEditorListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e) {
			setBodyEditorVisible(showBodyEditor.getText().contains("Show"));
		}
		
	}
	
	private class BodyEditorFrameListener implements WindowListener
	{
		public void windowActivated(WindowEvent e) {}
		public void windowClosed(WindowEvent e) {}

		@Override
		public void windowClosing(WindowEvent e) {
			setBodyEditorVisible(false);
		}
		public void windowDeactivated(WindowEvent e) {}
		public void windowDeiconified(WindowEvent e) {}
		public void windowIconified(WindowEvent e) {}
		public void windowOpened(WindowEvent e) {}
	}

}
