import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSplitPane;

import parser.ConfigFile;
import parser.Parser;
import parser.ParserKeys;



public class Window implements ActionListener {

	private JFrame frame;
	private Canvas canvas;
	private BufferStrategy strategy;
	private JButton addFiles;
	private JButton resetSpeed;
	private JMenuItem clearFiles;
	private JMenuItem export;
	private JMenuItem open;
	private JMenuItem setBackground;
	private JMenuItem clearBackground;
	private JMenuItem setSpeedRange;
	private JPanel scrollPanel;
	private JSlider speedSlider;
	
	private Sprite background = null;
	
	private ArrayList<Frame> list = new ArrayList<Frame>();
	private ArrayList<Frame> newList = null;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
			
		new Window().start();
	}
	
	public void start() 
	{
		
		//set up the Canvas
		canvas = new Canvas();
		frame = new JFrame("VN Game");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//set up the menu
		clearFiles = new JMenuItem("Clear Files");
		clearFiles.addActionListener(this);
		export = new JMenuItem("Export");
		export.addActionListener(this);
		open = new JMenuItem("Open");
		open.addActionListener(this);
		setBackground = new JMenuItem("Set Background");
		setBackground.addActionListener(this);
		clearBackground = new JMenuItem("Clear Background");
		clearBackground.addActionListener(this);
		setSpeedRange = new JMenuItem("Set Background Speed Max");
		setSpeedRange.addActionListener(this);
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenu edit = new JMenu("Edit");
		file.add(export);
		file.add(open);
		edit.add(clearFiles);
		edit.add(setBackground);
		edit.add(clearBackground);
		edit.add(setSpeedRange);
		menuBar.add(file);
		menuBar.add(edit);
		frame.setJMenuBar(menuBar);
		
		
		//set up the buttons
		addFiles = new JButton("Add Files");
		addFiles.addActionListener(this);
		
		ArithmaticPanel ap = new ArithmaticPanel(this);
		
		speedSlider = new JSlider(JSlider.VERTICAL, -1000, 1000, 0);
		speedSlider.setMinorTickSpacing(1);
		
		resetSpeed = new JButton("Reset Background Speed");
		resetSpeed.addActionListener(this);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(addFiles);
		buttonPanel.add(new JSeparator(JSeparator.VERTICAL));
		buttonPanel.add(ap);
		buttonPanel.add(speedSlider);
		buttonPanel.add(resetSpeed);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(buttonPanel, BorderLayout.NORTH);
		scrollPanel = new JPanel();
		JScrollPane scrollPane = new JScrollPane(scrollPanel);
		panel.add(scrollPane, BorderLayout.CENTER);
		
		JSplitPane pane = new JSplitPane();
		pane.setLeftComponent(panel);
		pane.setRightComponent(canvas);
		pane.setDividerLocation(500);
		
		frame.getContentPane().add(pane, BorderLayout.CENTER);
		frame.pack();
		frame.setMinimumSize(new Dimension(800, 600));
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
		
		canvas.setIgnoreRepaint(true);
		canvas.requestFocus();
		canvas.setFocusTraversalKeysEnabled(false);
		canvas.createBufferStrategy(3);
		strategy = canvas.getBufferStrategy();
		
		scrollPanel.removeAll();
        scrollPanel.setLayout(new BoxLayout(scrollPanel, BoxLayout.Y_AXIS));
        
        try {
        	Scanner s = new Scanner(new File("lastBackground.txt"));
        	background = new Sprite(s.nextLine());
        }
        catch(Exception e) {}
		
		
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
				if(newList.isEmpty())
					list.clear();
				else
				{
					for(Frame f : newList)
						list.add(f);
				}
				newList = null;
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
			int returnVal = fc.showOpenDialog(frame);

	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File[] file = fc.getSelectedFiles();
	            
	            ArrayList<Frame> newList = new ArrayList<Frame>();
	            for(File f : file)
	            {
	            	newList.add(new Frame(f.getAbsolutePath()));
	            	scrollPanel.add(newList.get(newList.size() - 1));
	            }
	            
	            this.newList = newList;
	            
	            frame.pack();
	            
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
		else if(arg0.getSource().equals(clearFiles))
		{
			scrollPanel.removeAll();
            scrollPanel.setLayout(new BoxLayout(scrollPanel, BoxLayout.Y_AXIS));
            newList = new ArrayList<Frame>();
		}
		else if(arg0.getSource().equals(export))
		{
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if(fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION)
			{
				File dir = fc.getSelectedFile();
				if(!dir.exists())
					dir.mkdir();
				else if(dir.isFile())
				{
					dir.delete();
					dir.mkdir();
				}
				
				String names = "icon = ";
				String times = "displayTime = ";
				
				for(Frame f : list)
				{
					File to = new File(dir, new File(f.getFrame().toString()).getName());
					try {
						FileCopy.copy(f.getFrame().toString(), to.getAbsolutePath());
					} catch (IOException e) {
						e.printStackTrace();
						return;
					}
					
					names += new File(f.getFrame().toString()).getName() + ", ";
					times += f.getTime() + ", ";
				}
				
				names = names.substring(0, names.lastIndexOf(','));
				times = times.substring(0, times.lastIndexOf(','));
				
				try {
					BufferedWriter writer = new BufferedWriter(new FileWriter(new File(dir, dir.getName() + ".cfg")));
					writer.write(names);
					writer.newLine();
					writer.write(times);
					writer.flush();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		else if(arg0.getSource().equals(open))
		{
			JFileChooser fc = new JFileChooser();
			
			try {
				Scanner s = new Scanner(new File("lastDirectory.txt"));
				fc = new JFileChooser(s.nextLine());
			} catch (FileNotFoundException e) {	}
			
			fc.setMultiSelectionEnabled(false);
			int returnVal = fc.showOpenDialog(frame);

	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	        	scrollPanel.removeAll();
	            scrollPanel.setLayout(new BoxLayout(scrollPanel, BoxLayout.Y_AXIS));
	            newList = new ArrayList<Frame>();
	            
	            Parser parser = null;
	            try {
	            	parser = new Parser(new ConfigFile(fc.getSelectedFile().getAbsolutePath()));
	            	String[] names = parser.getList(ParserKeys.icon);
	            	String[] times = parser.getList(ParserKeys.displayTime);
	            	
	            	ArrayList<Frame> newList = new ArrayList<Frame>();
		            for(int i = 0; i < names.length; i++)
		            {
		            	Frame f = new Frame(new File(fc.getSelectedFile().getParentFile(), names[i]).getAbsolutePath());
		            	f.setTime(times[i]);
		            	newList.add(f);
		            	scrollPanel.add(f);
		            }
		            
		            this.newList = newList;
		            
		            frame.pack();
	            } catch(Exception e) {
	            	e.printStackTrace();
	            	return;
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
			int returnVal = fc.showOpenDialog(frame);

	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	        	background = new Sprite(fc.getSelectedFile().getAbsolutePath());
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
		}
		else if(arg0.getSource().equals(resetSpeed))
		{
			speedSlider.setValue(0);
		}
		else if(arg0.getSource().equals(setSpeedRange))
		{
			String s = (String)JOptionPane.showInputDialog(frame, "Input the maximum background speed", "",
					JOptionPane.PLAIN_MESSAGE, null, null, "" + speedSlider.getMaximum());
			int i = Integer.valueOf(s);
			speedSlider.setMaximum(i);
			speedSlider.setMinimum(-i);
		}
	}
	
	public ArrayList<Frame> getFrames()
	{
		return list;
	}

}
