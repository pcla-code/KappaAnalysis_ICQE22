import org.knowm.xchart.*;

import javax.swing.*;
import java.util.ArrayList;

public class GraphLayout {

    XYChart chart;
    JPanel panel;

    ArrayList<Double> th05Array = new ArrayList<>();
    ArrayList<Double> th1Array = new ArrayList<>();
    ArrayList<Double> th2Array = new ArrayList<>();
    ArrayList<Double> th3Array = new ArrayList<>();

    public GraphLayout (){
        drawComponents();
    }

    public void drawComponents() {
        // Add initial values to our array
        th05Array.add(0.0);
        th1Array.add(0.0);
        th2Array.add(0.0);
        th3Array.add(0.0);

        // Initialized the chart
        chart = new XYChartBuilder().width(600).height(350).title("% Subsample Kappa over Criterion Kappa (Sample Size: 60)")
                .xAxisTitle("Criterion Kappa").yAxisTitle("Percentage over criterion").build();

        XYSeries th05Series = chart.addSeries("Th-0.05", th05Array);
        XYSeries th1Series = chart.addSeries("th-0.1", th1Array);
        XYSeries th2Series = chart.addSeries("th-0.2", th2Array);
        XYSeries th3Series = chart.addSeries("th-0.3", th3Array);

        // Customize Chart
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Area);
        chart.getStyler().setMarkerSize(0);
        chart.getStyler().setCursorEnabled(true);

        th05Series.setEnabled(true);
        th1Series.setEnabled(true);
        th2Series.setEnabled(true);
        th3Series.setEnabled(true);

        panel = new XChartPanel<>(chart);

        panel.revalidate();
        panel.repaint();
    }
}
