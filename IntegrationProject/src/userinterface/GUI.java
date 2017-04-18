package userinterface;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.imageio.ImageIO;
import javax.swing.JTextPane;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class GUI extends Application {

	protected static Stage window;
	protected static Scene connectionScreen;
	protected static Scene chatScreen;
	protected static VBox chatBox;
	protected static Label currentChatHeader;
	protected static TextField inputBox;
	protected static VBox rightVBox;
	protected static ScrollPane scrollingChatBox;
	protected static Button globalChatButton;
	protected static Map<String, File> myEmoticons;
	
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
		BorderPane inputHBox = new BorderPane();
		outerHBox.setPadding(new Insets(20, 20, 20, 20));
		outerHBox.setSpacing(20);
		
		// Initialize elements of left VBox
		currentChatHeader = new Label();
		
		
		chatBox = new VBox();
		chatBox.setSpacing(2);
		chatBox.setMaxWidth(920);
		chatBox.setPadding(new Insets(10, 10, 30, 10));
		currentChatHeader.setFont(Font.font(null, FontWeight.SEMI_BOLD, 24));
		scrollingChatBox = new ScrollPane();
		scrollingChatBox.setContent(chatBox);
		scrollingChatBox.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrollingChatBox.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		
		HBox bottomBox = new HBox();
		bottomBox.setSpacing(10);
		inputBox = new TextField();
		Button sendButton = new Button("Send");
		
		inputHBox.setBottom(bottomBox);
		leftVBox.getChildren().addAll(currentChatHeader, scrollingChatBox, inputHBox);
		leftVBox.setSpacing(15);
		
		File dir = new File("emoticons/");
		File[] directoryListing = dir.listFiles(
	            (directory, name) -> {
	                return name.toLowerCase().endsWith(".png");
	            }
	    );
		myEmoticons = new HashMap<>();
		Arrays.sort(directoryListing);
		FlowPane topBox = new FlowPane();
		topBox.setPadding(new Insets(0, 0, 5, 0));
		Button emoticons = new Button("");
		if (directoryListing != null && directoryListing.length > 0) {
		  for (File child : directoryListing) {
			  ImageView ImgVwtemp = new ImageView(child.toURI().toString());
			  ImgVwtemp.setFitWidth(18);
			  ImgVwtemp.setPreserveRatio(true);
			  ImgVwtemp.setSmooth(true);
			  Button temp = new Button("", ImgVwtemp);
			  String fileName = child.getName();
          	  String displayName = fileName.substring(fileName.indexOf("_")+1, fileName.indexOf("."));
          	  myEmoticons.put(displayName, child);
		      temp.setOnAction(new EventHandler<ActionEvent>() {
		            @Override public void handle(ActionEvent e) {
		            	inputBox.appendText("::" + displayName + "::");
		            }
		        });
			  topBox.getChildren().add(temp);
		  }
		  emoticons = new Button("", new ImageView(directoryListing[0].toURI().toString()));
		  bottomBox.getChildren().addAll(inputBox, emoticons, sendButton);
		} else {
		  bottomBox.getChildren().addAll(inputBox, sendButton);
		}

		// Initialize elements of right VBox
		Label personListHeader = new Label("Nearby");
		
		ScrollPane scrollingNearbyList = new ScrollPane();
		scrollingNearbyList.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrollingNearbyList.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollingNearbyList.setFitToWidth(true);
		
		personListHeader.setFont(Font.font(null, FontWeight.SEMI_BOLD, 24));
		VBox nearbyPersonsVBox = new VBox(0);
		
		// Add Global Chat Button
		globalChatButton = new Button("GLOBAL");
		globalChatButton.setFont(Font.font(null, FontWeight.NORMAL, 14.5));
		globalChatButton.setTextFill(Color.BLUE);
		globalChatButton.setOnAction(e -> {
			GUIHandler.showChat();
		});
		
		// Let the button fill the width of the right sidebar
		globalChatButton.setMaxWidth(Double.MAX_VALUE);
		globalChatButton.setMinHeight(100);
		globalChatButton.setMaxHeight(100);
		
		nearbyPersonsVBox.getChildren().add(globalChatButton);
		scrollingNearbyList.setContent(nearbyPersonsVBox);
		
		rightVBox.getChildren().addAll(personListHeader, scrollingNearbyList);
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
		chatScreen.getStylesheets().add(getClass().getResource("Test.css").toExternalForm());
		
		// Set actions
		inputBox.setOnAction(e -> {
			GUIHandler.sendMessage(inputBox.getText());
			inputBox.clear();
		});
		sendButton.setOnAction(e -> {
			GUIHandler.sendMessage(inputBox.getText());
			inputBox.clear();
		});
		emoticons.setOnAction(e -> Platform.runLater(() -> {
		if (inputHBox.getChildren().contains(topBox)) {
			inputHBox.setTop(null);
		} else {
			inputHBox.setTop(topBox);
		}}));
	}
	
	// Pass on user name if not empty, otherwise give warning
	private void validateUsername(String input) {
		if (input.equals("")) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Invalid input");
			alert.setHeaderText("Empty username");
			alert.setContentText("Please enter a valid, non-empty username.");
			alert.showAndWait();
		} else if (input.length() > 20) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Invalid input");
			alert.setHeaderText("Username too long");
			alert.setContentText("Please enter a shorter username.");
			alert.showAndWait();
		} else {
			GUIHandler.username = input;
			window.hide();
			window.setScene(chatScreen);
			window.setTitle(GUIHandler.username + " - " + GUIHandler.getApplicationName());
			GUIHandler.showChat();
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
				System.exit(0);
			} else {
				e.consume();
			}
		});
	}
	
}
