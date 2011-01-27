package editor.mapitems;

import java.util.Scanner;

import javax.swing.*;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;

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
		setData(cd, false);
	}

	@Override
	public void forceSetData(ConfigData cd) {
		setData(cd, true);
	}
	
	private void setData(ConfigData cd, boolean force)
	{
		if(cd.getDefinedKeys().contains(ParserKeys.maxHP))
		{
			Double d = cd.getNumber(ParserKeys.maxHP);
			if(d != null)
				maxHP.setText(d.toString());
			else if(force)
				maxHP.setText("");
			else
				throw new IllegalArgumentException("Max HP is not defined");
		}
		else if(force)
			maxHP.setText("");
		else
			throw new IllegalArgumentException("Max HP is not defined");
	}

	@Override
	public void reset() {
		maxHP.setText("");
	}

	@Override
	public Configuration getData() {
		ConfigData cd = new ConfigData();
		cd.set(ParserKeys.coefficients, "dummy");
		Scanner s = new Scanner(maxHP.getText());
		if(s.hasNextDouble())
			cd.set(ParserKeys.maxHP, s.nextDouble());
		
		ConfigData combat = new ConfigData();
		combat.set(ParserKeys.type, "NoCombat");
		cd.set(ParserKeys.combatStrategy, combat);
		
		ConfigData mov = new ConfigData();
		mov.set(ParserKeys.type, "Immovable");
		cd.set(ParserKeys.movementStrategy, mov);
		return cd;
	}

	@Override
	public ConfigType getType() {
		return ParserKeys.gateURI;
	}

	@Override
	public JPanel getPanel() {
		return this;
	}
	
	@Override
	public boolean isValidConfig() {
		Scanner s = new Scanner(maxHP.getText());
		if(!s.hasNextDouble())
			return false;
		
		return true;
	}

}
