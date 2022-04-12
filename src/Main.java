import com.sun.xml.internal.xsom.XSUnionSimpleType;

import java.util.Arrays;
import java.util.Random;

public class Main {
    /*
        Notes:
        1. TP: table[0][0]
        2. FP: table[1][0]
        3. TN: table[1][1]
        4. FN: table[0][1]
     */

    private static final Random rand = new Random();

    private static int[][] table;
    private static int[] rater1;
    private static int[] rater2;

    private static final int POPULATION_SIZE = 1000 * 1000; // 1 Million
    private static final int NUM_RATERS = 2;
    private static final int SAMPLE_SIZE = 500; // 60, 100, 200, 500
    private static final double CRITERION_KAPPA = 0.75; // 0.6, 0.65, 0.7, 0.75, 0.8
    private static final double TARGET_KAPPA = CRITERION_KAPPA - 0.05; // th-0.05, th-0.1, th-0.2, th-0.3
    private static final int ITERATIONS = 100 * 1000;

    public static void main(String[] args) {
        System.out.println("Criterion-Kappa: " + CRITERION_KAPPA);

        System.out.println("Running simulation with true kappa: th - 0.05");
        // 1. Build Dataset [Done and works]
        buildDataset(CRITERION_KAPPA - 0.05, POPULATION_SIZE, NUM_RATERS);

        System.out.println();

        // 2. Run Simulation
        runSimulation(CRITERION_KAPPA, SAMPLE_SIZE, ITERATIONS);

        System.out.println("================");

        System.out.println("Running simulation with true kappa: th - 0.1");
        // 1. Build Dataset [Done and works]
        buildDataset(CRITERION_KAPPA - 0.1, POPULATION_SIZE, NUM_RATERS);

        System.out.println();

        // 2. Run Simulation
        runSimulation(CRITERION_KAPPA, SAMPLE_SIZE, ITERATIONS);

        System.out.println("================");

        System.out.println("Running simulation with true kappa: th - 0.2");
        // 1. Build Dataset [Done and works]
        buildDataset(CRITERION_KAPPA - 0.2, POPULATION_SIZE, NUM_RATERS);

        System.out.println();

        // 2. Run Simulation
        runSimulation(CRITERION_KAPPA, SAMPLE_SIZE, ITERATIONS);

        System.out.println("================");

        System.out.println("Running simulation with true kappa: th - 0.3");
        // 1. Build Dataset [Done and works]
        buildDataset(CRITERION_KAPPA - 0.3, POPULATION_SIZE, NUM_RATERS);

        System.out.println();

        // 2. Run Simulation
        runSimulation(CRITERION_KAPPA, SAMPLE_SIZE, ITERATIONS);
    }

    private static void runSimulation(double criterionKappa, int subSampleSize, int iterations) {
        double[] kappaOverCriterion = new double[iterations];
        // Run number of iterations times
        for (int i = 0; i < iterations; i++) {
            // Keeps track of visited indexes to prevent duplicates
            boolean[] visited = new boolean[POPULATION_SIZE];
            int[] sample1 = new int[subSampleSize];
            int[] sample2 = new int[subSampleSize];

            // To avoid confusion
            Arrays.fill(sample1, -1);
            Arrays.fill(sample2, -2);

            // To keep track of our subSample
            int[][] subSampleTable = new int[2][2];

            for (int j = 0; j < subSampleSize; j++) {
                int randomIndex = rand.nextInt(POPULATION_SIZE - 1);

                while (visited[randomIndex]) {
                    randomIndex = rand.nextInt(POPULATION_SIZE - 1);
                }

                sample1[j] = rater1[randomIndex];
                sample2[j] = rater2[randomIndex];

                visited[randomIndex] = true;

                // We add our values into a 2x2 table
                subSampleTable[(sample2[j] + 1) % 2][(sample1[j] + 1) % 2]++;

            }

            double currentKappa = getKappaValue(subSampleTable);

            // Compare only if kappa is higher than our cutoff
            if (currentKappa > criterionKappa) {
                kappaOverCriterion[i] = currentKappa;
            }
        }

        int count = 0;
        for (int i = 0; i < kappaOverCriterion.length; i++) {
            if (kappaOverCriterion[i] != 0) {
                count++;
            }
        }
        System.out.println("Above TH: " + count);
        System.out.println("% above th: " + ((double) count/iterations) * 100 + "%");
    }

    public static void buildDataset(double targetKappa, int populationSize, int numberRaters) {
        table = new int[2][2];
        rater1 = new int[populationSize];
        rater2 = new int[populationSize];

        // Originally fill arrays to avoid confusion during calculation
        Arrays.fill(rater1, -1);
        Arrays.fill(rater2, -2);

        // Initialize the first 200 values in the table
        initiallyPopulateRaters(rater1, rater2, 200);
        updateTable(rater1, rater2, table);

        // Populate the rest of the population, fine-tuning it to the target kappa
        for (int i = 200; i < populationSize; i++) {
            double currentKappa = getKappaValue(table);

            int number = rand.nextInt(2);
            if (currentKappa < targetKappa) {
                rater1[i] = number;
                rater2[i] = number;
            } else {
                rater1[i] = number;
                rater2[i] = (number + 1) % 2;
            }
            table[(rater2[i] + 1) % 2][(rater1[i] + 1) % 2]++;
        }
        updateTable(rater1, rater2, table);
        System.out.println("Population Kappa: " + getKappaValue(table));
        System.out.println("Population table");
        printTable(table);
    }

    private static double getKappaValue(int[][] table) {
        int truePositive = table[0][0];
        int falseNegative = table[0][1];
        int falsePositive = table[1][0];
        int trueNegative = table[1][1];

        double total = truePositive + falseNegative + falsePositive + trueNegative;
        double BaseRate1 = (truePositive + falsePositive) / total;
        double BaseRate2 = (trueNegative + falseNegative) / total;

        double ObservedAgreement = (truePositive + trueNegative) / total;
        double ProbabilityAgreementChance = (BaseRate1 * BaseRate2) + ((1 - BaseRate1) * (1 - BaseRate2));

        // Returns the Kappa value
        return (ObservedAgreement - ProbabilityAgreementChance) / (1 - ProbabilityAgreementChance);
    }

    private static void initiallyPopulateRaters(int[] rater1, int[] rater2, int count) {
        for (int i = 0; i < count; i++) {
            rater1[i] = rand.nextInt(2);
            rater2[i] = rand.nextInt(2);
        }
    }

    private static void updateTable(int[] rater1, int[] rater2, int[][] table) {
        int truePositive = 0;
        int trueNegative = 0;
        int falsePositive = 0;
        int falseNegative = 0;

        for (int i = 0; i < rater1.length; i++) {
            // If we haven't initialized the rest of the raters, then break
            if (rater1[i] == -1) break;

            // Otherwise, check which pos in the table to increment
            if (rater1[i] == rater2[i]) {
                if (rater1[i] == 1) {
                    truePositive++;
                } else {
                    trueNegative++;
                }
            } else {
                if (rater1[i] == 1) {
                    falsePositive++;
                } else {
                    falseNegative++;
                }
            }
        }

        // Update our table
        table[0][0] = truePositive;
        table[1][0] = falsePositive;
        table[0][1] = falseNegative;
        table[1][1] = trueNegative;
    }

    private static void printTable(int[][] table) {
        System.out.println("//===========");
        System.out.println("| " + table[0][0] + " | " + table[0][1] + " |");
        System.out.println("| " + table[1][0] + " | " + table[1][1] + " |");
        System.out.println("//===========");
    }
}
