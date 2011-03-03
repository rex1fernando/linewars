package editor.mapitems;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import linewars.gamestate.mapItems.ProjectileDefinition;
import linewars.gamestate.mapItems.strategies.impact.ImpactStrategyConfiguration;

import configuration.Configuration;
import editor.BigFrameworkGuy.ConfigType;
import editor.BigFrameworkGuy;
import editor.ConfigurationEditor;
import editor.GenericSelector;
import editor.GenericSelector.CustomToString;
import editor.GenericSelector.GenericListCallback;

/**
 * 
 * @author Connor Schenck
 *
 *  This class represents the panel that allows users
 * to edit the specific values related to only
 * projectiles.
 *
 */
public class ProjectileEditor extends JPanel implements ConfigurationEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8198606751190360416L;
	
	//variable associated with the velocity
	private JTextField velocity;
	
	//variables associated with the impact strategy
	private GenericSelector<Configuration> impactStrat;
	
	public ProjectileEditor(BigFrameworkGuy bfg)
	{
		//set up the velocity panel
		velocity = new JTextField();
		velocity.setColumns(20);
		JPanel velPanel = new JPanel();
		velPanel.add(new JLabel("Velocity:"));
		velPanel.add(velocity);
		
		//set up the impact strat panel
		impactStrat = new GenericSelector<Configuration>("Impact Strategy", 
				new GenericSelector.SelectConfigurations<Configuration>(bfg, ConfigType.impactStrategy),
				new GenericSelector.ShowBFGName<Configuration>());
		
		//set up this panel
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(velPanel);
		this.add(impactStrat);
	}
	

	@Override
	public void setData(Configuration cd) {
		ProjectileDefinition pd = (ProjectileDefinition)cd;
		velocity.setText(pd.getVelocity() + "");
		impactStrat.setSelectedObject(pd.getImpactStratConfig());
	}
	
	public void resetEditor()
	{
		velocity.setText("");
		impactStrat.setSelectedObject(null);
	}

	@Override
	public Configuration instantiateNewConfiguration() {
		return new ProjectileDefinition();
	}

	@Override
	public ConfigType getData(Configuration toSet) {
		ProjectileDefinition pd = (ProjectileDefinition)toSet;
		
		Scanner s = new Scanner(velocity.getText());
		if(s.hasNextDouble())
			pd.setVelocity(s.nextDouble());
		else
			pd.setVelocity(0);
		
		pd.setImpactStratConfig((ImpactStrategyConfiguration) impactStrat.getSelectedObject());
		
		return ConfigType.projectile;
	}

	@Override
	public List<ConfigType> getAllLoadableTypes() {
		List<ConfigType> ret = new ArrayList<ConfigType>();
		ret.add(ConfigType.part);
		return ret;
	}

	@Override
	public JPanel getPanel() {
		return this;
	}

}
