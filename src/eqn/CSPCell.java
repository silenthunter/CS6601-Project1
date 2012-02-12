package eqn;
import java.util.ArrayList;
import map.*;

public class CSPCell
{
	enum type { MINE, CLEAR, UNKNOWN };
	enum sat { NO, YES, INVALID };

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

		ArrayList<CSPCell> clearCells = new ArrayList<CSPCell>();
		for(CSPCell cell : neighbours)
		{
			if(cell.cellType == type.UNKNOWN)
				count++;
			if(cell.cellType == type.CLEAR)
				clearCells.add(cell);
			if(cell.cellType == type.MINE)//No need to keep mines around
			{
				clearCells.add(cell);
				value--;
				//count++;
			}
		}

		//Don't have constraints between already cleared cells
		for(CSPCell cell : clearCells)
			neighbours.remove(cell);
				

		//The unknown values are mines
		if(count == value && value != 0)
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

	public void simplify()
	{
		//See if the neighbors contain subsets that we can simplify
		for(CSPCell cell : neighbours)
		{
			//No need to check if the other cell has more constraints
			if(cell.neighbours.size() >= cell.neighbours.size()) continue;

			boolean isSubset = true;
			for(CSPCell val : cell.neighbours)
			{
				if(!neighbours.contains(val))
				{
					isSubset = false;
					break;
				}
			}

			if(isSubset)
			{
				System.out.printf("Simplified!");
				for(CSPCell val : cell.neighbours) neighbours.remove(val);
				value -= cell.getValue();
			}
		}
	}

	public sat isSatisfied()
	{
		int count = 0;
		for(CSPCell cell : neighbours)
		{
			if(cell.cellType == type.MINE) count++;
		}
		if(count == value) return sat.YES;
		if(count > value) return sat.INVALID;
		return sat.NO;
	}
}
