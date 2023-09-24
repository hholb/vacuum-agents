
package vacuumagentproject;

import java.awt.Point;
import java.util.*;

/**
 *
 * @author Marietta E. Cameron
 */
public class TPVacuumModelReflexLocAgent extends VacuumAgent {
	private Point prev;
	private List<Point> map;
	private VacuumAction lastAction;
	private Point curr;
	private List<Point> visited;
	// private Queue<VacuumAction> loopBreaker;
	private Stack<Point> path;
	// private int maxLoops;
	private VacuumAction action;

	TPVacuumModelReflexLocAgent() {
		map = new ArrayList<>();
		lastAction = VacuumAction.STOP; // Initialize lastAction
		prev = new Point();
		visited = new ArrayList<>();
		path = new Stack<>();
		action = VacuumAction.STOP;

		// loopBreaker = new LinkedList<VacuumAction>();
		// maxLoops = 10;
	}

	public VacuumAction getAction(VacuumPercept percept) {
		if (percept instanceof VacuumLocPercept) {
			return getActionModelReflex((VacuumLocPercept) percept);
		} else {
			System.out.println("Error: Expected a Location Percept!!");
			return VacuumAction.STOP;
		}
	}

	private VacuumAction getActionModelReflex(VacuumLocPercept percept) {
		if (percept.currentStatus == Status.DIRTY) {
			return VacuumAction.SUCK;
		}

		System.out.println(action.toString());
		System.out.println(percept.toString());
		//List of obstacles i found i'll use to see where i haven't explored
		System.out.println(map.toString());
		listPrinter(map);
		curr = new Point(percept.getCol(), percept.getRow());
		
		if (!visited.contains(curr)) {
			visited.add(curr);
		}
		//Stack of tiles i've been to in case i need to backtrack to find unexplored tiles
		path.push(curr);

		VacuumAction action = getNextMove();
		System.out.println(
				"moved " + action.toString() + " from " + prev.x + " " + prev.y + " to " + curr.x + " " + curr.y);
		if (curr.x == prev.x && curr.y == prev.y) {
			if (action == VacuumAction.BACK) {
				map.add(new Point(curr.x, curr.y + 1));
			}
			if (action == VacuumAction.FORWARD) {
				map.add(new Point(curr.x, curr.y - 1));
			}
			if (action == VacuumAction.LEFT) {
				map.add(new Point(curr.x - 1, curr.y));
			}
			if (action == VacuumAction.RIGHT) {
				map.add(new Point(curr.x + 1, curr.y));
			}
		}
		while (action == VacuumAction.STOP && !path.isEmpty()) {
			if(!path.isEmpty()) {
				action = backTrack();
			} else {
				action = getNextMove();
			}
		}

		System.out.println("stack path " + path.toString());
		prev = curr;
		return action;

	}

	private VacuumAction backTrack() {
	    if (path.isEmpty()) {
	        return VacuumAction.STOP; 
	    }

	    Point target = path.peek();
	    if (!curr.equals(target)) {
	        return VacuumAction.STOP; 
	    }

	    // Check if there are unexplored neighbors
	    boolean hasUnexplored = false;
	    Point[] neighbors = getNeighbors(curr);
	    for (Point neighbor : neighbors) {
	        if (!visited.contains(neighbor) && !map.contains(neighbor)) {
	            hasUnexplored = true;
	            break;
	        }
	    }

	    if (!hasUnexplored) {
	        path.pop();
	        if (!path.isEmpty()) {
	            Point next = path.peek();
	            VacuumAction backAction = move(curr, next);
	            curr = next;
	            System.out.println("backtracked ");
	            return backAction; // Recursive case: Continue backtracking
	        } else {
	            return VacuumAction.STOP; // Base case: No more path left, stop backtracking
	        }
	    } else {
	        return VacuumAction.STOP; // Base case: Found an unexplored tile, stop backtracking
	    }
	}


	private Point[] getNeighbors(Point curr2) {
		int x = curr.x;
		int y = curr.y;

		// Define the possible neighboring points
		Point[] neighbors = { new Point(x, y + 1), // Up
				new Point(x, y - 1), // Down
				new Point(x - 1, y), // Left
				new Point(x + 1, y) // Right
		};

		return neighbors;
	}

	private VacuumAction getNextMove() {
		Point currentLocation = path.peek();
		Point[] possibles = { new Point(currentLocation.x, currentLocation.y + 1), // Up
				new Point(currentLocation.x, currentLocation.y - 1), // Down
				new Point(currentLocation.x - 1, currentLocation.y), // Left
				new Point(currentLocation.x + 1, currentLocation.y) // Right
		};

		for (Point p : possibles) {
			if (!visited.contains(p) && !map.contains(p)) {
				VacuumAction moveAction = move(currentLocation, p);
				System.out.println("Next move: " + moveAction + " to " + p);
				return moveAction;
			}
		}
		//VacuumAction action = backTrack();
		return VacuumAction.STOP;
	}

