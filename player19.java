import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.util.*;

public class player19 implements ContestSubmission {
    Random rnd_;
    ContestEvaluation evaluation_;
    private int evaluations_limit_;

    public static int evals;

    public player19() {
        rnd_ = new Random();
    }

    public void setSeed(long seed) {
        // Set seed of algortihms random process
        rnd_.setSeed(seed);
    }

    public static void resetEvals() {
        evals = 0;
    }

    public void setEvaluation(ContestEvaluation evaluation) {
        // Set evaluation problem used in the run
        evaluation_ = evaluation;

        // Get evaluation properties
        Properties props = evaluation.getProperties();
        // Get evaluation limit
        evaluations_limit_ = Integer.parseInt(props.getProperty("Evaluations"));
        // Property keys depend on specific evaluation
        // E.g. double param = Double.parseDouble(props.getProperty("property_name"));
        boolean isMultimodal = Boolean.parseBoolean(props.getProperty("Multimodal"));
        boolean hasStructure = Boolean.parseBoolean(props.getProperty("Regular"));
        boolean isSeparable = Boolean.parseBoolean(props.getProperty("Separable"));

        // Do sth with property values, e.g. specify relevant settings of your algorithm
        if (isMultimodal) {
            // Do sth
        } else {
            // Do sth else
        }
    }

    public void run() {
        // Run your algorithm here
        Configs Cfgs = new Configs();
        Initializations Inits = new Initializations(Cfgs, evaluation_);
        Variations Vars = new Variations(Cfgs);
        Selections Sels = new Selections(Cfgs, evaluation_);

        // init population
        // calculate fitness
        double[][] population;
        population = Inits.initPopulation(Initializations.RandomDistributions.NORMAL);
        resetEvals();
        Inits.updateFitness(population);
        while (evals < evaluations_limit_) {
            if (evals % Cfgs.getpopulationSize() == 0) {
                System.out.println("Best fitness value at evaluation " + Integer.toString(evals) + ": "
                        + Double.toString(Inits.maxScore));
            }
            // Select parents
            Sels.sortbyColumn(population, Cfgs.getdimension());
            int[] parentsInd = Sels.parentSelection_Elitism(population, Initializations.RandomDistributions.NORMAL);
            // Apply crossover
            for (int i = 0; i < Cfgs.getparentSelected(); i = i + 2) {
                // population = Vars.order1CrossOver(population, population[parentsInd[i]],
                // population[parentsInd[i + 1]],
                // Inits);
                population = Vars.singleArithmeticCrossOver(population, population[parentsInd[i]],
                        population[parentsInd[i + 1]]);
            }
            // Apply mutation
            for (int i = Cfgs.getpopulationSize(); i < Cfgs.getpopulationSize() + Cfgs.getparentSelected(); i++) {
                if (new Random().nextInt((int) (1 / Cfgs.getmutationRate())) == 0) {
                    Vars.rnd_swap(population[i]);
                }
            }
            // Check fitness of unknown fuction
            for (int i = 0; i < Cfgs.getparentSelected(); i++) {
                double[] tempPop = Arrays.copyOfRange(population[Cfgs.getpopulationSize() + i], 0, Cfgs.getdimension());
                double tempEval = (double) evaluation_.evaluate(tempPop);
                population[Cfgs.getpopulationSize() + i][Cfgs.getdimension()] = tempEval;
                if (Cfgs.getDEBUG()) {
                    if (tempEval >= Inits.maxScore) {
                        Inits.maxScore = tempEval;
                    }
                }
                evals++;
            }
            // Select survivors
            population = Sels.survSelection_Elitism(population);
        }
        System.out.println(
                "Best fitness value at evaluation " + Integer.toString(evals) + ": " + Double.toString(Inits.maxScore));

    }

}

// For backup:

// double[][] mystring = new double[][] { // just an example matrix
// { 2.0, 3.2, 2.0 }, { 1.0, 4.2, 1.0 }, { 9.2, 5.2, 9.2 }, {3.7, 2.9, 3.3} };
// System.out.println("Original:" + Arrays.deepToString(mystring));// print the
// original array
// double[][] newstring = Sels.survSelection(mystring, 2); //keep only the best
// 2 rows of 'mystring' based on the last column
// System.out.println("New (keeps best 2):" + Arrays.deepToString(newstring));
// //check if it works

// // Test arr
// double[] arr = {1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0};

// // Functions takes arr and swaps 2 floats
// Vars.rnd_swap(arr);

// // check if swapped
// System.out.println(Arrays.toString(arr));