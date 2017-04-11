package connection;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import application.Session;
import model.Message;
import model.Person;
import packet.*;
import userinterface.GUIHandler;

public class TransportLayer {
	
	// Constants
	public static final int PULSE_TTL = 5;

	// Used objects
	private Session session;
	private ArrayList<Packet> seenPackets = new ArrayList<>();

	/**
	 * Creates a <code>TransportLayer</code> object that acts on a session.
	 * @param session the session to act upon
	 */
	public TransportLayer(Session session) {
		this.session = session;
	}

	/**
	 * Shortens the given input from the datagramArray back to the
	 * initial packet that was sent by the source node.
	 * @param datagramArray the data from the DatagramPacket
	 * @return originalDatagramContents the initial packet that was sent by the source node
	 */
	private static byte[] shortenDatagramPacket(byte[] datagramArray) {
		int length = 0;
		length += Packet.HEADER_LENGTH;
		
		// Add the length according to the type of payload
		int typeIdentifier = getTypeIdentifier(datagramArray);
		switch (typeIdentifier) {
		case Payload.PULSE:
			length += Pulse.NAME_LENGTH_LENGTH;
			length += getNameLength(getPayload(datagramArray, typeIdentifier).getPayloadData());
			break;
		case Payload.ENCRYPTED_MESSAGE:
			length += EncryptedMessage.MESSAGE_ID_LENGTH;
			length += EncryptedMessage.MESSAGE_LENGTH_LENGTH;
			length += getMessageLength(getPayload(datagramArray, typeIdentifier).getPayloadData());
			break;
		case Payload.ACKNOWLEDGEMENT:
			length += Acknowledgement.ACK_PAYLOAD_LENGHT;
			break;
		case Payload.ENCRYPTION_PAIR:
			// TODO: implement encryption
			break;
		default: 
			System.err.println("Unknown type identifier: " + typeIdentifier);
		}
		
		byte[] originalDatagramContents = Arrays.copyOfRange(datagramArray, 0, length);
		return originalDatagramContents;
	}

	/**
	 * Processes a received <code>DatagramPacket</code>. If the packet has been here 
	 * before, don't process the packet, otherwise pass it on to the corresponding payload
	 * handlers according to the packet's type identifier.
	 * @param datagramPacket the received packet
	 */
	public void handlePacket(DatagramPacket datagramPacket) {
		byte[] datagramContents = shortenDatagramPacket(datagramPacket.getData());
		
		// Construct a Packet object from the datagramContents	
		Packet receivedPacket = getPacket(datagramContents);
		
		// Don't do anything if: we've already seen this packet OR if this packet is from ourself
		// Else: add the packet to the seenPackets list
		if (seenPackets.contains(receivedPacket) || session.getID() == receivedPacket.getSenderID()) {
			return;
		} else {
			addPacketToSeenPackets(receivedPacket);
		}		
		
		// Forward the packet if: it's NOT a Pulse AND we are NOT the destination
		// Else: process the packet accordingly
		if (receivedPacket.getTypeIdentifier() != Payload.PULSE && 
				receivedPacket.getReceiverID() != session.getID()) {
			forwardPacket(receivedPacket);
		} else {	
			switch (receivedPacket.getTypeIdentifier()) {
			case Payload.PULSE:
				forwardPacket(receivedPacket);
				handlePulse(receivedPacket);
				break;
			case Payload.ENCRYPTED_MESSAGE:
				handleEncryptedMessage(receivedPacket);
				break;
			case Payload.ACKNOWLEDGEMENT:
				handleAcknowledgement(receivedPacket);
				break;
			case Payload.ENCRYPTION_PAIR:
				// TODO: Implement encryption pair
				break;
			default: 
				System.err.println("Unknown type identifier: " + receivedPacket.getTypeIdentifier());
			}
		}
	}

	/**
	 * Processes a received <code>Packet</code> object, interpreted with a
	 * Pulse payload. Adds the person to the knownPersons list if the person was not 
	 * in the list yet. Resets the person's TTL and updates the GUI. 
	 * @param receivedPacket the packet that has been received
	 */
	public void handlePulse(Packet receivedPacket) {
		
		// Create a Pulse object and a Person object, derived from the receivedPacket
		Pulse payload = (Pulse) receivedPacket.getPayload();
		Person person;
		int senderID = receivedPacket.getSenderID();
		
		// If the sender is known: use this Person object as person
		// else: create a new person with the sender's ID and name
		if (session.getKnownPersons().containsKey(senderID)) {
			person = session.getKnownPersons().get(senderID);
		} else {
			person = new Person(payload.getName(), senderID);
		}
		
		// Set the peron's time to live to PULSE_TTL and put it in the session.knownPersons map
		person.setTimeToLive(PULSE_TTL);
		session.getKnownPersons().put(senderID, person);
		
		// Update the GUI
		GUIHandler.changedPersonList();
	}

