package eqn;

import map.*;
import set.*;
import java.util.Random;

public final class CSPStrategy implements Strategy
{
	public void play(Map m)
	{
		Random rand = new Random();
		while(!m.done())
		{
			int x = rand.nextInt(m.rows());
			int y = rand.nextInt(m.columns());
			m.probe(x, y);
		}
	}
}
