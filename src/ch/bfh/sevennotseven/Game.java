package ch.bfh.sevennotseven;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Game {
	
	/**
	 * Interface for Game Update Listeners
	 * Listeners who want to receive game updates events (e.g. after the player has done a move) 
	 *  should implement this interface and call addUpdateListener on the Game class.
	 * @author timo
	 *
	 */
	public interface UpdateListener  {
		public void gameUpdate();
	}
	
	// Constants
	static final int numberOfColors = 5;
	static final int linesPerLevel = 40;
	static final int blocksPerLevel []= {3,4,5}; 
	
	/**
	 * Class that stores one specific state of the game and restores it on demand
	 * @author timo
	 *
	 */
	private class State {
		
		/**
		 * Creates new State Object containing the current game state
		 */
		State() {
			field = new int[size][size];
			//Deep copy field array
			for(int i=0; i<size; i++) {
				field[i] = Arrays.copyOf(Game.this.field[i], size); //copies one dimension
			}
			
			nextBlocks = new ArrayList<Integer>(Game.this.nextBlocks);
			score = Game.this.score;	
			linesLeft = Game.this.linesLeft;
			level = Game.this.level;
		}
		
		private ArrayList<Integer> nextBlocks;
		private int score;
		private int field[][];
		private int linesLeft;
		private int level;
		
		/**
		 * Restores the game state
		 */
		void restore() {
			Game.this.score = score;
			Game.this.field = field;
			Game.this.nextBlocks = nextBlocks;
			Game.this.linesLeft = linesLeft;
			Game.this.level = level;
		}
		
	}
	
	// Private members
	//  State relevant members that can be undone by calling doUndo()
	private int[][] field; //current game field with all blocks. an entry of 0 means there's no block at this position
	private ArrayList<Integer> nextBlocks; //the colors which will be placed in the next move
	private int score; //the current score
	private int level; //the current level
	private int linesLeft; //the number of lines left to the next level
	
	
	private ArrayList<State> lastStates; //Last Game States. Has one entry per move
	private int size; //size of the field along one dimension
	private int freeBlocks; //number of free block positions on the field
	private int freeMoves; //number of freemoves left
	private int numUndos; //number of undos left
	private Random rand; //instance to get random numbers from
	private ArrayList<UpdateListener> updateListeners; //registered listeners
	
	public Game(){
		this(7);
	}
	
	/**
	 * Constructor.
	 * 
	 * @author aaron
	 * @param size
	 */
	public Game (int size) {		
		rand = new Random(); // Initialize random object
		this.updateListeners = new ArrayList<UpdateListener>();
		this.reset(size);
	}
	
	public boolean isGameOver(){
		return false;
	}
	
	public int getAvailFreeMoves(){
		return freeMoves;
	}
	
	public int getAvailUndo(){
		return numUndos;
	}
	
	public int getSize(){
		return size;
	}
	
	public int getScore(){
		return score;
	
	}
	
	public int getLinesLeft(){
		return linesLeft;
	}
	
	public int getLevel(){
		return level;
	}
	
	public List<Integer> getNextBlocks(){
		return nextBlocks;
	}
	
	public int[][] getField(){
		return field;
	}
	
	public boolean canMove(Point src, Point dst){
		return getPath(src, dst) != null;
	}
	
	/**
	 * Adds an update listener to the game object.
	 * The listener will be called when the game has an update (e.g. the user made a move)
	 * 
	 * @author aaron
	 * @param listener
	 */
	public void addUpdateListener(UpdateListener listener){
		updateListeners.add(listener);
	}
	
	/**
	 * Removes the update listener from the game object.
	 * The listener will no longer be called
	 * @author aaron
	 * @param listener
	 */
	public void removeUpdateListener(UpdateListener listener){
		updateListeners.remove(listener);
	}
	
	/**
	 * Emits the game change event to all registered listeners
	 * 
	 * @author aaron
	 */
	private void emitUpdateEvent(){
		for(UpdateListener e: updateListeners) {
			e.gameUpdate();
		}
	}
	
	/**
	 * Try to move the block from src to dst without crossing any walls
	 * 
	 * @author aaron
	 * @param src
	 * @param dst
	 * @return True if a move was successful
	 */
	public boolean doMove(Point src, Point dst){
		if(field[src.x][src.y] == 0 || field[dst.x][dst.y] != 0 || src.equals(dst)){
			return false;
		}
		
		if(!canMove(src, dst)){
			return false; // checking if there is a path from src to dst
		}
		
		saveStep();
		
		field[dst.x][dst.y] = field[src.x][src.y];
		field[src.x][src.y] = 0;
		
		nextStep(dst); // cleanup rows or add new blocks
		
		return true;
	}
	
	/**
	 * Seeks the shortest path between src and dst without crossing any walls
	 * 
	 * @author aaron
	 * @param src
	 * @param dst
	 * @return Shortest path between src and dst, or null if there is no path
	 */
	public List<Point> getPath(final Point src, final Point dst){
		
		ArrayList<Vertex> vertices = new ArrayList<Vertex>(); // List of vertices

		vertices.add(new Vertex(0, src));
	
		// Get a verticies list from the field data
		for(int i= 0; i < size; i++){
			for(int j = 0; j < size; j++){
				if(field[i][j] == 0 && (src.x!=i || src.y!=j)){ //field empty and not src
					vertices.add(new Vertex(Integer.MAX_VALUE, new Point(i, j)));
				}
			}
		}
		
		ArrayList<Vertex> allVerticies = new ArrayList<Vertex>(vertices); // List of vertices
		
		while(!vertices.isEmpty()){ // As long as there are vertices 
			final Vertex u = findNearestVertex(vertices);	
			vertices.remove(u);	// Remove u from the set of vertices
			
			final Point[] offsets = { 
				new Point(0,1),
				new Point(0,-1),
				new Point(1,0),
				new Point(-1,0)
			};
			
			for(int i = 0; i < 4; i++){	// for each neighbour of u ...
				final Point p = u.getPos();
				final Point offs = offsets[i];
				int x = p.x + offs.x;
				int y = p.y + offs.y;
				if(x<0 || y<0 || x>=size || y>= size) continue;
			
				final Vertex v = findVertex(x,y, vertices);
				//distanz_update(u,v)
				if(v!=null){
					int alternative = u.getDist()+1;
					if( alternative< v.getDist()) { 
						v.setDist(alternative);
						v.setPrev(u);
					}
				}
			}
		}

		return reconstructShortestPath(allVerticies, src,dst);
	}
	
	/**
	 * Undo the last move if there are enough available undos.
	 * 
	 * @author aaron
	 * @return True if undo was possible.
	 */
	public boolean doUndo(){
		if(getAvailUndo() > 0 && lastStates.size() > 0){
			
			lastStates.remove(lastStates.size() - 1).restore();
			
			numUndos--;

			emitUpdateEvent();
			
			return true;
		}
		
		return false;
	}
	
	private void saveStep() {
		lastStates.add(new State()); //add a new State Object (which will be initialized to the current game state) to the backup list
	}
	
	/**
	 * Move a block from src to dst and jump over walls.
	 * Only possible if availableFreeMoves()>0
	 * 
	 * @author aaron
	 * @param src
	 * @param dst
	 * @return True if freemove was possible.
	 */
	public boolean doFreeMove(Point src, Point dst){
		//move without path checking
		if(getAvailFreeMoves() <= 0 ) {
			return false;
		}
		
		if(field[src.x][src.y]==0) {
			return false;
		}
		
		saveStep();
		
		field[dst.x][dst.y] = field[src.x][src.y];
		field[src.x][src.y] = 0;
		
		freeMoves--;
		
		nextStep(dst);
		
		return true;
	}
	
	/**
	 * Reset game score, field and state.
	 * 
	 * @author aaron
	 */
	public void reset(int size){
		this.size = size;
		this.freeBlocks = size * size;
		
		// Initialize new blocks and oldMove list
		nextBlocks = new ArrayList<Integer>();
		nextBlocks.add(1);
		nextBlocks.add(2);
		nextBlocks.add(3);
		
		// Initialize field, level and score
		field = new int[size][size];

		
		//undo stuff
		lastStates = new ArrayList<State>();
		
		level = 1;
		score = 0;
		numUndos = 2;
		freeMoves = 0;
		linesLeft=linesPerLevel;
		
		// Populate game field
		this.populateField();
	}
	
	/**
	 * Finds the nearest vertex to start
	 * 
	 * @author aaron
	 * @param vertices
	 * @return Nearest vertex to the fist element of the given vertices list.
	 */
	private Vertex findNearestVertex(final List<Vertex> vertices){
		Vertex tmp = vertices.get(0);
		
		for (int i = 1; i < vertices.size(); i++) {
			Vertex n = vertices.get(i);
			if(n.getDist() < tmp.getDist()) {
				tmp = n;
			}
		}
		
		return tmp;
	}
	
	/**
	 * Helper function for pathfinding. Finds a vertex corresponding to the given coordinate.
	 * 
	 * @author aaron
	 * @param x
	 * @param y
	 * @param vertices
	 * @return Vertex with the given position out of a list of vertices.
	 */
	private Vertex findVertex(int x, int y, final List<Vertex> vertices){
		return findVertex(new Point(x,y), vertices);
	}

	/**
	 * Helper function for pathfinding. Finds a vertex corresponding to the given point.
	 * 
	 * @author aaron
	 * @param pos
	 * @param vertices
	 * @return Vertex with the given position out of a list of vertices.
	 */
	private Vertex findVertex(final Point pos, final List<Vertex> vertices){
		for (int i = 0; i < vertices.size(); i++) {
			Vertex n = vertices.get(i);
			if(n.getPos().equals(pos)) {
				return n;
			}
		}
		return null;
	}
	
	/**
	 * Helper function for pathfinding. Returns shortest path between src and dst in a given set of vertices.
	 * 
	 * @author aaron
	 * @param vertices
	 * @param src
	 * @param dst
	 * @return Shortest path between two given points in a list of vertices.
	 */
	private List<Point> reconstructShortestPath(final List<Vertex> vertices, final Point src, final Point dst){
		ArrayList<Point> path = new ArrayList<Point>();
		path.add(dst);
		Vertex u = findVertex(dst, vertices);
		if(u==null) {
			return null;
		}
		while(u.getPrev()!=null){
			u= u.getPrev();
			path.add(0, u.getPos());
		}
		if(u!=findVertex(src, vertices)){
			return null;
		}
		return path;
	}
	/**
	 * Calculates the next game step. This method will either call populateField, or it will cleanup blocks 
	 *
	 * @author aaron
	 * @param lastPoint
	 */
	private void nextStep(final Point lastPoint){
		if(!checkRemoveBlocks(lastPoint)){
			populateField(); //add new blocks	
		} else {
			linesLeft--;
			if(linesLeft==0){
				level++;
				numUndos++;
				linesLeft=linesPerLevel;
			
			}
		}
		
		emitUpdateEvent();

	}
	
	/**
	 * Collision detection and block removal if there are 4 or more blocks in a row in any direction.
	 * Also increases the score if necessary
	 * 
	 * @author aaron
	 * @param lastPoint
	 * @return True if any blocks got removed
	 */
	private boolean checkRemoveBlocks(final Point lastPoint){
		
		//Offset to reach the neighbors
		final Point[] offsets = { 
				new Point(0,1),	 // right
				new Point(0,-1), // left
				new Point(1,0),	 // bottom
				new Point(-1,0), // top
				new Point(1,1),  // bottom right
				new Point(-1,-1),// top left
				new Point(-1,1), // bottom left
				new Point(1,-1)  // top right
		};
		
		int matches[] = new int[8]; //number of blocks of the same color in each direction
		int color = field[lastPoint.x][lastPoint.y]; //current block color
		
		//Count the matches per direction
		for(int i = 0; i < 8; i++){ //for every direction
			Point offset = offsets[i];
			Point current = new Point(lastPoint);
			
			int matchcount = 0;		
			while(true){ //iterate until one of the conditions below fail
				current.translate(offset.x, offset.y); //walk 1 step in the given direction
				
				//Abort if out of bounds
				if(current.x < 0 || current.x >= size || current.y < 0 || current.y >= size) break;
				
				//Abort if block there has not the correct color
				if(field[current.x][current.y] != color) break;
				
				//Else: we found one more block that matches an we can continue the loop to seach for more
				matchcount++;
			}
			
			matches[i] = matchcount;
		}
		
		//Detect in which directions we have at least 4 blocks/matches
		//we always look at two directions together e.g right and left
		int distinctmatches = 0; //number of directions that have a match
		
		for(int i = 0; i < 4; i++){ //for the 4 direction pairs
			int totalmatches = 1 + matches[i*2] + matches[i*2+1]; //sum up matches of both directions + current block
			if(totalmatches >= 4){ //4 or more blocks => Block-group matched!
				distinctmatches++;
				for(int j = 0; j < 2; j++){ //now remove the blocks
					Point offset = offsets[j+i*2];
					Point current = new Point(lastPoint);

					for(int k = 0; k < matches[j+i*2]; k++){ //for both directions in this direction pair
						current.translate(offset.x, offset.y); //go one step in the direction
						field[current.x][current.y] = 0;
						freeBlocks++;
					}
				}
			} else { //not enough matches in that direction pair => reset matchcount
				matches[i*2] = 0;
				matches[i*2 +1] = 0;
			}
		}
		
		if(distinctmatches > 0){ //match in at least on direction (pair)
			field[lastPoint.x][lastPoint.y] = 0; //remove current block
			freeBlocks++;
			
			if(distinctmatches > 1){
				freeMoves++;
			}
			
			int sum = 0;
			for( Integer i : matches ) sum += i; //Sum up the number of blocks which participate in a match
			//(the other directions matches have been set to 0)
				
			score += (1 + distinctmatches * sum); 
			
			System.out.println("Score: " + score);
			
			return true;
		}
		
		return false;
	
	}
	
	/**
	 * Adds n new blocks to random positions on the field, according to the level number.
	 * 
	 * @author aaron
	 */
	private void populateField(){
		
		// while there are blocks left in nextBlocks
		while((nextBlocks.size() > 0) && (freeBlocks > 0)){
			int x = rand.nextInt(size); // get random x position
			int y = rand.nextInt(size); // get random y position
			
			// if the position is free
			if(field[x][y] == 0){
				field[x][y] = nextBlocks.remove(0); // fill with the first element of nextBlocks
				freeBlocks--;
				checkRemoveBlocks(new Point(x,y));
			}
		}
		
		int blocksToAdd = 0;
		if(level <= blocksPerLevel.length) {
			blocksToAdd = blocksPerLevel[level-1];
		} else {
			blocksToAdd = blocksPerLevel[blocksPerLevel.length-1];
		}
		
		// add n new colors to nextBlocks according to the level number.
		for(int i = 0; i < blocksToAdd; i++){
			nextBlocks.add(1 + rand.nextInt(numberOfColors));	
		}
	}
}
