package editor.animations;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;

import linewars.display.Animation;

import configuration.Configuration;
import editor.BigFrameworkGuy.ConfigType;
import editor.ConfigurationEditor;



/**
 * 
 * @author Connor Schenck
 *
 *A panel that allows the animations to be edited.
 *
 */
public class AnimationEditor implements ActionListener, ConfigurationEditor, Runnable {

	
	private JPanel mainPanel; 
	private Canvas canvas;
	private BufferStrategy strategy;
	private JButton addFiles;
	private JButton resetSpeed;
	private JButton setBackground;
	private JButton clearBackground;
	private JButton setSpeedRange;
	private JButton removeSelected;
	private JButton applyRotation;
	private JPanel scrollPanel;
	private JScrollPane scrollPane;
	private JPanel leftPanel;
	private JSlider speedSlider;
	
	private Sprite background = null;
	
	private ArrayList<Frame> list = new ArrayList<Frame>();
	private ArrayList<Frame> newList = null;
	
	private String animationFolder;
	
	/**
	 * Animation editor constructor. Puts all the components in
	 * the contained panel. Any container that wishes to include
	 * this panel must explicitly add it to that container.
	 * 
	 * @param folderToPutImages		
	 */
	public AnimationEditor(String folderToPutImages)
	{
		this.animationFolder = folderToPutImages;
		
		//set up the Canvas
		canvas = new Canvas();
		canvas.setSize(500, 500);
		mainPanel = new JPanel();
		
		
		//set up the menu
		setBackground = new JButton("Set Background");
		setBackground.addActionListener(this);
		clearBackground = new JButton("Clear Background");
		clearBackground.addActionListener(this);
		setSpeedRange = new JButton("Set Background Speed Max");
		setSpeedRange.addActionListener(this);
		
		
		//set up the buttons
		addFiles = new JButton("Add Files");
		addFiles.addActionListener(this);
		
		ArithmaticPanel ap = new ArithmaticPanel(this);
		
		speedSlider = new JSlider(JSlider.VERTICAL, -1000, 1000, 0);
		speedSlider.setMinorTickSpacing(1);
		
		resetSpeed = new JButton("Reset Background Speed");
		resetSpeed.addActionListener(this);
		
		removeSelected = new JButton("Remove Selected");
		removeSelected.addActionListener(this);
		
		applyRotation = new JButton("Rotate...");
		applyRotation.addActionListener(this);
		
		JPanel buttonLeftPanel = new JPanel();
		buttonLeftPanel.setLayout(new BoxLayout(buttonLeftPanel, BoxLayout.Y_AXIS));
		
		JPanel buttonPanel = new JPanel();
		buttonLeftPanel.add(addFiles);
		buttonLeftPanel.add(setBackground);
		buttonLeftPanel.add(clearBackground);
		buttonLeftPanel.add(setSpeedRange);
		buttonLeftPanel.add(resetSpeed);
		buttonLeftPanel.add(removeSelected);
		buttonLeftPanel.add(applyRotation);
		buttonPanel.add(buttonLeftPanel);
		buttonPanel.add(ap);
		buttonPanel.add(speedSlider);
		
		leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());
		leftPanel.add(buttonPanel, BorderLayout.NORTH);
		scrollPanel = new JPanel();
		scrollPane = new JScrollPane(scrollPanel);
		scrollPane.setPreferredSize(new Dimension(300, 300));
		leftPanel.add(scrollPane, BorderLayout.CENTER);
		
		JSplitPane pane = new JSplitPane();
		pane.setLeftComponent(leftPanel);
		pane.setRightComponent(canvas);
		pane.setDividerLocation(500);
		
		mainPanel.add(pane, BorderLayout.CENTER);
//		mainPanel.setMinimumSize(new Dimension(800, 600));
		
		scrollPanel.removeAll();
        scrollPanel.setLayout(new BoxLayout(scrollPanel, BoxLayout.Y_AXIS));
        
