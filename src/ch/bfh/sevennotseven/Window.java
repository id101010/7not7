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
	
	private Game game;
	private Field field;

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
		
		field = new Field();
		field.setSize(7);
		game = new Game();
		
		
		this.add(field);
		this.setSize(400,400);
		this.setVisible(true);
		
		
		int [][] testfield = new int[7][7];
		testfield[0][0] = 2;
		testfield[1][3] = 4;
		field.setField(testfield);


	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Window("Test");

	}

}
