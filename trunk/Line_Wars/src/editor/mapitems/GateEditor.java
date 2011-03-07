package editor.mapitems;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import linewars.gamestate.mapItems.GateDefinition;
import linewars.gamestate.mapItems.strategies.combat.NoCombatConfiguration;
import linewars.gamestate.mapItems.strategies.movement.ImmovableConfiguration;
import configuration.Configuration;
import editor.BigFrameworkGuy.ConfigType;
import editor.ConfigurationEditor;


/**
 * 
 * @author Connor Schenck
 *
 *  This class represents the panel that allows users
 * to edit the specific values related to only
 * gates.
 * 
 */
public class GateEditor extends JPanel implements ConfigurationEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1347736687861020929L;
	
	private JTextField maxHP;
	
	public GateEditor()
	{
		//set up the max hp panel
		maxHP = new JTextField();
		maxHP.setColumns(20);
		JPanel hpPanel = new JPanel();
		hpPanel.add(new JLabel("Max HP:"));
		hpPanel.add(maxHP);
		
		//set up this panel
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(hpPanel);
	}

	@Override
	public void setData(Configuration cd) {
		GateDefinition gd = (GateDefinition)cd;
		maxHP.setText(gd.getMaxHP() + "");
	}
	
	public void resetEditor()
	{
		maxHP.setText("");
	}

	@Override
	public Configuration instantiateNewConfiguration() {
		return new GateDefinition();
	}

	@Override
	public ConfigType getData(Configuration toSet) {
		GateDefinition gd = (GateDefinition)toSet;
		
		Scanner s = new Scanner(maxHP.getText());
		if(s.hasNextDouble())
			gd.setMaxHP(s.nextDouble());
		else
			gd.setMaxHP(0);
		
		gd.setCombatStratConfig(new NoCombatConfiguration());
		gd.setMovementStratConfig(new ImmovableConfiguration());
		
		return ConfigType.gate;
	}

	@Override
	public List<ConfigType> getAllLoadableTypes() {
		List<ConfigType> ret = new ArrayList<ConfigType>();
		ret.add(ConfigType.gate);
		return ret;
	}

	@Override
	public JPanel getPanel() {
		return this;
	}

}
