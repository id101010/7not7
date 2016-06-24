package ch.bfh.sevennotseven;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Window class, places gui elements and listeners.
 * 
 * @author timo
 */
public class Window  extends JFrame implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	private Game game;
	private FieldCanvas field;
	private NextMovesCanvas moves;
	
	private JButton buttonUndo;
	private JButton buttonFreeMove;
	private JLabel labelScore;
	private JLabel labelLinesLeft;	
	private JLabel labelLevel;
	private JPanel mainPanel;
	private CardLayout cardLayout;

	/**
	 * Constructor of window class
	 * 
	 * @param title
	 * @throws HeadlessException
	 */
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
	
		buttonFreeMove= new JButton("Free Move (0)");
		buttonFreeMove.setEnabled(false);
		buttonFreeMove.addActionListener(this);
		buttonFreeMove.setActionCommand("freemove");
		buttonUndo = new JButton("Undo (0)");
		buttonUndo.setEnabled(false);
		buttonUndo.addActionListener(this);
		buttonUndo.setActionCommand("undo");
		labelScore= new JLabel("Score: 0");
		labelLinesLeft = new JLabel("Lines Left: 40");
		labelLevel = new JLabel("Level: 1");
		
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		topPanel.add(buttonFreeMove);
		topPanel.add(buttonUndo);
		topPanel.add(labelScore);
		topPanel.add(labelLinesLeft);
		topPanel.add(labelLevel);
		topPanel.add(moves);
		
		buttonFreeMove.setEnabled(game.getAvailFreeMoves()>0);
		buttonUndo.setEnabled(game.getAvailUndo()>0);
		buttonFreeMove.setText("Free Move ("+game.getAvailFreeMoves()+")");
		buttonUndo.setText("Undo ("+game.getAvailUndo()+")");
		
		game.addUpdateListener(new Game.UpdateListener() {
			
			@Override
			public void gameUpdate() {
				labelScore.setText("Score: "+game.getScore());
				buttonFreeMove.setEnabled(game.getAvailFreeMoves()>0);
				buttonUndo.setEnabled(game.getAvailUndo()>0);
				buttonFreeMove.setText("Free Move ("+game.getAvailFreeMoves()+")");
				buttonUndo.setText("Undo ("+game.getAvailUndo()+")");
				labelLinesLeft.setText("Lines Left: "+game.getLinesLeft());
				labelLevel.setText("Level: "+game.getLevel());
				
			}
		});
		
		JPanel welcomePanel = new JPanel();
		int sizes [] = {7,8,9,10};
		for(int i=0; i<sizes.length; i++) {
			JButton btn = new JButton(sizes[i]+"x"+sizes[i]);
			btn.addActionListener(this);
			btn.setActionCommand(Integer.toString(sizes[i]));
			welcomePanel.add(btn);
		}
		
		JPanel gamePanel = new JPanel();
		gamePanel.setLayout(new BorderLayout());
		gamePanel.add(topPanel, BorderLayout.NORTH);
		gamePanel.add(field);
		
		mainPanel = new JPanel();
		cardLayout = new CardLayout();
		mainPanel.setLayout(cardLayout);
		mainPanel.add(welcomePanel);
		mainPanel.add(gamePanel);
		
		this.setContentPane(mainPanel);
		
		this.setSize(470,460);
		this.setVisible(true);
		
	}
	
	/**
	 * Action listener callback, determines which function to call.
	 * 
	 * @author timo
	 * @param e ActionEvent
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("undo")) {
			field.doUndo();
		} else if (command.equals("freemove")) {
			field.toggleFreeMove();
		} else {
			int size = Integer.parseInt(command);
			cardLayout.last(mainPanel);
			game.reset(size);
		}
		
	}

	/**
	 * Main method
	 * 
	 * @author timo
	 * @param args
	 */
	public static void main(String[] args) {
		new Window("7 not 7");
	}
}
