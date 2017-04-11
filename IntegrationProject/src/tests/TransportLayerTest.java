package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import connection.TransportLayer;
import packet.*;

public class TransportLayerTest {
	
	Payload pulse = new Pulse(20, "Bob");
	Payload encryptedMessage = new EncryptedMessage(20, 10, 321, "This is a test");
	Payload acknowledgement = new Acknowledgement(20,  10, 321);
	
	Packet pulsePacket = new Packet(300, 0, pulse);
	Packet encrMsgPacket = new Packet(400, 1, encryptedMessage);
	Packet ackPacket = new Packet(500, 2, acknowledgement);
	
	@Test
	public void getReceiverIDTest() {
		assertEquals(20, TransportLayer.getReceiverID(pulse.getPayload()));
		assertEquals(20, TransportLayer.getReceiverID(encrMsgPacket.getDatagramPacket().getData()));
		assertEquals(20, TransportLayer.getReceiverID(ackPacket.getDatagramPacket().getData()));
	}
}
