package application;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public abstract class GameMenu extends VBox
{
	protected Button logo = new Button("BUMP");
	protected Text penaltyText = new Text("Penalty");
	protected static Label penaltyLabel = new Label("0");
	protected ToMainMenuButton toMainMenuButton = new ToMainMenuButton();
	
	public GameMenu()
	{
		super(10);
		setAlignment(Pos.CENTER);
		setPrefWidth(540);
	}
}
