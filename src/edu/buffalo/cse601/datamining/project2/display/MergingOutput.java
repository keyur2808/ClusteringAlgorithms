package edu.buffalo.cse601.datamining.project2.display;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JLabel;

public class MergingOutput extends JFrame {

	private JPanel contentPane;
	private JTextArea txtrMerge = null;

	/**
	 * Launch the application.
	 */

	public JTextArea getTxtrMerge() {
		return txtrMerge;
	}

	public void setTxtrMerge(JTextArea txtrMerge) {
		this.txtrMerge = txtrMerge;
	}

	/**
	 * Create the frame.
	 */
	public MergingOutput() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 738, 438);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 68, 702, 321);
		contentPane.add(scrollPane);

		txtrMerge = new JTextArea();
		scrollPane.setViewportView(txtrMerge);
		txtrMerge.setText("");

		JLabel lblMergingOuput = new JLabel("Merging Ouput");
		lblMergingOuput.setBounds(327, 11, 200, 50);
		contentPane.add(lblMergingOuput);
	}

}
