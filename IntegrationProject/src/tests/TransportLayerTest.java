package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import connection.TransportLayer;
import packet.*;

public class TransportLayerTest {
	
	Payload pulse;
	Payload encryptedMessage;
	Payload acknowledgement;
	Packet pulsePacket;
	Packet encrMsgPacket;
	Packet ackPacket;
	
	@Before
	public void setUp() {
		pulse = new Pulse("Bob");
		encryptedMessage = new EncryptedMessage(20, "This is a test");
		acknowledgement = new Acknowledgement(20);
		pulsePacket = new Packet(1, 2, 1, 0, pulse);
		encrMsgPacket = new Packet(2, 1, 2 , 1, encryptedMessage);
		ackPacket = new Packet(4, 3, 4, 2, acknowledgement);
	}
	
	@Test
	public void getReceiverIDTest() {
		assertEquals(2, TransportLayer.getReceiverID(pulsePacket.getDatagramPacketData()));
		assertEquals(1, TransportLayer.getReceiverID(encrMsgPacket.getDatagramPacketData()));
		assertEquals(3, TransportLayer.getReceiverID(ackPacket.getDatagramPacketData()));
	}
	
	@Test
	public void getSenderIDTest() {
		assertEquals(1, TransportLayer.getSenderID(pulsePacket.getDatagramPacketData()));
		assertEquals(2, TransportLayer.getSenderID(encrMsgPacket.getDatagramPacketData()));
		assertEquals(4, TransportLayer.getSenderID(ackPacket.getDatagramPacketData()));
	}
	
	@Test
	public void getSequenceNumberTest() {
		assertEquals(1, TransportLayer.getSequenceNumber(pulsePacket.getDatagramPacketData()));
		assertEquals(2, TransportLayer.getSequenceNumber(encrMsgPacket.getDatagramPacketData()));
		assertEquals(4, TransportLayer.getSequenceNumber(ackPacket.getDatagramPacketData()));
	}
	
	@Test
	public void getTypeIdentifiefTest() {
		assertEquals(0, TransportLayer.getTypeIdentifier(pulsePacket.getDatagramPacketData()));
		assertEquals(1, TransportLayer.getTypeIdentifier(encrMsgPacket.getDatagramPacketData()));
		assertEquals(2, TransportLayer.getTypeIdentifier(ackPacket.getDatagramPacketData()));
	}
	
	@Test
	public void getNameTest() {
		assertEquals("Bob", TransportLayer.getName(pulsePacket.getDatagramPacket().getData()));
	}
	
	@Test
	public void getMessageIDTest() {
		assertEquals(0, TransportLayer.getMessageID(pulsePacket.getDatagramPacket().getData()));
		assertEquals(1, TransportLayer.getMessageID(encrMsgPacket.getDatagramPacket().getData()));
		assertEquals(2, TransportLayer.getMessageID(ackPacket.getDatagramPacket().getData()));
	}
}
