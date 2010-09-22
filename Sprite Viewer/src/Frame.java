import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;




public class Frame extends JPanel {

	private JLabel file;
	private JTextField time;
	private Sprite frame;
	
	public Frame(String filePath)
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
		
	}
	
	public Sprite getFrame() {
		return frame;
	}
	
	public int getTime() {
		try {
			return new Scanner(time.getText()).nextInt();
		} catch(Exception e) {
			return 200;
		}
	}
	
}
