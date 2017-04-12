package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import connection.TransportLayer;
import packet.*;

public class TransportLayerTest {
	
	Pulse pulse;
	EncryptedMessage encryptedMessage;
	Acknowledgement acknowledgement;
	
	Packet pulsePacket;
	Packet encrMsgPacket;
	Packet ackPacket;
	
	@Before
	public void setUp() {
		pulse = new Pulse(3, "Bob");
		encryptedMessage = new EncryptedMessage(20, 14, "This is a test");
		acknowledgement = new Acknowledgement(20);
		
		pulsePacket = new Packet(1, 2, 1, 0, pulse);
		encrMsgPacket = new Packet(2, 1, 2 , 1, encryptedMessage);
		ackPacket = new Packet(4, 3, 4, 2, acknowledgement);
	}
	
	@Test
	public void getSenderIDTest() {
		assertEquals(1, TransportLayer.getSenderID(pulsePacket.getDatagramPacketData()));
		assertEquals(2, TransportLayer.getSenderID(encrMsgPacket.getDatagramPacketData()));
		assertEquals(4, TransportLayer.getSenderID(ackPacket.getDatagramPacketData()));
		assertEquals(1, pulsePacket.getSenderID());
		assertEquals(2, encrMsgPacket.getSenderID());
		assertEquals(4, ackPacket.getSenderID());
	}
	
	@Test
	public void getReceiverIDTest() {
		assertEquals(2, TransportLayer.getReceiverID(pulsePacket.getDatagramPacketData()));
		assertEquals(1, TransportLayer.getReceiverID(encrMsgPacket.getDatagramPacketData()));
		assertEquals(3, TransportLayer.getReceiverID(ackPacket.getDatagramPacketData()));
		assertEquals(2, pulsePacket.getReceiverID());
		assertEquals(1, encrMsgPacket.getReceiverID());
		assertEquals(3, ackPacket.getReceiverID());
	}
	
	@Test
	public void getSequenceNumberTest() {
		assertEquals(1, TransportLayer.getSequenceNumber(pulsePacket.getDatagramPacketData()));
		assertEquals(2, TransportLayer.getSequenceNumber(encrMsgPacket.getDatagramPacketData()));
		assertEquals(4, TransportLayer.getSequenceNumber(ackPacket.getDatagramPacketData()));
		assertEquals(1, pulsePacket.getSequenceNumber());
		assertEquals(2, encrMsgPacket.getSequenceNumber());
		assertEquals(4, ackPacket.getSequenceNumber());		
	}
	
	@Test
	public void getTypeIdentifiefTest() {
		assertEquals(0, TransportLayer.getTypeIdentifier(pulsePacket.getDatagramPacketData()));
		assertEquals(1, TransportLayer.getTypeIdentifier(encrMsgPacket.getDatagramPacketData()));
		assertEquals(2, TransportLayer.getTypeIdentifier(ackPacket.getDatagramPacketData()));
		assertEquals(0, pulsePacket.getTypeIdentifier());
		assertEquals(1, encrMsgPacket.getTypeIdentifier());
		assertEquals(2, ackPacket.getTypeIdentifier());		
	}
	
	@Test
	public void getNameLengthTest() {
		assertEquals(3, TransportLayer.getNameLength(pulsePacket.getPayload().getPayloadData()));
		assertEquals(3, pulse.getNameLength());
	}
	@Test
	public void getNameTest() {
		assertEquals("Bob", TransportLayer.getName(pulsePacket.getPayload().getPayloadData()));
		assertEquals("Bob", pulse.getName()); 
	}
	
	@Test
	public void getMessageIDTest() {
		assertEquals(20, TransportLayer.getMessageID(encrMsgPacket.getPayload().getPayloadData()));
		assertEquals(20, TransportLayer.getMessageID(ackPacket.getPayload().getPayloadData()));
		assertEquals(20, encryptedMessage.getMessageID());
		assertEquals(20, acknowledgement.getMessageID());
		
	}
	
	@Test
	public void getMessageLengthTest() {
		assertEquals(14, TransportLayer.getMessageLength(encrMsgPacket.getPayload().getPayloadData(), 1));
		assertEquals(14, encryptedMessage.getMessageLength());
	}
	
	@Test
	public void getEncryptedMessageTest() {
		assertEquals("This is a test", TransportLayer.getPlainMessage(encrMsgPacket.getPayload().getPayloadData()));
		assertEquals("This is a test", encryptedMessage.getEncryptedMessage());
	}
}
