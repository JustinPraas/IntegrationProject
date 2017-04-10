package userinterface;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GUI extends Application {

	protected static Stage window;
	protected static Scene connectionScreen;
	protected static Scene chatScreen;
	
	protected static void launchGUI() {
		launch();
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		window = primaryStage;
		// Initialize Stage
		window.setTitle(GUIHandler.getApplicationName());
		window.setResizable(false);
		
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
		
		// Set action of Connect Button
		connectButton.setOnAction(e -> {
			if (usernameInput.getText().equals("")) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Invalid input");
				alert.setHeaderText("Empty username");
				alert.setContentText("Please enter a valid, non-empty username.");
				alert.showAndWait();
			} else {
				GUIHandler.username = usernameInput.getText();
				window.hide();
				window.setScene(chatScreen);
				window.show();
			}
		});
		
		// Build the Scene
		connectionScreen = new Scene(vb, 320, 240);
	}
	
	private void initializeChatScreen() {
		Button button = new Button("Test");
		chatScreen = new Scene(button, 800, 600);
	}
	
}
