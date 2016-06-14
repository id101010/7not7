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
		return 0;
	
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
		return false;
	}
	
	public boolean doMove(Point src, Point dst){
		return false;
	}
	
	public List<Point> getPath(Point src, Point dst){
		return null;
	}
	
	public boolean doUndo(){
		return false;		
	}
	
	public boolean doFreeMove(Point src, Point dst){
		return false;
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
	
	/*
	 * Calculates the next game step.
	 * Add n new blocks to random positions on the field, 
	 * according to the level number.
	 */
	public void populateField(){
		
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
