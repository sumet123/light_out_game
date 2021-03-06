package application.PlayerData;

import application.Main;
import application.MainMenu;
import application.StartMenu;
import application.Button.BackButton;
import application.Button.OKButton;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class CreateNewPlayer extends VBox {
	public CreateNewPlayer() {
		super(50);
		setStyle("-fx-background-color: #7DA3A1");

		VBox upVBox = new VBox();
		upVBox.setAlignment(Pos.CENTER);
		upVBox.setStyle("-fx-background-color: #324851");
		Label upLabel = new Label("Create New Player");
		upLabel.setStyle("-fx-font-size: 64px; -fx-font-family:\"Arial Black\";");
		upLabel.setTextFill(Color.web("#B7B8B6"));
		upLabel.setPrefHeight(120);
		upVBox.getChildren().add(upLabel);

		VBox nameVBox = new VBox(20);
		Label nameLabel = new Label("What's your name?");
		nameLabel.setStyle("-fx-font-size: 48px; -fx-font-family:\"Arial Black\";");
		nameLabel.setTextFill(Color.web("#FFFFFF"));
		TextField nameField = new TextField();
		nameField.setPrefHeight(50);
		nameField.setFont(Font.font("Verdana", FontWeight.BOLD, 50));
		nameVBox.setPadding(new Insets(80, 350, 80, 350));
		nameVBox.setAlignment(Pos.CENTER);
		nameVBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-background-radius: 10;");
		nameVBox.getChildren().addAll(nameLabel, nameField);

		OKButton okButton = new OKButton(100, 120);
		BackButton toStartMenuButton = new BackButton(100, 120, new StartMenu());

		HBox buttonHbox = new HBox(20);
		buttonHbox.setAlignment(Pos.CENTER);
		buttonHbox.getChildren().addAll(toStartMenuButton, okButton);
		this.getChildren().addAll(upVBox, nameVBox, buttonHbox);

		okButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				OKButton.playSoundEffect();
				try {
					PlayerInfo playerInfo;
					playerInfo = new PlayerInfo(nameField.getText());
					PlayerInfo.setSelectedPlayerInfo(playerInfo);
					MainMenu mainMenu = new MainMenu();
					Main.changeScene(mainMenu);
				} catch (BadNameException e) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Invalid name");
					alert.setHeaderText("The name you entered is invalid!");
					alert.setContentText(e.getMessage());
					alert.showAndWait();
				}
			}
		});
	}
}
