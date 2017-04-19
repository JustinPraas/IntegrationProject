package model;

/**
 * A class that stores this application session's statistics.
 * @author Justin Praas, Daan Kooij, Casper Plentinger, Tim van Brederode
 */
public class Statistics {

	/**
	 * The variables that this application keeps track of.
	 */
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

	/**
	 * Returns the sum of the fields that are considered trackers of packets sent.
	 * @return totalPacketsSent the number of packets sent by this application
	 */
	public int getTotalPacketsSent() {
		return getPacketsForwarded() + + getRetransmissionsDone() + getPulsesSent() 
				+ getPrivateMessagesSent() + getGlobalMessagesSent() 
				+ getAcknowledgementsSent() + getSecurityMessagesSent();
	}
	
	/**
	 * Returns the sum of the fields that are considered trackers of packets received.
	 * @return totalPacketsReceived the number of packets received by this application
	 */
	public int getTotalPacketsReceived() {
		return getPacketsIgnored() + getPulsesReceived() + getPrivateMessagesReceived()
				+ getGlobalMessagesReceived() + getAcknowledgementsReceived()
				+ getSecurityMessagesReceived();
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

	/**
	 * Increments the sessionTime field.
	 */
	public void increaseSessionTime() {
		sessionTime++;
	}
	
	/**
	 * Increments the packetsForwarded field.
	 */
	public void increasePacketsForwarded() {
		packetsForwarded++;
	}
	
	/**
	 * Increments the packetsIgnored field.
	 */
	public void increasePacketsIgnored() {
		packetsIgnored++;
	}
	
	/**
	 * Increments the retransmissionsDone field.
	 */
	public void increaseRetransmissionsDone() {
		retransmissionsDone++;
	}
	
	/**
	 * Increments the pulsesSent field.
	 */
	public void increasePulsesSent() {
		pulsesSent++;
	}
	
	/**
	 * Increments the privateMessagesSent field.
	 */
	public void increasePrivateMessagesSent() {
		privateMessagesSent++;
	}
	
	/**
	 * Increments the globalMessagesSent field.
	 */
	public void increaseGlobalMessagesSent() {
		globalMessagesSent++;
	}
	
	/**
	 * Increments the acknowledgementsSent field.
	 */
	public void increaseAcknowlegdementsSent() {
		acknowledgementsSent++;
	}
	
	/**
	 * Increments the securitymessagesSent field.
	 */
	public void increaseSecurityMessagesSent() {
		securityMessagesSent++;
	}
	
	/**
	 * Increments the pulsesReceived field.
	 */
	public void increasePulsesReceived() {
		pulsesReceived++;
	}
	
	/**
	 * Increments the privateMessagesReceived field.
	 */
	public void increasePrivateMessagesReceived() {
		privateMessagesReceived++;
	}
	
	/**
	 * Increments the globalMessagesReceived field.
	 */
	public void increaseGlobalMessagesReceived() {
		globalMessagesReceived++;
	}
	
	/**
	 * Increments the acknowledgementsReceived field.
	 */
	public void increaseAcknowlegdementsReceived() {
		acknowledgementsReceived++;
	}
	
	/**
	 * Increments the securityMessagesReceived field.
	 */
	public void increaseSecurityMessagesReceived() {
		securityMessagesReceived++;
	}
	
}
