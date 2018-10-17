import java.util.*;
import umontreal.ssj.rng.*;
import umontreal.ssj.randvar.*;
import umontreal.ssj.randvarmulti.*;
import cern.colt.matrix.*;
// import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
// import org.apache.commons.math3.random.JDKRandomGenerator;
// import org.apache.commons.math3.random.RandomGenerator;

public class Variations {
    private Configs cfgs;

    public Variations(Configs cfgs) {
        this.cfgs = cfgs;
    }

    /**
     * Random swap mutation operator.
     * 
     * @param individual
     */
    public void rnd_swap(double[] individual) {
        int idx1 = new Random().nextInt(this.cfgs.getDimension());
        int idx2 = new Random().nextInt(this.cfgs.getDimension());

        while (idx1 == idx2) {
            idx2 = new Random().nextInt(this.cfgs.getDimension());
        }

        double temp = individual[idx1];
        individual[idx1] = individual[idx2];
        individual[idx2] = temp;

    }

    /**
     * Uniform mutation operator.
     * 
     * @param individual
     */
    public void uniformMutation(double[] individual) {
        for (int i = 0; i < this.cfgs.getDimension(); i++) {
            individual[i] = new Random().nextDouble() * (this.cfgs.getUpperBound() - this.cfgs.getLowerBound())
                    + this.cfgs.getLowerBound();
        }
    }

    /**
     * Non-uniform mutation operator.
     * 
     * @param individual
     */
    public void nonUniformMutation(double[] individual) {
        for (int i = 0; i < this.cfgs.getDimension(); i++) {
            int counter = 0;
            double temp = individual[i];
            individual[i] = individual[i] + new Random().nextGaussian() * individual[this.cfgs.getDimension()];
            while ((individual[i] > 5 || individual[i] < -5) && counter < 5) {
                counter++;
                individual[i] = temp;
                individual[i] = individual[i] + new Random().nextGaussian() * individual[this.cfgs.getDimension()];
            }
        }
    }

    /**
     * Older version of non-uniform mutation operator(wrongly implemented).
     * 
     * @param individual
     */
    public void customizedMutation(double[] individual) {
        int[] mutationInds = new int[this.cfgs.getMutationSize()];
        for (int i = 0; i < this.cfgs.getMutationSize(); i++) {
            mutationInds[i] = new Random().nextInt(this.cfgs.getDimension());
            if (i != 0) {
                boolean overlapping = true;
                while (overlapping) {
                    overlapping = false;
                    for (int j = 0; j < i; j++) {
                        if (mutationInds[i] == mutationInds[j]) {
                            mutationInds[i] = new Random().nextInt(this.cfgs.getDimension());
                            overlapping = true;
                            break;
                        }
                    }
                }
            }
        }
        for (int mutationInd : mutationInds) {
            individual[mutationInd] = individual[mutationInd]
                    + new Random().nextGaussian() * individual[this.cfgs.getDimension()];
        }
    }

    /**
     * Self-adaptive uncorrelated mutation with single mutation step size.
     * 
     * @param individual
     */
    public void singleUncorrelatedMutation(double[] individual) {
        individual[this.cfgs.getDimension()] = individual[this.cfgs.getDimension()]
                        * Math.exp(this.cfgs.getSingleMutationLearningRate()
                        * this.cfgs.getSingleMutationCoefficient() * new Random().nextGaussian());
        if (individual[this.cfgs.getDimension()] < this.cfgs.getMutationStepSizeBound()) {
            individual[this.cfgs.getDimension()] = this.cfgs.getMutationStepSizeBound();
        }
        for (int i = 0; i < this.cfgs.getDimension(); i++) {
            int counter = 0;
            double temp = individual[i];
            individual[i] = individual[i] + individual[this.cfgs.getDimension()] * new Random().nextGaussian();
            while ((individual[i] > 5 || individual[i] < -5) && counter < 5) {
                counter++;
                individual[i] = temp;
                individual[i] = individual[i] + individual[this.cfgs.getDimension()] * new Random().nextGaussian();
            }
        }
    }

