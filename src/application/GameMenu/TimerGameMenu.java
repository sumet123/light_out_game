package application.GameMenu;

import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TimerGameMenu extends GameMenu
{
	private Button newPuzzleButton;
	private Button pauseButton;
	private Label passedLevelLabel;
	private Canvas canvas;
	private int timeLeft;
	private GraphicsContext gc;
	
	public TimerGameMenu(int penalty,int level)
	{
		super(level);
		super.penalty = penalty;
		canvas = new Canvas(70, 50);
		gc = canvas.getGraphicsContext2D();
		newPuzzleButton = new Button("New");
		pauseButton = new Button("Pause");
		Text timeLeftText = new Text("Time left");
		Text passedLevelText = new Text("Completed");
		passedLevelLabel = new Label("0");
		GridPane gridPane = new GridPane();
		gridPane.add(timeLeftText, 0, 0);
		gridPane.add(canvas, 0, 1);
		gridPane.add(passedLevelText, 1, 0);
		gridPane.add(passedLevelLabel, 1, 1);
		gridPane.add(penaltyLabel, 2, 0);
		gridPane.setHgap(40);
		gridPane.setVgap(10);
		gridPane.setAlignment(Pos.CENTER);
		getChildren().addAll(levelLabel, gridPane, newPuzzleButton, resetButton, undoButton, pauseButton, toMainMenuButton);
	}
	
	public Button getNewPuzzleButton() {
		return newPuzzleButton;
	}
	
	public void setTimeLeft(int timeLeft) {
		this.timeLeft = timeLeft;
	}
	
	public void setPassedLevelLabel(int passedLevel) {
		passedLevelLabel.setText(""+passedLevel);
	}
	
	public Button getPauseButton() {
		return pauseButton;
	}
	
	public GraphicsContext getGc() {
		return gc;
	}
	public void drawCurrentTimeString(GraphicsContext gc){
		gc.setFill(Color.BLACK);
		gc.setFont(new Font(40));
		gc.clearRect(0, 0, this.canvas.getWidth(), this.canvas.getHeight());
		gc.fillText("" + this.timeLeft, 0, this.canvas.getWidth() / 2);
	}
}