	/**
	 * Processes a received <code>Packet</code> object interpreted with an
	 * EncryptedMessage payload. Creates a <code>Message</code> object from the 
	 * payload contents and updates the chatMessages map. Updates the GUI and sends 
	 * an acknowledgement.
	 * @param receivedPacket
	 */
	public void handleEncryptedMessage(Packet receivedPacket) {
		EncryptedMessage payload = (EncryptedMessage) receivedPacket.getPayload();
		
		// Convert the packet to a message
		Message message = new Message(receivedPacket.getSenderID(), 
				receivedPacket.getReceiverID(), payload.getMessageID(), payload.getEncryptedMessage(), false);
		
		// The person that sent the message
		Person person = session.getKnownPersons().get(receivedPacket.getSenderID());
		
		// TODO SYNCHRONIZE
		// Add it to the chatmessages map
		if (!session.getChatMessages().containsKey(person)) {
			session.getChatMessages().put(person, new ArrayList<>(Arrays.asList(new Message[]{message})));
		} else {
			ArrayList<Message> currentMessageList = session.getChatMessages().get(person);
			currentMessageList.add(message);
			session.getChatMessages().put(person, currentMessageList);
		}
		
		// Update GUI
		GUIHandler.messagePutInMap(person);
		
		// Send an acknowledgement
		sendAcknowledgement(receivedPacket, message);		
	}

	public void handleAcknowledgement(Packet receivedPacket) {
		// TODO Auto-generated method stub
		
	}
	
	public void handleEncryptionPair(Packet receivedPacket) {
		// TODO implement encryption pair
	}

	public void addPacketToSeenPackets(Packet receivedPacket) {
		if (seenPackets.size() == 300) {
			seenPackets.remove(0);
		}			
		seenPackets.add(receivedPacket);
	}
	
	public void forwardPacket(Packet receivedPacket) {
		if (!seenPackets.contains(receivedPacket)) {
			session.getConnection().getSender().send(receivedPacket);
		}
	}

	private void sendAcknowledgement(Packet receivedPacket, Message message) {
		// Prepare an acknowledgement
		Acknowledgement acknowledgement = new Acknowledgement(message.getMessageID());
		
		int senderID = receivedPacket.getReceiverID();
		int receiverID = receivedPacket.getSenderID();
		int sequenceNum = session.getNextSeq();
		int typeIdentifier = Payload.ACKNOWLEDGEMENT;
		
		// Send an acknowledgement
		Packet packet = new Packet(senderID, receiverID, sequenceNum, typeIdentifier, acknowledgement);
		session.getConnection().getSender().send(packet);
	}
	
	public void sendMessageFromGUI(String msg, Person receiver) {
		int msgLength = msg.length();
		int nextMessageID = receiver.getNextMessageID();
		EncryptedMessage EncryptedMessage = new EncryptedMessage(nextMessageID, msgLength, msg); // TODO: Encrypt
		Message message = new Message(session.getID(), receiver.getID(), nextMessageID, msg, true);
		Packet packet = new Packet(session.getID(), receiver.getID(), session.getNextSeq(), Payload.ENCRYPTED_MESSAGE, EncryptedMessage);
		session.getConnection().getSender().send(packet);

		// TODO SYNCHRONIZE
		// Add it to the chatmessages map
		if (!session.getChatMessages().containsKey(receiver)) {
			session.getChatMessages().put(receiver, new ArrayList<>(Arrays.asList(new Message[]{message})));
		} else {
			ArrayList<Message> currentMessageList = session.getChatMessages().get(receiver);
			currentMessageList.add(message);
			session.getChatMessages().put(receiver, currentMessageList);
		}
		
		GUIHandler.messagePutInMap(receiver);
	}
	
	/**
	 * Creates a <code>Packet</code> object from datagram contents.
	 * @param datagramContents the datagram contents of a <code>DatagramPacket</code>
	 * @return resultPacket the <code>Packet</code> resulting from the datagram contents
	 */
	private Packet getPacket(byte[] datagramContents) {
		int senderID = getSenderID(datagramContents);
		int receiverID = getReceiverID(datagramContents);
		int sequenceNumber = getSequenceNumber(datagramContents);
		int typeIdentifier = getTypeIdentifier(datagramContents);		
		Payload payload = getPayload(datagramContents, typeIdentifier);	
		Packet resultPacket = new Packet(senderID, receiverID, sequenceNumber, typeIdentifier, payload); 
		return resultPacket;
	}

	/**
	 * Converts the datagram-packet contents into a <code>Payload</code> object, according to the
	 * type identifier.
	 * @param datagramContents the packet contents
	 * @param typeIdentifier the type of the payload
	 * @return a <code>Payload</code> object, converted from the packet contents
	 */
	private static Payload getPayload(byte[] datagramContents, int typeIdentifier) {
		byte[] payloadData = Arrays.copyOfRange(datagramContents, Packet.HEADER_LENGTH, datagramContents.length);
		
		switch (typeIdentifier) {
		case Payload.PULSE:
			int nameLength = getNameLength(payloadData);
			String name = getName(payloadData);
			return new Pulse(nameLength, name);
		case Payload.ENCRYPTED_MESSAGE:
			String message = getMessage(payloadData);
			int encryptionMessageID = getMessageID(payloadData);
			int messageLength = getMessageLength(payloadData);
			return new EncryptedMessage(encryptionMessageID, messageLength, message);
		case Payload.ACKNOWLEDGEMENT:
			int acknowledgeMessageID = getMessageID(payloadData);
			return new Acknowledgement(acknowledgeMessageID);	
		case Payload.ENCRYPTION_PAIR:
			// TODO: implement encryption pair
		default: 
			System.err.println("Unknown type identifier: " + typeIdentifier);
			return null;
		}	
	}

