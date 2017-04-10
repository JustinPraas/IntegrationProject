package connection;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.Arrays;

import packet.Packet;
import packet.Payload;
import packet.Pulse;

public class TransportLayer {

	public static void handlePacket(DatagramPacket datagramPacket) {
		byte[] datagramContents = datagramPacket.getData();
		
		int sequenceNumber = getSequenceNumber(datagramContents);
		int typeIdentifier = getTypeIdentifier(datagramContents);
		Payload payload = getPayload(datagramContents, typeIdentifier);
		
		switch (typeIdentifier) {
		case 0: 
			handlePulse(datagramContents)
		}
		
		
	}

	private static Payload getPayload(byte[] datagramContents, int typeIdentifier) {
		byte[] payloadData = new byte[datagramContents.length - 3];
		payloadData = Arrays.copyOfRange(datagramContents, 3, datagramContents.length);
		
		// Get the senderID from the payloadData
		int senderID = getSenderID(payloadData);
		
		switch (typeIdentifier) {
		case 0: 
			String name = getName(payloadData);
			return new Pulse(senderID, name);
		case 1:
			int receiverID = getReceiverID(payloadData);
			int messageID = getMessageID(payloadData);
		}
		return null;
	}

	private static int getMessageID(byte[] payloadData) {
		byte[] messageIdArray = new byte[2];
		messageIdArray = Arrays.copyOfRange(payloadData, 8, 10);
		ByteBuffer messageIdByteBuffer = ByteBuffer.wrap(messageIdArray);
		
		int messageID = messageIdByteBuffer.getInt();
		return messageID;
	}

	private static int getReceiverID(byte[] payloadData) {
		byte[] receiverIdArray = new byte[4];
		receiverIdArray = Arrays.copyOfRange(payloadData, 4, 8);
		ByteBuffer receiverIdByteBuffer = ByteBuffer.wrap(receiverIdArray);
		
		int receiverID = receiverIdByteBuffer.getInt();
		return receiverID;
	}

	private static int getSenderID(byte[] payloadData) {
		byte[] senderIdArray = new byte[4];
		senderIdArray = Arrays.copyOfRange(payloadData, 0, 4);
		ByteBuffer senderIdByteBuffer = ByteBuffer.wrap(senderIdArray);
		
		int senderID = senderIdByteBuffer.getInt();
		return senderID;
	}

	private static String getName(byte[] pulsePayload) {
		byte[] nameArray = new byte[pulsePayload.length - 4];
		nameArray = Arrays.copyOfRange(pulsePayload, 4, pulsePayload.length);
		
		String name = "";
		try {
			name = new String(nameArray, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return name;
	}
	
	public static void main(String[] args) {
		Pulse p = new Pulse(2, "Justin");
		Packet packet = new Packet (12, 0, p);
		
		Payload payload = getPayload(packet.getDatagramPacket().getData(), 0);
		
		System.out.println(Arrays.toString(p.getPayload()));
		System.out.println(Arrays.toString(packet.getDatagramPacket().getData()));
		System.out.println(Arrays.toString(payload.getPayload()));
	}

	private static int getSequenceNumber(byte[] datagramContents) {
		return datagramContents[0] * 256 + datagramContents[1];
	}
	
	private static int getTypeIdentifier(byte[] datagramContents) {
		return datagramContents[3];
	}

}
