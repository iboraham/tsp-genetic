package pck;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;



public class GeneticAlg {
	
		static int populationSize=100;
		static int reproductionSize=20;
		static int startingCity=0; 
		static int maxIterations=500000;
		static float mutationRate=0.3f;
		static int maxStalling = 200000;
		


	public static void main(String[] args) throws IOException {
		
		long startTime = System.nanoTime();
		

		List<Genome> initial = new ArrayList<>();
		ArrayList<Integer> distanceList = new ArrayList<>();
		int totalCityNumber;
		
		System.out.println("Please give the number of cities of the problem that you want to solve:");
		Scanner sc = new Scanner(System.in);
		String str= sc.nextLine()+"_cities.txt";
		sc.close();
		
		distanceList=readTxt(str);
		totalCityNumber=(int) Math.sqrt(distanceList.size());
		int[][] distance = new int[totalCityNumber][totalCityNumber];
		distance=to2dArray(distanceList);
		
		int lowerBound=lowerBound(distance);
		
		for(int i=0; i<populationSize;i++) {
			initial.add(new Genome(totalCityNumber,distance,startingCity));
		}
		
		int initialBestFitness;
		Genome globalBestGenome = bestGenome(initial);
		initialBestFitness=globalBestGenome.getFitness();
		
		List<Genome> population = initial;
		
		int i=0;
		int j=0;
		float relativeError = (float) Math.abs(lowerBound-globalBestGenome.getFitness())/lowerBound;
	    
		while (  i<maxIterations && j<maxStalling) {

	        List<Genome> parentsForNextGen = selection(population);
	        
	        population = createGeneration(parentsForNextGen);

	        if(bestGenome(population).getFitness()<globalBestGenome.getFitness()) {
	        	globalBestGenome=bestGenome(population);
	        	j=0;
	        } else {
	        	j++;
	        }
	        
	        //System.out.println("Please wait! Iteration at ----> "+i+"\t At most, It will stop at--->"+maxIterations);
	        relativeError = (float) Math.abs(lowerBound-globalBestGenome.getFitness())/lowerBound;
	        i++;
	    }
	    
	    System.out.println("relative Error: "+relativeError );
	    System.out.println("Total Cost at initial was: "+initialBestFitness);
	    System.out.println("Total number of Iteration: "+i);
	    System.out.println("Lower Bound: "+lowerBound);
	    
	    System.out.println("Best route starting from city 0: \n"+globalBestGenome.getGenome().toString());
	    System.out.println("Total cost: "+globalBestGenome.getFitness());
	    System.out.println("Objective was stalling by "+j+" iteration");
	    
	    long endTime   = System.nanoTime();
	    long totalTime = endTime - startTime;

	    System.out.println("Runtime of the code: "+(float) totalTime/1000000000+"sec");

		
	}

	private static int lowerBound(int[][] distance) {
		int lowerBound=0;
		int[][] template=new int[distance.length][distance.length];
		List<Integer> temp=new ArrayList<>();
		int index;
		for(int i=0;i<distance.length;i++) {
			for(int j=0;j<distance.length;j++) {
				template[i][j]=distance[i][j]; }}
		
		for(int i=0;i<distance.length;i++) {
			for(int j=0;j<distance.length;j++) {				
				if(template[i][j] == 0) {
					template[i][j]=99999;
				}
				temp.add(template[i][j]);
			}
			lowerBound=min(temp)+lowerBound;
			index = temp.indexOf(Collections.min(temp));
			template[i][index] = 99999;
			template[index][i] = 99999;
			temp.clear();
		}
		return lowerBound;
	}
	
	private static int mean(List<Integer> temp) {
		int result=0;
		int total=0;
		for(int i=0;i<temp.size();i++) {
			total=temp.get(i);
		}
		
		result=total/temp.size();
		return result;
	}


	private static int min(List<Integer> temp) {
		int result=999999;
		for(int i=0;i<temp.size();i++) {
			if(result>temp.get(i)) {
				result=temp.get(i);
			}
		}
		return result;
	}

	private static ArrayList<Integer> readTxt(String string) throws IOException {
		File file = new File(string);
		Scanner sc = new Scanner(file);
		ArrayList<Integer> distance = new ArrayList<>();
		while(sc.hasNext()) {
			distance.add(sc.nextInt());
		}
		sc.close();
		return distance;
	}
	
