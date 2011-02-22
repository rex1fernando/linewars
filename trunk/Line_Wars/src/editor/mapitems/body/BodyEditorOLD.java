package editor.mapitems.body;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.*;

import configuration.Configuration;

import linewars.configfilehandler.ConfigData;
import linewars.configfilehandler.ConfigFileWriter;
import linewars.configfilehandler.ConfigData.NoSuchKeyException;
import linewars.configfilehandler.ConfigFileReader;
import linewars.configfilehandler.ConfigFileReader.InvalidConfigFileException;
import linewars.configfilehandler.ParserKeys;
import linewars.gamestate.Position;
import linewars.gamestate.mapItems.MapItemState;
import editor.BigFrameworkGuy.ConfigType;
import editor.ConfigurationEditor;
import editor.ListURISelector;
import editor.URISelector;
import editor.URISelector.SelectorOptions;
import editor.animations.Sprite;
import editor.mapitems.MapItemCommanalitiesEditor;

/**
 * 
 * @author Connor Schenck
 *
 * This class represents the editor that allows the user
 * to set the body for a map item. 
 * 
 */
public class BodyEditorOLD extends JPanel implements ConfigurationEditor, ActionListener, Runnable, MouseListener, WindowListener {
	
	private MapItemCommanalitiesEditor mie;
	
	//variable for selecting the shape type, at the moment only circle supported
	private URISelector shapeType;
	
	//variables for drawing the image
	private Canvas canvas;
	private BufferStrategy strategy;
	private Sprite[] images;
	private long[] imagetimes;
	
	//variable to save the circle (only circles allowed for now)
	private double radius;
	private Position center;
	
	//variable for setting the scale
	private JTextField scale;
	
	//buttons to save or cancel
	private JButton done;
	private JButton cancel;
	
	private JFrame frame;
	private boolean running;
	
	private boolean mouseDown;
	
	/**
	 * Constructs this body editor. Creates a frame that shows this editor.
	 * Needs a reference to the calling map item editor so it can set the
	 * body when the user clicks 'done'.
	 * 
	 * @param m	the calling map item editor
	 */
	public BodyEditorOLD(MapItemCommanalitiesEditor m)
	{
		mie = m;
		
		//set up the type selector
		shapeType = new URISelector("Type", new ShapeTypeSelector());
		
		//set up the canvas
		canvas = new Canvas();
		canvas.setSize(800, 600);
		canvas.addMouseListener(this);
		
		//set up the scale panel
		scale = new JTextField();
		scale.setColumns(10);
		JPanel scalePanel = new JPanel();
		scalePanel.add(scale);
		scalePanel.add(new JLabel("% scale"));
		
		//set up the button panel
		done = new JButton("Done");
		done.addActionListener(this);
		cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(done);
		buttonPanel.add(cancel);
		
		//now set up the whole panel
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(shapeType);
		this.add(canvas);
		this.add(scalePanel);
		this.add(buttonPanel);
		
		//set up the new frame
		frame = new JFrame("Body Editor");
		frame.setContentPane(this);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setVisible(true);
		frame.addWindowListener(this);
		
		running = true;
		
		this.instantiateNewConfiguration();
		
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		
		canvas.setIgnoreRepaint(true);
		canvas.requestFocus();
		canvas.setFocusTraversalKeysEnabled(false);
		canvas.createBufferStrategy(3);
		strategy = canvas.getBufferStrategy();
		
		//lets load the images
		ConfigData animation = null;
		try {
			animation = new ConfigFileReader(mie.getAnimation(MapItemState.Idle)).read();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidConfigFileException e) {
			e.printStackTrace();
		}
		if(animation == null)
			running = false;
		else
		{
			String[] uris = animation.getStringList(ParserKeys.icon).toArray(new String[0]);
			images = new Sprite[uris.length];
			Double[] times = animation.getNumberList(ParserKeys.displayTime).toArray(new Double[0]);
			imagetimes = new long[times.length];
			for(int i = 0; i < uris.length; i++)
			{
				try {
					images[i] = new Sprite(new File(new File(animation.getURI()).getParentFile(), uris[i]).getAbsolutePath(), false);
				} catch (IOException e) {
					e.printStackTrace();
					running = false;
					break;
				}
				imagetimes[i] = (long)(double)times[i];
			}
		}
		
		long lastTime = System.currentTimeMillis();
		int currentImage = 0;
		while(running)
		{
			//get graphics object to draw to
			Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
			g.setColor(Color.black);
			g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
			
			//calculate the width and height scaling and pick the smallest
			double widthScale = (double)canvas.getWidth()/images[currentImage].getWidth();
			double heightScale = (double)canvas.getHeight()/images[currentImage].getHeight();
			double scale = Math.min(widthScale, heightScale);
			
			//draw the image
			images[currentImage].draw(g, 0, 0,
					(int) (images[currentImage].getWidth() * scale),
					(int) (images[currentImage].getHeight() * scale));
			
			//check to see if we need to go to the next frame
			if(System.currentTimeMillis() - lastTime > imagetimes[currentImage])
				currentImage = (currentImage + 1)%images.length;
			
			//are we drawing a circle?
			if(shapeType.getSelectedURI().equalsIgnoreCase("Circle"))
			{
				center = new Position(scale
						* images[currentImage].getWidth() / 2, scale
						* images[currentImage].getHeight() / 2);
				g.setColor(new Color(35, 225, 241, 80));
				g.fillOval((int) (center.getX() - radius), (int) (center.getY() - radius),
						(int)(2*radius), (int)(2*radius));
				g.setColor(new Color(35, 225, 241));
				
				Position mouse = null;
				if(canvas.getMousePosition() != null)
					mouse = new Position(canvas.getMousePosition().getX(), canvas.getMousePosition().getY());
				if(mouse != null && Math.abs(Math.sqrt(mouse.distanceSquared(center)) - radius) < 10)
					g.setStroke(new BasicStroke(10));
				else
					g.setStroke(new BasicStroke(5));
				
				if(mouseDown && mouse != null)
					radius = Math.sqrt(mouse.distanceSquared(center));
				
				g.drawOval((int) (center.getX() - radius), (int) (center.getY() - radius),
						(int)(2*radius), (int)(2*radius));
			}
			
			//flip the buffers
			g.dispose();
			strategy.show();
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {}
		}
		frame.dispose();
		
	}

