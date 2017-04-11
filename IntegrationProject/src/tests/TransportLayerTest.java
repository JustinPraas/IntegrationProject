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
		pulsePacket = new Packet(1, 2, 3, pulse);
		encrMsgPacket = new Packet(400, 1, encryptedMessage);
		ackPacket = new Packet(500, 2, acknowledgement);
	}
	
	
	
	@Test
	public void getReceiverIDTest() {
		assertEquals(20, TransportLayer.getReceiverID(pulse.getPayloadData()));
		assertEquals(20, TransportLayer.getReceiverID(encrMsgPacket.getDatagramPacket().getData()));
		assertEquals(20, TransportLayer.getReceiverID(ackPacket.getDatagramPacket().getData()));
	}
}
