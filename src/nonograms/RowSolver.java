package nonograms;

import static nonograms.Nonogram.*;

public class RowSolver
{
	NonogramRow row;

	public RowSolver(NonogramRow row)
	{
		this.row = row;
	}

	public boolean hasContradiction()
	{
		return hasContradiction(0, 0);
	}

	boolean hasContradiction(int start, int firstHint)
	{
		int numHints = row.getHintCount() - firstHint;
		if (numHints == 0) {
			// special case
			for (int j = start; j < row.getLength(); j++) {
				if (row.get(j) == FILLED) {
					return true;
				}
			}
			return false;
		}

		assert numHints >= 1;

		int needed = numHints-1;
		for (int i = 0; i < numHints; i++) {
			needed += row.getHint(firstHint+i);
		}
		int len = row.getLength() - start;

		while (len > 0 && row.get(start) == CLEAR) {
			start++;
			len--;
		}

		if (len < needed) {
			return true;
		}

		int spanLen = row.getHint(firstHint);

		assert len >= spanLen;

		// check for any "cleared" spots in the span
		int firstFilled = Integer.MAX_VALUE;
		int lastClear = -1;
		for (int j = 0; j < spanLen; j++) {
			if (row.get(start+j) == CLEAR) {
				lastClear = j;
			}
			else if (row.get(start+j) == FILLED && j < firstFilled) {
				firstFilled = j;
			}
		}

		if (firstFilled < lastClear) {
			return true;
		}

		if (lastClear != -1) {
			return hasContradiction(start+lastClear+1, firstHint);
		}

		while (spanLen < len && row.get(start+spanLen)==FILLED) {
			if (row.get(start) == FILLED) {
				return true;
			}
			start++;
			len--;
		}

		// seems like it will fit there; must it go there?
		if (hasContradiction(start+spanLen+1, firstHint+1)) {
			if (row.get(start) == FILLED) {
				return true;
			}
			else {
				return hasContradiction(start+1, firstHint);
			}
		}
		return false;
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
		new RowSolver(new ReverseRowWrapper(row)).solveForward(0,0);

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
		if (start + curHint > row.getLength()) {
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
		int j = 1;
		while (j < curHint && row.get(start+j) == UNKNOWN)
			j++;

		if (j < curHint && row.get(start+j) == CLEAR) {
			// cannot fit here
			row.set(start, CLEAR);
			return;
		}

		if (j < curHint && row.get(start+j) == FILLED) {
			while (j + 1 < curHint) {
				j++;
				row.set(start+j, FILLED);
			}

			// we've found this span, now check ahead to see
			// if there's any known-clear cell

			while (start+j < row.getLength() &&
				row.get(start+j) != CLEAR)
			{
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

	static class ReverseRowWrapper implements NonogramRow
	{
		NonogramRow orow;

		ReverseRowWrapper(NonogramRow row) {
			this.orow = row;
		}

		public int getHintCount() {
			return orow.getHintCount();
		}

		public int getHint(int hintIdx) {
			return orow.getHint(
				orow.getHintCount() - 1 - hintIdx
				);
		}

		public int getLength() {
			return orow.getLength();
		}

		public byte get(int index) {
			return orow.get(
				orow.getLength() - 1 - index
				);
		}

		public void set(int index, byte value) {
			orow.set(
				orow.getLength() - 1 - index,
				value
				);
		}
	}
}
