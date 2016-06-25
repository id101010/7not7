package ch.bfh.sevennotseven;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JPanel;

/**
 * Class which renders the Blocks that will be placed next on the field
 *
 */
public class NextMovesCanvas extends JPanel implements Game.UpdateListener {

	private static final long serialVersionUID = 1L;

	private Game game;
	
	static final int borderLeft = 1;
	static final int borderRight = 1;
	static final int borderTop = 1;
	static final int borderBottom = 1;
	
	public NextMovesCanvas(Game g) {
		this.game = g;
		g.addUpdateListener(this);
	}
	
	@Override
	public void gameUpdated() {
		NextMovesCanvas.this.repaint();
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
