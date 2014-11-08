package edu.buffalo.cse601.datamining.project2.display;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.ListModel;
import javax.swing.UIManager;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.SwingConstants;

import edu.buffalo.cse601.datamining.project2.datastructures.Cluster;
import edu.buffalo.cse601.datamining.project2.dbScan.DBScan;
import edu.buffalo.cse601.datamining.project2.loaders.HierarchicalLoader;
import edu.buffalo.cse601.datamining.project2.loaders.KMeansLoader;
import edu.buffalo.cse601.datamining.project2.mixtureModel.MixtureModelEM;

import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.JList;
import javax.swing.JComboBox;

public class ClusteringAlgorithms extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTable table_1;
	private JTextField textField_3;
	private JTextField textField_4;
	private JTable table;
	private JTextField textField_5;
	private JTextField textField_6;
	private JTextField textField_7;
	private JTextField textField_8;
	private JTextField eps;
	private JTextField MinPts;
	private JTextField jac;
	private JTextField cor;
	private JTable table_2;
	private JTextField clusNo;
	private JTextField threshold;
	private JTextField jacc;
	private JTextField corr;
	private JTable table_3;
	private JTable table_4;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClusteringAlgorithms frame = new ClusteringAlgorithms();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ClusteringAlgorithms() {
		setTitle("ClusteringAlgorithms");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 736, 491);

		String header[] = new String[] { "ClusterId", "GeneId" };
		String header2[] = new String[] { "GeneId", "ClusterId" };

		final DefaultTableModel model = new DefaultTableModel(0, 0);
		model.setColumnIdentifiers(header);

		final DefaultTableModel model2 = new DefaultTableModel(0, 0);
		model2.setColumnIdentifiers(header);

		final DefaultTableModel model3 = new DefaultTableModel(0, 0);
		model3.setColumnIdentifiers(header2);

		final DefaultTableModel model4 = new DefaultTableModel(0, 0);
		model4.setColumnIdentifiers(header2);

		JTabbedPane jtp = new JTabbedPane();
		getContentPane().add(jtp);
		JPanel jp4 = new JPanel();
		final JPanel jp1 = new JPanel();
		// JLabel label1 = new JLabel();
		// label1.setText("You are in area of Tab1");
		// jp1.add(label1);
		jtp.addTab("K-Means", jp1);
		jp1.setLayout(null);

		JLabel lblNumberOfClusters = new JLabel("Number of Clusters:");
		lblNumberOfClusters.setBounds(10, 34, 117, 23);
		jp1.add(lblNumberOfClusters);

		textField = new JTextField();
		textField.setBounds(127, 35, 86, 20);
		jp1.add(textField);
		textField.setColumns(10);

		JLabel lblList = new JLabel("SSError:");
		lblList.setBounds(248, 34, 79, 23);
		jp1.add(lblList);

		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(306, 35, 86, 20);
		jp1.add(textField_1);

		JLabel lblIterations = new JLabel("Iterations:");
		lblIterations.setBounds(428, 34, 79, 23);
		jp1.add(lblIterations);

		textField_2 = new JTextField();
		textField_2.setColumns(10);
		textField_2.setBounds(500, 35, 86, 21);
		jp1.add(textField_2);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 219, 695, 195);
		jp1.add(scrollPane_1);

		table_1 = new JTable(model);
		scrollPane_1.setViewportView(table_1);

		final JLabel lblNewLabel = new JLabel("File");
		lblNewLabel.setBounds(153, 98, 282, 29);
		jp1.add(lblNewLabel);

		JButton btnInputFile = new JButton("Select Input File");
		btnInputFile.setHorizontalAlignment(SwingConstants.LEFT);
		btnInputFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File("C:\\"));
				int result = fileChooser.showOpenDialog(jp1);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					System.out.println("Selected file: "
							+ selectedFile.getAbsolutePath());
					lblNewLabel.setText(selectedFile.getAbsolutePath());

				}
			}
		});
		btnInputFile.setBounds(10, 101, 133, 23);
		jp1.add(btnInputFile);

		textField_5 = new JTextField();
		textField_5.setColumns(10);
		textField_5.setBounds(127, 186, 86, 20);
		jp1.add(textField_5);

		JButton btnEvalute = new JButton("Evalute");
		btnEvalute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String fileName = lblNewLabel.getText();
				String numberOfPartitions = textField.getText();
				String[] rows = textField_3.getText().split("\\|");
				String SSE = textField_1.getText();
				String iterations = textField_2.getText();
				double jacoeff = 0.0;
				double corr = 0.0;
				ArrayList<Double> indexes = new ArrayList<>();
				indexes.add(jacoeff);
				indexes.add(corr);
				KMeansLoader loader = new KMeansLoader();
				if (rows.length < Integer.valueOf(numberOfPartitions))
					rows = null;
				Map<Integer, ArrayList<Integer>> kMeansResult = loader
						.KMeansAlgo(fileName, rows, numberOfPartitions,
								iterations, SSE, indexes);
				model.setRowCount(0);
				for (Entry<Integer, ArrayList<Integer>> entry : kMeansResult
						.entrySet()) {
					model.addRow(new Object[] { entry.getKey() + 1,
							entry.getValue() });
				}
				textField_5.setText(String.format("%1.4f", indexes.get(0)));
				textField_6.setText(String.format("%1.4f", indexes.get(1)));
			}
		});
		btnEvalute.setBounds(497, 140, 89, 23);
		jp1.add(btnEvalute);

		JLabel lblInitialclusterrows = new JLabel("InitialClusterRows:");
		lblInitialclusterrows.setBounds(10, 140, 144, 23);
		jp1.add(lblInitialclusterrows);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(127, 135, 265, 28);
		jp1.add(scrollPane);

		textField_3 = new JTextField();
		scrollPane.setViewportView(textField_3);
		textField_3.setColumns(10);

		JLabel lblJacardCoeff = new JLabel("Jacard Coeff:");
		lblJacardCoeff.setBounds(10, 185, 117, 23);
		jp1.add(lblJacardCoeff);

		JLabel lblCorrelationCoeff = new JLabel("Correlation Coeff:");
		lblCorrelationCoeff.setBounds(383, 185, 117, 23);
		jp1.add(lblCorrelationCoeff);

		textField_6 = new JTextField();
		textField_6.setColumns(10);
		textField_6.setBounds(500, 186, 86, 20);
		jp1.add(textField_6);
		JPanel jp2 = new JPanel();

		jtp.addTab("Hierarchical Clustering", jp2);
		jp2.setLayout(null);

		JLabel label = new JLabel("Number of Clusters:");
		label.setBounds(10, 30, 121, 14);
		jp2.add(label);

		textField_4 = new JTextField();
		textField_4.setBounds(131, 27, 86, 20);
		textField_4.setColumns(10);
		jp2.add(textField_4);

		final JLabel label_1 = new JLabel("File");
		label_1.setBounds(165, 94, 282, 29);
		jp2.add(label_1);

		JButton button = new JButton("Select Input File");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File("C:\\"));
				int result = fileChooser.showOpenDialog(jp1);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					System.out.println("Selected file: "
							+ selectedFile.getAbsolutePath());
					label_1.setText(selectedFile.getAbsolutePath());

				}
			}
		});

		button.setHorizontalAlignment(SwingConstants.LEFT);
		button.setBounds(10, 97, 133, 23);
		jp2.add(button);

		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(10, 168, 695, 246);
		jp2.add(scrollPane_2);

		table = new JTable(model2);
		scrollPane_2.setViewportView(table);

		JButton button_1 = new JButton("Evalute");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String numofClusters = textField_4.getText();
				String name = label_1.getText();
				Double jacoeff = new Double(0.0);
				Double corr = new Double(0.0);
				ArrayList<Double> indexes = new ArrayList<>();
				indexes.add(jacoeff);
				indexes.add(corr);
				HierarchicalLoader ldr = new HierarchicalLoader();
				List<Cluster> clusteringResult = ldr.Loader(name,
						numofClusters, indexes);
				int i = 1;
				model2.setRowCount(0);
				for (Cluster cluster : clusteringResult) {
					model2.addRow(new Object[] { String.valueOf(i),
							cluster.getExpressions().toString() });
					i++;
				}
				textField_7.setText(String.format("%1.4f", indexes.get(1)));
				textField_8.setText(String.format("%1.4f", indexes.get(0)));
			}
		});
		button_1.setBounds(289, 26, 89, 23);
		jp2.add(button_1);

		JLabel label_2 = new JLabel("Jacard Coeff:");
		label_2.setBounds(10, 134, 117, 23);
		jp2.add(label_2);

		textField_7 = new JTextField();
		textField_7.setColumns(10);
		textField_7.setBounds(403, 137, 86, 20);
		jp2.add(textField_7);

		JLabel label_3 = new JLabel("Correlation Coeff:");
		label_3.setBounds(289, 134, 117, 23);
		jp2.add(label_3);

		textField_8 = new JTextField();
		textField_8.setColumns(10);
		textField_8.setBounds(131, 134, 86, 20);
		jp2.add(textField_8);

		JPanel jp3 = new JPanel();
		jtp.addTab("DBScan", jp3);
		jp3.setLayout(null);

		JLabel lblEps = new JLabel("EPS:");
		lblEps.setBounds(10, 29, 96, 14);
		jp3.add(lblEps);

		eps = new JTextField();
		eps.setBounds(135, 26, 86, 20);
		eps.setColumns(10);
		jp3.add(eps);

		JLabel lblMin = new JLabel("MinPts:");
		lblMin.setBounds(291, 29, 54, 14);
		jp3.add(lblMin);

		MinPts = new JTextField();
		MinPts.setBounds(412, 26, 86, 20);
		MinPts.setColumns(10);
		jp3.add(MinPts);

		final JLabel FileSelected = new JLabel("File");
		FileSelected.setBounds(153, 84, 128, 29);
		jp3.add(FileSelected);

		JButton File_3 = new JButton("Select Input File");
		File_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File("C:\\"));
				int result = fileChooser.showOpenDialog(jp1);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					System.out.println("Selected file: "
							+ selectedFile.getAbsolutePath());
					FileSelected.setText(selectedFile.getAbsolutePath());
				}
			}
		});
		File_3.setHorizontalAlignment(SwingConstants.LEFT);
		File_3.setBounds(10, 87, 133, 23);
		jp3.add(File_3);

		jac = new JTextField();
		jac.setColumns(10);
		jac.setBounds(135, 136, 86, 20);
		jp3.add(jac);

		cor = new JTextField();
		cor.setColumns(10);
		cor.setBounds(412, 136, 86, 20);
		jp3.add(cor);

		final JComboBox<String> list = new JComboBox<String>();
		list.setBounds(412, 84, 86, 26);
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(
					"Properties/DistanceMeasures.txt"));
			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				list.addItem(strLine);

			}

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		jp3.add(list);

		JScrollPane scrollPane_3 = new JScrollPane();
		scrollPane_3.setBounds(10, 184, 695, 230);
		jp3.add(scrollPane_3);

		table_4 = new JTable(model3);
		scrollPane_3.setViewportView(table_4);

		JButton button_2 = new JButton("Evalute");
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String epsilon = eps.getText();
				String mpts = MinPts.getText(); // Call DBScan from here
				int minPts = 0;
				if (!mpts.equals("")) {
					minPts = Integer.valueOf(mpts);
				}
				double epsil = Double.valueOf(epsilon);
				String fName = FileSelected.getText();
				Double jacoeff = new Double(0.0);
				Double corr = new Double(0.0);
				ArrayList<Double> indexes = new ArrayList<>();
				indexes.add(jacoeff);
				indexes.add(corr);
				DBScan scan = new DBScan();
				Map<Integer, Integer> clusteringResult = scan.main(fName, list
						.getSelectedItem().toString(), epsil, minPts, indexes);
				model3.setRowCount(0);
				for (Map.Entry<Integer, Integer> cluster : clusteringResult
						.entrySet()) {
					model3.addRow(new Object[] {
							String.valueOf(cluster.getKey()),
							cluster.getValue() });

				}
				jac.setText(String.format("%1.4f", indexes.get(0)));
				cor.setText(String.format("%1.4f", indexes.get(1)));

			}
		});
		button_2.setBounds(521, 25, 89, 23);
		jp3.add(button_2);

		JLabel label_5 = new JLabel("Jacard Coeff:");
		label_5.setBounds(10, 135, 117, 23);
		jp3.add(label_5);

		JLabel label_6 = new JLabel("Correlation Coeff:");
		label_6.setBounds(291, 135, 117, 23);
		jp3.add(label_6);

		JLabel distanceMeasure = new JLabel("Distance Measure:");
		distanceMeasure.setBounds(291, 91, 117, 14);
		jp3.add(distanceMeasure);

		jtp.addTab("MixtureModel", jp4);
		jp4.setLayout(null);

		JLabel label_4 = new JLabel("Number of Clusters:");
		label_4.setBounds(10, 37, 121, 14);
		jp4.add(label_4);

		clusNo = new JTextField();
		clusNo.setColumns(10);
		clusNo.setBounds(131, 34, 86, 20);
		jp4.add(clusNo);

		final JLabel label_7 = new JLabel("File");
		label_7.setBounds(153, 82, 128, 29);
		jp4.add(label_7);

		JButton button_4 = new JButton("Select Input File");
		button_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File("C:\\"));
				int result = fileChooser.showOpenDialog(jp1);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					System.out.println("Selected file: "
							+ selectedFile.getAbsolutePath());
					label_7.setText(selectedFile.getAbsolutePath());
				}

			}
		});
		button_4.setHorizontalAlignment(SwingConstants.LEFT);
		button_4.setBounds(10, 85, 133, 23);
		jp4.add(button_4);

		JLabel lblTheshold = new JLabel("Theshold");
		lblTheshold.setBounds(10, 137, 121, 14);
		jp4.add(lblTheshold);

		threshold = new JTextField();
		threshold.setColumns(10);
		threshold.setBounds(131, 134, 86, 20);
		jp4.add(threshold);

		JLabel label_8 = new JLabel("Jacard Coeff:");
		label_8.setBounds(10, 199, 117, 23);
		jp4.add(label_8);

		jacc = new JTextField();
		jacc.setColumns(10);
		jacc.setBounds(135, 200, 86, 20);
		jp4.add(jacc);

		JLabel label_9 = new JLabel("Correlation Coeff:");
		label_9.setBounds(291, 199, 117, 23);
		jp4.add(label_9);

		corr = new JTextField();
		corr.setColumns(10);
		corr.setBounds(412, 200, 86, 20);
		jp4.add(corr);

		JScrollPane scrollPane_4 = new JScrollPane();
		scrollPane_4.setBounds(10, 232, 695, 182);
		jp4.add(scrollPane_4);

		table_3 = new JTable(model4);
		scrollPane_4.setViewportView(table_3);

		JButton button_3 = new JButton("Evaluate");
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				double threshld = Double.valueOf(threshold.getText());
				int numberofPart = Integer.valueOf(clusNo.getText());
				String fileName = label_7.getText();
				Double jacoeff = new Double(0.0);
				Double cor = new Double(0.0);
				MixtureModelEM mmodel = new MixtureModelEM();
				ArrayList<Double> indexes = new ArrayList<>();
				indexes.add(jacoeff);
				indexes.add(cor);
				Map<Integer, Integer> values = mmodel.main(fileName,
						numberofPart, threshld, indexes);
				model4.setRowCount(0);
				for (Map.Entry<Integer, Integer> cluster : values.entrySet()) {
					model4.addRow(new Object[] {
							String.valueOf(cluster.getKey()),
							cluster.getValue() });

				}
				jacc.setText(String.format("%1.4f", indexes.get(0)));
				corr.setText(String.format("%1.4f", indexes.get(1)));

			}
		});
		button_3.setBounds(10, 170, 95, 23);
		jp4.add(button_3);

	}

	public void loadFile(String fileName) {
		if (fileName != null && fileName != "") {

		}
	}
}
