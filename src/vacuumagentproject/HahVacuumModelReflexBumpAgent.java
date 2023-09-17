package vacuumagentproject;

/**
 * @author Hayden Holbrook
 */
import java.util.*;

public class HahVacuumModelReflexBumpAgent extends VacuumAgent {

	private Cell[][] grid;
	private int x;
	private int y;
	Cell currentCell;
	Cell goalCell;
	PriorityQueue<Cell> frontier; // ordered by distance from current cell, rebuilt as needed
	ArrayDeque<Cell> path; // path from vacuum to goal
	Set<Cell> visitedCells;
	Set<Cell> knownCells;
	Set<Cell> blockedCells;
	private int timeStep;

	public HahVacuumModelReflexBumpAgent() {
		super();
		grid = new Cell[1000][1000];
		for (int i = 0; i < 1000; i++) {
			for (int j = 0; j < 1000; j++) {
				grid[i][j] = new Cell(i, j);
			}
		}
		x = 500;
		y = 500;
		currentCell = grid[x][y];
		goalCell = currentCell;
		timeStep = 0;
		frontier = new PriorityQueue<>(new CellComparator());
		path = new ArrayDeque<Cell>();
		visitedCells = new HashSet<>();
		knownCells = new HashSet<>();
		blockedCells = new HashSet<>();
	}

	// Set a maximum depth for pathfinding
	private static final int MAX_DEPTH = 2500;

	public void buildPathToGoal() {
		path.clear();
		clearCellPathfindingFields();
		// build a path to the goalCell using shortest path through known cells
		// using a priority queue to store cells to visit
		// cells are ordered by their depth, with cells closer to the goal having higher  priority
		// cells are visited in order of priority
		PriorityQueue<Cell> queue = new PriorityQueue<>(new CellComparator());
		Set<Cell> visited = new HashSet<>(); // cells visited during this search
		queue.add(currentCell);
		while (!queue.isEmpty()) {
			Cell c = queue.remove();
			visited.add(c);
			if (c == goalCell) {
				// build path from goalCell to currentCell
				Cell cell = goalCell;
				while (cell != currentCell) {
					path.addFirst(cell);
					cell = cell.parent;
				}
				break;
			}
			if (c.depth > MAX_DEPTH) {
				// if the cell is too far away, stop searching
				break;
			}
			for (Cell neighbor : getNeighbors(c)) {
				if (knownCells.contains(neighbor) && !blockedCells.contains(neighbor) && !visited.contains(neighbor)) {
					// if the neighbor is not blocked and has not been visited, add it to the queue
					neighbor.parent = c;
					neighbor.depth = calculateDepth(c, neighbor);
					queue.add(neighbor);
				}
			}
		}

		// Print path for debugging
		StringBuilder s = new StringBuilder();
		s.append("PATH: ");
		for (Cell cell : path) {
			s.append(cell);
		}
		System.out.println(s);
	}

	public List<Cell> getNeighbors(Cell cell) {
		List<Cell> neighbors = new ArrayList<>();
		int x = cell.x;
		int y = cell.y;
		

		if (x > 0) {
			Cell left = grid[x - 1][y];
			neighbors.add(left);
		}
		if (x < 999) {
			Cell right = grid[x + 1][y];
			neighbors.add(right);
		}
		if (y > 0) {
			Cell forward = grid[x][y - 1];
			neighbors.add(forward);
		}
		if (y < 999) {
			Cell back = grid[x][y + 1];
			neighbors.add(back);
		}

		return neighbors;
	}

	public int updateDepth(Cell c) {
		// set cell depth based on its absolute distance from the current cell
		return Math.abs(c.x - x) + Math.abs(c.y - y);
	}
	
	public int calculateDepth(Cell c1, Cell c2) {
		return Math.abs(c1.x - c2.x) + Math.abs(c1.y - c2.y);
	}

	public void clearCellPathfindingFields() {
		for (Cell c : visitedCells) {
			c.parent = null;
			c.depth = updateDepth(c);
		}
	}

