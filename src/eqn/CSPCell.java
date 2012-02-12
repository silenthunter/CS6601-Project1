package eqn;
import java.util.*;

public class CSPCell
{
	enum type { MINE, CLEAR, UNKNOWN };

	int row;
	int col;
	public ArrayList<CSPCell> neighbours = new ArrayList<CSPCell>();
	int value = 0;
	public type cellType = type.UNKNOWN;

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
	}
}
