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
		throws Contradiction
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

		solveForward(0,0);

		System.out.println();
	}

	void solveForward(int hintIdx, int start)
		throws Contradiction
	{
		if (hintIdx >= row.getHintCount()) {
			// rest of the row must be clear
			for (int j = start; j < row.getLength(); j++) {
				row.set(j, CLEAR);
			}
			return;
		}

		int curHint = row.getHint(hintIdx);
		if (start + curHint >= row.getLength()) {
			throw new Contradiction();
		}

		if (row.get(start) == CLEAR) {
			solveForward(hintIdx, start+1);
			return;
		}
		else if (row.get(start) == FILLED) {
			// fill in the rest
			for (int j = 1; j < curHint; j++) {
				row.set(start+j, FILLED);
			}
			if (start+curHint < row.getLength()) {
				row.set(start+curHint, CLEAR);
			}
			solveForward(hintIdx+1, start+curHint+1);
			return;
		}

		assert row.get(start) == UNKNOWN;

		// look ahead to see if any cell has been set
		{
		int j = 1;
		while (j < curHint && row.get(start+j) == UNKNOWN)
			j++;

		if (j < curHint && row.get(start+j) == CLEAR) {
			// cannot fit here
			row.set(start, CLEAR);
			return;
		}

		if (j + 1 < curHint && row.get(start+j) == FILLED) {
			while (j + 1 < curHint) {
				j++;
				row.set(start+j, FILLED);
			}
		}
		}

		// look ahead to the next known-clear cell
		{
		int j = curHint;
		while (start+j < row.getLength() &&
			row.get(start+j) != CLEAR) {
			j++;
		}

		if (start+j < row.getLength() && j - curHint < curHint) {
			row.set(start+j-curHint, FILLED);
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

	public static class Contradiction extends Exception
	{
	}
}
