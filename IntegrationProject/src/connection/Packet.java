package connection;

import chat.Message;

/**
 * Data packet consisting of source, destination and data
 * @author Tim
 * @version 10-4-2017
 *
 */
public class Packet {
	
	private int sourceAddress;
	private int destinationAddress;
	private boolean encrypted;
	private Message msg;
	
	/**
	 * Instantiates a new Packet which includes a Message
	 * @param sourceAddress int
	 * @param destinationAddress int
	 * @param encrypted boolean that defines if the message is encrypted or not
	 * @param msg a Message object
	 */
	public Packet(int sourceAddress, int destinationAddress, boolean encrypted, Message msg) {
		this.sourceAddress = sourceAddress;
		this.destinationAddress = destinationAddress;
		this.msg = msg;
	}

}