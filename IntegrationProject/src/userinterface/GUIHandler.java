package userinterface;

import javafx.application.Platform;

public class GUIHandler {

	private static String applicationName;
	protected static String username;
		
	// Testing purposes
	public static void main(String[] args) {
		GUIHandler aa = new GUIHandler("Application");
		System.out.println(getUsername());
	}
	
	// Constructor to launch the GUI
	public GUIHandler(String name) {
		applicationName = name;
		GUIThread thread = new GUIThread();
		thread.start();
		sleep(500); // To prevent from the GUI being used before it is fully loaded
	}
	
	protected static String getApplicationName() {
		return applicationName;
	}
	
	// Inner class GUIThread
	private class GUIThread extends Thread {

		@Override
		public void run() {
			GUI.launchGUI();
		}
		
	}
	
	// Wait for ms milliseconds
	protected static void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	// TO BE CALLED BY OTHER PARTS OF THE PROGRAM
	public static String getUsername() {
		Platform.runLater(() -> {
			GUI.window.setScene(GUI.connectionScreen);
			GUI.window.show();
		});
		while (username == null) {
			sleep(100);
		}
		return username;
	}
	
}
