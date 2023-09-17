package vacuumagentproject;

/**
 * @author Hayden Holbrook
 */
import java.util.*;

public class HahVacuumModelReflexBumpAgent extends VacuumAgent {

	private Cell[][] grid;
	private int x;
	private int y;
	private Cell currentCell;
	private Cell goalCell;
	private PriorityQueue<Cell> frontier; // ordered by distance from current cell, rebuilt as needed
	private ArrayDeque<Cell> path; // sequence of cells that form a path from vacuum to goal
	private Set<Cell> visitedCells;
	private Set<Cell> knownCells;
	private Set<Cell> blockedCells;
	private int timeStep;
	private static final int MAX_DEPTH = 2500; // maximum depth for search


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

	/**
	 * Builds a path from currentCell to GoalCell through known cells.
	 * A priority queue is used to determine which cells to visit first.
	 * Cells are ordered by their distance from the goal, with cells closer to the goal having higher priority
	 */
	private void buildPathToGoal() {
		// clean up before starting
		path.clear();
		clearCellPathfindingFields();

		// setup data structures for the search
		PriorityQueue<Cell> queue = new PriorityQueue<>(new CellComparator());
		Set<Cell> visited = new HashSet<>(); // cells visited during this search
		queue.add(currentCell);
		
		// do a depth-first search and build path once a solution is found
		while (!queue.isEmpty()) {
			Cell c = queue.remove();
			visited.add(c);
			if (c == goalCell) { // solution found, build path
				Cell cell = goalCell;
				while (cell != currentCell) {
					path.addFirst(cell);
					cell = cell.parent;
				}
				break;
			}
			if (c.depth > MAX_DEPTH) { // stop the search if it gets too deep
				break;
			}
			for (Cell neighbor : getNeighbors(c)) { // add neighbors to queue
				if (knownCells.contains(neighbor) && !blockedCells.contains(neighbor) && !visited.contains(neighbor)) {
					neighbor.parent = c;
					neighbor.depth = calculateDepth(c, neighbor);
					queue.add(neighbor);
				}
			}
		}

		// Print path for debugging
//		StringBuilder s = new StringBuilder();
//		s.append("PATH: ");
//		for (Cell cell : path) {
//			s.append(cell);
//		}
//		System.out.println(s);
	}

	/**
	 * Returns a list cell that neighbor the given cell.
	 * @param cell
	 * @return list of cell's neighbors
	 */
	private List<Cell> getNeighbors(Cell cell) {
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

	/**
	 * Determine cell depth based on its absolute distance from the current cell.
	 * @param c
	 * @return int depth
	 */
	private int updateDepth(Cell c) {
		return Math.abs(c.x - x) + Math.abs(c.y - y);
	}
	
	/**
	 * Determine cell depth based on distance between given cells
	 * @param c1 cell
	 * @param c2 cell
	 * @return int depth
	 */
	private int calculateDepth(Cell c1, Cell c2) {
		return Math.abs(c1.x - c2.x) + Math.abs(c1.y - c2.y);
	}

	/**
	 * Resets the cell attributes used for path finding
	 */
	private void clearCellPathfindingFields() {
		for (Cell c : visitedCells) {
			c.parent = null;
			c.depth = updateDepth(c);
		}
	}

	/**
	 * Chooses a new goal cell from the frontier
	 */
	private void updateGoalCell() {
		updateFrontier();
		if (!frontier.isEmpty())
			goalCell = frontier.remove();
//		System.out.println("New Goal Cell: (" + goalCell.x + ", " + goalCell.y + ")");
	}

	/**
	 * Rebuilds the frontier from known-unblocked cells that are unvisited.
	 */
	private void updateFrontier() {
		frontier.clear();
		for (Cell c : knownCells) {
			if (!visitedCells.contains(c) && !blockedCells.contains(c)) {
				frontier.add(c);
			}
		}
	}

	/**
	 * Returns the next move action based on the next cell in the path.
	 * Updates the x and y values for the vacuum based on the move
	 * @return Move action, or stop if path is empty
	 */
	private VacuumAction getNextMove() {
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

	/**
	 * Senses neighboring cells for obstacles and adds new cells to the internal sets knownCells and blockedCells
	 * @param percept
	 */
	private void checkNeighbors(VacuumBumpPercept percept) {
		Cell left = grid[x - 1][y];
		Cell right = grid[x + 1][y];
		Cell forward = grid[x][y - 1];
		Cell back = grid[x][y + 1];

		// check neighbors for blocked cells
		if (percept.willBump(VacuumAction.LEFT)) {
			blockedCells.add(left);
		}

		if (percept.willBump(VacuumAction.RIGHT)) {
			blockedCells.add(right);
		}

		if (percept.willBump(VacuumAction.FORWARD)) {
			blockedCells.add(forward);
		}

		if (percept.willBump(VacuumAction.BACK)) {
			blockedCells.add(back);
		}
		
		knownCells.add(left);
		knownCells.add(right);
		knownCells.add(forward);
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

	private VacuumAction getActionModelReflex(VacuumBumpPercept percept) {
		timeStep++;
//		System.out.printf("Time: %d\n", timeStep);
		currentCell = grid[x][y];
		knownCells.add(currentCell);
		visitedCells.add(currentCell);

		// process percept and update frontier
		checkNeighbors(percept);
		updateFrontier();

		if (percept.currentStatus == Status.DIRTY) { // if cell is dirty, clean it
//			System.out.println("CLEANING: " + currentCell);
			return VacuumAction.SUCK;
		}
		
		// otherwise get the next move
		if (currentCell == goalCell || goalCell == null) { // pick a new goal if we need to
			updateGoalCell();
			buildPathToGoal();
		}
		VacuumAction action = getNextMove();
//		System.out.println("MOVING: " + action.name());
		return action;
	}

	private class Cell {
		public int x;
		public int y;
		public int depth; // used for path finding
		public Cell parent; // used for path finding

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
		
		/*
		 * Compare cells by distance from current cell,
		 * with closer cells having higher priority.
		 */
		@Override
		public int compare(Cell c1, Cell c2) {

			return Math.abs(c1.x - x) + Math.abs(c1.y - y) - Math.abs(c2.x - x) - Math.abs(c2.y - y);
		}
	}
}
