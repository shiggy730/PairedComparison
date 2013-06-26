package edu.cmu.pairedComparison;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 * Holds actions performed for UI
 * 
 * @author ssasao
 */
public class MainFrameActions {

	/** random number generator */
	private Random randGen;

	/** The main frame */
	private MainFrame frame;

	/** contains string for formatted output */
	StringBuilder sb;

	/** format for output */
	Formatter formatter;

	/**
	 * Constructor
	 */
	public MainFrameActions() {
		this.randGen = new Random();
		this.sb = new StringBuilder();
		this.formatter = new Formatter(sb, Locale.US);
	}

	/**
	 * Constructor
	 * 
	 * @param frame
	 *            The main frame.
	 */
	public MainFrameActions(MainFrame frame) {
		this();
		this.frame = frame;
	}

	/**
	 * import to file
	 */
	public void importMatrix() {
		this.frame.fileDialog.showOpenDialog(frame);
		File importFile = this.frame.fileDialog.getSelectedFile();
		try {
			this.frame.matrixTableModel = this.frame.fileIO
					.readFromFile(importFile);
			this.frame.matrixTableModel
					.addTableModelListener(new TableModelListener() {
						public void tableChanged(TableModelEvent e) {

							// set total number of comparisons and comparisons
							// remaining.
							updateComparisonRemaining();
						}
					});
			this.frame.matrixTable.setModel(this.frame.matrixTableModel);
			this.frame.matrixTable.refresh();
			this.frame.matrixTable.updateUI();
			this.frame.artifactCount = this.frame.matrixTableModel
					.getArtifactCount();
			this.frame.artifactCountField.setValue(this.frame.artifactCount);
			this.frame.replicationFactor = this.frame.matrixTableModel
					.getReplicationFactor();
			this.frame.replicationFactorField
					.setValue(this.frame.replicationFactor);

			// reset total number of comparisons and comparisons remaining.
			this.updateComparisonRemaining();
			this.frame.statusBar.setReady();

		} catch (FileNotFoundException ioe) {
			this.frame.ioBox.append("File not found...\n");
		} catch (IOException ioe) {
			this.frame.ioBox.append("Problems while reading from file...\n");
		} catch (NumberFormatException ioe) {
			this.frame.ioBox.append("Problems while reading from file...\n");
		}

	}

	/**
	 * export to file
	 */
	public void exportMatrix() {
		this.frame.fileDialog.showSaveDialog(frame);
		File exportFile = this.frame.fileDialog.getSelectedFile();
		try {
			this.frame.fileIO.writeToFile(exportFile,
					this.frame.matrixTableModel);
		} catch (IOException ioe) {
			this.frame.ioBox.append("Problems while writing to file...\n");
		}
	}

	/**
	 * change in artifact count.
	 */
	public void artifactCountActionPerformed() {
		int n = JOptionPane.showConfirmDialog(frame,
				"This will change your matrix size. Continue?", "",
				JOptionPane.YES_NO_OPTION);
		if (n == JOptionPane.YES_OPTION) {
			this.frame.artifactCount = Integer
					.parseInt(this.frame.artifactCountField.getValue()
							.toString());
			this.frame.replicationFactor = Integer
					.parseInt(this.frame.replicationFactorField.getValue()
							.toString());

			// Set replication factor to artifact count if replication factor
			// is bigger.
			if (this.frame.replicationFactor > this.frame.artifactCount) {
				this.frame.replicationFactor = this.frame.artifactCount;
				this.frame.replicationFactorField
						.setValue(this.frame.replicationFactor);
			}

			// copy old data to new. note that if the new size is
			// smaller, you will lose data.
			this.frame.matrixTableModel = this.frame.matrixTableModel.getCopy(
					this.frame.artifactCount, this.frame.replicationFactor);
			this.frame.matrixTableModel
					.addTableModelListener(new TableModelListener() {
						public void tableChanged(TableModelEvent e) {

							// set total number of comparisons and comparisons
							// remaining.
							updateComparisonRemaining();
						}
					});
			this.frame.matrixTable.setModel(this.frame.matrixTableModel);
			this.frame.matrixTable.refresh();
			this.frame.matrixTable.updateUI();

			// reset total number of comparisons and comparisons remaining.
			this.updateComparisonRemaining();

		} else {
			this.frame.artifactCountField.setValue(frame.artifactCount);
		}
		this.frame.statusBar.setReady();
	}

