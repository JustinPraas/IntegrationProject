package packet;

import java.net.DatagramPacket;
import java.util.ArrayList;

import connection.Connection;

/**
 * A class that stores properties and a <code>Payload</code> of a <code>Packet</code>.
 * @author Justin Praas, Daan Kooij, Casper Plentinger, Tim van Brederode
 */
public class Packet {
	
	/**
	 * The total length (bytes) of the packet header.
	 */
	public static final int HEADER_LENGTH = 11;
	
	/**
	 * The length (bytes) of the senderID field in the packet header.
	 */
	public static final int SENDER_LENGTH = 4;
	
	/**
	 * The length (bytes) of the receiverID field in the packet header.
	 */
	public static final int RECEIVER_LENGTH = 4;
	
	/**
	 * The length (bytes) of the sequenceNumber field in the packet header.
	 */
	public static final int SEQUENCE_NUM_LENGTH = 2;
	
	/**
	 * The length (bytes) of the typeIdentifier field in the packet header.
	 */
	public static final int TYPE_LENGTH = 1;

	/**
	 * The ID of the sender of the <code>Packet</code>.
	 */
	private int senderID;
	
	/**
	 * The ID of the receiver (destination) of the <code>Packet</code>.
	 */
	private int receiverID;
	
	/**
	 * The sequence number of the <code>Packet</code>.
	 */
	private int sequenceNumber;
	
	/**
	 * The type identifier of the <code>Payload</code> that comes with this packet.
	 */
	private byte typeIdentifier;
	
	/**
	 * The <code>Payload</code> that comes with this packet.
	 */
	private Payload payload;

	/**
	 * Constructs a <code>Packet</code> object that holds all relevant fields and 
	 * a <code>Payload</code>.
	 * @param senderID the ID of the sender of this packet
	 * @param receiverID the ID of the receiver of this packet
	 * @param sequenceNumber the sequence number of this packet
	 * @param typeIdentifier the type identifier of this packet
	 * @param payload the <code>Payload</code> that comes with this packet.
	 */
	public Packet(int senderID, int receiverID, int sequenceNumber, int typeIdentifier, Payload payload) {
		this.senderID = senderID;
		this.receiverID = receiverID;
		this.sequenceNumber = sequenceNumber;
		this.typeIdentifier = (byte) typeIdentifier;
		this.payload = payload;
	}	
	
	
	/**
	 * Returns a <code>DatagramPacket</code> object derived from this <code>Packet</code>.
	 * @return
	 */
	public DatagramPacket getDatagramPacket() {
		//byte[] packet = new byte[HEADER_LENGTH + payload.getPayload().length];
		ArrayList<Byte> packetList = new ArrayList<>();
		
		// SenderID to binary
		for (int i = (SENDER_LENGTH - 1) * 8; i >= 0; i -= 8) {
			packetList.add((byte) (senderID >> i));
		}
		
		// ReceiverID to binary
		for (int i = (RECEIVER_LENGTH - 1) * 8; i >= 0; i -= 8) {
			packetList.add((byte) (receiverID >> i));
		}
		
		// SequenceNumber to binary
		for (int i = (SEQUENCE_NUM_LENGTH - 1) * 8; i >= 0; i -= 8) {
			packetList.add((byte) (sequenceNumber >> i));
		}
		
		
		// TypeIdentifier to binary
		for (int i = (TYPE_LENGTH - 1) * 8; i >= 0; i -= 8) {
			packetList.add((byte) (typeIdentifier >> i));
		}
		
		// Add the payload data to the packetList
		for (byte b: payload.getPayloadData()) {
			packetList.add(b);
		}
		
		// Add all the contents of the packetList to packetArray
		byte[] packetArray = new byte[packetList.size()];		
		for (int i = 0; i < packetList.size(); i++) {
			packetArray[i] = packetList.get(i);
		}
		
		return new DatagramPacket(packetArray, packetArray.length, Connection.group, Connection.port);		
	}
	
	/**
	 * Returns the data byte array of the the <code>DatagramPacket</code> that derives from this 
	 * <code>Packet</code>.
	 * @return
	 */
	public byte[] getDatagramPacketData() {
		return getDatagramPacket().getData();
	}

	public int getSenderID() {
		return senderID;
	}

	public int getReceiverID() {
		return receiverID;
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public int getTypeIdentifier() {
		return typeIdentifier;
	}

	public Payload getPayload() {
		return payload;
	}

	public void setSequenceNum(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;		
	}
}
