package org.robert.tom;

import javax.swing.*;

/**
 * Hello world!
 *
 */
public class App {

/**
*		static {
*			try {
*				LibraryLoader.loadLibrary("libmetrics");
*			} catch (Exception e) {
*				System.out.println("LibraryLoader didn't work.");
*				System.err.println(e);
*				System.exit(1);
*			}
*		}
*/
		static {
        //System.loadLibrary("mcollect");
			System.load("/home/rob/Documents/workspace/capstone/so_outside_of_jar/metrics-collection-app/target/libmcollect.so");
		}

		private native void pidList();

		private static void launchGUI() {
			JFrame frame = new JFrame("Metrics Collection App");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			JLabel label = new JLabel("Metrics go here");
			frame.getContentPane().add(label);
			label.setHorizontalAlignment(JLabel.CENTER);

			frame.pack();
			frame.setSize(500, 300);
			frame.setVisible(true);
		}

    public static void main( String[] args ) {
        System.out.println( "Loading interface..." );

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
        	public void run() {
        		launchGUI();
        	}
        });

        new App().pidList();
    }
}
