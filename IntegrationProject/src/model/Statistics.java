package model;

public class Statistics {

	int sessionTime,
		packetsForwarded,
		packetsIgnored,
		retransmissionsDone,
		pulsesSent,
		privateMessagesSent,
		globalMessagesSent,
		acknowledgementsSent,
		securityMessagesSent,
		pulsesReceived,
		privateMessagesReceived,
		globalMessagesReceived,
		acknowledgementsReceived,
		securityMessagesReceived;

	// GET SUMS OF FIELDS
	public int getTotalPacketsSent() {
		return getPacketsForwarded() + + getRetransmissionsDone() + getPulsesSent() 
				+ getPrivateMessagesSent() + getGlobalMessagesSent() 
				+ getAcknowledgementsSent() + getSecurityMessagesSent();
	}
	
	public int getTotalPacketsReceived() {
		return getPacketsIgnored() + getPulsesReceived() + getPrivateMessagesReceived()
				+ getGlobalMessagesReceived() + getAcknowledgementsReceived()
				+ getSecurityMessagesReceived();
	}
	
	// GET FIELDS
	public int getSessionTime() {
		return sessionTime;
	}

	public int getPacketsForwarded() {
		return packetsForwarded;
	}

	public int getPacketsIgnored() {
		return packetsIgnored;
	}
	
	public int getRetransmissionsDone() {
		return retransmissionsDone;
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
	
	public int getAcknowledgementsSent() {
		return acknowledgementsSent;
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
	
	public int getAcknowledgementsReceived() {
		return acknowledgementsReceived;
	}

	public int getSecurityMessagesReceived() {
		return securityMessagesReceived;
	}

	// INCREASE FIELDS
	public void increaseSessionTime() {
		sessionTime++;
	}
	
	public void increasePacketsForwarded() {
		packetsForwarded++;
	}
	
	public void increasePacketsIgnored() {
		packetsIgnored++;
	}
	
	public void increaseRetransmissionsDone() {
		retransmissionsDone++;
	}
	
	public void increasePulsesSent() {
		pulsesSent++;
	}
	
	public void increasePrivateMessagesSent() {
		privateMessagesSent++;
	}
	
	public void increaseGlobalMessagesSent() {
		globalMessagesSent++;
	}
	
	public void increaseAcknowlegdementsSent() {
		acknowledgementsSent++;
	}
	
	public void increaseSecurityMessagesSent() {
		securityMessagesSent++;
	}
	
	public void increasePulsesReceived() {
		pulsesReceived++;
	}
	
	public void increasePrivateMessagesReceived() {
		privateMessagesReceived++;
	}
	
	public void increaseGlobalMessagesReceived() {
		globalMessagesReceived++;
	}
	
	public void increaseAcknowlegdementsReceived() {
		acknowledgementsReceived++;
	}
	
	public void increaseSecurityMessagesReceived() {
		securityMessagesReceived++;
	}
	
}
