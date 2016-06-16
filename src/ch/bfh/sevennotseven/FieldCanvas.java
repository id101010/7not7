package ch.bfh.sevennotseven;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class FieldCanvas extends JPanel{
	private static final long serialVersionUID = 1L;
	static final int border = 5;
	static final Color[] colors = {Color.red,Color.green, Color.blue, Color.yellow,Color.magenta};
	
	
	
	private int size;
	private Game game;
	private Point src;
	
	FieldCanvas(){
		
		addMouseListener(new MouseAdapter(){
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
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				Point p = FieldCanvas.this.getClickPoint(e.getPoint());
				if(p != null && src!=null) {
					System.out.println("Moving from "+src.toString()+ " to "+p.toString());
					game.doMove(src, p);
					repaint();
				}
				src = null;
			}
		});

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
		
	}
	
	
}