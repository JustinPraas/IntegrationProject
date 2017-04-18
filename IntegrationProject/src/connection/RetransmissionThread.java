package connection;

import packet.Packet;

public class RetransmissionThread extends Thread {
	
	private Packet packet;
	private TransportLayer transportLayer;
	private int retransmissionsDone = 0;
	private boolean finished;

	public RetransmissionThread(TransportLayer transportLayer, Packet packet) {
		this.packet = packet;
		this.transportLayer = transportLayer;
		this.finished = false;
		this.start();
	}
	
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
	
	private void retransmit() {
		if (isUnacknowledged()) {
			packet.setSequenceNum(transportLayer.session.getNextSeqNumber());
			transportLayer.session.getConnection().getSender().send(packet);
			retransmissionsDone++;
		} else {
			finished = true;
		}
	}
	
	public boolean isUnacknowledged() {
		synchronized (transportLayer.unacknowledgedPackets) {
			return transportLayer.unacknowledgedPackets.contains(packet);
		}
	}

}
