package eqn;

import map.*;
import set.*;
import java.util.Random;
import java.util.Stack;
import eqn.CSPCell;

public final class CSPStrategy implements Strategy
{

	public CSPCell[][] constraints;
	int[][] solutionSum;
	int totalSln;
	public void play(Map m)
	{

		//System.out.println("New game");
		Random rand = new Random();
		boolean first = true;
		constraints = new CSPCell[m.columns()][m.rows()];
		solutionSum = new int[m.columns()][m.rows()];

		//Initialize
		for(int i = 0; i < m.columns(); i++)
			for(int j = 0; j < m.rows(); j++)
				constraints[i][j] = new CSPCell(i, j);

		//Link all of the cell constraints
		for(int i = 0; i < m.columns(); i++)
			for(int j = 0; j < m.rows(); j++)
			{
				boolean L = false, R = false, D = false, U = false;
				if(i > 0)//up
					U = true;
				if(j > 0)//left
					L = true;
				if(i < m.columns() - 1)//down
					D = true;
				if(j < m.rows() - 1)//right
					R = true;

				if(U)//up
					constraints[i][j].neighbours.add(constraints[i - 1][j]);
				if(L)//left
					constraints[i][j].neighbours.add(constraints[i][j - 1]);
				if(D)//down
					constraints[i][j].neighbours.add(constraints[i + 1][j]);
				if(R)//right
					constraints[i][j].neighbours.add(constraints[i][j + 1]);
				if(U && R)
					constraints[i][j].neighbours.add(constraints[i - 1][j + 1]);
				if(U && L)
					constraints[i][j].neighbours.add(constraints[i - 1][j - 1]);
				if(D && L)
					constraints[i][j].neighbours.add(constraints[i + 1][j - 1]);
				if(D && R)
					constraints[i][j].neighbours.add(constraints[i + 1][j + 1]);
			}

		while(!m.done())
		{
			if(m.mines_minus_marks() < 0)
			{
				System.out.println("Bad marks!");
				return;
			}
			if(first)
			{
				first = false;
				constraints[0][0].setValue(m.probe(0, 0));
				continue;
			}

			int x = 0, y = 0;
			//Start building constraints for this state
			for(int i = 0; i < m.columns(); i++)
				for(int j = 0; j < m.rows(); j++)
				{
					constraints[i][j].calculate(m);
					constraints[i][j].simplify(m);
					int cellVal = m.look(i, j);//Look at a cell

					//A cell that hasn't been probed but constraints show to be clear
					if(cellVal == m.UNPROBED && constraints[i][j].cellType == CSPCell.type.CLEAR)
					{
						x = i;
						y = j;
						break;
					}
				}


			//Use constraints to try to find a clear cell
			if( x == 0 && y == 0)
			{
				CSPFindBestChoice(m);

				int maxVal = 0;
				for(int i = 0; i < m.columns(); i++)
					for(int j = 0; j  < m.rows(); j++)
					{
						if(constraints[i][j].cellType == CSPCell.type.UNKNOWN &&
							solutionSum[i][j] > maxVal)
						{
							maxVal = solutionSum[i][j];
							x = i;
							y = j;
						}
					}
				System.out.println(maxVal + " / " + totalSln);
			}

			//No known clear space. Pick a random cell
			if( x == 0 && y == 0)
			{
				x = rand.nextInt(m.columns());
				y = rand.nextInt(m.rows());
				//System.out.println("Guessing!");
			}
			//else
				//System.out.println("Not Guessing!");

			constraints[x][y].setValue(m.probe(x, y));
			//System.out.println(x + " " + y);
		}
	}

	public void CSPFindBestChoice(Map m)
	{
		int count = 0;
		//Reset the solutionSum table
		for(int i = 0; i < m.columns(); i++)
			for(int j = 0; j  < m.rows(); j++)
			{
				solutionSum[i][j] = 0;
				if(constraints[i][j].cellType == CSPCell.type.UNKNOWN && 
					constraints[i][j].hasRevealedNeighbours())
				{
					count++;
				}
			}
		totalSln = 0;
		System.out.println("Computing for: " + count);

		CSPRecurse(0,0,m,0);
	}

	private void CSPRecurse(int row, int col, Map m, int d)
	{
		//System.out.println(d + " " + row + ":" + col);
		boolean done = true;
		for(int i = 0; i < m.columns(); i++)
			for(int j = 0; j  < m.rows(); j++)
			{
				CSPCell.sat satis = constraints[i][j].isSatisfied();
				if(satis != CSPCell.sat.YES)
				{
					if(satis == CSPCell.sat.INVALID) return;//This cannot be a solution
					done = false;
					break;
				}
			}

		if(done)
		{
			//System.out.println("Solution found");
			for(int i = 0; i < m.columns(); i++)
				for(int j = 0; j  < m.rows(); j++)
				{
					if(constraints[i][j].cellType == CSPCell.type.CLEAR)
						solutionSum[i][j]++;
				}
			totalSln++;
			return;
		}
		
		if(++col >= m.columns())
		{
			col -= m.columns();
			if(++row >= m.rows()) return;//Reached the end of the board
		}

		if(constraints[col][row].cellType == CSPCell.type.UNKNOWN && 
			constraints[col][row].hasRevealedNeighbours())
		{
			
			constraints[col][row].cellType = CSPCell.type.CLEAR;
			CSPRecurse(row, col, m, d + 1);
			constraints[col][row].cellType = CSPCell.type.MINE;
			CSPRecurse(row, col, m, d + 1);
			constraints[col][row].cellType = CSPCell.type.UNKNOWN;
		}
		else CSPRecurse(row, col, m, d + 1);

	}
}
