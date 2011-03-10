package menu.networking;

import java.io.Serializable;

public enum MessageType implements Serializable
{
	name, slot, race, color, 
	
	chat, clientCancelGame, isReplay, selection, serverCancelGame, playerJoin,
	
	startGame,
}
