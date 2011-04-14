package menu;

import java.util.List;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import linewars.gamestate.MapConfiguration;
import linewars.init.Game;
import linewars.init.PlayerData;

public class GameInitializer extends SwingWorker<Game, Object>
{
	private MapConfiguration map;
	private String serverIp;
	private List<PlayerData> playerList;
	private List<String> clientList;
	private int playerId;
	private boolean isObserver;
	
	private LoadingProgress progress;
	private JProgressBar progressBar;
	private WindowManager wm;
	
	public GameInitializer() {
		progress = new LoadingProgressImp();
	}
	
	public void setMap(MapConfiguration map){
		this.map = map;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public void setPlayerList(List<PlayerData> playerList) {
		this.playerList = playerList;
	}

	public void setClientList(List<String> clientList) {
		this.clientList = clientList;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public void setObserver(boolean isObserver) {
		this.isObserver = isObserver;
	}
	
	public void setProgressBar(JProgressBar progressBar) {
		this.progressBar = progressBar;
	}
	
	public void setWindowManager(WindowManager wm) {
		this.wm = wm;
	}
	
	public LoadingProgress getLoadingProgress() {
		return progress;
	}
	
	@Override
	protected Game doInBackground()
	{
		Game g = new Game(map, playerList, progress, wm);
		if (playerId == 0) 
			g.initializeServer(clientList);
		g.initializeClient(serverIp, playerId, isObserver);
		return g;
	}
	
	public static interface LoadingProgress
	{
		public void setMaxValue(int max);
		public void updateValue(int newValue);
	}
	
	private class LoadingProgressImp implements LoadingProgress
	{
		@Override
		public void setMaxValue(final int max)
		{
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					progressBar.setMaximum(max);
				}
			});
		}

		@Override
		public void updateValue(final int newValue)
		{
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					progressBar.setValue(newValue);
				}
			});
		}
	}
}