	public void updateGoalCell() {
		// choose a new goal cell from the frontier
		updateFrontier();
		if (!frontier.isEmpty())
			goalCell = frontier.remove();
		System.out.println("New Goal Cell: (" + goalCell.x + ", " + goalCell.y + ")");
	}

	public void updateFrontier() {
		frontier.clear();
		for (Cell c : knownCells) {
			if (!visitedCells.contains(c) && !blockedCells.contains(c)) {
				frontier.add(c);
			}
		}
	}

	private VacuumAction getNextMove() {
		// returns the next move based on the next cell in the path
		// if the path is empty, stop
		// update x and y
		if (path.isEmpty()) {
			return VacuumAction.STOP;
		}
		Cell nextCell = path.remove();
		if (nextCell.x == x) {
			if (nextCell.y == y - 1) {
				y--;
				return VacuumAction.FORWARD;
			} else if (nextCell.y == y + 1) {
				y++;
				return VacuumAction.BACK;
			}
		} else if (nextCell.y == y) {
			if (nextCell.x == x - 1) {
				x--;
				return VacuumAction.LEFT;
			} else if (nextCell.x == x + 1) {
				x++;
				return VacuumAction.RIGHT;
			}
		}
		return VacuumAction.STOP;
	}

	private void checkNeighbors(VacuumBumpPercept percept) {
		Cell left = grid[x - 1][y];
		Cell right = grid[x + 1][y];
		Cell forward = grid[x][y - 1];
		Cell back = grid[x][y + 1];

		// check neighbors for blocked cells
		if (percept.willBump(VacuumAction.LEFT)) {
			blockedCells.add(left);
		}
		knownCells.add(left);

		if (percept.willBump(VacuumAction.RIGHT)) {
			blockedCells.add(right);
		}
		knownCells.add(right);

		if (percept.willBump(VacuumAction.FORWARD)) {
			blockedCells.add(forward);
		}
		knownCells.add(forward);

		if (percept.willBump(VacuumAction.BACK)) {
			blockedCells.add(back);
		}
		knownCells.add(back);
	}

	@Override
	public VacuumAction getAction(VacuumPercept percept) {
		if (percept instanceof VacuumBumpPercept)
			return getActionModelReflex((VacuumBumpPercept) percept);
		else {
			System.out.println("Error:  Expected a Bump Percept!!");
			return VacuumAction.STOP;
		}
	}

	/** This gets called on every step of the simulation */
	private VacuumAction getActionModelReflex(VacuumBumpPercept percept) {
		System.out.printf("Time: %d\n", timeStep++);
		currentCell = grid[x][y];
		knownCells.add(currentCell);
		visitedCells.add(currentCell);

		if (frontier.contains(currentCell))
			frontier.remove(currentCell);

		// process percept and update internal state
		checkNeighbors(percept);
		updateFrontier();

		if (percept.currentStatus == Status.DIRTY) { // if cell is dirty, clean it
			System.out.println("CLEANING: " + currentCell);
			return VacuumAction.SUCK;
		}
		// otherwise get the next move
		if (currentCell == goalCell || goalCell == null) { // pick a new goal
			updateGoalCell();
			buildPathToGoal();
		}
		VacuumAction action = getNextMove();
		System.out.println("MOVING: " + action.name());
		return action;
	}

	private class Cell {
		public int x;
		public int y;
		public int depth;
		public Cell parent;

		public Cell(int x, int y) {
			this.x = x;
			this.y = y;
			this.depth = 0;
		}

		@Override
		public String toString() {
			return String.format("Cell(%d, %d)", x, y);
		}
	}

	private class CellComparator implements Comparator<Cell> {
		@Override
		public int compare(Cell c1, Cell c2) {
			// compare cells by distance from current cell with
			// closer cells having higher priority
			return Math.abs(c1.x - x) + Math.abs(c1.y - y) - Math.abs(c2.x - x) - Math.abs(c2.y - y);
		}
	}
}