	/**
	 * change in replication factor
	 */
	public void replicationFactorActionPerformed() {
		int repFactor = Integer.parseInt(this.frame.replicationFactorField
				.getValue().toString());
		if (repFactor <= this.frame.artifactCount) {
			this.frame.replicationFactor = repFactor;
			this.frame.matrixTableModel
					.setReplicationFactor(this.frame.replicationFactor);
			this.frame.matrixTableModel.resetDesign();
			this.frame.matrixTableModel.fireTableDataChanged();
			this.frame.matrixTable.updateUI();

			// reset total number of comparisons and comparisons remaining.
			this.updateComparisonRemaining();
		} else {
			JOptionPane.showMessageDialog(frame,
					"Replication factor cannot exceed number of artifacts.",
					"Input Error", JOptionPane.ERROR_MESSAGE);
			this.frame.replicationFactorField
					.setValue(this.frame.replicationFactor);
		}
	}

	/**
	 * Sets the comparison information at table selection
	 */
	public synchronized void setComparisonInfo() {

		int row = this.frame.matrixTable.getSelectedRow();
		int col = this.frame.matrixTable.getSelectedColumn();
		String component1;
		String component2;
		String notes1;
		String notes2;

		// comparison (editable) cell is clicked
		if (row >= 1
				&& col >= 2
				&& this.frame.matrixTableModel.getDesignMatrix()[row - 1][col - 2]) {
			component1 = this.frame.matrixTableModel.getArtifacts()[row - 1];
			notes1 = this.frame.matrixTableModel.getArtifactNotes()[row - 1];
			if (component1 != null) {
				this.frame.component1Field.setText(component1);
			} else {
				this.frame.component1Field.setText("");
			}
			if (notes1 != null) {
				this.frame.notes1TextArea.setText(notes1);
				this.frame.notes1TextArea.setEditable(true);
			} else {
				this.frame.notes1TextArea.setText("");
				this.frame.notes1TextArea.setEditable(true);
			}
			component2 = this.frame.matrixTableModel.getArtifacts()[col - 2];
			notes2 = this.frame.matrixTableModel.getArtifactNotes()[col - 2];
			if (component2 != null) {
				this.frame.component2Field.setText(component2);
			} else {
				this.frame.component2Field.setText("");
			}
			if (notes2 != null) {
				this.frame.notes2TextArea.setText(notes2);
				this.frame.notes2TextArea.setEditable(true);
			} else {
				this.frame.notes2TextArea.setText("");
				this.frame.notes2TextArea.setEditable(true);
			}
			if (component1 != null && component2 != null) {

				if (!component1.equals("") && !component2.equals("")) {
					String newMsg = "How many times bigger or smaller is "
							+ component1 + " compared to " + component2 + "?";
					this.frame.statusBar.setMessage(newMsg);
				} else {
					this.frame.statusBar.setReady();
				}

				// set comparison notes if exists, and enable editing
				HashMap<String, String> notes = this.frame.matrixTableModel
						.getComparisonNotes();
				String comparisonKey = this.comparisonKey(component1,
						component2);
				if (notes.containsKey(comparisonKey)) {
					this.frame.comparisonNotesTextArea.setText(notes
							.get(comparisonKey));
				} else {
					this.frame.comparisonNotesTextArea.setText("");
				}

				this.frame.comparisonNotesTextArea.setEditable(true);
			} else {
				this.frame.statusBar.setReady();
			}

			// artifact names (row) is clicked
		} else if (row >= 1 && col == 1) {
			component1 = this.frame.matrixTableModel.getArtifacts()[row - 1];
			notes1 = this.frame.matrixTableModel.getArtifactNotes()[row - 1];
			if (component1 != null) {
				this.frame.component1Field.setText(component1);
			} else {
				this.frame.component1Field.setText("");
			}
			if (notes1 != null) {
				this.frame.notes1TextArea.setText(notes1);
				this.frame.notes1TextArea.setEditable(true);
			} else {
				this.frame.notes1TextArea.setText("");
				this.frame.notes1TextArea.setEditable(true);
			}
			this.frame.component2Field.setText("");
			this.frame.notes2TextArea.setText("");
			this.frame.notes2TextArea.setEditable(false);
			this.frame.comparisonNotesTextArea.setText("");
			this.frame.comparisonNotesTextArea.setEditable(false);
			this.frame.statusBar.setReady();

			// artifact names (column) is clicked
		} else if (row == 0 && col >= 2) {
			component2 = this.frame.matrixTableModel.getArtifacts()[col - 2];
			notes2 = this.frame.matrixTableModel.getArtifactNotes()[col - 2];
			if (component2 != null) {
				this.frame.component2Field.setText(component2);
			} else {
				this.frame.component2Field.setText("");
			}
			if (notes2 != null) {
				this.frame.notes2TextArea.setText(notes2);
				this.frame.notes2TextArea.setEditable(true);
			} else {
				this.frame.notes2TextArea.setText("");
				this.frame.notes2TextArea.setEditable(true);
			}
			this.frame.component1Field.setText("");
			this.frame.notes1TextArea.setText("");
			this.frame.notes1TextArea.setEditable(false);
			this.frame.comparisonNotesTextArea.setText("");
			this.frame.comparisonNotesTextArea.setEditable(false);
			this.frame.statusBar.setReady();

			// all other cases
		} else {
			this.frame.component1Field.setText("");
			this.frame.component2Field.setText("");
			this.frame.notes1TextArea.setText("");
			this.frame.notes1TextArea.setEditable(false);
			this.frame.notes2TextArea.setText("");
			this.frame.notes2TextArea.setEditable(false);
			this.frame.comparisonNotesTextArea.setText("");
			this.frame.comparisonNotesTextArea.setEditable(false);
			this.frame.statusBar.setReady();
		}

	}