    /**
     * Self-adaptive uncorrelated mutation with multiple mutation step sizes.
     * 
     * @param individual
     */
    public void multiUncorrelatedMutation(double[] individual) {
        double overallNormalDist = new Random().nextGaussian();
        for (int i = 0; i < this.cfgs.getDimension(); i++) {
            int counter = 0;
            individual[this.cfgs.getDimension() + i] = individual[this.cfgs.getDimension() + i] * Math.exp(
                    this.cfgs.getMutationLearningRate() * this.cfgs.getOverallMutationCoefficient() * overallNormalDist
                            + this.cfgs.getSecondaryMutationLearningRate() * this.cfgs.getSecondaryMutationCoefficient()
                                    * new Random().nextGaussian());
            if (individual[this.cfgs.getDimension() + i] < this.cfgs.getMutationStepSizeBound()) {
                individual[this.cfgs.getDimension() + i] = this.cfgs.getMutationStepSizeBound();
            }
            double temp = individual[i];
            individual[i] = individual[i] + individual[this.cfgs.getDimension() + i] * new Random().nextGaussian();
            while ((individual[i] > 5 || individual[i] < -5) && counter < 5) {
                counter++;
                individual[i] = temp;
                individual[i] = individual[i] + individual[this.cfgs.getDimension() + i] * new Random().nextGaussian();
            }
        }
    }

    public void correlatedMutation(double[] individual) {
        double overallNormalDist = new Random().nextGaussian();
        double[] means = new double[this.cfgs.getDimension()];

        for (int i = 0; i < this.cfgs.getDimension(); i++) {
            individual[this.cfgs.getDimension() + i] = individual[this.cfgs.getDimension() + i] * Math.exp(
                    this.cfgs.getMutationLearningRate() * this.cfgs.getOverallMutationCoefficient() * overallNormalDist
                            + this.cfgs.getSecondaryMutationLearningRate() * this.cfgs.getSecondaryMutationCoefficient()
                                    * new Random().nextGaussian());
            if (individual[this.cfgs.getDimension() + i] < this.cfgs.getMutationStepSizeBound()) {
                individual[this.cfgs.getDimension() + i] = this.cfgs.getMutationStepSizeBound();
            }
        }

        for (int i = 0; i < this.cfgs.getDimension() * (this.cfgs.getDimension() - 1) / 2; i++) {
            individual[this.cfgs.getDimension() + 10 + i] = individual[this.cfgs.getDimension() + 10 + i] 
                    + this.cfgs.getCorrelationAngle() * new Random().nextGaussian();
            if (Math.abs(individual[this.cfgs.getDimension() + 10 + i]) > Math.PI) {
                individual[this.cfgs.getDimension() + 10 + i] = individual[this.cfgs.getDimension() + 10 + i]
                        - 2 * Math.PI * Math.signum(individual[this.cfgs.getDimension() + 10 + i]);
            }
        }

        this.cfgs.setCovarianceMatrix(Arrays.copyOfRange(individual, this.cfgs.getDimension(), this.cfgs.getDimension() + 10), 
                Arrays.copyOfRange(individual, this.cfgs.getDimension() + 10, this.cfgs.getDimension() + 10 + 
                        this.cfgs.getDimension() * (this.cfgs.getDimension() - 1) / 2));
        RandomStream stream = new MRG31k3p();
        // RandomStream stream = new MRG32k3a();
        // RandomStream stream = new LFSR113();
        NormalGen generator1 = new NormalGen(stream);
        MultinormalPCAGen generator2 = new MultinormalPCAGen(generator1, means, this.cfgs.getCovarienceMatrix());
        double[] tmp = new double[this.cfgs.getDimension()];
        generator2.nextPoint(tmp);
        for (int i = 0; i < this.cfgs.getDimension(); i++) {
            int counter = 0;
            double temp = individual[i];
            individual[i] = individual[i] + tmp[i];
            while ((individual[i] > 5 || individual[i] < -5) && counter < 5) {
                counter++;
                individual[i] = temp;
                generator2.nextPoint(tmp);
                individual[i] = individual[i] + tmp[i];
            }
        }
    }

