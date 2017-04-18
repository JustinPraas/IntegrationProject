package userinterface;

import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import model.Message;
import model.Person;
import model.Session;
import packet.FileMessage;

public class GUIHandler {

	private static String applicationName;
	private static Session session;
	protected static String username;
	private static HashMap<Button, Person> buttonToPerson;
	private static HashMap<Person, Button> personToButton;
	private static HashMap<Person, String> textBoxText;
	private static HashMap<Person, Boolean> personUnreadMessages;
	private static Person currentPerson;
	private static String globalTextBoxText;
	private static boolean unreadGlobalChatMessages;
	
	// Constructor to launch the GUI
	public GUIHandler(String name) {
		applicationName = name;
		buttonToPerson = new HashMap<>();
		textBoxText = new HashMap<>();
		personUnreadMessages = new HashMap<>();
		GUIThread thread = new GUIThread();
		unreadGlobalChatMessages = false;
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
	
	// Send message
	protected static void sendMessage(String msg) {
		if (!msg.equals("")) {
			if (currentPerson != null) {
				Person receiver = currentPerson;
				session.getConnection().getTransportLayer().sendMessageFromGUI(msg, receiver);
			} else {
				session.getConnection().getTransportLayer().sendMessageFromGUI(msg);
			}
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
		if (currentPerson != null) {
			textBoxText.put(currentPerson, GUI.inputBox.getText());
		} else {
			globalTextBoxText = GUI.inputBox.getText();
		}
		
		// Initialize empty label for message inside HBox
		Label label;
		
		// Initialize string for final message
		String finalMessage;
		
		// Initialize list with all final messages
		ArrayList<HBox> total = new ArrayList<>();
		
		// Iterate of all the messages with person (argument)
		for (int i = 0; i < messages.size(); i++) {
			
			// Set message to currently being processed message
			Message message = messages.get(i);
			
			// Set sender of message to actual sender
			String messageSender;
			if (message.getSenderID() == session.getID()) {
				messageSender = username;
			} else {
				messageSender = person.getName();
			}
			if (message.getText().startsWith(FileMessage.FILE_IDENTIFIER)) {
				
				Text senderText = new Text(messageSender);
				senderText.getStyleClass().add("sender");
				Text timestampText = new Text(" (" + message.getTimestampString() + "): ");
				ByteArrayInputStream inputStream = new ByteArrayInputStream(message.getFileData());
				Image image = new Image(inputStream);
				ImageView view = new ImageView(image);
				view.setFitWidth(100);
				view.setPreserveRatio(true);
				view.setCache(true);
				TextFlow flow = new TextFlow(senderText, timestampText, view);
				if (message.getSenderID() == session.getID()) {
					flow.getStyleClass().add("local");
				} else {
					flow.getStyleClass().add("remote");
				}
				HBox box = new HBox(flow);
				total.add(box);
				
			} else {
				TextFlow flow;
				Text senderText = new Text(messageSender);
				senderText.getStyleClass().add("sender");
				Text timestampText = new Text(" (" + message.getTimestampString() + "): ");
				
				// Append this message to ChatBox String
				Text messageText = new Text(message.getText());
				
				flow = new TextFlow();
				flow.getChildren().addAll(senderText, timestampText, messageText);
				
				HBox box = new HBox();
				if (message.getSenderID() == session.getID()) {
					flow.getStyleClass().add("local");
				} else {
					flow.getStyleClass().add("remote");
				}
				box.getChildren().add(flow);
				
				total.add(box);
			}
		}
		
		// Set the chatbox to all the hbox elements
		Platform.runLater(() -> {
			GUI.chatBox.getChildren().clear();
			for (HBox message : total) {
					GUI.chatBox.getChildren().add(message);
			}
			GUI.scrollingChatBox.setVvalue(1);
		});
		
		// Set Header Label
		Platform.runLater(() -> {
			GUI.currentChatHeader.setText("Chat with " + person.getName() + " " 
					+ "(ID = " + person.getID() + ")");
		});
		
		
		// Set TextBox text (if exists)
		if (currentPerson != person) {
			if (textBoxText.containsKey(person)) {
				GUI.inputBox.setText(textBoxText.get(person));
			} else {
				GUI.inputBox.setText("");
			}
		}
		
		// Set current person
		currentPerson = person;
		
		// Store link between Person object and Button object
		personUnreadMessages.put(person, false);
		
		personToButton.get(person).setFont(Font.font(null, FontWeight.NORMAL, 14.5));
	}
	
	// Show Global Chat
	protected static void showChat() {
		// To prevent the session from being accessed before it is initialized
		while (session == null) {
			sleep(100);
		}
		
		// Retrieve the public chat messages
		ArrayList<Message> messages = session.getPublicChatMessages();
		
		// Store TextBox text
		if (currentPerson != null) {
			textBoxText.put(currentPerson, GUI.inputBox.getText());
		} else {
			globalTextBoxText = GUI.inputBox.getText();
		}
		
		// Initialize list with all final messages
		ArrayList<HBox> total = new ArrayList<>();
		
		// Iterate of all the messages with person (argument)
		for (int i = 0; i < messages.size(); i++) {
			
			// Set message to currently being processed message
			Message message = messages.get(i);
			
			// Get the name of the sender of the message
			String messageSenderName = "";
			if (message.getSenderID() == session.getID()) {
				messageSenderName = username;
			} else {
				messageSenderName = session.getKnownPersons().get(message.getSenderID()).getName();
			}
			
			// Append this message to ChatBox String
			Text senderNameText = new Text(messageSenderName);
			Text timestampText = new Text(" (" + message.getTimestampString() + "): ");
			Text messageText = new Text(message.getText());
			senderNameText.getStyleClass().add("sender");
			TextFlow flow = new TextFlow();
			flow.getChildren().addAll(senderNameText, timestampText, messageText);
			HBox chatBoxEntry = new HBox();
			if (message.getSenderID() == session.getID()) {
				flow.getStyleClass().add("local");
			} else {
				flow.getStyleClass().add("remote");
			}
			chatBoxEntry.getChildren().add(flow);			
			total.add(chatBoxEntry);
		}
		
		// Set the chatbox to all the hbox elements
		Platform.runLater(() -> {
			GUI.chatBox.getChildren().clear();
			for (HBox boxEntry : total) {
					GUI.chatBox.getChildren().add(boxEntry);
			}
			GUI.scrollingChatBox.setVvalue(1);
		});
		
		// Set Header Label
		Platform.runLater(() -> {
			GUI.currentChatHeader.setText("Global Chat");
		});
		
		// Set TextBox text (if exists)
		if (currentPerson != null) {
			GUI.inputBox.setText(globalTextBoxText);
		}
		
		// Set current person
		currentPerson = null;
		
		// Mark Global Chat as read
		unreadGlobalChatMessages = false;
		GUI.globalChatButton.setFont(Font.font(null, FontWeight.NORMAL, 14.5));
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
		
		// Add Global Chat Button
		GUI.globalChatButton = new Button("GLOBAL");
		GUI.globalChatButton.setFont(Font.font(null, FontWeight.NORMAL, 14.5));
		GUI.globalChatButton.setTextFill(Color.BLUE);
		GUI.globalChatButton.setOnAction(e -> {
			GUIHandler.showChat();
		});
		
		// Let the button fill the width of the right sidebar
		GUI.globalChatButton.setMaxWidth(Double.MAX_VALUE);
		GUI.globalChatButton.setMinHeight(100);
		GUI.globalChatButton.setMaxHeight(100);
		vb.getChildren().add(GUI.globalChatButton);
		
		// Check if global chat button should me marked as unread
		if (unreadGlobalChatMessages) {
			GUI.globalChatButton.setFont(Font.font(null, FontWeight.BOLD, 14.5));
		}
		
		
		// Iterate over all the known Person objects to create Buttons for them
		for (Map.Entry<Integer, Person> entry : session.getKnownPersons().entrySet()) {
			Person person = entry.getValue();
			
			Button button = new Button(person.getName());
			
			// Check if the Button should be marked as containing unread messages
			if (personUnreadMessages.containsKey(person) && personUnreadMessages.get(person) 
					&& currentPerson != person) {
				button.setFont(Font.font(null, FontWeight.BOLD, 14.5));
			} else {
				button.setFont(Font.font(null, FontWeight.NORMAL, 14.5));
			}
			
			// Mark Person as online or offline
			if (person.getTimeToLive() > 0) {
				button.setTextFill(Color.GREEN);
			} else {
				button.setTextFill(Color.RED);
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
			VBox.setVgrow(scrollingNearbyList, Priority.ALWAYS);
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
			personToButton.get(person).setFont(Font.font(null, FontWeight.BOLD, 14.5));
		}
	}
	
	// To be called when a message is put in the Global Chat Messages list of session
	public static void messagePutInMap() {
		// Check if the Global chat is currently opened
		if (currentPerson == null) {
			showChat();
		} else {
			// If not, mark chat with the global chat as containing unread messages
			unreadGlobalChatMessages = true;
			GUI.globalChatButton.setFont(Font.font(null, FontWeight.BOLD, 14.5));
		}
	}


	public static void sendFile(File file) {
		if (file != null && file.exists() && file.length() <= 1000000) {
			if (currentPerson != null) {
				Person receiver = currentPerson;
				try {
				session.getConnection().getTransportLayer().sendFileFromGUI(file, receiver);
				} catch (IOException e) {
					System.out.println("Couldn't send file");
				}
			}
		}
	}
	
}
