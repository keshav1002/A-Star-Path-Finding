import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

public class PathFindingOnSquaredGrid {

	// declaring variables and arrays required for path finding.
	private Square[][] squaredGrid;
	private int startX, startY, endX, endY;
	private PriorityQueue<Square> openList;
	private List<Square> closedList;
	private String distanceMetric;
	private int N;

	public static void main(String[] args) {
		PathFindingOnSquaredGrid pathFinder = new PathFindingOnSquaredGrid();
		pathFinder.init();
	}

	public void init() {
		N = 10; // Initializing the NxN grid

		// Getting the NxN matrix with randomly blocked squares
		boolean[][] randomlyGenMatrix = randomMatrix(N, 0.8);
		StdArrayIO.print(randomlyGenMatrix);

		// showing the matrix for the user
		showRandomSquaredGrid(randomlyGenMatrix, true);

		Scanner input = new Scanner(System.in);

		// Getting user input for the starting and ending coordinates
		System.out.print("Enter x for Starting Square : ");
		int sX = input.nextInt();

		System.out.print("Enter y for Starting Square : ");
		int sY = input.nextInt();

		System.out.print("Enter x for Ending Square : ");
		int eX = input.nextInt();

		System.out.print("Enter x for Ending Square : ");
		int eY = input.nextInt();

		// looping 3 times, for each distance metric
		for (int count = 0; count < 3; count++) {
			// changing the distance metric based on the loop count
			switch (count) {
			case 0:
				distanceMetric = "manhattan";
				break;
			case 1:
				distanceMetric = "euclidean";
				break;
			case 2:
				distanceMetric = "chebyshev";
				break;
			}

			// Asking for approval before moving on to the next iteration
			System.out.println();
			System.out.println("Press Enter for " + distanceMetric + "...");
			String temp = input.nextLine();

			// showing the matrix for the user again, so that the path drawn is
			// cleared
			showRandomSquaredGrid(randomlyGenMatrix, true);
			Stopwatch algoClock = new Stopwatch();
			// Initializing the squared grid, closed list and open list
			squaredGrid = new Square[N][N];
			closedList = new ArrayList<Square>();
			/*
			 * The open list is a priority queue, what it does is it will take
			 * two objects compare the f Cost of both of them and order them in
			 * the queue according to their values. this process is continued
			 * for all the objects in the queue. The Integer.compare() will
			 * return -1 if s1.fCost < s2.fCost 1 if s1.fCost > s2.fCost 0 if
			 * s1.fCost == s2.fCost the queue will order elements based on the
			 * value returned
			 */
			openList = new PriorityQueue<>((Square s1, Square s2) -> {
				return Integer.compare(s1.fCost, s2.fCost);
			});

			// setting the startX, startY, endX, endY
			setStartingSquare(sX, sY);

			setEndingSquare(eX, eY);

			// assigning the heuristic cost for each square based on the
			// distance metric
			for (int x = 0; x < N; x++) {
				for (int y = 0; y < N; y++) {
					squaredGrid[x][y] = new Square(x, y);
					if (distanceMetric.equals("manhattan")) {
						squaredGrid[x][y].hCost = (Math.abs(x - eX)) + (Math.abs(y - eY)); // Manhattan
					} else if (distanceMetric.equals("euclidean")) {
						squaredGrid[x][y].hCost = (int) Math.sqrt(Math.pow((x - eX), 2) + Math.pow((y - eY), 2)); // Euclidean
					} else if (distanceMetric.equals("chebyshev")) {
						squaredGrid[x][y].hCost = Math.max(Math.abs(x - eX), Math.abs(y - eY)); // Chebyshev
					}

					// System.out.print(squaredGrid[x][y].hCost + " ");
				}
				// System.out.println();
			}

			// Initializing the starting square fCost to 0
			squaredGrid[sX][sY].fCost = 0;

			/*
			 * Setting all the blocked squares in the squared grid, which is
			 * Corresponding to the randomly generated matrix
			 */
			for (int x = 0; x < N; x++) {
				for (int y = 0; y < N; y++) {
					if (!randomlyGenMatrix[x][y]) {
						setBlockedSquare(x, y);
					}
				}
			}
			System.out.println();
			// running the shortest pathfinding algorithm and calculating the
			// time taken

			findPath();

			// printing the f Cost of each square
			System.out.println();
			System.out.println("Cost for Squares");
			System.out.println("----------------");
			for (int x = 0; x < N; x++) {
				for (int y = 0; y < N; y++) {
					if (squaredGrid[x][y] != null)
						System.out.printf("%-3d ", squaredGrid[x][y].fCost);
					else
						System.out.print("BL  ");
				}
				System.out.println();
			}
			System.out.println();

			List<Square> outputPath = new ArrayList<Square>();

			/*
			 * tracing the path back and storing each square inside the
			 * outputPath ArrayList so that it can be used to draw the path more
			 * efficiently. the total cost is also calculated while tracing
			 * back. if a path is not found then error message is printed
			 */
			if (closedList.contains(squaredGrid[endX][endY])) {
				Square current = squaredGrid[endX][endY];
				outputPath.add(current);
				int totalCost = current.fCost;
				while (current.parentOfCurrentSquare != null) {
					current = current.parentOfCurrentSquare;
					outputPath.add(squaredGrid[current.x][current.y]);
					totalCost += current.fCost;
				}
				System.out.println("Elapsed time = " + algoClock.elapsedTime());
				showStartEnd(squaredGrid, sX, sY, eX, eY);
				for (Square pathSquare : outputPath) {
					drawShortestPath(squaredGrid, pathSquare.x, pathSquare.y);
				}
				showStartEnd(squaredGrid, sX, sY, eX, eY);
				System.out.println("Total Cost : " + totalCost);
			} else {
				System.out.println("No possible path");
				continue;
			}

		}
	}

