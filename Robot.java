import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Represents an intelligent agent moving through a particular room.
 * The robot only has one sensor - the ability to get the status of any
 * tile in the environment through the command env.getTileStatus(row, col).
 * 
 * @author Adam Gaweda, Michael Wollowski
 */

public class Robot {
	private Environment env;
	private int posRow;
	private int posCol;
	private LinkedList<Action> path;
	private boolean pathFound;
	private long openCount;
	private int pathLength;

	/**
	 * Initializes a Robot on a specific tile in the environment.
	 */

	public Robot(Environment env, int posRow, int posCol) {
		this.env = env;
		this.posRow = posRow;
		this.posCol = posCol;
		this.path = new LinkedList<>();
		this.pathFound = false;
		this.openCount = 0;
		this.pathLength = 0;
	}

	public void init() {
		bfs();
	}

	public boolean getPathFound() {
		return this.pathFound;
	}

	public long getOpenCount() {
		return this.openCount;
	}

	public int getPathLength() {
		return this.pathLength;
	}

	public void resetOpenCount() {
		this.openCount = 0;
	}

	public int getPosRow() {
		return posRow;
	}

	public int getPosCol() {
		return posCol;
	}

	public void incPosRow() {
		posRow++;
	}

	public void decPosRow() {
		posRow--;
	}

	public void incPosCol() {
		posCol++;
	}

	public void decPosCol() {
		posCol--;
	}

	/**
	 * Returns the next action to be taken by the robot. A support function
	 * that processes the path LinkedList that has been populates by the
	 * search functions.
	 */
	public Action getAction() {
		// Done: Implement this method
		if (path.isEmpty()) {
			return Action.DO_NOTHING;
		} else {
			return path.removeFirst();
		}
	}

	/**
	 * This method implements breadth-first search. It populates the path LinkedList
	 * and sets pathFound to true, if a path has been found. IMPORTANT: This method
	 * increases the openCount field every time your code adds a node to the open
	 * data structure, i.e. the queue or priorityQueue
	 * 
	 */
	public void bfs() {
		// done: Implement this method
		int rows[] = { -1, 0, 1, 0 };
		int cols[] = { 0, 1, 0, -1 };

		// use a queue for all the open states and linkedlist for closed states
		LinkedList<State> passed = new LinkedList<>();
		Queue<State> pos = new LinkedList<>();

		// add each position into positions queue
		pos.add(new State(posRow, posCol, new LinkedList<Action>()));
		this.openCount++;

		// while still space in the map
		while (!pos.isEmpty()) {
			// get current position
			State cur = pos.poll();
			passed.add(cur);

			// get the target
			if (cur.row == env.getTargetRow() && cur.col == env.getTargetCol()) {
				this.pathFound = true;
				this.pathLength = cur.actions.size();
				this.path = cur.getActions();
				return;
			}
			// move
			for (int i = 0; i < rows.length; i++) {
				int row = cur.row + rows[i];
				int col = cur.col + cols[i];

				if (env.validPos(row, col) && !containsState((LinkedList<State>) pos, row, col)
						&& !containsState(passed, row, col)) {

					// check where to move
					LinkedList<Action> action = (LinkedList<Action>) cur.getActions().clone();
					switch (i) {
						case 0:
							action.add(Action.MOVE_UP);
							break;
						case 1:
							action.add(Action.MOVE_RIGHT);
							break;
						case 2:
							action.add(Action.MOVE_DOWN);
							break;
						case 3:
							action.add(Action.MOVE_LEFT);
							break;
					}
					pos.add(new State(row, col, action));
					this.openCount++;
				}
			}
			// when there is no space, stop
			if (pos.isEmpty())
				return;
		}
	}

	public class State {
		private int row;
		private int col;
		private LinkedList<Action> actions;
		private int active;

		public State(int row, int col, int active) {
			this.row = row;
			this.col = col;
			this.active = active;
		}

		public State(int row, int col, LinkedList<Action> actions) {
			this.row = row;
			this.col = col;
			this.actions = actions;
		}

		public LinkedList<Action> getActions() {
			return actions;
		}

