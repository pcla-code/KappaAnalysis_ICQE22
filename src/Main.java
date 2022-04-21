import javax.swing.*;
import java.awt.*;
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

    private static int[] rater1;
    private static int[] rater2;

    private static final int POPULATION_SIZE = 100 * 1000 * 1000; // 100 Million
    private static final int SAMPLE_SIZE = 60; // 60, 100, 200, 500
    private static double CRITERION_KAPPA = 0.7; // 0.6, 0.65, 0.7, 0.75, 0.8
    private static final double TARGET_KAPPA = CRITERION_KAPPA - 0.05; // th-0.05, th-0.1, th-0.2, th-0.3
    private static final int ITERATIONS = 1000 * 1000; //1 million

    public static void main(String[] args) {
        GraphLayout graphLayout = new GraphLayout();
        graphLayout.panel.setPreferredSize(new Dimension(1500, 1000));

        JFrame frame = new JFrame("Behind The Curve");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // add components into our content page
        frame.getContentPane().add(graphLayout.panel);
        // set dimensions
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        // layout all the components
        frame.pack();
        // make our frame visible to the user
        frame.setVisible(true);

//        runAnalysis(CRITERION_KAPPA); // CRITERION_KAPPA = 0.6
//        CRITERION_KAPPA += 0.05;
//        runAnalysis(CRITERION_KAPPA); // CRITERION_KAPPA = 0.65
//        CRITERION_KAPPA += 0.05;
//        runAnalysis(CRITERION_KAPPA); // CRITERION_KAPPA = 0.7
//        CRITERION_KAPPA += 0.05;
//        runAnalysis(CRITERION_KAPPA + 0.05); // CRITERION_KAPPA = 0.75
//        runAnalysis(CRITERION_KAPPA + 0.05); // CRITERION_KAPPA = 0.8
    }

    private static void runAnalysis(double thresholdKappa) {
        System.out.println("Criterion-Kappa: " + thresholdKappa);

        System.out.println("Running simulation with true kappa: th - 0.05");
        // 1. Build Dataset [Done and works]
        buildDataset(thresholdKappa - 0.05, POPULATION_SIZE);

        System.out.println();

        // 2. Run Simulation
        runSimulation(thresholdKappa, SAMPLE_SIZE, ITERATIONS);

        System.out.println("================");

        System.out.println("Running simulation with true kappa: th - 0.1");
        // 1. Build Dataset [Done and works]
        buildDataset(thresholdKappa - 0.1, POPULATION_SIZE);

        System.out.println();

        // 2. Run Simulation
        runSimulation(thresholdKappa, SAMPLE_SIZE, ITERATIONS);

        System.out.println("================");

        System.out.println("Running simulation with true kappa: th - 0.2");
        // 1. Build Dataset [Done and works]
        buildDataset(thresholdKappa - 0.2, POPULATION_SIZE);

        System.out.println();

        // 2. Run Simulation
        runSimulation(thresholdKappa, SAMPLE_SIZE, ITERATIONS);

        System.out.println("================");

        System.out.println("Running simulation with true kappa: th - 0.3");
        // 1. Build Dataset [Done and works]
        buildDataset(thresholdKappa - 0.3, POPULATION_SIZE);

        System.out.println();

        // 2. Run Simulation
        runSimulation(thresholdKappa, SAMPLE_SIZE, ITERATIONS);
    }

    private static void runSimulation(double criterionKappa, int subSampleSize, int iterations) {
        int kappaOverCriterion = 0;

        // Run number of iterations times
        for (int i = 0; i < iterations; i++) {
            // Keeps track of visited indexes to prevent duplicates
            boolean[] visited = new boolean[POPULATION_SIZE];
            int[] sample1 = new int[subSampleSize];
            int[] sample2 = new int[subSampleSize];

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
                kappaOverCriterion++;
            }
        }

        System.out.println("Above TH: " + kappaOverCriterion);
        System.out.println("% above th: " + ((double) kappaOverCriterion/iterations) * 100 + "%");
    }

    public static void buildDataset(double targetKappa, int populationSize) {
        rater1 = new int[populationSize];
        rater2 = new int[populationSize];

        int[][] populationTable = new int[2][2];

        // Originally fill arrays to avoid confusion during calculation
        Arrays.fill(rater1, -1);
        Arrays.fill(rater2, -2);

        // Initialize the first 200 values in the table
        initiallyPopulateRaters(rater1, rater2, 200);
        updateTable(rater1, rater2, populationTable);

        // Populate the rest of the population, fine-tuning it to the target kappa
        for (int i = 200; i < populationSize; i++) {
            double currentKappa = getKappaValue(populationTable);

            int number = rand.nextInt(2);
            if (currentKappa < targetKappa) {
                rater1[i] = number;
                rater2[i] = number;
            } else {
                rater1[i] = number;
                rater2[i] = (number + 1) % 2;
            }
            populationTable[(rater2[i] + 1) % 2][(rater1[i] + 1) % 2]++;
        }
        updateTable(rater1, rater2, populationTable);
        System.out.println("Population Kappa: " + getKappaValue(populationTable));
        System.out.println("Population table");
        printTable(populationTable);
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
