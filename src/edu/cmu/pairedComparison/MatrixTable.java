package edu.cmu.pairedComparison;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EventObject;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.JTextComponent;

/**
 * Matrix Table
 * 
 * @author Shigeru Sasao
 */
public class MatrixTable extends JTable {

	/**
	 * Constructor
	 * 
	 * @param model
	 *            The table model to set.
	 */
	public MatrixTable(MatrixTableModel model) {
		super(model);
		this.addMouseListener(new MousePressListener());
		this.addKeyListener(new RepaintKeyListener());
		refresh();
	}

	/**
	 * Refresh the table attributes
	 */
	public void refresh() {
		this.setTableHeader(null);
		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		TableColumn col = null;
		for (int i = 0; i < this.columnModel.getColumnCount(); i++) {
			col = this.getColumnModel().getColumn(i);
			col.setPreferredWidth(88);
			col.setCellRenderer(new MatrixTableCellRenderer());
		}
		this.setRowHeight(25);
		this.setCellSelectionEnabled(true);
		Font f = new Font("Helvetica", Font.PLAIN, 12);
		this.setFont(f);
	}

	/**
	 * Don't append values when editing
	 */
	public boolean editCellAt(int row, int column, EventObject e) {
		boolean result = super.editCellAt(row, column, e);
		final Component editor = getEditorComponent();

		if (editor != null && editor instanceof JTextComponent) {
			if (e == null) {
				((JTextComponent) editor).selectAll();
			} else {
				if (!(e instanceof KeyEvent)) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							((JTextComponent) editor).selectAll();
						}
					});
				} else {
					((JTextComponent) editor).setText("");
				}
			}
		}
		return result;
	}

	/**
	 * MatrixTable cell renderer class
	 */
	private class MatrixTableCellRenderer extends DefaultTableCellRenderer {

		/**
		 * Constructor
		 */
		public MatrixTableCellRenderer() {
			super();
			this.setOpaque(true);
			this.setHorizontalAlignment(JLabel.CENTER);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent
		 * (javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
		 */
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int col) {
			Component c = super.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, col);
			MatrixTableModel model = (MatrixTableModel) table.getModel();

			// top row with header and artifact name
			if (row == 0) {
				c.setBackground(Color.lightGray);
				c.setForeground(Color.black);

				// reference value
			} else if (col == 0) {
				c.setBackground(Color.white);
				c.setForeground(Color.black);
				c.addKeyListener(new NumericKeyListener());

				// artifact name
			} else if (col == 1) {
				c.setBackground(Color.white);
				c.setForeground(Color.black);

				// comparison of same artifact
			} else if (row - 1 == col - 2) {
				c.setBackground(Color.gray);
				c.setForeground(Color.gray);

				// non-editable cells
			} else if (!model.getDesignMatrix()[row - 1][col - 2]) {
				c.setBackground(Color.pink);
				c.setForeground(Color.pink);
				c.addKeyListener(new NumericKeyListener());

				// editable cells
			} else {
				c.setBackground(Color.white);
				c.setForeground(Color.black);
				c.addKeyListener(new NumericKeyListener());
				c.enableInputMethods(true);
			}

			// highlight selected row and column
			if ((table.getSelectedRow() == row && table.getSelectedColumn() >= col)
					|| (table.getSelectedColumn() == col && table
							.getSelectedRow() >= row)) {
				if (table.getSelectedRow() >= 1
						&& table.getSelectedColumn() >= 2) {
					if (model.getDesignMatrix()[table.getSelectedRow() - 1][table
							.getSelectedColumn() - 2]) {
						c.setBackground(Color.darkGray);
						c.setForeground(Color.white);

						// don't show values for cells that are not editable
						if (row >= 1 && col >= 2) {
							if (!model.getDesignMatrix()[row - 1][col - 2]) {
								c.setBackground(Color.darkGray);
								c.setForeground(Color.darkGray);
							}
						}
					}
				}
			}

			return c;
		}
	}

	/**
	 * Filter all but numeric key entries
	 */
	protected class NumericKeyListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}

		@Override
		public void keyTyped(KeyEvent e) {
			if (!((Character.isDigit(e.getKeyChar())
					|| (e.getKeyChar() == KeyEvent.VK_BACK_SPACE) || (e
					.getKeyChar() == KeyEvent.VK_DELETE)))) {
				Toolkit.getDefaultToolkit().beep();
				e.consume();
			}
		}
	}

	/**
	 * Listen for mouse events on table
	 */
	private class MousePressListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			repaint();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			repaint();
		}

		@Override
		public void mouseExited(MouseEvent e) {
			repaint();
		}

		@Override
		public void mousePressed(MouseEvent e) {
			repaint();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			repaint();
		}
	}

	/**
	 * Repaint when arrow keys are pressed
	 */
	protected class RepaintKeyListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			repaint();
		}

		@Override
		public void keyReleased(KeyEvent e) {
			repaint();
		}

		@Override
		public void keyTyped(KeyEvent e) {
			repaint();
		}
	}
}
