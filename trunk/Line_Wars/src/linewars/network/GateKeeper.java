package linewars.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import linewars.network.messages.Message;

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
 */
public class GateKeeper
{
	private static final int MAX_MTU = 1500;
	private static final int TARGET_MTU = 1000;
	private static final long SLEEP_TIME = 10;
	private static final int SO_TIMEOUT = 1;
	
	private DatagramSocket socket;
	private int port;
	
	private HashMap<Integer, Message[]> messages;
	private HashMap<Integer, List<MessagePacket>> incompletePackets;
	
	private MessageListener msgListener;
	
	public GateKeeper(int port) throws SocketException
	{
		messages = new HashMap<Integer, Message[]>();
		incompletePackets = new HashMap<Integer, List<MessagePacket>>();
		
		socket = new DatagramSocket(port);
		socket.setSoTimeout(SO_TIMEOUT);
		
		msgListener = new MessageListener(SLEEP_TIME);
	}
	
	/**
	 * Steals the current thread by using it to listen for messages.
	 */
	public void startListening()
	{
		msgListener.start();
	}

//	public void sendMessage(String address, Message msg) throws IOException
//	{
//		MessagePacket[] packets = MessageConstructor.createMessagePackets(msg);
//		for (MessagePacket p : packets)
//		{
//			byte[] packetData = Serializer.serialize(p);
//			socket.send(new DatagramPacket(packetData, packetData.length, InetAddress.getByName(address), port));
//		}
//	}
	
	public Message[] urgentlyPollMessagesForTick(int tickID)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public void pushMessagesForTick(Message[] msgs, int tickID)
	{
		// TODO Auto-generated method stub
	}
	
	/**
	 * TODO document
	 */
	private class MessageListener
	{
		private long delay;
		private boolean isListening;
		
		public MessageListener(long delay)
		{
			this.delay = delay;
		}
		
		public void start()
		{
			while (isListening)
			{
				MessagePacket msgPacket = receivePacket();
				
				// if the packet was able to be deserialized ...
				if (msgPacket != null)
				{
					// if it is the first packet received for the time step ...
					if (incompletePackets.get(msgPacket.timeStep) == null)
					{
						// if there is only one packet for this message ...
						if (msgPacket.numPackets == 1)
						{
							// create the message
							try {
								messages.put(msgPacket.timeStep, MessageConstructor.constructMessage(new MessagePacket[]{msgPacket}));
							} catch (InvalidMessageException e) {
								e.printStackTrace();
							}
						}
						// if there are multiple packets, create a new list of packets
						else
						{
							List<MessagePacket> packetList = new LinkedList<MessagePacket>();
							packetList.add(msgPacket);
							incompletePackets.put(msgPacket.timeStep, packetList);
						}
					}
					// if it is not the first packet received ...
					else
					{
						// add it to the list of packets for that id
						List<MessagePacket> packetList = incompletePackets.get(msgPacket.timeStep);
						packetList.add(msgPacket);
						
						// if all packets for a message are found ...
						if (packetList.size() == msgPacket.numPackets)
						{
							// create the message
							try {
								messages.put(msgPacket.timeStep, MessageConstructor.constructMessage(packetList.toArray(new MessagePacket[packetList.size()])));
							} catch (InvalidMessageException e) {
								e.printStackTrace();
							}
							
							// remove it from the incomplete packet map
							incompletePackets.remove(msgPacket.timeStep);
						}
					}
				}
				
				// sleep
				try { Thread.sleep(delay); }
				catch (InterruptedException e) { e.printStackTrace(); }
			}
		}
		
