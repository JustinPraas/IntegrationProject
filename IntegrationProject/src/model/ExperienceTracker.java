package model;

/**
 * A class that keeps track of the client's experience and level in this application 
 * session.
 * @author Justin Praas, Daan Kooij, Casper Plentinger, Tim van Brederode
 *
 */
public class ExperienceTracker {
	
	/**
	 * Initial experience points required to reach level 1.
	 */
	public static final int BASE_EXP_REQUIRED = 10;
	
	/**
	 * The experience points gained from sending a message, both private and global.
	 */
	public static final int EXP_SEND = 3;
	
	/**
	 * The experience points gained for receiving a private message.
	 */
	public static final int EXP_RECEIVE_PRIVATE = 4;
	
	/**
	 * The experience points gained from receiving a global message.
	 */
	public static final int EXP_RECEIVE_GLOBAL = 2;
	
	/**
	 * The experience points gained from forwarding a message.
	 */
	public static final int EXP_FORWARD = 1;
	
	/**
	 * Keeps track of the user's current level.
	 */
	private int currentLevel = 0;
	
	/**
	 * Keeps track of the user's current experience points.
	 */
	private int currentExperience = 0;
	
	/**
	 * Returns the required amount of experience points to reach the given level.
	 * @param level the level for which the required experience points are needed
	 * @return requiredExp the required experience points to reach the given level
	 */
	public int getExperienceRequired(int level) {
		if (level > 1) {
			return BASE_EXP_REQUIRED * level + getExperienceRequired(level - 1);
		} else {
			return BASE_EXP_REQUIRED;
		}
	}
	
	/**
	 * Returns the required amount of experience points to reach the next level.
	 * @return requiredExp the required experience points to reach the next level
	 */
	public int getExperienceRequiredNextLevel() {
		return getExperienceRequired(getCurrentLevel() + 1);
	}
	
	/**
	 * Returns the user's level + 1.
	 * @return nextLevel the user's next level
	 */
	public int getNextLevel() {
		return getCurrentLevel() + 1;
	}
	
	/**
	 * Checks if the user has enough experience points to progress to the next level.
	 */
	public void checkLevelIncrease() {
		while (currentExperience >= getExperienceRequiredNextLevel()) {
			currentExperience -= getExperienceRequiredNextLevel();
			currentLevel += 1;
		}
	}
	
	/**
	 * Returns the ratio of current experience points to the required experience points.
	 * @return levelProgress the level progress from 0 to 1
	 */
	public double getLevelProgress() {
		return (double) getCurrentExperience() / getExperienceRequiredNextLevel();
	}
	
	/**
	 * Returns the total amount of experience points gained in this client's session.
	 * @return total the total amount of experience points gained
	 */
	public int getTotalExperience() {
		int total = getCurrentExperience();
		for (int i = getCurrentLevel(); i > 0; i--) {
			total += getExperienceRequired(i);
		}
		return total;
	}
	
	public int getCurrentLevel() {
		return currentLevel;
	}
	
	public int getCurrentExperience() {
		return currentExperience;
	}
	
	/**
	 * Increases the user's current experience points for sending a message.
	 */
	public void sendMessage() {
		currentExperience += EXP_SEND;
		checkLevelIncrease();
	}
	
	/**
	 * Increases the user's current experience points for receiving a private message.
	 */
	public void receivePrivateMessage() {
		currentExperience += EXP_RECEIVE_PRIVATE;
		checkLevelIncrease();
	}
	
	/**
	 * Increases the user's current experience points for receiving a global message.
	 */
	public void receiveGlobalMessage() {
		currentExperience += EXP_RECEIVE_GLOBAL;
		checkLevelIncrease();
	}
	
	/**
	 * Increases the user's current experience points for forwarding a message/packet.
	 */
	public void forwardMessage() {
		currentExperience += EXP_FORWARD;
		checkLevelIncrease();
	}

}
