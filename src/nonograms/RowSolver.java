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
		System.out.println();
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
