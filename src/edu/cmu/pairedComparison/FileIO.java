package edu.cmu.pairedComparison;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.ho.yaml.Yaml;

/**
 * Handle input/output for the files containing matrix data
 * 
 * @author Shigeru Sasao
 */
public class FileIO {

	/**
	 * characters that are invalid in file names
	 */
	public static char[] INVALID_CHARS = { ';', '=', '+', '<', '>', '|', '"',
			'[', ']', '\\', '/', '\'', ':', '*', '?' };

	/**
	 * constructor
	 */
	public FileIO() {

	}

	/**
	 * read matrix data from file
	 * 
	 * 
	 * @param inFile
	 *            name of input file
	 * @return table model with loaded data
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	public MatrixTableModel readFromFile(File inFile)
			throws FileNotFoundException, IOException {

		// check name of file
		if (inFile == null || inFile.equals("")) {
			throw new IllegalStateException("Name of file cannot be blank.");
		}
		
		// use jyaml to read from file
		MatrixTableModel matrixTableModel = (MatrixTableModel)Yaml.load(inFile);
		
		return matrixTableModel;
	}

	/**
	 * write matrix data to file
	 * 
	 * @param outFile
	 *            name of output file
	 * @param numData
	 *            number data
	 * @throws IOException
	 */
	public void writeToFile(File outFile, MatrixTableModel tableModel)
			throws IOException {

		// check file name
		if (outFile == null || outFile.equals("")) {
			throw new IllegalStateException("Name of file cannot be blank.");
		}
		
		// use jyaml to write to file
		Yaml.dump(tableModel, outFile);
		
		return;
	}

	/**
	 * check whether file name contains invalid characters
	 * 
	 * @param fullPath
	 *            the file path to check
	 * @return true if valid, false if invalid
	 */
	public boolean isValidFileName(String fullPath) {

		// get file name from full path
		File f = new File(fullPath);
		String fileName = f.getName();

		boolean isValid = true;
		for (char c : INVALID_CHARS) {
			if (fileName.indexOf(c) != -1) {
				isValid = false;
			}
		}

		return isValid;
	}

	/**
	 * check whether file exists.
	 * 
	 * @param fullPath
	 *            the file path to check
	 * @return true if exists, false if not exists
	 */
	public boolean checkFileExists(String fullPath) {
		File f = new File(fullPath);
		return f.exists();
	}
}
