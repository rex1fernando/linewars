import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;



public class Window implements ActionListener, WindowListener {

	private JFrame frame;
	private Canvas canvas;
	private BufferStrategy strategy;
	private JButton openFiles;
	private JPanel scrollPanel;
	
	private ArrayList<Frame> list = new ArrayList<Frame>();
	private ArrayList<Frame> newList = null;
	
	private String lastUsedPath = null;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
			
		new Window().start();
	}
	
	public void start() 
	{
		
		//load the last file path from file
		try {
			Scanner f = new Scanner(new File("lastFilePath.txt"));
			lastUsedPath = f.nextLine();
			f.close();
		} catch (FileNotFoundException e1) {
			lastUsedPath = null;
		}
		
		//set up the Canvas
		canvas = new Canvas();
		frame = new JFrame("VN Game");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(this);
		
		openFiles = new JButton("Open Files");
		openFiles.addActionListener(this);
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(openFiles, BorderLayout.NORTH);
		scrollPanel = new JPanel();
		JScrollPane scrollPane = new JScrollPane(scrollPanel);
		panel.add(scrollPane, BorderLayout.CENTER);
		
		JSplitPane pane = new JSplitPane();
		pane.setRightComponent(panel);
		pane.setLeftComponent(canvas);
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
		
		
		boolean b = true;
		int i = 0;
		long lastDrawTime = System.currentTimeMillis();
		while(b)
		{
			
			if(list.isEmpty())
			{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				//get graphics object to draw to
				Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
				g.setColor(Color.black);
				g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
				list.get(i).getFrame().draw(g, 0, 0, canvas.getWidth(), canvas.getWidth());
				//flip the buffers
				g.dispose();
				strategy.show();
				
				if(System.currentTimeMillis() - lastDrawTime > list.get(i).getTime())
				{
					lastDrawTime = System.currentTimeMillis();
					i++;
					if(i >= list.size())
						i = 0;
				}
			}
				
			if(newList != null)
			{
				list = newList;
				newList = null;
			}
			
			
			
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource().equals(openFiles))
		{
			JFileChooser fc;
			if(lastUsedPath == null)
				fc = new JFileChooser();
			else
				fc = new JFileChooser(lastUsedPath);
			fc.setMultiSelectionEnabled(true);
			int returnVal = fc.showOpenDialog(frame);

	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File[] file = fc.getSelectedFiles();
	            lastUsedPath = file[0].getParent();
	            scrollPanel.removeAll();
	            scrollPanel.setLayout(new BoxLayout(scrollPanel, BoxLayout.Y_AXIS));
	            ArrayList<Frame> newList = new ArrayList<Frame>();
	            for(File f : file)
	            {
	            	newList.add(new Frame(f.getAbsolutePath()));
	            	scrollPanel.add(newList.get(newList.size() - 1));
	            }
	            
	            this.newList = newList;
	            
	            frame.pack();
	        }
		}
	}

	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {}

	@Override
	public void windowClosing(WindowEvent arg0) {
		try {
			FileWriter fw = new FileWriter("lastFilePath.txt");
			fw.write(lastUsedPath);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			
		}
		
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
