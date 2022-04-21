import java.util.Arrays;
import java.util.Random;

public class TableBuilder {
    Random rand = new Random();

    double targetKappa;
    int populationSize;

    int[] rater1, rater2;
    int[][] table;

    public TableBuilder(double targetKappa, int populationSize) {
        this.targetKappa = targetKappa;
        this.populationSize = populationSize;

        this.rater1 = new int[populationSize];
        this.rater2 = new int[populationSize];

        this.table = new int[2][2];

        buildDataset();
    }

    public void buildDataset() {
        // Originally fill arrays to avoid confusion during calculation
        Arrays.fill(rater1, -1);
        Arrays.fill(rater2, -2);

        // Initialize the first 200 values in the table
        initiallyPopulateRaters(rater1, rater2, 200);

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
    }

    public static double getKappaValue(int[][] table) {
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

    private void initiallyPopulateRaters(int[] rater1, int[] rater2, int count) {
        int num1 = rand.nextInt(2);
        int num2 = rand.nextInt(2);

        for (int i = 0; i < count; i++) {
            rater1[i] = num1;
            rater2[i] = num2;

            table[(rater2[i] + 1) % 2][(rater1[i] + 1) % 2]++;
        }
    }

    private void printTable(int[][] table) {
        System.out.println("//===========");
        System.out.println("| " + table[0][0] + " | " + table[0][1] + " |");
        System.out.println("| " + table[1][0] + " | " + table[1][1] + " |");
        System.out.println("//===========");
    }
}
