package edu.cmu.pairedComparison;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.text.NumberFormatter;

/**
 * Main frame for PairedComparison tool
 * 
 * @author Shigeru Sasao
 */
public class MainFrame extends JFrame {

	/** copyright string */
	protected static String copyrightString = "PairedComparison (c) Shigeru Sasao\n2013 All Rights Reserved\n\nIf you have comments please contact Shigeru Sasao at ssasao@alumni.cmu.edu\n\n";

	/** reference to self */
	JFrame frame;

	/** main frame actions */
	MainFrameActions actions;

	/** paired comparison calculator */
	protected Calculator calculator;

	/** number of artifacts to estimate */
	protected int artifactCount;

	/** replication factor for incomplete acyclic design */
	protected int replicationFactor;

	/** total number of comparisons as per design */
	protected int totalComparisons;

	/** number of comparisons remaining to be filled */
	protected int comparisonsRemaining;

	/** holds artifact count */
	protected JFormattedTextField artifactCountField;

	/** holds replication factor */
	protected JFormattedTextField replicationFactorField;

	/** holds total number of comparisons */
	protected JFormattedTextField totalComparisonsField;

	/** holds comparisons remaining to be filled */
	protected JFormattedTextField comparisonsRemainingField;

	/** holds component 1 name */
	protected JTextField component1Field;

	/** holds component 2 name */
	protected JTextField component2Field;

	/** hold notes for component 1 */
	protected JTextArea notes1TextArea;

	/** hold notes for component 2 */
	protected JTextArea notes2TextArea;

	/** hold notes for individual comparison */
	protected JTextArea comparisonNotesTextArea;

	/** menu bar */
	protected JMenuBar menuBar;

	/** file menu */
	protected JMenu fileMenu;

	/** import menu item */
	protected JMenuItem importMenuItem;

	/** export menu item */
	protected JMenuItem exportMenuItem;

	/** calculate button */
	protected JButton calculateButton;

	/** guide button */
	protected JButton guideButton;

	/** holds judgment table */
	protected MatrixTable matrixTable;

	/** judgment table model */
	protected MatrixTableModel matrixTableModel;

	/** input/output text area */
	protected JTextArea ioBox;

	/** number formatter for output */
	protected NumberFormatter numFormat;

	/** file io */
	protected FileIO fileIO;

	/** file dialog */
	protected JFileChooser fileDialog;

	/** status bar */
	protected StatusBar statusBar;

