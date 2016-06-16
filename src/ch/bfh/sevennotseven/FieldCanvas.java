package ch.bfh.sevennotseven;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JPanel;

public class FieldCanvas extends JPanel{
	private static final long serialVersionUID = 1L;
	static final int border = 5;
	static final Color[] colors = {Color.red,Color.green, Color.blue, Color.yellow,Color.magenta};
	
	
	
	private int size;
	private Game game;
	private Point src;
	private Point dst;
	private List<Point> path;
	
	FieldCanvas(){
		MouseAdapter ad = new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				
				Point p = FieldCanvas.this.getClickPoint(e.getPoint());
				if(p==null) { //invalid click
					src = null;
				} else {
					src = p;
				}
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				super.mouseDragged(e);
				if(src!=null) {
					Point lastDst = dst;
					dst = FieldCanvas.this.getClickPoint(e.getPoint());
					if(lastDst!=dst) { //hovered field changed
						path= game.getPath(src, dst);
						repaint();
					}
				} else {
					dst = null;
					path = null;
				}
			};
			
			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				dst = FieldCanvas.this.getClickPoint(e.getPoint());
				path = null;
				if(dst != null && src!=null) {
					System.out.println("Moving from "+src.toString()+ " to "+dst.toString());
					game.doMove(src, dst);
					repaint();
				}
				src = null;
			}
		};
		
		addMouseListener(ad);
		addMouseMotionListener(ad);

	}
	
	private Point getClickPoint(Point globalPos) {
		int total = Math.min(this.getHeight(),FieldCanvas.this.getWidth())-2*border;
		int space = total/size;

		globalPos.translate(-border, -border);
		if(globalPos.x<0 || globalPos.x >total  || globalPos.y < 0 || globalPos.y > total) return null;
		return new Point(globalPos.x/space,globalPos.y/space);	
	}
	
	public void setGame(Game game) {
		this.game=game;
	}
	
	public void setSize(int s) {
		this.size = s;
	}
	
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.lightGray);
		
		g.translate(border, border);
		int total = Math.min(this.getHeight(),this.getWidth())-2*border;
		int space = total/size;
		
		g.setClip(0, 0, total-1,total-1);
		g.fillRect(0, 0, total-1,total-1);
		
		g.setColor(Color.white);
	
		for(int i=0; i<=size; i++) {
			g.drawLine(0,i*space,total-2,i*space);
			g.drawLine(i*space,0,i*space,total-2);
		}
	
		if(game==null) return;
		
		for(int x=0; x<size; x++) {
			for(int y=0; y<size; y++) {
				int colorCode = game.getField()[x][y];
				if(colorCode!=0) {
					g.setColor(colors[colorCode-1]);
					g.fillRect(x*space+2, y*space+2, space -3, space -3);	
				}
			}
		}
		
		if(path!=null && src!=null && dst!=null) {
			int colorCode = game.getField()[src.x][src.y];
			Color c = colors[colorCode-1];
			int sSpace = space/3;	
			int sSpace2 = space/5;
			
			g.setColor(Color.lightGray);
			g.fillRect(src.x*space+2, src.y*space+2, space -3, space -3);
			g.fillRect(dst.x*space+2, dst.y*space+2, space -3, space -3);
			
			g.setColor(c);
			g.fillRect(src.x*space+2+sSpace2, src.y*space+2+sSpace2, space -3 - 2* sSpace2, space -3 - 2* sSpace2);
			
			
			for(int i=1; i<path.size() -1; i++) {
				Point p = path.get(i);
				g.fillRect(p.x*space+2+sSpace, p.y*space+2+sSpace, space -3 - 2* sSpace, space -3 - 2* sSpace);
			}
			g.fillRect(dst.x*space+2+sSpace2, dst.y*space+2+sSpace2, space -3 - 2* sSpace2, space -3 - 2* sSpace2);
			
			
			
		}
		
	}
	
	
}