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
		
		if (seenPackets.contains(receivedPacket) || session.getID() == receivedPacket.getSenderID()) {
			return;
		}
		
		addPacketToSeenPackets(receivedPacket);
		
		// TODO: First check if we've seen this packet before, otherwise process the packet
		if (typeIdentifier != PayloadType.PULSE.getType() && receivedPacket.getReceiverID() != session.getID()) {
			forwardPacket(receivedPacket);
		} else {	
			if (PayloadType.PULSE.getType() == typeIdentifier) {
				forwardPacket(receivedPacket);
				handlePulse(receivedPacket);
			} else if (PayloadType.ENCRYPTED_MESSAGE.getType() == typeIdentifier) {
				handleEncryptedMessage(receivedPacket);
			} else if (PayloadType.ACKNOWLEDGEMENT.getType() == typeIdentifier) {
				handleAcknowledgement(receivedPacket);
			} else if (PayloadType.ENCRYPTION_PAIR.getType() == typeIdentifier) {
				// TODO: handleEncryptionPair(receivedPacket);
			} else {
				System.err.println("Something went wrong...");
			}
		}			
	}

	private void handleAcknowledgement(Packet receivedPacket) {
		// TODO Auto-generated method stub
		
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
	
	public void sendMessageFromGUI(String msg, Person receiver) {
		int nextMessageID = receiver.getNextMessageID();
		EncryptedMessage EncryptedMessage = new EncryptedMessage(nextMessageID, msg); // TODO: Encrypt
		Message message = new Message(session.getID(), receiver.getID(), nextMessageID, msg, true);
		Packet packet = new Packet(session.getID(), receiver.getID(), session.getNextSeq(), PayloadType.ENCRYPTED_MESSAGE.getType(), EncryptedMessage);
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
	 * Processes a received <code>Packet</code> object.
	 * @param receivedPacket the packet that has been received
	 */
	public void handlePulse(Packet receivedPacket) {
		Pulse payload = (Pulse) receivedPacket.getPayload();
		Person person = new Person(payload.getName(), receivedPacket.getSenderID());
		
		if (!session.getKnownPersons().containsKey(person.getID())) {
			session.getKnownPersons().put(person.getID(), person);
			GUIHandler.changedPersonList();
		} 
		//TODO
		person.setTimeToLive(PULSE_TTL);
		
	}
	
	private void handleEncryptedMessage(Packet receivedPacket) {
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
		
		// Prepare an acknowledgement
		Acknowledgement acknowledgement = new Acknowledgement(message.getMessageID());
		
		int senderID = receivedPacket.getReceiverID();
		int receiverID = receivedPacket.getSenderID();
		int sequenceNum = session.getNextSeq();
		int type = PayloadType.ACKNOWLEDGEMENT.getType();
		
		// Send an acknowledgement
		Packet packet = new Packet(senderID, receiverID, sequenceNum, type, acknowledgement);
		session.getConnection().getSender().send(packet);		
	}

	/**
	 * Converts the datagram-packet contents into a <code>Payload</code> object, according to the
	 * type identifier.
	 * @param datagramContents the packet contents
	 * @param typeIdentifier the type of the payload
	 * @return a <code>Payload</code> object, converted from the packet contents
	 */
	public static Payload getPayload(byte[] datagramContents, int typeIdentifier) {
		byte[] payloadData = Arrays.copyOfRange(datagramContents, Packet.HEADER_LENGTH, datagramContents.length);
		
		if (PayloadType.PULSE.getType() == typeIdentifier) {
			String name = getName(payloadData);
			return new Pulse(name);
		} else if (PayloadType.ENCRYPTED_MESSAGE.getType() == typeIdentifier) {
			String message = getMessage(payloadData);
			int messageID = getMessageID(payloadData);
			return new EncryptedMessage(messageID, message);
		} else if (PayloadType.ACKNOWLEDGEMENT.getType() == typeIdentifier) {
			int messageID = getMessageID(payloadData);
			return new Acknowledgement(messageID);
		} else if (PayloadType.ENCRYPTION_PAIR.getType() == typeIdentifier) {
			// TODO: implement encryption pair exchange
		} else {
			System.err.println("Something went wrong...");
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
		
		int seqNum = seqNumByteBuffer.getShort();
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
		
		int typeIdentifier = typeIdentifierBuffer.get();
		return typeIdentifier;
	}

	/**
	 * Returns the name of the source.
	 * @param pulsePayloadData the payload data of a pulse packet
	 * @return the name of the source
	 */
	public static String getName(byte[] pulsePayloadData) {
		byte[] nameArray = new byte[pulsePayloadData.length - 4];
		System.out.println(Arrays.toString(pulsePayloadData));
		nameArray = Arrays.copyOfRange(pulsePayloadData, 5, pulsePayloadData.length);
		
		String name = "";
		try {
			name = new String(nameArray, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.out.println(name);
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
		messageIdArray = Arrays.copyOfRange(payloadData, 11, 13);
		ByteBuffer messageIdByteBuffer = ByteBuffer.wrap(messageIdArray);
		
		int messageID = messageIdByteBuffer.getShort();
		return messageID;
	}

}