    /**
     * Single arithmetic Crossover operator.
     * 
     * @param population
     * @param parent1
     * @param parent2
     * @return Renewed population with the children.
     */
    public double[][] singleArithmeticCrossOver(double[][] population, double[] parent1, double[] parent2) {
        double[][] renewedPopulation = new double[population.length + 2][population[0].length];
        double[] child1 = new double[population[0].length];
        double[] child2 = new double[population[0].length];

        int k = new Random().nextInt(this.cfgs.getDimension());
        for (int i = 0; i < k; i++) {
            child1[i] = parent1[i];
            child2[i] = parent2[i];
        }

        child1[k] = this.cfgs.getMixingFactor() * parent2[k] + (1 - this.cfgs.getMixingFactor()) * parent1[k];
        child2[k] = this.cfgs.getMixingFactor() * parent1[k] + (1 - this.cfgs.getMixingFactor()) * parent2[k];

        for (int i = k + 1; i < this.cfgs.getDimension(); i++) {
            child1[i] = parent1[i];
            child2[i] = parent2[i];
        }
        
        for (int i = this.cfgs.getDimension(); i < this.cfgs.getDimension() + 10; i++) {
            child1[i] = this.cfgs.getInitialStepSize();
            child2[i] = this.cfgs.getInitialStepSize();
        }

        for (int i = 0; i < population.length; i++) {
            renewedPopulation[i] = population[i];
        }
        renewedPopulation[population.length] = child1;
        renewedPopulation[population.length + 1] = child2;

        return renewedPopulation;
    }

    /**
     * Simple arithmetic Crossover operator.
     * 
     * @param population
     * @param parent1
     * @param parent2
     * @return Renewed population with the children.
     */
    public double[][] simpleArithmeticCrossOver(double[][] population, double[] parent1, double[] parent2) {
        double[][] renewedPopulation = new double[population.length + 2][population[0].length];
        double[] child1 = new double[population[0].length];
        double[] child2 = new double[population[0].length];

        int k = new Random().nextInt(this.cfgs.getDimension());
        for (int i = 0; i < k; i++) {
            child1[i] = parent1[i];
            child2[i] = parent2[i];
        }

        for (int i = k; i < this.cfgs.getDimension(); i++) {
            child1[i] = this.cfgs.getMixingFactor() * parent2[i] + (1 - this.cfgs.getMixingFactor()) * parent1[i];
            child2[i] = this.cfgs.getMixingFactor() * parent1[i] + (1 - this.cfgs.getMixingFactor()) * parent2[i];
        }
        
        for (int i = this.cfgs.getDimension(); i < this.cfgs.getDimension() + 10; i++) {
            child1[i] = this.cfgs.getInitialStepSize();
            child2[i] = this.cfgs.getInitialStepSize();
        }

        for (int i = 0; i < population.length; i++) {
            renewedPopulation[i] = population[i];
        }
        renewedPopulation[population.length] = child1;
        renewedPopulation[population.length + 1] = child2;

        return renewedPopulation;
    }

