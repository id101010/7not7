/**
 * 
 */
package ch.bfh.sevennotseven;

import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;



/**
 * @author aaron
 *
 */
public class Window extends Frame {
	
	private static final long serialVersionUID = 1L;
	private Game game;
	private FieldCanvas field;


	public Window(String title) throws HeadlessException {
		super(title);

		this.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}
		});
		
		field = new FieldCanvas();
		field.setSize(7);
		game = new Game();
		field.setGame(game);
		

		
		this.add(field);
		this.setSize(400,400);
		this.setVisible(true);

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Window("7 not 7");

	}

}
