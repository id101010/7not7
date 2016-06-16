package ch.bfh.sevennotseven;

import java.awt.Point;

public class Vertex {
	
	private int dist;
	private Point prev;
	
	public Vertex(int dist, Point prev) {
		this.dist = dist;
		this.prev = prev;
	}
	
	public void setDist(int dist){
		this.dist = dist;
	}
	
	public void setPrev(Point prev){
		this.prev = prev;
	}
	
	public int getDist(){
		return this.dist;
	}
	
	public Point getPrev(){
		return this.prev;
	}

}
