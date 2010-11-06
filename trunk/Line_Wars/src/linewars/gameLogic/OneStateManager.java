package linewars.gameLogic;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

import linewars.configfilehandler.ConfigFileReader.InvalidConfigFileException;
import linewars.gamestate.GameState;
import linewars.network.messages.Message;

/**
 * FOR DEBUGGING PURPOSES ONLY
 * FOR DEBUGGING PURPOSES ONLY
 * FOR DEBUGGING PURPOSES ONLY
 * FOR DEBUGGING PURPOSES ONLY
 * FOR DEBUGGING PURPOSES ONLY
 * FOR DEBUGGING PURPOSES ONLY
 * FOR DEBUGGING PURPOSES ONLY
 * FOR DEBUGGING PURPOSES ONLY
 * FOR DEBUGGING PURPOSES ONLY
 * FOR DEBUGGING PURPOSES ONLY
 * FOR DEBUGGING PURPOSES ONLY
 * FOR DEBUGGING PURPOSES ONLY
 * FOR DEBUGGING PURPOSES ONLY
 * FOR DEBUGGING PURPOSES ONLY
 * FOR DEBUGGING PURPOSES ONLY
 * FOR DEBUGGING PURPOSES ONLY
 * FOR DEBUGGING PURPOSES ONLY
 * FOR DEBUGGING PURPOSES ONLY
 * FOR DEBUGGING PURPOSES ONLY
 * FOR DEBUGGING PURPOSES ONLY
 * FOR DEBUGGING PURPOSES ONLY
 * FOR DEBUGGING PURPOSES ONLY
 * FOR DEBUGGING PURPOSES ONLY
 * FOR DEBUGGING PURPOSES ONLY
 * FOR DEBUGGING PURPOSES ONLY
 * FOR DEBUGGING PURPOSES ONLY
 * FOR DEBUGGING PURPOSES ONLY
 * @author Knexer
 *
 */
public class OneStateManager implements GameStateProvider, GameStateUpdater {
	
	private GameState singleState;
	
	public OneStateManager(String mapURI, int numPlayers, List<String> raceURIs, List<String> players) throws FileNotFoundException, InvalidConfigFileException {
		singleState = new GameState(mapURI, numPlayers, raceURIs, players);
	}

	@Override
	public void addOrdersForTick(int tickID, Message[] orders) {
		singleState.update(orders);
	}

	@Override
	public GameState getCurrentGameState() {
		return singleState;
	}

	@Override
	public void lockViewableGameState() {
	}

	@Override
	public void unlockViewableGameState() {
	}

}