	// method to find the shortest path between the two given points
	private void findPath() {
		// adding the square to the openList to prepare for path finding
		openList.add(squaredGrid[startX][startY]);

		// Square currently being checked by the algorithm
		Square currentSquare;

		while (true) {
			/*
			 * remove the first element from the openList, which is also the one
			 * with the smallest fCost
			 */
			currentSquare = openList.poll();

			// stop the loop if the path doesnt exist
			if (currentSquare == null) {
				break;
			}

			/*
			 * adding the current square to the closed list so it wont be
			 * checked again in the future
			 */
			closedList.add(currentSquare);

			// stop the loop as the path is found
			if (closedList.contains(squaredGrid[endX][endY])) {
				break;
			}

			boolean top = false, left = false, bottom = false, right = false;

			// all the adjacent squares to the current square
			Square adjacentSquare;

			// Checking the square on top of the current square
			if (currentSquare.x - 1 >= 0) {
				adjacentSquare = squaredGrid[currentSquare.x - 1][currentSquare.y];
				if (adjacentSquare != null) {
					validateAndUpdateSquareCost(currentSquare, adjacentSquare);
					top = true;
				}
			}

			// Checking the square left to the current square
			if (currentSquare.y - 1 >= 0) {
				adjacentSquare = squaredGrid[currentSquare.x][currentSquare.y - 1];
				if (adjacentSquare != null) {
					validateAndUpdateSquareCost(currentSquare, adjacentSquare);
					left = true;
				}
			}

			// Checking the square below the current square
			if (currentSquare.x + 1 < squaredGrid.length) {
				adjacentSquare = squaredGrid[currentSquare.x + 1][currentSquare.y];
				if (adjacentSquare != null) {
					validateAndUpdateSquareCost(currentSquare, adjacentSquare);
					bottom = true;
				}
			}

			// Checking the square right to the current square
			if (currentSquare.y + 1 < squaredGrid.length) {
				adjacentSquare = squaredGrid[currentSquare.x][currentSquare.y + 1];
				if (adjacentSquare != null) {
					validateAndUpdateSquareCost(currentSquare, adjacentSquare);
					right = true;
				}
			}

			// Checking the square top-left to the current square
			if (top && left && currentSquare.y - 1 >= 0) {
				adjacentSquare = squaredGrid[currentSquare.x - 1][currentSquare.y - 1];
				if (adjacentSquare != null) {
					validateAndUpdateSquareCost(currentSquare, adjacentSquare);
				}
			}

			// Checking the square bottom-left to the current square
			if (bottom && left && currentSquare.y - 1 >= 0) {
				adjacentSquare = squaredGrid[currentSquare.x + 1][currentSquare.y - 1];
				if (adjacentSquare != null) {
					validateAndUpdateSquareCost(currentSquare, adjacentSquare);
				}
			}
			
			// Checking the square top-right to the current square
			if (top && right && currentSquare.y + 1 < squaredGrid.length) {
				adjacentSquare = squaredGrid[currentSquare.x - 1][currentSquare.y + 1];
				if (adjacentSquare != null) {
					validateAndUpdateSquareCost(currentSquare, adjacentSquare);
				}
			}

			// Checking the square bottom-right to the current square
			if (bottom && right && currentSquare.y + 1 < squaredGrid.length) {
				adjacentSquare = squaredGrid[currentSquare.x + 1][currentSquare.y + 1];
				if (adjacentSquare != null) {
					validateAndUpdateSquareCost(currentSquare, adjacentSquare);
				}
			}

		}

	}