	private static int[][] to2dArray(ArrayList<Integer> arrayList) {
        ArrayList<Integer> temp =new ArrayList<>();
        int size = (int) Math.sqrt(arrayList.size());
        int[][] array = new int[size][size];
        int index = 0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++){
				index = (size)*i+j;
				temp.add(arrayList.get(index));
			}
			for (int x = 0; x < size; x++){
				array[i][x]=temp.get(x); }
			temp.clear();
		}
		return array;
	}
	
	public static Genome select_a_parent(List<Genome> population) {
	    int totalFitness = population.stream().map(Genome::getFitness).mapToInt(Integer::intValue).sum();
	    
	    
	    int i=0;
		DistributedRandomNumberGenerator drng = new DistributedRandomNumberGenerator();
		for (Genome genome : population) {
			drng.addNumber(i, (float) genome.getFitness()/totalFitness);
			i++;
		}
		
		Genome parent=population.get(drng.getDistributedRandomNumber());
		
		return parent;
		
	}
	
	public static List<Genome> selection(List<Genome> population) {
	    List<Genome> selectedParents = new ArrayList<>();
	    
	    for (int i=0; i < reproductionSize; i++) {
	    	selectedParents.add(select_a_parent(population));
	    }

	    return selectedParents;
	}
	
	public static List<Genome> crossover(List<Genome> parents) {
	    
		int genomeSize = parents.get(0).getGenome().size();
	    Random random = new Random();
	    int breakpoint = random.nextInt(genomeSize);
	    List<Genome> children = new ArrayList<>();

	    
	    List<Integer> parent1Genome = new ArrayList<>(parents.get(0).getGenome());
	    List<Integer> parent2Genome = new ArrayList<>(parents.get(1).getGenome());
	    
	    
	    for (int i = 0; i < breakpoint; i++) {
	        int newVal;
	        newVal = parent2Genome.get(i);
	        Collections.swap(parent1Genome, parent1Genome.indexOf(newVal), i);
	    }
	    int numberOfCities=parents.get(0).numberOfCities;
	    int[][] distance=parents.get(0).distance;
	    int startingCity=parents.get(0).startingCity;
	    children.add(new Genome(parent1Genome, numberOfCities, distance, startingCity));
	    parent1Genome = parents.get(0).getGenome(); 
	    
	    
	    for (int i = breakpoint; i < genomeSize; i++) {
	        int newVal = parent1Genome.get(i);
	        Collections.swap(parent2Genome, parent2Genome.indexOf(newVal), i);
	    }
	    children.add(new Genome(parent2Genome, numberOfCities, distance, startingCity));

	    return children;
	}
	
	public static Genome mutate(Genome salesman) {
		int genomeSize = salesman.getGenome().size();
		Random random = new Random();
	    float mutate = random.nextFloat();
	    int numberOfCities=salesman.numberOfCities;
	    int[][] distance=salesman.distance;
	    int startingCity=salesman.startingCity;
	    if (mutate < mutationRate) {
	        List<Integer> genome = salesman.getGenome();
	        Collections.swap(genome, random.nextInt(genomeSize), random.nextInt(genomeSize));
	        return new Genome(genome, numberOfCities, distance, startingCity);
	    }
	    return salesman;
	}
	
	public static List<Genome> createGeneration(List<Genome> parentPopulation) {
	    List<Genome> generation = new ArrayList<>();
	    int currentGenerationSize = parentPopulation.size();
	    generation.addAll(parentPopulation);
	    while (currentGenerationSize < populationSize) {
	        
	    	List<Genome> parents = RandomElement(parentPopulation, 2);
	        List<Genome> children = crossover(parents);
	        children.set(0, mutate(children.get(0)));
	        children.set(1, mutate(children.get(1)));
	        
	        generation.addAll(children);
	        currentGenerationSize += 2;
	    }
	    
	    return generation;
	}
	
	public static <E> List<E> RandomElement(List<E> list, int n) {
	    Random r = new Random();
	    int length = list.size();

	    if (length < n) return null;

	    for (int i = length - 1; i >= length - n; --i) {
	        Collections.swap(list, i , r.nextInt(i + 1));
	    }
	    return list.subList(length - n, length);
	}
	
	public static Genome bestGenome(List<Genome> population) {
		int size=population.size();
		int numberOfCities=population.get(0).numberOfCities;
		int[][] distance = population.get(0).distance;
		int startingCity = 0;
	    Genome best= new Genome(numberOfCities, distance, startingCity);
		for(int i=0;i<size;i++) {
			if(population.get(i).getFitness()<best.getFitness()) {
				best.setFitness(population.get(i).getFitness());
				best.SetGenome(population.get(i).getGenome());
			}
		}
	    return best;
	}

}
