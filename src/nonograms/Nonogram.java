package nonograms;

public class Nonogram
{
	int [][] columnHints;
	int [][] rowHints;
	byte [][] grid;

	public static final byte FILLED = 1;
	public static final byte CLEAR = -1;
	public static final byte UNKNOWN = 0;

	public Nonogram(int width, int height)
	{
		grid = new byte[height][width];
		columnHints = new int[width][0];
		rowHints = new int[height][0];
	}

	public NonogramRow getColumn(int column)
	{
		return new Column(column);
	}

	public NonogramRow getRow(int row)
	{
		return new Row(row);
	}

	public int getWidth()
	{
		return columnHints.length;
	}

	public int getHeight()
	{
		return rowHints.length;
	}

	class Column implements NonogramRow
	{
		int colNumber;

		Column(int colNumber) {
			this.colNumber = colNumber;
		}

		public int getHintCount() {
			return columnHints[colNumber].length;
		}

		public int getHint(int hintIdx) {
			return columnHints[colNumber][hintIdx];
		}

		public int getLength() {
			return grid.length;
		}

		public byte get(int index) {
			return grid[index][colNumber];
		}

		public void set(int index, byte value) {
			grid[index][colNumber] = value;
		}
	}

	class Row implements NonogramRow
	{
		int rowNumber;

		Row(int rowNumber) {
			this.rowNumber = rowNumber;
		}

		public int getHintCount() {
			return rowHints[rowNumber].length;
		}

		public int getHint(int hintIdx) {
			return rowHints[rowNumber][hintIdx];
		}

		public int getLength() {
			return grid[rowNumber].length;
		}

		public byte get(int index) {
			return grid[rowNumber][index];
		}

		public void set(int index, byte value) {
			grid[rowNumber][index] = value;
		}
	}
}
