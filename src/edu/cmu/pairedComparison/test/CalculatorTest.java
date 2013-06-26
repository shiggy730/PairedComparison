package edu.cmu.pairedComparison.test;

import junit.framework.TestCase;
import edu.cmu.pairedComparison.Calculator;

public class CalculatorTest extends TestCase {
	
    public static Calculator calculator;
	
    public CalculatorTest() {
	calculator = Calculator.getInstance();
    }
	
    public void testPairedComparison1() {

	int replicationFactor = 2;
	int referenceId = 2;
	double referenceSize = 3.0;
		
	// generate test matrix
	double[][] judgmentMatrix = new double[10][10];
	boolean[][] designMatrix = new boolean[10][10];
	for (int i = 0; i < judgmentMatrix.length; i++) {
	    for (int j = 0; j < judgmentMatrix[i].length; j++) {
		judgmentMatrix[i][j] = 0.0;
		designMatrix[i][j] = false;
	    }
	}
	judgmentMatrix[0][1] = 0.8;
	designMatrix[0][1] = true;
	judgmentMatrix[1][2] = 2.5;
	designMatrix[1][2] = true;
	judgmentMatrix[2][3] = 2.5;
	designMatrix[2][3] = true;
	judgmentMatrix[3][4] = 0.4;
	designMatrix[3][4] = true;
	judgmentMatrix[4][5] = 1.2;
	designMatrix[4][5] = true;
	judgmentMatrix[5][6] = 0.9;
	designMatrix[5][6] = true;
	judgmentMatrix[6][7] = 4.0;
	designMatrix[6][7] = true;
	judgmentMatrix[7][8] = 0.05;
	designMatrix[7][8] = true;
	judgmentMatrix[8][9] = 9.0;
	designMatrix[8][9] = true;
	judgmentMatrix[9][0] = 0.2;
	designMatrix[9][0] = true;
		
	// fill matrix
	judgmentMatrix = calculator.calcReciprocals(designMatrix, judgmentMatrix, judgmentMatrix.length);
	judgmentMatrix = calculator.fillJudgmentMatrix(judgmentMatrix, judgmentMatrix.length);
		
	// print matrices
	System.out.println("");
	for (int i = 0; i < judgmentMatrix.length; i++) {
	    for (int j = 0; j < judgmentMatrix[i].length; j++) {
		System.out.print("[" + judgmentMatrix[i][j] + "]\t");
	    }
	    System.out.print("\n");
	}
		
	// calculate relative ratios
	double[] ratio = calculator.calcRatio(judgmentMatrix, judgmentMatrix.length);
		
	// print ratios
	System.out.println("");
	System.out.println("Ratio: ");
	for (int i = 0; i < ratio.length; i++) {
	    System.out.println(ratio[i]);
	}
		
	// calculate inconsistency index
	double inconsistencyIndex = 
	    calculator.calcInconsistencyIndex(judgmentMatrix, judgmentMatrix.length, replicationFactor);
		
	// print inconsistency index
	System.out.println("");
	System.out.println("Inconsistency Index: " + inconsistencyIndex);
		
	// calculate relativeSize
	double[] relativeSize = calculator.calcRatio(judgmentMatrix, judgmentMatrix.length);
		
	// print relative size
	System.out.println("");
	System.out.println("Relative size: ");
	for (int i = 0; i < relativeSize.length; i++) {
	    System.out.println(relativeSize[i]);
	}
		
	// calculate absolute size
	double[] absoluteSize = calculator.calcAbsoluteSize(referenceSize, referenceId, 
							    relativeSize, judgmentMatrix.length);
		
	// print absolute size
	System.out.println("");
	System.out.println("Absolute size: ");
	for (int i = 0; i < absoluteSize.length; i++) {
	    System.out.println(absoluteSize[i]);
	}
		
	double[] absoluteSizeStdDev = calculator.calcAbsoluteSizeStdDev(absoluteSize, 
									inconsistencyIndex, absoluteSize.length);
		
	// print absolute size std dev
	System.out.println("");
	System.out.println("Absolute size std dev: ");
	for (int i = 0; i < absoluteSizeStdDev.length; i++) {
	    System.out.println(absoluteSizeStdDev[i]);
	}
		
	// calculate sum absolute size std dev
	double sumAbsoluteSizeStdDev = calculator.calcSumAbsoluteSizeStdDev(absoluteSizeStdDev, 
									    absoluteSizeStdDev.length);
		
	// print sum absolute size std dev
	System.out.println("");
	System.out.println("Sum absolute size std dev: " + sumAbsoluteSizeStdDev);
    }
	
    public void testGenerateDesign() {
	boolean[][] design = calculator.generateDesign(10, 2);
	for (int i = 0; i < design.length; i++) {
	    for (int j = 0; j < design.length; j++) {
		System.out.print(design[i][j] + " ");
	    }
	    System.out.print("\n");
	}
		
    }
}
