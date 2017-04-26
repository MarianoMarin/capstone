package genAlg;

import java.util.ArrayList;
import java.util.Random;

public class Chromosome 
{
	private int length;
	private int[] alleles;
	public int fitness = 0;
	private Random rand = new Random();
	
	public Chromosome(int len)
	{
		length = len;
		alleles = new int[length];
	}
	
	public int getAllele(int index)
	{ return alleles[index]; }
	
	public void fixAllele(int index)
	{ alleles[index] = 1; }
	
	//to build initial chromosomes
	public void randomInit()
	{
		Random rand = new Random();
		for(int i = 0; i < length; i++)
			alleles[i] = rand.nextInt(2);
	}
	
	//to build one connected chromosome
	public void fullInit()
	{
		Random rand = new Random();
		for(int i = 0; i < length; i++)
		{
			alleles[i] = 1;
			if(rand.nextDouble() < 0.1)
				alleles[i] = 0;
		}
	}
	
	//to print out the chromosome if necessary
	public void display()
	{
		if(alleles.length < 30)
		{
			String chrom = "";
			for(int i = 0; i < alleles.length; i++)
				chrom += alleles[i] + " ";
			System.out.println(chrom + " : " + fitness);
		}
		else
			System.out.println("Fitness: " + fitness);
	}
	
	//get selected points to evaluate fitness
	public ArrayList<Integer> getSelected()
	{
		ArrayList<Integer> sel = new ArrayList<Integer>();
		for(int i = 0; i < alleles.length; i++)
			if(alleles[i] == 1)
				sel.add(i);
		return sel;
	}
	
	//set fitness level after being evaluated
	public void setFitness(int fit)
	{ fitness = fit; }
	
	//mutation: switching the value of a random allele
	public void pointMutate()
	{
		int chosen = rand.nextInt(alleles.length);
		alleles[chosen] = 1 - alleles[chosen];
	}
	
	//mutation: interchanging two random (but different) alleles
	public void slotMutate()
	{
		boolean different = false;
		int spot1 = rand.nextInt(alleles.length);
		int spot2 = rand.nextInt(alleles.length);
		int timeLimit = 0;
		while(!different)
		{
			if(alleles[spot1] != alleles[spot2])
				different = true;
			else if(timeLimit >= 100)
				different = true;
			else
			{
				timeLimit++;
				spot2 = rand.nextInt(alleles.length);
			}
		}
		int temp = alleles[spot1];
		alleles[spot1] = alleles[spot2];
		alleles[spot2] = temp;
	}
	
	//use uniform crossover to create self from parents
	public void uniformCross(Chromosome basis, Chromosome nonBase, int[] u)
	{
		for(int i = 0; i < u.length; i++)
		{
			if(u[i] == 0)
				alleles[i] = basis.getAllele(i);
			else
				alleles[i] = nonBase.getAllele(i);
		}
	}
	
	//double (or single) point crossover to create self from parents
	public void nPointCross(Chromosome basis, Chromosome nonBase)
	{
		int slicePt1 = rand.nextInt(length);
		int slicePt2 = rand.nextInt(length);
		if(slicePt1 > slicePt2)
		{
			for(int i = 0; i < length; i++)
				if(i < slicePt2 || i > slicePt1)
					alleles[i] = basis.getAllele(i);
				else
					alleles[i] = nonBase.getAllele(i);
		}
		else if(slicePt1 < slicePt2)
		{
			for(int i = 0; i < length; i++)
				if(i > slicePt2 || i < slicePt1)
					alleles[i] = basis.getAllele(i);
				else
					alleles[i] = nonBase.getAllele(i);
		}
		else
		{
			for(int i = 0; i < length; i++)
				if(i < slicePt1)
					alleles[i] = basis.getAllele(i);
				else
					alleles[i] = nonBase.getAllele(i);
		}
	}
}
