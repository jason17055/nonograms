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

	public byte[] getColumn(int column)
	{
		byte[] values = new byte[getHeight()];
		for (int i = 0; i < values.length; i++) {
			values[i] = grid[i][column];
		}
		return values;
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
