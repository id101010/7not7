package ch.bfh.sevennotseven;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Game {
	
	interface UpdateListener  {
		public void gameUpdate();
	}
	
	// Constants
	static final int numberOfColors = 5;
	static final int linesPerLevel = 40;
	static final int blocksPerLevel []= {3,4,5}; 
	
	// Private members
	private Integer[][] field;
	private ArrayList<Integer> nextBlocks;
	private ArrayList<Integer[][]> oldFields;
	private int level;
	private int score;
	private int size;
	private int freeBlocks;
	private int freeMoves;
	private int numUndos;
	private int linesLeft;
	private Random rand;
	private ArrayList<UpdateListener> updateListeners;
	
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
	
	public Integer[][] getField(){
		return field;
	
	}
	
	public boolean canMove(Point src, Point dst){
		return getPath(src, dst) != null;
		
	}
	
	/**
	 * Adds an update listener to the game object.
	 * 
	 * @author aaron
	 * @param listener
	 */
	public void addUpdateListener(UpdateListener listener){
		updateListeners.add(listener);
	}
	
	/**
	 * Removes the update listener from the game object.
	 * 
	 * @author aaron
	 * @param listener
	 */
	public void removeUpdateListener(UpdateListener listener){
		updateListeners.remove(listener);
	}
	
	/**
	 * Updatelistener callback, updates game when a listener gets triggered.
	 * 
	 * @author aaron
	 */
	private void emitUpdateEvent(){
		for(UpdateListener e: updateListeners) {
			e.gameUpdate();
		}
	}
	
	/**
	 * Check if there is a valid path from src to dst and move a block if possible.
	 * 
	 * @author aaron
	 * @param src
	 * @param dst
	 * @return True if a move from src to dst is possible.
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
	 * Pathfinding of shortest path between src and dst.
	 * 
	 * @author aaron
	 * @param src
	 * @param dst
	 * @return Shortest path between src and dst.
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
		if(getAvailUndo() > 0 && oldFields.size() > 0){

			field= oldFields.remove(oldFields.size()-1);
			numUndos--;
			
			emitUpdateEvent();
			
			return true;
		}
		
		return false;
	}
	
	private void saveStep() {
		Integer[][] fieldCopy = new Integer[size][size];
		for(int i=0; i<size; i++) {
			fieldCopy[i] = Arrays.copyOf(field[i], size);
		}
		
		oldFields.add(fieldCopy);
	}
	
	/**
	 * Do a free move if freeMoves < 0.
	 * 
	 * @author aaron
	 * @param src
	 * @param dst
	 * @return True if freemove is posible.
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
		field = new Integer[size][size];
		for(int i=0; i<size; i++) {
			Arrays.fill(field[i], 0);
		}
		
		oldFields = new ArrayList<Integer[][]>();
		
		level = 1;
		score = 0;
		numUndos = 100;
		freeMoves = 100;
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
	 * 
	 * @author aaron
	 * @param lastPoint
	 * @return True if 4 or more blocks got removed.
	 */
	private boolean checkRemoveBlocks(final Point lastPoint){
		
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
		
		int matches[] = new int[8];
		int color = field[lastPoint.x][lastPoint.y];
		
		for(int i = 0; i < 8; i++){
			Point offset = offsets[i];
			Point current = new Point(lastPoint);
			
			int matchcount = 0;

			
			while(true){
				current.translate(offset.x, offset.y);
				if(current.x < 0 || current.x >= size || current.y < 0 || current.y >= size) break;
				if(field[current.x][current.y] != color) break;
				matchcount++;
			}
			
			matches[i] = matchcount;
		}
		
		int distinctmatches = 0;
		
		for(int i = 0; i < 4; i++){
			int totalmatches = 1 + matches[i*2] + matches[i*2+1];
			if(totalmatches >= 4){
				distinctmatches++;
				for(int j = 0; j < 2; j++){
					Point offset = offsets[j+i*2];
					Point current = new Point(lastPoint);

					for(int k = 0; k < matches[j+i*2]; k++){
						current.translate(offset.x, offset.y);
						field[current.x][current.y] = 0;
						freeBlocks++;
					}
				}
			}
		}
		
		if(distinctmatches > 0){
			field[lastPoint.x][lastPoint.y] = 0;
			freeBlocks++;
			
			if(distinctmatches > 1){
				freeMoves++;
			}
			
			int sum = 0;
			
			for( Integer i : matches ) sum += i;
			
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
