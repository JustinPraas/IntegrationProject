package application;

import userinterface.GUIHandler;

public class Main {

	public static void main(String[] args) {
		new GUIHandler("Come Tjetten");
		Session session = new Session(GUIHandler.getUsername());
		
	}
}
