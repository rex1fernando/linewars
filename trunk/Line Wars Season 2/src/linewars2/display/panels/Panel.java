package linewars2.display.panels;

import java.awt.Graphics;

import javax.swing.JPanel;

import linewars2.gamelogic.GameStateProvider;

public abstract class Panel extends JPanel {
	
	public Panel(GameStateProvider provider, int width, int height, Parser ... animations){
		//TODO
	}
	
	public void updateLocation(){
		//TODO
	}
	
	@Override
	public void paint(Graphics g){
		
	}
}
