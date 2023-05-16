import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.StringTokenizer;

public class EightPuzzle {
    public static void main(String args[]) throws NumberFormatException, IOException {

        int[][] example = {{4, 1, 2}, {5, 3, 0}, {7, 8, 6}};        //Default Puzzle

        int[][] userInput = new int[3][3];      //Prepare array to store puzzle given by user.

        Node problem = new Node();      //Create a base or start node containing initial puzzle state.

        //Take input from user:

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("This is an 8-Puzzle solver. Please type 1 to use default puzzle or 2 to give a new puzzle and press enter.");

        int puzzleChoice = Integer.parseInt(br.readLine());     //Input choice for deafult or new puzzle.

        if(puzzleChoice == 1) {     //If user selects default puzzle.
            System.out.println("You have selected to solve default puzzle. The default puzzle is as follows:");
            for(int i = 0; i < 3; i++) {
                for(int j = 0; j < 3; j++) {
                    System.out.print(example[i][j] + " ");
                }
                System.out.println();
            }

            problem.state = example;

        } else if(puzzleChoice == 2) {      //If user selects to input new puzzle.
            System.out.println("You have selected to solve a new puzzle. Please enter your puzzle as specified below.");
            System.out.println("Please enter 3 numbers on each line separated by space and enter 0 in place of the blank.");

            //Read the input for the first row of the puzzle.
            System.out.print("Enter the first row: ");
            StringTokenizer st = new StringTokenizer(br.readLine());
            userInput[0][0] = Integer.parseInt(st.nextToken());
            userInput[0][1] = Integer.parseInt(st.nextToken());
            userInput[0][2] = Integer.parseInt(st.nextToken());

            //Read the input for the second row of the puzzle.
            System.out.print("Enter the second row: ");
            st = new StringTokenizer(br.readLine());
            userInput[1][0] = Integer.parseInt(st.nextToken());
            userInput[1][1] = Integer.parseInt(st.nextToken());
            userInput[1][2] = Integer.parseInt(st.nextToken());

            //Read the input for the third row of the puzzle.
            System.out.print("Enter the third row: ");
            st = new StringTokenizer(br.readLine());
            userInput[2][0] = Integer.parseInt(st.nextToken());
            userInput[2][1] = Integer.parseInt(st.nextToken());
            userInput[2][2] = Integer.parseInt(st.nextToken());

            System.out.println("You have entered the following puzzle:");

            for(int i = 0; i < 3; i++) {
                for(int j = 0; j < 3; j++) {
                    System.out.print(userInput[i][j] + " ");
                }
                System.out.println();
            }

            problem.state = userInput;
        } else {
            System.out.println("Invalid choice. Please restart the puzzle solver.");
            return;
        }

        System.out.println("Please enter the choice of algorithm to solve the above puzzle.");
        System.out.println("Enter 1 for Uniform Cost Search.");
        System.out.println("Enter 2 for A* search with Misplaced Tile Heuristic.");
        System.out.println("Enter 3 for A* search with Manhattan Distance Heuristic.");

        int queueingFunction = Integer.parseInt(br.readLine());     //Read the user's choice for search algorithm and heuristic.        
        
        //Initialize the initial parameters for the initial puzzle state for search.

        problem.depth = 0;
        problem.blankRow = 0;
        problem.blankCol = 0;

        //An array to store total number of nodes expanded and maximum queue length throughout the search.
        int[] performance_metrics = new int[2];     //Nodes expanded at index 0 and Max Queue Length at index 1

        //Create the Goal State.
        String goalState = "123456780";

        //Build the string representing initial state of the puzzle for comparing with goal state.
        //Also find the position of the blank in the initial puzzle state.
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                problem.stateString += problem.state[i][j];

                if(problem.state[i][j] == 0) {
                    problem.blankRow = i;   // Row of the blank
                    problem.blankCol = j;   // Column of the blank
                }
            }
        }

        //Measure and store execution time.
        long[] time = new long[2];

        //Call the general search algorithm.    
        int depth = generalSearch(problem, performance_metrics, goalState, queueingFunction, time);

        if(depth == -1) {
            //If the solution is not found in the entire search space, then return failure.
            System.out.println("Failure, No solution found for the given puzzle.");
        } else {
            //If solution is found, then output the depth at which solution / goal state is found. 
            //Also output the total number of nodes expanded, maximum queue length reached in the entire search
            //and total time required to search or reach the goal state.
            System.out.println("Solution found at depth: " + depth);
            System.out.println("Number of nodes expanded: " + performance_metrics[0]);
            System.out.println("Max queue length: " + performance_metrics[1]);
            System.out.println("Total search time : " + (time[1] - time[0]) + " milliseconds");


        }

        
        
        
    }

    public static int generalSearch(Node problem, int[] performance_metrics, String goalState, int queueingFunction, long[] time) {

        Queue<Node> queue = null;
        int heuristic = 0;

        //Depending on choice of queueing function, initialize the type of queue and heuristic option.
        if(queueingFunction == 1){
            queue = new LinkedList<>();     //FIFO queue for Uniform Cost Search
            heuristic = 1;                  //Heuristic for Uniform Cost Search.
        } else {
            queue = new PriorityQueue<>();  //Priority queue for A* search.
            if(queueingFunction == 2) {
                heuristic = 2;              //Heuristic for A* search with Misplaced Tiles
            } else {
                heuristic = 3;              //Heuristic for A* search with Manhattan Distance.
            }
        }

        //Create a hashset to store unique states, to avoid evaluating / expanding states which have already been seen before.
        Set<String> uniqueStates = new HashSet<>();

        queue.add(problem);     // Add initial puzzle state to queue.
        uniqueStates.add(problem.stateString);  // Add initial puzzle state to hashset.

        //Set search start time.

        time[0] = System.currentTimeMillis();


        //Start exploring the search space to find solution and stop when solution found or no more states left to explore.
        while(!queue.isEmpty()) {

            Node currentNode = queue.remove();      //Current state to be evaluated and / or expanded.

            //Check if the current node's state is the goal state

            if(currentNode.stateString.compareTo(goalState) == 0) {    //check goal state.
                time[1] = System.currentTimeMillis();       //end time of search
                System.out.println("Goal State!");
                return currentNode.depth;       //Return depth of goal state.
            }

            //Print next best state to be explored:
            System.out.println("Next Best state to be explored:");
            for(int i = 0; i < 3; i++) {
                for(int j = 0; j < 3; j++) {
                    System.out.print(currentNode.state[i][j] + " ");
                }
                System.out.println();
            }


            // Generate next level states or neighbouring states.

            if((currentNode.blankCol - 1) >= 0) {       //Move the blank to the left.
                //Build blank slide left state string to check if it is a unique state.
                String leftState = "";

                for(int i = 0; i < 3; i++) {
                    for(int j = 0; j < 3; j++) {

                        //Swap the blank and tile to its left in state string.
                        if((i == currentNode.blankRow) && (j == (currentNode.blankCol - 1))) {
                            leftState += currentNode.state[i][j + 1];
                        }
                        else if((i == currentNode.blankRow) && (j == currentNode.blankCol)) {
                            leftState += currentNode.state[i][j - 1];
                        } 
                        //Copy same positions in next puzzle state string for all tiles except for blank and its left tile.
                        else {
                            leftState += currentNode.state[i][j];
                        }                        
                    }
                }

                //If state is unique, only then create a new node for the state.
                if(!uniqueStates.contains(leftState)) {
                    Node left = new Node();
                    left.stateString = leftState;

                    //Replicate current state with change of blank space position.
                    for(int i = 0; i < 3; i++) {
                        for(int j = 0; j < 3; j++) {
                            //Swap the blank and tile to its left in next puzzle state.
                            if((i == currentNode.blankRow) && (j == (currentNode.blankCol - 1))) {
                                left.state[i][j] = currentNode.state[i][j + 1];
                            }
                            else if((i == currentNode.blankRow) && (j == currentNode.blankCol)) {
                                left.state[i][j] = currentNode.state[i][j - 1];
                            } 
                            //Copy same positions in next puzzle state for all tiles except for blank and its left tile.
                            else {
                                left.state[i][j] = currentNode.state[i][j];
                            }                              
                        }
                    }

                    //Store the position of the blank in next puzzle state.
                    left.blankRow = currentNode.blankRow;
                    left.blankCol = currentNode.blankCol - 1;

                    //Store the depth of the next puzzle state in the search space tree.
                    left.depth = currentNode.depth + 1;

                    //Calculate the heuristic for the next puzzle state according to choic of search algorithm and heuristic.
                    if(heuristic == 1) {
                        left.hn = left.depth;
                    } else if(heuristic == 2) {
                        misplacedTileHeuristic(left, goalState);
                    } else {
                        manhattanDistace(left);
                    }

                    queue.add(left);        //Add next state to the queue to be explored.
                    uniqueStates.add(left.stateString);     //Add next state to the to the hashset to avoid adding duplicate states in search queue.
                }            
            }


            if((currentNode.blankCol + 1) < currentNode.state[0].length) {       //Move the blank to the right.
                //Build blank slide right state string to check if it is a unique state.
                String rightState = "";

                for(int i = 0; i < 3; i++) {
                    for(int j = 0; j < 3; j++) {

                        //Swap the blank and tile to its right in state string.
                        if((i == currentNode.blankRow) && (j == (currentNode.blankCol + 1))) {
                            rightState += currentNode.state[i][j - 1];
                        }
                        else if((i == currentNode.blankRow) && (j == currentNode.blankCol)) {
                            rightState += currentNode.state[i][j + 1];
                        } 
                        //Copy same positions in next puzzle state string for all tiles except for blank and its right tile.
                        else {
                            rightState += currentNode.state[i][j];
                        }                        
                    }
                }

                //If state is unique, only then create a new node for the state.
                if(!uniqueStates.contains(rightState)) {
                    Node right = new Node();
                    right.stateString = rightState;

                    //Replicate current state with change of blank space position.
                    for(int i = 0; i < 3; i++) {
                        for(int j = 0; j < 3; j++) {
                            //Swap the blank and tile to its right in next puzzle state.
                            if((i == currentNode.blankRow) && (j == (currentNode.blankCol + 1))) {
                                right.state[i][j] = currentNode.state[i][j - 1];
                            }
                            else if((i == currentNode.blankRow) && (j == currentNode.blankCol)) {
                                right.state[i][j] = currentNode.state[i][j + 1];
                            } 
                            //Copy same positions in next puzzle state for all tiles except for blank and its right tile.
                            else {
                                right.state[i][j] = currentNode.state[i][j];
                            }                              
                        }
                    }

                    //Store the position of the blank in next puzzle state.
                    right.blankRow = currentNode.blankRow;
                    right.blankCol = currentNode.blankCol + 1;

                    //Store the depth of the next puzzle state in the search space tree.
                    right.depth = currentNode.depth + 1;
                    
                    //Calculate the heuristic for the next puzzle state according to choic of search algorithm and heuristic.
                    if(heuristic == 1) {
                        right.hn = right.depth;
                    } else if(heuristic == 2) {
                        misplacedTileHeuristic(right, goalState);
                    } else {
                        manhattanDistace(right);
                    }

                    queue.add(right);       //Add next state to the queue to be explored.
                    uniqueStates.add(rightState);       //Add next state to the to the hashset to avoid adding duplicate states in search queue.
                }
                
            }

            if((currentNode.blankRow - 1) >= 0) {       //Move the blank up.
                //Build blank slide up state string to check if it is a unique state.
                String aboveState = "";

                for(int i = 0; i < 3; i++) {
                    for(int j = 0; j < 3; j++) {

                        //Swap the blank and tile above it in state string.
                        if((i == (currentNode.blankRow - 1)) && (j == currentNode.blankCol)) {
                            aboveState += currentNode.state[i+1][j];
                        }
                        else if((i == currentNode.blankRow) && (j == currentNode.blankCol)) {
                            aboveState += currentNode.state[i-1][j];
                        } 
                        //Copy same positions in next puzzle state string for all tiles except for blank and tile above it.
                        else {
                            aboveState += currentNode.state[i][j];
                        }                        
                    }
                }

                //If state is unique, only then create a new node for the state.
                if(!uniqueStates.contains(aboveState)) {
                    Node above = new Node();
                    above.stateString = aboveState;

                    //Replicate current state with change of blank space position.
                    for(int i = 0; i < 3; i++) {
                        for(int j = 0; j < 3; j++) {

                            //Swap the blank and tile above it in next puzzle state.
                            if((i == currentNode.blankRow - 1) && (j == currentNode.blankCol)) {
                                above.state[i][j] = currentNode.state[i + 1][j];
                            }
                            else if((i == currentNode.blankRow) && (j == currentNode.blankCol)) {
                                above.state[i][j] = currentNode.state[i-1][j];
                            } 
                            //Copy same positions in next puzzle state for all tiles except for blank and tile above it.
                            else {
                                above.state[i][j] = currentNode.state[i][j];
                            }                              
                        }
                    }

                    //Store the position of the blank in next puzzle state.
                    above.blankRow = currentNode.blankRow - 1;
                    above.blankCol = currentNode.blankCol;

                    //Store the depth of the next puzzle state in the search space tree.
                    above.depth = currentNode.depth + 1;

                    //Calculate the heuristic for the next puzzle state according to choic of search algorithm and heuristic.
                    if(heuristic == 1) {
                        above.hn = above.depth;
                    } else if(heuristic == 2) {
                        misplacedTileHeuristic(above, goalState);
                    } else {
                        manhattanDistace(above);
                    }

                    queue.add(above);       //Add next state to the queue to be explored.
                    uniqueStates.add(aboveState);       //Add next state to the to the hashset to avoid adding duplicate states in search queue.
                }            
            }

            if((currentNode.blankRow + 1) < currentNode.state.length) {       //Move the blank down.
                //Build blank slide down state string to check if it is a unique state.
                String belowState = "";

                for(int i = 0; i < 3; i++) {
                    for(int j = 0; j < 3; j++) {

                        //Swap the blank and tile below it in state string.
                        if((i == (currentNode.blankRow + 1)) && (j == currentNode.blankCol)) {
                            belowState += currentNode.state[i-1][j];
                        }
                        else if((i == currentNode.blankRow) && (j == currentNode.blankCol)) {
                            belowState += currentNode.state[i+1][j];
                        } 
                        //Copy same positions in next puzzle state string for all tiles except for blank and tile below it.
                        else {
                            belowState += currentNode.state[i][j];
                        }                        
                    }
                }

                //If state is unique, only then create a new node for the state.
                if(!uniqueStates.contains(belowState)) {
                    Node below = new Node();
                    below.stateString = belowState;

                    //Replicate current state with change of blank space position.
                    for(int i = 0; i < 3; i++) {
                        for(int j = 0; j < 3; j++) {

                            //Swap the blank and tile below it in next puzzle state.
                            if((i == currentNode.blankRow + 1) && (j == currentNode.blankCol)) {
                                below.state[i][j] = currentNode.state[i - 1][j];
                            }
                            else if((i == currentNode.blankRow) && (j == currentNode.blankCol)) {
                                below.state[i][j] = currentNode.state[i+1][j];
                            } 
                            //Copy same positions in next puzzle state for all tiles except for blank and tile below it.
                            else {
                                below.state[i][j] = currentNode.state[i][j];
                            }                              
                        }
                    }

                    //Store the position of the blank in next puzzle state.
                    below.blankRow = currentNode.blankRow + 1;
                    below.blankCol = currentNode.blankCol;

                    //Store the depth of the next puzzle state in the search space tree.
                    below.depth = currentNode.depth + 1;

                    //Calculate the heuristic for the next puzzle state according to choic of search algorithm and heuristic.
                    if(heuristic == 1) {
                        below.hn = below.depth;
                    } else if(heuristic == 2) {
                        misplacedTileHeuristic(below, goalState);
                    } else {
                        manhattanDistace(below);
                    }

                    queue.add(below);       //Add next state to the queue to be explored.
                    uniqueStates.add(belowState);       //Add next state to the to the hashset to avoid adding duplicate states in search queue.
                }            
            }            
            
            performance_metrics[0]++;       //Increment the number of nodes expanded by 1.

            //Update the max queue size if required.
            if(queue.size() > performance_metrics[1]) {
                performance_metrics[1] = queue.size();
            }

            


        }



        return -1;
    }

    //Calculate the Manhattan Distance
    public static void manhattanDistace(Node node) {
        //Build a Hashmap to store positions of all puzzle tiles as expected in the goal state.
        Map<Integer, Integer[]> goalPositions = new HashMap<>();
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {

                if((i == 2) && (j == 2)) {
                    Integer[] coordinates = new Integer[2];
                    coordinates[0] = i;
                    coordinates[1] = j;
                    goalPositions.put(0, coordinates);  //Set blank coordinates.
                } else {
                    Integer[] coordinates = new Integer[2];
                    coordinates[0] = i;
                    coordinates[1] = j;
                    int tile = i * 3 + j + 1;
                    goalPositions.put(tile, coordinates);
                }

                
            }
        }


        int totalDistance = 0;
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                //Get the final expected row number of the current tile at index i, j in the puzzle from the goal state.

                if(node.state[i][j] != 0) {

                    int row = goalPositions.get(node.state[i][j])[0];
                    int col = goalPositions.get(node.state[i][j])[1];

                    //Calculate how for the current tile is from its expected position.
                    int manhattanDistance = Math.abs(i - row) + Math.abs(j - col);
                    totalDistance += manhattanDistance;
                    
                }

                
            }
        }

        node.hn = totalDistance + node.depth;       //Heurisitc = distance from goal state + cost of expanding node state.

    }

    //Misplaced Tile heuristic
    public static void misplacedTileHeuristic(Node node, String goalString) {
        int counter = 0;

        for(int i = 0; i < goalString.length(); i++) {
            if((node.stateString.charAt(i) != '0') && (node.stateString.charAt(i) != goalString.charAt(i))) {
                counter++;
            }
        }

        node.hn = counter + node.depth;     //Heuristic = Number of misplaced tiles + cost of expanding the node state.

    }
}

//Class which represents structure of all state nodes in search space tree.
class Node implements Comparable<Node> {
    int[][] state = new int[3][3];      //state of the puzzle(Position of tiles)
    int depth;      //Depth of the node in the search space tree.
    // boolean visited = false;
    int hn;     //Heuristic of the node state.
    //Coordinates / Position of the blank in node state.
    int blankRow;       //Row number of blank
    int blankCol;       //Column number of blank
    String stateString = new String();      //State string of node state


    //Function to sort different node states in priority queue as per the heuristic value of the node states.
    @Override
    public int compareTo(Node node) {
        return Integer.compare(this.hn, node.hn);
    }
}