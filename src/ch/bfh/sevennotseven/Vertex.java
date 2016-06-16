package ch.bfh.sevennotseven;

import java.awt.Point;

public class Vertex {
	
	/* Class Members */
	private int dist;
	private Point pos;
	private Vertex prev;
	
	/**
	 * Costructor
	 * 
	 * @author aaron
	 * @param int dist
	 * @param Point pos
	 */
	public Vertex(int dist, Point pos) {
		this.dist = dist;
		this.pos = pos;
		this.prev = null;
	}

	/**
	 * Set the distance
	 * 
	 * @author aaron
	 * @param int dist
	 */
	public void setDist(int dist){
		this.dist = dist;
	}
	
	/**
	 * Set current position
	 * 
	 * @author aaron
	 * @param Point pos
	 */
	public void setPos(Point pos){
		this.pos = pos;
	}
	
	/**
	 * Set previous vertex
	 * 
	 * @author aaron
	 * @param Vertex prev
	 */
	public void setPrev(Vertex prev){
		this.prev = prev;		
	}
	
	/**
	 * Get the distance 
	 * 
	 * @author aaron
	 * @return int dist
	 */
	public int getDist(){
		return this.dist;
	}
	
	/** 
	 * Get current position
	 * 
	 * @author aaron
	 * @return Point pos
	 */
	public Point getPos(){
		return this.pos;
	}
	
	/** 
	 * Get previous vertex
	 * 
	 * @author aaron
	 * @return Vertex prev
	 */
	public Vertex getPrev(){
		return this.prev;
	}

}
