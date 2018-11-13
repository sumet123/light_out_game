package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class BoardSolver
{
	private static String inversedFilePath = "BoardSolver_inversedM.txt";
	private Board board;
	private Light[][] inversedM;
	private int n;
	private ArrayList<Integer> shouldPress;
	private ArrayList<Light> boardVector;

	public BoardSolver(Board board) throws FileNotFoundException
	{
		this.board = board;
		n = board.getN();
		read_inversedM();
		shouldPress = new ArrayList<Integer>();
		boardVector = new ArrayList<Light>();
		for(int i=0;i<n;i++)
		{
			for(int j=0;j<n;j++)
			{
				boardVector.add(board.getLight(i, j));
			}
		}
		find_ShouldPress();
	}

	private void read_inversedM() throws FileNotFoundException
	{
		inversedM = new Light[n * n][n * n];
		Scanner sc = new Scanner(new File(inversedFilePath));
		while (sc.nextInt() != n)
		{
			inversedM[0][0] = new Light();
		}
		for (int i = 0; i < n * n; i++)
		{
			for (int j = 0; j < n * n; j++)
			{
				inversedM[i][j] = new Light(sc.nextInt(), 2);
			}
		}
	}

	private void find_ShouldPress()
	{
		for (int i = 0; i < n * n; i++)
		{
			Light ans = new Light();
			for (int j = 0; j < n * n; j++)
			{
				ans = ans.plus(inversedM[i][j].multiply(boardVector.get(j)));
			}
			if (ans.getCurrentState()==1)
				shouldPress.add(i);
		}
	}

	public void printInversedM()
	{
		for (int i = 0; i < n * n; i++)
		{
			for (int j = 0; j < n * n; j++)
			{
				System.out.print(inversedM[i][j].getCurrentState() + " ");
			}
			System.out.println();
		}
	}
	
	public void printShouldPress()
	{
		int sz=shouldPress.size();
		for(int i=0;i<sz;i++)
		{
			System.out.println(shouldPress.get(i));
		}
	}
}
