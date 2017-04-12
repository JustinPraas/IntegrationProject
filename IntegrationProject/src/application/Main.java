package application;

import userinterface.GUIHandler;

public class Main {
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {		
		GUIHandler GuiHandler = new GUIHandler("Come Tjetten");
		Session session = new Session(GUIHandler.getUsername());
		System.err.println("You are " + session.getName());
		GUIHandler.setSession(session);
	}
}
