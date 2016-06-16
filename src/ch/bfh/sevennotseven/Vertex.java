package ch.bfh.sevennotseven;

import java.awt.Point;

public class Vertex {
	
	private int dist;
	private Point pos;
	private Vertex prev;
	
	public Vertex(int dist, Point pos) {
		this.dist = dist;
		this.pos = pos;
		this.prev = new Vertex(0, new Point(0,0));
	}
	
	public void setDist(int dist){
		this.dist = dist;
	}
	
	public void setPos(Point pos){
		this.pos = pos;
	}
	
	public void setPrev(Vertex prev){
		this.prev = prev;		
	}
	
	public int getDist(){
		return this.dist;
	}
	
	public Point getPos(){
		return this.pos;
	}
	
	public Vertex getPrev(){
		return this.prev;
	}

}
