/**
 * 
 */
package ch.bfh.sevennotseven;

import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author aaron
 *
 */
public class Window extends Frame {
	
	private Game game;

	/**
	 * @param title
	 * @throws HeadlessException
	 */
	public Window(String title) throws HeadlessException {
		super(title);
		this.setVisible(true);
		this.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}
		});
		
		game = new Game();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Window("Test");

	}

}
