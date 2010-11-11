package editor.tech;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ParserKeys;


public class FunctionPanel implements ActionListener{

	private ConfigData cd;
	private JComboBox typeOptionBox;
	private JTextField c0;
	private JTextField c1;
	private JTextField c2;
	private JTextField addCoefficient;
	private JPanel panel;
	private JLabel c0Label;
	private JLabel c1Label;
	private JLabel c2Label;
	private JLabel addCoefficientLabel;
	private JTextArea functionDisplay;
	
	private List<Double> coefficients;
	int line = 0;
	
	public FunctionPanel(ConfigData cd)
	{
		String[] typeOptions = {"", "Polynomial", "Exponential"};
		this.cd = cd;
		functionDisplay = new JTextArea(15, 15);
		c0Label = new JLabel("c: ");
		c1Label = new JLabel("c1: ");
		c2Label = new JLabel("c2: ");
		addCoefficientLabel = new JLabel("Coefficient: ");
		coefficients = new ArrayList<Double>();
		
		
		coefficients = cd.getNumberList(ParserKeys.coefficients);
		for(int i = coefficients.size(); i <= 3; i++){
			coefficients.add(null);
		}
		
		cd = new ConfigData();
		panel = new JPanel();
		typeOptionBox = new JComboBox(typeOptions);
		c0 = new JTextField(20);
		c1 = new JTextField(20);
		c2 = new JTextField(20);
		addCoefficient = new JTextField(20);
		

		typeOptionBox.addActionListener(this);
		c0.addActionListener(this);
		c1.addActionListener(this);
		c2.addActionListener(this);
		addCoefficient.addActionListener(this);
		panel.setPreferredSize(new Dimension(1200, 400));
		panel.add(typeOptionBox);
		panel.add(functionDisplay);

		
		
	}

	public JPanel getPanel(){
		return panel;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource() == typeOptionBox)
		{
			if(((String)typeOptionBox.getSelectedItem()).equals("Polynomial")){
				line = 0;
				coefficients.clear();
				coefficients.add(null);
				coefficients.add(null);
				coefficients.add(null);
				cd.set(ParserKeys.functionType, "Polynomial");
				panel.remove(functionDisplay);
				functionDisplay.setText("");
				c0.setText(coefficients.get(0).toString());
				c1.setText(coefficients.get(1).toString());
				c2.setText(coefficients.get(2).toString());
				addCoefficient.setText("");
				panel.remove(c0Label);
				panel.remove(c1Label);
				panel.remove(c2Label);
				panel.remove(c0);
				panel.remove(c1);
				panel.remove(c2);
				panel.add(addCoefficientLabel);
				panel.add(addCoefficient);
				panel.add(functionDisplay);
				panel.validate();
				panel.updateUI();
			}else if(((String)typeOptionBox.getSelectedItem()).equals("Exponential")){
								
				line = 0;
				coefficients.clear();
				coefficients.add(null);
				coefficients.add(null);
				coefficients.add(null);
				
				cd.set(ParserKeys.functionType, "Exponential");
				functionDisplay.setText("c0: \nc1: \nc2: \n");
				
				c0.setText("");
				c1.setText("");
				c2.setText("");
				addCoefficient.setText("");
				panel.remove(functionDisplay);
				panel.remove(addCoefficientLabel);
				panel.remove(addCoefficient);
				panel.add(c0Label);
				panel.add(c0);
				panel.add(c1Label);
				panel.add(c1);
				panel.add(c2Label);
				panel.add(c2);
				panel.add(functionDisplay);
				panel.validate();
				panel.updateUI();
			}
		}else if(arg0.getSource() == c0)
		{
			try{
				coefficients.add(0, Double.valueOf(c0.getText()));
				functionDisplay.replaceRange("c0 :" +c0.getText() +'\n', 0, functionDisplay.getLineEndOffset(0));
			}catch(NumberFormatException e){
				JOptionPane.showMessageDialog(panel, "Coefficients must be doubles.", "Error", JOptionPane.WARNING_MESSAGE);
			}catch(BadLocationException e){
				System.out.println("This shouldn't happen.");
			}
		}else if(arg0.getSource() == c1)
		{
			try{
				coefficients.add(1, Double.valueOf(c1.getText()));
				functionDisplay.replaceRange("c1 :" +c1.getText() +'\n', functionDisplay.getLineEndOffset(0), functionDisplay.getLineEndOffset(1));
			}catch(NumberFormatException e){
				JOptionPane.showMessageDialog(panel, "Coefficients must be doubles.", "Error", JOptionPane.WARNING_MESSAGE);
			}catch(BadLocationException e){
				System.out.println("This shouldn't happen.");
			}
		}else if(arg0.getSource() == c2)
		{
			try{
				coefficients.add(2, Double.valueOf(c2.getText()));
				functionDisplay.replaceRange("c2: " +c2.getText() +'\n', functionDisplay.getLineEndOffset(1), functionDisplay.getLineEndOffset(2));
			}catch(NumberFormatException e){
				JOptionPane.showMessageDialog(panel, "Coefficients must be doubles.", "Error", JOptionPane.WARNING_MESSAGE);
			}catch(BadLocationException e){
				System.out.println("This shouldn't happen.");
			}
		}else if(arg0.getSource() == addCoefficient)
		{
			try{
				coefficients.add(Double.valueOf(addCoefficient.getText()));
				functionDisplay.append("c" +line +" " +addCoefficient.getText() + "\n");
				line++;
			}catch(NumberFormatException e){
				JOptionPane.showMessageDialog(panel, "Coefficients must be doubles.", "Error", JOptionPane.WARNING_MESSAGE);
			}
		}
	}
	
	public static void main(String[] args) {
		FunctionPanel fp = new FunctionPanel(new ConfigData());
		JFrame frame = new JFrame("test frame");
		frame.setContentPane(fp.getPanel());
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	
}
