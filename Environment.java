package genAlg;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Environment 
{
	private static int POP_SIZE = 10;
	private static final int POP_MAX = 15;
	private static final int POP_MIN = 5;
	private static final int NUM_GENERATIONS = 10000;
	private static final double MUTATION_RATE = 0.075;
	private static int NUM_POINTS;
	private static int RADIUS;
	private static Point[] data;
	private static Random rand = new Random();
	
	public Environment()
	{ }
	
	//for debugging purposes, to see what data are being generated
	public static void printData(Point[] data)
	{
		System.out.println(NUM_POINTS);
		for(int i = 0; i<data.length; i++)
			System.out.println(i + ": " +data[i].x + ", " + data[i].y);
	}
	
	//Euclidean radius check; is dataPt covered by selected (does [selected] cover [dataPt]?)
	private boolean isCovered(Point dataPt , Point selected)
	{
		int dx = dataPt.x; int dy = dataPt.y;
		int sx = selected.x; int sy = selected.y;
		int distance = (int)Math.sqrt((dx - sx)*(dx - sx) + (dy - sy)*(dy - sy));
		return (distance <= RADIUS);
	}
	
	//make chromosome feasible w/ respect to P'
	private int valid(Chromosome chrom)
	{
		ArrayList<Integer> spots = chrom.getSelected();
		Point[] subset = new Point[spots.size()];
		for(int i = 0; i < subset.length; i++)
			subset[i] = data[spots.get(i)];
		ArrayList<Point> pZeroSet = new ArrayList<Point>();
		ArrayList<Point> missing = new ArrayList<Point>();
		int oldSize = 0; int newSize = 0;
		int toCheck = 0;
		
		spots = chrom.getSelected();
		subset = new Point[spots.size()];
		for(int i = 0; i < subset.length; i++)
			subset[i] = data[spots.get(i)];
		//--if P'[0] connected to each P', FIXED:
		//establish new set {P'[0]}, all P' connected directly to P'[0]
		pZeroSet.clear();
		missing.clear();
		oldSize = 0; newSize = 0;
		pZeroSet.add(subset[0]);
		for(int i = 1; i < subset.length; i++)
			if(isCovered(subset[0], subset[i]))
				pZeroSet.add(subset[i]);
		newSize = pZeroSet.size();
		
		//while(new size > old size)
		while(newSize > oldSize)
		{
			//find points to be checked defined by: [length - (new size - old size)] -> length
			toCheck = pZeroSet.size() - (newSize - oldSize);
			oldSize = pZeroSet.size();
			//add (P' connected to newly checked points) to {P'[0]}
			for(int i = toCheck; i < pZeroSet.size(); i++)
				for(Point p : subset)
					if(!pZeroSet.contains(p) && isCovered(p, pZeroSet.get(i)))
						pZeroSet.add(p);
			//record current size as new size
			newSize = pZeroSet.size();
		}
		
		//if({P'[0]} == P') FIXED (assume fixed, try to prove wrong)
		for(Point p : subset)
		{
			if(!pZeroSet.contains(p))
			{
				missing.add(p);
			}
		}
		return missing.size();
	}
	
	//fitness function for a given chromosome
	private int fitness(Chromosome chrom)
	{
		//get array of chrom's selected points
		ArrayList<Integer> spots = chrom.getSelected();
		int numSelected = spots.size();
		Point[] selected = new Point[numSelected];
		for(int i = 0; i < selected.length; i++)
			selected[i] = data[spots.get(i)];
		
		//determine number of points not covered by chrom
		int notCovered = 0;
		boolean done = false;
		for(int i = 0; i < data.length; i++)
		{
			done = false;
			if(spots.contains(data[i].index))
				continue;
			else
			{
				for(int j = 0; j < selected.length; j++)
					if(isCovered(data[i], selected[j]))
						done = true;
				if(!done)
					notCovered++;
			}
		}
		
		//fitness function!! and return (smaller fitness number is better)
		int result = 13*valid(chrom) + 4*notCovered + numSelected;
		return result;
	}
	
	//roulette selection method
	private static Chromosome rouletteSelection(double[] wheel, ArrayList<Chromosome> population)
	{
		Chromosome parent = new Chromosome(NUM_POINTS);
		double wheelBall;
		boolean parentFound = false;
		int wheelSpot = 0;
		
		wheelBall = Math.random();
		//begin roulette selection
		//find parent1
		while(!parentFound)
		{
			if(wheelBall <= wheel[wheelSpot])
			{
				parent = population.get(wheelSpot);
				parentFound = true;
			}
			else
			{
				wheelBall -= wheel[wheelSpot];
				wheelSpot++;
			}
		}
		
		return parent;
	}
	
	//tournament selection method
	private static Chromosome tournamentSelection(Chromosome[] population)
	{
		int index1 = rand.nextInt(population.length);
		Chromosome cand1 = population[index1];
		int index2 = rand.nextInt(population.length);
		double factor = rand.nextDouble();
		Chromosome cand2 = population[index2];
		if(cand1.fitness > cand2.fitness)
			if(factor < 0.75)
				return cand1;
			else
				return cand2;
		else if(cand1.fitness < cand2.fitness)
			if(factor < 0.75)
				return cand2;
			else
				return cand1;
		else
			if(factor < 0.5)
				return cand1;
			else
				return cand2;
	}
	
	//BEGIN GENETIC ALGORITHM PROCESS
	public static void main(String [] args)
	{
		ArrayList<Chromosome> population = new ArrayList<Chromosome>();
		Environment runSpace = new Environment();
		//read points into data array
		try
		{ 
			Scanner datascan = new Scanner(new File("C:/Users/Mariano/workspace/EvComp/src/genAlg/dataset.txt"));
			NUM_POINTS = datascan.nextInt();
			RADIUS = datascan.nextInt();
			data = new Point[NUM_POINTS];
			for(int i = 0; i < data.length; i++)
			{
				if(datascan.hasNextLine())
					data[i] = new Point(datascan.nextInt(), datascan.nextInt(), i);
				else
					data[i] = new Point(-1, -1, i);
			}
			datascan.close();
		}
		//in case file doesn't work
		catch(FileNotFoundException e)
		{ 
			System.out.println("data file not found.");
			data = new Point[1];
			data[0] = new Point(-1, -1, 0);
		}
		
		System.out.println("File read-in complete.");
		
		//randomly create chromosomes
		for(int i = 0; i < POP_SIZE; i++)
		{
			population.add(new Chromosome(NUM_POINTS));
			population.get(i).randomInit();
		}
		
		System.out.println("initial population created.");
		
		//assign fitness to initial chromosomes
		Chromosome best = new Chromosome(NUM_POINTS);
		best = population.get(0);
		for(Chromosome c : population)
		{
			c.setFitness(runSpace.fitness(c));
			if(c.fitness < best.fitness)
				best = c;
			c.display();
		}
		System.out.println("End initial population");
		
		//declare per generation variables
		int totalFitness = 0;
		int prevBestFit = best.fitness;
		int prevProgress = 0;
		int stuckCount = 0;
		int bestGen = 0;
		double[] wheel = new double[POP_SIZE];
		Chromosome parent1 = new Chromosome(NUM_POINTS);
		Chromosome parent2 = new Chromosome(NUM_POINTS);
		ArrayList<Chromosome> children = new ArrayList<Chromosome>();
		int[] u = new int[NUM_POINTS];
		
		//begin generational code
		int g = 0;
		int convergeCount = 0;
		int bestFit = best.fitness;
		while(g < NUM_GENERATIONS && convergeCount < 7000)
		{
			//prepare double[] wheel for roulette selection
			totalFitness = 0;
			wheel = new double[POP_SIZE];
			for(Chromosome c : population)
				totalFitness += c.fitness;
			for(int i = 0; i < POP_SIZE; i++)
				wheel[i] = totalFitness - population.get(i).fitness;
			totalFitness = 0;
			for(int i = 0; i < POP_SIZE; i++)
				totalFitness += wheel[i];
			for(int i = 0; i < POP_SIZE; i++)
				wheel[i] = (double)wheel[i]/(double)totalFitness;
			
			//elitism at work - find current best
			best = population.get(0);
			for(int i = 1; i < population.size(); i++)
				if(population.get(i).fitness < best.fitness)
				{
					best = population.get(i);
					if(best.fitness < bestFit)
					{
						convergeCount = 0;
						bestGen = g;
					}
				}
			
			//adjust population size based on current progress level
			if(g > 0 && g%10 == 0)
			{
			   if(best.fitness < prevBestFit)
			   {
			      if ((prevBestFit - best.fitness) >= prevProgress)
			      {
			         if(POP_SIZE > POP_MIN)
			            POP_SIZE--;
			      }
			      stuckCount = 0;
			      prevProgress = (prevBestFit - best.fitness);
			   }
			   else
			   {
			      if(stuckCount >= 10)
			      {
			         if(POP_SIZE < POP_MAX)
			            POP_SIZE++;
			      }
               else
                  stuckCount++;
			      prevProgress = 0;
			   }
			   prevBestFit = best.fitness;
			   System.out.println("New POP_SIZE is: " + POP_SIZE);
			}
			
			children.clear();
			//create child pool
			for(int s = 0; s < POP_SIZE; s+=2)
			{
				//pick parents (selection)
				parent1 = rouletteSelection(wheel, population);
				parent2 = rouletteSelection(wheel, population);
				
				//make 2 children (crossover)
				for(int i = 0; i < NUM_POINTS; i++)
					u[i] = rand.nextInt(2);
				children.add(s, new Chromosome(NUM_POINTS));
				children.add(s+1, new Chromosome(NUM_POINTS));
				children.get(s).uniformCross(parent1, parent2, u);
				children.get(s+1).uniformCross(parent2, parent1, u);
				
				//mutation (not always)
				if(Math.random() < MUTATION_RATE)
					children.get(s).slotMutate();
				if(Math.random() < MUTATION_RATE)
					children.get(s+1).slotMutate();			
			}
			for(Chromosome c : children)
			   c.setFitness(runSpace.fitness(c));
			//elitism
			//this replaces the worst chromosome with the best one yet (overall)
			int worstspot = 0;
			for(int i = 0; i<children.size(); i++)
			{
				if(children.get(i).fitness > children.get(worstspot).fitness)
					worstspot = i;
			}
			children.add(worstspot, best);
			children.remove(worstspot+1);
			bestFit = best.fitness;
			System.out.println("End Generation " + g);
			best.display();
			population.clear();
			for(Chromosome c : children)
			{
			   population.add(c);
			}
			
			g++; convergeCount++;
		}
		System.out.println("Best fitness first achieved at gen: " + bestGen);
	}
}