	private VacuumAction move(Point from, Point to) {
		if (from.x < to.x)
			return VacuumAction.RIGHT;
		if (from.x > to.x)
			return VacuumAction.LEFT;
		if (from.y > to.y)
			return VacuumAction.FORWARD;
		if (from.y < to.y)
			return VacuumAction.BACK;
		return VacuumAction.STOP;
	}

	private VacuumAction randomAction() {
		ArrayList<VacuumAction> p = new ArrayList();
		if (!map.contains(new Point(curr.x, curr.y - 1)))
			if (!map.contains(new Point(curr.x, curr.y - 1))) {
				p.add(VacuumAction.BACK);
			}
		if (!map.contains(new Point(curr.x, curr.y + 1))) {
			p.add(VacuumAction.FORWARD);
		}
		if (!map.contains(new Point(curr.x + 1, curr.y))) {
			p.add(VacuumAction.RIGHT);
		}
		if (!map.contains(new Point(curr.x - 1, curr.y))) {
			p.add(VacuumAction.LEFT);
		}
		Collections.shuffle(p);
		Random random = new Random();
		int randomIndex = random.nextInt(p.size());
		return p.get(randomIndex);
	}
	/*
	 * { possibleMoves.add(VacuumAction.BACK); } if (!map.contains(new Point(curr.x,
	 * curr.y + 1))) { possibleMoves.add(VacuumAction.FORWARD); } if
	 * (!map.contains(new Point(curr.x + 1, curr.y))) {
	 * possibleMoves.add(VacuumAction.RIGHT); } if (!map.contains(new Point(curr.x -
	 * 1, curr.y))) { possibleMoves.add(VacuumAction.LEFT); }
	 * Collections.shuffle(possibleMoves); return possibleMoves.get(0); Random
	 * random = new Random(); int randomIndex = random.nextInt(4);
	 * 
	 * switch (randomIndex) { case 0: return VacuumAction.BACK; case 1: return
	 * VacuumAction.FORWARD; case 2: return VacuumAction.LEFT; case 3: return
	 * VacuumAction.RIGHT; } return lastAction; }
	 */

	/*
	 * private boolean badLooping() { VacuumAction[] arr = loopBreaker.toArray(new
	 * VacuumAction[loopBreaker.size()]); for (int i = 0; i < arr.length / 2; i++) {
	 * if (arr[i].equals(arr[arr.length / 2 + i])) { return false; } } return true;
	 * }
	 */

	/*
	 * private VacuumAction selectNextMove(List<Point> map) { List<VacuumAction>
	 * possibleMoves = new ArrayList<>(); if (!map.contains(new Point(curr.x, curr.y
	 * - 1))) { possibleMoves.add(VacuumAction.BACK); } if (!map.contains(new
	 * Point(curr.x, curr.y + 1))) { possibleMoves.add(VacuumAction.FORWARD); } if
	 * (!map.contains(new Point(curr.x + 1, curr.y))) {
	 * possibleMoves.add(VacuumAction.RIGHT); } if (!map.contains(new Point(curr.x -
	 * 1, curr.y))) { possibleMoves.add(VacuumAction.LEFT); }
	 * Collections.shuffle(possibleMoves); return possibleMoves.get(0); }
	 */
	public void listPrinter(List<Point> l) {
		for (Point p : l) {
			System.out.println(p.toString() + " ");
		}
	}

	private VacuumAction listRanker(Point curr, List<Point> map, List<Point> visited) {
		Point[] arr = new Point[4];
		List<Point> ranks = new ArrayList<>();
		Point prospectBack = new Point(curr.x, curr.y + 1);
		Point prospectForward = new Point(curr.x, curr.y - 1);
		Point prospectLeft = new Point(curr.x - 1, curr.y);
		Point prospectRight = new Point(curr.x + 1, curr.y);

		arr[0] = prospectBack;
		arr[1] = prospectForward;
		arr[2] = prospectLeft;
		arr[3] = prospectRight;

		for (Point p : arr) {
			if (!map.contains(p) && visited.contains(p)) {
				ranks.add(p);

			}
			if (!map.contains(p) && !visited.contains(p)) {
				ranks.add(0, p);

			}
		}
		if (ranks.size() == 1 || ranks.size() == 0) {
			Random gen = new Random();

			int index = gen.nextInt(actionList.length); // randomly select an action
			while (!actionList[index].isAMove())// sequentially look for a move
				index = (index + 1) % actionList.length;
			return actionList[index];
		}

		Point first = ranks.get(0);
		if (first.equals(prospectBack)) {
			return VacuumAction.BACK;
		} else if (first.equals(prospectForward)) {
			return VacuumAction.FORWARD;
		} else if (first.equals(prospectLeft)) {
			return VacuumAction.LEFT;
		} else if (first.equals(prospectRight)) {
			return VacuumAction.RIGHT;

		}

		return VacuumAction.STOP;

	}

}