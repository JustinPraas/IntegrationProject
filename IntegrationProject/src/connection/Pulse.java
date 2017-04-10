package connection;

public class Pulse extends Thread {
	
	public Connection connection;
	public Packet pkt;
	
	public Pulse(Connection c) {
		connection = c;
		
	}
	
	public void run() {
		while (!connection.sendSocket.isClosed()) {
			pulse();
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void pulse() {
		//TODO
	}
}
