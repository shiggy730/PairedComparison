package edu.cmu.pairedComparison;

import java.util.HashMap;

import javax.swing.table.DefaultTableModel;

/**
 * Matrix Table Model
 * 
 * @author Shigeru Sasao
 */
public class MatrixTableModel extends DefaultTableModel {

	/** paired comparison calculator */
	private Calculator calculator;

	/** number of artifacts */
	private int artifactCount;

	/** replication factor */
	private int replicationFactor;

	/** reference index */
	private int referenceIndex;

	/** reference size */
	private double referenceSize;

	/** artifact names */
	private String[] artifacts;

	/** artifact notes */
	private String[] artifactNotes;

	/** comparison notes */
	private HashMap<String, String> comparisonNotes;

	/** judgment matrix */
	private double[][] judgmentMatrix;

	/** design matrix */
	private boolean[][] designMatrix;

	/**
	 * Default constructor
	 */
	public MatrixTableModel() {

	}

	/**
	 * Constructor
	 * 
	 * @param initArtifactCount
	 *            The initial number of artifacts
	 */
	public MatrixTableModel(int initArtifactCount, int replicationFactor) {
		this.calculator = new Calculator();
		this.referenceIndex = -1;
		this.referenceSize = 0.0;
		this.artifactCount = initArtifactCount;
		this.replicationFactor = replicationFactor;
		this.artifacts = new String[this.artifactCount];
		this.artifactNotes = new String[this.artifactCount];
		this.comparisonNotes = new HashMap<String, String>();
		this.judgmentMatrix = new double[this.artifactCount][this.artifactCount];
		resetDesign();
	}

	/**
	 * Return a copy of itself (MatrixTableModel), given the new artifact count
	 * and replication factore. Note that the data will be lost if the new
	 * artifact count is smaller than the original artifact count.
	 * 
	 * @param newArtifactCount
	 *            The new artifact count
	 * @param newReplicationFactor
	 *            The new replication factor
	 * @return Copy of itself
	 */
	public MatrixTableModel getCopy(int newArtifactCount,
			int newReplicationFactor) {
		MatrixTableModel newMatrixTableModel = new MatrixTableModel(
				newArtifactCount, newReplicationFactor);

		// reset the design of the new matrix
		newMatrixTableModel.resetDesign();
		boolean[][] newDesign = newMatrixTableModel.getDesignMatrix();

		// copy values to new judgment matrix, artifacts and notes
		double[][] newJudgmentMatrix = new double[newArtifactCount][newArtifactCount];
		String[] newArtifacts = new String[newArtifactCount];
		String[] newArtifactNotes = new String[newArtifactCount];
		int min = 0;
		if (artifactCount <= newArtifactCount) {
			min = artifactCount;
		} else {
			min = newArtifactCount;
		}
		for (int i = 0; i < min; i++) {
			newArtifacts[i] = artifacts[i];
			newArtifactNotes[i] = artifactNotes[i];
			for (int j = 0; j < min; j++) {

				// copy old values only if the new design requires it
				if (newDesign[i][j]) {
					newJudgmentMatrix[i][j] = judgmentMatrix[i][j];
				}
			}
		}
		newMatrixTableModel.setJudgmentMatrix(newJudgmentMatrix);
		newMatrixTableModel.setArtifacts(newArtifacts);

		// set reference index and size
		if (referenceIndex < artifactCount) {
			newMatrixTableModel.setReferenceIndex(referenceIndex);
			newMatrixTableModel.setReferenceSize(referenceSize);
		}

		return newMatrixTableModel;
	}

	@Override
	public int getColumnCount() {
		// extra 2 fields for reference, row header
		return this.artifactCount + 2;
	}

	@Override
	public int getRowCount() {
		// extra 1 fields for column header
		return this.artifactCount + 1;
	}

