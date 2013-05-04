package nonograms;

public class Nonogram
{
	int [][] columnHints;
	int [][] rowHints;
	byte [][] grid;

	public Nonogram(int width, int height)
	{
		grid = new byte[height][width];
		columnHints = new int[width][0];
		rowHints = new int[height][0];
	}

	public int getWidth()
	{
		return columnHints.length;
	}

	public int getHeight()
	{
		return rowHints.length;
	}
}
