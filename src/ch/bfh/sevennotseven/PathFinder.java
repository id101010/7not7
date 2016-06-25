package ch.bfh.sevennotseven;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Class which provides helper to find the shortest path between two points
 * Based on pseudo-code from https://de.wikipedia.org/wiki/Dijkstra-Algorithmus#Algorithmus_in_Pseudocode
 *
 */
public class PathFinder {
	
	
	/**
	 * 
	 * Helper class which represents a Node/Vertex of a Graph
	 *
	 */
	public static class Vertex {
		
		/* Class Members */
		private int dist;
		private Point pos;
		private Vertex prev;
		
		/**
		 * Costructor
		 * 
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
		 * @param int dist
		 */
		public void setDist(int dist){
			this.dist = dist;
		}
		
		/**
		 * Set current position
		 * 
		 * @param Point pos
		 */
		public void setPos(Point pos){
			this.pos = pos;
		}
		
		/**
		 * Set previous vertex
		 * 
		 * @param Vertex prev
		 */
		public void setPrev(Vertex prev){
			this.prev = prev;		
		}
		
		/**
		 * Get the distance 
		 * 
		 * @return int dist
		 */
		public int getDist(){
			return this.dist;
		}
		
		/** 
		 * Get current position
		 * 
		 * @return Point pos
		 */
		public Point getPos(){
			return this.pos;
		}
		
		/** 
		 * Get previous vertex
		 * 
		 * @return Vertex prev
		 */
		public Vertex getPrev(){
			return this.prev;
		}

	}
	
	//Storage for last values
	private ArrayList<Vertex> verticies = null;
	private Point lastSrc = null;
	private int[][] lastField = null;
	private int lastSize;
	
	
	
	/**
	 * Calculates the cost to reach each block starting from point src
	 * @param field game field
	 * @param size game size
	 * @param src starting point
	 */
	public void calculateCosts(final int[][] field, int size, final Point src) {
		ArrayList<Vertex> openVerticies = new ArrayList<Vertex>(); // List of vertices

		openVerticies.add(new Vertex(0, src));
	
		// Get a verticies list from the field data
		for(int i= 0; i < size; i++){
			for(int j = 0; j < size; j++){
				if(field[i][j] == 0 && (src.x!=i || src.y!=j)){ //field empty and not src
					openVerticies.add(new Vertex(Integer.MAX_VALUE, new Point(i, j)));
				}
			}
		}
		
		ArrayList<Vertex> allVerticies = new ArrayList<Vertex>(openVerticies); // List of vertices
		
		while(!openVerticies.isEmpty()){ // As long as there are vertices 
			final Vertex u = findNearestVertex(openVerticies);	
			openVerticies.remove(u);	// Remove u from the set of vertices
			
			if(u.getDist()==Integer.MAX_VALUE) {
				continue;
			}
			
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
			
				final Vertex v = findVertex(x,y, openVerticies);
				//distanz_update(u,v)
				if(v!=null){
					int alternative = u.getDist()+1; //alternative has cost of current path + 1 (=cost to reach neighbor)
					if( alternative< v.getDist()) { 
						v.setDist(alternative);
						v.setPrev(u);
					}
				}
			}
		}
		
		//assign new values to storage
		lastSrc = src;
		verticies = allVerticies;	
		lastField = field;
		lastSize = size;		
	}
	
	
	
	/**
	 * Seeks the shortest path to dst without crossing any walls
	 * @param dst
	 * @return Shortest path between src and dst, or null if there is no path
	 */
	public List<Point> getPath(final Point dst){
		if(lastSrc==null || verticies == null || lastField == null) return null;
		
		ArrayList<Point> path = new ArrayList<Point>();
		path.add(dst);
		Vertex u = findVertex(dst, verticies);
		if(u==null) {
			return null;
		}
		while(u.getPrev()!=null){
			u= u.getPrev();
			path.add(0, u.getPos());
		}
		if(u!=findVertex(lastSrc, verticies)){
			return null;
		}
		return path;
	}
	
	/**
	 * Returns all points which are reachable from src, without crossing any walls
	 * @return
	 */
	public List<Point> getReachablePoints() {
		if(lastSrc==null || verticies == null || lastField == null) return null;
		ArrayList<Point> res = new ArrayList<Point>();
		for(int x=0; x<lastSize; x++) {
			for(int y=0; y<lastSize; y++) {
				if(lastSrc.x == x && lastSrc.y ==y) continue;
				if(lastField[x][y]!=0) continue;
				Vertex u = findVertex(x, y, verticies);
				if(u==null) continue;
				if(u.getPrev()!=null && u.getDist()!= Integer.MAX_VALUE) {
					res.add(u.getPos());
				}
			}
		}
		return res;
	}
	
	/**
	 * Returns all points which are UNreachable from src, without crossing any walls
	 * @return
	 */
	public List<Point> getUnreachablePoints() {
		if(lastSrc==null || verticies == null || lastField == null) return null;
		ArrayList<Point> res = new ArrayList<Point>();
		for(int x=0; x<lastSize; x++) {
			for(int y=0; y<lastSize; y++) {
				if(lastSrc.x == x && lastSrc.y ==y) continue;
				if(lastField[x][y]!=0) continue;
				Vertex u = findVertex(x, y, verticies);
				if(u==null|| u.getPrev() == null || u.getDist() == Integer.MAX_VALUE) {
					res.add(u.getPos());
				}
			}
		}
		return res;
	}
	

	/**
	 * Finds the nearest vertex to start
	 * 
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
	

}