	@Override
	public Object getValueAt(int row, int col) {

		// reference value header
		if (row == 0 && col == 0) {
			return "Ref. Value";
		}

		// artifact name header
		if (row == 0 && col == 1) {
			return "Artifact Name";
		}

		// artifact names
		if (col == 1) {
			return this.artifacts[row - 1];
		}
		if (row == 0) {
			return this.artifacts[col - 2];
		}

		// reference value
		if (col == 0 && row == this.referenceIndex + 1) {
			return this.referenceSize;
		}

		// create default 0 filled judgment matrix
		if (row > 0 && col > 1) {
			double val;
			if (row - 1 == col - 2) {
				val = 1.0;
			} else {
				val = this.judgmentMatrix[row - 1][col - 2];
			}
			return val;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.DefaultTableModel#setValueAt(java.lang.Object,
	 * int, int)
	 */
	public void setValueAt(Object val, int row, int col) {

		// reference value
		if (col == 0) {
			if (val.equals("")) {
				this.referenceIndex = -1;
				this.referenceSize = 0.0;
			} else {
				this.referenceIndex = row - 1;
				this.referenceSize = Double.parseDouble(val.toString());
			}

			// artifact name
		} else if (col == 1) {
			this.artifacts[row - 1] = val.toString();

			// judgment matrix
		} else if (row > 0 && col > 1) {
			this.judgmentMatrix[row - 1][col - 2] = Double.parseDouble(val
					.toString());
		}

		this.fireTableDataChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.DefaultTableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int row, int col) {

		// reference value and artifact names
		if (col <= 1 && row > 0) {
			return true;

			// judgment matrix
		} else if (row > 0 && col > 1) {
			return this.designMatrix[row - 1][col - 2];
		}
		return false;
	}

	/**
	 * Reset the matrix design
	 */
	public void resetDesign() {
		this.designMatrix = calculator.generateDesign(this.artifactCount,
				this.replicationFactor);
	}

	/**
	 * Get total number of comparisons as per design matrix. This is the number
	 * of comparisons the estimator will need to make.
	 * 
	 * @return Total number of comparisons as per design matrix.
	 */
	public int getTotalComparisonFromDesign() {
		int count = 0;
		for (int i = 0; i < this.designMatrix.length; i++) {
			for (int j = 0; j < this.designMatrix.length; j++) {
				if (this.designMatrix[i][j]) {
					count++;
				}
			}
		}

		return count;
	}

	/**
	 * Get number of comparisons remaining in the judgment matrix.
	 * 
	 * @return Number of comparisons remaining to be filled.
	 */
	public int getComparisonsRemainingFromDesign() {
		int count = 0;
		for (int i = 0; i < this.designMatrix.length; i++) {
			for (int j = 0; j < this.designMatrix.length; j++) {
				if (this.designMatrix[i][j] && this.judgmentMatrix[i][j] != 0.0) {
					count++;
				}
			}
		}

		return this.getTotalComparisonFromDesign() - count;
	}

	/**
	 * get number of artifacts
	 * 
	 * @return the artifactCount
	 */
	public int getArtifactCount() {
		return artifactCount;
	}

	/**
	 * set the number of artifacts
	 * 
	 * @param artifactCount
	 *            the artifactCount to set
	 */
	public void setArtifactCount(int artifactCount) {
		this.artifactCount = artifactCount;
	}

	/**
	 * @return the calculator
	 */
	public Calculator getCalculator() {
		return calculator;
	}

	/**
	 * @param calculator
	 *            the calculator to set
	 */
	public void setCalculator(Calculator calculator) {
		this.calculator = calculator;
	}

	/**
	 * @return the replicationFactor
	 */
	public int getReplicationFactor() {
		return replicationFactor;
	}

	/**
	 * @param replicationFactor
	 *            the replicationFactor to set
	 */
	public void setReplicationFactor(int replicationFactor) {
		this.replicationFactor = replicationFactor;
	}

	/**
	 * @return the referenceIndex
	 */
	public int getReferenceIndex() {
		return referenceIndex;
	}

	/**
	 * @param referenceIndex
	 *            the referenceIndex to set
	 */
	public void setReferenceIndex(int referenceIndex) {
		this.referenceIndex = referenceIndex;
	}

	/**
	 * @return the referenceSize
	 */
	public double getReferenceSize() {
		return referenceSize;
	}

	/**
	 * @param referenceSize
	 *            the referenceSize to set
	 */
	public void setReferenceSize(double referenceSize) {
		this.referenceSize = referenceSize;
	}

	/**
	 * @return the artifacts
	 */
	public String[] getArtifacts() {
		return artifacts;
	}

	/**
	 * @param artifacts
	 *            the artifacts to set
	 */
	public void setArtifacts(String[] artifacts) {
		this.artifacts = artifacts;
	}

	/**
	 * @return the artifact notes
	 */
	public String[] getArtifactNotes() {
		return artifactNotes;
	}

	/**
	 * @param artifactNotes
	 *            list of notes on artifacts
	 */
	public void setArtifactNotes(String[] artifactNotes) {
		this.artifactNotes = artifactNotes;
	}

	/**
	 * @return the comparison notes
	 */
	public HashMap<String, String> getComparisonNotes() {
		return comparisonNotes;
	}

	/**
	 * @param comparisonNotes
	 *            the comparison notes to set
	 */
	public void setComparisonNotes(HashMap<String, String> comparisonNotes) {
		this.comparisonNotes = comparisonNotes;
	}

	/**
	 * @return the judgmentMatrix
	 */
	public double[][] getJudgmentMatrix() {
		return judgmentMatrix;
	}

	/**
	 * @param judgmentMatrix
	 *            the judgmentMatrix to set
	 */
	public void setJudgmentMatrix(double[][] judgmentMatrix) {
		this.judgmentMatrix = judgmentMatrix;
	}

	/**
	 * @return the designMatrix
	 */
	public boolean[][] getDesignMatrix() {
		return designMatrix;
	}

	/**
	 * @param designMatrix
	 *            the designMatrix to set
	 */
	public void setDesignMatrix(boolean[][] designMatrix) {
		this.designMatrix = designMatrix;
	}
}
