package application.Mode;

import java.util.Random;

import application.Main;
import application.Button.BackButton;
import application.Button.GameMenuButton;
import application.GameLogic.Board;
import application.GameMenu.TimerGameMenu;
import application.PassLevel.TimerPassLevel;
import application.PlayerData.PlayerInfo;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TimerMode extends Mode {

	private class PauseMenu extends VBox {

		protected GameMenuButton restartButton;
		protected GameMenuButton resumeButton;

		public PauseMenu() {
			super(10);
			setAlignment(Pos.CENTER);
			setPadding(new Insets(50, 50, 50, 50));

			Label pauseMenuLabel = new Label("PAUSE MENU");
			pauseMenuLabel.setPrefWidth(1180);
			pauseMenuLabel.setPrefHeight(100);
			pauseMenuLabel.setAlignment(Pos.CENTER);
			pauseMenuLabel
					.setStyle("-fx-font-size: 48px; -fx-font-family:\"Arial Black\"; -fx-background-color: #FFD728;");

			resumeButton = new GameMenuButton(100, 100, "next.png");
			resumeButton.setTooltip(new Tooltip("Resume"));

			restartButton = new GameMenuButton(100, 100, "reset.png");
			restartButton.setTooltip(new Tooltip("Restart\n(Unsaved progress will be lost)"));

			BackButton backButton = new BackButton(100, 120, new ModeSelection());
			backButton.setTooltip(new Tooltip("Back to Mode Selection\n(Unsaved progress will be lost)"));

			HBox buttonHBox = new HBox(30);
			buttonHBox.setAlignment(Pos.CENTER);
			buttonHBox.getChildren().addAll(backButton, restartButton, resumeButton);

			VBox pauseMenuVBox = new VBox(120);
			pauseMenuVBox.setAlignment(Pos.TOP_CENTER);
			pauseMenuVBox.setPadding(new Insets(100, 0, 0, 0));
			pauseMenuVBox.setPrefHeight(640);
			pauseMenuVBox.setStyle("-fx-background-color: #FFED9F; -fx-border-color: #FFD728; -fx-border-width: 4px;");
			pauseMenuVBox.getChildren().addAll(pauseMenuLabel, buttonHBox);

			getChildren().add(pauseMenuVBox);
		}
	};

	private int passedLevel;
	private int level;
	private int timeLeft;
	private int[] pressed;
	private Thread timerThread;
	private Thread passedLevelThread;
	private PauseMenu pauseMenu;

	public TimerMode(int level, int time, int penalty, int passedLevel, int[] tmp) {
		mode = 1;
		timeLeft = time;
		this.passedLevel = passedLevel;
		this.level = level;
		int n = (level - 1) / 5 + 4;
		board = new Board(n, 2, level, 1);
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				board.getLight(i, j).setOnMouseClicked(mouseClick);
			}
		}
		if (tmp == null) {
			tmp = new int[2 * (n - 4) + 3 + (level - 1) % 5];
			Random rand = new Random();
			for (int i = 0; i < tmp.length; i++) {
				boolean chk = false;
				while (!chk) {
					boolean chk2 = false;
					int t = rand.nextInt((n) * (n));
					for (int j = 0; j < tmp.length; j++) {
						if (tmp[j] == t) {
							chk2 = true;
							break;
						}
					}
					if (!chk2) {
						tmp[i] = t;
						chk = true;
					}
				}
			}
		}
		pressed = tmp;
		for (int i = 0; i < pressed.length; i++) {
			board.changeColor(pressed[i] / (n), pressed[i] % (n), true);
		}
		gameMenu = new TimerGameMenu(0, level);
		((TimerGameMenu) gameMenu).setPassedLevelLabel(passedLevel);
		gameMenu.addPenalty(penalty);
		setNewPuzzleButton(((TimerGameMenu) gameMenu).getNewPuzzleButton());
		setResetButton(gameMenu.getResetButton());
		setUndoButton(gameMenu.getUndoButton());
		setPauseButton(((TimerGameMenu) gameMenu).getPauseButton());

		pauseMenu = new PauseMenu();
		setResumeButton(pauseMenu.resumeButton);
		setRestartButton(pauseMenu.restartButton);

		modeHBox.getChildren().addAll(board, gameMenu);

		passedLevelThread = new Thread(() -> {
			gameMenu.setDisable(true);
			for (int i = 0; i < (n) * (n); i++) {
				try {
					board.changeColor(i / (n), i % (n), false);
					;
					Thread.sleep(3000 / ((n) * (n) * 2));
					board.changeColor(i / (n), i % (n), false);
					;
					Thread.sleep(3000 / ((n) * (n) * 2));
				} catch (InterruptedException e) {
					System.out.println("Stop Timer Thread");
					break;
				}
			}
			gameMenu.setDisable(false);
			javafx.application.Platform.runLater(new Runnable() {
				@Override
				public void run() {
					toNextLevel();
				}
			});
		});

		timerThread = new Thread(() -> {
			while (true && Main.pane instanceof TimerMode) {
				try {
					((TimerGameMenu) gameMenu).setTimeLeft(timeLeft);
					((TimerGameMenu) gameMenu).drawCurrentTimeString(((TimerGameMenu) gameMenu).getGc());
					Thread.sleep(1000);
					timeLeft--;
					if (timeLeft == 0) {
						javafx.application.Platform.runLater(new Runnable() {
							@Override
							public void run() {
								showPassLevel();
								setPenalty();
								timerThread.interrupt();
							}
						});
						break;
					}
				} catch (InterruptedException e) {
					System.out.println("Stop Timer Thread");
					break;
				}
			}
		});
		timerThread.start();
	}

	private void setResetButton(Button resetButton) {
		resetButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				GameMenuButton.playSoundEffect();
				resetBoard();
			}
		});
	}

	private void setNewPuzzleButton(Button newPuzzleButton) {
		newPuzzleButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				GameMenuButton.playSoundEffect();
				timerThread.interrupt();
				TimerMode timerMode = new TimerMode(level, timeLeft, gameMenu.getPenalty() + 200, passedLevel, null);
				Main.changeScene(timerMode);
			}
		});
	}

	private void setPauseButton(Button pauseButton) {
		pauseButton.setOnAction(new EventHandler<ActionEvent>() {
			@SuppressWarnings("deprecation")
			@Override
			public void handle(ActionEvent arg0) {
				getChildren().add(pauseMenu);
				timerThread.suspend();
				passedLevelThread.suspend();
			}
		});
	}

	private void setResumeButton(Button resumeButton) {
		resumeButton.setOnAction(new EventHandler<ActionEvent>() {
			@SuppressWarnings("deprecation")
			@Override
			public void handle(ActionEvent arg0) {
				GameMenuButton.playSoundEffect();
				getChildren().remove(pauseMenu);
				timerThread.resume();
				passedLevelThread.resume();
			}
		});
	}

	@Override
	protected void setRestartButton(Button restartButton) {

		restartButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				GameMenuButton.playSoundEffect();
				timerThread.interrupt();
				TimerMode timerMode = new TimerMode(1, 60, 0, 0, null);
				Main.changeScene(timerMode);
			}
		});
	}

	@Override
	protected void toNextLevel() {
		// TODO Auto-generated method stub
		TimerMode nextLevel = new TimerMode(level + 1, timeLeft + 15, gameMenu.getPenalty(), passedLevel + 1, null);
		Main.changeScene(nextLevel);
	}

	@Override
	protected void setPenalty() {
		PlayerInfo.setTimerPassedLevel(passedLevel);
		PlayerInfo.setTimerPenalty(gameMenu.getPenalty());
		return;
	}

	@Override
	public void timerNextLevel() {
		timerThread.interrupt();
		Main.playSoundEffect("congrats.mp3");
		disableBoard();
		passedLevelThread.start();
	}

	@Override
	protected void resetBoard() {
		timerThread.interrupt();
		TimerMode timerMode = new TimerMode(level, timeLeft, gameMenu.getPenalty(), passedLevel, pressed);
		Main.changeScene(timerMode);
	}

	@Override
	public void showPassLevel() {
		TimerPassLevel passLevel = new TimerPassLevel(gameMenu.getPenalty(), passedLevel);
		setToNextLevelButton(passLevel.getToNextLevelButton());
		setRestartButton(passLevel.getRestartButton());
		Main.playSoundEffect("congrats.mp3");
		disableBoard();
		passLevel.setPenalty(gameMenu.getPenalty());
		modeHBox.getChildren().remove(gameMenu);
		modeHBox.getChildren().add(passLevel);
	}
}