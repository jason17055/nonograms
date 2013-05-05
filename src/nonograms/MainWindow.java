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

	public MainWindow()
	{
		this(sample1());
	}

	public MainWindow(Nonogram N)
	{
		super("Nonogram Solver");

		this.view = new NonogramView(N);
		add(view, BorderLayout.CENTER);

		JPanel buttonPane = new JPanel();
		JButton btn1 = new JButton("Start Over");
		btn1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				startOver();
			}});
		buttonPane.add(btn1);

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

	void startOver()
	{
		for (int y = 0; y < view.model.getHeight(); y++) {
			for (int x = 0; x < view.model.getWidth(); x++) {
				view.model.grid[y][x] = UNKNOWN;
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

		N.grid[y][x] = FILLED;
		boolean cannotBeFilled =
			new RowSolver(N.getRow(y)).hasContradiction() ||
			new RowSolver(N.getColumn(x)).hasContradiction();

		N.grid[y][x] = CLEAR;
		boolean cannotBeClear =
			new RowSolver(N.getRow(y)).hasContradiction() ||
			new RowSolver(N.getColumn(x)).hasContradiction();

		N.grid[y][x] = UNKNOWN;

		if (cannotBeFilled && !cannotBeClear) {
			return CLEAR;
		}

		if (cannotBeClear && !cannotBeFilled) {
			return FILLED;
		}

		return UNKNOWN;
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