	/**
	 * Constructor
	 */
	public MainFrame() {
		this.setSize(1285, 750);
		this.setMinimumSize(new Dimension(1000, 750));
		this.addWindowListener(new ExitListener());
		this.setTitle("Paired Comparison - Incomplete Cyclic Design");
		this.frame = this;

		// initialize main frame actions
		actions = new MainFrameActions(this);

		// initialize content panel
		Container content = this.getContentPane();
		content.setBackground(Color.white);
		content.setLayout(new BorderLayout());

		// initialize calculator
		calculator = Calculator.getInstance();

		// initialize number formatter
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		this.numFormat = new NumberFormatter(decimalFormat);

		// initialize variables
		this.artifactCount = 5;
		this.replicationFactor = 2;

		// initialize menu
		this.menuBar = new JMenuBar();
		this.fileMenu = new JMenu("File");
		this.importMenuItem = new JMenuItem("import");
		this.importMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actions.importMatrix();
			}
		});
		this.exportMenuItem = new JMenuItem("export");
		this.exportMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actions.exportMatrix();
			}
		});
		this.fileMenu.add(this.importMenuItem);
		this.fileMenu.add(this.exportMenuItem);
		this.menuBar.add(this.fileMenu);
		this.setJMenuBar(this.menuBar);

		// initialize file dialog
		fileIO = new FileIO();
		fileDialog = new JFileChooser();

		// initialize artifact count and replication factor components
		JLabel artifactCountLabel = new JLabel("# of Artifacts (n)");
		JLabel replicationFactorLabel = new JLabel("Replication factor (r)");
		this.artifactCountField = new JFormattedTextField(NumberFormat
				.getInstance());
		this.artifactCountField.setValue(this.artifactCount);
		this.artifactCountField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actions.artifactCountActionPerformed();
			}
		});
		this.artifactCountField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (!e.isTemporary()) {
					artifactCountField.setValue(new Integer(artifactCount));
				}
			}
		});
		this.replicationFactorField = new JFormattedTextField(NumberFormat
				.getInstance());
		this.replicationFactorField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actions.replicationFactorActionPerformed();
			}

		});
		this.replicationFactorField.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (!e.isTemporary()) {
					replicationFactorField.setValue(new Integer(
							replicationFactor));
				}
			}
		});
		this.replicationFactorField.setValue(this.replicationFactor);

		// initialize button components
		this.calculateButton = new JButton("Calculate");
		this.calculateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actions.calculateActionPerformed();
			}
		});
		this.guideButton = new JButton("Guide");
		this.guideButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actions.guideActionPerformed();
			}
		});
		this.matrixTableModel = new MatrixTableModel(this.artifactCount,
				replicationFactor);
		this.matrixTableModel.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {

				// set total number of comparisons and comparisons remaining.
				actions.updateComparisonRemaining();
			}
		});
		this.matrixTable = new MatrixTable(this.matrixTableModel);
		this.matrixTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						actions.setNotes1();
						actions.setNotes2();
						actions.setComparisonNotes();
						actions.setComparisonInfo();
					}

				});
		this.matrixTable.getColumnModel().addColumnModelListener(
				new TableColumnModelListener() {

					public void columnAdded(TableColumnModelEvent e) {
					}

					public void columnMarginChanged(ChangeEvent e) {
					}

					public void columnMoved(TableColumnModelEvent e) {
					}

					public void columnRemoved(TableColumnModelEvent e) {
					}

					public void columnSelectionChanged(ListSelectionEvent e) {
						actions.setNotes1();
						actions.setNotes2();
						actions.setComparisonNotes();
						actions.setComparisonInfo();
					}

				});
		this.ioBox = new JTextArea(this.getSize().width / 95,
				this.getSize().height / 75);

		// initialize total comparisons and comparisons remaining components.
		JLabel totalComparisonsLabel = new JLabel("Total comparisons");
		JLabel comparisonsRemainingLabel = new JLabel("Comparisons remaining");
		this.totalComparisonsField = new JFormattedTextField(NumberFormat
				.getInstance());
		this.totalComparisonsField.setEditable(false);
		this.comparisonsRemainingField = new JFormattedTextField(NumberFormat
				.getInstance());
		this.comparisonsRemainingField.setEditable(false);
		this.totalComparisons = this.matrixTableModel
				.getTotalComparisonFromDesign();
		this.comparisonsRemaining = this.matrixTableModel
				.getComparisonsRemainingFromDesign();
		this.totalComparisonsField.setValue(this.totalComparisons);
		this.comparisonsRemainingField.setValue(this.comparisonsRemaining);

		// Initialize current comparison information
		this.component1Field = new JTextField();
		this.component1Field.setEditable(false);
		this.component1Field.setBorder(new EtchedBorder());
		this.component2Field = new JTextField();
		this.component2Field.setEditable(false);
		this.component2Field.setBorder(new EtchedBorder());
		this.notes1TextArea = new JTextArea(5, 5);
		this.notes1TextArea.setEditable(false);
		this.notes1TextArea.setLineWrap(true);
		notes1TextArea.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
			}

			public void focusLost(FocusEvent e) {
				actions.setNotes1();
			}

		});
		this.notes2TextArea = new JTextArea(5, 5);
		this.notes2TextArea.setEditable(false);
		this.notes2TextArea.setLineWrap(true);
		notes2TextArea.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
			}

			public void focusLost(FocusEvent e) {
				actions.setNotes2();
			}

		});

		// Initialize comparison notes field
		// Initialize current comparison information
		this.comparisonNotesTextArea = new JTextArea();
		this.comparisonNotesTextArea.setEditable(false);
		this.comparisonNotesTextArea.setLineWrap(true);
		comparisonNotesTextArea.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
			}

			public void focusLost(FocusEvent e) {
				actions.setComparisonNotes();
			}

		});

		// initialize status bar
		this.statusBar = new StatusBar();

		// west panel
		JPanel westPanel = new JPanel();
		westPanel.setBackground(Color.white);
		westPanel.setLayout(new BorderLayout());
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new BorderLayout());
		JPanel paramPanel = new JPanel();
		TitledBorder paramTitle = BorderFactory
				.createTitledBorder("Parameters");
		paramPanel.setBorder(paramTitle);
		paramPanel.setBackground(Color.white);
		paramPanel.setLayout(new GridLayout(4, 2));
		paramPanel.add(artifactCountLabel);
		paramPanel.add(replicationFactorLabel);
		paramPanel.add(this.artifactCountField);
		paramPanel.add(this.replicationFactorField);
		paramPanel.add(totalComparisonsLabel);
		paramPanel.add(comparisonsRemainingLabel);
		paramPanel.add(this.totalComparisonsField);
		paramPanel.add(this.comparisonsRemainingField);

		JPanel infoPanel = new JPanel();
		infoPanel.setBackground(Color.white);
		infoPanel.setLayout(new GridLayout(3, 1));
		TitledBorder infoTitle = BorderFactory
				.createTitledBorder("Comparison Information");
		infoPanel.setBorder(infoTitle);
		JPanel component1Panel = new JPanel();
		component1Panel.setLayout(new BorderLayout());
		component1Panel.add(this.component1Field, BorderLayout.NORTH);
		TitledBorder notesTitle = BorderFactory.createTitledBorder("Notes");
		JScrollPane notes1ScrollPanel = new JScrollPane(this.notes1TextArea);
		notes1ScrollPanel.setBorder(notesTitle);
		notes1ScrollPanel.setBackground(Color.white);
		component1Panel.add(notes1ScrollPanel, BorderLayout.CENTER);
		infoPanel.add(component1Panel);
		JPanel component2Panel = new JPanel();
		component2Panel.setLayout(new BorderLayout());
		component2Panel.add(this.component2Field, BorderLayout.NORTH);
		JScrollPane notes2ScrollPanel = new JScrollPane(this.notes2TextArea);
		notes2ScrollPanel.setBorder(notesTitle);
		notes2ScrollPanel.setBackground(Color.white);
		component2Panel.add(notes2ScrollPanel, BorderLayout.CENTER);
		infoPanel.add(component2Panel);
		JPanel comparisonNotesPanel = new JPanel();
		comparisonNotesPanel.setLayout(new BorderLayout());
		TitledBorder comparisonNotesTitle = BorderFactory
				.createTitledBorder("Comparison Notes");
		JScrollPane comparisonNotesScrollPanel = new JScrollPane(
				this.comparisonNotesTextArea);
		comparisonNotesScrollPanel.setBorder(comparisonNotesTitle);
		comparisonNotesScrollPanel.setBackground(Color.white);
		comparisonNotesPanel.add(comparisonNotesScrollPanel,
				BorderLayout.CENTER);
		infoPanel.add(comparisonNotesPanel);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 2));
		buttonPanel.add(this.calculateButton);
		buttonPanel.add(this.guideButton);
		controlPanel.add(paramPanel, BorderLayout.NORTH);
		controlPanel.add(infoPanel, BorderLayout.CENTER);
		controlPanel.add(buttonPanel, BorderLayout.SOUTH);
		westPanel.add(controlPanel, BorderLayout.NORTH);

		// center panel
		JPanel centerPanel = new JPanel(); 
		centerPanel.setBackground(Color.white);
		centerPanel.setLayout(new BorderLayout());
		JScrollPane scrollPanel = new JScrollPane(matrixTable);
		scrollPanel.setBorder(new EtchedBorder());
		scrollPanel.setBackground(Color.white);
		JPanel matrixPanel = new JPanel();
		matrixPanel.setLayout(new BorderLayout());
		matrixPanel.add(scrollPanel, BorderLayout.NORTH);
		matrixPanel.add(this.statusBar, BorderLayout.SOUTH);
		matrixPanel.setBorder(new EtchedBorder());
		JScrollPane ioPanel = new JScrollPane(ioBox);
		ioPanel.setBorder(new EtchedBorder());
		centerPanel.add(matrixPanel, BorderLayout.NORTH);
		centerPanel.add(ioPanel, BorderLayout.SOUTH);

		// add child panels to parent panel
		content.add(westPanel, BorderLayout.WEST);
		content.add(centerPanel, BorderLayout.CENTER);

		// show copyright message
		JOptionPane.showMessageDialog(frame, copyrightString, "",
				JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Window exit listener class
	 */
	public class ExitListener extends WindowAdapter {
		public void windowClosing(WindowEvent event) {
			System.exit(0);
		}
	}

	/**
	 * Main
	 * 
	 * @param args
	 *            Arguments
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			MainFrame main = new MainFrame();
			main.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
