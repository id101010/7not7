package ch.bfh.sevennotseven;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

/**
 * FieldCanvas class, implements the field to draw upon. Draws the game field and handles mouse actions.
 * 
 * @author timo
 */
public class FieldCanvas extends JPanel{
	
	// private and static members
	private static final long serialVersionUID = 1L;
	static final int borderLeft = 5;
	static final int borderRight = 5;
	static final int borderTop = 5;
	static final int borderBottom = 5;
	
	public static final Color[] colors = { 
			new Color(0xD66436),
			new Color(0x486F70),
			new Color(0xCBD840),
			new Color(0x8B2700),
			new Color(0x33CCCC)
	};
	
	
	private Game game;
	private Point src;
	private Point dst;
	private List<Point> path;
	private List<Point> blockedFields;
	private boolean freeMoveMode = false;
	
	/**
	 * Constructor of FieldCanvas
	 * 
	 * @author timo
	 * @param g
	 */
	FieldCanvas(Game g){
		MouseAdapter ad = new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				
				Point p = FieldCanvas.this.getClickPoint(e.getPoint());
				if(p==null || game.getField()[p.x][p.y]==0) { //invalid click
					src = null;
				} else {
					src = p;
					if(freeMoveMode) {
						blockedFields = game.getReachablePoints(src);
					} else {
						blockedFields = game.getUnreachablePoints(src);
					}
					repaint();
				}
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				super.mouseDragged(e);
				if(src!=null) {
					Point lastDst = dst;
					dst = FieldCanvas.this.getClickPoint(e.getPoint());
					if(lastDst!=dst && dst!=null) { //hovered field changed
						if(freeMoveMode) {
							if(!game.canMove(src, dst) && game.getField()[dst.x][dst.y]==0) {
								path = new ArrayList<Point>();
								path.add(src);
								path.add(dst);
							} else {
								path= null;
							}
						} else {
							path= game.getPath(src, dst);
						}
						repaint();
					}
				}else {
					dst = null;
					path = null;
				}
			};
			
			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				dst = FieldCanvas.this.getClickPoint(e.getPoint());
				path = null;
				if(freeMoveMode) {
					if(!game.canMove(src, dst)) {
						game.doFreeMove(src, dst);
					}
				} else {
					if(dst != null && src!=null && !src.equals(dst)) {
						System.out.println("Moving from "+src.toString()+ " to "+dst.toString());
						game.doMove(src, dst);
					}
				}
				freeMoveMode = false;
				src = null;
				blockedFields = null;
				repaint();
			}
		};
		
		addMouseListener(ad);
		addMouseMotionListener(ad);
		this.game=g;

	}
	
	/**
	 * Callback if button freeMove gets pressed.
	 * 
	 * @author timo
	 */
	public void toggleFreeMove() {
		if(freeMoveMode) {
			freeMoveMode =false;
		} else if(game.getAvailFreeMoves()>0) {
			freeMoveMode = true;
		}
		repaint();
	}
	
	/**
	 * Callback if button undo gets pressed.
	 * 
	 * @author timo
	 */
	public void doUndo() {
		if(game.getAvailUndo()>0) {
			game.doUndo();
			repaint();
		}
	}
	
	/**
	 * Calculates the position in which a click event has happened.
	 * 
	 * @author timo
	 * @param globalPos
	 * @return Position of clickevent
	 */
	private Point getClickPoint(Point globalPos) {
		int total = Math.min(this.getHeight()-borderTop-borderBottom,FieldCanvas.this.getWidth()-borderLeft-borderRight);
		int space = total/game.getSize();

		globalPos.translate(-borderLeft, -borderTop);
		if(globalPos.x<0 || globalPos.x >total  || globalPos.y < 0 || globalPos.y > total) return null;
		return new Point(globalPos.x/space,globalPos.y/space);	
	}
	
	/**
	 * Paint the game field.
	 * 
	 * @author timo
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		//Draw field (background and lines)
		if(freeMoveMode) {
			g.setColor(Color.gray);
		} else {
			g.setColor(Color.lightGray);
		}
		
		g.translate(borderLeft, borderTop);
		int total = Math.min(this.getHeight()-borderTop-borderBottom,FieldCanvas.this.getWidth()-borderLeft-borderRight);
		int space = total/game.getSize();
		
		g.setClip(0, 0, total-4,total-4);
		g.fillRect(0, 0, total-4,total-4);
		
		g.setColor(Color.white);
	
		for(int i=0; i<=game.getSize(); i++) {
			g.drawLine(0,i*space,total-2,i*space);
			g.drawLine(i*space,0,i*space,total-2);
		}
	
		if(game==null) return;
		
		//Draw blocks
		
		for(int x=0; x<game.getSize(); x++) {
			for(int y=0; y<game.getSize(); y++) {
				int colorCode = game.getField()[x][y];
				if(colorCode!=0) {
					g.setColor(colors[colorCode-1]);
					g.fillRect(x*space+2, y*space+2, space -3, space -3);	
				}
			}
		}
		
		//Draw blocked fields
		
		if(blockedFields!=null) {
			g.setColor(Color.darkGray);
			for(int i=0; i<blockedFields.size(); i++) {
				Point p = blockedFields.get(i);
				g.drawLine(p.x*space+1, p.y*space+1, (p.x+1)*space, (p.y+1)*space);
				g.drawLine((p.x+1)*space-1, p.y*space+2, p.x*space+1, (p.y+1)*space);
			}
		}
		
		//Draw Path
		if(path!=null && src!=null && dst!=null) {
			int colorCode = game.getField()[src.x][src.y];
			Color c = colors[colorCode-1];
			int sSpace = space/3;	
			int sSpace2 = space/5;
			
			
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