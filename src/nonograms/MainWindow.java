package nonograms;

import java.awt.*;
import javax.swing.*;

public class MainWindow extends JFrame
{
	NonogramView view;

	public MainWindow()
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

		this.view = new NonogramView(nonogram);
		add(view, BorderLayout.CENTER);

		pack();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
	}

	public static void main(String [] args)
	{
		SwingUtilities.invokeLater(new Runnable() {
		public void run() {
			new MainWindow().setVisible(true);
		}});
	}
}
