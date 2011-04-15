package editor.mapitems;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import linewars.gamestate.mapItems.ProjectileDefinition;
import linewars.gamestate.mapItems.strategies.impact.ImpactStrategyConfiguration;
import linewars.gamestate.mapItems.strategies.targeting.TargetingStrategyConfiguration;

import configuration.Configuration;
import editor.BigFrameworkGuy.ConfigType;
import editor.BigFrameworkGuy;
import editor.ConfigurationEditor;
import editor.GenericSelector;

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
	
	//variable associated with the durability
	private JTextField durability;
	
	//variables associated with the impact strategy
	private GenericSelector<Configuration> impactStrat;
	
	//variables associated with the targeting strategy
	private GenericSelector<Configuration> targetingStrat;
	
	public ProjectileEditor(BigFrameworkGuy bfg)
	{
		//set up the durability panel
		durability = new JTextField();
		durability.setColumns(20);
		JPanel durPanel = new JPanel();
		durPanel.add(new JLabel("Base Durability:"));
		durPanel.add(durability);
		
		//set up the impact strat panel
		impactStrat = new GenericSelector<Configuration>("Impact Strategy", 
				new GenericSelector.SelectConfigurations<Configuration>(bfg, ConfigType.impactStrategy),
				new GenericSelector.ShowBFGName<Configuration>());
		
		//set up the targeting strat panel
		targetingStrat = new GenericSelector<Configuration>("Targeting Strategy", 
				new GenericSelector.SelectConfigurations<Configuration>(bfg, ConfigType.targetingStrategy),
				new GenericSelector.ShowBFGName<Configuration>());
		
		//set up this panel
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(durPanel);
		this.add(impactStrat);
		this.add(targetingStrat);
	}
	

	@Override
	public void setData(Configuration cd) {
		ProjectileDefinition pd = (ProjectileDefinition)cd;
		durability.setText(pd.getBaseDurability() + "");
		impactStrat.setSelectedObject(pd.getImpactStratConfig());
		targetingStrat.setSelectedObject(pd.getTargetingStratConfig());
	}
	
	public void resetEditor()
	{
		durability.setText("");
		impactStrat.setSelectedObject(null);
		targetingStrat.setSelectedObject(null);
	}

	@Override
	public Configuration instantiateNewConfiguration() {
		return new ProjectileDefinition();
	}

	@Override
	public ConfigType getData(Configuration toSet) {
		ProjectileDefinition pd = (ProjectileDefinition)toSet;
		
		Scanner s = new Scanner(durability.getText());
		if(s.hasNextDouble())
			pd.setBaseDurability(s.nextDouble());
		else
			pd.setBaseDurability(0);
		
		pd.setImpactStratConfig((ImpactStrategyConfiguration) impactStrat.getSelectedObject());
		
		pd.setTargetingStratConfig((TargetingStrategyConfiguration) targetingStrat.getSelectedObject());
		
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
