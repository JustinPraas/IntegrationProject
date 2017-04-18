package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import connection.TransportLayer;
import packet.*;

public class TransportLayerTest {
	
	Pulse pulse;
	GlobalMessage globalMessage;
	Acknowledgement acknowledgement;
	
	Packet pulsePacket;
	Packet ackPacket;
	
	@Before
	public void setUp() {		
		pulse = new Pulse(3, "Bob");
		globalMessage = new GlobalMessage(20, 5, "Hello");
		acknowledgement = new Acknowledgement(20);
		
		pulsePacket = new Packet(1, 2, 1, 0, pulse);
		ackPacket = new Packet(4, 3, 4, 2, acknowledgement);
		
	}
	
	@Test
	public void getSenderIDTest() {
		assertEquals(1, TransportLayer.getSenderID(pulsePacket.getDatagramPacketData()));
		assertEquals(4, TransportLayer.getSenderID(ackPacket.getDatagramPacketData()));
		assertEquals(1, pulsePacket.getSenderID());
		assertEquals(4, ackPacket.getSenderID());
	}
	
	@Test
	public void getReceiverIDTest() {
		assertEquals(2, TransportLayer.getReceiverID(pulsePacket.getDatagramPacketData()));
		assertEquals(3, TransportLayer.getReceiverID(ackPacket.getDatagramPacketData()));
		assertEquals(2, pulsePacket.getReceiverID());
		assertEquals(3, ackPacket.getReceiverID());
	}
	
	@Test
	public void getSequenceNumberTest() {
		assertEquals(1, TransportLayer.getSequenceNumber(pulsePacket.getDatagramPacketData()));
		assertEquals(4, TransportLayer.getSequenceNumber(ackPacket.getDatagramPacketData()));
		assertEquals(1, pulsePacket.getSequenceNumber());
		assertEquals(4, ackPacket.getSequenceNumber());		
	}
	
	@Test
	public void getTypeIdentifiefTest() {
		assertEquals(0, TransportLayer.getTypeIdentifier(pulsePacket.getDatagramPacketData()));
		assertEquals(2, TransportLayer.getTypeIdentifier(ackPacket.getDatagramPacketData()));
		assertEquals(0, pulsePacket.getTypeIdentifier());
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
		assertEquals(20, TransportLayer.getMessageID(ackPacket.getPayload().getPayloadData(), Payload.ACKNOWLEDGEMENT));
		assertEquals(20, acknowledgement.getMessageID());
		
	}
	
	@Test
	public void getMessageLengthTest() {
		assertEquals(5, TransportLayer.getMessageLength(globalMessage.getPayloadData(), Payload.GLOBAL_MESSAGE));
	}
}
