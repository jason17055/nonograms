package nonograms;

public class RowSolver
{
	int [] hint;
	byte [] values;

	public RowSolver(int [] hint, byte [] values)
	{
		this.hint = hint;
		this.values = values;
	}

	public void solve()
	{
		if (hint.length == 0) {
			// special case
			for (int j = 0; j < values.length; j++) {
				values[j] = -1;
			}
			return;
		}

		System.out.print("hint:");
		for (int i = 0; i < hint.length; i++) {
			System.out.print(" " + hint[i]);
		}
		System.out.println();

		System.out.print("row: [");
		for (int i = 0; i < values.length; i++) {
			System.out.print(values[i] == 1 ? '#' :
				values[i] == -1 ? '.' : ' ');
		}
		System.out.println("]");

		int filled = 0;
		int unknown = 0;
		for (int j = 0; j < values.length; j++) {
			if (values[j] == 1) {
				filled++;
			}
			else if (values[j] == 0) {
				unknown++;
			}
		}

		if (unknown == values.length) {
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
		if (hintIdx >= hint.length ||
			col >= values.length) return;

		int curHint = hint[hintIdx];

		if (values[col] == -1) {
			solveForward(hintIdx, col+1);
			return;
		}
		else if (values[col] == 1) {
			// fill in the rest
			for (int j = 1; j < curHint; j++) {
				values[col+j] = 1;
			}
			if (col+curHint < values.length) {
				values[col+curHint] = -1;
			}
			solveForward(hintIdx+1, col+curHint+1);
			return;
		}

		assert values[col] == 0;

		// look ahead to see if any cell has been set

		for (int j = 1; j < curHint; j++) {
			if (values[col+j] == -1) {
				// cannot fit here
				values[col] = -1;
				return;
			}
		}

		if (col+curHint < values.length) {
			if (values[col+curHint] == 1) {
				// this span cannot start here
				values[col] = -1;
				return;
			}
		}
	}

	void solveInitial()
	{
		int sumFilled = 0;
		for (int i = 0; i < hint.length; i++) {
			sumFilled += hint[i];
		}
		int gapCount = hint.length - 1;
		int needed = sumFilled + gapCount;
		int flex = values.length - needed;

		int col = 0;
		for (int i = 0; i < hint.length; i++)
		{
			int curHint = hint[i];
			if (curHint > flex) {
				int excess = curHint - flex;
				for (int j = col + flex; j < col + curHint; j++) {
					values[j] = 1;
				}
			}
			col += curHint + 1;
		}
	}
}
