import javax.swing.*;
import java.awt.*;

public class Main {

    private static final int POPULATION_SIZE = 1000 * 1000; // 1 Million
    private static final int SAMPLE_SIZE = 60; // 60, 100, 200, 500
    private static final int ITERATIONS = 1000 * 1000; //100k

    public static void main(String[] args) {
        // Initialize our Graph Pane
        GraphLayout graphLayout = new GraphLayout();
        graphLayout.panel.setPreferredSize(new Dimension(1500, 1000));

        // Customize our JFrame
        JFrame frame = new JFrame("Kappa Reliability");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(graphLayout.panel);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.pack();
        frame.setVisible(true);

        // Run our simulations
        // Starting at 0.61 because to init our graph, it already calculated 0.6
        for (double i = 0.61; i < 0.81; i += 0.01) {
            System.out.println("//===========================================");
            System.out.println("th: " + i);
            System.out.println("Population Kappa: " + (i - 0.05));

            TableBuilder populationTable = new TableBuilder(i - 0.05, POPULATION_SIZE);
            int[] rater1 = populationTable.rater1;
            int[] rater2 = populationTable.rater2;

            graphLayout.runSimulation(i, SAMPLE_SIZE, ITERATIONS, rater1, rater2, POPULATION_SIZE, "" + 0.05);
            for (double j = 0.1; j <= 0.35; j += 0.1) {
                i = round(i, 2);
                j = round(j, 2);
                System.out.println("//===========================================");
                System.out.println("th: " + i);
                System.out.println("Population Kappa: " + (i - j));

                populationTable = new TableBuilder(i - j, POPULATION_SIZE);
                rater1 = populationTable.rater1;
                rater2 = populationTable.rater2;

                graphLayout.runSimulation(i, SAMPLE_SIZE, ITERATIONS, rater1, rater2, POPULATION_SIZE, "" + j);
            }
        }
    }

    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
}