	/**
	 * Returns the senderID of the source.
	 * @param datagramContents the contents of the packet
	 * @return
	 */
	private static int getSenderID(byte[] datagramContents) {
		int start = 0;
		int end = start + Packet.SENDER_LENGTH;
		
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
	private static int getReceiverID(byte[] datagramContents) {
		int start = Packet.RECEIVER_LENGTH;
		int end = start + Packet.RECEIVER_LENGTH;
		
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
	private static int getSequenceNumber(byte[] datagramContents) {
		int start = Packet.SENDER_LENGTH + Packet.RECEIVER_LENGTH;
		int end = start + Packet.SEQUENCE_NUM_LENGTH;
		
		byte[] seqNumArray = new byte[Packet.SEQUENCE_NUM_LENGTH];
		seqNumArray = Arrays.copyOfRange(datagramContents, start, end);
		ByteBuffer seqNumByteBuffer = ByteBuffer.wrap(seqNumArray);
		
		int seqNum = seqNumByteBuffer.getShort();
		return seqNum;
	}
	
	/**
	 * Returns the type identifier of the packet, representing the payload type.
	 * @param datagramContents the packet contents
	 * @return the type identifier of the packet
	 */
	private static int getTypeIdentifier(byte[] datagramContents) {
		int start = Packet.SENDER_LENGTH + Packet.RECEIVER_LENGTH + Packet.SEQUENCE_NUM_LENGTH;
		int end = start + Packet.TYPE_LENGTH;
		
		byte[] typeIdentifierArray = new byte[Packet.SEQUENCE_NUM_LENGTH];
		typeIdentifierArray = Arrays.copyOfRange(datagramContents, start, end);
		ByteBuffer typeIdentifierBuffer = ByteBuffer.wrap(typeIdentifierArray);
		
		int typeIdentifier = typeIdentifierBuffer.get();
		return typeIdentifier;
	}

	/**
	 * Returns the name of the source.
	 * @param pulsePayloadData the payload data of a pulse packet
	 * @return the name of the source
	 */
	private static String getName(byte[] pulsePayloadData) {
		int length = getNameLength(pulsePayloadData);
		int start = Pulse.NAME_LENGTH_LENGTH;
		int end = start + length;
		
		byte[] nameArray = new byte[length];
		nameArray = Arrays.copyOfRange(pulsePayloadData, start, end);
		
		String name = "";
		try {
			name = new String(nameArray, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return name;
	}

	private static int getNameLength(byte[] pulsePayloadData) {		
		return pulsePayloadData[0];	
	}

	/**
	 * Returns the (encrypted) message from an encrypted message packet.
	 * @param encryptedPayloadData the payload data of the encrypted message packet
	 * @return the (encrypted) message of a encrypted message packet
	 */
	private static String getMessage(byte[] encryptedPayloadData) {
		int length = getMessageLength(encryptedPayloadData);
		int start = EncryptedMessage.MESSAGE_ID_LENGTH + EncryptedMessage.MESSAGE_LENGTH_LENGTH;
		int end = start + length;
		byte[] messageArray = new byte[length];
		messageArray = Arrays.copyOfRange(encryptedPayloadData, start, end);
		
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
	 * @param encryptedPayloadData the payload data of the packet
	 * @return the messageID of the encrypted message
	 */
	private static int getMessageID(byte[] encryptedPayloadData) {
		int start = 0;
		int end = start + EncryptedMessage.MESSAGE_ID_LENGTH;
		byte[] messageIdArray = new byte[EncryptedMessage.MESSAGE_ID_LENGTH];
		messageIdArray = Arrays.copyOfRange(encryptedPayloadData, start, end);
		ByteBuffer messageIdByteBuffer = ByteBuffer.wrap(messageIdArray);
		
		int messageID = messageIdByteBuffer.getShort();
		return messageID;
	}

	private static int getMessageLength(byte[] encryptedPayloadData) {
		int start = EncryptedMessage.MESSAGE_ID_LENGTH;
		int end = start + EncryptedMessage.MESSAGE_LENGTH_LENGTH;
		
		byte[] messageLengthArray = new byte[EncryptedMessage.MESSAGE_LENGTH_LENGTH];
		messageLengthArray = Arrays.copyOfRange(encryptedPayloadData, start, end);	
		ByteBuffer messageLengthBytebuffer = ByteBuffer.wrap(messageLengthArray);
		
		int messageLength = messageLengthBytebuffer.getShort();
		return messageLength;
	}

}
