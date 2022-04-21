import org.knowm.xchart.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;

public class GraphLayout {

    Random rand;
    XYChart chart;
    JPanel panel;

    ArrayList<Double> th05Array = new ArrayList<>();
    ArrayList<Double> th1Array = new ArrayList<>();
    ArrayList<Double> th2Array = new ArrayList<>();
    ArrayList<Double> th3Array = new ArrayList<>();

    ArrayList<Double> th05XArray = new ArrayList<>();
    ArrayList<Double> th1XArray = new ArrayList<>();
    ArrayList<Double> th2XArray = new ArrayList<>();
    ArrayList<Double> th3XArray = new ArrayList<>();

    public GraphLayout () {
        rand = new Random();
        drawComponents();
    }

    public void drawComponents() {
        // Initialize Array with starting values (th 0.6, tk th - 0.05 ...)
        init();

        // Initialized the chart
        chart = new XYChartBuilder().width(600).height(350).title("% Subsample Kappa over Criterion Kappa (Sample Size: 60)")
                .xAxisTitle("Criterion Kappa").yAxisTitle("Percentage over criterion").build();

        XYSeries th05Series = chart.addSeries("th-0.05", th05XArray, th05Array);
        XYSeries th1Series = chart.addSeries("th-0.1", th1XArray, th1Array);
        XYSeries th2Series = chart.addSeries("th-0.2", th2XArray, th2Array);
        XYSeries th3Series = chart.addSeries("th-0.3", th3XArray, th3Array);

        // Customize Chart
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Area);
        chart.getStyler().setMarkerSize(0);
        chart.getStyler().setCursorEnabled(true);

        chart.getStyler().setYAxisMin(0.0);
        chart.getStyler().setYAxisMax(1.0);
        chart.getStyler().setXAxisMin(0.6);
        chart.getStyler().setXAxisMax(0.8);

        th05Series.setEnabled(true);
        th1Series.setEnabled(true);
        th2Series.setEnabled(true);
        th3Series.setEnabled(true);

        panel = new XChartPanel<>(chart);

        repaint();
    }
    private void init() {
        System.out.println("Making initial calculations...");
        double criterionKappa = 0.6;
        int subSampleSize = 60;
        int populationSize = 1000 * 1000;
        int iterations = 100 * 1000;

        TableBuilder population = new TableBuilder(criterionKappa - 0.05, populationSize);
        th05Array.add(getPercentage(criterionKappa, subSampleSize, iterations,
                population.rater1, population.rater2, population.populationSize));

        population = new TableBuilder(criterionKappa - 0.1, populationSize);
        th1Array.add(getPercentage(criterionKappa, subSampleSize, iterations,
                population.rater1, population.rater2, population.populationSize));

        population = new TableBuilder(criterionKappa - 0.2, populationSize);
        th2Array.add(getPercentage(criterionKappa, subSampleSize, iterations,
                population.rater1, population.rater2, population.populationSize));

        population = new TableBuilder(criterionKappa - 0.3, populationSize);
        th3Array.add(getPercentage(criterionKappa, subSampleSize, iterations,
                population.rater1, population.rater2, population.populationSize));

        th05XArray.add(criterionKappa);
        th1XArray.add(criterionKappa);
        th2XArray.add(criterionKappa);
        th3XArray.add(criterionKappa);
    }

    public void runSimulation(double criterionKappa, int subSampleSize, int iterations, int[] rater1, int[] rater2, int populationSize, String trueKappa) {
        double percentage = getPercentage(criterionKappa, subSampleSize, iterations, rater1, rater2, populationSize);
        System.out.println("% above th: " + percentage / 100 + "%");

        switch (trueKappa) {
            case "0.05":
                th05Array.add(percentage);
                th05XArray.add(criterionKappa);
                break;
            case "0.1":
                th1Array.add(percentage);
                th1XArray.add(criterionKappa);
                break;
            case "0.2":
                th2Array.add(percentage);
                th2XArray.add(criterionKappa);
                break;
            case "0.3":
                th3Array.add(percentage);
                th3XArray.add(criterionKappa);
                break;
            default:
                System.out.println("Error");
                break;
        }

        // Updates the chart
        chart.updateXYSeries("th-0.05", th05XArray, th05Array, null);
        chart.updateXYSeries("th-0.1", th1XArray, th1Array, null);
        chart.updateXYSeries("th-0.2", th2XArray, th2Array, null);
        chart.updateXYSeries("th-0.3", th3XArray, th3Array, null);

        repaint();
    }

    private double getPercentage(double criterionKappa, int subSampleSize, int iterations, int[] rater1, int[] rater2, int populationSize) {
        int kappaOverCriterion = 0;

        // Run number of iterations times
        for (int i = 0; i < iterations; i++) {
            // Keeps track of visited indexes to prevent duplicates
            boolean[] visited = new boolean[populationSize];

            // To keep track of our subSample
            int[][] subSampleTable = new int[2][2];

            for (int j = 0; j < subSampleSize; j++) {
                int randomIndex = rand.nextInt(populationSize);

                while (visited[randomIndex]) {
                    randomIndex = rand.nextInt(populationSize);
                }

                visited[randomIndex] = true;

                // We add our values into a 2x2 table
                subSampleTable[(rater2[randomIndex] + 1) % 2][(rater1[randomIndex] + 1) % 2]++;
            }

            double currentKappa = TableBuilder.getKappaValue(subSampleTable);

            // Record only if kappa is higher than our cutoff
            if (currentKappa > criterionKappa) {
                kappaOverCriterion++;
            }
        }

        return ((double) kappaOverCriterion / iterations);
    }

    private void repaint() {
        panel.revalidate();
        panel.repaint();
    }
}
