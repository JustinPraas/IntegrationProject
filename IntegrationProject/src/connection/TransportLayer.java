package connection;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import encryption.Crypter;
import encryption.DiffieHellman;
import encryption.EncryptionPair;
import model.Message;
import model.Person;
import model.Session;
import packet.*;
import userinterface.GUIHandler;

public class TransportLayer {
	
	// Constants
	public static final int PULSE_TTL = 5;
	public static final int MAX_SEEN_PACKETS_SIZE = 300;
	public static final int RETRANSMISSION_INTERVAL = 1000;
	public static final int MAXIMUM_RETRANSMISSIONS = 5;

	// Used objects
	public Session session;
	public ArrayList<Packet> seenPackets = new ArrayList<>();
	public ArrayList<Packet> unacknowledgedPackets = new ArrayList<>();
	public HashMap<Integer, HashMap<Integer, ArrayList<Packet>>> fileBuffer = new HashMap<>();

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
	public static byte[] shortenDatagramPacket(byte[] datagramArray) {
		int length = 0;
		length += Packet.HEADER_LENGTH;
		
		// Add the length according to the type of payload
		int typeIdentifier = getTypeIdentifier(datagramArray);
		switch (typeIdentifier) {
		case Payload.PULSE:
			length += Pulse.NAME_LENGTH_LENGTH;
			length += Pulse.LEVEL_LENGTH;
			length += getNameLength(getPayload(datagramArray, typeIdentifier).getPayloadData());
			break;
		case Payload.GLOBAL_MESSAGE:
			length += GlobalMessage.MESSAGE_ID_LENGTH;
			length += GlobalMessage.MESSAGE_LENGTH_LENGTH;
			length += getMessageLength(getPayload(datagramArray, typeIdentifier).getPayloadData(), Payload.GLOBAL_MESSAGE);
			break;
		case Payload.ACKNOWLEDGEMENT:
			length += Acknowledgement.ACK_PAYLOAD_LENGHT;
			break;
		case Payload.ENCRYPTION_PAIR:
			length += EncryptionPairExchange.PRIME_LENGTH;
			length += EncryptionPairExchange.GENERATOR_LENGTH;
			length += EncryptionPairExchange.HALF_KEY_LENGTH;
			break;
		case Payload.ENCRYPTED_MESSAGE:
			length += EncryptedMessage.MESSAGE_ID_LENGTH;
			length += EncryptedMessage.MID_WAY_KEY_LENGTH;
			length += EncryptedMessage.CIPHER_LENGTH_LENGTH;
			length += getCipherLength(getPayload(datagramArray, typeIdentifier).getPayloadData());
			break;
		case Payload.FILE_MESSAGE:
			length += FileMessage.FILE_ID_LENGTH;
			length += FileMessage.MESSAGE_LENGTH_LENGTH;
			length += FileMessage.TOTAL_PACKETS;
			length += FileMessage.SEQUENCE_NUMBER;
			length += ((FileMessage) getPayload(datagramArray, typeIdentifier)).getMessageLength();
			break;
		default: 
			System.err.println("Unknown type identifier at shortenDatagramContents(): " + typeIdentifier);
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
		if (seenPacket(receivedPacket) || session.getID() == receivedPacket.getSenderID()) {
			return;
		}	
		
		// If the packet has PLAIN_MESSAGE payload contents, handle it
		// Else, if it's NOT a Pulse AND we are NOT the destination, foward it
		// Else process the packet accordingly
		if (receivedPacket.getTypeIdentifier() == Payload.GLOBAL_MESSAGE) {
			handleGlobalMessage(receivedPacket);
			forwardPacket(receivedPacket);
		} else if (receivedPacket.getTypeIdentifier() != Payload.PULSE && 
				receivedPacket.getReceiverID() != session.getID()) {
			forwardPacket(receivedPacket);
		} else {	
			switch (receivedPacket.getTypeIdentifier()) {
			case Payload.PULSE:
				forwardPacket(receivedPacket);
				handlePulse(receivedPacket);
				break;
			case Payload.ACKNOWLEDGEMENT:
				handleAcknowledgement(receivedPacket);
				break;
			case Payload.ENCRYPTION_PAIR:
				handleEncryptionPair(receivedPacket);
				break;
			case Payload.ENCRYPTED_MESSAGE:
				handleEncryptedMessage(receivedPacket);
				break;
			case Payload.FILE_MESSAGE:
				handleFileMessage(receivedPacket);
				break;
			default: 
				System.err.println("Unknown type identifier at handlePacket(): " + receivedPacket.getTypeIdentifier());
			}
		}
		
		addPacketToSeenPackets(receivedPacket);
	}

	private void handleFileMessage(Packet receivedPacket) {
		int senderID = receivedPacket.getSenderID();
		FileMessage payload = (FileMessage) receivedPacket.getPayload();
		byte[] packetData = payload.getFileData();
		int totalPackets = payload.getTotalPackets();
		int fileID = payload.getFileID();
		
		if (totalPackets > 1) {
			if (!fileBuffer.containsKey(senderID)) {
				HashMap<Integer, ArrayList<Packet>> files = new HashMap<>();
				ArrayList<Packet> packets = new ArrayList<>();
				packets.add(receivedPacket);
				files.put(fileID, packets);
				fileBuffer.put(senderID, files);
			} else if (!fileBuffer.get(senderID).containsKey(fileID)) {
				HashMap<Integer, ArrayList<Packet>> files = fileBuffer.get(senderID);
				ArrayList<Packet> packets = new ArrayList<>();
				packets.add(receivedPacket);
				files.put(fileID, packets);
				fileBuffer.put(senderID, files);
			} else if (fileBuffer.get(senderID).get(fileID).size() < totalPackets - 1) {
				HashMap<Integer, ArrayList<Packet>> files = fileBuffer.get(senderID);
				ArrayList<Packet> packets = files.get(fileID);
				packets.add(receivedPacket);
				files.put(fileID, packets);
				fileBuffer.put(senderID, files);
			} else if (fileBuffer.get(senderID).get(fileID).size() == totalPackets - 1) {
				HashMap<Integer, ArrayList<Packet>> files = fileBuffer.get(senderID);
				ArrayList<Packet> packets = files.get(fileID);
				packets.add(receivedPacket);
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1000000);
				int targetSequence = 0;
				while(targetSequence != totalPackets) {
					for (Packet packet : packets) {
						FileMessage packetPayload = (FileMessage) packet.getPayload();
						if (packetPayload.getSequenceNumber() == targetSequence) {
							try {
								outputStream.write(packetPayload.getFileData());
								targetSequence++;
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
				byte[] fileData = outputStream.toByteArray();
				Message message = new Message(senderID, session.getID(), fileID, FileMessage.FILE_INDICATOR, fileData, false);
				Person sender = session.getKnownPersons().get(senderID);
				boolean addMessageToList = true;		
				// Add it to the chatmessages map
				if (!session.getChatMessages().containsKey(sender)) {
					session.getChatMessages().put(sender, new ArrayList<>(Arrays.asList(new Message[]{message})));
				} else {
					ArrayList<Message> currentMessageList = session.getChatMessages().get(sender);
					
					for (Message msg : currentMessageList) {
						if (msg.getMessageID() == message.getMessageID() && message.getSenderID() == msg.getSenderID()) {
							addMessageToList = false;
							break;
						}
					}
					
					if (addMessageToList) {
						int insertPosition = currentMessageList.size();
						int receivedMessageID = message.getMessageID();
						boolean continues = true;
						for (int i = currentMessageList.size() - 1; i >= 0 && continues; i--) {
							if (currentMessageList.get(i).getSenderID() != session.getID()) {
								if (currentMessageList.get(i).getMessageID() > receivedMessageID) {
									insertPosition = i;
								} else {
									if (insertPosition == currentMessageList.size()) {
										currentMessageList.add(message);
										continues = false;
									} else {
										currentMessageList.add(insertPosition, message);
										continues = false;
									}
								}
							}
						}
						if (continues) {
							currentMessageList.add(message);
						}
						session.getChatMessages().put(sender, currentMessageList);
						GUIHandler.messagePutInMap(sender);
					}			
				}
			}
		} else {
			Message message = new Message(senderID, session.getID(), fileID, FileMessage.FILE_INDICATOR, packetData, false);
			Person sender = session.getKnownPersons().get(senderID);
			boolean addMessageToList = true;		
			// Add it to the chatmessages map
			if (!session.getChatMessages().containsKey(sender)) {
				session.getChatMessages().put(sender, new ArrayList<>(Arrays.asList(new Message[]{message})));
			} else {
				ArrayList<Message> currentMessageList = session.getChatMessages().get(sender);
				
				for (Message msg : currentMessageList) {
					if (msg.getMessageID() == message.getMessageID() && message.getSenderID() == msg.getSenderID()) {
						addMessageToList = false;
						break;
					}
				}
				
				if (addMessageToList) {
					int insertPosition = currentMessageList.size();
					int receivedMessageID = message.getMessageID();
					boolean continues = true;
					for (int i = currentMessageList.size() - 1; i >= 0 && continues; i--) {
						if (currentMessageList.get(i).getSenderID() != session.getID()) {
							if (currentMessageList.get(i).getMessageID() > receivedMessageID) {
								insertPosition = i;
							} else {
								if (insertPosition == currentMessageList.size()) {
									currentMessageList.add(message);
									continues = false;
								} else {
									currentMessageList.add(insertPosition, message);
									continues = false;
								}
							}
						}
					}
					if (continues) {
						currentMessageList.add(message);
					}
					session.getChatMessages().put(sender, currentMessageList);
					GUIHandler.messagePutInMap(sender);
				}			
			}
		}
	}

	/**
	 * Checks if the session has seen this packet before.
	 * @param receivedPacket the received packet
	 * @return true if this packet has been seen before, otherwise false
	 */
	private boolean seenPacket(Packet receivedPacket) {
		for (int i = 0; i < seenPackets.size(); i++) {
			if (seenPackets.get(i).getSenderID() == receivedPacket.getSenderID() &&
					seenPackets.get(i).getSequenceNumber() == receivedPacket.getSequenceNumber()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Processes a received <code>Packet</code> object, interpreted with a
	 * Pulse payload. Adds the person to the knownPersons list if the person was not 
	 * 
	 * in the list yet. Resets the person's TTL and updates the GUI. 
	 * @param receivedPacket the packet that has been received
	 */
	public void handlePulse(Packet receivedPacket) {
		
		// Create a Pulse object and a Person object, derived from the receivedPacket
		Pulse payload = (Pulse) receivedPacket.getPayload();
		Person person;
		int senderID = receivedPacket.getSenderID();
		int level = payload.getLevel();
		boolean updateGUI = false;
		
		// If the sender is known: use this Person object as person
		// else: create a new person with the sender's ID and name
		if (session.getKnownPersons().containsKey(senderID)) {
			person = session.getKnownPersons().get(senderID);
			
			if (level != person.getLevel()) {//TODO
				person.setLevel(level);
				String notificationString = person.getName() + " reached level " + level;
				Message notificationMessage = new Message(-1, -1, -1, notificationString, false);
				session.getPublicChatMessages().add(notificationMessage);
				GUIHandler.messagePutInMap();
				updateGUI = true;
			}
			
			// Please do not merge these two if-statements into one if-else ^v
			if (person.getTimeToLive() <= 0) {
				updateGUI = true;
			}
	
			
			
		} else {
			person = new Person(payload.getName(), senderID, payload.getLevel());
			updateGUI = true;
		}
		
		// Set the peron's time to live to PULSE_TTL and put it in the session.knownPersons map
		person.setTimeToLive(PULSE_TTL);
		session.getKnownPersons().put(senderID, person);
		
		if (updateGUI) {
			// Update the GUI
			GUIHandler.changedPersonList();
		}
	}

	/**
	 * Processes a received <code>Packet</code> object interpreted with an
	 * PlainMessage payload. Creates a <code>Message</code> object from the 
	 * payload contents and updates the chatMessages map. Updates the GUI and sends 
	 * an acknowledgement.
	 * @param receivedPacket
	 */
	public void handleGlobalMessage(Packet receivedPacket) {
		
		GlobalMessage payload = (GlobalMessage) receivedPacket.getPayload();
		
		// The person that sent the message
		Person sender = session.getKnownPersons().get(receivedPacket.getSenderID());
		
		// Convert the packet to a message
		Message receivedMessage = new Message(receivedPacket.getSenderID(), 
				receivedPacket.getReceiverID(), payload.getMessageID(), payload.getPlainText(), false);		
		
		boolean addMessageToList = true;		
		// Add it to the chatmessages map
		ArrayList<Message> publicChatMessageList = session.getPublicChatMessages();		
		for (Message message : publicChatMessageList) {
			if (message.getMessageID() == receivedMessage.getMessageID() && receivedMessage.getSenderID() == message.getSenderID()) {
				addMessageToList = false;
				break;
			}
		}

		// Update GUI
		if (addMessageToList) {
			
			// Update experience bar
			session.getExperienceTracker().receiveGlobalMessage();
			GUIHandler.updateProgressBar();
			
			publicChatMessageList.add(receivedMessage);
			session.setPublicChatMessages(publicChatMessageList);
			GUIHandler.messagePutInMap();
		}		
	}
	
	/**
	 * Processes a received <code>Packet</code> object interpreted with an
	 * PlainMessage payload. Creates a <code>Message</code> object from the 
	 * payload contents and updates the chatMessages map. Updates the GUI and sends 
	 * an acknowledgement.
	 * @param receivedPacket
	 */
	public void handleEncryptedMessage(Packet receivedPacket) {
		
		EncryptedMessage payload = (EncryptedMessage) receivedPacket.getPayload();
		
		// The person that sent the message
		Person sender = session.getKnownPersons().get(receivedPacket.getSenderID());
		
		// Get the messageID
		int messageID = payload.getMessageID();
		
		// Create EncryptedMessage
		EncryptionPair ep = session.getKnownPersons().get(sender.getID()).getPrivateChatPair();
		int secretInteger = session.getSecretKeysForPerson().get(sender.getID());
		String cipher = payload.getCipher();
				
		String decryptedMessage = Crypter.decrypt(Crypter.getKey(ep, secretInteger), cipher);		
		
		// Construct a message
		Message message = new Message(sender.getID(), session.getID(), messageID, decryptedMessage, false);
		
		
		boolean addMessageToList = true;		
		// Add it to the chatmessages map
		if (!session.getChatMessages().containsKey(sender)) {
			session.getChatMessages().put(sender, new ArrayList<>(Arrays.asList(new Message[]{message})));
		} else {
			ArrayList<Message> currentMessageList = session.getChatMessages().get(sender);
			
			for (Message msg : currentMessageList) {
				if (msg.getMessageID() == message.getMessageID() && message.getSenderID() == msg.getSenderID()) {
					addMessageToList = false;
					break;
				}
			}
			
			if (addMessageToList) {
				int insertPosition = currentMessageList.size();
				int receivedMessageID = message.getMessageID();
				boolean continues = true;
				for (int i = currentMessageList.size() - 1; i >= 0 && continues; i--) {
					if (currentMessageList.get(i).getSenderID() != session.getID()) {
						if (currentMessageList.get(i).getMessageID() > receivedMessageID) {
							insertPosition = i;
						} else {
							if (insertPosition == currentMessageList.size()) {
								currentMessageList.add(message);
								continues = false;
							} else {
								currentMessageList.add(insertPosition, message);
								continues = false;
							}
						}
					}
				}
				if (continues) {
					currentMessageList.add(message);
				}
				session.getChatMessages().put(sender, currentMessageList);
			}			
		}
		
		// Update GUI
		if (addMessageToList) {
			
			// Update experience bar
			session.getExperienceTracker().receivePrivateMessage();
			GUIHandler.updateProgressBar();
			
			GUIHandler.messagePutInMap(sender);
		}
		
		// Send an acknowledgement
		sendAcknowledgement(receivedPacket, message);
	}
	
	/**
	 * Processes an <code>Acknowledgment</code> packet. If the message with 
	 * the messageID of this acknowledgement packet was unacknowledged, remove it from
	 * the unacknowledgedPacketList.
	 * @param receivedPacket the received acknowledgement packet
	 */
	public void handleAcknowledgement(Packet receivedPacket) {
		Acknowledgement acknowledgement = (Acknowledgement) receivedPacket.getPayload();
		int messageID = acknowledgement.getMessageID();
		int senderID = receivedPacket.getSenderID();
		
		synchronized (this.unacknowledgedPackets) {
			Packet removePacket = null;
			for (Packet packet : unacknowledgedPackets) {
				if (packet.getTypeIdentifier() == Payload.GLOBAL_MESSAGE) {
					if (packet.getReceiverID() == senderID && 
							((GlobalMessage) packet.getPayload()).getMessageID() == messageID) {
						removePacket = packet;
					}
				} else if (packet.getTypeIdentifier() == Payload.ENCRYPTED_MESSAGE){ 
					if (packet.getReceiverID() == senderID && 
							((EncryptedMessage) packet.getPayload()).getMessageID() == messageID) {
						removePacket = packet;
					}
				}
			}
			// To prevent ConcurrentModificationException
			if (removePacket != null) {
				unacknowledgedPackets.remove(removePacket);
			}
		}	
			
	}
	
	public void handleEncryptionPair(Packet receivedPacket) {
		EncryptionPairExchange epe = (EncryptionPairExchange) receivedPacket.getPayload();
		
		int senderID = receivedPacket.getSenderID();
		
		if (session.getKnownPersons().containsKey(senderID)) {
			if (senderID >= session.getID()) {
				
				// Add the EncryptionPair to the person// Set a secretInteger for messages with this person
				session.getSecretKeysForPerson().put(senderID, DiffieHellman.produceSecretKey(epe.getPrime()));
				int secretInteger = session.getSecretKeysForPerson().get(senderID);
				
				EncryptionPair ep = new EncryptionPair(epe.getPrime(), epe.getGenerator(), secretInteger, true);
				ep.setRemoteHalfKey(epe.getLocalHalfKey());
				session.getKnownPersons().get(senderID).setPrivateChatPair(ep);				
				
				// Send the same EncryptionPairExchange packet back as acknowledgement
				EncryptionPairExchange epeResponse = new EncryptionPairExchange(epe.getPrime(), epe.getGenerator(), ep.getLocalHalfKey());
				Packet packet = new Packet(session.getID(), senderID, session.getNextSeqNumber(), Payload.ENCRYPTION_PAIR, epeResponse);
				session.getConnection().getSender().send(packet);
			} else {
				// Set the PrivateChatPair to be acknowlegded
				session.getKnownPersons().get(senderID).getPrivateChatPair().setAcknowledged(true);
				session.getKnownPersons().get(senderID).getPrivateChatPair().setRemoteHalfKey(epe.getLocalHalfKey());
				}
		}
	}

	/**
	 * Adds a packet to the seenPacket's list. Removes the oldest
	 * entry from the list if the list size exceeds <code>MAX_SEEN_PACKETS_SIZE</code>.
	 * @param receivedPacket the packet that has been received
	 */
	public void addPacketToSeenPackets(Packet receivedPacket) {
		if (seenPackets.size() == MAX_SEEN_PACKETS_SIZE) {
			seenPackets.remove(0);
		}			
		seenPackets.add(receivedPacket);
	}
	
	/**
	 * Forwards a packet to all reachable nodes if this packet has not 
	 * been seen before.
	 * @param receivedPacket the packet that has been received
	 */
	public void forwardPacket(Packet receivedPacket) {		
		// Update experience bar
		if (receivedPacket.getTypeIdentifier() == Payload.ENCRYPTED_MESSAGE) {
			session.getExperienceTracker().forwardMessage();
			GUIHandler.updateProgressBar();
		}
		
		session.getConnection().getSender().send(receivedPacket);
	}

	/**
	 * Sends an acknowledgement to the originator of the <code>message</code>.
	 * @param receivedPacket the packet that contains the message that needs acknowledgement
	 * @param message the message that needs acknowledgement
	 */
	public void sendAcknowledgement(Packet receivedPacket, Message message) {
		// Prepare an acknowledgement
		Acknowledgement acknowledgement = new Acknowledgement(message.getMessageID());
		
		int senderID = receivedPacket.getReceiverID();
		int receiverID = receivedPacket.getSenderID();
		int sequenceNum = session.getNextSeqNumber();
		int typeIdentifier = Payload.ACKNOWLEDGEMENT;
		
		// Send an acknowledgement
		Packet packet = new Packet(senderID, receiverID, sequenceNum, typeIdentifier, acknowledgement);
		session.getConnection().getSender().send(packet);
	}
	
	/**
	 * Sends a message that was entered through the GUI to the <code>receiver</code>. Also
	 * updates the chatMessages map.
	 * @param msg the message to be sent
	 * @param receiver the destination person
	 */
	public void sendMessageFromGUI(String msg, Person receiver) {
		
		// Update experience bar
		session.getExperienceTracker().sendMessage();
		GUIHandler.updateProgressBar();
		
		int nextMessageID = receiver.getNextMessageID();
		
		// Create EncryptedMessage
		EncryptionPair ep = session.getKnownPersons().get(receiver.getID()).getPrivateChatPair();
		int secretInteger = session.getSecretKeysForPerson().get(receiver.getID());
		String cipher = Crypter.encrypt(Crypter.getKey(ep, secretInteger), msg);
		EncryptedMessage encryptedMessage = new EncryptedMessage(nextMessageID, ep.getLocalHalfKey(), cipher.length(), cipher);
		
		Packet packet = new Packet(session.getID(), receiver.getID(), session.getNextSeqNumber(), Payload.ENCRYPTED_MESSAGE, encryptedMessage);
		session.getConnection().getSender().send(packet);
		
		synchronized (this.unacknowledgedPackets) {
			unacknowledgedPackets.add(packet);
			new RetransmissionThread(this, packet);
		}
		
		Message message = new Message(session.getID(), receiver.getID(), nextMessageID, msg, true);		

		// Add it to the chatmessages map
		if (!session.getChatMessages().containsKey(receiver)) {
			session.getChatMessages().put(receiver, new ArrayList<>(Arrays.asList(new Message[]{message})));
		} else {
			ArrayList<Message> currentMessageList = session.getChatMessages().get(receiver);
			currentMessageList.add(message);
			session.getChatMessages().put(receiver, currentMessageList);
		}
		
		// Update the GUI
		GUIHandler.messagePutInMap(receiver);
	}
	
	public void sendMessageFromGUI(String msg) {
		
		// Update experience bar
		session.getExperienceTracker().sendMessage();
		GUIHandler.updateProgressBar();
		
		int msgLength = msg.length();
		int nextPublicMessageID = session.getNextPublicMessageID();
		
		GlobalMessage plainMessage = new GlobalMessage(nextPublicMessageID, msgLength, msg);
		Packet packet = new Packet(session.getID(), 0, session.getNextSeqNumber(), Payload.GLOBAL_MESSAGE, plainMessage);
		session.getConnection().getSender().send(packet);
		
		Message message = new Message(session.getID(), 0, nextPublicMessageID, msg, true);
		session.getPublicChatMessages().add(message);
		
		// Update the GUI
		GUIHandler.messagePutInMap();
	}
	
	/**
	 * Creates a <code>Packet</code> object from datagram contents.
	 * @param datagramContents the datagram contents of a shortened <code>DatagramPacket</code>
	 * @return resultPacket the <code>Packet</code> resulting from the datagram contents
	 */
	public static Packet getPacket(byte[] datagramContents) {
		int senderID = getSenderID(datagramContents);
		int receiverID = getReceiverID(datagramContents);
		int sequenceNumber = getSequenceNumber(datagramContents);
		int typeIdentifier = getTypeIdentifier(datagramContents);		
		Payload payload = getPayload(datagramContents, typeIdentifier);	
		Packet resultPacket = new Packet(senderID, receiverID, sequenceNumber, typeIdentifier, payload); 
		return resultPacket;
	}

	/**
	 * Converts the datagram (shortened or unshortened) contents into a <code>Payload</code> object, according to the
	 * type identifier.
	 * @param datagramContents the packet contents
	 * @param typeIdentifier the type of the payload
	 * @return a <code>Payload</code> object, converted from the packet contents
	 */
	public static Payload getPayload(byte[] datagramContents, int typeIdentifier) {
		byte[] payloadData = Arrays.copyOfRange(datagramContents, Packet.HEADER_LENGTH, datagramContents.length);
		
		switch (typeIdentifier) {
		case Payload.PULSE:
			int nameLength = getNameLength(payloadData);
			String name = getName(payloadData);
			int level = getLevel(payloadData);
			return new Pulse(nameLength, level, name);
		case Payload.GLOBAL_MESSAGE:
			String message = getPlainMessage(payloadData);
			int messageID = getMessageID(payloadData, Payload.GLOBAL_MESSAGE);
			int messageLength = getMessageLength(payloadData, Payload.GLOBAL_MESSAGE);
			return new GlobalMessage(messageID, messageLength, message);
		case Payload.ACKNOWLEDGEMENT:
			int acknowledgeMessageID = getMessageID(payloadData, Payload.ACKNOWLEDGEMENT);
			return new Acknowledgement(acknowledgeMessageID);
		case Payload.ENCRYPTION_PAIR:
			int prime = getPrime(payloadData);
			int generator = getGenerator(payloadData);
			int localHalfKey = getLocalHalfKey(payloadData);
			return new EncryptionPairExchange(prime, generator, localHalfKey);
		case Payload.ENCRYPTED_MESSAGE:
			int MessageID = getMessageID(payloadData, Payload.ENCRYPTED_MESSAGE);
			int midWayKey = getMidWayKey(payloadData);
			int cipherLength = getCipherLength(payloadData);
			String cipher = getCipher(payloadData);
			return new EncryptedMessage(MessageID, midWayKey, cipherLength, cipher);
		case Payload.FILE_MESSAGE:
			int fileID = getMessageID(payloadData, Payload.FILE_MESSAGE);
			int fileMessageLength = getMessageLength(payloadData, Payload.FILE_MESSAGE);
			int totalPackets = getTotalPackets(payloadData);
			int sequenceNumber = getFileSequenceNumber(payloadData);
			byte[] fileData = getFileData(payloadData);
			return new FileMessage(fileID, fileMessageLength, totalPackets, sequenceNumber, fileData);
		default: 
			System.err.println("Unknown type identifier at getPayload(): " + typeIdentifier);
			return null;
		}	
	}

	private static byte[] getFileData(byte[] payloadData) {
		int start = FileMessage.FILE_ID_LENGTH + FileMessage.MESSAGE_LENGTH_LENGTH + 
				FileMessage.TOTAL_PACKETS + FileMessage.SEQUENCE_NUMBER;
		int end = start + getMessageLength(payloadData, Payload.FILE_MESSAGE);
		
		byte[] fileData = Arrays.copyOfRange(payloadData, start, end);
		
		return fileData;
	}

	private static int getFileSequenceNumber(byte[] payloadData) {
		int start = FileMessage.FILE_ID_LENGTH + FileMessage.MESSAGE_LENGTH_LENGTH + FileMessage.TOTAL_PACKETS;
		int end = start + FileMessage.SEQUENCE_NUMBER;
		
		byte[] fileSequenceArray = Arrays.copyOfRange(payloadData, start, end);
		ByteBuffer fileSequenceBytebuffer = ByteBuffer.wrap(fileSequenceArray);
		
		int fileSequency = fileSequenceBytebuffer.get();
		
		return fileSequency;
	}

	private static int getTotalPackets(byte[] payloadData) {
		int start = FileMessage.FILE_ID_LENGTH + FileMessage.MESSAGE_LENGTH_LENGTH;
		int end = start + FileMessage.TOTAL_PACKETS;
		
		byte[] totalPacketsArray = Arrays.copyOfRange(payloadData, start, end);
		ByteBuffer totalPacketsBytebuffer = ByteBuffer.wrap(totalPacketsArray);
		
		int totalPackets = totalPacketsBytebuffer.get();
		
		return totalPackets;
	}

	/**
	 * Returns the senderID of the source.
	 * @param datagramContents the contents of the packet
	 * @return senderID the senderID of the source
	 */
	public static int getSenderID(byte[] datagramContents) {
		int start = 0;
		int end = start + Packet.SENDER_LENGTH;
		
		byte[] senderIdArray = Arrays.copyOfRange(datagramContents, start, end);
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
		int end = start + Packet.RECEIVER_LENGTH;
		
		byte[] receiverIdArray = Arrays.copyOfRange(datagramContents, start, end);
		ByteBuffer receiverIdByteBuffer = ByteBuffer.wrap(receiverIdArray);
		
		int receiverID = receiverIdByteBuffer.getInt();
		return receiverID;
	}

	/**
	 * Returns the sequence number of the packet.
	 * @param datagramContents the packet contents
	 * @return seqNum the sequence number of the packet
	 */
	public static int getSequenceNumber(byte[] datagramContents) {
		int start = Packet.SENDER_LENGTH + Packet.RECEIVER_LENGTH;
		int end = start + Packet.SEQUENCE_NUM_LENGTH;
		byte[] seqNumArray = Arrays.copyOfRange(datagramContents, start, end);
		ByteBuffer seqNumByteBuffer = ByteBuffer.wrap(seqNumArray);
		
		int seqNum = seqNumByteBuffer.getShort();
		return seqNum;
	}

	/**
	 * Returns the type identifier of the packet, representing the payload type.
	 * @param datagramContents the packet contents
	 * @return typeIdentifier the type identifier of the packet
	 */
	public static int getTypeIdentifier(byte[] datagramContents) {
		int start = Packet.SENDER_LENGTH + Packet.RECEIVER_LENGTH + Packet.SEQUENCE_NUM_LENGTH;
		int end = start + Packet.TYPE_LENGTH;
		
		byte[] typeIdentifierArray = Arrays.copyOfRange(datagramContents, start, end);
		ByteBuffer typeIdentifierBuffer = ByteBuffer.wrap(typeIdentifierArray);
		
		int typeIdentifier = typeIdentifierBuffer.get();
		return typeIdentifier;
	}

	/**
	 * Returns the name of the source from the <code>Pulse</code> payload data.
	 * @param pulsePayloadData the payload data of a pulse packet
	 * @return name the name of the source
	 */
	public static String getName(byte[] pulsePayloadData) {
		int length = getNameLength(pulsePayloadData);
		int start = Pulse.NAME_LENGTH_LENGTH + Pulse.LEVEL_LENGTH;
		int end = start + length;
		
		byte[] nameArray = Arrays.copyOfRange(pulsePayloadData, start, end);
		
		String name = "";
		try {
			name = new String(nameArray, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return name;
	}
	
	public static int getLevel(byte[] pulsePayloadData) {
		int position = Pulse.NAME_LENGTH_LENGTH;
		
		return pulsePayloadData[position];
	}

	/**
	 * Returns the name length from the <code>Pulse</code> payload data.
	 * @param pulsePayloadData the payload data of a pulse packet
	 * @return pulsePayloadData[0] the length of the name
	 */
	public static int getNameLength(byte[] pulsePayloadData) {
		return pulsePayloadData[0];	
	}

	/**
	 * Returns the (encrypted) message from the <code>EncryptedMessage</code> payload data.
	 * @param payloadData the payload data of the <code>EncryptedMessage</code> packet
	 * @return message the (encrypted) message of an encrypted message packet
	 */

	public static String getPlainMessage(byte[] payloadData) {
		int length = getMessageLength(payloadData, Payload.GLOBAL_MESSAGE);
		int start = GlobalMessage.MESSAGE_ID_LENGTH + GlobalMessage.MESSAGE_LENGTH_LENGTH;
		int end = start + length;
		byte[] messageArray = Arrays.copyOfRange(payloadData, start, end);
		
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
	 * @return messageID the messageID of the message
	 */
	public static int getMessageID(byte[] payloadData, int typeIdentifier) {
		int start = 0, end = 0;
		
		if (typeIdentifier == Payload.GLOBAL_MESSAGE) {
			start = 0;
			end = start + GlobalMessage.MESSAGE_ID_LENGTH;
		} else if (typeIdentifier == Payload.ENCRYPTED_MESSAGE) {
			start = 0;
			end = start + EncryptedMessage.MESSAGE_ID_LENGTH;
		} else if (typeIdentifier == Payload.ACKNOWLEDGEMENT) {
			start = 0;
			end = start + Acknowledgement.ACK_PAYLOAD_LENGHT;
		} else if (typeIdentifier == Payload.FILE_MESSAGE) {
			start = 0;
			end = start + FileMessage.FILE_ID_LENGTH;
		}
		
		byte[] messageIdArray = Arrays.copyOfRange(payloadData, start, end);
		ByteBuffer messageIdByteBuffer = ByteBuffer.wrap(messageIdArray);
		
		int messageID = messageIdByteBuffer.getShort();
		return messageID;
	}

	public static int getMessageLength(byte[] payloadData, int payloadType) {
		int start = 0;
		int end = 0;
		switch (payloadType) {
			case Payload.GLOBAL_MESSAGE:
				start = GlobalMessage.MESSAGE_ID_LENGTH;
				end = start + GlobalMessage.MESSAGE_LENGTH_LENGTH;
				
				byte[] encryptedMessageLengthArray = Arrays.copyOfRange(payloadData, start, end);
				ByteBuffer encryptedMessageLengthBytebuffer = ByteBuffer.wrap(encryptedMessageLengthArray);
				
				int messageLength = encryptedMessageLengthBytebuffer.getShort();
				
				return messageLength;
			case Payload.FILE_MESSAGE:
				start = FileMessage.FILE_ID_LENGTH;
				end = start + FileMessage.MESSAGE_LENGTH_LENGTH;
				
				byte[] fileMessageLengthArray = Arrays.copyOfRange(payloadData, start, end);
				ByteBuffer fileMessageLengthBytebuffer = ByteBuffer.wrap(fileMessageLengthArray);
				
				int fileMessageLength = fileMessageLengthBytebuffer.getInt();
				
				return fileMessageLength;
			default: return 0;
		}
	}

	private static int getCipherLength(byte[] payloadData) {
		int start = EncryptedMessage.MESSAGE_ID_LENGTH + EncryptedMessage.MID_WAY_KEY_LENGTH;
		int end = start + EncryptedMessage.CIPHER_LENGTH_LENGTH;
		
		byte[] cipherLengthArray = Arrays.copyOfRange(payloadData, start, end);	
		ByteBuffer cipherLengthBytebuffer = ByteBuffer.wrap(cipherLengthArray);
		
		int cipherLength = cipherLengthBytebuffer.getShort();
		return cipherLength;
	}

	private static String getCipher(byte[] payloadData) {
		int length = getCipherLength(payloadData);
		int start = EncryptedMessage.MESSAGE_ID_LENGTH + EncryptedMessage.MID_WAY_KEY_LENGTH + EncryptedMessage.CIPHER_LENGTH_LENGTH;
		int end = start + length;
		byte[] cipherArray = Arrays.copyOfRange(payloadData, start, end);
		
		String cipher = "";
		try {
			cipher = new String(cipherArray, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return cipher;
	}

	private static int getMidWayKey(byte[] payloadData) {
		int start = EncryptedMessage.MESSAGE_ID_LENGTH;
		int end = start + EncryptedMessage.MID_WAY_KEY_LENGTH;
		
		byte[] midWayKeyArray = Arrays.copyOfRange(payloadData, start, end);	
		ByteBuffer midWayKeyBytebuffer = ByteBuffer.wrap(midWayKeyArray);
		
		int midWayKey = midWayKeyBytebuffer.get();
		return midWayKey;
	}

	private static int getPrime(byte[] payloadData) {
		int start = 0;
		int end = start + EncryptionPairExchange.PRIME_LENGTH;
		
		byte[] primeArray = Arrays.copyOfRange(payloadData, start, end);	
		ByteBuffer primeBytebuffer = ByteBuffer.wrap(primeArray);
		
		int prime = primeBytebuffer.get();
		return prime;
	}

	private static int getGenerator(byte[] payloadData) {
		int start = EncryptionPairExchange.PRIME_LENGTH;
		int end = start + EncryptionPairExchange.GENERATOR_LENGTH;
		
		byte[] generatorArray = Arrays.copyOfRange(payloadData, start, end);	
		ByteBuffer generatorBytebuffer = ByteBuffer.wrap(generatorArray);
		
		int generator = generatorBytebuffer.get();
		return generator;
	}
	
	private static int getLocalHalfKey(byte[] payloadData) {
		int start = EncryptionPairExchange.PRIME_LENGTH + EncryptionPairExchange.GENERATOR_LENGTH;;
		int end = start + EncryptionPairExchange.HALF_KEY_LENGTH;
		
		byte[] halfKeyArray = Arrays.copyOfRange(payloadData, start, end);	
		ByteBuffer halfKeyBytebuffer = ByteBuffer.wrap(halfKeyArray);
		
		int halfKey = halfKeyBytebuffer.get();
		return halfKey;
	}

	public void sendFile(File file, Person receiver) throws IOException {
		byte[] fileData = Files.readAllBytes(file.toPath());
		ArrayList<byte[]> result = new ArrayList<byte[]>();
		int start = 0;
		int chunksize = 62000;
		while (start < fileData.length) {
			int end = Math.min(fileData.length, start + chunksize);
		    result.add(Arrays.copyOfRange(fileData, start, end));
		    start += chunksize;
		}
		int nextFileID = receiver.getNextFileID();
		int seqNum = 0; 
		for (byte[] dataSegment : result) {
			FileMessage payload = new FileMessage(nextFileID, dataSegment.length, result.size(), seqNum, dataSegment);
			Packet packet = new Packet(session.getID(), receiver.getID(), session.getNextSeqNumber(), Payload.FILE_MESSAGE, payload);
			session.getConnection().getSender().send(packet);
			synchronized (this.unacknowledgedPackets) {
				unacknowledgedPackets.add(packet);
				new RetransmissionThread(this, packet);
			}
			seqNum++;
		}
		
		Message message = new Message(session.getID(), receiver.getID(), nextFileID, FileMessage.FILE_INDICATOR, fileData, true);		

		// Add it to the chatmessages map
		if (!session.getChatMessages().containsKey(receiver)) {
			session.getChatMessages().put(receiver, new ArrayList<>(Arrays.asList(new Message[]{message})));
		} else {
			ArrayList<Message> currentMessageList = session.getChatMessages().get(receiver);
			currentMessageList.add(message);
			session.getChatMessages().put(receiver, currentMessageList);
		}
		// Update the GUI
		GUIHandler.messagePutInMap(receiver);
	}

}
