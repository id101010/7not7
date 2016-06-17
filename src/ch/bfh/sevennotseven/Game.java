package ch.bfh.sevennotseven;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {
	
	interface UpdateListener  {
		public void gameUpdate();
	}
	
	
	// Constants
	static final int numberOfColors = 5;
	
	// Private members
	private int[][] field;
	private ArrayList<Integer> nextBlocks;
	private int level;
	private int score;
	private int size;
	private int freeBlocks;
	private int freeMoves;
	private int numUndos;
	private Random rand;
	private ArrayList<UpdateListener> updateListeners;
	
	public Game(){
		this(7);
	}
	
	public Game (int size) {
		/* Initialize variables */
		this.size = size;
		this.level = 1;
		this.score = 0;
		this.freeBlocks = size * size;
		this.updateListeners = new ArrayList<UpdateListener>();
		
		rand = new Random(); // Initialize random object
		this.reset();
	}
	
	public void addUpdateListener(UpdateListener listener) {
		updateListeners.add(listener);
	}
	public void removeUpdateListener(UpdateListener listener) {
		updateListeners.remove(listener);
	}
	private void emitUpdateEvent() {
		for(UpdateListener e: updateListeners) {
			e.gameUpdate();
		}
	}
	
	public boolean isGameOver(){
		return false;
	
	}
	
	public int getSize() {
		return size;
	}
	
	public int getScore(){
		return score;
	
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
		return getPath(src, dst)!=null;
		
	}
	
	public boolean doMove(Point src, Point dst){
		if(field[src.x][src.y]==0 ||field[dst.x][dst.y] !=0 || src.equals(dst)) {
			return false;
		}
		
		if(!canMove(src, dst)) {
			return false; // checking if there is a path from src to dest
		}
		
		field[dst.x][dst.y] = field[src.x][src.y];
		field[src.x][src.y] = 0;
		
		nextStep(dst); // cleanup rows or add new blocks
		
		return true;
	}
	
	/**
	 *  Pathfinding of shortest path between src and dst.
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
	
	public boolean doUndo(){
		return false;	
	}
	
	public boolean doFreeMove(Point src, Point dst){
		//move without path checking
		if(getAvailFreeMoves() <= 0 ) {
			return false;
		}
		
		if(field[src.x][src.y]==0) {
			return false;
		}
		
		field[dst.x][dst.y] = field[src.x][src.y];
		field[src.x][src.y] = 0;
		
		freeMoves--;
		
		nextStep(dst); //cleanup rows or add new blocks
		
		return true;
		
		
	}
	
	public int getAvailFreeMoves(){
		return freeMoves;
	}
	
	public int getAvailUndo(){
		return numUndos;
	}
	
	public void reset(){
		// Initialize new blocks
		nextBlocks = new ArrayList<Integer>();
		nextBlocks.add(1);
		nextBlocks.add(2);
		nextBlocks.add(3);
		
		// Initialize field, level and score
		field = new int[size][size];
		level = 1;
		score = 0;
		numUndos = 0;
		freeMoves = 0;
		
		// Populate game field
		this.populateField();
	}
	
	/**
	 * Finds the nearest vertex to start
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
	
	private Vertex findVertex(int x, int y, final List<Vertex> vertices) {
		return findVertex(new Point(x,y), vertices);

	}

	private Vertex findVertex(final Point pos, final List<Vertex> vertices) {
		for (int i = 0; i < vertices.size(); i++) {
			Vertex n = vertices.get(i);
			if(n.getPos().equals(pos)) {
				return n;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param vertices
	 * @param src
	 * @param dst
	 * @return
	 */
	private List<Point> reconstructShortestPath(final List<Vertex> vertices, final Point src, final Point dst) {
		ArrayList<Point> path = new ArrayList<Point>();
		path.add(dst);
		Vertex u = findVertex(dst, vertices);
		if(u==null) {
			return null;
		}
		while(u.getPrev()!=null) {
			u= u.getPrev();
			path.add(0, u.getPos());
		}
		if(u!=findVertex(src, vertices)) {
			return null;
		}
		return path;
	}
	/**
	 * Calculates the next game step. This method will either call populateField, or it will cleanup blocks 
	 *
	 * @author 
	 * @param lastPoint
	 */
	private void nextStep(final Point lastPoint) {
		if(!checkRemoveBlocks(lastPoint)){
			populateField(); //add new blocks	
		}
		emitUpdateEvent();
	}
	
	/**
	 * Collision detection and block removal if there are 4 or more blocks in a row in any direction.
	 * 
	 * @author 
	 * @param lastPoint
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
			
			if(distinctmatches < 1){
				freeMoves++;
			}
			
			return true;
		}
		
		return false;
	
	}
	
	/**
	 * Adds n new blocks to random positions on the field, 
	 * according to the level number.
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
		
		// add n new colors to nextBlocks according to the level number.
		for(int i = 0; i < (level * 3); i++){
			nextBlocks.add(1 + rand.nextInt(numberOfColors));	
		}
	}
}