	/**
	 * Set text of notes1 into table model
	 */
	public synchronized void setNotes1() {
		String component1 = this.frame.component1Field.getText();
		String[] artifacts = this.frame.matrixTableModel.getArtifacts();
		String[] artifactNotes = this.frame.matrixTableModel.getArtifactNotes();
		boolean found = false;
		int i;

		// check that component exists in the artifact list.
		if (component1 != null) {
			for (i = 0; i < artifacts.length; i++) {
				if (artifacts[i] != null && artifacts[i].equals(component1)) {
					found = true;
					break;
				}
			}

			if (found) {
				artifactNotes[i] = this.frame.notes1TextArea.getText();
			}
		}

		return;
	}

	/**
	 * Set text of notes2 into table model
	 */
	public synchronized void setNotes2() {
		String component2 = this.frame.component2Field.getText();
		String[] artifacts = this.frame.matrixTableModel.getArtifacts();
		String[] artifactNotes = this.frame.matrixTableModel.getArtifactNotes();
		boolean found = false;
		int i;

		// check that component exists in the artifact list.
		if (component2 != null) {
			for (i = 0; i < artifacts.length; i++) {
				if (artifacts[i] != null && artifacts[i].equals(component2)) {
					found = true;
					break;
				}
			}

			if (found) {
				artifactNotes[i] = this.frame.notes2TextArea.getText();
			}
		}

		return;
	}