	@Override
	public void setData(Configuration cd) {
		String type = cd.getString(ParserKeys.shapetype);

		if(type.equalsIgnoreCase("Circle"))
		{
			shapeType.setSelectedURI("Circle");
			radius = cd.getNumber(ParserKeys.radius);
			try {
				scale.setText(cd.getNumber(ParserKeys.scale).toString());
				radius /= (cd.getNumber(ParserKeys.scale)/100);
			} catch(NoSuchKeyException e) {
				scale.setText("100");
			}
		}
		else
			throw new IllegalArgumentException("Invalid shapetype specified");
	}

	@Override
	public void forceSetData(ConfigData cd) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Configuration instantiateNewConfiguration() {
		shapeType.setSelectedURI("");
		scale.setText("100");
		if(images != null && images.length > 0)
			radius = images[0].getWidth()/4;
		else
			radius = 50;
	}

	@Override
	public ConfigType getData(Configuration toSet) {
		String type = shapeType.getSelectedURI();
		ConfigData cd = new ConfigData();
		if(type.equalsIgnoreCase("Circle"))
		{
			cd.set(ParserKeys.shapetype, "Circle");
			cd.set(ParserKeys.radius, radius*Double.valueOf(scale.getText())/100.0);
			cd.set(ParserKeys.x, (Double)0.0);
			cd.set(ParserKeys.y, (Double)0.0);
			cd.set(ParserKeys.scale, scale.getText());
		}
		
		return cd;
	}

	@Override
	public boolean isValidConfig() {
		if(shapeType.getSelectedURI().equals(""))
			return false;
			
		Scanner s = new Scanner(scale.getText());
		if(!s.hasNextDouble())
			return false;
		return true;
	}

	@Override
	public List<ConfigType> getAllLoadableTypes() {
		throw new UnsupportedOperationException();
	}

	@Override
	public JPanel getPanel() {
		return this;
	}
	
	private class ShapeTypeSelector implements SelectorOptions {

		@Override
		public String[] getOptions() {
			return new String[]{"Circle"};
		}

		@Override
		public void uriSelected(String uri) {}
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {
		Position mouse = null;
		if(canvas.getMousePosition() != null && center != null)
			mouse = new Position(canvas.getMousePosition().getX(), canvas.getMousePosition().getY());
		if(mouse != null && Math.abs(Math.sqrt(mouse.distanceSquared(center)) - radius) < 10)
			mouseDown = true;		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		mouseDown = false;		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(cancel))
			running = false;
		else if(e.getSource().equals(done))
		{
			if(this.isValidConfig()) 
			{
				ConfigData cd = this.getData(null);
				mie.setBody(cd);
				running = false;
			}
			else
				JOptionPane.showMessageDialog(frame,
					    "The body is invalid.",
					    "Error",
					    JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {}

	@Override
	public void windowClosing(WindowEvent arg0) {
		running = false;
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {}

	@Override
	public void windowDeiconified(WindowEvent arg0) {}

	@Override
	public void windowIconified(WindowEvent arg0) {}

	@Override
	public void windowOpened(WindowEvent arg0) {}

}
