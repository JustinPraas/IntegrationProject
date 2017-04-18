package tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import connection.TransportLayer;
import packet.*;

public class TransportLayerTest {
	
	Pulse pulse = new Pulse(3, 2, "Bob");
	Packet pulsePacket = new Packet(2, 5, 4, Payload.PULSE, pulse);
	
	GlobalMessage globalMessage = new GlobalMessage(1, 3, "Hey");
	Packet globalMessagePacket = new Packet(3, 33, 5, Payload.GLOBAL_MESSAGE, globalMessage);
	
	Acknowledgement acknowledgement = new Acknowledgement(3);
	Packet acknowledgementPacket = new Packet(22, 11, 6, Payload.ACKNOWLEDGEMENT, acknowledgement);
	
	EncryptedMessage encryptedMessage = new EncryptedMessage(3, 20, 10, "Hello Jane");
	Packet encryptedMessagePacket = new Packet(23, 53, 7, Payload.ENCRYPTED_MESSAGE, encryptedMessage);
	
	EncryptionPairExchange encryptionPairExchange = new EncryptionPairExchange(23, 5, 25);
	Packet encryptionPairExchangePacket = new Packet(53, 23, 8, Payload.ENCRYPTION_PAIR, encryptionPairExchange);
	
	public DatagramPacket simulateReceiver(Packet packet) {
		byte buffer[] = new byte[1024000];
		DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
		for (int i = 0; i < packet.getDatagramPacketData().length; i++) { 
			datagramPacket.getData()[i] = packet.getDatagramPacketData()[i];
		}
		return datagramPacket;
	}

	@Test
	public void shortenDatagramPacketTest() {
		
		// Test a pulse packet
		DatagramPacket pulseDatagram = pulsePacket.getDatagramPacket();
		DatagramPacket receivedPulseDatagram = simulateReceiver(pulsePacket);
		
		byte[] pulseDatagramData = pulseDatagram.getData();
		byte[] shortenedReceivedPulseDatagram = TransportLayer.shortenDatagramPacket(receivedPulseDatagram.getData());
		
		assertEquals(Arrays.toString(pulseDatagramData), Arrays.toString(shortenedReceivedPulseDatagram));
	
		// Test a global message packet
		DatagramPacket globalMessageDatagram = globalMessagePacket.getDatagramPacket();
		DatagramPacket receivedGlobalMessageDatagram = simulateReceiver(globalMessagePacket);
		
		byte[] globalMessageDatagramData = globalMessageDatagram.getData();
		byte[] shortenedReceivedGlobalMessageDatagram = TransportLayer.shortenDatagramPacket(receivedGlobalMessageDatagram.getData());
		
		assertEquals(Arrays.toString(globalMessageDatagramData), Arrays.toString(shortenedReceivedGlobalMessageDatagram));
	
		// Test an acknowledgement packet
		DatagramPacket acknowledgementDatagram = acknowledgementPacket.getDatagramPacket();
		DatagramPacket receivedAcknowledgementDatagram = simulateReceiver(acknowledgementPacket);
		
		byte[] acknowledgmentDatagramData = acknowledgementDatagram.getData();
		byte[] shortenedReceivedAcknowledgementDatagram = TransportLayer.shortenDatagramPacket(receivedAcknowledgementDatagram.getData());
		
		assertEquals(Arrays.toString(acknowledgmentDatagramData), Arrays.toString(shortenedReceivedAcknowledgementDatagram));
	
		// Test an acknowledgement packet
		DatagramPacket encryptedMessageDatagram = encryptedMessagePacket.getDatagramPacket();
		DatagramPacket receivedEncryptedMessageDatagram = simulateReceiver(encryptedMessagePacket);
		
		byte[] encryptedMessageDatagramData = encryptedMessageDatagram.getData();
		byte[] shortenedReceivedEncryptedMessageDatagram = TransportLayer.shortenDatagramPacket(receivedEncryptedMessageDatagram.getData());
		
		assertEquals(Arrays.toString(encryptedMessageDatagramData), Arrays.toString(shortenedReceivedEncryptedMessageDatagram));
	
		// Test an acknowledgement packet
		DatagramPacket encryptionPairExchangeDatagram = encryptionPairExchangePacket.getDatagramPacket();
		DatagramPacket receivedencryptionPairExchangeDatagram = simulateReceiver(encryptionPairExchangePacket);
		
		byte[] encryptionPairExchangeDatagramData = encryptionPairExchangeDatagram.getData();
		byte[] shortenedReceivedEncryptionPairExchangeDatagram = TransportLayer.shortenDatagramPacket(receivedencryptionPairExchangeDatagram.getData());
		
		assertEquals(Arrays.toString(encryptionPairExchangeDatagramData), Arrays.toString(shortenedReceivedEncryptionPairExchangeDatagram));
	
	}

