package nonograms;

import java.io.*;
import java.util.*;

public class NonogramReader
{
	BufferedReader in;

	NonogramReader(Reader in)
	{
		this.in = new BufferedReader(in);
	}

	public Nonogram readNonogram()
		throws IOException
	{
		ArrayList<String> rowStrings = new ArrayList<String>();
		ArrayList<String> columnStrings = new ArrayList<String>();
		boolean inRows = false;
		boolean inColumns = false;

		String s;
		while ( (s = in.readLine()) != null )
		{
			if (s.equals("%rows")) {
				inRows = true;
				inColumns = false;
			}
			else if (s.equals("%columns")) {
				inRows = false;
				inColumns = true;
			}
			else {
				if (inRows) {
					rowStrings.add(s);
				}
				else if (inColumns) {
					columnStrings.add(s);
				}
			}
		}

		Nonogram N = new Nonogram(
				columnStrings.size(),
				rowStrings.size()
				);
		for (int i = 0; i < rowStrings.size(); i++) {
			N.rowHints[i] = parseHint(rowStrings.get(i));
		}
		for (int i = 0; i < columnStrings.size(); i++) {
			N.columnHints[i] = parseHint(columnStrings.get(i));
		}

		return N;
	}

	int [] parseHint(String s)
	{
		String [] parts = s.split(" ");
		int [] rv = new int[parts.length];
		for (int i = 0; i < parts.length; i++) {
			rv[i] = Integer.parseInt(parts[i]);
		}
		return rv;
	}

	public static Nonogram load(File f)
		throws IOException
	{
		FileReader fr = new FileReader(f);
		try
		{
			return new NonogramReader(fr).readNonogram();
		}
		finally
		{
			fr.close();
		}
	}
}