    /**
     * Whole arithmetic Crossover operator.
     * 
     * @param population
     * @param parent1
     * @param parent2
     * @return Renewed population with the children.
     */
    public double[][] wholeArithmeticCrossOver(double[][] population, double[] parent1, double[] parent2) {
        double[][] renewedPopulation = new double[population.length + 2][population[0].length];
        double[] child1 = new double[population[0].length];
        double[] child2 = new double[population[0].length];

        for (int i = 0; i < this.cfgs.getDimension(); i++) {
            child1[i] = this.cfgs.getMixingFactor() * parent2[i] + (1 - this.cfgs.getMixingFactor()) * parent1[i];
            child2[i] = this.cfgs.getMixingFactor() * parent1[i] + (1 - this.cfgs.getMixingFactor()) * parent2[i];
        }
        
        for (int i = this.cfgs.getDimension(); i < this.cfgs.getDimension() + 10; i++) {
            child1[i] = this.cfgs.getInitialStepSize();
            child2[i] = this.cfgs.getInitialStepSize();
        }

        for (int i = 0; i < population.length; i++) {
            renewedPopulation[i] = population[i];
        }
        renewedPopulation[population.length] = child1;
        renewedPopulation[population.length + 1] = child2;

        return renewedPopulation;
    }

    /**
     * Blend Crossover operator.
     * 
     * @param population
     * @param parent1
     * @param parent2
     * @return Renewed population with the children.
     */
    public double[][] blendCrossOver(double[][] population, double[] parent1, double[] parent2) {
        double[][] renewedPopulation = new double[population.length + 2][population[0].length];
        double[] child1 = new double[population[0].length];
        double[] child2 = new double[population[0].length];

        for (int i = 0; i < this.cfgs.getDimension(); i++) {
            double d = Math.abs(parent1[i] - parent2[i]);
            child1[i] = (Math.min(parent1[i], parent2[i]) - this.cfgs.getMixingFactor() * d)
                    + new Random().nextDouble() * ((Math.min(parent1[i], parent2[i]) + this.cfgs.getMixingFactor() * d)
                            - (Math.min(parent1[i], parent2[i]) - this.cfgs.getMixingFactor() * d));

            child2[i] = (Math.min(parent1[i], parent2[i]) - this.cfgs.getMixingFactor() * d)
                    + new Random().nextDouble() * ((Math.min(parent1[i], parent2[i]) + this.cfgs.getMixingFactor() * d)
                            - (Math.min(parent1[i], parent2[i]) - this.cfgs.getMixingFactor() * d));
        }
        
        for (int i = this.cfgs.getDimension(); i < this.cfgs.getDimension() + 10; i++) {
            child1[i] = this.cfgs.getInitialStepSize();
            child2[i] = this.cfgs.getInitialStepSize();
        }

        for (int i = 0; i < population.length; i++) {
            renewedPopulation[i] = population[i];
        }
        renewedPopulation[population.length] = child1;
        renewedPopulation[population.length + 1] = child2;

        return renewedPopulation;
    }

    /**
     * Order-one Crossover operator.
     * 
     * @param population
     * @param parent1
     * @param parent2
     * @return Renewed population with the children.
     */
    public double[][] order1CrossOver(double[][] population, double[] parent1, double[] parent2) {
        double[][] renewedPopulation = new double[population.length + 2][population[0].length];
        double[] child1 = new double[population[0].length];
        double[] child2 = new double[population[0].length];

        int startInd = new Random().nextInt(this.cfgs.getDimension());
        int endInd = new Random().nextInt(this.cfgs.getDimension() - startInd) + startInd;

        for (int i = 0; i < startInd; i++) {
            child1[i] = parent2[i];
            child2[i] = parent1[i];
        }

        for (int i = startInd; i < endInd; i++) {
            child1[i] = parent1[i];
            child2[i] = parent2[i];
        }

        for (int i = endInd; i < this.cfgs.getDimension(); i++) {
            child1[i] = parent2[i];
            child2[i] = parent1[i];
        }
        
        for (int i = this.cfgs.getDimension(); i < this.cfgs.getDimension() + 10; i++) {
            child1[i] = this.cfgs.getInitialStepSize();
            child2[i] = this.cfgs.getInitialStepSize();
        }

        for (int i = 0; i < population.length; i++) {
            renewedPopulation[i] = population[i];
        }
        renewedPopulation[population.length] = child1;
        renewedPopulation[population.length + 1] = child2;
        return renewedPopulation;
    }

}
