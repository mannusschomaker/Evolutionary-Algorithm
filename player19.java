import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.util.Random;
import java.util.Properties;
import java.util.Arrays;

public class player19 implements ContestSubmission {
    Random rnd_;
    ContestEvaluation evaluation_;
    private int evaluations_limit_;

    public player19() {
        rnd_ = new Random();
    }

    public void setSeed(long seed) {
        // Set seed of algortihms random process
        rnd_.setSeed(seed);
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
        Initializations Inits = new Initializations();
        Inits.test_print();

        Variations Vars = new Variations();
        Vars.test_print();

        Selections Sels = new Selections();
        Sels.test_print();

        int evals = 0;
        // init population
        // calculate fitness
        while (evals < evaluations_limit_) {
            // Select parents
            // Apply crossover / mutation operators
            double child[] = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
            // Check fitness of unknown fuction
            Double fitness = (double) evaluation_.evaluate(child);
            evals++;
            // Select survivors
        }
        Double[][] mystring = new Double[][]{ //just an example of sorting by a column
			{2.0,3.2},
			{1.0,4.2},
			{9.2,5.2},
		};
		Arrays.sort(mystring, (Double[] c1, Double[] c2) -> c1[0].compareTo(c2[0])); //sorting by a column
		System.out.println(Arrays.deepToString(mystring));//print the array
    }

}
