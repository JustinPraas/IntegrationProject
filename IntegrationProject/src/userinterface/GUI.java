package userinterface;

import java.util.Optional;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class GUI extends Application {

	protected static Stage window;
	protected static Scene connectionScreen;
	protected static Scene chatScreen;
	protected static Label chatBox;
	protected static Label currentChatHeader;
	protected static TextField inputBox;
	protected static VBox rightVBox;
	
	int i = 0;
	
	protected static void launchGUI() {
		launch();
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		window = primaryStage;
		// Initialize Stage
		window.setTitle(GUIHandler.getApplicationName());
		window.setResizable(false);
		
		// Initialize things (some for later use)
		setProgramExitClick();
		initializeConnectionScreen();
		initializeChatScreen();
	}

	private void initializeConnectionScreen() {
		// Initialize VBox
		VBox vb = new VBox();
		vb.setPadding(new Insets(20, 20, 20, 20));
		vb.setSpacing(10);
		vb.setAlignment(Pos.CENTER);
		
		// VBox element 1: Label with welcome text
		Label welcomeText = new Label("Welcome to " + GUIHandler.getApplicationName() + "! "
				+ "Please enter a username to get started.");
		welcomeText.setWrapText(true);
		
		// VBox element 2: GridPane
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		
		// GridPane elements
		Label usernameText = new Label("Username:");
		TextField usernameInput = new TextField();
		Button connectButton = new Button("Connect");
		
		// GridPane positioning
		GridPane.setConstraints(usernameText, 0, 0);
		GridPane.setConstraints(usernameInput, 1, 0);
		GridPane.setConstraints(connectButton, 1, 1);
		
		// Add elements to GridPane
		grid.getChildren().addAll(usernameText, usernameInput, connectButton);
		
		// Add elements to VBox
		vb.getChildren().addAll(welcomeText, grid);
		
		// Set action of Connect Button and return press
		connectButton.setOnAction(e -> {
			validateUsername(usernameInput.getText());
		});
		usernameInput.setOnAction(e -> {
			validateUsername(usernameInput.getText());
		});
		
		// Build the Scene
		connectionScreen = new Scene(vb, 320, 180);
	}
	
	private void initializeChatScreen() {
		
		// Initialize HBoxes and VBoxes
		HBox outerHBox = new HBox();
		VBox leftVBox = new VBox();
		rightVBox = new VBox();
		HBox inputHBox = new HBox();
		outerHBox.setPadding(new Insets(20, 20, 20, 20));
		outerHBox.setSpacing(20);
		
		// Initialize elements of left VBox
		currentChatHeader = new Label();
		chatBox = new Label();
		currentChatHeader.setFont(Font.font(null, FontWeight.SEMI_BOLD, 24));
		ScrollPane scrollingChatBox = new ScrollPane();
		scrollingChatBox.setContent(chatBox);
		scrollingChatBox.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrollingChatBox.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		
		inputBox = new TextField();
		Button sendButton = new Button("Send");
		inputHBox.getChildren().addAll(inputBox, sendButton);
		inputHBox.setSpacing(10);
		leftVBox.getChildren().addAll(currentChatHeader, scrollingChatBox, inputHBox);
		leftVBox.setSpacing(15);
		
		// Initialize elements of right VBox
		Label personListHeader = new Label("Nearby");
		
		ScrollPane scrollingNearbyList = new ScrollPane();
		scrollingNearbyList.setContent(rightVBox);
		scrollingNearbyList.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrollingNearbyList.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		
		personListHeader.setFont(Font.font(null, FontWeight.SEMI_BOLD, 24));
		VBox nearbyPersonsVBox = new VBox(15);
		rightVBox.getChildren().addAll(personListHeader, nearbyPersonsVBox);
		rightVBox.setSpacing(15);
		
		// Set filling properties
		VBox.setVgrow(scrollingChatBox, Priority.ALWAYS);
		HBox.setHgrow(inputBox, Priority.ALWAYS);
		
		// Set size of left VBox and right VBox
		leftVBox.setMinWidth(930); // 960 minus padding and spacing (30)
		leftVBox.setMaxWidth(930);
		rightVBox.setMinWidth(290); // 320 minus padding and spacing (30)
		rightVBox.setMaxWidth(290);
		
		// Add left VBox and right VBox to outer HBox
		outerHBox.getChildren().addAll(leftVBox, rightVBox);

		// Construct Scene
		chatScreen = new Scene(outerHBox, 1280, 720);
		
		// Set actions
		inputBox.setOnAction(e -> {
			GUIHandler.sendMessage(inputBox.getText());
			inputBox.clear();
		});
		sendButton.setOnAction(e -> {
			GUIHandler.sendMessage(inputBox.getText());
			inputBox.clear();
		});
	}
	
	// Pass on user name if not empty, otherwise give warning
	private void validateUsername(String input) {
		if (input.equals("")) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Invalid input");
			alert.setHeaderText("Empty username");
			alert.setContentText("Please enter a valid, non-empty username.");
			alert.showAndWait();
		} else {
			GUIHandler.username = input;
			window.hide();
			window.setScene(chatScreen);
			window.show();
		}
	}
	
	// Set GUI exit confirmation
	private void setProgramExitClick() {
		window.setOnCloseRequest(e -> {
			Alert exitQuestion = new Alert(AlertType.CONFIRMATION);
			exitQuestion.setTitle("Close " + GUIHandler.getApplicationName());
			exitQuestion.setHeaderText("Are you sure you want to close " 
					+ GUIHandler.getApplicationName() + "?");
			exitQuestion.setContentText("By closing " + GUIHandler.getApplicationName()
					+ ", the current session will end.");
			Optional<ButtonType> answer = exitQuestion.showAndWait();
			if (answer.get() == ButtonType.OK) {
				window.close();
			} else {
				e.consume();
			}
		});
	}
	
}