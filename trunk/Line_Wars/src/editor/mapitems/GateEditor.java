package editor.mapitems;

import javax.swing.JPanel;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;

import editor.ConfigurationEditor;

public class GateEditor extends JPanel implements ConfigurationEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1347736687861020929L;

	@Override
	public void setData(ConfigData cd) {
		
	}

	@Override
	public void forceSetData(ConfigData cd) {
		
	}

	@Override
	public void reset() {
		
	}

	@Override
	public ConfigData getData() {
		ConfigData cd = new ConfigData();
		cd.set(ParserKeys.coefficients, "dummy");
		return cd;
	}

	@Override
	public ParserKeys getType() {
		return ParserKeys.gateURI;
	}

	@Override
	public JPanel getPanel() {
		return this;
	}

}