		/**
		 * If a packet has arrived, it returns the deSerialized MessagePacket. If it
		 * hasn't arrived, or a MessagePacket is unable to be constructed from the
		 * packet, this method returns null.
		 * 
		 * @return A received MessagePacket or null if no such packet exists.
		 */
		private MessagePacket receivePacket()
		{
			DatagramPacket packet = new DatagramPacket(new byte[MAX_MTU], MAX_MTU);
			
			boolean receivedData = true;
			try {
				socket.receive(packet);
			} catch (IOException e) {
				receivedData = false;
			}
			
			if (receivedData)
			{
				// deserializes the packet data
				try {
					return (MessagePacket) Serializer.deSerialize(packet.getData());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			return null;
		}
	}
	
	/**
	 * Encapsulates all information that is sent in a packet through
	 * the UDP socket.  It includes header information and a subset
	 * of the bytes that compose a message.
	 */
	private static class MessagePacket implements Serializable
	{
		private static final long serialVersionUID = -4623292576498196472L;
		
		private int timeStep;
		private int numPackets;
		private int packetNumber;
		private int playerID;
		private byte[] data;
		
		public MessagePacket(int playerID, int timeStep, int packetNumber, int numPackets, byte[] data)
		{
			this.timeStep = timeStep;
			this.numPackets = numPackets;
			this.packetNumber = packetNumber;
			this.playerID = playerID;
			this.data = data;
		}
	}
	
	/**
	 * TODO document
	 */
	private static class MessageConstructor
	{
		/**
		 * Reconstructs the original messages from the given packets.
		 * 
		 * @param packets
		 *            The packets that represent the Message to be returned.
		 * @return The messages that the packets compose.
		 * @throws InvalidMessageException If the message could not be constructed.
		 */
		public static Message[] constructMessage(MessagePacket[] packets) throws InvalidMessageException
		{
			// orders the packets and calculates the total size
			int dataSize = 0;
			int[] ordering = new int[packets.length];
			for (int i = 0; i < packets.length; ++i)
			{
				ordering[packets[i].packetNumber] = i;
				dataSize += packets[i].data.length;
			}
			
			// combine all the byte segments into one array
			byte[] msgData = new byte[dataSize];
			int curPos = 0;
			for (int i = 0; i < packets.length; ++i)
			{
				byte[] curData = packets[ordering[i]].data;
				for (int j = 0; j < curData.length; ++j, ++curPos)
				{
					msgData[curPos] = curData[j];
				}
			}
			
			// return the deserialized object
			try {
				return (Message[]) Serializer.deSerialize(msgData);
			} catch (Exception e) {
				throw new InvalidMessageException(e);
			}
		}
		
		/**
		 * Splits the message into packet objects that can be serialized and sent
		 * across the network.
		 * 
		 * @param msg
		 *            The message to be split into packets.
		 * @return A list of packets that together compose the message object.
		 * @throws IOException
		 *             If there was a problem serializing the message object into a
		 *             byte array.
		 */
		public static MessagePacket[] createMessagePackets(Message[] msg) throws IOException
		{
			if (msg == null || msg.length == 0)
				return new MessagePacket[0];
			
			// serializes the message
			byte[] msgBytes = Serializer.serialize(msg);
			
			// calculates the number of packets required to send the message
			int numPackets = (int) Math.ceil((1.0 * msgBytes.length) / TARGET_MTU);
			MessagePacket[] msgPackets = new MessagePacket[numPackets];
			
			// creates the packets
			int curPos = 0;
			for (int i = 0; i < numPackets; ++i)
			{
				int packetSize = (i == numPackets-1) ? msgBytes.length % TARGET_MTU : TARGET_MTU;
				
				// grabs a segment of the message bytes to store in the current packet
				byte[] packetBytes = new byte[packetSize];
				for (int j = 0; j < packetSize; ++j, ++curPos)
				{
					packetBytes[j] = msgBytes[curPos];
				}
				msgPackets[i] = new MessagePacket(msg[0].getPlayerId(), msg[0].getTimeStep(), i, numPackets, packetBytes);
			}
			return msgPackets;
		}
	}
	
	/**
	 * TODO document
	 */
	private static class Serializer
	{
		/**
		 * Deserializes an array of bytes into an object.
		 * 
		 * @param b
		 *            The serialized byte array to be used in instantiating the
		 *            object.
		 * @return The instantiated object representation of the bytes provided.
		 * @throws IOException
		 *             If something bad happens during the serialization process.
		 * @throws ClassNotFoundException
		 *             If the class being instaniated isn't recognized.
		 */
		private static Object deSerialize(byte[] b) throws IOException, ClassNotFoundException
		{
			ObjectInputStream os = new ObjectInputStream(new ByteArrayInputStream(b));
			Object o = os.readObject();
			os.close();
			return o;
		}
		
		/**
		 * Serializes a serializable object into an array of bytes.
		 * 
		 * @param s
		 *            The serializable object to be serialized.
		 * @return An array of bytes representing the object provided.
		 */
		private static byte[] serialize(Object s)
		{
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				ObjectOutputStream os = new ObjectOutputStream(out);
				os.writeObject(s);
				os.close();
			return out.toByteArray();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
	
	/**
	 * TODO document
	 */
	private static class InvalidMessageException extends Exception
	{
		private static final long serialVersionUID = -7580077906255482160L;

		public InvalidMessageException(Throwable e)
		{
			super(e);
		}
	}
}
