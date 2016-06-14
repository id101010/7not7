/**
 * 
 */
package ch.bfh.sevennotseven;

import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;



/**
 * @author aaron
 *
 */
public class Window extends Frame {
	
	private Game game;
	private FieldCanvas field;

	/**
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
		
		field = new FieldCanvas();
		field.setSize(7);
		game = new Game();
		
		field.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				game.populateField();
				field.repaint();
			}
		});
		
		this.add(field);
		this.setSize(400,400);
		this.setVisible(true);
		
		field.setField(game.getField());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Window("Test");

	}

}