		public void setActions(LinkedList<Action> actions) {
			this.actions = actions;
		}

		public int getActive() {
			return this.active;
		}
	}

	public boolean containsState(LinkedList<State> states, int row, int col) {
		for (State state : states) {
			if (state.row == row && state.col == col) {
				return true;
			}
		}
		return false;
	}

	public boolean containsPosition(LinkedList<Position> positions, int row, int col) {
		for (Position pos : positions) {
			if (pos.getRow() == row && pos.getCol() == col) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method implements breadth-first search for maps with multiple targets.
	 * It populates the path LinkedList
	 * and sets pathFound to true, if a path has been found. IMPORTANT: This method
	 * increases the openCount field every time your code adds a node to the open
	 * data structure, i.e. the queue or priorityQueue
	 * 
	 */

	public void bfsM() {
		// done: Implement this method
		int cols = this.env.getCols();
		int rows = this.env.getRows();
		int pos[][] = new int[rows][cols];
		int count = 0;
		LinkedList<Position> targets = this.env.getTargets();

		for (int i = 0; i < targets.size(); i++) {
			int x = targets.get(i).getRow();
			int y = targets.get(i).getCol();
			pos[x][y] = 3;
		}

		int row = this.posRow;
		int col = this.posCol;
		while (count < targets.size()) {
			count++;
			Position cur = bfshelper(row, col, pos);
			row = cur.getRow();
			col = cur.getCol();
		}

		boolean tmp = true;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (pos[i][j] == 3)
					tmp = false;
			}
		}
		if (tmp) {
			this.pathFound = true;
			this.pathLength = this.path.size();
		}
	}

	public Position bfshelper(int row, int col, int[][] targets) {

		// get cols and rows
		int cols = this.env.getCols();
		int rows = this.env.getRows();
		int pass[][] = new int[rows][cols];
		int tmp = 0;
		int map[][] = { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 } };
		Action result[] = new Action[rows * cols];
		Queue<Position> pos = new LinkedList<>();
		pos.offer(new Position(row, col));
		State[][] temp = new State[rows][cols];

