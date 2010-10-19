package linewars.network;

/**
 * Like Client, except it’s a server.
 * 
 * Polls (Lazily ask) the Gatekeeper for Messages for the current tick x from the set of players from whom the server
 * hasn’t heard this tick.
 * 		- If the Gatekeeper doesn’t get all of the Messages for the current tick within a certain time window, the
 * 		  server starts urgently polling
 * 
 * Once all of the Messages associated with some tick id have been received, send them all out to each Client.
 * 
 * 
 * @author Titus Klinge
 */
public class Server
{

}
