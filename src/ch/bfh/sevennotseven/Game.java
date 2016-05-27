package ch.bfh.sevennotseven;

import java.awt.Point;
import java.util.List;

public class Game {
	
	private int[][] field;
	
	
	public Game(){
		this(7);
	}
	
	public Game (int size) {
		field = new int[size][size];
	
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
		return null;
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
		
	}
	
}
