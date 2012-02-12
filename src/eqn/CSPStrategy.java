package eqn;

import map.*;
import set.*;
import java.util.Random;
import eqn.CSPCell;

public final class CSPStrategy implements Strategy
{

	public CSPCell[][] constraints;
	int x = 0, y = 0;
	public void play(Map m)
	{
		Random rand = new Random();
		boolean first = true;
		constraints = new CSPCell[m.rows()][m.columns()];

		while(!m.done())
		{
			if(first)
			{
				first = false;
				m.probe(0,0);
				continue;
			}

			//Start building constraints for this state
			for(int i = 0; i < m.rows(); i++)
				for(int j = 0; j < m.columns(); j++)
				{
					int cellVal = m.look(i, j);//Look at a cell
					if(cellVal == m.MARKED)
						constraints[i][j].cellType = CSPCell.type.MINE;
				}

			m.probe(x, y++);
			//System.out.println(x + " " + y);
			if(y >= m.rows())
			{
				x++;
				y = 0;
			}
		}
	}
}
