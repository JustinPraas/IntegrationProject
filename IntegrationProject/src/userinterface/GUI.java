package userinterface;

import java.io.File;
import java.util.Formatter;
import java.util.Locale;
import java.util.Optional;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jdk.management.resource.internal.inst.SocketOutputStreamRMHooks;
import model.Statistics;

public class GUI extends Application {

	protected static Stage window;
	protected static Scene connectionScreen;
	protected static Scene chatScreen;
	protected static VBox chatBox;
	protected static Label currentChatHeader;
	protected static Label levelLabel;
	protected static ProgressBar experienceProgressBar;
	protected static TextField inputBox;
	protected static VBox rightVBox;
	protected static ScrollPane scrollingChatBox;
	protected static Button globalChatButton;
	
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
		HBox headerArea = new HBox();
		headerArea.setSpacing(10);
		currentChatHeader = new Label();
		Label fillerLabel = new Label();
		Button statisticsButton = new Button("Stats");
		statisticsButton.setMaxHeight(Double.MAX_VALUE);
		initializeStatisticsWindow(statisticsButton);
		fillerLabel.setMaxWidth(Double.MAX_VALUE);
		levelLabel = new Label("Level 0");
		levelLabel.setFont(Font.font(null, FontWeight.NORMAL, 24));
		experienceProgressBar = new ProgressBar(0.0);
		experienceProgressBar.setMaxHeight(Double.MAX_VALUE);
		experienceProgressBar.setPrefWidth(200);
		HBox.setHgrow(fillerLabel, Priority.ALWAYS);
		headerArea.getChildren().addAll(currentChatHeader, fillerLabel, levelLabel, 
				experienceProgressBar, statisticsButton);
		
		chatBox = new VBox();
		chatBox.setSpacing(2);
		chatBox.setMaxWidth(920);
		chatBox.setPadding(new Insets(10, 10, 30, 10));
		currentChatHeader.setFont(Font.font(null, FontWeight.SEMI_BOLD, 24));
		scrollingChatBox = new ScrollPane();
		scrollingChatBox.setContent(chatBox);
		scrollingChatBox.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrollingChatBox.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		
		inputBox = new TextField();
		Button sendButton = new Button("Send");
		inputHBox.getChildren().addAll(inputBox, sendButton);
		inputHBox.setSpacing(10);
		leftVBox.getChildren().addAll(headerArea, scrollingChatBox, inputHBox);
		leftVBox.setSpacing(15);
		
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
		
	}
	
	private void initializeStatisticsWindow(Button statisticsButton) {
		
		statisticsButton.setOnAction(e -> {
			
			Alert statisticsWindow = new Alert(AlertType.INFORMATION);
			statisticsWindow.setTitle("Statistics");
			statisticsWindow.setHeaderText("Usage statistics");
			statisticsWindow.getDialogPane().autosize();
			statisticsWindow.getDialogPane().getStylesheets().add(getClass().getResource("Test.css").toExternalForm());
			statisticsWindow.getDialogPane().getStyleClass().add("statisticsScreen");
			
			Statistics stats = GUIHandler.session.getStatistics();
			
			String statisticsString = "";
			String format = "%1$20s %2$10s %3$10s\n";
			
			statisticsString += String.format(format, "Session time", "", 
					stats.getSessionTime());
			statisticsString += String.format(format, "Experience gained", "", 
					GUIHandler.session.getTotalExperience());
			statisticsString += String.format(format, "Packets forwarded", "", 
					stats.getPacketsForwarded()); // TODO
			statisticsString += String.format(format, "Packets ignored", "", 
					stats.getPacketsIgnored()); // TODO
			statisticsString += String.format(format, "", "", "");
			
			statisticsString += String.format(format, "", "Sent", "Received");
			statisticsString += String.format(format, "Packets", 
					stats.getTotalPacketsSent(), stats.getTotalPacketsReceived()); // TODO
			statisticsString += String.format(format, "Pulses", 
					stats.getPulsesSent(), stats.getPulsesReceived()); // TODO
			statisticsString += String.format(format, "Private messages", 
					stats.getPrivateMessagesSent(), stats.getPrivateMessagesReceived()); // TODO
			statisticsString += String.format(format, "Global messages", 
					stats.getGlobalMessagesSent(), stats.getGlobalMessagesReceived()); // TODO
			statisticsString += String.format(format, "Acknowledgements", 
					stats.getAcknowledgementsSent(), stats.getAcknowledgementsReceived()); // TODO
			statisticsString += String.format(format, "Security messages", 
					stats.getSecurityMessagesSent(), stats.getSecurityMessagesReceived()); // TODO
			
			statisticsWindow.setContentText(statisticsString);
			statisticsWindow.showAndWait();
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
