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
  private int timeStep;

  public HahVacuumModelReflexBumpAgent() {
    super();
    grid = new Cell[1000][1000];
    for (int i = 0; i < 1000; i++) {
      for (int j = 0; j < 1000; j++) {
        grid[i][j] = new Cell(i, j, CellStatus.UNVISITED);
      }
    }
    x = 500;
    y = 500;
    currentCell = grid[x][y];
    goalCell = currentCell;
    timeStep = 0;
    frontier =
        new PriorityQueue<Cell>(
            new Comparator<Cell>() {
              @Override
              public int compare(Cell c1, Cell c2) {
                return (Math.abs(c1.x - goalCell.x) + Math.abs(c1.y - goalCell.y))
                    - (Math.abs(c2.x - goalCell.x) + Math.abs(c2.y - goalCell.y));
              }
            });

    path = new ArrayDeque<Cell>();
    visitedCells = new HashSet<>();
    knownCells = new HashSet<>();
  }

  public void buildPathToGoal() {
	    // Build a path between currentCell and goalCell
	    // through known cells that are not blocked using IDBFS

	    // Initialize depth limit to 1
	    int depthLimit = 1;

	    while (true) {
	        // Perform a breadth-first search with the current depth limit
	        boolean pathFound = IDBFS(depthLimit);

	        if (pathFound) {
	            break; // Exit the loop if a path is found
	        }

	        // Increase the depth limit for the next iteration
	        depthLimit++;
	    }

	    System.out.println("Path to goal:");
	    for (Cell cell : path) {
	        System.out.println("(" + cell.x + ", " + cell.y + ")");
	    }
	    clearCellPathfindingFields();
	}

	public boolean IDBFS(int depthLimit) {
	    Queue<Cell> queue = new LinkedList<>();
	    Set<Cell> visitedCells = new HashSet<>();

	    queue.add(currentCell);
	    visitedCells.add(currentCell);

	    while (!queue.isEmpty()) {
	        Cell cell = queue.poll();

	        if (cell == goalCell) {
	            // Reconstruct the path
	            while (cell != currentCell) {
	                path.push(cell);
	                cell = cell.parent;
	            }
	            return true; // Path found
	        }

	        if (cell.parent != null) {
	            visitedCells.add(cell.parent); // Mark the parent as visited
	        }

	        if (cell.parent == null || cell.parent != currentCell) {
	            // If the cell's parent is not the current cell, it's a new level
	            if (cell.depth < depthLimit) {
	                // Explore neighbors only if depth is within the limit
	                for (Cell neighbor : getNeighbors(cell)) {
	                    if (!visitedCells.contains(neighbor)) {
	                        neighbor.parent = cell;
	                        neighbor.depth = cell.depth + 1;
	                        queue.add(neighbor);
	                    }
	                }
	            }
	        }
	    }

	    return false; // Path not found within the depth limit
	}

	public List<Cell> getNeighbors(Cell cell) {
	    List<Cell> neighbors = new ArrayList<>();

	    int x = cell.x;
	    int y = cell.y;

	    if (x > 0) {
	        neighbors.add(grid[x - 1][y]);
	    }
	    if (x < 999) {
	        neighbors.add(grid[x + 1][y]);
	    }
	    if (y > 0) {
	        neighbors.add(grid[x][y - 1]);
	    }
	    if (y < 999) {
	        neighbors.add(grid[x][y + 1]);
	    }

	    return neighbors;
	}

  public void clearCellPathfindingFields() {
    for (Cell c : visitedCells) {
      c.parent = null;
      c.depth = 0;
    }
  }

  public void updateGoalCell() {
    // choose a new goal cell from the frontier
    // if the frontier is empty, there is nowhere to go
    if (frontier.isEmpty()) {
      return;
    } else {
      goalCell = frontier.remove();
    }
    System.out.println("New Goal Cell: (" + goalCell.x + ", " + goalCell.y + ")");
    buildPathToGoal();
  }

  public void updateFrontier() {
    // update the frontier with all unvisited cells adjacent to visited cells
    // if a cell is blocked, it is not added to the frontier
	frontier.clear();
	for (Cell c : knownCells) {
		if (!visitedCells.contains(c) && c.status != CellStatus.BLOCKED) {
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
      Cell left = grid[x -1][y];
      Cell right = grid[x +1][y];
      Cell forawrd = grid[x][y - 1];
      Cell back = grid[x][y + 1];
      
    // check neighbors for blocked cells
    if (percept.willBump(VacuumAction.LEFT)) {
      left.status = CellStatus.BLOCKED;
    }
    knownCells.add(left);

    if (percept.willBump(VacuumAction.RIGHT)) {
        right.status = CellStatus.BLOCKED;
    }
    knownCells.add(right);

    if (percept.willBump(VacuumAction.FORWARD)) {
    	forawrd.status = CellStatus.BLOCKED;
    }
    knownCells.add(forawrd);

    if (percept.willBump(VacuumAction.BACK)) {
        back.status = CellStatus.BLOCKED;
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
	// mark current cell as visited
	currentCell = grid[x][y];
    currentCell.status = CellStatus.VISITED;
	knownCells.add(currentCell);
	visitedCells.add(currentCell);
	
	// process percept and update internal state
    checkNeighbors(percept);
    updateFrontier();
    
    if (currentCell == goalCell || goalCell == null) { // pick a new goal
      updateGoalCell();
    }
    
    if (percept.currentStatus == Status.DIRTY) { // if cell is dirty, clean it
      return VacuumAction.SUCK;
    } else { // otherwise get the next move
      VacuumAction action = getNextMove();
      return action;
    }
  }

  private class Cell {
    public int x;
    public int y;
    public int depth;
    public Cell parent;
    public CellStatus status;

    public Cell(int x, int y, CellStatus status) {
      this.x = x;
      this.y = y;
      this.depth = 0;
      this.status = status;
    }
  }

  private enum CellStatus {
    UNVISITED,
    VISITED,
    BLOCKED,
  }
}
