package nonograms;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

public class NonogramView extends JComponent
{
	Nonogram model;

	boolean metricsDirty;
	int rowHeight;
	int maxRowHintWidth;
	int columnWidth;
	int maxColumnHintHeight;

	static final int CELL_WIDTH = 16;
	static final int CELL_HEIGHT = 16;

	public NonogramView(Nonogram model)
	{
		this.model = model;
		this.metricsDirty = true;

		setFont(new Font("Arial", Font.BOLD, 11));
	}

	@Override
	public Dimension getPreferredSize()
	{
		if (metricsDirty) {
			reloadMetrics();
		}

		return new Dimension(
			maxRowHintWidth + columnWidth * model.getWidth() + 2,
			maxColumnHintHeight + rowHeight * model.getHeight() + 2
			);
	}

	void reloadMetrics()
	{
		Graphics2D gr = (Graphics2D) getGraphics();
		Font f = gr.getFont();
		FontMetrics fm = gr.getFontMetrics();

		maxRowHintWidth = 0;
		for (int i = 0; i < model.getHeight(); i++) {
			Rectangle2D r = f.getStringBounds(
				getRowHintAsString(i),
				gr.getFontRenderContext());
			int rh = (int)Math.round(r.getHeight());
			int rw = (int)Math.round(r.getWidth());
			rowHeight = Math.max(rh, rowHeight);
			maxRowHintWidth = Math.max(rw, maxRowHintWidth);
		}

		maxColumnHintHeight = 0;
		for (int i = 0; i < model.getWidth(); i++) {
			int hintHeight = 0;
			for (int j = 0; j < model.columnHints[i].length; j++) {
				hintHeight += fm.getHeight();
				columnWidth = Math.max(fm.stringWidth(Integer.toString(model.columnHints[i][j])), columnWidth);
			}
			maxColumnHintHeight = Math.max(hintHeight, maxColumnHintHeight);
		}

		rowHeight = CELL_HEIGHT;
		columnWidth = CELL_WIDTH;

		metricsDirty = false;
	}

	String getRowHintAsString(int row)
	{
		StringBuilder sb = new StringBuilder();
		for (int j = 0; j < model.rowHints[row].length; j++) {
			sb.append(Integer.toString(model.rowHints[row][j]));
			sb.append(" ");
		}
		return sb.toString();
	}

	void drawColumnHint(Graphics2D gr, int column)
	{
		int [] hint = model.columnHints[column];
		FontMetrics fm = gr.getFontMetrics();

		for (int j = 0; j < hint.length; j++) {
			int y = maxColumnHintHeight + (j - hint.length) * fm.getHeight() + fm.getAscent();
			String s = Integer.toString(hint[j]);
			int x = maxRowHintWidth + column * columnWidth
				+ columnWidth / 2
				- fm.stringWidth(s) / 2;
			gr.drawString(s, x, y);
		}
	}

	void drawRowHint(Graphics2D gr, int row)
	{
		String s = getRowHintAsString(row);
		FontMetrics fm = gr.getFontMetrics();

		gr.drawString(s,
			maxRowHintWidth - fm.stringWidth(s),
			maxColumnHintHeight + row * rowHeight + fm.getAscent() + 1);
	}

	@Override
	public void paintComponent(Graphics gr1)
	{
		Graphics2D gr = (Graphics2D) gr1;

		for (int i = 0; i <= model.getWidth(); i++) {
			int x = maxRowHintWidth + i * columnWidth;
			gr.drawLine(x, maxColumnHintHeight,
				x, maxColumnHintHeight + model.getHeight() * rowHeight);
			if (i % 5 == 0) {
				gr.drawLine(x+1, maxColumnHintHeight,
					x+1, maxColumnHintHeight + model.getHeight() * rowHeight);
			}

			if (i < model.getWidth()) {
				drawColumnHint(gr, i);
			}
		}

		for (int i = 0; i <= model.getHeight(); i++) {
			int y = maxColumnHintHeight + i * rowHeight;
			gr.drawLine(maxRowHintWidth, y,
				maxRowHintWidth + model.getWidth() * columnWidth, y);
			if (i % 5 == 0) {
				gr.drawLine(maxRowHintWidth, y+1,
					maxRowHintWidth + model.getWidth() * columnWidth, y+1);
			}

			if (i < model.getHeight()) {
				drawRowHint(gr, i);
			}
		}
	}
}
