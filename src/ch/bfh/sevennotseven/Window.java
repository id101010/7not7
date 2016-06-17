/**
 * 
 */
package ch.bfh.sevennotseven;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Panel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;



/**
 * @author aaron
 *
 */
public class Window extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private Game game;
	private FieldCanvas field;
	private NextMovesCanvas moves;
	
	private JButton buttonUndo;
	private JButton buttonFreeMove;
	private JLabel labelScore;


	public Window(String title) throws HeadlessException {
		super(title);

		this.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}
		});
		
		
		game = new Game();
		moves = new NextMovesCanvas(game);	
		field = new FieldCanvas(game);
		
		moves.setPreferredSize(new Dimension(200, 40));
	

		buttonFreeMove= new JButton("FreeMove");
		buttonUndo = new JButton("Undo");
		labelScore= new JLabel("Score: 0");
		
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		topPanel.add(buttonFreeMove);
		topPanel.add(buttonUndo);
		topPanel.add(labelScore);
		topPanel.add(moves);
		
		game.addUpdateListener(new Game.UpdateListener() {
			
			@Override
			public void gameUpdate() {
				labelScore.setText("Score: "+game.getScore());
				
			}
		});
		

		
		this.add(topPanel, BorderLayout.NORTH);
		this.add(field);
		this.setSize(470,460);
		this.setVisible(true);
		


	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Window("7 not 7");

	}

}
