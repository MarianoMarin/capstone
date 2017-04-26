package datamaker;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;

public class Generator 
{
	public static Random rand = new Random();
	
	//dimensions and data population info
	
	//a 500x500 space with 250 points
	//selected points broadcast with radius 50
	public static final int MEDIUM_SPACE = 250;
	public static final int MEDIUM_SIZE = 250;
	public static final int MEDIUM_RADIUS = 30;
	
	//a 2000x2000 space with 1000 points
	//selected points broadcast with radius 100
	public static final int LARGE_SPACE = 1000;
	public static final int LARGE_SIZE = 1000;
	public static final int LARGE_RADIUS = 60;
	
	public static void main(String[] args)
	{
		
		try {
			PrintWriter writer = new PrintWriter("medium_dataset.txt", "UTF-8");
			writer.println("" + MEDIUM_SIZE);
			writer.println("" + MEDIUM_RADIUS);
			int x = 0; int y = 0;
			for(int i = 0; i<MEDIUM_SIZE; i++)
			{
				x = rand.nextInt(MEDIUM_SPACE);
				y = rand.nextInt(MEDIUM_SPACE);
				writer.println(x + " " + y);
			}
			writer.close();
			writer = new PrintWriter("large_dataset.txt", "UTF-8");
			writer.println("" + LARGE_SIZE);
			writer.println("" + LARGE_RADIUS);
			for(int i = 0; i<LARGE_SIZE; i++)
			{
				x = rand.nextInt(LARGE_SPACE);
				y = rand.nextInt(LARGE_SPACE);
				writer.println(x + " " + y);
			}
			
			writer.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("Problem creating file");
		} catch (UnsupportedEncodingException e) {
			System.out.println("Problem writing file");
		}
	}
}
