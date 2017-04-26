package genAlg;

public class Point 
{
	public int x;
	public int y;
	public int index;
	
	public Point(int xin, int yin, int ind)
	{
		x = xin;
		y = yin;
		index = ind;
	}
	
	public Point()
	{
		x = -1;
		y = -1;
		index = -1;
	}
	
	//just in case, probably won't be used
	public void setNewX(int newx)
	{ x = newx; }
	public void setNewY(int newy)
	{ y = newy; }
	
	//returns location as [x, y]
	public int[] getLocation()
	{ return new int[] {x, y}; }
}
