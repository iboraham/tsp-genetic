package pck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Genome {
	private List<Integer> genome;
	int[][] distance;
	int startingCity;
	int numberOfCities;
	private int fitness;
	
	public Genome(int numberOfCities, int[][] distance, int startingCity) {
	    this.distance = distance;
	    this.startingCity = startingCity;
	    this.numberOfCities = numberOfCities;

	    this.genome = randomResult();
	    this.setFitness(this.calculateFitness());
	}
	
	public Genome(List<Integer> permutationOfCities, int numberOfCities, int[][] distance, int startingCity) {
	    this.genome = permutationOfCities;
	    this.distance = distance;
	    this.startingCity = startingCity;
	    this.numberOfCities = numberOfCities;

	    this.setFitness(this.calculateFitness());
	    
	}
	
	private List<Integer> randomResult() {
	    List<Integer> result = new ArrayList<Integer>();
	    for (int i = 0; i < numberOfCities; i++) {
	        if (i != startingCity)
	            result.add(i);
	    }
	    Collections.shuffle(result);
	    return result;
	} 
	
	public int calculateFitness() {
	    int fitness = 0;
	    int currentCity = startingCity;
	    

	    for (int gene : genome) {
	        fitness += distance[currentCity][gene];
	        currentCity = gene;
	    }
	    

	    fitness += distance[genome.get(numberOfCities-2)][startingCity];
	    
	    return fitness;
	}

	public int getFitness() {
		return fitness;
	}

	public void setFitness(int fitness) {
		this.fitness = fitness;
	}
	public List<Integer> getGenome() {
		return genome;
	}

	public void SetGenome(List<Integer> genome) {
		this.genome = genome;
	}
}
