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
	int value = -1;
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
		if(value != -1) return;
		value = val;
		cellType = type.CLEAR;
			
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
		if(value == -1) return;
		neighbourChanged = false;
		int count = 0;

		synchronized(m)
		{
		ArrayList<CSPCell> clearCells = new ArrayList<CSPCell>();
		for(CSPCell cell : neighbours)
		{
			if(cell.cellType == type.UNKNOWN)
				count++;
			/*if(cell.cellType == type.CLEAR)
				clearCells.add(cell);*/
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
				

		//No mines left unaccounted for. Only clear spaces remain
		if(value == 0)
		{
			for(CSPCell cell : neighbours)
				if(cell.cellType == type.UNKNOWN)
				{
					cell.changed();
					cell.cellType = type.CLEAR;
				}
		}
		//The unknown values are mines
		else if(count == value)
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

	public void simplify(Map m)
	{
		if(value == -1) return;
		//See if the neighbors contain subsets that we can simplify

		synchronized(m)
		{
		ArrayList<CSPCell> clearCells = new ArrayList<CSPCell>();
		for(CSPCell cell : neighbours)
		{
			if(cell.getValue() == -1) continue;
			//No need to check if the other cell has more constraints
			//if(cell.neighbours.size() >= cell.neighbours.size()) continue;

			boolean isSubset = true;
			for(CSPCell val : cell.neighbours)
			{
				if(val.cellType == type.CLEAR) continue;
				if(!neighbours.contains(val))
				{
					isSubset = false;
					break;
				}
			}

			if(isSubset)
			{
				//System.out.printf("Simplified!");
				for(CSPCell val : cell.neighbours) clearCells.add(val);
				value -= cell.getValue();
			}
		}
		for(CSPCell cell : clearCells)
			neighbours.remove(cell);
		}
	}

	public boolean hasRevealedNeighbours()
	{
		boolean edge = false;

		for(CSPCell cell : neighbours)
		{
			if(cell.cellType == type.CLEAR && cell.value != -1)
			{
				edge = true;
				break;
			}
		}

		return edge;

	}

	public sat isSatisfied()
	{
		if(value == -1) return sat.YES;
		int count = 0;
		int Ucount = 0;
		for(CSPCell cell : neighbours)
		{
			if(cell.cellType == type.MINE) count++;
			if(cell.cellType == type.UNKNOWN) Ucount++;
		}
		if(count == value && Ucount == 0) return sat.YES;
		if(count > value || (Ucount + count < value)) return sat.INVALID;
		return sat.NO;
	}
}
