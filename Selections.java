import java.util.*;
import org.vu.contest.ContestEvaluation;

public class Selections {
    private Configs cfgs;

    public Selections(Configs cfgs) {
        this.cfgs = cfgs;
    }

    /**
     * Sort the population by fitness value at the 11th dimension.
     * 
     * @param population
     * @param colToSort  Index of column to sort on.
     */
    public void sortbyColumn(double population[][], int colToSort) {
        // Using built-in sort function Arrays.sort
        Arrays.sort(population, new Comparator<double[]>() {

            @Override
            // Compare values according to columns
            public int compare(final double[] entry1, final double[] entry2) {

                // To sort in descending order
                Double fitness1 = new Double(entry1[colToSort]);
                Double fitness2 = new Double(entry2[colToSort]);
                return fitness2.compareTo(fitness1);
            }
        });
    }

    /**
     * Roulette Wheel Selection.
     * 
     * @param fitnessValues Array of fitness values corresponding to the population.
     * @param rand          Specified random distribution.
     * @return Index of the selected individual.
     */

    public int rouletteWheelSelection(double[] fitnessValues) {
        // Sum up the fitnessValues of all the selectees.
        double fitnessSum = 0;
        for (double fitness : fitnessValues) {
            fitnessSum += fitness;
        }

        // Compute the corresponding probabilities.
        double[] probs = new double[fitnessValues.length];
        for (int i = 0; i < probs.length; i++) {
            probs[i] = fitnessValues[i] / fitnessSum;
        }

        // Init a random probability and return its corresponding index.
        double prob = 0;
        prob = new Random().nextDouble();

        double probSum = 0;
        int i = 0;
        while (prob - probSum > 0) {
            probSum += probs[i];
            i++;
        }
        return i - 1;
    }

    /**
     * Parent selection based on Elitism.
     * 
     * @param population
     * @param rand       Specified random distribution.
     * @return Indices of the selected parents.
     */
    public int[] parentSelection_Elitism(double[][] population, Initializations.RandomDistributions rand) {
        // Extract the fitness values per individuals in the population.
        double[] fitnessValues = new double[population.length];
        for (int i = 0; i < population.length; i++) {
            fitnessValues[i] = population[i][this.cfgs.getDimension()];
        }

        // Use roulette wheel selection to pick (randomSize) of individuals out of the
        // population and select the best (intensionSize) of them.
        int[] chosenInd = new int[this.cfgs.getRandomSelected()];
        for (int i = 0; i < this.cfgs.getRandomSelected(); i++) {
            // chosenInd[i] = rouletteWheelSelection(fitnessValues);
            chosenInd[i] = new Random().nextInt(population.length);
            // boolean overlapping = true;
            // while (overlapping) {
            // overlapping = false;
            // for (int j = 0; j < i; j++) {
            // if (chosenInd[i] == chosenInd[j]) {
            // // chosenInd[i] = rouletteWheelSelection(fitnessValues);
            // chosenInd[i] = new Random().nextInt(population.length);
            // overlapping = true;
            // break;
            // }
            // }
            // }
        }
        Arrays.sort(chosenInd);
        int[] parentsInd = new int[this.cfgs.getParentSelected()];
        for (int i = 0; i < this.cfgs.getParentSelected(); i++) {
            parentsInd[i] = chosenInd[i];
        }
        return parentsInd;
    }

    /**
     * Parent selection based on Rank.
     * 
     * @param population
     * @return Indices of the selected parents.
     */
    public int[] parentSelection_RankedBased(double[][] population) {
        int[] parentsInd = new int[this.cfgs.getParentSelected()];
        // Compute the corresponding probabilities.
        double[] probs = new double[population.length];
        for (int i = 0; i < probs.length; i++) {
            probs[i] = (2 - this.cfgs.getS_value()) / population.length
                    + (2 * (population.length - 1 - i) * (this.cfgs.getS_value() - 1))
                            / (population.length * (population.length - 1));
        }
        int selectedNr = 0;
        while (selectedNr < this.cfgs.getParentSelected()) {
            double prob = 0;
            prob = new Random().nextDouble();
            double probSum = 0;
            int i = 0;
            while (prob - probSum > 0) {
                probSum += probs[i];
                i++;
            }
            parentsInd[selectedNr] = i - 1;
            selectedNr++;
        }
        return parentsInd;
    }

    /**
     * Survivor selection based on Elitism.
     * 
     * @param population
     * @return Purged population.
     */
    public double[][] survSelection_Elitism(double[][] population) {
        sortbyColumn(population, this.cfgs.getDimension()); // sort the current matrix of the population based on the
        // fitness stored at the last column
        double[][] new_population = new double[this.cfgs.getPopulationSize()][population[0].length]; // create a new
                                                                                                     // matrix
        // for storing
        // only the top numbers of
        // individuals
        for (int i = 0; i < this.cfgs.getPopulationSize(); i++) {
            for (int j = 0; j < population[0].length; j++) {
                new_population[i][j] = population[i][j]; // copy from the old matrix to the new matrix
            }
        }
        return new_population;
    }
}
