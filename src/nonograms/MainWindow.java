package nonograms;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import static nonograms.Nonogram.*;

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
		this(sample2());
	}

	public MainWindow(Nonogram N)
	{
		this.view = new NonogramView(N);
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

		JButton btn3 = new JButton("Solve Step");
		btn3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				solveStep();
			}});
		buttonPane.add(btn3);

		add(buttonPane, BorderLayout.SOUTH);

		pack();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
	}

	void solveByColumn()
	{
		for (int i = 0; i < view.model.getWidth(); i++) {
			try {
			new RowSolver(view.model.getColumn(i)).solve();
			}
			catch (RowSolver.Contradiction e) {
				JOptionPane.showMessageDialog(this,
				"Contradiction in column "+(i+1),
				"Contradiction",
				JOptionPane.INFORMATION_MESSAGE);
				break;
			}
		}
		view.repaint();
	}

	void solveByRow()
	{
		for (int i = 0; i < view.model.getHeight(); i++) {
			try {
			new RowSolver(view.model.getRow(i)).solve();
			}
			catch (RowSolver.Contradiction e) {
				JOptionPane.showMessageDialog(this,
				"Contradiction in row "+(i+1),
				"Contradiction",
				JOptionPane.INFORMATION_MESSAGE);
				break;
			}
		}
		view.repaint();
	}

	void solveStep()
	{
		view.clearTagged();
		byte [][] changes = new byte[view.model.getHeight()][view.model.getWidth()];
		for (int y = 0; y < view.model.getHeight(); y++) {
			for (int x = 0; x < view.model.getWidth(); x++) {
				if (view.model.grid[y][x] == UNKNOWN) {
					changes[y][x] = solveOneCell(view.model, x, y);
				}
			}
		}

		for (int y = 0; y < view.model.getHeight(); y++) {
			for (int x = 0; x < view.model.getWidth(); x++) {
				if (changes[y][x] != UNKNOWN) {
					view.model.grid[y][x] = changes[y][x];
					view.tag(x, y);
				}
			}
		}

		view.repaint();
	}

	byte solveOneCell(Nonogram N, int x, int y)
	{
		assert N.grid[y][x] == UNKNOWN;

		try {
		N.grid[y][x] = FILLED;
		if (new RowSolver(N.getRow(y)).hasContradiction()) {
			return CLEAR;
		}
		if (new RowSolver(N.getColumn(x)).hasContradiction()) {
			return CLEAR;
		}

		N.grid[y][x] = CLEAR;
		if (new RowSolver(N.getRow(y)).hasContradiction()) {
			return FILLED;
		}
		if (new RowSolver(N.getColumn(x)).hasContradiction()) {
			return FILLED;
		}

		return UNKNOWN;

		}
		finally {
			N.grid[y][x] = UNKNOWN;
		}
	}

	public static void main(final String [] args)
	{
		SwingUtilities.invokeLater(new Runnable() {
		public void run() {
			try {

			MainWindow mw;
			if (args.length >= 1) {
				mw = new MainWindow(
					NonogramReader.load(new File(args[0]))
					);
			}
			else {
				mw = new MainWindow();
			}
			mw.setVisible(true);

			}
			catch (Exception e) {
				JOptionPane.showMessageDialog(null,
					e.getMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE);
			}
		}});
	}
}
