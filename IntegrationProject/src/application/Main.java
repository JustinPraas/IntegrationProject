package application;

import model.Session;
import userinterface.GUIHandler;

/**
 * The main class that has the sole purpose of launching the chat application.
 * @author Justin Praas, Daan Kooij, Casper Plentinger, Tim van Brederode *
 */
public class Main {
	
	/**
	 * Launches the main application by creating a <code>GUIHandler</code>
	 * and a <code>Session</code>
	 * @param args unused arguments
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		GUIHandler GuiHandler = new GUIHandler("Come Tjetten");
		Session session = new Session(GUIHandler.getUsername());
		System.out.println("You are " + session.getName() + " (ID = " + session.getID()+ ")");
		GUIHandler.setSession(session);
	}
}