	// method to validate and update the gCost, hCost and fCost of the adjacent
	// square
	private void validateAndUpdateSquareCost(Square currentSquare, Square adjacentSquare) {
		/*
		 * if the adjacent square is null, meaning its blocked or if its already
		 * checked and in the closed list then stop the execution of this method
		 */
		if (closedList.contains(adjacentSquare)) {
			return;
		}

		// cost to move from current square to the adjacent one
		int movementCost = getCostToMoveFromCurrentSquare(currentSquare, adjacentSquare);

		// updating the g Cost of the adjacent square
		adjacentSquare.gCost += movementCost;

		// calculate the f cost of the adjacent square
		int adjSqrFCost = currentSquare.fCost + movementCost + adjacentSquare.hCost;

		/*
		 * if the adjacent square isnt already in the open list then update the
		 * f Cost and the set the parent of adjacent square to the current
		 * square, then add this square to the open list.
		 */
		if (!openList.contains(adjacentSquare)) {
			adjacentSquare.fCost = adjSqrFCost;
			adjacentSquare.parentOfCurrentSquare = currentSquare;
			openList.add(adjacentSquare);
		} else {
			/*
			 * if it is already in the open list, then check if the f Cost
			 * calculated is lower than the f Cost that is already in that
			 * Square. if so this is a better path and hence update the f Cost
			 * and the parent.
			 */
			if (adjSqrFCost < adjacentSquare.fCost) {
				adjacentSquare.fCost = adjSqrFCost;
				adjacentSquare.parentOfCurrentSquare = currentSquare;
			}
		}

	}

	// method to get the cost required to move from the current square to the
	// adjacent square
	private int getCostToMoveFromCurrentSquare(Square currentSquare, Square adjacentSquare) {
		if (currentSquare != null && adjacentSquare != null) {
			if ((currentSquare.x != adjacentSquare.x) && (currentSquare.y != adjacentSquare.y)) {
				return 14;
			} else {
				return 10;
			}
		} else {
			return 0;
		}
	}

	// return a random N-by-N boolean matrix, where each entry is true with
	// probability p
	public static boolean[][] randomMatrix(int N, double p) {
		boolean[][] a = new boolean[N][N];
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++)
				a[i][j] = StdRandom.bernoulli(p);
		return a;
	}

	// method to show the randomly generated matrix
	public static void showRandomSquaredGrid(boolean[][] a, boolean which) {
		int N = a.length;
		StdDraw.setXscale(-1, N);
		StdDraw.setYscale(-1, N);
		StdDraw.setPenColor(StdDraw.BLACK);
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (a[i][j] == which) {
					StdDraw.setPenColor(StdDraw.WHITE);
					StdDraw.filledSquare(j, N - i - 1, .5);
					StdDraw.setPenColor(StdDraw.BLACK);
					StdDraw.square(j, N - i - 1, .5);
				} else {
					StdDraw.filledSquare(j, N - i - 1, .5);
				}
			}
			StdDraw.setPenColor(StdDraw.BLACK);
		}
	}

	// method to mark the start and end points with a circle
	public static void showStartEnd(Square[][] a, int x1, int y1, int x2, int y2) {
		int N = a.length;
		StdDraw.setXscale(-1, N);
		StdDraw.setYscale(-1, N);
		StdDraw.setPenColor(StdDraw.BLACK);
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (a[i][j] != null) {
					if ((i == x1 && j == y1) || (i == x2 && j == y2)) {
						StdDraw.circle(j, N - i - 1, .5);
					} else {
						StdDraw.square(j, N - i - 1, .5);
					}
				} else {
					StdDraw.filledSquare(j, N - i - 1, .5);
				}
			}
		}
	}

	// method to draw the shortest path between the start and end points
	public static void drawShortestPath(Square[][] a, int x1, int y1) {
		int N = a.length;
		StdDraw.setXscale(-1, N);
		StdDraw.setYscale(-1, N);
		StdDraw.setPenColor(StdDraw.BLACK);
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (a[i][j] != null) {
					if ((i == x1 && j == y1)) {
						StdDraw.setPenColor(StdDraw.GREEN);
						StdDraw.filledSquare(j, N - i - 1, .5);
					} else {
						StdDraw.square(j, N - i - 1, .5);
					}
				} else {
					StdDraw.filledSquare(j, N - i - 1, .5);
				}
				StdDraw.setPenColor(StdDraw.BLACK);
			}
		}
	}

	// method to set the starting coordinates
	public void setStartingSquare(int x, int y) {
		startX = x;
		startY = y;
	}

	// method to set the ending coordinates
	public void setEndingSquare(int x, int y) {
		endX = x;
		endY = y;
	}

	/*
	 * method to mark a specific cell as blocked, in this program a blocked
	 * status is signified using null
	 */
	public void setBlockedSquare(int x, int y) {
		squaredGrid[x][y] = null;
	}
}
