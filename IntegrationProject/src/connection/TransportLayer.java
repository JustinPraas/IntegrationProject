package connection;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.Arrays;

import application.Session;
import model.Message;
import model.Person;
import packet.*;

public class TransportLayer {
	
	// Constants
	public static final int PULSE_TTL = 5;

	// Used objects
	private Session session;

	/**
	 * Creates a <code>TransportLayer</code> object that acts on a sessions.
	 * @param session the session to act upon
	 */
	public TransportLayer(Session session) {
		this.session = session;
	}
	
	public static void main(String[] args) {
		Pulse p = new Pulse("Justin");
		Packet packet = new Packet (1, 2, PayloadType.PULSE.getType(), 0, p);
		
		Payload payload = getPayload(packet.getDatagramPacket().getData(), 0);
		
		System.out.println(Arrays.toString(p.getPayloadData()));
		System.out.println(Arrays.toString(packet.getDatagramPacket().getData()));
		System.out.println(Arrays.toString(payload.getPayloadData()));
	}
	
	/**
	 * Processes a received packet. If the packet has been here before, don't 
	 * process the packet, otherwise pass it on to the corresponding payload
	 * handlers according to the packet's type identifier.
	 * @param datagramPacket the received packet
	 */
	public void handlePacket(DatagramPacket datagramPacket) {
		byte[] datagramContents = datagramPacket.getData();
		
		int senderID = getSenderID(datagramContents);
		int receiverID = getReceiverID(datagramContents);
		int sequenceNumber = getSequenceNumber(datagramContents);
		int typeIdentifier = getTypeIdentifier(datagramContents);		
		Payload payload = getPayload(datagramContents, typeIdentifier);
		
		Packet receivedPacket = new Packet(senderID, receiverID, sequenceNumber, typeIdentifier, payload);
		
		// TODO: First check if we've seen this packet before, otherwise process the packet
		if (receivedPacket.getReceiverID() != session.getID()) {
			forwardPacket(datagramContents);
		} else {
			switch (receivedPacket.getTypeIdentifier()) { 
			case PayloadType.PULSE.getType():
				handlePulse(receivedPacket);
				break;
			case PayloadType.ENCRYPTED_MESSAGE.getType():
				handleEncryptedMessage(receivedPacket);
				break;
			case PayloadType.ACKNOWLEDGEMENT.getType():
				handleAcknowledgement(receivedPacket);
				break;
			case PayloadType.ENCRYPTION_PAIR.getType():
				// TODO: handleEncryptionPair(receivedPacket);
				break;
			default: 
				System.err.println("Nothing to do here.");
			}	
		}			
	}
	
	/**
	 * Processes a received <code>Packet</code> object.
	 * @param packet the packet that has been received
	 */
	public void handlePulse(Packet packet) {
		Pulse payload = (Pulse) packet.getPayload();
		Person person = new Person(payload.getName(), packet.getSenderID());	
		if (!session.getKnownPersons().con) {
			person.setTimeToLive(PULSE_TTL);
			session.getKnownPersons().add(person);
		}		
	}	

	/**
	 * Processes a received <code>EncryptedMessage</code> packet.
	 * @param payload the payload that has been received
	 */
	public void handleEncryptedMessage(EncryptedMessage payload) {
		// TODO: implement encryption
		
		int messageID = payload.getMessageID();
		int senderID = payload.getSenderID();
		String encryptedText = payload.getEncryptedMessage();
		Message message = new Message(); // TODO: Further implement
		
		
	}

	/**
	 * Converts the datagram-packet contents into a <code>Payload</code> object, according to the
	 * type identifier.
	 * @param datagramContents the packet contents
	 * @param typeIdentifier the type of the payload
	 * @return a <code>Payload</code> object, converted from the packet contents
	 */
	public static Payload getPayload(byte[] datagramContents, int typeIdentifier) {
		byte[] payloadData = Arrays.copyOfRange(datagramContents, 3, datagramContents.length);
		
		// Get the senderID from the payloadData
		int senderID = getSenderID(payloadData);
		int receiverID, messageID;
		
		
		switch (typeIdentifier) {
		case 0: 
			String name = getName(payloadData);
			return new Pulse(senderID, name);
		case 1:
			receiverID = getReceiverID(payloadData);
			messageID = getMessageID(payloadData);
			String message = getMessage(payloadData);
			return new EncryptedMessage(senderID, receiverID, messageID, message);
		case 2:
			receiverID = getReceiverID(payloadData);
			messageID = getMessageID(payloadData);
			return new Acknowledgement(senderID, receiverID, messageID);
		case 3:
			// TODO implement encryption pair exchange
			break;
		default:
			System.err.println("Nothing to do here.");	
		}
		
		return null;
	}

