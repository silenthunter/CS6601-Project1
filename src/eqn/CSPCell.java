package eqn;
import java.util.ArrayList;
import map.*;

public class CSPCell
{
	enum type { MINE, CLEAR, UNKNOWN };

	public int row;
	public int col;
	public ArrayList<CSPCell> neighbours = new ArrayList<CSPCell>();
	int value = 0;
	public type cellType = type.UNKNOWN;
	public boolean neighbourChanged = false;

	public CSPCell(int x, int y)
	{
		row = x;
		col = y;
	}

	public int getValue()
	{
		return value;
	}

	public void setValue(int val)
	{
		if(val == 0)
		{
			for(CSPCell cell : neighbours)
				cell.cellType = type.CLEAR;
			cellType = type.CLEAR;
		}
		else
			value = val;
			
		//Mark neighbours to be updated
		changed();
	}

	public void changed()
	{
		//Mark neighbours to be updated
		for(CSPCell cell : neighbours)
			cell.neighbourChanged = true;
	}

	public void calculate(Map m)
	{
		neighbourChanged = false;
		int count = 0;

		for(CSPCell cell : neighbours)
			if(cell.cellType == type.UNKNOWN || cell.cellType == type.MINE)
				count++;

		//The unknown values are mines
		if(count == value)
		{
			for(CSPCell cell : neighbours)
				if(cell.cellType == type.UNKNOWN)
				{
					cell.changed();
					cell.cellType = type.MINE;
					m.mark(cell.row, cell.col);
				}
		}

	}
}
