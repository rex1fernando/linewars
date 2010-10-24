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



public class Window implements ActionListener {

	private JFrame frame;
	private Canvas canvas;
	private BufferStrategy strategy;
	private JButton addFiles;
	private JButton clearFiles;
	private JPanel scrollPanel;
	
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
		
		addFiles = new JButton("Add Files");
		addFiles.addActionListener(this);
		clearFiles = new JButton("Clear Files");
		clearFiles.addActionListener(this);
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(addFiles, BorderLayout.NORTH);
		panel.add(clearFiles, BorderLayout.SOUTH);
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
		
		scrollPanel.removeAll();
        scrollPanel.setLayout(new BoxLayout(scrollPanel, BoxLayout.Y_AXIS));
		
		
		boolean b = true;
		int current = 0;
		long lastChangeTime = System.currentTimeMillis();
		while(b)
		{
			
			if(!list.isEmpty())
			{
				//get graphics object to draw to
				Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
				g.setColor(Color.black);
				g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
				list.get(current).getFrame().draw(g, 0, 0, canvas.getWidth(), canvas.getWidth());
				//flip the buffers
				g.dispose();
				strategy.show();
				
				if(System.currentTimeMillis() - lastChangeTime > list.get(current).getTime())
				{
					current = (current + 1)%list.size();
					lastChangeTime = System.currentTimeMillis();
				}
			}
			
			if(list.isEmpty())
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
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
		if(arg0.getSource().equals(clearFiles))
		{
			scrollPanel.removeAll();
            scrollPanel.setLayout(new BoxLayout(scrollPanel, BoxLayout.Y_AXIS));
            newList = new ArrayList<Frame>();
		}
	}

}