		// iterate through the positions
		while (pos.isEmpty()==false) {

			// poll one position from the queue
			Position p = pos.poll();
			pass[p.getRow()][p.getCol()] = 1;
			this.openCount++;

			// check each position
			for (int i = 0; i < map.length; i++) {

				int new_row = map[i][0] + p.getRow();
				int new_col = map[i][1] + p.getCol();

				if (this.env.validPos(new_row, new_col) && pass[new_row][new_col] == 0) {

					// new state here
					temp[new_row][new_col] = new State(new_row, new_col, i);

					if (targets[new_row][new_col] == 3) {

						targets[new_row][new_col] = 0;
						int final_row = new_row;
						int final_col = new_col;

						// determine the direction
						while (true) {
							int num = temp[final_row][final_col].getActive();
							switch (num) {
								case 0: {
									final_col--;
									result[tmp] = Action.MOVE_RIGHT;
									tmp++;
									break;
								}
								case 1: {
									final_col++;
									result[tmp] = Action.MOVE_LEFT;
									tmp++;
									break;
								}
								case 2: {
									final_row--;
									result[tmp] = Action.MOVE_DOWN;
									tmp++;
									break;
								}
								case 3: {
									final_row++;
									result[tmp] = Action.MOVE_UP;
									tmp++;
									break;
								}
							}
							if (final_row == row && final_col == col)
								break;
						}

						for (int j = 0; j < tmp; j++) {
							this.path.add(result[tmp - j - 1]);
						}
						// return the current position
						return new Position(new_row, new_col);
					} else {
						// edge
						pass[new_row][new_col] = 1;
						pos.offer(new Position(new_row, new_col));
					}
				}
			}
		}
		return null;
	}

	/**
	 * This method implements A* search. It populates the path LinkedList
	 * and sets pathFound to true, if a path has been found. IMPORTANT: This method
	 * increases the openCount field every time your code adds a node to the open
	 * data structure, i.e. the queue or priorityQueue
	 * 
	 */
	public void astar() {
		// done: Implement this method
		int rows[] = { -1, 0, 1, 0 };
		int cols[] = { 0, 1, 0, -1 };

		// use prioqueue and linkedlist
		PriorityQueue<AState> passed = new PriorityQueue<>();
		LinkedList<AState> pos = new LinkedList<>();
		passed.add(new AState(posRow, posCol, 0, 0, new LinkedList<Action>(), null));
		this.openCount++;

		// iterate through the map
		while (!passed.isEmpty()) {
			AState cur = passed.poll();
			pos.add(cur);
			if (cur.row == env.getTargetRow() && cur.col == env.getTargetCol()) {
				this.pathFound = true;
				this.path = cur.actions;
				this.pathLength = cur.actions.size();
				return;
			}
			for (int i = 0; i < rows.length; i++) {
				// check the grid

				int row = cur.row + rows[i];
				int col = cur.col + cols[i];

				// determine the direction
				if (env.validPos(row, col)) {
					LinkedList<Action> action = (LinkedList<Action>) cur.actions.clone();
					switch (i) {
						case 0:
							action.add(Action.MOVE_UP);
							break;
						case 1:
							action.add(Action.MOVE_RIGHT);
							break;
						case 2:
							action.add(Action.MOVE_DOWN);
							break;
						case 3:
							action.add(Action.MOVE_LEFT);
							break;
					}
					AState next = new AState(row, col, 0, cur.current + 1, action, null);

					// calculate manhattan discttance here
					if (!containsAStatePrio(passed, row, col) && !containsAState(pos, row, col)) {
						int score = 0;
						if (env.getTargets().getFirst() == null) {
							LinkedList<Position> targets = (LinkedList<Position>) next.targets.clone();
							Position place = targets.poll();
							if (place == null) {
								score = 0;
							} else {
								score = manhattanDist(next.row, next.col, place);
							}
						} else
							score = manhattanDist(next.row, next.col, env.getTargets().getFirst());
						next.finalcost = next.current + score;
						passed.add(next);
						this.openCount++;
					}

				}
			}

		}
	}

	public class AState implements Comparable<AState> {
		int row;
		int col;
		int current;
		int finalcost = 0;

		LinkedList<Position> targets;
		private LinkedList<Action> actions;

		public AState(int row, int col, int finalCost, int cost, LinkedList<Action> actions,
				LinkedList<Position> targets) {
			this.row = row;
			this.col = col;
			this.finalcost = finalCost;
			this.current = cost;
			this.actions = actions;
			this.targets = targets;
		}

		public int compareTo(AState state) {
			if (state == null) {
				return -1;
			}
			if (this.finalcost > state.finalcost) {
				return 1;
			} else if (this.finalcost < state.finalcost) {
				return -1;
			}
			return 0;
		}
	}

	public boolean containsAStatePrio(PriorityQueue<AState> states, int row, int col) {
		for (AState astate : states) {
			if (astate.row == row && astate.col == col) {
				return true;
			}
		}
		return false;
	}

	public boolean containsAState(LinkedList<AState> states, int row, int col) {
		for (AState astate : states) {
			if (astate.row == row && astate.col == col) {
				return true;
			}
		}
		return false;
	}

	public int manhattanDist(int row, int col, Position t) {
		return Math.abs(col - t.getCol()) + Math.abs(row - t.getRow());
	}

	/**
	 * This method implements A* search for maps with multiple targets. It
	 * populates the path LinkedList
	 * and sets pathFound to true, if a path has been found. IMPORTANT: This method
	 * increases the openCount field every time your code adds a node to the open
	 * data structure, i.e. the queue or priorityQueue
	 * 
	 */

	public class AStateM implements Comparable<AStateM> {
		int xx;
		int yy;
		int active;
		AStateM prev;
		Position point;

		public AStateM(Position point, Position end, int g) {
			this.point = point;
			this.xx = g;
			this.xx = (Math.abs(end.getRow() - point.getRow()) + Math.abs(end.getCol() - point.getCol())) * 10;
		}

		public int compareTo(AStateM node) {
			if (node == null)
				return -1;
			if (this.xx + this.yy < node.xx + node.yy) {
				return -1;
			} else if (this.xx + this.yy > node.xx + node.yy) {
				return 1;
			}
			return 0;
		}
	}

	public void astarM() {
		// done: Implement this method
		int rows = this.env.getRows();
		int cols = this.env.getCols();
		LinkedList<Position> pos = this.env.getTargets();
		Position side = new Position(this.posRow, this.posCol);
		int end = pos.size();
		int count = 0;
		int[][] map = { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 } };
		int[] visited = new int[end];

		ArrayList<AStateM> closed = new ArrayList<>();
		PriorityQueue<AStateM> opened = new PriorityQueue<>(AStateM::compareTo);
		PriorityQueue<AStateM> choosed = new PriorityQueue<>(AStateM::compareTo);

		// searching
		while (true) {
			boolean flag = false;

			closed.clear();
			opened.clear();
			choosed.clear();

			for (int i = 0; i < pos.size(); i++)
				if (visited[i] == 0)
					choosed.add(new AStateM(pos.get(i), side, 0));

			AStateM find_min = choosed.poll();
			int end_indx = 0;
			for (int i = 0; i < pos.size(); i++)
				if (pos.get(i).getCol() == find_min.point.getCol()
						&& pos.get(i).getRow() == find_min.point.getRow()) {
					end_indx = i;
					break;
				}

			Position enode = pos.get(end_indx);
			visited[end_indx] = 1;
			AStateM start = new AStateM(side, enode, 0);

			opened.add(start);
			while (!opened.isEmpty()) {
				this.openCount++;
				AStateM minNode = opened.poll();
				closed.add(minNode);

				for (int i = 0; i < map.length; i++) {
					int newRow = map[i][0] + minNode.point.getRow();
					int newCol = map[i][1] + minNode.point.getCol();
					Position currP = new Position(newRow, newCol);

					if (this.env.validPos(newRow, newCol) && ptInClose(closed, currP) == null) {

						AStateM pt = ptInOpen(opened, currP);
						if (pt == null) {
							pt = new AStateM(currP, enode, minNode.xx + 10);
							pt.prev = minNode;
							pt.active = i;
							opened.add(pt);
						} else {
							if (minNode.xx < pt.xx - 10) {
								pt.prev = minNode;
								pt.active = i;
								pt.xx = minNode.xx + 10;
							}
						}
					}
				}

				AStateM node = ptInClose(closed, enode);
				if (node != null) {
					flag = true;
					Action[] result = new Action[rows * cols];
					int tmp = 0;
					while (true) {
						// check direction
						if (node.prev == null) {
							for (int i = 0; i < tmp; i++) {
								this.path.add(result[tmp - i - 1]);
							}
							this.pathLength += tmp;
							break;
						} else {
							int i = node.active;
							switch (i) {
								case 0: {
									result[tmp] = Action.MOVE_RIGHT;
									tmp = tmp + 1;
									break;
								}
								case 1: {
									result[tmp] = Action.MOVE_LEFT;
									tmp = tmp + 1;
									break;
								}
								case 2: {
									result[tmp] = Action.MOVE_DOWN;
									tmp++;
									break;
								}
								case 3: {
									result[tmp] = Action.MOVE_UP;
									tmp = tmp + 1;
									break;
								}
							}
							node = node.prev;
						}
					}
				}
				if (flag)
					break;
			}
			// edge
			if (flag) {
				side = enode;
				count++;
			} else
				break;
			if (count == end) {
				this.pathFound = true;
				break;
			}
		}
	}

	public AStateM ptInClose(ArrayList<AStateM> closed, Position position) {
		for (AStateM state : closed)
			if (state.point.getCol() == position.getCol() && state.point.getRow() == position.getRow())
				return state;
		return null;
	}

	public AStateM ptInOpen(PriorityQueue<AStateM> opend, Position position) {
		for (AStateM state : opend)
			if (state.point.getRow() == position.getRow() && state.point.getCol() == position.getCol())
				return state;
		return null;
	}
}
