package ch.bfh.sevennotseven;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {
	
	// Constants
	static final int numberOfColors = 5;
	
	// Private members
	private int[][] field;
	private ArrayList<Integer> nextBlocks;
	private int level;
	private int score;
	private int size;
	private int freeBlocks;
	private Random rand;
	
	public Game(){
		this(7);
	}
	
	public Game (int size) {
		/* Initialize variables */
		this.size = size;
		this.level = 1;
		this.score = 0;
		this.freeBlocks = size * size;
		
		rand = new Random(); // Initialize random object
		this.reset();
	}
	
	public boolean isGameOver(){
		return false;
	
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
		//TODO: uncomment next line and implement getPath
		
		//return getPath(src, dst)!=null;
		return true;
	}
	
	public boolean doMove(Point src, Point dst){
		
		if(!canMove(src, dst)) {
			return false; //checking if there is a path from src to dest
		}
		
		if(field[src.x][src.y]==0) {
			return false;
		}
		
		field[dst.x][dst.y] = field[src.x][src.y];
		field[src.x][src.y] = 0;
		
		nextStep(dst); //cleanup rows or add new blocks
		
		return true;
	}
	
	/*
	 *  Pathfinding of shortest path between src and dst.
	 */
	public ArrayList<Vertex> getPath(Point src, Point dst){
		
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();	// List of vertices
		ArrayList<Vertex> path = new ArrayList<Vertex>();	// Output
		
		Vertex u = new Vertex(0, src);
		Vertex v = new Vertex(0, dst);
		int tmp, tmp_i = 0;
		int alt = 0;
		
		for(int i = 0; i < size*size; i++){
			vertices.add(new Vertex(Integer.MAX_VALUE, new Point(i%7,i%7))); // Initialize all vertices
		}
		
		vertices.get(src.x * src.y).setDist(0); // Set distance from source to source
		
		
		while(!vertices.isEmpty()){ 			// As long as there are vertices 
			tmp = minDistance(u, vertices);		// Get the index of v with the least distance to u
			u = vertices.get(tmp);
			vertices.remove(tmp);				// Remove u from the set of vertices
			
			for(int i = 0; i < 4; i++){			// for each neighbour of u ...
				if(i == 0){
					tmp_i = u.getPrev().x * u.getPrev().y - 7; // neighbour above
				}
				
				if(i == 1){
					tmp_i = u.getPrev().x * u.getPrev().y + 7; // neighbour below					
				}
				
				if(i == 2){
					tmp_i = u.getPrev().x * u.getPrev().y - 1; // left neighbour							
				}
				
				if(i == 3){
					tmp_i = u.getPrev().x * u.getPrev().y + 1; // right neighbour						
				}
				
				if(tmp_i >= 0 && tmp_i <= 49){
					alt = u.getDist() + distanceBetween(u,v);
					if(alt < v.getDist()){	// A shorter path has been found
						v.setDist(alt);
						v.setPrev(u.getPrev());
						path.add(v);
					}
				}
			}
		}

		return path;
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
		nextStep(dst); //cleanup rows or add new blocks
		
		return true;
		
		
	}
	
	public int getAvailFreeMoves(){
		return 0;
	}
	
	public int getAvailUndo(){
		return 0;
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
		
		// Populate game field
		this.populateField();
	}
	
	/**
	 * Calculates the minimal distance between a vertex and a set of vertices.
	 */
	private int minDistance(Vertex v, ArrayList<Vertex> vertices){
		int dist = Integer.MAX_VALUE;
		int tmp = 0;
		int out = 0;
		
		for (int i = 0; i < vertices.size(); i++) {
			tmp = distanceBetween(v, vertices.get(i));
			
			if(tmp < dist){
				dist = tmp;
				out = i;
			}
		}
		
		return out;
	}
	
	/**
	 * Calculates the distance between two vertices.
	 */
	private int distanceBetween(Vertex v1, Vertex v2){
		int dx = 0;
		int dy = 0;
		
		dx = v2.getPrev().x - v2.getPrev().x;
		dy = v2.getPrev().x - v2.getPrev().x;
		
		return (int) Math.sqrt(dx*dx + dy*dy);
	}
	
	/**
	 * Calculates the next game step. This method will either call populateField, or it will cleanup blocks 
	 */
	private void nextStep(Point lastPoint) {
		//TODO: Check if there are any new rows (with at least 4 elements horizontally, vertically, or diagonal) near lastpoint
		//TODO:  if so: remove the row, add some points or extras and quit
		//TODO:  if not: 
		populateField(); //add new blocks
		
	}
	
	/*

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
			}
		}
		
		// add n new colors to nextBlocks according to the level number.
		for(int i = 0; i < (level * 3); i++){
			nextBlocks.add(1 + rand.nextInt(numberOfColors));	
		}
	}
}
