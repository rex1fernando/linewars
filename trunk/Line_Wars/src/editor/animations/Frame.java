package editor.animations;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;




public class Frame extends JPanel implements ItemListener{

	private JLabel file;
	private JTextField time;
	private Sprite frame;
	private JCheckBox checkBox;
	private boolean checked = false;
	
	public Frame(String filePath) throws IOException
	{
		frame = new Sprite(filePath);
		ImageIcon icon = new ImageIcon(filePath);
		Image img = icon.getImage();
		img = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
		icon = new ImageIcon(img);
		this.add(new JLabel(icon));
		file = new JLabel(new File(filePath).getName());
		this.add(file);
		time = new JTextField("200");
		time.setColumns(10);
		this.add(time);
		checkBox = new JCheckBox();
		checkBox.setSelected(false);
		checkBox.addItemListener(this);
		checkBox.setPreferredSize(new Dimension(50, 50));
		this.add(checkBox);
		
	}
	
	public Sprite getFrame() {
		return frame;
	}
	
	public int getTime() {
		try {
			return (int) new Scanner(time.getText()).nextDouble();
		} catch(Exception e) {
			return 200;
		}
	}
	
	public void setTime(String times) {
		time.setText(times);
	}
	
	public boolean getChecked()
	{
		return checked;
	}
	
	public void setChecked(boolean value)
	{
		checked = value;
		checkBox.setSelected(value);
	}

	@Override
	public void itemStateChanged(ItemEvent arg0) {
		checked = arg0.getStateChange() == ItemEvent.SELECTED;
	}
	
}
