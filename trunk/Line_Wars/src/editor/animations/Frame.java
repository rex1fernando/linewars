package editor.animations;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
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




/**
 * 
 * @author Connor Schenck
 *
 * This class represents a frame in the animation. It knows
 * what image it is, the timing for this image, and the file
 * location of that image. It extends JPanel so any panel
 * wanting to display information about this frame can just
 * add this frame directly to the containing panel.
 * 
 */
public class Frame extends JPanel implements ItemListener{

	private JLabel file;
	private JTextField time;
	private Sprite frame;
	private JCheckBox checkBox;
	private boolean checked = false;
	
	/**
	 * Creates a new frame. Takes in the file path to the image
	 * for this frame. Sets the default timing to 200ms.
	 * 
	 * @param filePath	the file path to the image
	 * @throws IOException
	 */
	public Frame(String filePath, String imagePath) throws IOException
	{
		frame = new Sprite(filePath, new File(filePath).getParentFile().equals(new File(imagePath)));
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
	
	/**
	 * 
	 * @return	the sprite object associated with the image for this frame
	 */
	public Sprite getFrame() {
		return frame;
	}
	
	/**
	 * 
	 * @return	the timing (in ms) for this frame
	 */
	public int getTime() {
		try {
			return (int) new Scanner(time.getText()).nextDouble();
		} catch(Exception e) {
			return 200;
		}
	}
	
	/**
	 * 
	 * @param times	the timing (in ms) to set this frame to
	 */
	public void setTime(String times) {
		time.setText(times);
	}
	
	/**
	 * 
	 * @return	true if the checkbox for this frame is checked; false otherwise
	 */
	public boolean getChecked()
	{
		return checked;
	}
	
	/**
	 * 
	 * @param value	the state to set the checkbox for this frame to.
	 */
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
