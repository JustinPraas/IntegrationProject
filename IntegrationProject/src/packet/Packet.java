package packet;

import java.net.DatagramPacket;
import java.util.Arrays;

public class Packet {

	private int sequenceNumber;
	private byte identifier;
	private Payload payload;

	public Packet(int sequenceNumber, int identifier, Payload payload) {
		this.sequenceNumber = sequenceNumber;
		this.identifier = (byte) identifier;
		this.payload = payload;
	}
	
	public DatagramPacket getDatagramPacket() {
		byte[] packet = new byte[3 + payload.getPayload().length];
		System.arraycopy(payload.getPayload(), 0, packet, 3, payload.getPayload().length);
		packet[0] = (byte) (sequenceNumber >> 8);
		packet[1] = (byte) sequenceNumber;
		packet[2] = identifier;
		
		return new DatagramPacket(packet, packet.length);		
	}
	
	public static void main(String[] args) {
		Pulse p = new Pulse(2, "Justin");
		Packet packet = new Packet (12, 0, p);
		System.out.println(Arrays.toString(p.getPayload()));
		System.out.println(Arrays.toString(packet.getDatagramPacket().getData()));
	}
}
