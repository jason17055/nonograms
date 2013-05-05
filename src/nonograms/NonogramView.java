package nonograms;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

public class NonogramView extends JComponent
{
	Nonogram model;

	boolean editMode;

	boolean metricsDirty;
	int rowHeight;
	int maxRowHintWidth;
	int columnWidth;
	int maxColumnHintHeight;

	boolean [][] tagBlue;

	static final int CELL_WIDTH = 16;
	static final int CELL_HEIGHT = 16;

	public NonogramView(Nonogram model)
	{
		this.model = model;
		this.metricsDirty = true;
		clearTagged();

		setFont(new Font("Arial", Font.BOLD, 11));
		MouseAdapter mouse = new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				onMousePressed(evt);
			}
		};
		addMouseListener(mouse);
	}

	private void onMousePressed(MouseEvent evt)
	{
		int row = (evt.getY() - maxColumnHintHeight) / rowHeight;
		int column = (evt.getX() - maxRowHintWidth) / columnWidth;

		if (row >= 0 && row < model.getHeight() &&
			column >= 0 && column < model.getWidth())
		{
			onCellPressed(column, row, evt.getButton());
			return;
		}
	}

	@Override
	public Dimension getPreferredSize()
	{
		if (metricsDirty) {
			reloadMetrics();
		}

		return new Dimension(
			maxRowHintWidth + columnWidth * model.getWidth() + model.getWidth()/5 + 1,
			maxColumnHintHeight + rowHeight * model.getHeight() + model.getHeight()/5 + 1
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
		int [] hint = model.rowHints[row];
		StringBuilder sb = new StringBuilder();
		for (int j = 0; j < hint.length; j++) {
			sb.append(Integer.toString(hint[j]));
			sb.append(j == hint.length-1 ? " " : "  ");
		}
		return sb.toString();
	}

	void drawColumnHint(Graphics2D gr, Point p, int column)
	{
		int [] hint = model.columnHints[column];
		FontMetrics fm = gr.getFontMetrics();

		for (int j = 0; j < hint.length; j++) {
			int y = p.y + (j - hint.length) * fm.getHeight() + fm.getAscent();
			String s = Integer.toString(hint[j]);
			int x = p.x + columnWidth / 2
				- fm.stringWidth(s) / 2;
			gr.drawString(s, x, y);
		}
	}

	void drawRowHint(Graphics2D gr, Point p, int row)
	{
		String s = getRowHintAsString(row);
		FontMetrics fm = gr.getFontMetrics();

		gr.drawString(s,
			p.x - fm.stringWidth(s),
			p.y + rowHeight / 2
				- fm.getHeight() / 2
				+ fm.getAscent());
	}

	void drawFilledCell(Graphics2D gr, int x, int y)
	{
		Paint oldPaint = gr.getPaint();

		if (tagBlue[y][x]) {
			gr.setColor(Color.BLUE);
		}
		else {
			gr.setColor(Color.BLACK);
		}

		Point p = getCellPosition(x, y);
		gr.fillRect(p.x+2,p.y+2,
			columnWidth-3,
			rowHeight-3);

		gr.setPaint(oldPaint);
	}

	void drawBlockedCell(Graphics2D gr, int x, int y)
	{
		Paint oldPaint = gr.getPaint();

		if (tagBlue[y][x]) {
			gr.setColor(Color.BLUE);
		}
		else {
			gr.setColor(Color.RED);
		}

		Point p = getCellPosition(x, y);
		gr.drawLine(p.x + 4, p.y + 5,  p.x + 10, p.y + 11);
		gr.drawLine(p.x + 5, p.y + 5,  p.x + 11, p.y + 11);
		gr.drawLine(p.x + 4, p.y + 11, p.x + 10, p.y + 5);
		gr.drawLine(p.x + 5, p.y + 11, p.x + 11, p.y + 5);

		gr.setPaint(oldPaint);
	}

	public Point getCellPosition(int col, int row)
	{
		return new Point(
			maxRowHintWidth + col * columnWidth + col/5,
			maxColumnHintHeight + row * rowHeight + row/5
			);
	}

	@Override
	public void paintComponent(Graphics gr1)
	{
		Graphics2D gr = (Graphics2D) gr1;

		int farRight = maxRowHintWidth + model.getWidth() * columnWidth + model.getWidth() / 5;
		int farBottom = maxColumnHintHeight + model.getHeight() * rowHeight + model.getHeight() / 5;

		gr.setColor(Color.YELLOW);
		for (int i = 0; i < model.getWidth(); i++) {
			if (new RowSolver(model.getColumn(i)).hasContradiction()) {
				Point p = getCellPosition(i, 0);
				gr.fillRect(p.x, p.y, columnWidth, farBottom-p.y);
			}
		}

		for (int i = 0; i < model.getHeight(); i++) {
			if (new RowSolver(model.getRow(i)).hasContradiction()) {
				Point p = getCellPosition(0, i);
				gr.fillRect(p.x, p.y, farRight-p.x, rowHeight);
			}
		}

		gr.setColor(Color.BLACK);
		for (int i = 0; i <= model.getWidth(); i++) {
			Point p = getCellPosition(i, 0);
			gr.drawLine(p.x, p.y, p.x, farBottom);
			if (i % 5 == 4) {
				gr.drawLine(p.x+columnWidth, p.y,
					p.x+columnWidth, farBottom);
			}

			if (i < model.getWidth()) {
				drawColumnHint(gr, p, i);
			}
		}

		for (int i = 0; i <= model.getHeight(); i++) {
			Point p = getCellPosition(0, i);
			gr.drawLine(p.x, p.y, farRight, p.y);
			if (i % 5 == 4) {
				gr.drawLine(p.x, p.y+rowHeight,
					farRight, p.y+rowHeight);
			}

			if (i < model.getHeight()) {
				drawRowHint(gr, p, i);
			}
		}

		for (int y = 0; y < model.getHeight(); y++) {
			for (int x = 0; x < model.getWidth(); x++) {
				if (model.grid[y][x] == 1) {
					drawFilledCell(gr, x, y);
				}
				else if (model.grid[y][x] == -1) {
					drawBlockedCell(gr, x, y);
				}
			}
		}
	}

	void onCellPressed(int x, int y, int button)
	{
		if (button == MouseEvent.BUTTON1) {
			if (model.grid[y][x] == 1) {
				model.grid[y][x] = 0;
				repaint();
			}
			else {
				model.grid[y][x] = 1;
				repaint();
			}
		}
		else if (button == MouseEvent.BUTTON3) {
			if (model.grid[y][x] == -1) {
				model.grid[y][x] = 0;
				repaint();
			}
			else {
				model.grid[y][x] = -1;
				repaint();
			}
		}
	}

	public void clearTagged()
	{
		tagBlue = new boolean[model.getHeight()][model.getWidth()];
	}

	public void tag(int x, int y)
	{
		tagBlue[y][x] = true;
	}
}
