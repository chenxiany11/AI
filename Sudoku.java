import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * 
 * @author Sybil Chen
 * 
 * backtracking method to solve sudoku
 * 9/7/21
 * 
 */
public class Sudoku {

	private static int boardSize = 0;
	private static int partitionSize = 0;

	public static void main(String[] args) {
		String filename = "src/SudokuProblems/sudoku9Hard.txt";
		File inputFile = new File(filename);
		Scanner input = null;
		int[][] vals = null;

		int temp = 0;
		int count = 0;

		ArrayList<Answer> numbers = new ArrayList<Answer>();

		try {
			input = new Scanner(inputFile);
			temp = input.nextInt();
			boardSize = temp;
			partitionSize = (int) Math.sqrt(boardSize);
			System.out.println("Boardsize: " + temp + "x" + temp);
			vals = new int[boardSize][boardSize];

			System.out.println("Input:");
			int i = 0;
			int j = 0;
			while (input.hasNext()) {
				temp = input.nextInt();
				count++;
				System.out.printf("%3d", temp);
				vals[i][j] = temp;
				if (temp == 0) {
					// Done
					numbers.add(new Answer(i, j));
				}
				j++;
				if (j == boardSize) {
					j = 0;
					i++;
					System.out.println();
				}
				if (j == boardSize) {
					break;
				}
			}
			input.close();
		} catch (FileNotFoundException exception) {
			System.out.println("Input file not found: " + filename);
		}
		if (count != boardSize * boardSize)
			throw new RuntimeException("Incorrect number of inputs.");

		boolean solved = solve(numbers, vals, 0);

		// output
		int index = filename.lastIndexOf(".");
	    if(index == -1){
	       filename = filename+"Solution";
	    }
		File file = new File(filename.substring(0, index)+ "Solution"+ filename.substring(index));
		FileWriter fw;
		try {
			fw = new FileWriter(file);
			PrintWriter pw = new PrintWriter(fw);
			if (!solved) {
				System.out.println("No solution found.");
				pw.print("-1");
				pw.close();
			} else {
				System.out.println("\nOutput\n");
				for (int i = 0; i < boardSize; i++) {
					for (int j = 0; j < boardSize; j++) {
						System.out.printf("%3d", vals[i][j]);
						pw.print(vals[i][j]);
						if (j != boardSize - 1) {
							pw.print(" ");
						}
					}
					pw.println();
					System.out.println();
				}
				pw.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static class Answer {
		int x;
		int y;
		int value;
		
		Answer(int x, int y) {
			this.x = x;
			this.y = y;
			this.value = 1;
		}
	}
	
	// 9*9
	public static boolean solve(ArrayList<Answer> answers, int[][] vals, int index) {
		// Done
		if (answers.size() <= index) {
			return false;
		}

		Answer tmp = answers.get(index);
		//try each value until find the right one
		while (tmp.value <= 9) {
			if (check(tmp, vals)) {
				vals[tmp.x][tmp.y] = tmp.value;
				if (solve(answers, vals, index + 1) || index == answers.size() - 1) {
					return true;
				}
			}
			tmp.value++;
		}
		//not working
		tmp.value = 1;
		vals[tmp.x][tmp.y] = 0;

		return false;
	}

	public static boolean check(Answer answer, int[][] vals) {
		//check if duplicated
		int tmp = answer.value;

		for (int i = 0; i < vals.length; i++) {
			if (vals[i][answer.y] == tmp || vals[answer.x][i] == tmp) {
				return false;
			}
		}

		int xx = answer.x - answer.x % 3;
		int yy = answer.y - answer.y % 3;
		
		//check each square
		for (int i = xx; i < 3+xx; i++) {
			for (int j = yy; j < 3+yy; j++) {
				if (vals[i][j] == tmp) {
					return false;
				}
			}
		}

		return true;
	}

}
