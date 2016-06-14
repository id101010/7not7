package ch.bfh.sevennotseven;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;

public class FieldCanvas extends Canvas{
	
	private int size;
	private int[][] field;
	
	static final Color[] colors = {Color.red,Color.green, Color.blue, Color.yellow,Color.magenta};
	
	FieldCanvas(){

	}
	
	public void setSize(int s) {
		this.size = s;
	}
	
	public void setField(int[][] field){
		this.field = field;
	
	}
	
	
	public void paint(Graphics g) {
		g.setColor(Color.lightGray);
		
		int total = this.getHeight();
		int space = total/size;
		
		g.setClip(0, 0, total,total);
		g.fillRect(0, 0, total,total);
		
		g.setColor(Color.white);
	
		for(int i=0; i<=size; i++) {
			g.drawLine(0,i*space,total,i*space);
			g.drawLine(i*space,0,i*space,total);
		}
		
		if(field==null) return;
		
		for(int x=0; x<size; x++) {
			for(int y=0; y<size; y++) {
				int colorCode = field[x][y];
				if(colorCode!=0) {
					g.setColor(colors[colorCode-1]);
					g.fillRect(x*space+2, y*space+2, space -3, space -3);	
				}
			}
		}
		
	}
	
	
}