	@Test
	public void getPacketTest() {
		
		// Test pulse packet		
		byte[] pulsePacketData = pulsePacket.getDatagramPacketData();
		assertEquals(Arrays.toString(pulsePacket.getDatagramPacketData()), 
				Arrays.toString(TransportLayer.getPacket(pulsePacketData).getDatagramPacketData()));
		
		// Test	global message packet
		byte[] globalMessagePacketData = globalMessagePacket.getDatagramPacketData();
		assertEquals(Arrays.toString(globalMessagePacket.getDatagramPacketData()), 
				Arrays.toString(TransportLayer.getPacket(globalMessagePacketData).getDatagramPacketData()));
	
		// Test encrypted message packet
		byte[] encryptedMessagePacketData = encryptedMessagePacket.getDatagramPacketData();
		assertEquals(Arrays.toString(encryptedMessagePacket.getDatagramPacketData()), 
				Arrays.toString(TransportLayer.getPacket(encryptedMessagePacketData).getDatagramPacketData()));
		
		// Test encrypted message packet
		byte[] acknowledgementPacketData = acknowledgementPacket.getDatagramPacketData();
		assertEquals(Arrays.toString(acknowledgementPacket.getDatagramPacketData()), 
				Arrays.toString(TransportLayer.getPacket(acknowledgementPacketData).getDatagramPacketData()));
			
		// Test encrypted message packet
		byte[] encrpytionPairExchangePacketData = encryptionPairExchangePacket.getDatagramPacketData();
		assertEquals(Arrays.toString(encryptionPairExchangePacket.getDatagramPacketData()), 
				Arrays.toString(TransportLayer.getPacket(encrpytionPairExchangePacketData).getDatagramPacketData()));
		
	}
	
	@Test
	public void getPayloadTest() {
		assertEquals(pulse, (Pulse)pulsePacket.getPayload());
		assertEquals(globalMessage, (GlobalMessage)globalMessagePacket.getPayload());
		assertEquals(acknowledgement, (Acknowledgement)acknowledgementPacket.getPayload());
		assertEquals(encryptedMessage, (EncryptedMessage)encryptedMessagePacket.getPayload());
		assertEquals(encryptionPairExchange, (EncryptionPairExchange)encryptionPairExchangePacket.getPayload());		
	}

	@Test
	public void getSenderIDTest() {
		assertEquals(2, pulsePacket.getSenderID());
		assertEquals(3, globalMessagePacket.getSenderID());
		assertEquals(22, acknowledgementPacket.getSenderID());
		assertEquals(23, encryptedMessagePacket.getSenderID());
		assertEquals(53, encryptionPairExchangePacket.getSenderID());
		
	}

	@Test
	public void getReceiverIDTest() {
		assertEquals(5, pulsePacket.getReceiverID());
		assertEquals(33, globalMessagePacket.getReceiverID());
		assertEquals(11, acknowledgementPacket.getReceiverID());
		assertEquals(53, encryptedMessagePacket.getReceiverID());
		assertEquals(23, encryptionPairExchangePacket.getReceiverID());
	}

	@Test
	public void getSequenceNumberTest() {
		assertEquals(4, pulsePacket.getSequenceNumber());
		assertEquals(5, globalMessagePacket.getSequenceNumber());
		assertEquals(6, acknowledgementPacket.getSequenceNumber());
		assertEquals(7, encryptedMessagePacket.getSequenceNumber());
		assertEquals(8, encryptionPairExchangePacket.getSequenceNumber());
	}
	
	@Test
	public void getTypeIdentifierTest() {
		assertEquals(Payload.PULSE, pulsePacket.getTypeIdentifier());
		assertEquals(Payload.GLOBAL_MESSAGE, globalMessagePacket.getTypeIdentifier());
		assertEquals(Payload.ACKNOWLEDGEMENT, acknowledgementPacket.getTypeIdentifier());
		assertEquals(Payload.ENCRYPTED_MESSAGE, encryptedMessagePacket.getTypeIdentifier());
		assertEquals(Payload.ENCRYPTION_PAIR, encryptionPairExchangePacket.getTypeIdentifier());
	}
	
	@Test
	public void getLevelTest() {
		assertEquals(2, pulse.getLevel());
	}
	
	@Test
	public void getNameTest() {
		assertEquals("Bob", pulse.getName());
	}
	
	@Test
	public void getNameLengthTest() {
		assertEquals(3, pulse.getNameLength());
	}
	
	@Test
	public void getGlobalMessageTest() {
		assertEquals("Hey", globalMessage.getPlainText());
	}
	
	@Test
	public void getMessageIDTest() {
		assertEquals(1, globalMessage.getMessageID());
		assertEquals(3, acknowledgement.getMessageID());
	}
	
	@Test
	public void getMessageLengthTest() {
		assertEquals(3, globalMessage.getMessageLength());
	}

}
