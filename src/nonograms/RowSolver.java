package nonograms;

import static nonograms.Nonogram.*;

public class RowSolver
{
	NonogramRow row;

	public RowSolver(NonogramRow row)
	{
		this.row = row;
	}

	public void solve()
	{
		if (row.getHintCount() == 0) {
			// special case
			for (int j = 0; j < row.getLength(); j++) {
				row.set(j, CLEAR);
			}
			return;
		}

		System.out.print("hint:");
		for (int i = 0; i < row.getHintCount(); i++) {
			System.out.print(" " + row.getHint(i));
		}
		System.out.println();

		System.out.print("row: [");
		for (int i = 0; i < row.getLength(); i++) {
			System.out.print(row.get(i) == FILLED ? '#' :
				row.get(i) == CLEAR ? '.' : ' ');
		}
		System.out.println("]");

		int filled = 0;
		int unknown = 0;
		for (int j = 0; j < row.getLength(); j++) {
			if (row.get(j) == FILLED) {
				filled++;
			}
			else if (row.get(j) == UNKNOWN) {
				unknown++;
			}
		}

		if (unknown == row.getLength()) {
			assert filled == 0;

			solveInitial();
			System.out.println();
			return;
		}

		solveForward();

		System.out.println();
	}

	void solveForward()
	{
		solveForward(0,0);
	}

	void solveForward(int hintIdx, int col)
	{
		if (hintIdx >= row.getHintCount() ||
			col >= row.getLength()) return;

		int curHint = row.getHint(hintIdx);

		if (row.get(col) == -1) {
			solveForward(hintIdx, col+1);
			return;
		}
		else if (row.get(col) == 1) {
			// fill in the rest
			for (int j = 1; j < curHint; j++) {
				row.set(col+j, FILLED);
			}
			if (col+curHint < row.getLength()) {
				row.set(col+curHint, CLEAR);
			}
			solveForward(hintIdx+1, col+curHint+1);
			return;
		}

		assert row.get(col) == 0;

		// look ahead to see if any cell has been set

		for (int j = 1; j < curHint; j++) {
			if (row.get(col+j) == -1) {
				// cannot fit here
				row.set(col, CLEAR);
				return;
			}
		}

		if (col+curHint < row.getLength()) {
			if (row.get(col+curHint) == 1) {
				// this span cannot start here
				row.set(col, CLEAR);
				return;
			}
		}
	}

	void solveInitial()
	{
		int sumFilled = 0;
		for (int i = 0; i < row.getHintCount(); i++) {
			sumFilled += row.getHint(i);
		}
		int gapCount = row.getHintCount() - 1;
		int needed = sumFilled + gapCount;
		int flex = row.getLength() - needed;

		int col = 0;
		for (int i = 0; i < row.getHintCount(); i++)
		{
			int curHint = row.getHint(i);
			if (curHint > flex) {
				int excess = curHint - flex;
				for (int j = col + flex; j < col + curHint; j++) {
					row.set(j, FILLED);
				}
			}
			col += curHint + 1;
		}
	}
}
