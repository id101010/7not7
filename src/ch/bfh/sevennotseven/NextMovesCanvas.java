package ch.bfh.sevennotseven;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JPanel;

public class NextMovesCanvas extends JPanel {
	private Game game;
	
	static final int borderLeft = 1;
	static final int borderRight = 1;
	static final int borderTop = 1;
	static final int borderBottom = 1;
	
	public NextMovesCanvas(Game g) {
		this.game = g;
		g.addUpdateListener(new Game.UpdateListener() {
			@Override
			public void gameUpdate() {
				NextMovesCanvas.this.repaint();
			}
		});
	}
	
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		
		g.setColor(Color.lightGray);
		int height = getHeight() - borderTop-borderBottom;
		
		List<Integer> nextBlocks = game.getNextBlocks();
		for(int i=0; i< nextBlocks.size(); i++) {
			g.setColor(FieldCanvas.colors[nextBlocks.get(i)-1]);
			g.fillRect(borderLeft + height *i, borderTop, height,height);
			g.setColor(Color.white);
			g.drawRect(borderLeft + height *i, borderTop, height,height);
		}
	}

}
