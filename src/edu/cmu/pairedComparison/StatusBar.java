package edu.cmu.pairedComparison;

import java.awt.Dimension;

import javax.swing.JLabel;

public class StatusBar extends JLabel {

	/** Creates a new instance of StatusBar */
	public StatusBar() {
		super();
		super.setPreferredSize(new Dimension(100, 16));
		setReady();
	}

	/**
	 * Sets the status bar message
	 * 
	 * @param message
	 *            message to set
	 */
	public void setMessage(String message) {
		setText(" " + message);
	}

	/**
	 * Sets status bar message to "Ready"
	 */
	public void setReady() {
		setText("Ready");
	}
}