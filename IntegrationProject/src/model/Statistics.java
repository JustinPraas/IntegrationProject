package model;

public class Statistics {

	int sessionTime,
		packetsForwarded,
		packetsIgnored,
		pulsesSent,
		privateMessagesSent,
		globalMessagesSent,
		securityMessagesSent,
		pulsesReceived,
		privateMessagesReceived,
		globalMessagesReceived,
		securityMessagesReceived;

	public int getTotalPacketsSent() {
		return getPacketsForwarded() + + getPulsesSent() + getPrivateMessagesSent() 
				+ getGlobalMessagesSent() + getSecurityMessagesSent();
	}
	
	public int getTotalPacketsReceived() {
		return getPacketsIgnored() + getPulsesReceived() + getPrivateMessagesReceived()
				+ getGlobalMessagesReceived() + getSecurityMessagesReceived();
	}
	
	public int getSessionTime() {
		return sessionTime;
	}

	public int getPacketsForwarded() {
		return packetsForwarded;
	}

	public int getPacketsIgnored() {
		return packetsIgnored;
	}

	public int getPulsesSent() {
		return pulsesSent;
	}

	public int getPrivateMessagesSent() {
		return privateMessagesSent;
	}

	public int getGlobalMessagesSent() {
		return globalMessagesSent;
	}

	public int getSecurityMessagesSent() {
		return securityMessagesSent;
	}

	public int getPulsesReceived() {
		return pulsesReceived;
	}

	public int getPrivateMessagesReceived() {
		return privateMessagesReceived;
	}

	public int getGlobalMessagesReceived() {
		return globalMessagesReceived;
	}

	public int getSecurityMessagesReceived() {
		return securityMessagesReceived;
	}


	
}
