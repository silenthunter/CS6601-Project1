package eqn;
import java.util.*;

public class CSPCell
{
	enum type { MINE, CLEAR, UNKNOWN };

	int row;
	int col;
	ArrayList<CSPCell> neighbours = new ArrayList<CSPCell>();
	int value = 0;
	public type cellType = type.UNKNOWN;

	public int getValue()
	{
		return value;
	}
}
