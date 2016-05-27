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
	private Random rand;
	
	public Game(){
		this(7);
	}
	
	public Game (int size) {
		this.size = size;
		this.reset();
	
	}
	
	public boolean isGameOver(){
		return false;
	
	}
	
	public int getScore(){
		return 0;
	
	}
	
	public int getLevel(){
		return 0;
	}
	
	public List<Integer> getNextBlocks(){
		return nextBlocks;
	}
	
	public int[][] getField(){
		return null;
	
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
			
		// Initialize random object
		rand = new Random();
		
		// Populate game field
		this.populateField();
	}
	
	/*
	 * Calculates the next game step.
	 * Add n new blocks to random positions on the field, 
	 * according to the level number.
	 */
	private void populateField(){
		
		// while there are blocks left in nextBlocks
		while(nextBlocks.size() > 0){
			int x = rand.nextInt(size); // get random x position
			int y = rand.nextInt(size); // get random y position
			
			// if the position is free
			if(field[x][y] == 0){
				field[x][y] = nextBlocks.remove(0); // fill with the first element of nextBlocks
			}
			
			// add n new colors to nextBlocks according to the level number.
			for(int i = 0; i < (level * 3); i++){
				nextBlocks.add(rand.nextInt(numberOfColors));	
			}
		}
	}
}
