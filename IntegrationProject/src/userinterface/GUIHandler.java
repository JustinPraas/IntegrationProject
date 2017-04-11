package userinterface;

import java.util.ArrayList;
import java.util.HashMap;

import application.Session;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Message;
import model.Person;

public class GUIHandler {

	private static String applicationName;
	private static Session session;
	protected static String username;
	private static HashMap<Button, Person> buttonToPerson;
	private static HashMap<Person, Button> personToButton;
	private static HashMap<Person, String> textBoxText;
	private static HashMap<Person, Boolean> personUnreadMessages;
	private static Person currentPerson;
	
	// Constructor to launch the GUI
	public GUIHandler(String name) {
		applicationName = name;
		buttonToPerson = new HashMap<>();
		textBoxText = new HashMap<>();
		personUnreadMessages = new HashMap<>();
		GUIThread thread = new GUIThread();
		thread.start();
		sleep(1000); // To prevent from the GUI being used before it is fully loaded
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
	
	
	// TO BE CALLED BY GUI
	
	// Get application name
	protected static String getApplicationName() {
		return applicationName;
	}
	
	// Send message TODO
	protected static void sendMessage(String msg) {
		Person receiver = currentPerson;
		if (currentPerson != null && !msg.equals("")) {
			System.out.println("Message to " + currentPerson.getName() + ": " + msg);
		}
	}
	
	// Show certain chat based on Button object
	protected static void showChat(Button button) {
		showChat(buttonToPerson.get(button));
	}
	
	// Show certain chat based on Person object
	protected static void showChat(Person person) {
		
		// Put chat messages with person (argument) in list
		// If no messages with person, initialize empty list
		ArrayList<Message> messages;
		if (session.getChatMessages().containsKey(person)) {
			messages = session.getChatMessages().get(person);
		} else {
			messages = new ArrayList<>();
		}
		
		// Store TextBox text
		textBoxText.put(currentPerson, GUI.inputBox.getText());
		
		// Initialize empty ChatBox String
		String chatBoxString = "";
		
		// Iterate of all the messages with person (argument)
		for (int i = 0; i < messages.size(); i++) {
			
			// Set message to currently being processed message
			Message message = messages.get(i);
			
			// Set sender of message to actual sender
			String messageSender;
			if (message.getMessageID() == session.getID()) {
				messageSender = username;
			} else {
				messageSender = person.getName();
			}
			
			// Append this message to ChatBox String
			chatBoxString += messageSender + " (" + message.getTimestampString()
					+ "): " + message.getText() + "\n";
		}
		
		// Set chatBoxStringFinal to chatBoxString
		// Because otherwise Platform.runLater() does not work
		String chatBoxStringFinal = chatBoxString;
		
		// Set chatBox label to chatBoxStringFinal
		Platform.runLater(() -> {
			GUI.chatBox.setText(new String(chatBoxStringFinal));
		});
		
		// Set Header Label
		GUI.currentChatHeader.setText("Chat with " + person.getName() + " " 
				+ "(ID = " + person.getID() + ")");
		
		// Set TextBox text (if exists)
		if (textBoxText.containsKey(person)) {
			GUI.inputBox.setText(textBoxText.get(person));
		} else {
			GUI.inputBox.setText("");
		}
		
		// Set current person
		currentPerson = person;
		
		// Store link between Person object and Button object
		personUnreadMessages.put(person, false);
		personToButton.get(person).setFont(Font.font(null, FontWeight.NORMAL, 13));
	}
	
	
	// TO BE CALLED BY OTHER PARTS OF THE PROGRAM

	// Ask for user name
	public static String getUsername() {
		Platform.runLater(() -> {
			GUI.window.setScene(GUI.connectionScreen);
			GUI.window.show();
		});
		// Block until user entered name
		while (username == null) {
			sleep(100);
		}
		return username;
	}
	
	// Set session
	public static void setSession(Session sess) {
		session = sess;
	}
	
	// To be called when the Person list of Session is changed
	public static void changedPersonList() {
		// Create new maps to link Person objects to Button objects
		// To prevent issues with this being executed while the GUI is not updated yet
		HashMap<Button, Person> newButtonToPerson = new HashMap<>();
		HashMap<Person, Button> newPersonToButton = new HashMap<>();
		
		// Create new VBox for the Buttons to be
		VBox vb = new VBox(0);
		
		// Iterate over all the known Person objects to create Buttons for them
		for (Person person : session.getKnownPersons()) {
			Button button = new Button(person.getName());
			
			// Check if the Button should be marked as containing unread messages
			if (personUnreadMessages.containsKey(person) && personUnreadMessages.get(person) 
					&& currentPerson != person) {
				button.setFont(Font.font(null, FontWeight.BOLD, 13));
			}
			
			// Put the link between the Person object and the Button object in the new map
			newButtonToPerson.put(button, person);
			newPersonToButton.put(person, button);
			
			// Let the button fill the width of the right sidebar
			button.setMaxWidth(Double.MAX_VALUE);
			button.setMinHeight(100);
			button.setMaxHeight(100);
			
			// Add the Button to the newly created VBox
			vb.getChildren().add(button);
			
			// Set the action of the Button
			button.setOnAction(e -> {
				showChat(person);
			});
		}
		
		ScrollPane scrollingNearbyList = new ScrollPane();
		scrollingNearbyList.setContent(vb);
		scrollingNearbyList.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrollingNearbyList.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollingNearbyList.setFitToWidth(true);
		
		// Remove old VBox element from the GUI and add new VBox element to the GUI
		// As a result, the user side bar of the GUI is updated
		Platform.runLater(() -> {
			GUI.rightVBox.getChildren().remove(1);
			GUI.rightVBox.getChildren().add(scrollingNearbyList);
			GUI.rightVBox.setVgrow(scrollingNearbyList, Priority.ALWAYS);
		});
		
		// Set maps that link Buttons to Persons to the newly created maps
		personToButton = newPersonToButton;
		buttonToPerson = newButtonToPerson;
	}
	
	// To be called when a message is put in the map of Session
	public static void messagePutInMap(Person person) {
		// Check if the chat with this Person is currently opened
		if (currentPerson == person) {
			// If so, update the current screen
			showChat(person);
		} else {
			// If not, mark chat with this Person as containing unread messages
			personUnreadMessages.put(person, true);
			personToButton.get(person).setFont(Font.font(null, FontWeight.BOLD, 13));
		}
	}
	
}