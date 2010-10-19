package linewars.network;

/**
 * Abstracts actual interaction with the network protocol, including ensuring
 * that all information is received properly (dropped packets)
 * 
 * Sends Messages over the network to a specified address
 * 
 * Converts incoming network information into Messages
 * 
 * Can be polled for Messages from a specific address for a specific tick id
 * 		- Urgent polling is urgent; this could trigger resend requests, if applicable
 * 		- Normal polling is not urgent, never triggers a resend request
 * 
 * @author Titus Klinge
 * 
 */
public class Gatekeeper
{

}
