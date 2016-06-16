package ch.bfh.sevennotseven;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class FieldCanvas extends JPanel{
	private static final long serialVersionUID = 1L;
	private int size;
	private Game game;
	
	static final Color[] colors = {Color.red,Color.green, Color.blue, Color.yellow,Color.magenta};
	
	FieldCanvas(){
		
		addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				Point point = e.getPoint();
				
				int total = Math.min(FieldCanvas.this.getHeight(),FieldCanvas.this.getWidth())-10;
				int space = total/size;
							
				
				
				
				
				repaint();
			}
		});

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
		
		g.translate(5, 5);
		int total = Math.min(this.getHeight(),this.getWidth())-10;
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