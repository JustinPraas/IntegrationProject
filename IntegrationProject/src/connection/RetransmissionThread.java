package connection;

import packet.Packet;

/**
 * A <code>Thread</code> extending class that handles the retransmission of an 
 * unacknowledged packet.
 * @author Justin Praas, Daan Kooij, Casper Plentinger, Tim van Brederode
 */
public class RetransmissionThread extends Thread {
	
	/**
	 * The packet to be retransmitted.
	 */
	private Packet packet;
	
	/**
	 * The transport layer to be used.
	 */
	private TransportLayer transportLayer;
	
	/**
	 * A counter that keeps track of the number of retransmissions done.
	 */
	private int retransmissionsDone = 0;
	
	/**
	 * A boolean that specifies whether this thread should be closed.
	 */
	private boolean finished;

	/**
	 * Constructs a <code>RetransmissionThread</code> object. Starts the Thread.
	 * @param transportLayer the transport layer to be used
	 * @param packet the packet that needs to be retransmitted.
	 */
	public RetransmissionThread(TransportLayer transportLayer, Packet packet) {
		this.packet = packet;
		this.transportLayer = transportLayer;
		this.finished = false;
		this.start();
	}
	
	/**
	 * Every <code>RETRANSMISSION_INTERVAL</code> milliseconds, checks if the packet
	 * still needs retransmission.
	 */
	@Override
	public void run() {
		while (retransmissionsDone < TransportLayer.MAXIMUM_RETRANSMISSIONS && !finished) {
			try {
				Thread.sleep(TransportLayer.RETRANSMISSION_INTERVAL);
				retransmit();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}
	
	/**
	 * Retransmits the packet (with an increased seqNum) if it's still unacknowledged.
	 * Increments the retransmissionsDone.
	 */
	private void retransmit() {
		if (isUnacknowledged()) {
			packet.setSequenceNum(transportLayer.session.getNextSeqNumber());
			transportLayer.session.getConnection().getSender().send(packet);
			retransmissionsDone++;
		} else {
			finished = true;
		}
	}
	
	/**
	 * Checks if the packet is still unacknowledged.
	 * @return true if the packet is unacknowledged, otherwise false
	 */
	public boolean isUnacknowledged() {
		synchronized (transportLayer.unacknowledgedPackets) {
			return transportLayer.unacknowledgedPackets.contains(packet);
		}
	}

}
