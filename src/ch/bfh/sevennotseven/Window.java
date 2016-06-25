package ch.bfh.sevennotseven;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Window class, contains the welcome screen and the game itself
 * 
 */
public class Window  extends JFrame implements ActionListener, Game.UpdateListener {
	
	private static final long serialVersionUID = 1L;
	private Game game;
	private FieldCanvas field;
	private NextMovesCanvas moves;
	
	private JButton buttonUndo;
	private JButton buttonFreeMove;
	private JButton buttonReset;
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
		
		game = new Game();
		game.addUpdateListener(this); //register for game updates
		
		initMainLayout();
	}
	
	private void initMainLayout() {
		mainPanel = new JPanel();
		cardLayout = new CardLayout();
		mainPanel.setLayout(cardLayout);
		initWelcomeLayout();
		initGameLayout();
		this.setContentPane(mainPanel);
		this.setSize(800,600);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void initGameLayout() {
		
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
		
		buttonReset = new JButton("Reset");
		buttonReset.addActionListener(this);
		buttonReset.setActionCommand("reset");
		
		
		labelScore= new JLabel("Score: 0");
		labelLinesLeft = new JLabel("Lines Left: 40");
		labelLevel = new JLabel("Level: 1");
		
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topPanel.add(buttonReset);
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
		
		JPanel gamePanel = new JPanel();
		gamePanel.setLayout(new BorderLayout());
		gamePanel.add(topPanel, BorderLayout.NORTH);
		gamePanel.add(field);
		mainPanel.add(gamePanel);
	}
	
	private void initWelcomeLayout() {
		JPanel welcomePanel = new JPanel();
		welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
		
		JButton btnInstr = new JButton("Instructions & More");
		btnInstr.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnInstr.setActionCommand("github");
		btnInstr.addActionListener(this);
		
		welcomePanel.add(Box.createRigidArea(new Dimension(0,5)));
		welcomePanel.add(btnInstr);
		welcomePanel.add(Box.createRigidArea(new Dimension(0,10)));
		
		int sizes [] = {7,8,9,10};
		for(int i=0; i<sizes.length; i++) {
			JButton btn = new JButton(sizes[i]+"x"+sizes[i]);
			btn.addActionListener(this);
			btn.setActionCommand(Integer.toString(sizes[i]));
			btn.setAlignmentX(Component.CENTER_ALIGNMENT);

			welcomePanel.add(btn);
			welcomePanel.add(Box.createRigidArea(new Dimension(0,5)));
		}
		
		mainPanel.add(welcomePanel);
	}
	
	/**
	 * Action listener callback: gets called when a button was pressed
	 * The method will either start a game or forward an action to the game
	 * 
	 * @param e ActionEvent
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("undo")) {
			field.doUndo();
		} else if (command.equals("freemove")) {
			field.toggleFreeMove();
		} else if(command.equals("github")) {
			if(Desktop.isDesktopSupported()) {
				final String url = "https://github.com/id101010/7not7/blob/master/README.md";
				try {
					Desktop.getDesktop().browse(new URI(url));
				} catch(Exception ex) {
					System.out.println("Couldn't open link: "+url+" "+ex.toString());
				}
			}
		} else if(command.equals("reset")) {
			cardLayout.first(mainPanel);
		} else {
			int size = Integer.parseInt(command);
			cardLayout.last(mainPanel);
			game.reset(size);
		}
		
	}
	
	/**
	 * GameUpdateListener: gets called when the games was updated (e.g. user made a move)
	 */
	@Override
	public void gameUpdated() {
		labelScore.setText("Score: "+game.getScore());
		buttonFreeMove.setEnabled(game.getAvailFreeMoves()>0);
		buttonUndo.setEnabled(game.getAvailUndo()>0);
		buttonFreeMove.setText("Free Move ("+game.getAvailFreeMoves()+")");
		buttonUndo.setText("Undo ("+game.getAvailUndo()+")");
		labelLinesLeft.setText("Lines Left: "+game.getLinesLeft());
		labelLevel.setText("Level: "+game.getLevel());
		
	}

	/**
	 * Main method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new Window("7 not 7");
	}
}
