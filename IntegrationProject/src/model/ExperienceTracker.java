package model;

public class ExperienceTracker {
	
	public static final int BASE_EXP_REQUIRED = 10;
	public static final int EXP_SEND = 3;
	public static final int EXP_RECEIVE_PRIVATE = 4;
	public static final int EXP_RECEIVE_GLOBAL = 2;
	public static final int EXP_FORWARD = 1;
	
	private int currentLevel = 0;
	private int currentExperience = 0;
	
	public int getExperienceRequired(int level) {
		if (level > 1) {
			return BASE_EXP_REQUIRED * level + getExperienceRequired(level - 1);
		} else {
			return BASE_EXP_REQUIRED;
		}
	}
	
	public int getExperienceRequiredNextLevel() {
		return getExperienceRequired(getCurrentLevel() + 1);
	}
	
	public int getCurrentLevel() {
		return currentLevel;
	}
	
	public int getNextLevel() {
		return getCurrentLevel() + 1;
	}
	
	public int getCurrentExperience() {
		return currentExperience;
	}
	
	public void checkLevelIncrease() {
		while (currentExperience >= getExperienceRequiredNextLevel()) {
			currentExperience -= getExperienceRequiredNextLevel();
			currentLevel += 1;
		}
	}
	
	public double getLevelProgress() {
		return (double) getCurrentExperience() / getExperienceRequiredNextLevel();
	}
	
	public int getTotalExperience() {
		int total = getCurrentExperience();
		for (int i = getCurrentLevel(); i > 0; i--) {
			total += getExperienceRequired(i);
		}
		return total;
	}
	
	public void sendMessage() {
		currentExperience += EXP_SEND;
		checkLevelIncrease();
	}
	
	public void receivePrivateMessage() {
		currentExperience += EXP_RECEIVE_PRIVATE;
		checkLevelIncrease();
	}
	
	public void receiveGlobalMessage() {
		currentExperience += EXP_RECEIVE_GLOBAL;
		checkLevelIncrease();
	}
	
	public void forwardMessage() {
		currentExperience += EXP_FORWARD;
		checkLevelIncrease();
	}

}
