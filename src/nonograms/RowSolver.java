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
}