	/**
	 * Set text of comparison notes into table model
	 */
	public synchronized void setComparisonNotes() {

		// get component names
		String component1 = this.frame.component1Field.getText();
		String component2 = this.frame.component2Field.getText();
		if (component1 == null || component2 == null || component1.equals("")
				|| component2.equals("")) {
			return;
		}

		// get current hash of comparison notes and update
		HashMap<String, String> notes = this.frame.matrixTableModel
				.getComparisonNotes();
		String comparisonKey = this.comparisonKey(component1, component2);
		String comparisonNote = this.frame.comparisonNotesTextArea.getText();
		if (comparisonNote != null && !comparisonNote.equals("")) {
			notes.put(comparisonKey, comparisonNote);
		}
		this.frame.matrixTableModel.setComparisonNotes(notes);
		return;
	}

	/**
	 * calculate button is pressed
	 */
	public void calculateActionPerformed() {
		// get input values
		String[] artifacts = this.frame.matrixTableModel.getArtifacts();
		double[][] judgmentMatrix = this.frame.matrixTableModel
				.getJudgmentMatrix();
		boolean[][] designMatrix = this.frame.matrixTableModel
				.getDesignMatrix();
		int referenceId = this.frame.matrixTableModel.getReferenceIndex();
		double referenceSize = this.frame.matrixTableModel.getReferenceSize();

		// validate input
		for (int i = 0; i < artifacts.length; i++) {
			if (artifacts[i] == null || artifacts[i].equals("")) {
				frame.ioBox.append("\nPlease enter all artifact names.\n");
				return;
			}
		}
		for (int i = 0; i < judgmentMatrix.length; i++) {
			for (int j = 0; j < judgmentMatrix[i].length; j++) {
				if (designMatrix[i][j] && judgmentMatrix[i][j] == 0.0) {
					frame.ioBox
							.append("\nPlease enter all relative indices.\n");
					return;
				}
			}
		}
		if (referenceId == -1) {
			frame.ioBox.append("\nPlease enter reference index.\n");
			return;
		}

		// paired comparison calculations
		judgmentMatrix = this.frame.calculator.calcReciprocals(designMatrix,
				judgmentMatrix, frame.artifactCount);
		judgmentMatrix = this.frame.calculator.fillJudgmentMatrix(
				judgmentMatrix, frame.artifactCount);
		double inconsistencyIndex = this.frame.calculator
				.calcInconsistencyIndex(judgmentMatrix, frame.artifactCount,
						this.frame.replicationFactor);
		double[] relativeSize = this.frame.calculator.calcRatio(judgmentMatrix,
				judgmentMatrix.length);
		double[] absoluteSize = this.frame.calculator.calcAbsoluteSize(
				referenceSize, referenceId, relativeSize, frame.artifactCount);
		double[] absoluteSizeStdDev = this.frame.calculator
				.calcAbsoluteSizeStdDev(absoluteSize, inconsistencyIndex,
						frame.artifactCount);
		double sumAbsoluteSizeStdDev = this.frame.calculator
				.calcSumAbsoluteSizeStdDev(absoluteSizeStdDev,
						frame.artifactCount);

		// print results to frame.ioBox
		frame.ioBox.append("\n-------------------------------------\n");
		frame.ioBox.append("Paired comparison calculation result \n");
		frame.ioBox.append("\n");
		sb.delete(0, sb.length());
		formatter.format("%1$-30s\t%2$-30s\t%3$-30s\n", "Artifact",
				"Absolute Size", "Absolute Std Dev");
		frame.ioBox.append(sb.toString());
		try {
			for (int i = 0; i < frame.artifactCount; i++) {
				sb.delete(0, sb.length());
				formatter.format("%1$-30s\t%2$-30s\t%3$-30s\n", artifacts[i],
						frame.numFormat.valueToString(absoluteSize[i]),
						frame.numFormat.valueToString(absoluteSizeStdDev[i]));
				frame.ioBox.append(sb.toString());

			}
			frame.ioBox.append("\n");
			frame.ioBox.append("Inconsistency Index:\t"
					+ frame.numFormat.valueToString(inconsistencyIndex) + "\n");
			frame.ioBox.append("Sum abs std dev:\t"
					+ frame.numFormat.valueToString(sumAbsoluteSizeStdDev)
					+ "\n");

		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		frame.ioBox.append("-------------------------------------\n");
		frame.statusBar.setReady();
	}

	/**
	 * The guide action to provide randomized comparison order
	 */
	public void guideActionPerformed() {
		// get input values
		String[] artifacts = this.frame.matrixTableModel.getArtifacts();
		int referenceId = this.frame.matrixTableModel.getReferenceIndex();

		// validate input
		for (int i = 0; i < artifacts.length; i++) {
			if (artifacts[i] == null || artifacts[i].equals("")) {
				frame.ioBox.append("\nPlease enter all artifact names.\n");
				return;
			}
		}

		// loop until comparison remaining is 0
		double[][] judgmentMatrix = null;
		boolean[][] designMatrix = null;
		List<String> remainingIndices = null;
		boolean isCancel = false;
		while (this.frame.matrixTableModel.getComparisonsRemainingFromDesign() > 0) {
			judgmentMatrix = this.frame.matrixTableModel.getJudgmentMatrix();
			designMatrix = this.frame.matrixTableModel.getDesignMatrix();
			remainingIndices = new ArrayList<String>();

			// find matrix entries that need to be filled, which are still 0
			for (int i = 0; i < designMatrix.length; i++) {
				for (int j = 0; j < designMatrix.length; j++) {
					if (designMatrix[i][j] && judgmentMatrix[i][j] == 0.0) {
						remainingIndices.add(i + "_" + j);
					}
				}
			}

			// get random entry from the remaining comparison
			int r = randGen.nextInt(remainingIndices.size());
			String[] indexString = remainingIndices.get(r).split("_");
			int i = Integer.parseInt(indexString[0]);
			int j = Integer.parseInt(indexString[1]);
			String component1 = artifacts[i];
			String component2 = artifacts[j];

			// show dialogue box to ask user to enter value
			boolean successful = false;
			double comparisonVal = 0.0;
			while (!successful) {
				String newMsg = "How many times bigger or smaller is "
					+ component1 + " compared to " + component2 + "?\n";
				String userVal = (String) JOptionPane.showInputDialog(frame,
						newMsg, "Comparison", JOptionPane.PLAIN_MESSAGE);

				// cancel is pressed. stop guiding.
				if (userVal == null) {
					isCancel = true;
					break;
				}

				try {
					comparisonVal = Double.parseDouble(userVal);
					successful = true;
				} catch (NumberFormatException e) {
					successful = false;
					// custom title, error icon
					JOptionPane.showMessageDialog(frame,
							"Please enter a number.", "Input Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}

			// break out of main loop if cancel is pressed
			if (isCancel) {
				break;
			}

			// set judgmentMatrix with the new comparison value
			judgmentMatrix[i][j] = comparisonVal;
			this.frame.matrixTableModel.setJudgmentMatrix(judgmentMatrix);
			this.frame.matrixTable.refresh();
			this.frame.matrixTable.updateUI();
			updateComparisonRemaining();

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// show calculation result
		if (!isCancel) {
			JOptionPane.showMessageDialog(frame, "Comparison complete",
					"Comparison", JOptionPane.PLAIN_MESSAGE);
		}
	}

	/**
	 * Set total comparison and comparisons remaining
	 */
	public void updateComparisonRemaining() {
		this.frame.totalComparisons = this.frame.matrixTableModel
				.getTotalComparisonFromDesign();
		this.frame.comparisonsRemaining = this.frame.matrixTableModel
				.getComparisonsRemainingFromDesign();
		this.frame.totalComparisonsField.setValue(this.frame.totalComparisons);
		this.frame.comparisonsRemainingField
				.setValue(this.frame.comparisonsRemaining);
	}

	/**
	 * Return key used to store comparison notes
	 * 
	 * @param comp1
	 *            component 1
	 * @param comp2
	 *            component 2
	 * @return key used to store comparison notes
	 */
	public String comparisonKey(String comp1, String comp2) {
		return comp1 + "_" + comp2;
	}
}
