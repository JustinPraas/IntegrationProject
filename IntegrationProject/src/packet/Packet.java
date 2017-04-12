package packet;

import java.net.DatagramPacket;
import java.util.ArrayList;

import connection.Connection;

public class Packet {
	
	// Header length in bytes
	public static final int HEADER_LENGTH = 11;
	public static final int SENDER_LENGTH = 4;
	public static final int RECEIVER_LENGTH = 4;
	public static final int SEQUENCE_NUM_LENGTH = 2;
	public static final int TYPE_LENGTH = 1;

	private int senderID;
	private int receiverID;
	private int sequenceNumber;
	private byte typeIdentifier;
	private Payload payload;

	public Packet(int senderID, int receiverID, int sequenceNumber, int typeIdentifier, Payload payload) {
		this.senderID = senderID;
		this.receiverID = receiverID;
		this.sequenceNumber = sequenceNumber;
		this.typeIdentifier = (byte) typeIdentifier;
		this.payload = payload;
	}	
	
	public DatagramPacket getDatagramPacket() {
		//byte[] packet = new byte[HEADER_LENGTH + payload.getPayload().length];
		ArrayList<Byte> packetList = new ArrayList<>();
		
		for (int i = (SENDER_LENGTH - 1) * 8; i >= 0; i -= 8) {
			packetList.add((byte) (senderID >> i));
		}
		
		for (int i = (RECEIVER_LENGTH - 1) * 8; i >= 0; i -= 8) {
			packetList.add((byte) (receiverID >> i));
		}
		
		for (int i = (SEQUENCE_NUM_LENGTH - 1) * 8; i >= 0; i -= 8) {
			packetList.add((byte) (sequenceNumber >> i));
		}
		
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
}
