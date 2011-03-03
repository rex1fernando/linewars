package editor.mapitems;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import linewars.gamestate.mapItems.UnitDefinition;
import linewars.gamestate.mapItems.strategies.combat.CombatStrategyConfiguration;
import linewars.gamestate.mapItems.strategies.movement.MovementStrategyConfiguration;
import configuration.Configuration;
import editor.BigFrameworkGuy;
import editor.BigFrameworkGuy.ConfigType;
import editor.ConfigurationEditor;
import editor.GenericSelector;

/**
 * 
 * @author Connor Schenck
 *
 *  This class represents the panel that allows users
 * to edit the specific values related to only
 * units.
 *
 */
public class UnitEditor extends JPanel implements ConfigurationEditor {

	//variable for storing the max hp
	private JTextField maxHP;
	private GenericSelector<Configuration> combatStrat;
	private GenericSelector<Configuration> moveStrat;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8788172270105836095L;
	
	public UnitEditor(BigFrameworkGuy bfg)
	{
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		//set up the hp panel
		maxHP = new JTextField();
		maxHP.setColumns(20);
		JPanel hpPanel = new JPanel();
		hpPanel.add(new JLabel("Max HP:"));
		hpPanel.add(maxHP);
		
		combatStrat  = new GenericSelector<Configuration>("Combat Strategy", 
				new GenericSelector.SelectConfigurations<Configuration>(bfg, ConfigType.combatStrategy),
				new GenericSelector.ShowBFGName<Configuration>());
		moveStrat  = new GenericSelector<Configuration>("Movement Strategy", 
				new GenericSelector.SelectConfigurations<Configuration>(bfg, ConfigType.movementStrategy),
				new GenericSelector.ShowBFGName<Configuration>());
		
		//now add them all
		this.add(hpPanel);
		this.add(combatStrat);
		this.add(moveStrat);
	}

	@Override
	public void setData(Configuration cd) {
		UnitDefinition ud = (UnitDefinition)cd;
		maxHP.setText(ud.getMaxHP() + "");
		combatStrat.setSelectedObject(ud.getCombatStratConfig());
		moveStrat.setSelectedObject(ud.getMovementStratConfig());
		
		this.validate();
		this.updateUI();
	}
	
	public void resetEditor()
	{
		maxHP.setText("");
		combatStrat.setSelectedObject(null);
		moveStrat.setSelectedObject(null);
	}

	@Override
	public Configuration instantiateNewConfiguration() {
		return new UnitDefinition();
	}

	@Override
	public ConfigType getData(Configuration toSet) {
		UnitDefinition ud = (UnitDefinition)toSet;
		Scanner s = new Scanner(maxHP.getText());
		if(s.hasNextDouble())
			ud.setMaxHP(s.nextDouble());
		else
			ud.setMaxHP(0);
		ud.setCombatStratConfig((CombatStrategyConfiguration) combatStrat.getSelectedObject());
		ud.setMovementStratConfig((MovementStrategyConfiguration) moveStrat.getSelectedObject());
		return ConfigType.unit;
	}

	@Override
	public List<ConfigType> getAllLoadableTypes() {
		List<ConfigType> ret = new ArrayList<ConfigType>();
		ret.add(ConfigType.unit);
		return ret;
	}

	@Override
	public JPanel getPanel() {
		return this;
	}

}