	/**
	 * Returns the senderID of the source.
	 * @param datagramContents the contents of the packet
	 * @return
	 */
	public static int getSenderID(byte[] datagramContents) {
		int start = 0;
		int end = Packet.SENDER_LENGTH;
		
		byte[] senderIdArray = new byte[Packet.SENDER_LENGTH];
		senderIdArray = Arrays.copyOfRange(datagramContents, start, end);
		ByteBuffer senderIdByteBuffer = ByteBuffer.wrap(senderIdArray);
		
		int senderID = senderIdByteBuffer.getInt();
		return senderID;
	}

	/**
	 * Returns the receiverID of the destination node.
	 * @param datagramContents the contents of the packet
	 * @return receiverID the receiverID of the destination node
	 */
	public static int getReceiverID(byte[] datagramContents) {
		int start = Packet.RECEIVER_LENGTH;
		int end = Packet.SENDER_LENGTH + Packet.RECEIVER_LENGTH;
		
		byte[] receiverIdArray = new byte[Packet.RECEIVER_LENGTH];
		receiverIdArray = Arrays.copyOfRange(datagramContents, start, end);
		ByteBuffer receiverIdByteBuffer = ByteBuffer.wrap(receiverIdArray);
		
		int receiverID = receiverIdByteBuffer.getInt();
		return receiverID;
	}

	/**
	 * Returns the sequence number of the packet.
	 * @param datagramContents the packet contents
	 * @return the sequence number of the packet
	 */
	public static int getSequenceNumber(byte[] datagramContents) {
		int start = Packet.SENDER_LENGTH + Packet.RECEIVER_LENGTH;
		int end = Packet.SENDER_LENGTH + Packet.RECEIVER_LENGTH + Packet.SEQUENCE_NUM_LENGTH;
		
		byte[] seqNumArray = new byte[Packet.SEQUENCE_NUM_LENGTH];
		seqNumArray = Arrays.copyOfRange(datagramContents, start, end);
		ByteBuffer seqNumByteBuffer = ByteBuffer.wrap(seqNumArray);
		
		int seqNum = seqNumByteBuffer.getInt();
		return seqNum;
	}
	
	/**
	 * Returns the type identifier of the packet, representing the payload type.
	 * @param datagramContents the packet contents
	 * @return the type identifier of the packet
	 */
	public static int getTypeIdentifier(byte[] datagramContents) {
		int start = Packet.SENDER_LENGTH + Packet.RECEIVER_LENGTH + Packet.SEQUENCE_NUM_LENGTH;
		int end = Packet.SENDER_LENGTH + Packet.RECEIVER_LENGTH + Packet.SEQUENCE_NUM_LENGTH + Packet.TYPE_LENGTH;
		
		byte[] typeIdentifierArray = new byte[Packet.SEQUENCE_NUM_LENGTH];
		typeIdentifierArray = Arrays.copyOfRange(datagramContents, start, end);
		ByteBuffer typeIdentifierBuffer = ByteBuffer.wrap(typeIdentifierArray);
		
		int typeIdentifier = typeIdentifierBuffer.getInt();
		return typeIdentifier;
	}

	/**
	 * Returns the name of the source.
	 * @param pulsePayloadData the payload data of a pulse packet
	 * @return the name of the source
	 */
	public static String getName(byte[] pulsePayloadData) {
		byte[] nameArray = new byte[pulsePayloadData.length - 4];
		nameArray = Arrays.copyOfRange(pulsePayloadData, 4, pulsePayloadData.length);
		
		String name = "";
		try {
			name = new String(nameArray, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return name;
	}

	/**
	 * Returns the (encrypted) message from an encrypted message packet.
	 * @param encryptedPayloadData the payload data of the encrypted message packet
	 * @return the (encrypted) message of a encrypted message packet
	 */
	public static String getMessage(byte[] encryptedPayloadData) {
		byte[] messageArray = new byte[encryptedPayloadData.length - 10];
		messageArray = Arrays.copyOfRange(encryptedPayloadData, 4, encryptedPayloadData.length);
		
		String message = "";
		try {
			message = new String(messageArray, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return message;
	}

	/**
	 * Returns the messageID of an encrypted message packet.
	 * @param payloadData the payload data of the packet
	 * @return the messageID of the encrypted message
	 */
	public static int getMessageID(byte[] payloadData) {
		byte[] messageIdArray = new byte[2];
		messageIdArray = Arrays.copyOfRange(payloadData, 8, 10);
		ByteBuffer messageIdByteBuffer = ByteBuffer.wrap(messageIdArray);
		
		int messageID = messageIdByteBuffer.getInt();
		return messageID;
	}

}
