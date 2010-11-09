package editor.animations;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class ArithmaticPanel extends JPanel implements ActionListener {
	
	private JTextField number;
	private JButton mult;
	private JButton add;
	private JButton equal;
	private JButton selectAll;
	private JButton deselectAll;
	
	private AnimationEditor window;
	
	public ArithmaticPanel(AnimationEditor w)
	{
		window = w;
		
		number = new JTextField();
		mult = new JButton("x");
		mult.addActionListener(this);
		add = new JButton("+");
		add.addActionListener(this);
		equal = new JButton("=");
		equal.addActionListener(this);
		selectAll = new JButton("Select All");
		selectAll.addActionListener(this);
		deselectAll = new JButton("Deselect All");
		deselectAll.addActionListener(this);
		
		this.setLayout(new BorderLayout());
		JPanel top = new JPanel();
		top.add(mult);
		top.add(add);
		top.add(equal);
		this.add(top, BorderLayout.NORTH);
		
		number.setColumns(10);
		this.add(number, BorderLayout.CENTER);
		
		JPanel bottom = new JPanel();
		bottom.add(selectAll);
		bottom.add(deselectAll);
		this.add(bottom, BorderLayout.SOUTH);
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource().equals(mult) || ae.getSource().equals(add) || ae.getSource().equals(equal))
		{
			Scanner s = new Scanner(number.getText());
			if(!s.hasNextDouble())
				return;
			double num = s.nextDouble();
			
			number.setText("");
			
			ArrayList<Frame> list = window.getFrames();
			for(Frame f : list)
			{
				if(f.getChecked())
				{
					int time = f.getTime();
					if(ae.getSource().equals(mult))
						time *= num;
					else if(ae.getSource().equals(add))
						time += num;
					else
						time = (int) num;
					f.setTime(time + "");
						
				}
			}
			
		}
		else if(ae.getSource().equals(selectAll))
		{
			ArrayList<Frame> list = window.getFrames();
			for(Frame f : list)
				f.setChecked(true);
		}
		else if(ae.getSource().equals(deselectAll))
		{
			ArrayList<Frame> list = window.getFrames();
			for(Frame f : list)
				f.setChecked(false);
		}
	}
	
	

}
