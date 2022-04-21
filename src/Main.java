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
        JFrame frame = new JFrame("Behind The Curve");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(graphLayout.panel);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.pack();
        frame.setVisible(true);

        // Run our simulations
        for (double i = 0.6; i <= 0.8; i += 0.01) {
            for (double j = 0.1; j <= 0.3; j += 0.1) {
                System.out.println("//===========================================");
                System.out.println("TH: " + i);
                System.out.println("Population Kappa: " + (i - j));

                TableBuilder populationTable = new TableBuilder(i - j, POPULATION_SIZE);
                int[] rater1 = populationTable.rater1;
                int[] rater2 = populationTable.rater2;

                graphLayout.runSimulation(i, SAMPLE_SIZE, ITERATIONS, rater1, rater2, POPULATION_SIZE, "" + j);
            }
        }

    }
}
