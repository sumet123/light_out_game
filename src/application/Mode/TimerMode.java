package application.Mode;

import java.util.Random;

import application.Main;
import application.Button.BackButton;
import application.Button.GameMenuButton;
import application.Button.ToMainMenuButton;
import application.GameLogic.Board;
import application.GameMenu.TimerGameMenu;
import application.PassLevel.ClassicPassLevel;
import application.PassLevel.TimerPassLevel;
import application.PlayerData.PlayerInfo;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class TimerMode extends Mode
{

	private class PauseMenu extends VBox {
		
		protected GameMenuButton restartButton;
		protected GameMenuButton resumeButton;
		protected BackButton backButton;
		protected Label pauseMenuLabel;
		protected VBox vBox = new VBox(120);
		protected HBox hBox = new HBox(30);
		
		public PauseMenu() {
			super(10);
			setAlignment(Pos.CENTER);
			vBox.setAlignment(Pos.CENTER);
			vBox.setPrefHeight(640);
			setPadding(new Insets(50,50,50,50));

			resumeButton = new GameMenuButton(100,100,"pause.png");
			resumeButton.setTooltip(new Tooltip("Resume"));
			restartButton = new GameMenuButton(100,100,"reset.png");
			restartButton.setTooltip(new Tooltip("Restart\n(Unsaved progress will be lost)"));
			backButton = new BackButton(100, 120, 41);
			backButton.setTooltip(new Tooltip("Back to Mode Selection\n(Unsaved progress will be lost)"));
			pauseMenuLabel = new Label("PAUSE MENU");
			pauseMenuLabel.setPrefWidth(1180);
			pauseMenuLabel.setPrefHeight(100);
			pauseMenuLabel.setAlignment(Pos.CENTER);
			pauseMenuLabel.setStyle("-fx-font-size: 48px; -fx-font-family:\"Arial Black\"; -fx-background-color: #FFD728;");
			 
			hBox.getChildren().addAll(resumeButton, restartButton, backButton);
			vBox.getChildren().addAll(pauseMenuLabel, hBox);
			
			hBox.setAlignment(Pos.CENTER);
			
			vBox.setAlignment(Pos.TOP_CENTER);
			vBox.setPadding(new Insets(100,0,0,0));
			vBox.setStyle("-fx-background-color: #FFED9F; -fx-border-color: #FFD728; -fx-border-width: 4px;");
			
			getChildren().add(vBox);
		}
	};
	
	private int n;
	private int passedLevel;
	private int level;
	private int timeLeft;
	private int[] pressed;
	private Thread timerThread;
	private Thread passedLevelThread;
	private PauseMenu pauseMenu;

	public TimerMode(int level, int time, int penalty, int passedLevel, int[] tmp)
	{
		mode = 1;
		passedLevelThread = new Thread(() -> {
			gameMenu.setDisable(true);
			for(int i = 0; i < (n+4)*(n+4); i++)
			{
				try
				{
					board.changeColor(i/(n+4), i%(n+4), false);;
					Thread.sleep(3000/((n+4)*(n+4)*2));
					board.changeColor(i/(n+4), i%(n+4), false);;
					Thread.sleep(3000/((n+4)*(n+4)*2));
					// System.out.println(timeLeft);
				}
				catch (InterruptedException e)
				{
					System.out.println("Stop Timer Thread");
					break;
				}
			}
			gameMenu.setDisable(false);
			javafx.application.Platform.runLater(new Runnable() {
	            @Override public void run() {
	                toNextLevel();
	            }
	        });
		});
		this.passedLevel = passedLevel;
		this.timeLeft = time;
		this.level = level;
		this.n = (level - 1) / 5;
		board = new Board(n + 4, 2, level, 1);
		gameMenu = new TimerGameMenu(0,level);
		gameMenu.addPenalty(penalty);
		passLevel = new TimerPassLevel(gameMenu.getPenalty(), passedLevel);
		pauseMenu = new PauseMenu();
		setPauseButton(((TimerGameMenu) gameMenu).getPauseButton());
		setResumeButton(pauseMenu.resumeButton);
		setRestartButton(pauseMenu.restartButton);
		setRestartButton(passLevel.getRestartButton());
		((TimerGameMenu) gameMenu).setPassedLevelLabel(passedLevel);
		setNewPuzzleButton(((TimerGameMenu) gameMenu).getNewPuzzleButton());
		setResetButton(gameMenu.getResetButton());
		setUndoButton(gameMenu.getUndoButton());
		if (tmp == null)
		{
			System.out.println("gen");
			tmp = new int[2 * n + 3 + (level - 1) % 5];
			Random rand = new Random();
			for (int i = 0; i < tmp.length; i++)
			{
				boolean chk = false;
				while (!chk)
				{
					boolean chk2 = false;
					int t = rand.nextInt((n + 4) * (n + 4));
					for (int j = 0; j < tmp.length; j++)
					{
						if (tmp[j] == t)
						{
							chk2 = true;
							break;
						}
					}
					if (!chk2)
					{
						tmp[i] = t;
						System.out.println(t);
						chk = true;
					}
				}
			}
		}
		pressed = tmp;
		for (int i = 0; i < pressed.length; i++)
		{
			board.changeColor(pressed[i] / (n + 4), pressed[i] % (n + 4), true);
		}
		for (int i = 0; i < n + 4; i++)
		{
			for (int j = 0; j < n + 4; j++)
			{
				board.getLight(i, j).setOnMouseClicked(mouseClick);
			}
		}
		hBox.getChildren().addAll(board, gameMenu);

		timerThread = new Thread(() -> {
			while (true && Main.pane instanceof TimerMode)
			{
				try
				{
					((TimerGameMenu) gameMenu).setTimeLeft(timeLeft);
					((TimerGameMenu) gameMenu).drawCurrentTimeString(((TimerGameMenu) gameMenu).getGc());
					Thread.sleep(1000);
					timeLeft--;
					if(timeLeft == 0) {
						javafx.application.Platform.runLater(new Runnable() {
				            @Override public void run() {
				                showPassLevel();
				                setPenalty();
				                timerThread.interrupt();
				            }
				        });
						break;
					}
					//System.out.println(timeLeft);
				}
				catch (InterruptedException e)
				{
					System.out.println("Stop Timer Thread");
					break;
				}
			}
		});
		timerThread.start();
	}
	
	private void setResetButton(Button resetButton)
	{
		resetButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent arg0)
			{
				resetBoard();
			}
		});
	}

	private void setNewPuzzleButton(Button newPuzzleButton)
	{
		newPuzzleButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent arg0)
			{
				timerThread.interrupt();
				TimerMode timerMode = new TimerMode(level, timeLeft, gameMenu.getPenalty() + 200, passedLevel, null);
				Main.changeScene(timerMode);
			}
		});
	}
	
	private void setResumeButton(Button resumeButton)
	{
		resumeButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@SuppressWarnings("deprecation")
			@Override
			public void handle(ActionEvent arg0)
			{
				getChildren().remove(pauseMenu);
				timerThread.resume();
				passedLevelThread.resume();
			}
		});
	}
	
	private void setPauseButton(Button pauseButton) {
		pauseButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@SuppressWarnings("deprecation")
			@Override
			public void handle(ActionEvent arg0)
			{
				getChildren().add(pauseMenu);
				timerThread.suspend();
				passedLevelThread.suspend();
			}
		});
	}

	@Override
	protected void toNextLevel()
	{
		// TODO Auto-generated method stub
		TimerMode nextLevel = new TimerMode(level + 1, timeLeft + 15, gameMenu.getPenalty(), passedLevel + 1, null);
		Main.changeScene(nextLevel);
	}

	@Override
	protected void setRestartButton(Button restartButton) {
		
		restartButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent arg0)
			{
				timerThread.interrupt();
				TimerMode timerMode = new TimerMode(1, 60, 0, 0, null);
				Main.changeScene(timerMode);
			}
		});
	}

	@Override
	protected void setPenalty()
	{
		PlayerInfo.setTimerPassedLevel(passedLevel);
		PlayerInfo.setTimerPenalty(gameMenu.getPenalty());
		return;
	}
	
	@Override
	public void timerNextLevel() {
		timerThread.interrupt();
		for (int i = 0; i < n + 4; i++)
		{
			for (int j = 0; j < n + 4; j++)
			{
				board.getLight(i, j).setOnMouseClicked(null);
			}
		}
		passedLevelThread.start();
	}

	@Override
	protected void resetBoard() {
		timerThread.interrupt();
		TimerMode timerMode = new TimerMode(level, timeLeft, gameMenu.getPenalty(), passedLevel, pressed);
		Main.changeScene(timerMode);
	}
}