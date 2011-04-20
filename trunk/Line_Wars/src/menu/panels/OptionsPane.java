package menu.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import linewars.display.sound.SoundPlayer;
import linewars.display.sound.SoundPlayer.SoundType;
import menu.ContentProvider;
import menu.ContentProvider.MenuImage;
import menu.WindowManager;
import menu.components.CustomCheckBox;
import menu.components.CustomSlider;
import menu.components.MenuButton;
import menu.components.MenuTextField;

public class OptionsPane extends JPanel
{
	private static final long serialVersionUID = 2104236176196959841L;

	public static final String FILENAME = "resources/Options.cfg";
	
	private MenuTextField nameField;
	private CustomSlider musicSlider;
	private CustomSlider effectsSlider;
	private CustomCheckBox healthCheckBox;
	
	private WindowManager wm;
	
	public OptionsPane(WindowManager wm)
	{
		this.wm = wm;
		initComponents();
	}
	
	private class ApplyButtonClick implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			saveOptions();
			SoundPlayer.getInstance().setVolume(SoundType.MUSIC, musicSlider.getValue()/100.0);
			SoundPlayer.getInstance().setVolume(SoundType.SOUND_EFFECT, effectsSlider.getValue()/100.0);
		}
	}
	
	private class BackButtonClick implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			wm.gotoTitleMenu();
		}
	}
	
    @Override
    public void paintComponent(Graphics g)
    {
    	Image img = ContentProvider.getImageResource(MenuImage.options_back);
    	g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
    }
    
    public void loadOptions()
    {
    	try {
			Scanner in = new Scanner(new FileInputStream(new File(FILENAME)));
			nameField.setText(in.next());
			musicSlider.setValue(in.nextInt());
			effectsSlider.setValue(in.nextInt());
			healthCheckBox.setSelected(in.nextBoolean());
			in.close();
		}
    	catch (Exception e)
		{
			nameField.setText("Unamed-Noob");
			musicSlider.setValue(50);
			effectsSlider.setValue(50);
			healthCheckBox.setSelected(false);
		}
    }
    
    public String getPlayerName()
    {
    	loadOptions();
    	return nameField.getText();
    }
    
    public boolean getHealthBarsSelected()
    {
    	return healthCheckBox.isSelected();
    }
    
    private void saveOptions()
    {
    	try {
			PrintWriter pw = new PrintWriter(new File(FILENAME));
			pw.println(nameField.getText());
			pw.println(musicSlider.getValue());
			pw.println(effectsSlider.getValue());
			pw.println(healthCheckBox.isSelected());
			pw.close();
		} catch (FileNotFoundException e) {}
    }
	
	private class Option extends JPanel
	{
		private static final long serialVersionUID = -454128596201128963L;

		public Option(String text, JComponent c)
		{
			Dimension size = new Dimension(500, 100);
			setPreferredSize(size);
			setMinimumSize(size);
			setMaximumSize(size);
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			setOpaque(false);
			add(Box.createHorizontalGlue());
			
			JLabel label = new JLabel(text);
			label.setFont(ContentProvider.FONT.deriveFont(16.0f));
			label.setForeground(Color.black);
			add(label);
			add(Box.createRigidArea(new Dimension(10, 0)));
			
			size = new Dimension(300,28);
			c.setPreferredSize(size);
			c.setMinimumSize(size);
			c.setMaximumSize(size);
			add(c);
		}
	}
	
	private void initComponents()
	{
		Dimension size = new Dimension(1024,640);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		add(Box.createRigidArea(new Dimension(0, 50)));
		JLabel titleLabel = new JLabel("Options");
		titleLabel.setFont(ContentProvider.FONT.deriveFont(32.0f));
		titleLabel.setForeground(Color.black);
		add(titleLabel);
		add(Box.createRigidArea(new Dimension(0, 50)));
		
		nameField = new MenuTextField();
		nameField.setFont(ContentProvider.FONT.deriveFont(16.0f));
		add(new Option("Player Name", nameField));
		
		musicSlider = new CustomSlider();
		musicSlider.setMaximum(100);
		musicSlider.setMinimum(0);
		add(new Option("Music Volume", musicSlider));
		
		effectsSlider = new CustomSlider();
		effectsSlider.setMaximum(100);
		effectsSlider.setMinimum(0);
		add(new Option("Effects Volume", effectsSlider));
		
		JPanel boxPanel = new JPanel();
		boxPanel.setOpaque(false);
		boxPanel.setLayout(new BoxLayout(boxPanel, BoxLayout.X_AXIS));
		healthCheckBox = new CustomCheckBox();
		boxPanel.add(healthCheckBox);
		boxPanel.add(Box.createHorizontalGlue());
		add(new Option("Health Always On", boxPanel));
		
		add(Box.createVerticalGlue());
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(Box.createHorizontalGlue());
		
		// Buttons
		int font = 16;
		MenuButton apply = new MenuButton(MenuImage.options_button_default, MenuImage.options_button_rollover, font);
		apply.setText("Apply");
		size = new Dimension(150, 40);
		apply.setPreferredSize(size);
		apply.setMinimumSize(size);
		apply.setMaximumSize(size);
		apply.addActionListener(new ApplyButtonClick());
		panel.add(apply);
		panel.add(Box.createRigidArea(new Dimension(20, 0)));
		
		MenuButton back = new MenuButton(MenuImage.options_button_default, MenuImage.options_button_rollover, font);
		back.setText("Back");
		back.setPreferredSize(size);
		back.setMinimumSize(size);
		back.setMaximumSize(size);
		back.addActionListener(new BackButtonClick());
		panel.add(back);
		panel.add(Box.createRigidArea(new Dimension(30, 0)));
		
		add(panel);
		add(Box.createRigidArea(new Dimension(0,20)));
	}
}