        try {
        	Scanner s = new Scanner(new File("lastBackground.txt"));
        	background = new Sprite(s.nextLine(), false);
        }
        catch(Exception e) {}
	}
	
	@Override
	public void run() 
	{
		
		canvas.setIgnoreRepaint(true);
		canvas.requestFocus();
		canvas.setFocusTraversalKeysEnabled(false);
		canvas.createBufferStrategy(3);
		strategy = canvas.getBufferStrategy();
		
		boolean b = true;
		int current = 0;
		long lastChangeTime = System.currentTimeMillis();
		int backgroundPos = 0;
		long loopTime = System.currentTimeMillis();
		while(b)
		{
			//get graphics object to draw to
			Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
			g.setColor(Color.black);
			g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
			
			if(current > list.size())
				current = 0;
			
			//draw the background if there is one
			if(background != null)
			{
				int height = (int) (((double)canvas.getWidth()/background.getWidth())*background.getHeight());
				backgroundPos += ((double)-speedSlider.getValue()*(System.currentTimeMillis() - loopTime)/1000.0);
				backgroundPos %= height;
				loopTime = System.currentTimeMillis();
				
				for(int pos = backgroundPos - height; pos <= canvas.getHeight(); pos += height )
					background.draw(g, 0, pos, canvas.getWidth(), height);
			}
			
			if(!list.isEmpty())
			{
				list.get(current).getFrame().draw(g, 0, 0, canvas.getWidth(), canvas.getWidth());
				
				if(System.currentTimeMillis() - lastChangeTime > list.get(current).getTime())
				{
					current = (current + 1)%list.size();
					lastChangeTime = System.currentTimeMillis();
				}
			}
			else if(background == null)
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {}
				
			//flip the buffers
			g.dispose();
			strategy.show();
				
			if(newList != null)
			{
				list.clear();
				scrollPanel.removeAll();
				for(Frame f : newList)
				{
					list.add(f);
					scrollPanel.add(f);
				}
				newList = null;
				current = 0;
				scrollPanel.validate();
				scrollPanel.updateUI();
			}
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {}
			
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource().equals(addFiles))
		{
			JFileChooser fc = new JFileChooser();
			
			try {
				Scanner s = new Scanner(new File("lastDirectory.txt"));
				fc = new JFileChooser(s.nextLine());
			} catch (FileNotFoundException e) {	}
			
			fc.setMultiSelectionEnabled(true);
			int returnVal = fc.showOpenDialog(mainPanel);

	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File[] file = fc.getSelectedFiles();
	            
	            ArrayList<Frame> newList = new ArrayList<Frame>();
	            
	            //add the current list
	            for(Frame f : list)
	            	newList.add(f);
	            
	           String errors = "";
	            for(File f : file)
	            {
	            	try {
						newList.add(new Frame(f.getAbsolutePath(), animationFolder));
					} catch (Exception e) {
						errors += f.getAbsolutePath() + "\n";
					}
	            }
	            
	            if(!errors.equals(""))
	            	JOptionPane.showMessageDialog(mainPanel,
	            	    "There was an error loading the following:\n" + errors,
	            	    "Error",
	            	    JOptionPane.ERROR_MESSAGE);
	            
	            this.newList = newList;
	            
	            scrollPane.validate();
	            scrollPane.updateUI();
	            
	            
	            try {
					FileWriter fWriter = new FileWriter("lastDirectory.txt");
					fWriter.write(file[0].getParent());
					fWriter.flush();
					fWriter.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
	        }
		}
		else if(arg0.getSource().equals(setBackground))
		{
			JFileChooser fc = new JFileChooser();
			
			try {
				Scanner s = new Scanner(new File("lastDirectory.txt"));
				fc = new JFileChooser(s.nextLine());
			} catch (FileNotFoundException e) {	}
			
			fc.setMultiSelectionEnabled(false);
			int returnVal = fc.showOpenDialog(mainPanel);

	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	        	try {
					background = new Sprite(fc.getSelectedFile().getAbsolutePath(), false);
				} catch (IOException e1) {
					e1.printStackTrace();
					return;
				}
	        	FileWriter fw;
				try {
					fw = new FileWriter("lastBackground.txt");
					fw.write(background.toString());
		        	fw.flush();
		        	fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
		}
		else if(arg0.getSource().equals(clearBackground))
		{
			background = null;
			FileWriter fw;
			try {
				fw = new FileWriter("lastBackground.txt");
				fw.write("");
	        	fw.flush();
	        	fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(arg0.getSource().equals(resetSpeed))
		{
			speedSlider.setValue(0);
		}
		else if(arg0.getSource().equals(setSpeedRange))
		{
			String s = (String)JOptionPane.showInputDialog(mainPanel, "Input the maximum background speed", "",
					JOptionPane.PLAIN_MESSAGE, null, null, "" + speedSlider.getMaximum());
			try {
				int i = Integer.valueOf(s);
				speedSlider.setMaximum(i);
				speedSlider.setMinimum(-i);
			} catch(NumberFormatException e) {
				
			}
		}
		else if(arg0.getSource().equals(removeSelected))
		{
			 ArrayList<Frame> newList = new ArrayList<Frame>();
			for(int i = 0; i < list.size();i++)
			{
				if(!list.get(i).getChecked())
					newList.add(list.get(i));
			}
			
			this.newList = newList;
		}
		else if(arg0.getSource().equals(applyRotation))
		{
			String s = (String)JOptionPane.showInputDialog(mainPanel, "Input the rotation in degrees", "",
					JOptionPane.PLAIN_MESSAGE, null, null, "0");
			try {
				double rot = Double.valueOf(s);
				
				for(Frame f : list)
					f.getFrame().rotate(-rot/180.0*Math.PI);
				
			} catch(NumberFormatException e) {
				
			}
		}
		
		mainPanel.validate();
		mainPanel.updateUI();
	}
	
	/**
	 * 
	 * @return	the list of frames for the animation being displayed
	 */
	public ArrayList<Frame> getFrames()
	{
		return list;
	}

	@Override
	public void setData(Configuration cd) {
		scrollPanel.removeAll();
        scrollPanel.setLayout(new BoxLayout(scrollPanel, BoxLayout.Y_AXIS));
        newList = new ArrayList<Frame>();
        
    	Animation an = (Animation)cd;
    	
    	ArrayList<Frame> newList = new ArrayList<Frame>();
        for(int i = 0; i < an.getNumImages(); i++)
        {
        	Frame f;
			try {
				f = new Frame(new File(new File(animationFolder), an.getImage(i)).getAbsolutePath(), animationFolder);
				f.setTime("" + an.getImageTime(i));
	        	newList.add(f);
	        	scrollPanel.add(f);
			} catch (Exception e) {
				throw new IllegalArgumentException(an.getImage(i) + " is not in " + animationFolder);
			}
        }
        
        this.newList = newList;
	}
	
	public void resetEditor()
	{
		scrollPanel.removeAll();
        scrollPanel.setLayout(new BoxLayout(scrollPanel, BoxLayout.Y_AXIS));
        newList = new ArrayList<Frame>();
	}

	@Override
	public Configuration instantiateNewConfiguration() {
        return new Animation();
	}

	@Override
	public ConfigType getData(Configuration toSet) {
		Animation an = (Animation)toSet;
		
		File dir = new File(animationFolder);
		for(Frame f : list)
		{
			File to = new File(dir, new File(f.getFrame().toString()).getName());
			try {
				f.getFrame().save(to.getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
			an.addFrame(new File(f.getFrame().toString()).getName(), (double)f.getTime());
		}
		
		return ConfigType.animation;
	}

	@Override
	public List<ConfigType> getAllLoadableTypes() {
		List<ConfigType> ret = new ArrayList<ConfigType>();
		ret.add(ConfigType.animation);
		return ret;
	}

	@Override
	public JPanel getPanel() {
		return mainPanel;
	}

}
