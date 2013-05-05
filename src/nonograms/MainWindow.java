package nonograms;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainWindow extends JFrame
{
	NonogramView view;

	static Nonogram sample1()
	{
		Nonogram nonogram = new Nonogram(5,5);
		nonogram.rowHints[0] = new int[] { 2, 2 };
		nonogram.rowHints[1] = new int[] { 2, 2 };
		nonogram.rowHints[2] = new int[] { 1 };
		nonogram.rowHints[3] = new int[] { 1 };
		nonogram.rowHints[4] = new int[] { 3 };

		nonogram.columnHints[0] = new int[] { 4 };
		nonogram.columnHints[1] = new int[] { 2 };
		nonogram.columnHints[2] = new int[] { 1 };
		nonogram.columnHints[3] = new int[] { 2, 1 };
		nonogram.columnHints[4] = new int[] { 2, 1 };

		return nonogram;
	}

	static Nonogram sample2()
	{
		Nonogram nonogram = new Nonogram(10,10);
		nonogram.rowHints[0] = new int[] { 7 };
		nonogram.rowHints[1] = new int[] { 5, 1 };
		nonogram.rowHints[2] = new int[] { 3 };
		nonogram.rowHints[3] = new int[] { 1, 1 };
		nonogram.rowHints[4] = new int[] { 2 };
		nonogram.rowHints[5] = new int[] { 2, 2 };
		nonogram.rowHints[6] = new int[] { 3, 2 };
		nonogram.rowHints[7] = new int[] { 7 };
		nonogram.rowHints[8] = new int[] { 3, 3 };
		nonogram.rowHints[9] = new int[] { 3, 5 };

		nonogram.columnHints[0] = new int[] { 4, 2 };
		nonogram.columnHints[1] = new int[] { 3, 2 };
		nonogram.columnHints[2] = new int[] { 4, 2 };
		nonogram.columnHints[3] = new int[] { 2, 3 };
		nonogram.columnHints[4] = new int[] { 2, 3, 1 };
		nonogram.columnHints[5] = new int[] { 1, 4 };
		nonogram.columnHints[6] = new int[] { 2, 3 };
		nonogram.columnHints[7] = new int[] { 3 };
		nonogram.columnHints[8] = new int[] { 4, 1 };
		nonogram.columnHints[9] = new int[] { 4 };

		return nonogram;
	}

	public MainWindow()
	{
		this.view = new NonogramView(sample2());
		add(view, BorderLayout.CENTER);

		JPanel buttonPane = new JPanel();
		JButton btn1 = new JButton("Solve By Row");
		btn1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				solveByRow();
			}});
		buttonPane.add(btn1);

		JButton btn2 = new JButton("Solve By Column");
		btn2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				solveByColumn();
			}});
		buttonPane.add(btn2);

		add(buttonPane, BorderLayout.SOUTH);

		pack();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
	}

	void solveByColumn()
	{
		for (int i = 0; i < view.model.getWidth(); i++) {
			new RowSolver(view.model.getColumn(i)).solve();
		}
		repaint();
	}

	void solveByRow()
	{
		for (int i = 0; i < view.model.getHeight(); i++) {
			new RowSolver(view.model.getRow(i)).solve();
		}
		repaint();
	}

	public static void main(String [] args)
	{
		SwingUtilities.invokeLater(new Runnable() {
		public void run() {
			new MainWindow().setVisible(true);
		}});
	}
}
